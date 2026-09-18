/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.mcp;

import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.mcp.model.CampaignModel;
import io.meeds.gamification.mcp.model.QuestModel;
import io.meeds.gamification.mock.SpaceServiceMock;
import io.meeds.gamification.test.AbstractServiceTest;

/**
 * Kernel/DB-backed integration test for {@link GamificationMcpTool}. Unlike the
 * Mockito-only {@code GamificationMcpToolTest}, this one exercises the real
 * gamification services and cached storages, so it can catch a
 * create&rarr;persist&rarr;read-back round-trip regression (e.g. a missing
 * program-list cache invalidation on create) that a mock cannot.
 */
public class GamificationMcpToolKernelTest extends AbstractServiceTest {

  private static final String ADMIN_USER = "root1";

  private GamificationMcpTool tool;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    tool = new GamificationMcpTool(programService,
                                   ruleService,
                                   realizationService,
                                   announcementService,
                                   badgeService,
                                   identityManager);
  }

  /**
   * The core regression: a campaign created through the MCP tool must be
   * retrievable IMMEDIATELY afterwards, in the same session, both by id
   * (get_campaign) and in the listing (list_campaigns). This proves there is no
   * read-after-write cache-staleness gap: {@code createProgram} clears the
   * by-id program cache and the program-list query is not cached.
   */
  @Test
  public void testCreateCampaignIsImmediatelyRetrievable() throws Exception {
    startSessionAsAdministrator(ADMIN_USER);

    // Prime any caches with the pre-create state of the listing.
    List<CampaignModel> before = tool.listCampaigns(null, null, null, null);
    int beforeCount = before.size();

    CampaignModel created = tool.createCampaign("EVA Campaign", "Created by EVA", null, 100L);
    assertNotNull("create_campaign must return the created campaign", created);
    assertTrue("created campaign must have a persisted id", created.getId() > 0);
    assertEquals("EVA Campaign", created.getTitle());

    // Read-after-write by id.
    CampaignModel fetched = tool.getCampaign(created.getId());
    assertNotNull("get_campaign must find the just-created campaign", fetched);
    assertEquals(created.getId(), fetched.getId());
    assertEquals("EVA Campaign", fetched.getTitle());
    assertEquals("Created by EVA", fetched.getDescription());

    // Read-after-write in the listing: it must appear immediately, not only
    // after a cache invalidation or a fresh request.
    List<CampaignModel> after = tool.listCampaigns(null, null, null, null);
    assertEquals("list_campaigns must include the newly created campaign", beforeCount + 1, after.size());
    assertTrue("the new campaign id must be present in list_campaigns",
               after.stream().anyMatch(c -> c.getId() == created.getId()));

    // And it must be findable by search term too.
    List<CampaignModel> byTerm = tool.listCampaigns("EVA", null, null, null);
    assertTrue("list_campaigns filtered by term must include the new campaign",
               byTerm.stream().anyMatch(c -> c.getId() == created.getId()));
  }

  /**
   * The end-to-end EVA flow that was reported as broken: create a campaign,
   * then add a quest to it, then confirm both the campaign and its quest are
   * retrievable straight away.
   */
  @Test
  public void testCreateCampaignThenQuestRoundTrip() throws Exception {
    startSessionAsAdministrator(ADMIN_USER);

    CampaignModel campaign = tool.createCampaign("Q3 Contributor", "Reward top contributors", null, 500L);
    assertTrue(campaign.getId() > 0);

    QuestModel quest = tool.createQuest(campaign.getId(),
                                        "Write a blog post",
                                        "Publish an article",
                                        50,
                                        "MANUAL",
                                        null,
                                        null,
                                        null,
                                        null);
    assertNotNull(quest);
    assertTrue(quest.getId() > 0);
    assertEquals(campaign.getId(), quest.getCampaignId());

    // The quest is retrievable immediately, both directly and via list_quests.
    QuestModel fetchedQuest = tool.getQuest(quest.getId());
    assertEquals(quest.getId(), fetchedQuest.getId());
    assertEquals("Write a blog post", fetchedQuest.getTitle());

    List<QuestModel> quests = tool.listQuests(campaign.getId(), null, null, null, null);
    assertTrue("list_quests must include the newly created quest",
               quests.stream().anyMatch(q -> q.getId() == quest.getId()));

    // And the campaign is still retrievable.
    assertNotNull(tool.getCampaign(campaign.getId()));
  }

  /**
   * ACL-scoping regression: with no id, list_campaigns / list_quests must NOT
   * leak a RESTRICTED program (nor its quests) that lives in a space the caller
   * is not a member of. Before the fix the tool built the filter with
   * allSpaces=true when no id was passed, which made the DAO drop the
   * "(audienceId IS NULL OR visibility = OPEN)" restriction and leak the
   * restricted content. "user" is a plain internal user, not a rewarding
   * admin, and a member of no space, so it must only see OPEN/no-audience
   * content.
   */
  @Test
  public void testListDoesNotLeakRestrictedContentToNonMember() throws Exception {
    // A RESTRICTED program whose audience is a space the caller is NOT a member of.
    ProgramEntity restricted = newDomain(EntityType.MANUAL,
                                         "Secret-EVA-Program",
                                         true,
                                         Collections.emptySet(),
                                         Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    restricted.setVisibility(EntityVisibility.RESTRICTED);
    programDAO.update(restricted);
    programStorage.clearCache();
    restartTransaction();

    // A quest inside that restricted program.
    RuleEntity restrictedRule = newManualRule("secret-quest", restricted.getId());
    ruleStorage.clearCache();
    restartTransaction();

    startSessionAs("user");

    List<CampaignModel> campaigns = tool.listCampaigns(null, null, null, null);
    assertTrue("list_campaigns must NOT leak a RESTRICTED program from a space the caller is not a member of",
               campaigns.stream().noneMatch(c -> c.getId() == restricted.getId()));

    List<QuestModel> quests = tool.listQuests(null, null, null, null, null);
    assertTrue("list_quests must NOT leak a quest of a RESTRICTED program from a space the caller is not a member of",
               quests.stream().noneMatch(q -> q.getId() == restrictedRule.getId()));
  }

  /**
   * "The campaigns of space X" through the real services: a member gets that
   * space's campaigns and not the platform-wide ones, and a caller who shares
   * none of that space gets what the space shows to everyone — its OPEN
   * campaigns — and nothing else. Before the fix the first case mixed in every
   * platform-wide campaign and the second died in the engine, because the
   * service left no space to filter on for a non-member.
   */
  @Test
  public void testListCampaignsBySpace() throws Exception {
    long spaceId = Long.parseLong(SpaceServiceMock.SPACE_ID_1);
    ProgramEntity spaceRestricted = newDomain(EntityType.MANUAL, "space-1-restricted", true, Collections.emptySet(), spaceId);
    spaceRestricted.setVisibility(EntityVisibility.RESTRICTED);
    programDAO.update(spaceRestricted);
    ProgramEntity spaceOpen = newDomain(EntityType.MANUAL, "space-1-open", true, Collections.emptySet(), spaceId);
    spaceOpen.setVisibility(EntityVisibility.OPEN);
    programDAO.update(spaceOpen);
    ProgramEntity platformWide = newDomain(EntityType.MANUAL, "platform-wide-campaign", true, Collections.emptySet());
    platformWide.setAudienceId(null);
    platformWide.setVisibility(EntityVisibility.OPEN);
    programDAO.update(platformWide);
    programStorage.clearCache();
    restartTransaction();

    // root10 is a member of space 1 (SpaceServiceMock), and of no other: both of
    // the space's campaigns, and never the platform-wide one.
    startSessionAs("root10");
    List<CampaignModel> ofSpace = tool.listCampaigns(null, spaceId, 10, 0);
    assertTrue("the space's campaigns must be listed",
               ofSpace.stream().anyMatch(c -> c.getId() == spaceRestricted.getId())
                   && ofSpace.stream().anyMatch(c -> c.getId() == spaceOpen.getId()));
    assertTrue("a platform-wide campaign is not a campaign of that space",
               ofSpace.stream().noneMatch(c -> c.getId() == platformWide.getId()));

    // "user" is a member of no space at all: what the space shows to everyone,
    // and nothing else — not its restricted campaign, not the platform-wide one,
    // and no error.
    startSessionAs("user");
    List<CampaignModel> asNonMember = tool.listCampaigns(null, spaceId, 10, 0);
    assertEquals(1, asNonMember.size());
    assertEquals(spaceOpen.getId().longValue(), asNonMember.get(0).getId());
  }

  /**
   * The write tools' refusals against the <b>real</b> services, not a stubbed
   * denial: a plain user who owns nothing must be refused by every write tool,
   * with the ACL signal of the norm ({@code IllegalAccessException}, i.e. 403)
   * rather than a generic illegal state.
   */
  @Test
  public void testWriteToolsAreRefusedToANonOwner() throws Exception {
    startSessionAsAdministrator(ADMIN_USER);
    CampaignModel campaign = tool.createCampaign("Owner's campaign", null, null, 10L);
    QuestModel quest = tool.createQuest(campaign.getId(), "Owner's quest", null, 50, "MANUAL", null, null, null, null);
    long campaignId = campaign.getId();
    long questId = quest.getId();

    // "user" is a plain internal user: no rewarding-admin right, no space
    // management, and not an owner of that campaign.
    startSessionAs("user");

    assertThrows(IllegalAccessException.class, () -> tool.createCampaign("Sneaky campaign", null, null, null));
    assertThrows(IllegalAccessException.class,
                 () -> tool.createQuest(campaignId, "Sneaky quest", null, 50, "MANUAL", null, null, null, null));
    assertThrows(IllegalAccessException.class,
                 () -> tool.updateQuest(questId, "Renamed by a stranger", null, null, null, null, null, null));
    assertThrows(IllegalAccessException.class, () -> tool.deleteQuest(questId));

    // And the quest is untouched.
    startSessionAsAdministrator(ADMIN_USER);
    assertEquals("Owner's quest", tool.getQuest(questId).getTitle());
  }

  /**
   * A campaign created with a space id stays that space's campaign: an "open"
   * program has its audience reset to 0 by ProgramServiceImpl and its
   * visibility computed from that empty audience, so creating it open — which
   * is what an omitted 'open' parameter used to mean — produced a platform-wide
   * campaign visible to everyone.
   */
  @Test
  public void testCreateCampaignInSpaceStaysRestrictedToThatSpace() throws Exception {
    startSessionAsAdministrator(ADMIN_USER);
    long spaceId = Long.parseLong(SpaceServiceMock.SPACE_ID_1);

    CampaignModel created = tool.createCampaign("Space campaign", "For that space only", spaceId, 10L);

    assertEquals("the campaign must keep the space it was created for", spaceId, created.getSpaceId());
    assertFalse("a space campaign is not an open one", created.isOpen());
    assertEquals("and its visibility is the one the service computes for that space",
                 EntityVisibility.RESTRICTED.name(),
                 created.getVisibility());
  }
}

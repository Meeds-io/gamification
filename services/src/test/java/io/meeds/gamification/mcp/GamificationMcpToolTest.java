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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.mcp.model.AnnouncementModel;
import io.meeds.gamification.mcp.model.BadgeModel;
import io.meeds.gamification.mcp.model.CampaignModel;
import io.meeds.gamification.mcp.model.LeaderboardEntryModel;
import io.meeds.gamification.mcp.model.PointsTotalModel;
import io.meeds.gamification.mcp.model.QuestAvailabilityModel;
import io.meeds.gamification.mcp.model.QuestModel;
import io.meeds.gamification.mcp.model.RealizationModel;
import io.meeds.gamification.mcp.model.ScoreBreakdownEntryModel;
import io.meeds.gamification.mcp.model.ScoreModel;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProfileReputation;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.RulePublication;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.plugin.ProgramPermanentLinkPlugin;
import io.meeds.gamification.plugin.RulePermanentLinkPlugin;
import io.meeds.gamification.rest.model.RealizationValidityContext;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.BadgeService;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;
import io.meeds.portal.permlink.model.PermanentLinkObject;
import io.meeds.portal.permlink.service.PermanentLinkService;

import lombok.SneakyThrows;

public class GamificationMcpToolTest {

  private static final String USERNAME    = "testuser1";

  private static final long   OWN_ID      = 99L;

  private static final long   CAMPAIGN_ID = 5L;

  private static final long   QUEST_ID    = 7L;

  private static final String DOMAIN      = "https://engage.example.org";

  private static final String SITE        = "dw";

  private ProgramService      programService;

  private RuleService         ruleService;

  private RealizationService  realizationService;

  private AnnouncementService announcementService;

  private BadgeService        badgeService;

  private IdentityManager     identityManager;

  private PermanentLinkService permanentLinkService;

  private String              previousDomain;

  private Identity            currentIdentity;

  private GamificationMcpTool tool;

  @Before
  public void setUp() {
    programService = mock(ProgramService.class);
    ruleService = mock(RuleService.class);
    realizationService = mock(RealizationService.class);
    announcementService = mock(AnnouncementService.class);
    badgeService = mock(BadgeService.class);
    identityManager = mock(IdentityManager.class);
    permanentLinkService = mock(PermanentLinkService.class);
    stubPermanentLinks();
    previousDomain = System.getProperty(CommonsUtils.CONFIGURED_DOMAIN_URL_KEY);
    System.setProperty(CommonsUtils.CONFIGURED_DOMAIN_URL_KEY, DOMAIN);
    currentIdentity = new Identity(USERNAME);
    tool = new GamificationMcpTool(programService,
                                   ruleService,
                                   realizationService,
                                   announcementService,
                                   badgeService,
                                   identityManager,
                                   permanentLinkService) {
      @Override
      public Identity getCurrentUserAclIdentity() {
        return currentIdentity;
      }
    };
  }

  /**
   * Restores the platform domain property this test overrides.
   */
  @After
  public void tearDown() {
    if (previousDomain == null) {
      System.clearProperty(CommonsUtils.CONFIGURED_DOMAIN_URL_KEY);
    } else {
      System.setProperty(CommonsUtils.CONFIGURED_DOMAIN_URL_KEY, previousDomain);
    }
  }

  /**
   * Answers the permanent link of a program and of a rule the way their
   * plugins do on a site named {@link #SITE}, so that a link resolved for the
   * wrong object type shows in the address.
   */
  @SneakyThrows
  private void stubPermanentLinks() {
    when(permanentLinkService.getLink(any())).thenAnswer(invocation -> {
      PermanentLinkObject object = invocation.getArgument(0);
      return switch (object.getObjectType()) {
      case ProgramPermanentLinkPlugin.OBJECT_TYPE -> String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, object.getObjectId());
      case RulePermanentLinkPlugin.OBJECT_TYPE -> String.format(RulePermanentLinkPlugin.URL_FORMAT, SITE, object.getObjectId());
      default -> throw new ObjectNotFoundException("No plugin for " + object.getObjectType());
      };
    });
  }

  private void stubSpacePrograms(long spaceId, List<Long> programIds) {
    when(programService.getProgramIds(argThat(filter -> filter != null && filter.getSpacesIds() != null
        && filter.getSpacesIds().equals(List.of(spaceId))), eq(0), eq(100))).thenReturn(programIds);
  }

  private void stubCurrentUserIdentity() {
    org.exoplatform.social.core.identity.model.Identity social =
        new org.exoplatform.social.core.identity.model.Identity(String.valueOf(OWN_ID));
    social.setRemoteId(USERNAME);
    when(identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, USERNAME)).thenReturn(social);
  }

  private ProgramDTO program(long id) {
    ProgramDTO program = new ProgramDTO();
    program.setId(id);
    program.setTitle("Campaign " + id);
    program.setDescription("A campaign");
    program.setSpaceId(3L);
    program.setBudget(1000L);
    program.setOpen(true);
    program.setEnabled(true);
    program.setVisibility(EntityVisibility.OPEN);
    return program;
  }

  private RuleDTO rule(long id, ProgramDTO program) {
    RuleDTO rule = new RuleDTO();
    rule.setId(id);
    rule.setTitle("Quest " + id);
    rule.setDescription("A quest");
    rule.setScore(50);
    rule.setProgram(program);
    rule.setType(EntityType.MANUAL);
    rule.setEnabled(true);
    rule.setRecurrence(RecurrenceType.NONE);
    return rule;
  }

  private RealizationDTO realization(long id, Long ruleId, ProgramDTO program) {
    return new RealizationDTO(id,
                              String.valueOf(OWN_ID),
                              "USER",
                              0,
                              "Quest title",
                              program,
                              null,
                              null,
                              30,
                              null,
                              null,
                              null,
                              ruleId,
                              null,
                              null,
                              null,
                              null,
                              "2026-01-01T00:00:00",
                              null,
                              null,
                              null,
                              "ACCEPTED",
                              EntityType.MANUAL,
                              null);
  }

  // --- page addresses (EXO-91659) -----------------------------------------

  /**
   * Each listed campaign carries the absolute address of its program page, the
   * address the agent links, instead of none.
   */
  @Test
  public void listCampaignsCarriesEachCampaignPageUrl() throws Exception {
    when(programService.getPrograms(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(program(CAMPAIGN_ID),
                                                                                                    program(6L)));

    List<CampaignModel> campaigns = tool.listCampaigns(null, null, null, null);

    assertEquals(DOMAIN + "/portal/dw/contributions/programs/5", campaigns.get(0).getUrl());
    assertEquals(DOMAIN + "/portal/dw/contributions/programs/6", campaigns.get(1).getUrl());
  }

  /**
   * A campaign read by id carries the absolute address of its program page.
   */
  @Test
  public void getCampaignCarriesItsPageUrl() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));

    assertEquals(DOMAIN + "/portal/dw/contributions/programs/5", tool.getCampaign(CAMPAIGN_ID).getUrl());
  }

  /**
   * A campaign the caller may not view is refused before any address is
   * resolved: the refusal carries no link to it.
   */
  @Test
  public void getCampaignDeniedResolvesNoUrl() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenThrow(new IllegalAccessException("denied"));

    assertThrows(IllegalAccessException.class, () -> tool.getCampaign(CAMPAIGN_ID));
    verify(permanentLinkService, never()).getLink(any());
  }

  /**
   * In the caller's own campaigns, one the caller can no longer view is
   * skipped with its address: only the viewable campaign's link is resolved.
   */
  @Test
  public void getMyCampaignsResolvesNoUrlForAnUnviewableCampaign() throws Exception {
    when(programService.getMyProgramIds(USERNAME, 0, 10)).thenReturn(List.of(CAMPAIGN_ID, 6L));
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    when(programService.getProgramById(6L, USERNAME)).thenThrow(new IllegalAccessException("denied"));

    List<CampaignModel> campaigns = tool.getMyCampaigns(null, null);

    assertEquals(1, campaigns.size());
    assertEquals(DOMAIN + "/portal/dw/contributions/programs/5", campaigns.get(0).getUrl());
    ArgumentCaptor<PermanentLinkObject> captor = ArgumentCaptor.forClass(PermanentLinkObject.class);
    verify(permanentLinkService).getLink(captor.capture());
    assertEquals(String.valueOf(CAMPAIGN_ID), captor.getValue().getObjectId());
  }

  /**
   * A campaign whose address cannot be resolved is still listed, with no
   * address rather than an invented one, and the listing does not fail.
   */
  @Test
  public void listCampaignsWithoutResolvableLinkCarriesNoUrl() throws Exception {
    when(programService.getPrograms(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(program(CAMPAIGN_ID)));
    doThrow(new ObjectNotFoundException("no plugin")).when(permanentLinkService).getLink(any());

    List<CampaignModel> campaigns = tool.listCampaigns(null, null, null, null);

    assertEquals(1, campaigns.size());
    assertNull(campaigns.get(0).getUrl());
  }

  /**
   * An empty path from the permanent link service is no address: the campaign
   * carries none rather than the bare domain.
   */
  @Test
  public void listCampaignsWithEmptyLinkCarriesNoUrl() throws Exception {
    when(programService.getPrograms(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(program(CAMPAIGN_ID)));
    doReturn("").when(permanentLinkService).getLink(any());

    assertNull(tool.listCampaigns(null, null, null, null).get(0).getUrl());
  }

  /**
   * A campaign without an id yet designates no page: no address is asked for
   * it.
   */
  @Test
  public void campaignWithoutIdCarriesNoUrl() throws Exception {
    when(programService.getPrograms(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(program(0L)));

    assertNull(tool.listCampaigns(null, null, null, null).get(0).getUrl());
    verify(permanentLinkService, never()).getLink(any());
  }

  /**
   * Each listed quest carries the absolute address of its action page.
   */
  @Test
  public void listQuestsCarriesEachQuestPageUrl() {
    when(ruleService.getRules(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(rule(QUEST_ID, program(CAMPAIGN_ID))));

    List<QuestModel> quests = tool.listQuests(CAMPAIGN_ID, null, null, null, null);

    assertEquals(DOMAIN + "/portal/dw/contributions/actions/7", quests.get(0).getUrl());
    assertEquals(DOMAIN + String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, CAMPAIGN_ID), quests.get(0).getCampaignUrl());
  }

  /**
   * A quest read by id carries the absolute address of its action page.
   */
  @Test
  public void getQuestCarriesItsPageUrl() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(rule(QUEST_ID, program(CAMPAIGN_ID)));

    QuestModel quest = tool.getQuest(QUEST_ID);
    assertEquals(DOMAIN + "/portal/dw/contributions/actions/7", quest.getUrl());
    assertEquals(DOMAIN + String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, CAMPAIGN_ID), quest.getCampaignUrl());
  }

  /**
   * A quest the caller may not view is refused before any address is
   * resolved.
   */
  @Test
  public void getQuestDeniedResolvesNoUrl() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenThrow(new IllegalAccessException("denied"));

    assertThrows(IllegalAccessException.class, () -> tool.getQuest(QUEST_ID));
    verify(permanentLinkService, never()).getLink(any());
  }

  // --- list_campaigns ------------------------------------------------------

  @Test
  public void listCampaigns() throws Exception {
    when(programService.getPrograms(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(program(CAMPAIGN_ID)));

    List<CampaignModel> campaigns = tool.listCampaigns("term", null, null, null);

    assertEquals(1, campaigns.size());
    assertEquals(CAMPAIGN_ID, campaigns.get(0).getId());
    assertEquals("OPEN", campaigns.get(0).getVisibility());
  }

  @Test
  public void listCampaignsBySpace() throws Exception {
    when(programService.getPrograms(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(program(CAMPAIGN_ID)));

    List<CampaignModel> campaigns = tool.listCampaigns(null, 3L, 5, 0);

    assertEquals(1, campaigns.size());
    ArgumentCaptor<ProgramFilter> captor = ArgumentCaptor.forClass(ProgramFilter.class);
    verify(programService).getPrograms(captor.capture(), eq(USERNAME), anyInt(), anyInt());
    assertEquals(List.of(3L), captor.getValue().getSpacesIds());
    // Same as ProgramRest#getPrograms: without excludeOpen the DAO answers "the
    // campaigns of space 3" with every platform-wide campaign too.
    assertTrue("a space-scoped listing must exclude the platform-wide campaigns",
               captor.getValue().isExcludeOpen());
  }

  @Test
  public void listCampaignsDoesNotBypassSpaceAcls() throws Exception {
    // Regression for the ACL bypass: with no space id the tool must NOT set
    // allSpaces=true on the ProgramFilter, otherwise the DAO drops the
    // "(audienceId IS NULL OR visibility = OPEN)" restriction and leaks
    // RESTRICTED programs from spaces the caller is not a member of.
    when(programService.getPrograms(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(program(CAMPAIGN_ID)));

    tool.listCampaigns(null, null, null, null);

    ArgumentCaptor<ProgramFilter> captor = ArgumentCaptor.forClass(ProgramFilter.class);
    verify(programService).getPrograms(captor.capture(), eq(USERNAME), anyInt(), anyInt());
    assertFalse("listCampaigns must not set allSpaces=true (ACL bypass)", captor.getValue().isAllSpaces());
  }

  // --- get_campaign --------------------------------------------------------

  @Test
  public void getCampaign() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));

    CampaignModel campaign = tool.getCampaign(CAMPAIGN_ID);

    assertNotNull(campaign);
    assertEquals(CAMPAIGN_ID, campaign.getId());
  }

  @Test
  public void getCampaignInvalidIdFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.getCampaign(0L));
    assertThrows(IllegalArgumentException.class, () -> tool.getCampaign(null));
  }

  @Test
  public void getCampaignNotFoundFails() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenThrow(new ObjectNotFoundException("missing"));
    assertThrows(ObjectNotFoundException.class, () -> tool.getCampaign(CAMPAIGN_ID));
  }

  @Test
  public void getCampaignDeniedFails() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenThrow(new IllegalAccessException("denied"));
    assertThrows(IllegalAccessException.class, () -> tool.getCampaign(CAMPAIGN_ID));
  }

  // --- list_quests ---------------------------------------------------------

  @Test
  public void listQuests() {
    when(ruleService.getRules(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(rule(QUEST_ID, program(CAMPAIGN_ID))));

    List<QuestModel> quests = tool.listQuests(CAMPAIGN_ID, "MANUAL", "ENABLED", null, null);

    assertEquals(1, quests.size());
    assertEquals(QUEST_ID, quests.get(0).getId());
    assertEquals(CAMPAIGN_ID, quests.get(0).getCampaignId());
    assertEquals("MANUAL", quests.get(0).getType());
  }

  @Test
  public void listQuestsInvalidTypeFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.listQuests(null, "BOGUS", null, null, null));
  }

  @Test
  public void listQuestsDoesNotBypassSpaceAcls() {
    // Regression for the ACL bypass: with no campaign id the tool must NOT set
    // allSpaces=true on the RuleFilter, otherwise the DAO drops the
    // "(audienceId IS NULL OR visibility = OPEN)" restriction and leaks
    // RESTRICTED quests from spaces the caller is not a member of.
    when(ruleService.getRules(any(), eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(rule(QUEST_ID, program(CAMPAIGN_ID))));

    tool.listQuests(null, null, null, null, null);

    ArgumentCaptor<RuleFilter> captor = ArgumentCaptor.forClass(RuleFilter.class);
    verify(ruleService).getRules(captor.capture(), eq(USERNAME), anyInt(), anyInt());
    assertFalse("listQuests must not set allSpaces=true (ACL bypass)", captor.getValue().isAllSpaces());
  }

  // --- get_quest -----------------------------------------------------------

  @Test
  public void getQuest() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(rule(QUEST_ID, program(CAMPAIGN_ID)));

    QuestModel quest = tool.getQuest(QUEST_ID);

    assertNotNull(quest);
    assertEquals(QUEST_ID, quest.getId());
    assertEquals("Campaign 5", quest.getCampaignTitle());
  }

  @Test
  public void getQuestNotFoundFails() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenThrow(new ObjectNotFoundException("missing"));
    assertThrows(ObjectNotFoundException.class, () -> tool.getQuest(QUEST_ID));
  }

  // --- get_my_gamification_score -------------------------------------------

  @Test
  public void getMyGamificationScore() {
    stubCurrentUserIdentity();
    when(realizationService.getScoreByIdentityId(String.valueOf(OWN_ID))).thenReturn(120L);
    when(realizationService.getScorePerProgramByIdentityId(String.valueOf(OWN_ID)))
        .thenReturn(List.of(new ProfileReputation(CAMPAIGN_ID, 80L)));
    when(realizationService.getLeaderboardRank(eq(OWN_ID), isNull(), isNull(), isNull(), eq(CAMPAIGN_ID))).thenReturn(2);

    ScoreModel score = tool.getMyGamificationScore(CAMPAIGN_ID, null);

    assertEquals(120L, score.getTotalScore());
    assertEquals(2, score.getRank());
    assertEquals(Long.valueOf(80L), score.getCampaignScore());
    assertEquals(1, score.getScoresPerCampaign().size());
  }

  @Test
  public void getMyGamificationScoreNoCampaign() {
    stubCurrentUserIdentity();
    when(realizationService.getScoreByIdentityId(String.valueOf(OWN_ID))).thenReturn(50L);
    when(realizationService.getScorePerProgramByIdentityId(String.valueOf(OWN_ID))).thenReturn(List.of());
    when(realizationService.getLeaderboardRank(eq(OWN_ID), isNull(), isNull(), isNull(), isNull())).thenReturn(9);

    ScoreModel score = tool.getMyGamificationScore(null, null);

    assertEquals(50L, score.getTotalScore());
    assertEquals(9, score.getRank());
  }

  /**
   * Under a space, the total, the rank and the campaigns are the space's: a
   * campaign of another space is left out of the list and of the total, which
   * is the sum of the campaigns listed, read once with them; a campaign id of
   * another space is ignored, one of the space keeps its score and its rank.
   * Mutants: the list unfiltered; the rank asked platform-wide; the overall
   * total returned; the space's campaign ignored.
   */
  @Test
  public void getMyGamificationScoreKeepsToTheSpace() {
    stubCurrentUserIdentity();
    stubSpacePrograms(12L, List.of(CAMPAIGN_ID, 8L));
    when(realizationService.getScorePerProgramByIdentityId(String.valueOf(OWN_ID)))
        .thenReturn(List.of(new ProfileReputation(CAMPAIGN_ID, 80L), new ProfileReputation(6L, 40L), new ProfileReputation(8L, 5L)));
    when(realizationService.getLeaderboardRank(eq(OWN_ID), isNull(), isNull(), eq(12L), isNull())).thenReturn(3);
    when(realizationService.getLeaderboardRank(eq(OWN_ID), isNull(), isNull(), eq(12L), eq(CAMPAIGN_ID))).thenReturn(2);

    ScoreModel elsewhere = tool.getMyGamificationScore(6L, 12L);
    ScoreModel inSpace = tool.getMyGamificationScore(CAMPAIGN_ID, 12L);

    assertEquals(85L, elsewhere.getTotalScore());
    assertEquals(3, elsewhere.getRank());
    assertNull(elsewhere.getCampaignId());
    assertNull(elsewhere.getCampaignScore());
    assertEquals(List.of(CAMPAIGN_ID, 8L),
                 elsewhere.getScoresPerCampaign().stream().map(ScoreModel.CampaignScore::getCampaignId).toList());
    assertEquals(85L, inSpace.getTotalScore());
    assertEquals(2, inSpace.getRank());
    assertEquals(Long.valueOf(CAMPAIGN_ID), inSpace.getCampaignId());
    assertEquals(Long.valueOf(80L), inSpace.getCampaignScore());
    verify(realizationService, never()).getScoreByIdentityId(any());
    verify(realizationService, never()).getScoreByIdentityIdAndBetweenDates(any(), any(), any(), any(), any());
  }

  /**
   * A space with no campaign of its own answers nothing, not the user's
   * overall score. Mutant: the list unfiltered, which sums every campaign.
   */
  @Test
  public void getMyGamificationScoreInASpaceWithNoCampaign() {
    stubCurrentUserIdentity();
    stubSpacePrograms(13L, List.of());
    when(realizationService.getScorePerProgramByIdentityId(String.valueOf(OWN_ID)))
        .thenReturn(List.of(new ProfileReputation(CAMPAIGN_ID, 80L)));

    ScoreModel score = tool.getMyGamificationScore(null, 13L);

    assertEquals(0L, score.getTotalScore());
    assertTrue(score.getScoresPerCampaign().isEmpty());
    verify(realizationService, never()).getScoreByIdentityIdAndBetweenDates(any(), any(), any(), any(), any());
  }

  // --- get_gamification_leaderboard ----------------------------------------

  @Test
  public void getGamificationLeaderboard() throws Exception {
    when(realizationService.getLeaderboard(any(), eq(USERNAME)))
        .thenReturn(List.of(new StandardLeaderboard(11L, 100L), new StandardLeaderboard(22L, 90L)));

    List<LeaderboardEntryModel> board = tool.getGamificationLeaderboard(CAMPAIGN_ID, 3L, "MONTH", "USER", 10);

    assertEquals(2, board.size());
    assertEquals(1, board.get(0).getRank());
    assertEquals(11L, board.get(0).getIdentityId());
    assertEquals(2, board.get(1).getRank());
  }

  @Test
  public void getGamificationLeaderboardInvalidPeriodFails() {
    assertThrows(IllegalArgumentException.class,
                 () -> tool.getGamificationLeaderboard(null, null, "DECADE", null, null));
  }

  // --- list_my_realizations ------------------------------------------------

  @Test
  public void listMyRealizationsIsSortedNewestFirst() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(realization(1L, QUEST_ID, program(CAMPAIGN_ID))));

    tool.listMyRealizations(null, null, null, null, null, null, null);

    ArgumentCaptor<RealizationFilter> captor = ArgumentCaptor.forClass(RealizationFilter.class);
    verify(realizationService).getRealizationsByFilter(captor.capture(), eq(currentIdentity), anyInt(), anyInt());
    // The DAO's default sort field is 'date' and its direction is ascending
    // unless asked otherwise, so without this the tool would answer with the
    // OLDEST realizations.
    assertTrue(captor.getValue().isSortDescending());
  }

  @Test
  public void listMyRealizationsDefaultsToSelf() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(realization(1L, QUEST_ID, program(CAMPAIGN_ID))));
    RuleDTO quest = rule(QUEST_ID, program(CAMPAIGN_ID));
    when(ruleService.findRuleById(QUEST_ID)).thenReturn(quest);
    when(ruleService.canViewRule(quest, USERNAME)).thenReturn(true);
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));

    List<RealizationModel> realizations = tool.listMyRealizations(null, null, null, null, null, null, null);

    assertEquals(1, realizations.size());
    assertEquals(QUEST_ID, realizations.get(0).getQuestId().longValue());
    assertEquals("ACCEPTED", realizations.get(0).getStatus());
    // the quest and its campaign are named with their pages, never by an id the agent turns into an address
    assertEquals(DOMAIN + String.format(RulePermanentLinkPlugin.URL_FORMAT, SITE, QUEST_ID), realizations.get(0).getQuestUrl());
    assertEquals(DOMAIN + String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, CAMPAIGN_ID), realizations.get(0).getCampaignUrl());
  }

  /**
   * The user's own realizations take no program restriction: one in a program
   * or of a rule the user may no longer view is still listed, with no link to
   * either, since the permanent links check no access. Each program and rule is
   * read once for the page, and never through the rule read that creates the
   * rule's activity. Mutants: either url resolved whatever its read answers;
   * the reads repeated per realization.
   */
  @Test
  public void listMyRealizationsLinksNoQuestOrCampaignTheUserMayNoLongerView() throws Exception {
    stubCurrentUserIdentity();
    long formerCampaignId = 6L;
    long formerQuestId = 8L;
    ProgramDTO formerProgram = program(formerCampaignId);
    RuleDTO formerQuest = rule(formerQuestId, formerProgram);
    RuleDTO quest = rule(QUEST_ID, program(CAMPAIGN_ID));
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(realization(1L, formerQuestId, formerProgram),
                            realization(2L, QUEST_ID, program(CAMPAIGN_ID)),
                            realization(3L, formerQuestId, formerProgram)));
    when(programService.getProgramById(formerCampaignId, USERNAME)).thenThrow(new IllegalAccessException("not viewable"));
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    when(ruleService.findRuleById(formerQuestId)).thenReturn(formerQuest);
    when(ruleService.canViewRule(formerQuest, USERNAME)).thenReturn(false);
    when(ruleService.findRuleById(QUEST_ID)).thenReturn(quest);
    when(ruleService.canViewRule(quest, USERNAME)).thenReturn(true);

    List<RealizationModel> realizations = tool.listMyRealizations(null, null, null, null, null, null, null);

    assertEquals(3, realizations.size());
    for (int index : new int[] { 0, 2 }) {
      RealizationModel former = realizations.get(index);
      assertEquals(formerQuestId, former.getQuestId().longValue());
      assertEquals(formerCampaignId, former.getCampaignId());
      assertNull(former.getQuestUrl());
      assertNull(former.getCampaignUrl());
    }
    assertEquals(DOMAIN + String.format(RulePermanentLinkPlugin.URL_FORMAT, SITE, QUEST_ID), realizations.get(1).getQuestUrl());
    assertEquals(DOMAIN + String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, CAMPAIGN_ID), realizations.get(1).getCampaignUrl());
    verify(programService, times(1)).getProgramById(formerCampaignId, USERNAME);
    verify(ruleService, times(1)).findRuleById(formerQuestId);
    verify(ruleService, never()).findRuleById(anyLong(), any());
  }

  @Test
  public void listMyRealizationsFiltered() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(realization(1L, QUEST_ID, program(CAMPAIGN_ID))));

    List<RealizationModel> realizations =
        tool.listMyRealizations("2026-01-01", "2026-02-01", CAMPAIGN_ID, null, "ACCEPTED", 20, 0);

    assertEquals(1, realizations.size());
  }

  /**
   * A space is a query predicate, and no space leaves the filter without one.
   * Mutant: the space left out of the filter.
   */
  @Test
  public void listMyRealizationsKeepsToTheSpace() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt())).thenReturn(List.of());

    tool.listMyRealizations(null, null, null, 12L, null, null, null);
    tool.listMyRealizations(null, null, null, null, null, null, null);

    ArgumentCaptor<RealizationFilter> captor = ArgumentCaptor.forClass(RealizationFilter.class);
    verify(realizationService, times(2)).getRealizationsByFilter(captor.capture(), eq(currentIdentity), anyInt(), anyInt());
    assertEquals(List.of(12L), captor.getAllValues().get(0).getSpacesIds());
    assertNull(captor.getAllValues().get(1).getSpacesIds());
  }

  @Test
  public void listMyRealizationsInvalidDateFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.listMyRealizations("nope", null, null, null, null, null, null));
  }

  @Test
  public void listMyRealizationsInvalidStatusFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.listMyRealizations(null, null, null, null, "BOGUS", null, null));
  }

  // --- list_my_announcements -----------------------------------------------

  private RealizationDTO announcementRealization(long id, Long ruleId) {
    return new RealizationDTO(id,
                              String.valueOf(OWN_ID),
                              "USER",
                              0,
                              "Quest title",
                              program(CAMPAIGN_ID),
                              null,
                              null,
                              30,
                              null,
                              null,
                              null,
                              ruleId,
                              55L,
                              "I did it",
                              OWN_ID,
                              null,
                              "2026-01-02T00:00:00",
                              null,
                              null,
                              null,
                              "ACCEPTED",
                              EntityType.MANUAL,
                              null);
  }

  @Test
  public void listMyAnnouncementsSelectsAnnouncementsInTheQuery() throws Exception {
    stubCurrentUserIdentity();
    // The page returned by the DAO is already announcements-only: the tool must
    // NOT discriminate after pagination (that made a full page of automatic
    // awards answer "you have no announcement"), so it asks the query to.
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(announcementRealization(1L, QUEST_ID)));

    List<AnnouncementModel> announcements = tool.listMyAnnouncements(null, null, null, null);

    ArgumentCaptor<RealizationFilter> captor = ArgumentCaptor.forClass(RealizationFilter.class);
    verify(realizationService).getRealizationsByFilter(captor.capture(), eq(currentIdentity), anyInt(), anyInt());
    assertTrue("list_my_announcements must select announcements in the query, not after pagination",
               captor.getValue().isAnnouncementsOnly());
    assertTrue("list_my_announcements is documented 'most recent first'", captor.getValue().isSortDescending());
    assertEquals(1, announcements.size());
    assertEquals(1L, announcements.get(0).getId());
    assertEquals(QUEST_ID, announcements.get(0).getQuestId().longValue());
    assertEquals(Long.valueOf(OWN_ID), announcements.get(0).getCreator());
    assertEquals(Long.valueOf(55L), announcements.get(0).getActivityId());
    assertEquals("I did it", announcements.get(0).getComment());
  }

  @Test
  public void listMyAnnouncementsForQuest() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(announcementRealization(1L, QUEST_ID)));

    List<AnnouncementModel> announcements = tool.listMyAnnouncements(QUEST_ID, null, 20, 0);

    assertEquals(1, announcements.size());
  }

  /**
   * A space is a query predicate, and no space leaves the filter without one.
   * Mutant: the space left out of the filter.
   */
  @Test
  public void listMyAnnouncementsKeepsToTheSpace() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt())).thenReturn(List.of());

    tool.listMyAnnouncements(null, 12L, null, null);
    tool.listMyAnnouncements(null, null, null, null);

    ArgumentCaptor<RealizationFilter> captor = ArgumentCaptor.forClass(RealizationFilter.class);
    verify(realizationService, times(2)).getRealizationsByFilter(captor.capture(), eq(currentIdentity), anyInt(), anyInt());
    assertEquals(List.of(12L), captor.getAllValues().get(0).getSpacesIds());
    assertTrue(captor.getAllValues().get(0).isAnnouncementsOnly());
    assertNull(captor.getAllValues().get(1).getSpacesIds());
  }

  @Test
  public void listMyAnnouncementsEmpty() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt())).thenReturn(List.of());

    List<AnnouncementModel> announcements = tool.listMyAnnouncements(null, null, null, null);

    assertTrue(announcements.isEmpty());
  }

  // --- create_campaign -----------------------------------------------------

  @Test
  public void createCampaign() throws Exception {
    stubCurrentUserIdentity();
    when(programService.canAddProgram(USERNAME)).thenReturn(true);
    when(programService.createProgram(any(ProgramDTO.class), eq(currentIdentity))).thenReturn(program(CAMPAIGN_ID));

    CampaignModel campaign = tool.createCampaign("New campaign", "desc", null, 500L);

    assertNotNull(campaign);
    assertEquals(CAMPAIGN_ID, campaign.getId());
    ArgumentCaptor<ProgramDTO> captor = ArgumentCaptor.forClass(ProgramDTO.class);
    verify(programService).createProgram(captor.capture(), eq(currentIdentity));
    // No space id: a platform-wide campaign, which IS what "open" means.
    assertTrue(captor.getValue().isOpen());
    assertEquals(0, captor.getValue().getSpaceId());
    assertNull("the visibility is the service's to compute, never the caller's", captor.getValue().getVisibility());
  }

  @Test
  public void createCampaignInSpaceStaysInThatSpace() throws Exception {
    stubCurrentUserIdentity();
    when(programService.canAddProgram(USERNAME, 3L)).thenReturn(true);
    when(programService.createProgram(any(ProgramDTO.class), eq(currentIdentity))).thenReturn(program(CAMPAIGN_ID));

    CampaignModel campaign = tool.createCampaign("New campaign", null, 3L, null);

    assertNotNull(campaign);
    ArgumentCaptor<ProgramDTO> captor = ArgumentCaptor.forClass(ProgramDTO.class);
    verify(programService).createProgram(captor.capture(), eq(currentIdentity));
    assertEquals(3L, captor.getValue().getSpaceId());
    // GAMIFICATION_DOMAIN.DESCRIPTION is NOT NULL: an omitted description must
    // be empty, not null, or the insert fails on commit.
    assertEquals("", captor.getValue().getDescription());
    // An OPEN program has its audience reset to 0 by ProgramServiceImpl and its
    // visibility computed from space 0 (i.e. OPEN): a space-scoped campaign
    // must therefore never be created open, or it becomes a platform-wide,
    // everyone-visible campaign.
    assertFalse("a campaign targeting a space must not be created open", captor.getValue().isOpen());
    assertNull("the visibility is the service's to compute, never the caller's", captor.getValue().getVisibility());
  }

  @Test
  public void createCampaignNotAdminFails() {
    when(programService.canAddProgram(USERNAME)).thenReturn(false);
    assertThrows(IllegalAccessException.class, () -> tool.createCampaign("New campaign", null, null, null));
  }

  @Test
  public void createCampaignNotSpaceManagerFails() {
    when(programService.canAddProgram(USERNAME, 3L)).thenReturn(false);
    assertThrows(IllegalAccessException.class, () -> tool.createCampaign("New campaign", null, 3L, null));
  }

  @Test
  public void createCampaignBlankTitleFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.createCampaign("  ", null, null, null));
  }

  @Test
  public void createCampaignDeniedByServiceFails() throws Exception {
    stubCurrentUserIdentity();
    when(programService.canAddProgram(USERNAME)).thenReturn(true);
    when(programService.createProgram(any(ProgramDTO.class), eq(currentIdentity)))
        .thenThrow(new IllegalAccessException("denied"));
    assertThrows(IllegalAccessException.class, () -> tool.createCampaign("Title", null, null, null));
  }

  // --- create_quest --------------------------------------------------------

  @Test
  public void createQuest() throws Exception {
    ProgramDTO program = program(CAMPAIGN_ID);
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program);
    when(programService.isProgramOwner(CAMPAIGN_ID, USERNAME)).thenReturn(true);
    when(ruleService.createRule(any(RuleDTO.class), eq(USERNAME))).thenReturn(rule(QUEST_ID, program));

    QuestModel quest = tool.createQuest(CAMPAIGN_ID, "Do it", "desc", 50, null, "2026-01-01", "2026-03-01", "DAILY", null);

    assertNotNull(quest);
    assertEquals(QUEST_ID, quest.getId());
    ArgumentCaptor<RuleDTO> captor = ArgumentCaptor.forClass(RuleDTO.class);
    verify(ruleService).createRule(captor.capture(), eq(USERNAME));
    // The rule service computes the quest's activity only for a publication
    // instance; a plain rule leaves it to the lazy back-fill of the first read,
    // which always creates it hidden and empty.
    assertTrue("create_quest must pass a RulePublication so the publication is a decision, not a side effect",
               captor.getValue() instanceof RulePublication);
    assertFalse("an EVA-created quest is not published unless asked", ((RulePublication) captor.getValue()).isPublish());
  }

  @Test
  public void createQuestPublishesWhenAsked() throws Exception {
    ProgramDTO program = program(CAMPAIGN_ID);
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program);
    when(programService.isProgramOwner(CAMPAIGN_ID, USERNAME)).thenReturn(true);
    when(ruleService.createRule(any(RuleDTO.class), eq(USERNAME))).thenReturn(rule(QUEST_ID, program));

    tool.createQuest(CAMPAIGN_ID, "Do it", "desc", 50, null, null, null, null, true);

    ArgumentCaptor<RuleDTO> captor = ArgumentCaptor.forClass(RuleDTO.class);
    verify(ruleService).createRule(captor.capture(), eq(USERNAME));
    RulePublication publication = (RulePublication) captor.getValue();
    assertTrue(publication.isPublish());
    // RuleServiceImpl#setActivityParams blanks the activity title/body when the
    // message is blank, so a published quest needs one.
    assertEquals("desc", publication.getMessage());
  }

  @Test
  public void createQuestAutomaticRejected() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    assertThrows(IllegalStateException.class,
                 () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 50, "AUTOMATIC", null, null, null, null));
  }

  @Test
  public void createQuestNotOwnerFails() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    when(programService.isProgramOwner(CAMPAIGN_ID, USERNAME)).thenReturn(false);
    assertThrows(IllegalAccessException.class,
                 () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 50, null, null, null, null, null));
  }

  @Test
  public void createQuestBlankTitleFails() {
    assertThrows(IllegalArgumentException.class,
                 () -> tool.createQuest(CAMPAIGN_ID, " ", null, 50, null, null, null, null, null));
  }

  @Test
  public void createQuestInvalidScoreFails() {
    assertThrows(IllegalArgumentException.class,
                 () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 0, null, null, null, null, null));
    assertThrows(IllegalArgumentException.class,
                 () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, null, null, null, null, null, null));
  }

  @Test
  public void createQuestMissingCampaignFails() {
    assertThrows(IllegalArgumentException.class,
                 () -> tool.createQuest(null, "Do it", null, 50, null, null, null, null, null));
  }

  @Test
  public void createQuestDeniedByServiceFails() throws Exception {
    ProgramDTO program = program(CAMPAIGN_ID);
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program);
    when(programService.isProgramOwner(CAMPAIGN_ID, USERNAME)).thenReturn(true);
    when(ruleService.createRule(any(RuleDTO.class), eq(USERNAME))).thenThrow(new IllegalAccessException("denied"));
    assertThrows(IllegalAccessException.class,
                 () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 50, null, null, null, null, null));
  }

  // --- update_quest --------------------------------------------------------

  @Test
  public void updateQuest() throws Exception {
    ProgramDTO program = program(CAMPAIGN_ID);
    RuleDTO existing = rule(QUEST_ID, program);
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(existing);
    when(ruleService.canEditRule(existing, USERNAME)).thenReturn(true);
    when(ruleService.updateRule(any(RuleDTO.class), eq(USERNAME))).thenAnswer(invocation -> invocation.getArgument(0));

    QuestModel quest = tool.updateQuest(QUEST_ID, "Renamed", null, 75, null, null, false, null);

    assertNotNull(quest);
    assertEquals("Renamed", quest.getTitle());
    assertEquals(75, quest.getScore());
    assertTrue(!quest.isEnabled());
    assertEquals("A quest", quest.getDescription()); // untouched field preserved
  }

  @Test
  public void updateQuestNotOwnerFails() throws Exception {
    RuleDTO existing = rule(QUEST_ID, program(CAMPAIGN_ID));
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(existing);
    when(ruleService.canEditRule(existing, USERNAME)).thenReturn(false);
    assertThrows(IllegalAccessException.class,
                 () -> tool.updateQuest(QUEST_ID, "Renamed", null, null, null, null, null, null));
  }

  @Test
  public void updateQuestMissingIdFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.updateQuest(null, "Renamed", null, null, null, null, null, null));
  }

  @Test
  public void updateQuestDeniedByServiceFails() throws Exception {
    RuleDTO existing = rule(QUEST_ID, program(CAMPAIGN_ID));
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(existing);
    when(ruleService.canEditRule(existing, USERNAME)).thenReturn(true);
    when(ruleService.updateRule(any(RuleDTO.class), eq(USERNAME))).thenThrow(new IllegalAccessException("denied"));
    assertThrows(IllegalAccessException.class,
                 () -> tool.updateQuest(QUEST_ID, "Renamed", null, null, null, null, null, null));
  }

  // --- delete_quest --------------------------------------------------------

  @Test
  public void deleteQuest() throws Exception {
    when(ruleService.deleteRuleById(QUEST_ID, USERNAME)).thenReturn(rule(QUEST_ID, program(CAMPAIGN_ID)));

    String result = tool.deleteQuest(QUEST_ID);

    assertTrue(result.contains(String.valueOf(QUEST_ID)));
    verify(ruleService).deleteRuleById(QUEST_ID, USERNAME);
  }

  @Test
  public void deleteQuestNotOwnerFails() throws Exception {
    doThrow(new IllegalAccessException("denied")).when(ruleService).deleteRuleById(QUEST_ID, USERNAME);
    assertThrows(IllegalAccessException.class, () -> tool.deleteQuest(QUEST_ID));
  }

  @Test
  public void deleteQuestNotFoundFails() throws Exception {
    doThrow(new ObjectNotFoundException("missing")).when(ruleService).deleteRuleById(QUEST_ID, USERNAME);
    assertThrows(ObjectNotFoundException.class, () -> tool.deleteQuest(QUEST_ID));
  }

  @Test
  public void deleteQuestInvalidIdFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.deleteQuest(0L));
  }

  // --- announce_quest ------------------------------------------------------

  @Test
  public void announceQuest() throws Exception {
    Announcement created = new Announcement();
    created.setId(42L);
    created.setChallengeId(QUEST_ID);
    created.setComment("done");
    created.setActivityId(100L);
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(rule(QUEST_ID, program(CAMPAIGN_ID)));
    when(announcementService.createAnnouncement(any(Announcement.class), any(), eq(USERNAME))).thenReturn(created);

    AnnouncementModel result = tool.announceQuest(QUEST_ID, "done");

    assertEquals(42L, result.getId());
    assertEquals(QUEST_ID, result.getQuestId().longValue());
    assertEquals(Long.valueOf(100L), result.getActivityId());
  }

  @Test
  public void announceQuestNotChallengeFails() throws Exception {
    RuleDTO automatic = rule(QUEST_ID, program(CAMPAIGN_ID));
    automatic.setType(EntityType.AUTOMATIC);
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(automatic);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> tool.announceQuest(QUEST_ID, "done"));

    assertTrue(e.getMessage().contains("not a manual challenge"));
  }

  @Test
  public void announceQuestKeepsAnUnrelatedIllegalStateMessage() throws Exception {
    // The previous catch labelled ANY IllegalStateException "not a manual
    // challenge", hiding the real cause of every other failure.
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(rule(QUEST_ID, program(CAMPAIGN_ID)));
    when(announcementService.createAnnouncement(any(Announcement.class), any(), eq(USERNAME)))
        .thenThrow(new IllegalStateException("endDate must be greater than startDate"));

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> tool.announceQuest(QUEST_ID, "done"));

    assertEquals("endDate must be greater than startDate", e.getMessage());
  }

  @Test
  public void announceQuestNotAllowedFails() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(rule(QUEST_ID, program(CAMPAIGN_ID)));
    when(announcementService.createAnnouncement(any(Announcement.class), any(), eq(USERNAME)))
        .thenThrow(new IllegalAccessException("not allowed"));
    assertThrows(IllegalAccessException.class, () -> tool.announceQuest(QUEST_ID, "done"));
  }

  @Test
  public void announceQuestNotFoundFails() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenThrow(new ObjectNotFoundException("missing"));
    assertThrows(ObjectNotFoundException.class, () -> tool.announceQuest(QUEST_ID, "done"));
  }

  /**
   * Covers a guard, not a live path: RuleServiceImpl throws
   * ObjectNotFoundException rather than returning null, so this pins the tool's
   * behaviour if that service contract ever changes — announceQuest reads the
   * rule's type and would otherwise NPE.
   */
  @Test
  public void announceQuestUnknownQuestFails() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(null);
    assertThrows(ObjectNotFoundException.class, () -> tool.announceQuest(QUEST_ID, "done"));
  }

  @Test
  public void announceQuestInvalidIdFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.announceQuest(null, "done"));
  }

  // --- cancel_quest_announcement -------------------------------------------

  @Test
  public void cancelQuestAnnouncement() throws Exception {
    when(announcementService.deleteAnnouncement(anyLong(), eq(USERNAME))).thenReturn(new Announcement());

    String result = tool.cancelQuestAnnouncement(42L);

    assertTrue(result.contains("42"));
    verify(announcementService).deleteAnnouncement(42L, USERNAME);
  }

  @Test
  public void cancelQuestAnnouncementNotAuthorFails() throws Exception {
    doThrow(new IllegalAccessException("denied")).when(announcementService).deleteAnnouncement(42L, USERNAME);
    assertThrows(IllegalAccessException.class, () -> tool.cancelQuestAnnouncement(42L));
  }

  @Test
  public void cancelQuestAnnouncementNotFoundFails() throws Exception {
    doThrow(new ObjectNotFoundException("missing")).when(announcementService).deleteAnnouncement(42L, USERNAME);
    assertThrows(ObjectNotFoundException.class, () -> tool.cancelQuestAnnouncement(42L));
  }

  @Test
  public void cancelQuestAnnouncementInvalidIdFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.cancelQuestAnnouncement(0L));
  }

  // --- list_campaign_badges ------------------------------------------------

  private BadgeDTO badge(long id, String title, int neededScore, long iconFileId) {
    BadgeDTO badge = new BadgeDTO();
    badge.setId(id);
    badge.setTitle(title);
    badge.setDescription("Reach " + neededScore + " points");
    badge.setNeededScore(neededScore);
    badge.setIconFileId(iconFileId);
    badge.setEnabled(true);
    return badge;
  }

  @Test
  public void listCampaignBadgesOrdersByNeededScore() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    // returned out of order to prove the tool sorts them by needed score
    when(badgeService.findEnabledBadgesByProgramId(CAMPAIGN_ID))
        .thenReturn(List.of(badge(2L, "Gold", 300, 20L), badge(1L, "Silver", 100, 10L)));

    List<BadgeModel> badges = tool.listCampaignBadges(CAMPAIGN_ID);

    assertEquals(2, badges.size());
    assertEquals("Silver", badges.get(0).getTitle());
    assertEquals(1, badges.get(0).getLevel());
    assertEquals(100, badges.get(0).getNeededScore());
    assertEquals(10L, badges.get(0).getIconFileId());
    assertEquals("Gold", badges.get(1).getTitle());
    assertEquals(2, badges.get(1).getLevel());
  }

  @Test
  public void listCampaignBadgesEmpty() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    when(badgeService.findEnabledBadgesByProgramId(CAMPAIGN_ID)).thenReturn(List.of());

    List<BadgeModel> badges = tool.listCampaignBadges(CAMPAIGN_ID);

    assertTrue(badges.isEmpty());
  }

  @Test
  public void listCampaignBadgesUnknownCampaignFails() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenThrow(new ObjectNotFoundException("missing"));
    assertThrows(ObjectNotFoundException.class, () -> tool.listCampaignBadges(CAMPAIGN_ID));
  }

  @Test
  public void listCampaignBadgesInvalidIdFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.listCampaignBadges(0L));
  }

  // --- get_my_campaigns ----------------------------------------------------

  @Test
  public void getMyCampaignsDelegatesTheUnionToTheService() throws Exception {
    // The member/owned union, its deduplication, its single pagination and the
    // exclusion of soft-deleted programs are ProgramService's job (see
    // ProgramServiceTest#testGetMyProgramIds*): the tool must not rebuild them,
    // and must not resolve a deleted program by id — that broadcasts a
    // program-deleted event on a read path and then fails, which is what used
    // to shorten the page.
    when(programService.getMyProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(5L, 6L, 7L));
    when(programService.getProgramById(5L, USERNAME)).thenReturn(program(5L));
    when(programService.getProgramById(6L, USERNAME)).thenReturn(program(6L));
    when(programService.getProgramById(7L, USERNAME)).thenReturn(program(7L));

    List<CampaignModel> campaigns = tool.getMyCampaigns(3, 0);

    assertEquals(3, campaigns.size());
    assertEquals(List.of(5L, 6L, 7L), campaigns.stream().map(CampaignModel::getId).toList());
    verify(programService).getMyProgramIds(USERNAME, 0, 3);
    verify(programService, never()).getMemberProgramIds(any(), anyInt(), anyInt());
    verify(programService, never()).getOwnedProgramIds(any(), anyInt(), anyInt());
  }

  @Test
  public void getMyCampaignsSkipsUnviewable() throws Exception {
    when(programService.getMyProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(5L, 8L));
    when(programService.getProgramById(5L, USERNAME)).thenReturn(program(5L));
    when(programService.getProgramById(8L, USERNAME)).thenThrow(new IllegalAccessException("denied"));

    List<CampaignModel> campaigns = tool.getMyCampaigns(10, 0);

    assertEquals(1, campaigns.size());
    assertEquals(5L, campaigns.get(0).getId());
  }

  @Test
  public void getMyCampaignsEmpty() {
    when(programService.getMyProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(null);

    List<CampaignModel> campaigns = tool.getMyCampaigns(null, null);

    assertTrue(campaigns.isEmpty());
  }

  // --- list_joinable_campaigns ---------------------------------------------

  @Test
  public void listJoinableCampaigns() throws Exception {
    when(programService.getPublicProgramIds(anyInt(), anyInt())).thenReturn(List.of(5L, 6L));
    when(programService.getProgramById(5L, USERNAME)).thenReturn(program(5L));
    when(programService.getProgramById(6L, USERNAME)).thenReturn(program(6L));

    List<CampaignModel> campaigns = tool.listJoinableCampaigns(null, null);

    assertEquals(2, campaigns.size());
    assertEquals(DOMAIN + String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, 5L), campaigns.get(0).getUrl());
    assertEquals(DOMAIN + String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, 6L), campaigns.get(1).getUrl());
  }

  @Test
  public void listJoinableCampaignsEmpty() {
    when(programService.getPublicProgramIds(anyInt(), anyInt())).thenReturn(null);

    List<CampaignModel> campaigns = tool.listJoinableCampaigns(null, null);

    assertTrue(campaigns.isEmpty());
  }

  // --- get_my_points_total -------------------------------------------------

  @Test
  public void getMyPointsTotal() {
    stubCurrentUserIdentity();
    when(realizationService.getScoreByIdentityIdAndBetweenDates(eq(String.valueOf(OWN_ID)),
                                                                any(),
                                                                any(),
                                                                isNull(),
                                                                eq(CAMPAIGN_ID))).thenReturn(240L);

    PointsTotalModel total = tool.getMyPointsTotal("2026-01-01", "2026-02-01", CAMPAIGN_ID, null);

    assertEquals(240L, total.getTotalPoints());
    assertEquals("2026-01-01", total.getFromDate());
    assertEquals("2026-02-01", total.getToDate());
    assertEquals(Long.valueOf(CAMPAIGN_ID), total.getCampaignId());
  }

  @Test
  public void getMyPointsTotalNoDatesNoCampaign() {
    stubCurrentUserIdentity();
    when(realizationService.getScoreByIdentityIdAndBetweenDates(eq(String.valueOf(OWN_ID)),
                                                                isNull(),
                                                                isNull(),
                                                                isNull(),
                                                                isNull())).thenReturn(500L);

    PointsTotalModel total = tool.getMyPointsTotal(null, null, null, null);

    assertEquals(500L, total.getTotalPoints());
    assertEquals(null, total.getCampaignId());
  }

  @Test
  public void getMyPointsTotalInvalidDateFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.getMyPointsTotal("not-a-date", null, null, null));
  }

  /**
   * Under a space, the points are counted in that space's campaigns, open ones
   * left out; a space with no campaign answers zero without asking the
   * service, which reads an empty campaign list as every campaign; a campaign
   * of the space counts that campaign, one of another space answers zero.
   * The space's campaigns are read with the service's bound, 100, so that a
   * score's campaigns, total and rank count one set. Mutants: the space not
   * passed; the empty space handed to the service; the campaign kept whatever
   * its space; the space's campaign answered zero; the campaigns read unbounded.
   */
  @Test
  public void getMyPointsTotalKeepsToTheSpace() {
    stubCurrentUserIdentity();
    stubSpacePrograms(12L, List.of(CAMPAIGN_ID));
    stubSpacePrograms(13L, List.of());
    when(realizationService.getScoreByIdentityIdAndBetweenDates(eq(String.valueOf(OWN_ID)), isNull(), isNull(), eq(12L), isNull()))
        .thenReturn(70L);
    when(realizationService.getScoreByIdentityIdAndBetweenDates(eq(String.valueOf(OWN_ID)), isNull(), isNull(), isNull(), eq(CAMPAIGN_ID)))
        .thenReturn(30L);

    assertEquals(70L, tool.getMyPointsTotal(null, null, null, 12L).getTotalPoints());
    assertEquals(30L, tool.getMyPointsTotal(null, null, CAMPAIGN_ID, 12L).getTotalPoints());
    assertEquals(0L, tool.getMyPointsTotal(null, null, null, 13L).getTotalPoints());
    assertEquals(0L, tool.getMyPointsTotal(null, null, 6L, 12L).getTotalPoints());
    verify(realizationService, never()).getScoreByIdentityIdAndBetweenDates(any(), any(), any(), eq(13L), any());
    verify(realizationService, never()).getScoreByIdentityIdAndBetweenDates(any(), any(), any(), any(), eq(6L));
    ArgumentCaptor<ProgramFilter> captor = ArgumentCaptor.forClass(ProgramFilter.class);
    verify(programService, atLeastOnce()).getProgramIds(captor.capture(), eq(0), eq(100));
    assertTrue(captor.getValue().isExcludeOpen());
  }

  // --- get_my_score_breakdown ----------------------------------------------

  @Test
  public void getMyScoreBreakdown() throws Exception {
    stubCurrentUserIdentity();
    PiechartLeaderboard slice = new PiechartLeaderboard(CAMPAIGN_ID, 80L);
    when(realizationService.getLeaderboardStatsByIdentityId(eq(String.valueOf(OWN_ID)), isNull(), eq("MONTH"), any(), any()))
        .thenReturn(List.of(slice));
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));

    List<ScoreBreakdownEntryModel> breakdown = tool.getMyScoreBreakdown("MONTH", null);

    assertEquals(1, breakdown.size());
    assertEquals(CAMPAIGN_ID, breakdown.get(0).getCampaignId());
    assertEquals(80L, breakdown.get(0).getPoints());
    assertEquals("Campaign 5", breakdown.get(0).getLabel());
    assertEquals(DOMAIN + String.format(ProgramPermanentLinkPlugin.URL_FORMAT, SITE, CAMPAIGN_ID), breakdown.get(0).getUrl());
  }

  /**
   * The points a user earned in a program they may no longer view still count,
   * but the entry gets no link to that program: the permanent link checks no
   * access. Nor does it get a label: the leaderboard rows carry none, and the
   * program's title is not read. Mutant: the url resolved whatever the program
   * read answers.
   */
  @Test
  public void getMyScoreBreakdownLinksNoProgramTheUserMayNoLongerView() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getLeaderboardStatsByIdentityId(eq(String.valueOf(OWN_ID)), isNull(), eq("MONTH"), any(), any()))
        .thenReturn(List.of(new PiechartLeaderboard(CAMPAIGN_ID, 80L)));
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenThrow(new IllegalAccessException("not viewable"));

    List<ScoreBreakdownEntryModel> breakdown = tool.getMyScoreBreakdown("MONTH", null);

    assertEquals(1, breakdown.size());
    assertEquals(80L, breakdown.get(0).getPoints());
    assertNull(breakdown.get(0).getLabel());
    assertNull(breakdown.get(0).getUrl());
  }

  @Test
  public void getMyScoreBreakdownDefaultsToAll() {
    stubCurrentUserIdentity();
    when(realizationService.getLeaderboardStatsByIdentityId(eq(String.valueOf(OWN_ID)), isNull(), eq("ALL"), isNull(), isNull()))
        .thenReturn(List.of());

    List<ScoreBreakdownEntryModel> breakdown = tool.getMyScoreBreakdown(null, null);

    assertTrue(breakdown.isEmpty());
  }

  /**
   * Under a space, the breakdown is asked for that space only. Mutant: the
   * space not passed to the service.
   */
  @Test
  public void getMyScoreBreakdownKeepsToTheSpace() {
    stubCurrentUserIdentity();
    when(realizationService.getLeaderboardStatsByIdentityId(eq(String.valueOf(OWN_ID)), eq(12L), eq("ALL"), isNull(), isNull()))
        .thenReturn(List.of(new PiechartLeaderboard(CAMPAIGN_ID, 80L)));

    List<ScoreBreakdownEntryModel> breakdown = tool.getMyScoreBreakdown(null, 12L);

    assertEquals(1, breakdown.size());
    verify(realizationService, never()).getLeaderboardStatsByIdentityId(any(), isNull(), any(), any(), any());
  }

  @Test
  public void getMyScoreBreakdownInvalidPeriodFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.getMyScoreBreakdown("DECADE", null));
  }

  // --- check_quest_availability --------------------------------------------

  @Test
  public void checkQuestAvailabilityAvailable() throws Exception {
    stubCurrentUserIdentity();
    RuleDTO existing = rule(QUEST_ID, program(CAMPAIGN_ID));
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(existing);
    when(realizationService.hasPendingRealization(QUEST_ID, String.valueOf(OWN_ID))).thenReturn(false);
    when(realizationService.getRealizationValidityContext(existing, OWN_ID)).thenReturn(new RealizationValidityContext());

    QuestAvailabilityModel result = tool.checkQuestAvailability(QUEST_ID);

    assertTrue(result.isAvailable());
    assertEquals(QUEST_ID, result.getQuestId());
    assertEquals("Quest 7", result.getQuestTitle());
    assertEquals(DOMAIN + String.format(RulePermanentLinkPlugin.URL_FORMAT, SITE, QUEST_ID), result.getUrl());
  }

  @Test
  public void checkQuestAvailabilityPendingNotAvailable() throws Exception {
    stubCurrentUserIdentity();
    RuleDTO existing = rule(QUEST_ID, program(CAMPAIGN_ID));
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(existing);
    when(realizationService.hasPendingRealization(QUEST_ID, String.valueOf(OWN_ID))).thenReturn(true);
    when(realizationService.getRealizationValidityContext(existing, OWN_ID)).thenReturn(new RealizationValidityContext());

    QuestAvailabilityModel result = tool.checkQuestAvailability(QUEST_ID);

    assertTrue(!result.isAvailable());
    assertTrue(result.getReason().toLowerCase().contains("pending"));
  }

  @Test
  public void checkQuestAvailabilityOutsideDatesNotAvailable() throws Exception {
    stubCurrentUserIdentity();
    RuleDTO existing = rule(QUEST_ID, program(CAMPAIGN_ID));
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(existing);
    when(realizationService.hasPendingRealization(QUEST_ID, String.valueOf(OWN_ID))).thenReturn(false);
    RealizationValidityContext context = new RealizationValidityContext();
    context.setValidDates(false);
    when(realizationService.getRealizationValidityContext(existing, OWN_ID)).thenReturn(context);

    QuestAvailabilityModel result = tool.checkQuestAvailability(QUEST_ID);

    assertTrue(!result.isAvailable());
    assertTrue(result.getReason().toLowerCase().contains("dates"));
  }

  @Test
  public void checkQuestAvailabilityAutomaticNotAvailable() throws Exception {
    stubCurrentUserIdentity();
    RuleDTO existing = rule(QUEST_ID, program(CAMPAIGN_ID));
    existing.setType(EntityType.AUTOMATIC);
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenReturn(existing);
    when(realizationService.hasPendingRealization(QUEST_ID, String.valueOf(OWN_ID))).thenReturn(false);
    when(realizationService.getRealizationValidityContext(existing, OWN_ID)).thenReturn(new RealizationValidityContext());

    QuestAvailabilityModel result = tool.checkQuestAvailability(QUEST_ID);

    assertTrue(!result.isAvailable());
    assertTrue(result.getReason().toLowerCase().contains("automatic"));
  }

  @Test
  public void checkQuestAvailabilityUnknownQuestFails() throws Exception {
    when(ruleService.findRuleById(QUEST_ID, USERNAME)).thenThrow(new ObjectNotFoundException("missing"));
    assertThrows(ObjectNotFoundException.class, () -> tool.checkQuestAvailability(QUEST_ID));
  }

  @Test
  public void checkQuestAvailabilityInvalidIdFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.checkQuestAvailability(0L));
  }

}

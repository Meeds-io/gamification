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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.exoplatform.commons.exception.ObjectNotFoundException;
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
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.rest.model.RealizationValidityContext;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.BadgeService;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

public class GamificationMcpToolTest {

  private static final String USERNAME    = "testuser1";

  private static final long   OWN_ID      = 99L;

  private static final long   CAMPAIGN_ID = 5L;

  private static final long   QUEST_ID    = 7L;

  private ProgramService      programService;

  private RuleService         ruleService;

  private RealizationService  realizationService;

  private AnnouncementService announcementService;

  private BadgeService        badgeService;

  private IdentityManager     identityManager;

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
    currentIdentity = new Identity(USERNAME);
    tool = new GamificationMcpTool(programService,
                                   ruleService,
                                   realizationService,
                                   announcementService,
                                   badgeService,
                                   identityManager) {
      @Override
      public Identity getCurrentUserAclIdentity() {
        return currentIdentity;
      }
    };
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
    assertThrows(IllegalStateException.class, () -> tool.getCampaign(CAMPAIGN_ID));
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

    ScoreModel score = tool.getMyGamificationScore(CAMPAIGN_ID);

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

    ScoreModel score = tool.getMyGamificationScore(null);

    assertEquals(50L, score.getTotalScore());
    assertEquals(9, score.getRank());
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
  public void listMyRealizationsDefaultsToSelf() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(realization(1L, QUEST_ID, program(CAMPAIGN_ID))));

    List<RealizationModel> realizations = tool.listMyRealizations(null, null, null, null, null, null);

    assertEquals(1, realizations.size());
    assertEquals(QUEST_ID, realizations.get(0).getQuestId().longValue());
    assertEquals("ACCEPTED", realizations.get(0).getStatus());
  }

  @Test
  public void listMyRealizationsFiltered() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(realization(1L, QUEST_ID, program(CAMPAIGN_ID))));

    List<RealizationModel> realizations =
        tool.listMyRealizations("2026-01-01", "2026-02-01", CAMPAIGN_ID, "ACCEPTED", 20, 0);

    assertEquals(1, realizations.size());
  }

  @Test
  public void listMyRealizationsInvalidDateFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.listMyRealizations("nope", null, null, null, null, null));
  }

  @Test
  public void listMyRealizationsInvalidStatusFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.listMyRealizations(null, null, null, "BOGUS", null, null));
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
  public void listMyAnnouncementsExcludesNonAnnouncements() throws Exception {
    stubCurrentUserIdentity();
    // second realization has a null creator (automatic award) and must be filtered out
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt()))
        .thenReturn(List.of(announcementRealization(1L, QUEST_ID), realization(2L, QUEST_ID, program(CAMPAIGN_ID))));

    List<AnnouncementModel> announcements = tool.listMyAnnouncements(null, null, null);

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

    List<AnnouncementModel> announcements = tool.listMyAnnouncements(QUEST_ID, 20, 0);

    assertEquals(1, announcements.size());
  }

  @Test
  public void listMyAnnouncementsEmpty() throws Exception {
    stubCurrentUserIdentity();
    when(realizationService.getRealizationsByFilter(any(), eq(currentIdentity), anyInt(), anyInt())).thenReturn(List.of());

    List<AnnouncementModel> announcements = tool.listMyAnnouncements(null, null, null);

    assertTrue(announcements.isEmpty());
  }

  // --- create_campaign -----------------------------------------------------

  @Test
  public void createCampaign() throws Exception {
    stubCurrentUserIdentity();
    when(programService.canAddProgram(USERNAME)).thenReturn(true);
    when(programService.createProgram(any(ProgramDTO.class), eq(currentIdentity))).thenReturn(program(CAMPAIGN_ID));

    CampaignModel campaign = tool.createCampaign("New campaign", "desc", null, 500L, true, null);

    assertNotNull(campaign);
    assertEquals(CAMPAIGN_ID, campaign.getId());
    verify(programService).createProgram(any(ProgramDTO.class), eq(currentIdentity));
  }

  @Test
  public void createCampaignInSpace() throws Exception {
    stubCurrentUserIdentity();
    when(programService.canAddProgram(USERNAME, 3L)).thenReturn(true);
    when(programService.createProgram(any(ProgramDTO.class), eq(currentIdentity))).thenReturn(program(CAMPAIGN_ID));

    CampaignModel campaign = tool.createCampaign("New campaign", null, 3L, null, null, "RESTRICTED");

    assertNotNull(campaign);
    verify(programService).createProgram(any(ProgramDTO.class), eq(currentIdentity));
  }

  @Test
  public void createCampaignNotAdminFails() {
    when(programService.canAddProgram(USERNAME)).thenReturn(false);
    assertThrows(IllegalStateException.class, () -> tool.createCampaign("New campaign", null, null, null, null, null));
  }

  @Test
  public void createCampaignBlankTitleFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.createCampaign("  ", null, null, null, null, null));
  }

  @Test
  public void createCampaignInvalidVisibilityFails() {
    when(programService.canAddProgram(USERNAME)).thenReturn(true);
    assertThrows(IllegalArgumentException.class, () -> tool.createCampaign("Title", null, null, null, null, "SECRET"));
  }

  @Test
  public void createCampaignDeniedByServiceFails() throws Exception {
    stubCurrentUserIdentity();
    when(programService.canAddProgram(USERNAME)).thenReturn(true);
    when(programService.createProgram(any(ProgramDTO.class), eq(currentIdentity)))
        .thenThrow(new IllegalAccessException("denied"));
    assertThrows(IllegalStateException.class, () -> tool.createCampaign("Title", null, null, null, null, null));
  }

  // --- create_quest --------------------------------------------------------

  @Test
  public void createQuest() throws Exception {
    ProgramDTO program = program(CAMPAIGN_ID);
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program);
    when(programService.isProgramOwner(CAMPAIGN_ID, USERNAME)).thenReturn(true);
    when(ruleService.createRule(any(RuleDTO.class), eq(USERNAME))).thenReturn(rule(QUEST_ID, program));

    QuestModel quest = tool.createQuest(CAMPAIGN_ID, "Do it", "desc", 50, null, "2026-01-01", "2026-03-01", "DAILY");

    assertNotNull(quest);
    assertEquals(QUEST_ID, quest.getId());
    verify(ruleService).createRule(any(RuleDTO.class), eq(USERNAME));
  }

  @Test
  public void createQuestAutomaticRejected() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    assertThrows(IllegalStateException.class,
                 () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 50, "AUTOMATIC", null, null, null));
  }

  @Test
  public void createQuestNotOwnerFails() throws Exception {
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));
    when(programService.isProgramOwner(CAMPAIGN_ID, USERNAME)).thenReturn(false);
    when(programService.canEditProgram(CAMPAIGN_ID, USERNAME)).thenReturn(false);
    assertThrows(IllegalStateException.class, () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 50, null, null, null, null));
  }

  @Test
  public void createQuestBlankTitleFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.createQuest(CAMPAIGN_ID, " ", null, 50, null, null, null, null));
  }

  @Test
  public void createQuestInvalidScoreFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 0, null, null, null, null));
    assertThrows(IllegalArgumentException.class, () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, null, null, null, null, null));
  }

  @Test
  public void createQuestMissingCampaignFails() {
    assertThrows(IllegalArgumentException.class, () -> tool.createQuest(null, "Do it", null, 50, null, null, null, null));
  }

  @Test
  public void createQuestDeniedByServiceFails() throws Exception {
    ProgramDTO program = program(CAMPAIGN_ID);
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program);
    when(programService.isProgramOwner(CAMPAIGN_ID, USERNAME)).thenReturn(true);
    when(ruleService.createRule(any(RuleDTO.class), eq(USERNAME))).thenThrow(new IllegalAccessException("denied"));
    assertThrows(IllegalStateException.class, () -> tool.createQuest(CAMPAIGN_ID, "Do it", null, 50, null, null, null, null));
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
    assertThrows(IllegalStateException.class, () -> tool.updateQuest(QUEST_ID, "Renamed", null, null, null, null, null, null));
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
    assertThrows(IllegalStateException.class, () -> tool.updateQuest(QUEST_ID, "Renamed", null, null, null, null, null, null));
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
    assertThrows(IllegalStateException.class, () -> tool.deleteQuest(QUEST_ID));
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
    when(announcementService.createAnnouncement(any(Announcement.class), any(), eq(USERNAME))).thenReturn(created);

    AnnouncementModel result = tool.announceQuest(QUEST_ID, "done");

    assertEquals(42L, result.getId());
    assertEquals(QUEST_ID, result.getQuestId().longValue());
    assertEquals(Long.valueOf(100L), result.getActivityId());
  }

  @Test
  public void announceQuestNotChallengeFails() throws Exception {
    when(announcementService.createAnnouncement(any(Announcement.class), any(), eq(USERNAME)))
        .thenThrow(new IllegalStateException("Rule with id '7' isn't a challenge"));
    assertThrows(IllegalStateException.class, () -> tool.announceQuest(QUEST_ID, "done"));
  }

  @Test
  public void announceQuestNotAllowedFails() throws Exception {
    when(announcementService.createAnnouncement(any(Announcement.class), any(), eq(USERNAME)))
        .thenThrow(new IllegalAccessException("not allowed"));
    assertThrows(IllegalStateException.class, () -> tool.announceQuest(QUEST_ID, "done"));
  }

  @Test
  public void announceQuestNotFoundFails() throws Exception {
    when(announcementService.createAnnouncement(any(Announcement.class), any(), eq(USERNAME)))
        .thenThrow(new ObjectNotFoundException("missing"));
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
    assertThrows(IllegalStateException.class, () -> tool.cancelQuestAnnouncement(42L));
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
  public void getMyCampaignsUnionsMemberAndOwned() throws Exception {
    stubCurrentUserIdentity();
    when(programService.getMemberProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(5L, 6L));
    when(programService.getOwnedProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(6L, 7L));
    when(programService.getProgramById(5L, USERNAME)).thenReturn(program(5L));
    when(programService.getProgramById(6L, USERNAME)).thenReturn(program(6L));
    when(programService.getProgramById(7L, USERNAME)).thenReturn(program(7L));

    List<CampaignModel> campaigns = tool.getMyCampaigns(null, null);

    // 5, 6, 7 deduped (6 appears in both lists)
    assertEquals(3, campaigns.size());
  }

  @Test
  public void getMyCampaignsSkipsUnviewable() throws Exception {
    stubCurrentUserIdentity();
    when(programService.getMemberProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(5L, 8L));
    when(programService.getOwnedProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of());
    when(programService.getProgramById(5L, USERNAME)).thenReturn(program(5L));
    when(programService.getProgramById(8L, USERNAME)).thenThrow(new IllegalAccessException("denied"));

    List<CampaignModel> campaigns = tool.getMyCampaigns(10, 0);

    assertEquals(1, campaigns.size());
    assertEquals(5L, campaigns.get(0).getId());
  }

  @Test
  public void getMyCampaignsEmpty() {
    stubCurrentUserIdentity();
    when(programService.getMemberProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(null);
    when(programService.getOwnedProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(null);

    List<CampaignModel> campaigns = tool.getMyCampaigns(null, null);

    assertTrue(campaigns.isEmpty());
  }

  @Test
  public void getMyCampaignsPaginatesOnceOverTheDedupedUnion() throws Exception {
    stubCurrentUserIdentity();
    // member ∪ owned = {1,2,3,4,5,6} (4 is in both), 6 distinct ids >= limit.
    when(programService.getMemberProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(1L, 2L, 3L, 4L));
    when(programService.getOwnedProgramIds(eq(USERNAME), anyInt(), anyInt())).thenReturn(List.of(4L, 5L, 6L));
    for (long id = 1; id <= 6; id++) {
      when(programService.getProgramById(id, USERNAME)).thenReturn(program(id));
    }

    // First page: exactly `limit` items, no duplicates, sorted union order.
    List<CampaignModel> page1 = tool.getMyCampaigns(3, 0);
    assertEquals("must return exactly limit items over the union, not up to 2x limit", 3, page1.size());
    List<Long> page1Ids = page1.stream().map(CampaignModel::getId).toList();
    assertEquals(List.of(1L, 2L, 3L), page1Ids);
    assertEquals("no duplicates", 3, page1Ids.stream().distinct().count());

    // Second page: offset applied ONCE over the union, no overlap with page 1.
    List<CampaignModel> page2 = tool.getMyCampaigns(3, 3);
    List<Long> page2Ids = page2.stream().map(CampaignModel::getId).toList();
    assertEquals(3, page2.size());
    assertEquals(List.of(4L, 5L, 6L), page2Ids);
    assertTrue("pages must not overlap", page1Ids.stream().noneMatch(page2Ids::contains));
  }

  // --- list_joinable_campaigns ---------------------------------------------

  @Test
  public void listJoinableCampaigns() throws Exception {
    when(programService.getPublicProgramIds(anyInt(), anyInt())).thenReturn(List.of(5L, 6L));
    when(programService.getProgramById(5L, USERNAME)).thenReturn(program(5L));
    when(programService.getProgramById(6L, USERNAME)).thenReturn(program(6L));

    List<CampaignModel> campaigns = tool.listJoinableCampaigns(null, null);

    assertEquals(2, campaigns.size());
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

    PointsTotalModel total = tool.getMyPointsTotal("2026-01-01", "2026-02-01", CAMPAIGN_ID);

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

    PointsTotalModel total = tool.getMyPointsTotal(null, null, null);

    assertEquals(500L, total.getTotalPoints());
    assertEquals(null, total.getCampaignId());
  }

  @Test
  public void getMyPointsTotalInvalidDateFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.getMyPointsTotal("not-a-date", null, null));
  }

  // --- get_my_score_breakdown ----------------------------------------------

  @Test
  public void getMyScoreBreakdown() throws Exception {
    stubCurrentUserIdentity();
    PiechartLeaderboard slice = new PiechartLeaderboard(CAMPAIGN_ID, 80L);
    when(realizationService.getLeaderboardStatsByIdentityId(eq(String.valueOf(OWN_ID)), isNull(), eq("MONTH"), any(), any()))
        .thenReturn(List.of(slice));
    when(programService.getProgramById(CAMPAIGN_ID, USERNAME)).thenReturn(program(CAMPAIGN_ID));

    List<ScoreBreakdownEntryModel> breakdown = tool.getMyScoreBreakdown("MONTH");

    assertEquals(1, breakdown.size());
    assertEquals(CAMPAIGN_ID, breakdown.get(0).getCampaignId());
    assertEquals(80L, breakdown.get(0).getPoints());
    assertEquals("Campaign 5", breakdown.get(0).getLabel());
  }

  @Test
  public void getMyScoreBreakdownDefaultsToAll() {
    stubCurrentUserIdentity();
    when(realizationService.getLeaderboardStatsByIdentityId(eq(String.valueOf(OWN_ID)), isNull(), eq("ALL"), isNull(), isNull()))
        .thenReturn(List.of());

    List<ScoreBreakdownEntryModel> breakdown = tool.getMyScoreBreakdown(null);

    assertTrue(breakdown.isEmpty());
  }

  @Test
  public void getMyScoreBreakdownInvalidPeriodFails() {
    stubCurrentUserIdentity();
    assertThrows(IllegalArgumentException.class, () -> tool.getMyScoreBreakdown("DECADE"));
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

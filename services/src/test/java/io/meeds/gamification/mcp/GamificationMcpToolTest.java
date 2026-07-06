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

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.mcp.model.AnnouncementModel;
import io.meeds.gamification.mcp.model.CampaignModel;
import io.meeds.gamification.mcp.model.LeaderboardEntryModel;
import io.meeds.gamification.mcp.model.QuestModel;
import io.meeds.gamification.mcp.model.RealizationModel;
import io.meeds.gamification.mcp.model.ScoreModel;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProfileReputation;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.service.AnnouncementService;
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

  private IdentityManager     identityManager;

  private Identity            currentIdentity;

  private GamificationMcpTool tool;

  @Before
  public void setUp() {
    programService = mock(ProgramService.class);
    ruleService = mock(RuleService.class);
    realizationService = mock(RealizationService.class);
    announcementService = mock(AnnouncementService.class);
    identityManager = mock(IdentityManager.class);
    currentIdentity = new Identity(USERNAME);
    tool = new GamificationMcpTool(programService,
                                   ruleService,
                                   realizationService,
                                   announcementService,
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

}

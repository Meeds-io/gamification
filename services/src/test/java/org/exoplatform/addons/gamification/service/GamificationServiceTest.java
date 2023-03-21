/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.exoplatform.addons.gamification.listener.social.activity.GamificationActivityListener;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter.Period;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

@RunWith(MockitoJUnitRunner.class)
public class GamificationServiceTest extends AbstractServiceTest {

  @Test
  public void testBuildHistory() {
    // root11 is not a member of domain audience
    RuleDTO newRuleDTO = newRuleDTO();
    GamificationActionsHistory aHistory = gamificationService.build(newRuleDTO,
                                                                    "11",
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    assertNull(aHistory);

    // case of deleted domain
    DomainDTO domainDTO = newDomainDTO();
    domainDTO.setDeleted(true);
    newRuleDTO.setDomainDTO(domainDTO);
    aHistory = gamificationService.build(newRuleDTO,
                                         TEST_USER_SENDER,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    assertNull(aHistory);

    newRuleDTO = newRuleDTO();
    aHistory = gamificationService.build(newRuleDTO,
                                         TEST_USER_SENDER,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    assertNotNull(aHistory);
  }

  @Test
  public void testSaveActionHistory() {
    List<GamificationActionsHistory> histories = gamificationHistoryDAO.findAll();
    assertEquals(histories.size(), 0);
    RuleDTO newRuleDTO = newRuleDTO();

    GamificationActionsHistory aHistory = gamificationService.build(newRuleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    histories = gamificationHistoryDAO.findAll();
    assertEquals(histories.size(), 1);

    GamificationActionsHistory gamificationActionsHistory = histories.get(0);
    assertEquals(TEST_USER_SENDER, gamificationActionsHistory.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, gamificationActionsHistory.getReceiver());
    assertEquals(ACTIVITY_ID, gamificationActionsHistory.getObjectId());
    assertEquals(IdentityType.USER, gamificationActionsHistory.getEarnerType());

    aHistory = gamificationService.build(newRuleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    histories = gamificationHistoryDAO.findAll();
    assertEquals(histories.size(), 2);

    gamificationActionsHistory = histories.get(1);
    assertEquals(TEST_SPACE_ID, gamificationActionsHistory.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, gamificationActionsHistory.getReceiver());
    assertEquals(ACTIVITY_ID, gamificationActionsHistory.getObjectId());
    assertEquals(IdentityType.SPACE, gamificationActionsHistory.getEarnerType());
  }

  @Test
  public void testComputeTotalScore() {
    RuleDTO ruleDTO = newRuleDTO();
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    GamificationActionsHistory aHistory1 = gamificationService.build(ruleDTO,
                                                                     TEST_USER_SENDER,
                                                                     TEST_USER_RECEIVER,
                                                                     ACTIVITY_ID,
                                                                     GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory1);
    long global = gamificationHistoryDAO.getTotalScore(TEST_USER_SENDER);
    assertEquals(global, aHistory.getActionScore() + aHistory1.getActionScore());
  }

  @Test
  public void testLeaderboardRank() {
    DomainDTO domainDTO = newDomainDTO();
    RuleDTO ruleDTO = newRuleDTO(RULE_NAME, domainDTO.getId());
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_USER_SENDER,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_USER_RECEIVER,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);

    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);

    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    Date date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());

    int rankUser1 = gamificationService.getLeaderboardRank(TEST_USER_SENDER, date, domainDTO.getId());
    int rankUser2 = gamificationService.getLeaderboardRank(TEST_USER_RECEIVER, date, domainDTO.getId());
    assertEquals(1, rankUser1);
    assertEquals(2, rankUser2);

    int rankSpace2 = gamificationService.getLeaderboardRank(TEST_SPACE2_ID, date, domainDTO.getId());
    int rankSpace1 = gamificationService.getLeaderboardRank(TEST_SPACE_ID, date, domainDTO.getId());
    assertEquals(1, rankSpace2);
    assertEquals(2, rankSpace1);
  }

  @Test
  public void testFindLatestActionHistoryBySocialId() {
    RuleDTO ruleDTO = newRuleDTO();
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    GamificationActionsHistory aHistory1 = gamificationService.build(ruleDTO,
                                                                     TEST_USER_SENDER,
                                                                     TEST_USER_RECEIVER,
                                                                     ACTIVITY_ID,
                                                                     GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory1);
    GamificationActionsHistory aHistory2 = gamificationService.build(ruleDTO,
                                                                     TEST_USER_SENDER,
                                                                     TEST_USER_RECEIVER,
                                                                     ACTIVITY_ID,
                                                                     GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory2);

    GamificationActionsHistory history = gamificationService.findLatestActionHistoryByEarnerId(TEST_USER_SENDER);
    compareHistory(aHistory2, history);
  }

  @Test
  public void testFindUserReputationBySocialId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(gamificationService.findReputationByEarnerId(TEST_USER_SENDER), 0);
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);

    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         ACTIVITY_ID,
                                         GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);

    assertTrue(gamificationService.findReputationByEarnerId(TEST_USER_SENDER) > 0);
    assertTrue(gamificationService.findReputationByEarnerId(TEST_SPACE_ID) > 0);
  }

  @Test
  public void testBuildDomainScoreByUserId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(gamificationService.buildDomainScoreByIdentityId(TEST_USER_SENDER).size(), 0);
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    assertEquals(1, gamificationService.buildDomainScoreByIdentityId(TEST_USER_SENDER).size());
  }

  @Test
  public void testFilterByDomainId() {
    RuleDTO ruleDTO = newRuleDTO();
    LeaderboardFilter filter = new LeaderboardFilter();
    filter.setPeriod(Period.ALL.name());
    filter.setIdentityType(IdentityType.USER);
    filter.setLoadCapacity(limit);
    filter.setDomainId(ruleDTO.getDomainDTO().getId());
    List<StandardLeaderboard> filteredLeaderboard = gamificationService.filter(filter);
    assertEquals(0, filteredLeaderboard.size());

    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    ACTIVITY_ID,
                                                                    GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);

    filteredLeaderboard = gamificationService.filter(filter);
    assertEquals(1, filteredLeaderboard.size());
    StandardLeaderboard userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_SENDER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.WEEK.name());
    filteredLeaderboard = gamificationService.filter(filter);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_SENDER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.MONTH.name());
    filteredLeaderboard = gamificationService.filter(filter);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_SENDER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setIdentityType(IdentityType.SPACE);
    filteredLeaderboard = gamificationService.filter(filter);
    assertEquals(0, filteredLeaderboard.size());
  }

  @Test
  public void testDeleteHistory() {
    DomainDTO domainDTO = newDomainDTO();
    RuleDTO ruleDTO = newRuleDTO(RULE_NAME, domainDTO.getId());
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
            TEST_USER_SENDER,
            TEST_USER_RECEIVER,
            ACTIVITY_ID,
            GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
            TEST_USER_RECEIVER,
            TEST_USER_SENDER,
            ACTIVITY_ID,
            GamificationActivityListener.OBJECT_TYPE_VALUE);
    gamificationService.saveActionHistory(aHistory);

    GamificationActionsHistory gamificationActionsHistory =
            gamificationService.findLatestActionHistoryByEarnerId(TEST_USER_SENDER);
    assertEquals(HistoryStatus.ACCEPTED, gamificationActionsHistory.getStatus());

    gamificationActionsHistory = gamificationService.findLatestActionHistoryByEarnerId(TEST_USER_RECEIVER);
    assertEquals(HistoryStatus.ACCEPTED, gamificationActionsHistory.getStatus());

    gamificationService.deleteHistory(ACTIVITY_ID, GamificationActivityListener.OBJECT_TYPE_VALUE);

    gamificationActionsHistory = gamificationService.findLatestActionHistoryByEarnerId(TEST_USER_SENDER);
    assertEquals(HistoryStatus.DELETED, gamificationActionsHistory.getStatus());

    gamificationActionsHistory = gamificationService.findLatestActionHistoryByEarnerId(TEST_USER_RECEIVER);
    assertEquals(HistoryStatus.DELETED, gamificationActionsHistory.getStatus());
  }
}

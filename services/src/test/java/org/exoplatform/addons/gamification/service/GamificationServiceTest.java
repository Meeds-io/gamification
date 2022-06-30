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

import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class GamificationServiceTest extends AbstractServiceTest {

  @Test
  public void testSaveActionHistory() {
    List<GamificationActionsHistory> histories = gamificationHistoryDAO.findAll();
    assertEquals(histories.size(), 0);
    RuleDTO newRuleDTO = newRuleDTO();

    GamificationActionsHistory aHistory = gamificationService.build(newRuleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    histories = gamificationHistoryDAO.findAll();
    assertEquals(histories.size(), 1);

    GamificationActionsHistory gamificationActionsHistory = histories.get(0);
    assertEquals(TEST_USER_SENDER, gamificationActionsHistory.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, gamificationActionsHistory.getReceiver());
    assertEquals(TEST_LINK_ACTIVITY, gamificationActionsHistory.getObjectId());
    assertEquals(IdentityType.USER, gamificationActionsHistory.getEarnerType());

    aHistory = gamificationService.build(newRuleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    histories = gamificationHistoryDAO.findAll();
    assertEquals(histories.size(), 2);

    gamificationActionsHistory = histories.get(1);
    assertEquals(TEST_SPACE_ID, gamificationActionsHistory.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, gamificationActionsHistory.getReceiver());
    assertEquals(TEST_LINK_ACTIVITY, gamificationActionsHistory.getObjectId());
    assertEquals(IdentityType.SPACE, gamificationActionsHistory.getEarnerType());
  }

  @Test
  public void testComputeTotalScore() {
    RuleDTO ruleDTO = newRuleDTO();
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    GamificationActionsHistory aHistory1 = gamificationService.build(ruleDTO,
                                                                     TEST_USER_SENDER,
                                                                     TEST_USER_RECEIVER,
                                                                     TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory1);
    long global = gamificationHistoryDAO.getTotalScore(TEST_USER_SENDER);
    assertEquals(global, aHistory.getActionScore() + aHistory1.getActionScore());
  }

  public void testLeaderboardRank() {
    RuleDTO ruleDTO = newRuleDTO();
    GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                    TEST_USER_SENDER,
                                                                    TEST_USER_RECEIVER,
                                                                    TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_USER_SENDER,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_USER_RECEIVER,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);

    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);

    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);
    aHistory = gamificationService.build(ruleDTO,
                                         TEST_SPACE2_ID,
                                         TEST_USER_RECEIVER,
                                         TEST_LINK_ACTIVITY);
    gamificationService.saveActionHistory(aHistory);

    int rankUser1 = gamificationService.getLeaderboardRank(TEST_USER_SENDER, new Date(), GAMIFICATION_DOMAIN);
    int rankUser2 = gamificationService.getLeaderboardRank(TEST_USER_RECEIVER, new Date(), GAMIFICATION_DOMAIN);
    assertEquals(rankUser1, 1);
    assertEquals(rankUser2, 2);

    int rankSpace2 = gamificationService.getLeaderboardRank(TEST_SPACE2_ID, new Date(), GAMIFICATION_DOMAIN);
    int rankSpace1 = gamificationService.getLeaderboardRank(TEST_SPACE_ID, new Date(), GAMIFICATION_DOMAIN);
    assertEquals(rankSpace2, 1);
    assertEquals(rankSpace1, 2);
  }

  public void testFindLatestActionHistoryBySocialId() {

    try {
      RuleDTO ruleDTO = newRuleDTO();
      GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                      TEST_USER_SENDER,
                                                                      TEST_USER_RECEIVER,
                                                                      TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory);
      GamificationActionsHistory aHistory1 = gamificationService.build(ruleDTO,
                                                                       TEST_USER_SENDER,
                                                                       TEST_USER_RECEIVER,
                                                                       TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory1);
      GamificationActionsHistory aHistory2 = gamificationService.build(ruleDTO,
                                                                       TEST_USER_SENDER,
                                                                       TEST_USER_RECEIVER,
                                                                       TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory2);

      GamificationActionsHistory history = gamificationService.findLatestActionHistoryByEarnerId(TEST_USER_SENDER);
      compareHistory(aHistory2, history);
    } catch (Exception e) {
      fail("Error to find last actionHistory entry ", e);
    }
  }

  public void testFindUserReputationBySocialId() {
    try {
      RuleDTO ruleDTO = newRuleDTO();
      assertEquals(gamificationService.findReputationByEarnerId(TEST_USER_SENDER), 0);
      GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                      TEST_USER_SENDER,
                                                                      TEST_USER_RECEIVER,
                                                                      TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory);

      aHistory = gamificationService.build(ruleDTO,
                                           TEST_SPACE_ID,
                                           TEST_USER_RECEIVER,
                                           TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory);

      assertTrue(gamificationService.findReputationByEarnerId(TEST_USER_SENDER) > 0);
      assertTrue(gamificationService.findReputationByEarnerId(TEST_SPACE_ID) > 0);
    } catch (Exception e) {
      fail("error on find user reputation by SocialId", e);
    }

  }

  public void testBuildDomainScoreByUserId() {
    try {
      RuleDTO ruleDTO = newRuleDTO();
      assertEquals(gamificationService.buildDomainScoreByIdentityId(TEST_USER_SENDER).size(), 0);
      GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                      TEST_USER_SENDER,
                                                                      TEST_USER_RECEIVER,
                                                                      TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory);
      assertFalse(gamificationService.buildDomainScoreByIdentityId(TEST_USER_SENDER).isEmpty());
    } catch (Exception e) {
      fail("Error to Build Domain Score By UserId ", e);
    }
  }

}

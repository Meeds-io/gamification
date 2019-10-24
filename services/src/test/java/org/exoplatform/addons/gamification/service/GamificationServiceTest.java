package org.exoplatform.addons.gamification.service;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class GamificationServiceTest extends AbstractServiceTest {

  private static final Log LOG = ExoLogger.getLogger(GamificationService.class);

  @Test
  public void testSaveActionHistory() {
    try {
      List<GamificationActionsHistory> histories = gamificationHistoryDAO.findAll();
      assertEquals(histories.size(), 0);
      GamificationActionsHistory aHistory = gamificationService.build(newRuleDTO(),
                                                                      TEST_USER_SENDER,
                                                                      TEST_USER_RECEIVER,
                                                                      TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory);
      histories = gamificationHistoryDAO.findAll();
      assertEquals(histories.size(), 1);
    } catch (Exception e) {
      fail("Error to save the following GamificationActionsHistory entry", e);
    }
  }

  @Test
  public void testComputeTotalScore() throws Exception {
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
      long global = gamificationHistoryDAO.computeTotalScore(TEST_USER_SENDER);
      assertEquals(global, aHistory.getActionScore() + aHistory1.getActionScore());
    } catch (Exception e) {
      fail("Error in user total score {}", e);
    }
  }

  public void testBluidCurrentUserRank() throws Exception {

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
                                                                       TEST_USER_RECEIVER,
                                                                       TEST_USER_RECEIVER,
                                                                       TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory2);
      int rankUser1 = gamificationService.bluidCurrentUserRank(TEST_USER_SENDER, new Date(), GAMIFICATION_DOMAIN);
      int rankUser2 = gamificationService.bluidCurrentUserRank(TEST_USER_RECEIVER, new Date(), GAMIFICATION_DOMAIN);
      assertEquals(rankUser1, 1);
      assertEquals(rankUser2, 2);
    } catch (Exception e) {
      fail("Error in test Build Current User Rank {}", e);
    }

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
      GamificationActionsHistory history = gamificationService.findLatestActionHistoryBySocialId(TEST_USER_SENDER);
      compareHistory(aHistory2, history);
    } catch (Exception e) {
      fail("Error to find last actionHistory entry ", e);
    }

  }

  public void testFindUserReputationBySocialId() {

    try {
      RuleDTO ruleDTO = newRuleDTO();
      assertEquals(gamificationService.findUserReputationBySocialId(TEST_USER_SENDER), 0);
      GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                      TEST_USER_SENDER,
                                                                      TEST_USER_RECEIVER,
                                                                      TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory);
      assertTrue(gamificationService.findUserReputationBySocialId(TEST_USER_SENDER) > 0);
    } catch (Exception e) {
      fail("error on find user reputation by SocialId", e);
    }

  }

  public void testBuildDomainScoreByUserId() {

    try {
      RuleDTO ruleDTO = newRuleDTO();
      assertEquals(gamificationService.buildDomainScoreByUserId(TEST_USER_SENDER).size(), 0);
      GamificationActionsHistory aHistory = gamificationService.build(ruleDTO,
                                                                      TEST_USER_SENDER,
                                                                      TEST_USER_RECEIVER,
                                                                      TEST_LINK_ACTIVITY);
      gamificationService.saveActionHistory(aHistory);
      assertTrue(gamificationService.buildDomainScoreByUserId(TEST_USER_SENDER).size() > 0);
    } catch (Exception e) {
      fail("Error to Build Domain Score By UserId ", e);
    }

  }

}

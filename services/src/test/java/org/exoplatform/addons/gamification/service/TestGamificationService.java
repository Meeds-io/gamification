package org.exoplatform.addons.gamification.service;

import java.util.Date;
import java.util.List;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;

public class TestGamificationService extends AbstractServiceTest {
  private static final String    GAMIFICATION_DOMAIN = "TeamWork";

  private static final String    RULE_NAME           = "Update a new Task";

  /* Receiver */
  private static final String    TEST_USER_RECEIVER  = "55";

  /* Sender */
  private static final String    TEST_USER_SENDER    = "1";

  /* Link to the activity stream */
  private static final String    TEST_LINK_ACTIVITY  = "/portal/intranet//activity?id=245590";

  private static final String    TEST_GLOBAL_SCORE   = "245590";

  private static final Log       LOG                 = ExoLogger.getLogger(GamificationService.class);

  // private Identity actorIdentity =
  // identityManager.getIdentity(TEST_USER_SENDER, false);
  // Needed for exchange points between users
  Identity                       testUserReceiver    = new Identity(TEST_USER_RECEIVER);

  Identity                       testUserSender      = new Identity(TEST_USER_SENDER);

  private GamificationHistoryDAO gamificationHistoryDAO;

  private RuleDTO                ruleDTO             = new RuleDTO();

  public GamificationActionsHistory build(RuleDTO ruleDto,
                                          String testUserSender,
                                          String testUserReceiver,
                                          String TEST_LINK_ACTIVITY) throws Exception {

    GamificationActionsHistory aHistory = null;
    try {
      // Build only an entry when a rule enable and exist
      if (ruleDto != null) {
        aHistory = new GamificationActionsHistory();
        aHistory.setActionScore(ruleDto.getScore());
        try {
          aHistory.setGlobalScore(TestcomputeTotalScore(testUserSender) + ruleDto.getScore());
        } catch (Exception e) {
          e.printStackTrace();
        }
        aHistory.setDate(new Date());
        aHistory.setUserSocialId(testUserSender);

        aHistory.setActionTitle(RULE_NAME);
        aHistory.setDomain(GAMIFICATION_DOMAIN);
        aHistory.setReceiver(testUserReceiver);
        aHistory.setObjectId(TEST_LINK_ACTIVITY);
        // Set update metadata
        aHistory.setLastModifiedDate(new Date());
        aHistory.setLastModifiedBy("Gamification Inner Process");
        // Set create metadata
        aHistory.setCreatedBy("Gamification Inner Process");
      }
    } catch (Exception e) {
      LOG.error("Error to run GamificationActionsHistory build {}", aHistory, e);
    }
    return aHistory;
  }

  public void testSaveActionHistory() {
    GamificationActionsHistory aHistory = null;

    try {
      if (aHistory != null) {

        assertNotNull(aHistory);
        gamificationService.build(ruleDTO, TEST_USER_SENDER, TEST_USER_RECEIVER, TEST_LINK_ACTIVITY);
        gamificationService.saveActionHistory(aHistory);

      }

    } catch (Exception e) {
      LOG.error("Error to save the following GamificationActionsHistory entry {}", aHistory, e);
    }

  }

  // Needed for the computed score

  public long TestcomputeTotalScore(String testUserSender) throws Exception {
    try {
      gamificationHistoryDAO.computeTotalScore(testUserSender);
    } catch (Exception e) {
      LOG.error("Error in user total score {}", testUserSender, e);
    }
    return gamificationHistoryDAO.computeTotalScore(testUserSender);

  }

  public void testAddActivityOnUserStream() throws Exception {

    try {
      Identity maryIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "mary", false);
    } catch (Exception e) {
      LOG.error("Error in add activity {}", e);
    }

  }

  public GamificationActionsHistory FindLatestActionHistoryBySocialId(String TEST_USER_SENDER) throws Exception {
    List<GamificationActionsHistory> aHistory = null;

    try {

      aHistory = gamificationHistoryDAO.findActionsHistoryByUserId(TEST_USER_SENDER);
      return (aHistory != null && !aHistory.isEmpty()) ? aHistory.get(0) : null;

    } catch (Exception e) {
      fail("Error to List an actionHistory associated to userId ", e);
    }
    return null;

  }

  public void testFindLatestActionHistoryBySocialId() {
    List<GamificationActionsHistory> aHistory = null;

    try {

      if (aHistory != null) {
        gamificationService.findLatestActionHistoryBySocialId(TEST_USER_RECEIVER);
        gamificationService.findLatestActionHistoryBySocialId(TEST_USER_SENDER);

        assertTrue((aHistory.size() == 1));
        GamificationActionsHistory result = aHistory.iterator().next();
        assertEquals(TEST_USER_SENDER, result.getUserSocialId());
        assertEquals("updateTask", result.getActionTitle());
        assertEquals("TeamWork", result.getDomain());
        assertEquals("/portal/intranet/activity?id=245590", result.getObjectId());
        assertEquals(TEST_GLOBAL_SCORE, result.getGlobalScore());
        assertEquals(TEST_USER_RECEIVER, result.getReceiver());

      }

    } catch (Exception e) {
      fail("Error to save an actionHistory entry ", e);
    }

  }

  public void testbuild(RuleDTO ruleDto,
                        String testUserSender,
                        String testUserReceiver,
                        String TEST_LINK_ACTIVITY) throws Exception {

    GamificationActionsHistory aHistory = null;
    try {
      // Build only an entry when a rule enable and exist
      if (ruleDto != null) {
        aHistory = new GamificationActionsHistory();
        aHistory.setActionScore(ruleDto.getScore());
        try {
          aHistory.setGlobalScore(TestcomputeTotalScore(testUserSender) + ruleDto.getScore());
        } catch (Exception e) {
          e.printStackTrace();
        }
        aHistory.setDate(new Date());
        aHistory.setUserSocialId(testUserSender);

        aHistory.setActionTitle(RULE_NAME);
        aHistory.setDomain(GAMIFICATION_DOMAIN);
        aHistory.setReceiver(testUserReceiver);
        aHistory.setObjectId(TEST_LINK_ACTIVITY);
        // Set update metadata
        aHistory.setLastModifiedDate(new Date());
        aHistory.setLastModifiedBy("Gamification Inner Process");
        // Set create metadata
        aHistory.setCreatedBy("Gamification Inner Process");
      }
    } catch (Exception e) {
      fail("Error to test an actionHistory entry ", e);
    }

  }

}

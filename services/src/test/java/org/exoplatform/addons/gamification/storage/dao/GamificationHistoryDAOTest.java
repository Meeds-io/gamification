package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.junit.Test;

public class GamificationHistoryDAOTest extends AbstractServiceTest {

  @Test
  public void testFindAllActionsHistoryAgnostic() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryAgnostic(IdentityType.USER).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryAgnostic(IdentityType.USER).size(), limit);
  }

  @Test
  public void testFindAllActionsHistoryByDateByDomain() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDateByDomain(IdentityType.USER, fromDate, GAMIFICATION_DOMAIN)
                                       .size(),
                 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDateByDomain(IdentityType.USER, fromDate, GAMIFICATION_DOMAIN)
                                       .size(),
                 limit);
  }

  @Test
  public void testFindAllActionsHistoryByDomain() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDomain(IdentityType.USER, GAMIFICATION_DOMAIN).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDomain(IdentityType.USER, GAMIFICATION_DOMAIN).size(), limit);
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDomain(GAMIFICATION_DOMAIN, IdentityType.USER, 2).size(), 2);
  }

  @Test
  public void testFindAllActionsHistoryByDate() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDate(IdentityType.USER, fromDate).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDate(IdentityType.USER, fromDate).size(), limit);
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDate(fromDate, IdentityType.USER, 2).size(), 2);
  }

  @Test
  public void testFindActionsHistoryByEarnerId() {
    assertEquals(gamificationHistoryDAO.findActionsHistoryByEarnerId(TEST_USER_SENDER, limit).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findActionsHistoryByEarnerId(TEST_USER_SENDER, limit).size(), limit);
  }

  @Test
  public void testFindAllActionsHistory() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistory(IdentityType.USER, limit).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findAllActionsHistory(IdentityType.USER, limit).size(), limit);
  }

  @Test
  public void testFindActionHistoryByDateByEarnerId() {
    assertEquals(gamificationHistoryDAO.findActionHistoryByDateByEarnerId(fromDate, TEST_USER_SENDER).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findActionHistoryByDateByEarnerId(fromDate, TEST_USER_SENDER).size(), limit);
  }

  @Test
  public void testFindActionsHistoryByDateByDomain() {
    assertEquals(gamificationHistoryDAO.findActionsHistoryByDateByDomain(fromDate, IdentityType.USER, GAMIFICATION_DOMAIN, limit)
                                       .size(),
                 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findActionsHistoryByDateByDomain(fromDate, IdentityType.USER, GAMIFICATION_DOMAIN, limit)
                                       .size(),
                 limit);
  }

  @Test
  public void testFindStatsByUserId() {
    assertEquals(gamificationHistoryDAO.findStatsByUserId(TEST_USER_SENDER, fromDate,toDate) .size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findStatsByUserId(TEST_USER_SENDER, fromDate,toDate) .size(), limit);
  }

  @Test
  public void testGetAllPointsByDomain() {
    assertEquals(gamificationHistoryDAO.getAllPointsByDomain(GAMIFICATION_DOMAIN) .size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getAllPointsByDomain(GAMIFICATION_DOMAIN) .size(), limit);
  }

  @Test
  public void testGetTotalScore() {
    assertEquals(gamificationHistoryDAO.getTotalScore(TEST_USER_SENDER), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getTotalScore(TEST_USER_SENDER), Integer.parseInt(TEST__SCORE)*3);
  }

  @Test
  public void testGetDomainList() {
    assertEquals(gamificationHistoryDAO.getDomainList().size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getDomainList().size(), 1);
  }

  @Test
  public void testFindAllAnnouncementByChallenge() {
    GamificationActionsHistoryDTO ghistory = newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
    assertEquals(gamificationHistoryDAO.findAllAnnouncementByChallenge(ghistory.getRuleId(),offset,limit).size(), limit);
  }

  @Test
  public void testGetAllRealizationsByDate() {
    assertEquals(gamificationHistoryDAO.getAllRealizationsByDate(fromDate,toDate,offset,limit).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getAllRealizationsByDate(fromDate,toDate,offset,limit).size(), limit);
  }
}

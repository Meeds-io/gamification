package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.junit.Test;


public class RealizationsStorageTest extends AbstractServiceTest {


  @Test
  public void testGetAllRealizationsByDate() {
    assertEquals(realizationsStorage.getAllRealizationsByDate(fromDate, toDate, offset, limit).size(),0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(realizationsStorage.getAllRealizationsByDate(fromDate, toDate, offset, limit).size(), 3);
  }

  @Test
  public void testGetRealizationById() {
    assertEquals(realizationsStorage.getAllRealizationsByDate(fromDate, toDate, offset, limit).size(),0);
    GamificationActionsHistoryDTO gHistory = newGamificationActionsHistoryDTO();
    GamificationActionsHistoryDTO newGHistory = realizationsStorage.getRealizationById(gHistory.getId());
    assertNotNull(newGHistory);
    assertEquals(gHistory.getActionTitle(), newGHistory.getActionTitle());
  }

  @Test
  public void testUpdateRealizationStatus() {
    assertEquals(realizationsStorage.getAllRealizationsByDate(fromDate, toDate, offset, limit).size(),0);
    GamificationActionsHistoryDTO gHistory = newGamificationActionsHistoryDTO();
    assertEquals(gHistory.getStatus(), HistoryStatus.ACCEPTED.name());
    gHistory.setStatus(HistoryStatus.REJECTED.name());
    GamificationActionsHistoryDTO rejectedGHistory = realizationsStorage.updateRealizationStatus(gHistory);
    assertEquals(rejectedGHistory.getStatus(), HistoryStatus.REJECTED.name());

  }
}

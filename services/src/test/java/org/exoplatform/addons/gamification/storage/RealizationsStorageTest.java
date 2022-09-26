/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import org.junit.Test;

public class RealizationsStorageTest extends AbstractServiceTest {

  @Test
  public void testFindAllRealizationsByFilter() {
    RealizationsFilter filter = new RealizationsFilter();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerId(0);
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, offset, limit).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, offset, limit).size(), 3);
  }
  
  @Test
  public void testFindUsersRealizationsByFilter() {
    RealizationsFilter filter = new RealizationsFilter();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerId(1L);
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, offset, limit).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, offset, limit).size(), 3);
  }

  @Test
  public void testGetRealizationById() {
    RealizationsFilter filter = new RealizationsFilter();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerId(0);
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, offset, limit).size(), 0);
    GamificationActionsHistoryDTO gHistory = newGamificationActionsHistoryDTO();
    GamificationActionsHistoryDTO newGHistory = realizationsStorage.getRealizationById(gHistory.getId());
    assertNotNull(newGHistory);
    assertEquals(gHistory.getActionTitle(), newGHistory.getActionTitle());
  }

  @Test
  public void testUpdateRealizationStatus() {
    RealizationsFilter filter = new RealizationsFilter();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerId(0);
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, offset, limit).size(), 0);
    GamificationActionsHistoryDTO gHistory = newGamificationActionsHistoryDTO();
    assertEquals(gHistory.getStatus(), HistoryStatus.ACCEPTED.name());
    gHistory.setStatus(HistoryStatus.REJECTED.name());
    GamificationActionsHistoryDTO rejectedGHistory = realizationsStorage.updateRealizationStatus(gHistory);
    assertEquals(rejectedGHistory.getStatus(), HistoryStatus.REJECTED.name());

  }
}

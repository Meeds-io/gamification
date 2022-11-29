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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.constant.IdentityType;
import org.exoplatform.addons.gamification.model.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.model.RealizationsFilter;
import org.exoplatform.addons.gamification.constant.HistoryStatus;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.manager.IdentityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RealizationsServiceTest {

  protected static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                           // NOSONAR

  protected static final Date fromDate        = new Date();

  protected static final Date toDate          = new Date(fromDate.getTime() + MILLIS_IN_A_DAY);

  protected static final int  offset          = 0;

  protected static final int  limit           = 3;

  private IdentityManager     identityManager;

  @Mock
  RealizationsStorage         realizationsStorage;

  @Mock
  RealizationsService         realizationsService;

  @Before
  public void setUp() throws Exception { // NOSONAR
    realizationsStorage = mock(RealizationsStorage.class);
    identityManager = mock(IdentityManager.class);
    realizationsService = new RealizationsServiceImpl(realizationsStorage, identityManager);
  }

  protected GamificationActionsHistoryDTO newGamificationActionsHistory() {
    GamificationActionsHistoryDTO gHistory = new GamificationActionsHistoryDTO();
    gHistory.setId(1L);
    gHistory.setStatus(HistoryStatus.ACCEPTED.name());
    gHistory.setDomain("gamification");
    gHistory.setReceiver("1");
    gHistory.setEarnerId("1L");
    gHistory.setEarnerType(IdentityType.USER.name());
    gHistory.setActionTitle("gamification title");
    gHistory.setActionScore(10);
    gHistory.setGlobalScore(10);
    gHistory.setRuleId(1L);
    gHistory.setCreatedDate(Utils.toRFC3339Date(fromDate));
    return gHistory;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testGetRealizationsByFilter() throws IllegalAccessException {
    // Given
    RealizationsFilter filter = new RealizationsFilter();
    Identity rootIdentity = new Identity("root1");
    ConversationState.setCurrent(new ConversationState(rootIdentity));
    MembershipEntry membershipentry = new MembershipEntry("/platform/administrators", "*");
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();
    memberships.add(membershipentry);
    rootIdentity.setMemberships(memberships);
    GamificationActionsHistoryDTO gHistory1 = newGamificationActionsHistory();
    GamificationActionsHistoryDTO gHistory2 = newGamificationActionsHistory();
    GamificationActionsHistoryDTO gHistory3 = newGamificationActionsHistory();
    List<GamificationActionsHistoryDTO> gamificationActionsHistoryDTOList = new ArrayList<>();
    gamificationActionsHistoryDTOList.add(gHistory1);
    gamificationActionsHistoryDTOList.add(gHistory2);
    gamificationActionsHistoryDTOList.add(gHistory3);
    when(realizationsStorage.getRealizationsByFilter(filter, offset, limit)).thenReturn(gamificationActionsHistoryDTOList);
    assertThrows(IllegalArgumentException.class, () -> realizationsService.getRealizationsByFilter(null, rootIdentity, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.getRealizationsByFilter(filter, null, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.getRealizationsByFilter(filter, rootIdentity, offset, limit));

    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(null, rootIdentity));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(filter, null));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(filter, rootIdentity));

    // When
    filter.setFromDate(toDate);
    filter.setToDate(fromDate);
    assertThrows(IllegalArgumentException.class, () -> realizationsService.getRealizationsByFilter(filter, rootIdentity, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(filter, rootIdentity));

    // When
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    List<GamificationActionsHistoryDTO> createdGamificationActionsHistoryDTOList =
                                                                                 realizationsService.getRealizationsByFilter(filter,
                                                                                                                             rootIdentity,
                                                                                                                              offset,
                                                                                                                              limit);
    // Then
    assertNotNull(createdGamificationActionsHistoryDTOList);
    assertEquals(3, createdGamificationActionsHistoryDTOList.size());
  }

  @Test
  public void updateRealizationStatus() {
    // Given
    GamificationActionsHistoryDTO gHistory1 = newGamificationActionsHistory();
    GamificationActionsHistoryDTO gHistory2 = newGamificationActionsHistory();
    gHistory2.setStatus(HistoryStatus.REJECTED.name());
    when(realizationsStorage.getRealizationById(1L)).thenReturn(gHistory1);
    when(realizationsStorage.getRealizationById(2L)).thenReturn(null);
    when(realizationsStorage.updateRealizationStatus(gHistory1)).thenReturn(gHistory2);
    GamificationActionsHistoryDTO rejectedHistory = null;
    // When
    assertThrows(IllegalArgumentException.class,
                 () -> realizationsService.updateRealizationStatus(null, HistoryStatus.REJECTED, "", 0L, ""));
    assertThrows(ObjectNotFoundException.class,
                 () -> realizationsService.updateRealizationStatus(2l, HistoryStatus.REJECTED, "", 0L, ""));

    try {

      rejectedHistory = realizationsService.updateRealizationStatus(1L, HistoryStatus.REJECTED, "new label", 10L, "domain");

    } catch (ObjectNotFoundException e) {
      fail(e.getMessage());
    }
    // Then
    assertNotNull(rejectedHistory);
    assertEquals(rejectedHistory.getStatus(), HistoryStatus.REJECTED.name());
  }

}

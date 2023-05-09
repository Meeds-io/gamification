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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RealizationsServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

@RunWith(MockitoJUnitRunner.class)
public class RealizationsServiceTest {

  protected static final long                      MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                           // NOSONAR

  protected static final Date                      fromDate        = new Date();

  protected static final Date                      toDate          = new Date(fromDate.getTime() + MILLIS_IN_A_DAY);

  protected static final int                       offset          = 0;

  protected static final int                       limit           = 3;

  private static MockedStatic<ExoContainerContext> CONTAINER_CONTEXT;

  @Mock
  IdentityManager                                  identityManager;

  @Mock
  DomainService                                    domainService;

  @Mock
  SpaceService                                     spaceService;

  @Mock
  RealizationsStorage                              realizationsStorage;

  @Mock
  IdentityRegistry                                 identityRegistry;

  RealizationsService                              realizationsService;

  @BeforeClass
  public static void initClassContext() {
    CONTAINER_CONTEXT = mockStatic(ExoContainerContext.class);
  }

  @AfterClass
  public static void endClassContext() {
    CONTAINER_CONTEXT.close();
  }

  @Before
  public void setUp() throws Exception { // NOSONAR
    realizationsService = new RealizationsServiceImpl(domainService, identityManager, spaceService, realizationsStorage);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testGetRealizationsByFilter() throws IllegalAccessException {
    // Given
    RealizationsFilter filter = new RealizationsFilter();
    Identity rootAclIdentity = new Identity("root1");
    org.exoplatform.social.core.identity.model.Identity rootIdentity =
                                                                     mock(org.exoplatform.social.core.identity.model.Identity.class);
    when(rootIdentity.getId()).thenReturn("2");
    when(rootIdentity.getRemoteId()).thenReturn(rootAclIdentity.getUserId());

    ConversationState.setCurrent(new ConversationState(rootAclIdentity));
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();
    rootAclIdentity.setMemberships(memberships);
    CONTAINER_CONTEXT.when(() -> ExoContainerContext.getService(IdentityRegistry.class)).thenReturn(identityRegistry);
    when(identityRegistry.getIdentity(rootIdentity.getRemoteId())).thenReturn(rootAclIdentity);

    GamificationActionsHistoryDTO gHistory1 = newGamificationActionsHistory();
    GamificationActionsHistoryDTO gHistory2 = newGamificationActionsHistory();
    GamificationActionsHistoryDTO gHistory3 = newGamificationActionsHistory();
    List<GamificationActionsHistoryDTO> gamificationActionsHistoryDTOList = new ArrayList<>();
    gamificationActionsHistoryDTOList.add(gHistory1);
    gamificationActionsHistoryDTOList.add(gHistory2);
    gamificationActionsHistoryDTOList.add(gHistory3);
    when(realizationsStorage.getRealizationsByFilter(filter, offset, limit)).thenReturn(gamificationActionsHistoryDTOList);
    when(identityManager.getOrCreateUserIdentity(rootAclIdentity.getUserId())).thenReturn(rootIdentity);
    assertThrows(IllegalArgumentException.class,
                 () -> realizationsService.getRealizationsByFilter(null, rootAclIdentity, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.getRealizationsByFilter(filter, null, offset, limit));
    assertThrows(IllegalArgumentException.class,
                 () -> realizationsService.getRealizationsByFilter(filter, rootAclIdentity, offset, limit));

    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(null, rootAclIdentity));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(filter, null));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(filter, rootAclIdentity));

    // When
    filter.setFromDate(toDate);
    filter.setToDate(fromDate);
    assertThrows(IllegalArgumentException.class,
                 () -> realizationsService.getRealizationsByFilter(filter, rootAclIdentity, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationsService.countRealizationsByFilter(filter, rootAclIdentity));

    // When
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    assertThrows(IllegalAccessException.class,
                 () -> realizationsService.getRealizationsByFilter(filter, rootAclIdentity, offset, limit));
    assertThrows(IllegalAccessException.class, () -> realizationsService.countRealizationsByFilter(filter, rootAclIdentity));

    filter.setEarnerIds(Collections.singletonList(rootIdentity.getId()));
    List<GamificationActionsHistoryDTO> createdGamificationActionsHistoryDTOList =
                                                                                 realizationsService.getRealizationsByFilter(filter,
                                                                                                                             rootAclIdentity,
                                                                                                                             offset,
                                                                                                                             limit);
    // Then
    assertNotNull(createdGamificationActionsHistoryDTOList);
    assertEquals(3, createdGamificationActionsHistoryDTOList.size());

    MembershipEntry membershipentry = new MembershipEntry("/platform/administrators", "*");
    memberships.add(membershipentry);
    rootAclIdentity.setMemberships(memberships);
    filter.setEarnerIds(null);

    createdGamificationActionsHistoryDTOList =
                                             realizationsService.getRealizationsByFilter(filter,
                                                                                         rootAclIdentity,
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
    when(realizationsStorage.updateRealization(gHistory1)).thenReturn(gHistory2);
    GamificationActionsHistoryDTO rejectedHistory = null;
    // When
    assertThrows(IllegalArgumentException.class,
                 () -> realizationsService.updateRealization(null));
    GamificationActionsHistoryDTO realization1 = new GamificationActionsHistoryDTO();
    realization1.setId(2L);
    assertThrows(ObjectNotFoundException.class,
                 () -> realizationsService.updateRealization(realization1));

    try {
      gHistory1.setStatus(HistoryStatus.REJECTED.name());
      gHistory1.setActionTitle("new label");
      gHistory1.setActionScore(10L);
      rejectedHistory = realizationsService.updateRealization(gHistory1);

    } catch (ObjectNotFoundException e) {
      fail(e.getMessage());
    }
    // Then
    assertNotNull(rejectedHistory);
    assertEquals(rejectedHistory.getStatus(), HistoryStatus.REJECTED.name());
  }

  protected GamificationActionsHistoryDTO newGamificationActionsHistory() {
    GamificationActionsHistoryDTO gHistory = new GamificationActionsHistoryDTO();
    gHistory.setId(1L);
    gHistory.setStatus(HistoryStatus.ACCEPTED.name());
    gHistory.setDomainDTO(new DomainDTO());
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

}

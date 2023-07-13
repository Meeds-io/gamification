/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
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
package io.meeds.gamification.storage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.concurrent.ConcurrentFIFOExoCache;

import io.meeds.gamification.dao.ConnectorAccountDAO;
import io.meeds.gamification.entity.ConnectorAccountEntity;
import io.meeds.gamification.storage.cached.ConnectorAccountCachedStorage;
import io.meeds.gamification.test.AbstractServiceTest;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectorAccountStorageTest extends AbstractServiceTest {

  @Mock
  CacheService                  cacheService;

  @Mock
  ConnectorAccountDAO           connectorAccountDAO;

  ConnectorAccountCachedStorage connectorAccountStorage;

  @Before
  public void setUp() throws Exception { // NOSONAR
    when(cacheService.getCacheInstance(any())).thenAnswer(invocation -> new ConcurrentFIFOExoCache<>(invocation.getArgument(0),
                                                                                                     500));
    connectorAccountStorage = new ConnectorAccountCachedStorage(connectorAccountDAO, cacheService);
  }

  @Test
  public void TestDeleteConnectorAccount() { // NOSONAR
    long connectorAccountId = 2L;

    ConnectorAccountEntity connectorAccountEntity = new ConnectorAccountEntity();
    connectorAccountEntity.setId(connectorAccountId);
    connectorAccountEntity.setConnectorName("connectorName");
    connectorAccountEntity.setUserId(2L);
    connectorAccountEntity.setRemoteId("connectorRemoteId");

    when(connectorAccountDAO.getConnectorAccountByNameAndRemoteId("connectorName",
                                                                  "connectorRemoteId")).thenReturn(connectorAccountEntity);
    when(connectorAccountDAO.getConnectorRemoteId("connectorName", 2L)).thenReturn("connectorRemoteId");
    when(connectorAccountDAO.getAssociatedUserIdentityId("connectorName", "connectorRemoteId")).thenReturn(2L);

    String remoteId = connectorAccountStorage.getConnectorRemoteId("connectorName", 2L);
    assertEquals("connectorRemoteId", remoteId);
    verify(connectorAccountDAO, times(1)).getConnectorRemoteId("connectorName", 2L);

    long userId = connectorAccountStorage.getUserIdentityId("connectorName", "connectorRemoteId");
    assertEquals(2L, userId);

    // Verify cache
    connectorAccountStorage.getConnectorRemoteId("connectorName", 2L);
    verify(connectorAccountDAO, times(1)).getConnectorRemoteId("connectorName", 2L);

    // Delete connector account
    connectorAccountStorage.deleteConnectorAccount("connectorName", "connectorRemoteId");
    verify(connectorAccountDAO, times(1)).delete(any());
    when(connectorAccountDAO.getConnectorRemoteId("connectorName", 2L)).thenReturn(null);
    when(connectorAccountDAO.getAssociatedUserIdentityId("connectorName", "connectorRemoteId")).thenReturn(0L);

    // Verify that cache is cleared
    connectorAccountStorage.getConnectorRemoteId("connectorName", 2L);
    verify(connectorAccountDAO, times(2)).getConnectorRemoteId("connectorName", 2L);

    connectorAccountStorage.getUserIdentityId("connectorName", "connectorRemoteId");
    verify(connectorAccountDAO, times(1)).getAssociatedUserIdentityId("connectorName", "connectorRemoteId");
  }
}

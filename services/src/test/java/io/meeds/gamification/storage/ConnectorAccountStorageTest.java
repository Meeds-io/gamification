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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import io.meeds.gamification.dao.ConnectorAccountDAO;
import io.meeds.gamification.entity.ConnectorAccountEntity;
import io.meeds.gamification.model.ConnectorAccount;
import io.meeds.gamification.storage.cached.ConnectorAccountCachedStorage;
import io.meeds.gamification.test.AbstractServiceTest;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.concurrent.ConcurrentFIFOExoCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectorAccountStorageTest extends AbstractServiceTest {

  private ConnectorAccountDAO           connectorAccountDAO;

  private ConnectorAccountCachedStorage connectorAccountStorage;

  @Before
  public void setUp() throws Exception { // NOSONAR
    CacheService cacheService = mock(CacheService.class);
    connectorAccountDAO = mock(ConnectorAccountDAO.class);

    when(cacheService.getCacheInstance(ConnectorAccountCachedStorage.CONNECTOR_ACCOUNT_CACHE_NAME)).thenReturn(new ConcurrentFIFOExoCache<>(ConnectorAccountCachedStorage.CONNECTOR_ACCOUNT_CACHE_NAME,
                                                                                                                                            500));
    when(cacheService.getCacheInstance(ConnectorAccountCachedStorage.CONNECTOR_REMOTE_ID_CACHE_NAME)).thenReturn(new ConcurrentFIFOExoCache<>(ConnectorAccountCachedStorage.CONNECTOR_REMOTE_ID_CACHE_NAME,
                                                                                                                                              500));
    when(cacheService.getCacheInstance(ConnectorAccountCachedStorage.CONNECTOR_USER_ID_CACHE_NAME)).thenReturn(new ConcurrentFIFOExoCache<>(ConnectorAccountCachedStorage.CONNECTOR_USER_ID_CACHE_NAME,
                                                                                                                                            500));
    connectorAccountStorage = new ConnectorAccountCachedStorage(connectorAccountDAO, cacheService);
  }

  @Test
  public void getConnectorAccountById() {
    ConnectorAccountEntity connectorAccountEntity = new ConnectorAccountEntity();
    connectorAccountEntity.setId(1L);
    connectorAccountEntity.setConnectorName("connectorName");
    connectorAccountEntity.setUserId(1L);
    connectorAccountEntity.setRemoteId("connectorRemoteId");

    when(connectorAccountDAO.find(1L)).thenReturn(connectorAccountEntity);
    when(connectorAccountDAO.getConnectorRemoteId("connectorName", 2L)).thenReturn("connectorRemoteId");
    when(connectorAccountDAO.getAssociatedUserId("connectorName", "connectorRemoteId")).thenReturn(2L);

    ConnectorAccount notExistingConnectorAccount = connectorAccountStorage.getConnectorAccountById(2L);
    assertNull(notExistingConnectorAccount);
    verify(connectorAccountDAO, times(1)).find(anyLong());

    ConnectorAccount connectorAccount = connectorAccountStorage.getConnectorAccountById(1L);
    assertNotNull(connectorAccount);
    verify(connectorAccountDAO, times(2)).find(anyLong());
    connectorAccountStorage.getConnectorAccountById(1L);

    // Verify cache
    verify(connectorAccountDAO, times(2)).find(anyLong());
  }

  @Test
  public void deleteConnectorAccountById() throws Exception { // NOSONAR
    long connectorAccountId = 2L;

    ConnectorAccountEntity connectorAccountEntity = new ConnectorAccountEntity();
    connectorAccountEntity.setId(connectorAccountId);
    connectorAccountEntity.setConnectorName("connectorName");
    connectorAccountEntity.setUserId(2L);
    connectorAccountEntity.setRemoteId("connectorRemoteId");

    when(connectorAccountDAO.find(connectorAccountId)).thenReturn(connectorAccountEntity);
    when(connectorAccountDAO.getConnectorRemoteId("connectorName", 2L)).thenReturn("connectorRemoteId");
    when(connectorAccountDAO.getAssociatedUserId("connectorName", "connectorRemoteId")).thenReturn(2L);

    ConnectorAccount connectorAccount = connectorAccountStorage.getConnectorAccountById(connectorAccountId);
    assertNotNull(connectorAccount);
    verify(connectorAccountDAO, times(1)).find(anyLong());

    String remoteId = connectorAccountStorage.getConnectorRemoteId("connectorName", 2L);
    assertEquals("connectorRemoteId", remoteId);
    verify(connectorAccountDAO, times(1)).getConnectorRemoteId("connectorName", 2L);

    long userId = connectorAccountStorage.getAssociatedUserId("connectorName", "connectorRemoteId");
    assertEquals(2L, userId);
    verify(connectorAccountDAO, times(1)).getAssociatedUserId("connectorName", "connectorRemoteId");

    // Verify cache
    connectorAccountStorage.getConnectorAccountById(connectorAccountId);
    verify(connectorAccountDAO, times(1)).find(anyLong());

    connectorAccountStorage.getConnectorRemoteId("connectorName", 2L);
    verify(connectorAccountDAO, times(1)).getConnectorRemoteId("connectorName", 2L);

    connectorAccountStorage.getAssociatedUserId("connectorName", "connectorRemoteId");
    verify(connectorAccountDAO, times(1)).getAssociatedUserId("connectorName", "connectorRemoteId");

    // Delete connector account
    connectorAccountStorage.deleteConnectorAccountById(connectorAccountId);
    verify(connectorAccountDAO, times(1)).create(any());
    when(connectorAccountDAO.find(connectorAccountId)).thenReturn(null);
    when(connectorAccountDAO.getConnectorRemoteId("connectorName", 2L)).thenReturn(null);
    when(connectorAccountDAO.getAssociatedUserId("connectorName", "connectorRemoteId")).thenReturn(0L);

    // Verify that cache is cleared
    connectorAccountStorage.getConnectorAccountById(connectorAccountId);
    verify(connectorAccountDAO, times(2)).find(anyLong());

    connectorAccountStorage.getConnectorRemoteId("connectorName", 2L);
    verify(connectorAccountDAO, times(2)).getConnectorRemoteId("connectorName", 2L);

    connectorAccountStorage.getAssociatedUserId("connectorName", "connectorRemoteId");
    verify(connectorAccountDAO, times(2)).getAssociatedUserId("connectorName", "connectorRemoteId");
  }
}

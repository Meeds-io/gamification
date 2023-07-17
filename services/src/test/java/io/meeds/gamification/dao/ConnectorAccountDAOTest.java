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
package io.meeds.gamification.dao;

import io.meeds.gamification.entity.ConnectorAccountEntity;
import io.meeds.gamification.test.AbstractServiceTest;

public class ConnectorAccountDAOTest extends AbstractServiceTest {

  public void testCreateConnectorAccount() {
    ConnectorAccountEntity connectorAccountEntity = new ConnectorAccountEntity();

    String connectorName = "connectorName";
    String remoteId = "connectorRemoteId";
    long userId = 2L;

    connectorAccountEntity.setConnectorName(connectorName);
    connectorAccountEntity.setRemoteId(remoteId);
    connectorAccountEntity.setUserId(userId);

    connectorAccountEntity = connectorAccountDAO.create(connectorAccountEntity);
    assertNotNull(connectorAccountEntity.getId());
    assertEquals(connectorName, connectorAccountEntity.getConnectorName());
    assertEquals(remoteId, connectorAccountEntity.getRemoteId());
    assertEquals(userId, connectorAccountEntity.getUserId());

    connectorAccountEntity = connectorAccountDAO.getConnectorAccountByNameAndUserId(connectorName, userId);
    assertNotNull(connectorAccountEntity.getId());
    assertEquals(connectorName, connectorAccountEntity.getConnectorName());
    assertEquals(remoteId, connectorAccountEntity.getRemoteId());
    assertEquals(userId, connectorAccountEntity.getUserId());

    String connectorRemoteId = connectorAccountDAO.getConnectorRemoteId(connectorName, userId);
    assertNotNull(connectorRemoteId);
    assertEquals(remoteId, connectorRemoteId);

    long connectorUserId = connectorAccountDAO.getAssociatedUserIdentityId(connectorName, remoteId);
    assertEquals(userId, connectorUserId);

  }

  public void testDeleteConnectorAccount() {
    ConnectorAccountEntity connectorAccountEntity = new ConnectorAccountEntity();

    String connectorName = "connectorName";
    String remoteId = "connectorRemoteId";
    long userId = 2L;

    connectorAccountEntity.setConnectorName(connectorName);
    connectorAccountEntity.setRemoteId(remoteId);
    connectorAccountEntity.setUserId(userId);

    connectorAccountEntity = connectorAccountDAO.create(connectorAccountEntity);

    assertNotNull(connectorAccountEntity.getId());

    connectorAccountDAO.delete(connectorAccountEntity);

    connectorAccountEntity = connectorAccountDAO.find(connectorAccountEntity.getId());
    assertNull(connectorAccountEntity);

    long connectorUserId = connectorAccountDAO.getAssociatedUserIdentityId(connectorName, remoteId);
    assertEquals(0, connectorUserId);
  }
}

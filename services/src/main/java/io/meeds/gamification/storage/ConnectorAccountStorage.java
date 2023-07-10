/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
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

import io.meeds.gamification.dao.ConnectorAccountDAO;
import io.meeds.gamification.entity.ConnectorAccountEntity;
import io.meeds.gamification.model.ConnectorAccount;
import io.meeds.gamification.storage.mapper.ConnectorMapper;

public class ConnectorAccountStorage {

  private ConnectorAccountDAO connectorAccountDAO;

  public ConnectorAccountStorage(ConnectorAccountDAO connectorAccountDAO) {
    this.connectorAccountDAO = connectorAccountDAO;
  }

  public void saveConnectorAccount(String connectorName, long userId, String connectorRemoteId) {
    ConnectorAccount connectorAccount = getConnectorAccountByNameAndUserId(connectorName, userId);
    if (connectorAccount != null) {
      ConnectorAccountEntity connectorAccountEntity = ConnectorMapper.toEntity(connectorAccount);
      connectorAccount.setRemoteId(connectorRemoteId);
      connectorAccountDAO.update(connectorAccountEntity);
    } else {
      ConnectorAccountEntity connectorAccountEntity = new ConnectorAccountEntity();
      connectorAccountEntity.setConnectorName(connectorName);
      connectorAccountEntity.setUserId(userId);
      connectorAccountEntity.setRemoteId(connectorRemoteId);
      connectorAccountDAO.create(connectorAccountEntity);
    }
  }

  public String getConnectorRemoteId(String connectorName, long userId) {
    return connectorAccountDAO.getConnectorRemoteId(connectorName, userId);
  }

  public long getAssociatedUserId(String connectorName, String connectorRemoteId) {
    return connectorAccountDAO.getAssociatedUserId(connectorName, connectorRemoteId);
  }

  public ConnectorAccount getConnectorAccountById(long connectorAccountId) {
    ConnectorAccountEntity connectorAccountEntity = connectorAccountDAO.find(connectorAccountId);
    if (connectorAccountEntity == null) {
      return null;
    }
    return new ConnectorAccount(connectorAccountEntity.getId(),
                                connectorAccountEntity.getConnectorName(),
                                connectorAccountEntity.getRemoteId(),
                                connectorAccountEntity.getId());
  }

  public ConnectorAccount getConnectorAccountByNameAndUserId(String connectorName, long userId) {
    ConnectorAccountEntity connectorAccountEntity = connectorAccountDAO.getConnectorAccountByNameAndUserId(connectorName, userId);
    if (connectorAccountEntity == null) {
      return null;
    }
    return new ConnectorAccount(connectorAccountEntity.getId(),
                                connectorAccountEntity.getConnectorName(),
                                connectorAccountEntity.getRemoteId(),
                                connectorAccountEntity.getId());
  }

  public void deleteConnectorAccountById(long connectorAccountId) {
    ConnectorAccount connectorAccount = getConnectorAccountById(connectorAccountId);
    if (connectorAccount == null) {
      return;
    }
    connectorAccount.setRemoteId(null);
    saveConnectorAccount(connectorAccount.getConnectorName(), connectorAccount.getUserId(), connectorAccount.getRemoteId());
  }
}

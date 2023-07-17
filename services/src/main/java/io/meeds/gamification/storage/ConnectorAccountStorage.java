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

import static io.meeds.gamification.storage.mapper.ConnectorMapper.*;

import org.exoplatform.commons.ObjectAlreadyExistsException;

import io.meeds.gamification.dao.ConnectorAccountDAO;
import io.meeds.gamification.entity.ConnectorAccountEntity;
import io.meeds.gamification.model.ConnectorAccount;

public class ConnectorAccountStorage {

  private ConnectorAccountDAO connectorAccountDAO;

  public ConnectorAccountStorage(ConnectorAccountDAO connectorAccountDAO) {
    this.connectorAccountDAO = connectorAccountDAO;
  }

  public String getConnectorRemoteId(String connectorName, long userId) {
    return connectorAccountDAO.getConnectorRemoteId(connectorName, userId);
  }

  public long getUserIdentityId(String connectorName, String connectorRemoteId) {
    return connectorAccountDAO.getAssociatedUserIdentityId(connectorName, connectorRemoteId);
  }

  public ConnectorAccount getConnectorAccount(String connectorName, long userId) {
    ConnectorAccountEntity connectorAccountEntity = connectorAccountDAO.getConnectorAccountByNameAndUserId(connectorName,
                                                                                                           userId);
    return fromEntity(connectorAccountEntity);
  }

  public void saveConnectorAccount(ConnectorAccount connectorAccount) throws ObjectAlreadyExistsException {
    long userIdentityId = getUserIdentityId(connectorAccount.getConnectorName(), connectorAccount.getRemoteId());
    if (userIdentityId == 0) {
      ConnectorAccountEntity connectorAccountEntity = toEntity(connectorAccount);
      connectorAccountEntity.setId(null);
      connectorAccountDAO.create(connectorAccountEntity);
    } else {
      throw new ObjectAlreadyExistsException(connectorAccount);
    }
  }

  public ConnectorAccount deleteConnectorAccount(String connectorName, String connectorRemoteId) {
    ConnectorAccountEntity connectorAccountEntity = connectorAccountDAO.getConnectorAccountByNameAndRemoteId(connectorName,
                                                                                                             connectorRemoteId);
    if (connectorAccountEntity != null) {
      connectorAccountDAO.delete(connectorAccountEntity);
    }
    return fromEntity(connectorAccountEntity);
  }

}

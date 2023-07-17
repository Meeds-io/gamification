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
package io.meeds.gamification.storage.mapper;

import io.meeds.gamification.entity.ConnectorAccountEntity;
import io.meeds.gamification.model.ConnectorAccount;

public class ConnectorMapper {

  private ConnectorMapper() {
    // Class with static methods
  }

  public static ConnectorAccountEntity toEntity(ConnectorAccount connectorAccount) {
    if (connectorAccount == null) {
      return null;
    }
    ConnectorAccountEntity connectorAccountEntity = new ConnectorAccountEntity();
    if (connectorAccount.getUserId() != 0) {
      connectorAccountEntity.setUserId(connectorAccount.getUserId());
    }
    if (connectorAccount.getConnectorName() != null) {
      connectorAccountEntity.setConnectorName(connectorAccount.getConnectorName());
    }
    if (connectorAccount.getRemoteId() != null) {
      connectorAccountEntity.setRemoteId(connectorAccount.getRemoteId());
    }
    return connectorAccountEntity;
  }

  public static ConnectorAccount fromEntity(ConnectorAccountEntity connectorAccountEntity) {
    if (connectorAccountEntity == null) {
      return null;
    }
    return new ConnectorAccount(connectorAccountEntity.getConnectorName(),
                                connectorAccountEntity.getRemoteId(),
                                connectorAccountEntity.getId());
  }

}

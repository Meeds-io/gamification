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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.rest.builder;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.rest.model.ConnectorRestEntity;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;

public class ConnectorBuilder {

  private ConnectorBuilder() {
    // Class with static methods
  }

  public static ConnectorRestEntity toRestEntity(ConnectorService connectorService,
                                                 ConnectorSettingService connectorSettingService,
                                                 RemoteConnector remoteConnector,
                                                 List<String> expandFields,
                                                 String username) {
    if (remoteConnector == null) {
      return null;
    }
    String identifier = null;
    String secretKey = null;
    if (CollectionUtils.isNotEmpty(expandFields)) {
      if (expandFields.contains("userIdentifier")) {
        identifier = connectorService.getConnectorRemoteId(remoteConnector.getName(), username);
      }
      if (expandFields.contains("secretKey")) {
        secretKey = connectorSettingService.getConnectorSecretKey(remoteConnector.getName());
      }
    }
    return new ConnectorRestEntity(remoteConnector.getName(),
                                   remoteConnector.getApiKey(),
                                   remoteConnector.getRedirectUrl(),
                                   StringUtils.isNotBlank(identifier) ? identifier : null,
                                   StringUtils.isNotBlank(secretKey) ? secretKey : null,
                                   remoteConnector.isEnabled());
  }

  public static List<ConnectorRestEntity> toRestEntities(ConnectorService connectorService,
                                                         ConnectorSettingService connectorSettingService,
                                                         Collection<RemoteConnector> remoteConnectors,
                                                         List<String> expandFields,
                                                         String username) {
    return remoteConnectors.stream()
                           .map(remoteConnector -> toRestEntity(connectorService,
                                                                connectorSettingService,
                                                                remoteConnector,
                                                                expandFields,
                                                                username))
                           .toList();
  }
}

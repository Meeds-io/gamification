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

import io.meeds.gamification.model.ConnectorHook;
import io.meeds.gamification.rest.model.ConnectorHookRestEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.rest.model.ConnectorRestEntity;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

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
    String accessToken = null;
    if (CollectionUtils.isNotEmpty(expandFields)) {
      if (expandFields.contains("userIdentifier")) {
        identifier = connectorService.getConnectorRemoteId(remoteConnector.getName(), username);
      }
      if (expandFields.contains("secretKey")) {
        secretKey = connectorSettingService.getConnectorSecretKey(remoteConnector.getName());
      }
      if (expandFields.contains("accessToken")) {
        accessToken = connectorSettingService.getConnectorAccessToken(remoteConnector.getName());
      }
    }
    return new ConnectorRestEntity(remoteConnector.getName(),
                                   remoteConnector.getApiKey(),
                                   remoteConnector.getRedirectUrl(),
                                   StringUtils.isNotBlank(identifier) ? identifier : null,
                                   StringUtils.isNotBlank(secretKey) ? secretKey : null,
                                   StringUtils.isNotBlank(accessToken) ? accessToken : null,
                                   remoteConnector.isEnabled());
  }

  public static ConnectorHookRestEntity toRestEntity(IdentityManager identityManager, ConnectorHook connectorHook) {
    if (connectorHook == null) {
      return null;
    }
    String imageUrl = null;
    Identity userIdentity = null;
    if (connectorHook.getImageFileId() > 0) {
      imageUrl = "/" + PortalContainer.getCurrentPortalContainerName() + "/" + PortalContainer.getCurrentRestContextName()
          + "/gamification/connectors/hook/" + "/" + connectorHook.getConnectorName() + "/" + connectorHook.getName() + "/avatar";
    }
    if (connectorHook.getWatchedBy() > 0) {
      userIdentity = identityManager.getIdentity(String.valueOf(connectorHook.getWatchedBy()));
    }

    return new ConnectorHookRestEntity(connectorHook.getConnectorName(),
                                       connectorHook.getName(),
                                       connectorHook.getTitle(),
                                       connectorHook.getDescription(),
                                       imageUrl,
                                       connectorHook.getWatchDate(),
                                       userIdentity != null ? userIdentity.getRemoteId() : null,
                                       connectorHook.getUpdatedDate());
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

  public static List<ConnectorHookRestEntity> toRestEntities(IdentityManager identityManager,
                                                             Collection<ConnectorHook> connectorHooks) {
    return connectorHooks.stream().map(connectorHook -> toRestEntity(identityManager, connectorHook)).toList();
  }
}

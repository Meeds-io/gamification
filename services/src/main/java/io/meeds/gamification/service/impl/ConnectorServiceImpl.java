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
package io.meeds.gamification.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.model.ConnectorAccount;
import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import io.meeds.gamification.storage.ConnectorAccountStorage;

public class ConnectorServiceImpl implements ConnectorService {

  public static final String                 CONNECTOR_NAME_IS_MANDATORY      = "Connector name is mandatory";

  public static final String                 USERNAME_IS_MANDATORY            = "Username is mandatory";

  public static final String                 CONNECTOR_REMOTE_ID_IS_MANDATORY = "connector RemoteId is mandatory";

  public static final String                 ACCESS_TOKEN_IS_MANDATORY        = "Access Token is mandatory";

  private final Map<String, ConnectorPlugin> connectorPlugins                 = new HashMap<>();

  protected final ConnectorAccountStorage    connectorAccountStorage;

  private final IdentityManager              identityManager;

  private final ConnectorSettingService      connectorSettingService;

  public ConnectorServiceImpl(ConnectorAccountStorage connectorAccountStorage,
                              IdentityManager identityManager,
                              ConnectorSettingService connectorSettingService) {
    this.connectorAccountStorage = connectorAccountStorage;
    this.connectorSettingService = connectorSettingService;
    this.identityManager = identityManager;
  }

  @Override
  public void addPlugin(ConnectorPlugin connectorPlugin) {
    connectorPlugins.put(connectorPlugin.getName(), connectorPlugin);
  }

  @Override
  public void removePlugin(String name) {
    connectorPlugins.remove(name);
  }

  @Override
  public Collection<ConnectorPlugin> getConnectorPlugins() {
    return connectorPlugins.values();
  }

  @Override
  public Collection<RemoteConnector> getConnectors(String username) {
    if (StringUtils.isBlank(username)) {
      throw new IllegalArgumentException("username is mandatory");
    }
    List<RemoteConnector> connectorList = new ArrayList<>();

    connectorPlugins.forEach((s, connectorPlugin) -> {
      RemoteConnector remoteConnector = new RemoteConnector();
      String connectorName = connectorPlugin.getConnectorName();
      remoteConnector.setName(connectorName);
      RemoteConnectorSettings remoteConnectorSettings = connectorSettingService.getConnectorSettings(connectorName);
      if (remoteConnectorSettings != null) {
        remoteConnector.setApiKey(remoteConnectorSettings.getApiKey());
        remoteConnector.setRedirectUrl(remoteConnectorSettings.getRedirectUrl());
        remoteConnector.setEnabled(remoteConnectorSettings.isEnabled());
      }
      connectorList.add(remoteConnector);
    });
    return connectorList;
  }

  @Override
  public String connect(String connectorName,
                        String connectorUserId,
                        String accessToken,
                        Identity userAclIdentity) throws ObjectAlreadyExistsException {
    if (connectorName == null) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (accessToken == null) {
      throw new IllegalArgumentException(ACCESS_TOKEN_IS_MANDATORY);
    }
    String username = userAclIdentity.getUserId();
    String userIdentityId = identityManager.getOrCreateUserIdentity(username).getId();
    connectorUserId = getConnectorPlugin(connectorName).validateToken(connectorUserId, accessToken);
    connectorAccountStorage.saveConnectorAccount(new ConnectorAccount(connectorName,
                                                                      connectorUserId,
                                                                      Long.parseLong(userIdentityId)));
    return connectorUserId;
  }

  @Override
  public void disconnect(String connectorName, String remoteId) {
    if (connectorName == null) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (remoteId == null) {
      throw new IllegalArgumentException(CONNECTOR_REMOTE_ID_IS_MANDATORY);
    }
    connectorAccountStorage.deleteConnectorAccount(connectorName, remoteId);
  }

  @Override
  public String getConnectorRemoteId(String connectorName, String username) {
    if (connectorName == null) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (username == null) {
      throw new IllegalArgumentException(USERNAME_IS_MANDATORY);
    }
    String userId = identityManager.getOrCreateUserIdentity(username).getId();
    return connectorAccountStorage.getConnectorRemoteId(connectorName, Long.parseLong(userId));
  }

  @Override
  public String getAssociatedUsername(String connectorName, String connectorRemoteId) {
    if (connectorName == null) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (connectorRemoteId == null) {
      throw new IllegalArgumentException(CONNECTOR_REMOTE_ID_IS_MANDATORY);
    }
    long userId = connectorAccountStorage.getUserIdentityId(connectorName, connectorRemoteId);
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getIdentity(String.valueOf(userId));

    if (userIdentity != null) {
      return userIdentity.getRemoteId();
    } else {
      return null;
    }
  }

  private ConnectorPlugin getConnectorPlugin(String connectorName) {
    return connectorPlugins.get(connectorName);
  }

}

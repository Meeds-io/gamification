/**
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;

public class ConnectorServiceImpl implements ConnectorService {

  private final Map<String, ConnectorPlugin> connectorPlugins = new HashMap<>();

  private final IdentityManager              identityManager;

  private final ConnectorSettingService      connectorSettingService;

  public ConnectorServiceImpl(IdentityManager identityManager,
                              ConnectorSettingService connectorSettingService) {
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
  public Map<String, ConnectorPlugin> getConnectorPlugins() {
    return connectorPlugins;
  }

  @Override
  public List<RemoteConnector> getUserRemoteConnectors(String username) {
    if (StringUtils.isBlank(username)) {
      throw new IllegalArgumentException("username is mandatory");
    }
    List<RemoteConnector> connectorList = new ArrayList<>();

    connectorPlugins.forEach((s, connectorPlugin) -> {
      RemoteConnector remoteConnector = new RemoteConnector();
      String connectorName = connectorPlugin.getConnectorName();
      remoteConnector.setName(connectorName);
      remoteConnector.setIdentifier(getConnectorRemoteId(connectorName, username));

      RemoteConnectorSettings remoteConnectorSettings =
                                                      connectorSettingService.getConnectorSettings(connectorName);
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
  public String connect(String connectorName, String accessToken, Identity userAclIdentity) throws ObjectAlreadyExistsException {
    ConnectorPlugin connectorPlugin = getConnectorPlugin(connectorName);
    String remoteIdentifier = connectorPlugin.validateToken(accessToken);
    saveConnectorRemoteId(connectorName, userAclIdentity.getUserId(), remoteIdentifier);
    return remoteIdentifier;
  }

  @Override
  public void disconnect(String connectorName, String username) throws ObjectNotFoundException {
    // TODO Use Common Storage Layer to move from Github Connector in order to
    // remove RemoteId/UserName/ConnectorName association
    // Note: Use Long Value of IdentityManager.getOrCreateUserIdentity(username).getId() in database
  }

  @Override
  public String getConnectorRemoteId(String connectorName, String username) {
    // TODO Use Common Storage Layer to retrieve ConnectorRemoteId/UserName/ConnectorName association
    // Note: Use Long Value of IdentityManager.getOrCreateUserIdentity(username).getId() in database
    return null;
  }

  @Override
  public String getAssociatedUsername(String connectorName, String connectorRemoteId) {
    // TODO Use Common Storage Layer to retrieve ConnectorRemoteId/UserName/ConnectorName association
    // Note: Use Long Value of IdentityManager.getOrCreateUserIdentity(username).getId() in database
    return null;
  }

  private void saveConnectorRemoteId(String connectorName, String username, String remoteIdentifier) {
    // TODO Use Common Storage Layer to move from Github Connector in order to
    // store ConnectorRemoteId/UserName/ConnectorName association
    // Note: Use Long Value of IdentityManager.getOrCreateUserIdentity(username).getId() in database
  }

  private ConnectorPlugin getConnectorPlugin(String connectorName) {
    return connectorPlugins.get(connectorName);
  }

}

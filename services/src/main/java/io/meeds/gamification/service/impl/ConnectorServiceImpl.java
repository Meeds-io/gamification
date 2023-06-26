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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

public class ConnectorServiceImpl implements ConnectorService {

  private final Map<String, ConnectorPlugin> connectorPlugins = new HashMap<>();

  private final ConnectorSettingService      connectorSettingService;

  public ConnectorServiceImpl(ConnectorSettingService connectorSettingService) {
    this.connectorSettingService = connectorSettingService;
  }

  /**
   * {@inheritDoc}
   */
  public void addPlugin(ConnectorPlugin connectorPlugin) {
    connectorPlugins.put(connectorPlugin.getName(), connectorPlugin);
  }

  /**
   * {@inheritDoc}
   */
  public void removePlugin(String name) {
    connectorPlugins.remove(name);
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, ConnectorPlugin> getConnectorPlugins() {
    return connectorPlugins;
  }

  /**
   * {@inheritDoc}
   */
  public List<RemoteConnector> getUserRemoteConnectors(String username) {
    if (StringUtils.isBlank(username)) {
      throw new IllegalArgumentException("username is mandatory");
    }
    List<RemoteConnector> connectorList = new ArrayList<>();

    connectorPlugins.forEach((s, connectorPlugin) -> {
      RemoteConnector remoteConnector = new RemoteConnector();
      remoteConnector.setName(connectorPlugin.getConnectorName());
      remoteConnector.setIdentifier(connectorPlugin.getIdentifier(username));

      RemoteConnectorSettings remoteConnectorSettings =
                                                      connectorSettingService.getConnectorSettings(connectorPlugin.getConnectorName());
      if (remoteConnectorSettings != null) {
        remoteConnector.setApiKey(remoteConnectorSettings.getApiKey());
        remoteConnector.setRedirectUrl(remoteConnectorSettings.getRedirectUrl());
        remoteConnector.setEnabled(remoteConnectorSettings.isEnabled());
      }
      connectorList.add(remoteConnector);
    });
    return connectorList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String connect(String connectorName, String accessToken, Identity identity) throws IOException,
                                                                                     ExecutionException,
                                                                                     ObjectAlreadyExistsException {
    return connectorPlugins.get(connectorName).connect(accessToken, identity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void disconnect(String connectorName, String username) throws ObjectNotFoundException {
    connectorPlugins.get(connectorName).disconnect(username);
  }
}

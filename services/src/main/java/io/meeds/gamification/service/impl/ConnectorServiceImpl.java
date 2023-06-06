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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.meeds.gamification.model.Connector;
import io.meeds.gamification.model.ConnectorLoginRequest;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.service.ConnectorService;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

public class ConnectorServiceImpl implements ConnectorService {

  private final Map<String, ConnectorPlugin> connectorPlugins = new HashMap<>();

  public void addPlugin(ConnectorPlugin connectorPlugin) {
    connectorPlugins.put(connectorPlugin.getName(), connectorPlugin);
  }

  public void removePlugin(String name) {
    connectorPlugins.remove(name);
  }

  public List<Connector> getEnabledConnectors(String username) {
    List<Connector> connectorList = new ArrayList<>();

    connectorPlugins.forEach((s, connectorPlugin) -> {
      Connector connector = new Connector(connectorPlugin.getConnectorName(),
                                          connectorPlugin.getConnectorRedirectURL(),
                                          connectorPlugin.getConnectorApiKey(),
                                          connectorPlugin.isConnected(username),
                                          connectorPlugin.getIdentifier(username));
      connectorList.add(connector);
    });
    return connectorList;
  }

  @Override
  public String connect(ConnectorLoginRequest connectorLoginRequest,
                        Identity identity) throws IOException, ExecutionException, ObjectAlreadyExistsException {
    String connectorName = connectorLoginRequest.getConnectorName();
    return connectorPlugins.get(connectorName).connect(connectorLoginRequest.getAccessToken(), identity);
  }

  @Override
  public void disconnect(String connectorName, String username) throws ObjectNotFoundException {
    connectorPlugins.get(connectorName).disconnect(username);
  }
}

/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
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
package io.meeds.gamification.service;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.plugin.ConnectorPlugin;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface ConnectorService {

  /**
   * Add a new {@link ConnectorPlugin} for a given connector name
   *
   * @param connectorPlugin {@link ConnectorPlugin}
   */
  void addPlugin(ConnectorPlugin connectorPlugin);

  /**
   * Removes a {@link ConnectorPlugin} identified by its connectorName
   *
   * @param name connector name
   */
  void removePlugin(String name);

  /**
   * Retrieves the list of Connector Plugins
   */
  Map<String, ConnectorPlugin> getConnectorPlugins();

  /**
   * @return {@link List} of user remote connectors
   */
  List<RemoteConnector> getUserRemoteConnectors(String username);

  /**
   * Connects a user to their connector account
   *
   * @param connectorName connector name
   * @param accessToken Access token
   * @param identity the user identity
   * @return the connector identifier {@link String}
   */
  String connect(String connectorName, String accessToken, Identity identity) throws IOException,
                                                                              ExecutionException,
                                                                              ObjectAlreadyExistsException;

  /**
   * Disconnect a user from their connector account
   *
   * @param username the user name
   */
  void disconnect(String connectorName, String username) throws ObjectNotFoundException;

  /**
   * @param connectorName connector name
   * @param username associated username
   * @return the connector remote Id {@link String}
   */
  String getConnectorRemoteId(String connectorName, String username);

  /**
   * @param connectorName connector name
   * @param connectorRemoteId connector remote Id
   * @return the connector associated username {@link String}
   */
  String getAssociatedUsername(String connectorName, String connectorRemoteId);
}

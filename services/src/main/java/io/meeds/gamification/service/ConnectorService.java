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
package io.meeds.gamification.service;

import java.util.Collection;

import org.exoplatform.services.security.Identity;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.plugin.ConnectorPlugin;

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
   * @return {@link Collection} of configured {@link ConnectorPlugin}
   */
  Collection<ConnectorPlugin> getConnectorPlugins();

  /**
   * @param  username the user name
   * @return          {@link Collection} of user remote connectors
   */
  Collection<RemoteConnector> getConnectors(String username);

  /**
   * Connects a user to their connector account
   *
   * @param  connectorName                connector name
   * @param  accessToken                  Access token
   * @param  connectorUserId              User identifier in connector
   * @param  identity                     the user identity
   * @return                              the connector identifier
   *                                      {@link String}
   */
  String connect(String connectorName, String connectorUserId, String accessToken, Identity identity);

  /**
   * Disconnect a user from their connector account
   *
   * @param connectorName connector name
   * @param username      the user name
   */
  void disconnect(String connectorName, String username);

  /**
   * @param  connectorName connector name
   * @param  username      associated user name
   * @return               the connector remote id of a user
   */
  String getConnectorRemoteId(String connectorName, String username);

  /**
   * @param  connectorName     connector name
   * @param  connectorRemoteId connector remote Id
   * @return                   the associated user name to connector remote id
   */
  String getAssociatedUsername(String connectorName, String connectorRemoteId);
}

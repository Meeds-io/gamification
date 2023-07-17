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

import io.meeds.gamification.model.RemoteConnectorSettings;
import org.exoplatform.services.security.Identity;

import java.util.List;

public interface ConnectorSettingService {

  /**
   * Save connector {@link RemoteConnectorSettings} which contains the api key,
   * secret key and connector status
   *
   * @param remoteConnectorSettings {@link RemoteConnectorSettings} To Store
   * @param aclIdentity Security identity of user attempting to save connector
   *          settings
   */
  void saveConnectorSettings(RemoteConnectorSettings remoteConnectorSettings, Identity aclIdentity) throws IllegalAccessException;

  /**
   * Delete connector settings identified by connector name
   *
   * @param connectorName connector name
   * @param aclIdentity Security identity of user attempting to retrieve connector
   *          settings
   */
  void deleteConnectorSettings(String connectorName, Identity aclIdentity) throws IllegalAccessException;

  /**
   * @param connectorName connector name
   * @param aclIdentity Security identity of user attempting to retrieve connector
   *          settings
   * @return {@link RemoteConnectorSettings} connector settings
   */
  RemoteConnectorSettings getConnectorSettings(String connectorName, Identity aclIdentity) throws IllegalAccessException;

  /**
   * @param connectorName connector name
   * @return {@link RemoteConnectorSettings} connector settings
   */
  RemoteConnectorSettings getConnectorSettings(String connectorName);

  /**
   * @param connectorName connector name
   *
   * @return {@link String} connector secret key
   */
  String getConnectorSecretKey(String connectorName);

  /**
   * Retrieves the list of Connectors setting
   * 
   * @param aclIdentity Security identity of user attempting to retrieve
   *          connectors settings
   * @return list of {@link RemoteConnectorSettings} connector settings
   */
  List<RemoteConnectorSettings> getConnectorsSettings(ConnectorService connectorService,
                                                      Identity aclIdentity) throws IllegalAccessException;

  /**
   * Check whether user can can edit connectors Settings or not
   *
   * @param aclIdentity Security identity of user
   * @return true if user has enough privileges to edit connectors Setting, else
   *         false
   */
  boolean canManageConnectorSettings(Identity aclIdentity);
}

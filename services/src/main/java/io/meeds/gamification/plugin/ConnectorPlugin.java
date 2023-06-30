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
package io.meeds.gamification.plugin;

import org.exoplatform.container.component.BaseComponentPlugin;
import io.meeds.gamification.service.ConnectorService;

/**
 * A plugin that will be used by {@link ConnectorService} to add and remove user
 * gamification connectors
 */
public abstract class ConnectorPlugin extends BaseComponentPlugin {

  /**
   * Validates a user Token with the Gamification Connector provider
   *
   * @param accessToken connector access token
   * @return the user identifier corresponding to Access Token generated on Remote
   *         Connector
   */
  public String validateToken(String accessToken) {
    return validateToken(null, accessToken);
  }

  /**
   * Validates a user Token with the Gamification Connector provider
   *
   * @param accessToken connector access token
   * @param connectorUserId User identifier in connector
   * @return the user identifier corresponding to Access Token generated on Remote
   *         Connector
   */
  public String validateToken(String connectorUserId, String accessToken) {
    return validateToken(accessToken);
  }

  /**
   * Gets connector name
   **
   * @return the connector name
   */
  public abstract String getConnectorName();
}

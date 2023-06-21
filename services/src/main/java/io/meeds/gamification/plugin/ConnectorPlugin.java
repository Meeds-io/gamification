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
package io.meeds.gamification.plugin;

import io.meeds.gamification.service.ConnectorService;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.services.security.Identity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * A plugin that will be used by {@link ConnectorService} to add and remove user
 * gamification connectors
 */
public abstract class ConnectorPlugin extends BaseComponentPlugin {

  /**
   * Connects a user to their connector account
   *
   * @param accessToken connector access token
   * @param identity the user identity
   * @return the connector identifier {@link String}
   */
  public abstract String connect(String accessToken,
                                 Identity identity) throws IOException, ExecutionException, ObjectAlreadyExistsException;

  /**
   * Disconnect a user from their connector account
   *
   * @param username the user name
   */
  public abstract void disconnect(String username) throws ObjectNotFoundException;

  /**
   * Gets user connector account identifier
   *
   * @param username the user name
   * @return the connector identifier {@link String}
   */
  public abstract String getIdentifier(String username);

  /**
   * Gets connector name
   **
   * @return the connector name
   */
  public abstract String getConnectorName();
}

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

import io.meeds.gamification.model.ConnectorHook;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.io.InputStream;
import java.util.List;

public interface ConnectorHookService {

  /**
   * Get available connector hooks using offset and limit.
   *
   * @param connectorName connector name
   * @param currentUser user name attempting to access connector hooks
   * @param offset Offset of result
   * @param limit Limit of result
   * @throws IllegalAccessException when user is not authorized to access
   *           connector hooks
   * @return {@link List} of {@link ConnectorHook}
   */
  List<ConnectorHook> getConnectorHooks(String connectorName,
                                        String currentUser,
                                        int offset,
                                        int limit) throws IllegalAccessException;

  /**
   * Get connector hook by organization name.
   *
   * @param connectorName connector name
   * @param organizationName connector hook organization name
   * @return {@link ConnectorHook}
   */
  ConnectorHook getConnectorHook(String connectorName, String organizationName);

  /**
   * create connector hook
   *
   * @param connectorHook {@link ConnectorHook} to create
   * @param currentUser user name attempting to save connector hook
   * @return created {@link ConnectorHook}
   * @throws IllegalAccessException when user is not authorized to create a
   *           connector hook
   * @throws ObjectAlreadyExistsException when connector hook already exists
   */
  ConnectorHook createConnectorHook(ConnectorHook connectorHook, String currentUser) throws IllegalAccessException,
                                                                                     ObjectAlreadyExistsException;

  /**
   * delete connector hook
   *
   * @param connectorName connector name
   * @param organizationName connector hook organization name
   * @param currentUser user name attempting to delete connector hook
   * @return created {@link ConnectorHook}
   * @throws IllegalAccessException when user is not authorized to delete the
   *           connector hook
   */
  ConnectorHook deleteConnectorHook(String connectorName,
                                    String organizationName,
                                    String currentUser) throws IllegalAccessException;

  /**
   * @param connectorName connector name
   * @param organizationRemoteId connector hook organization remoteId
   * @return {@link String} connector hook secret
   */
  String getConnectorHookSecret(String connectorName, long organizationRemoteId);

  /**
   * @param connectorName connector name
   * @param organizationName connector hook organization name
   * @param currentUser user name attempting to save connector hook found
   *          {@link InputStream}
   * @throws ObjectNotFoundException if connector hook wasn't found
   * @throws IllegalAccessException if user doesn't have access permission to
   *           application
   */
  InputStream getHookImageStream(String connectorName, String organizationName, String currentUser) throws IllegalAccessException,
                                                                                                    ObjectNotFoundException;
}

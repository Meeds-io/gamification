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

import io.meeds.gamification.model.ConnectorHook;
import io.meeds.gamification.service.ConnectorHookService;
import io.meeds.gamification.storage.ConnectorHookStorage;
import io.meeds.gamification.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.web.security.codec.CodecInitializer;
import org.exoplatform.web.security.security.TokenServiceInitializationException;

import java.io.InputStream;
import java.util.List;

public class ConnectorHookServiceImpl implements ConnectorHookService {

  public static final String         CONNECTOR_NAME_IS_MANDATORY         = "Connector name is mandatory";

  public static final String         HOOK_ORGANIZATION_NAME_IS_MANDATORY = "Hook organization name is mandatory";

  private final ConnectorHookStorage connectorHookStorage;

  private final CodecInitializer     codecInitializer;

  public ConnectorHookServiceImpl(ConnectorHookStorage connectorHookStorage, CodecInitializer codecInitializer) {
    this.connectorHookStorage = connectorHookStorage;
    this.codecInitializer = codecInitializer;
  }

  @Override
  public List<ConnectorHook> getConnectorHooks(String connectorName,
                                               String username,
                                               int offset,
                                               int limit) throws IllegalAccessException {
    if (!Utils.isRewardingManager(username)) {
      throw new IllegalAccessException("The user is not authorized to access connector Hooks");
    }
    if (StringUtils.isBlank(connectorName)) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    List<Long> hooksIds = connectorHookStorage.getConnectorHookIds(connectorName, offset, limit);
    return hooksIds.stream().map(connectorHookStorage::getConnectorHookById).toList();
  }

  @Override
  public ConnectorHook getConnectorHook(String connectorName, String organizationName) {
    if (StringUtils.isBlank(connectorName)) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (StringUtils.isBlank(organizationName)) {
      throw new IllegalArgumentException(HOOK_ORGANIZATION_NAME_IS_MANDATORY);
    }
    return connectorHookStorage.getConnectorHook(connectorName, organizationName);
  }

  @Override
  public ConnectorHook createConnectorHook(ConnectorHook connectorHook, String username) throws IllegalAccessException,
                                                                                         ObjectAlreadyExistsException {
    if (connectorHook == null) {
      throw new IllegalArgumentException("connector Hook object is mandatory");
    }
    if (!Utils.isRewardingManager(username)) {
      throw new IllegalAccessException("The user is not authorized to create a connector Hook");
    }
    try {
      connectorHook.setSecret(codecInitializer.getCodec().encode(connectorHook.getSecret()));
    } catch (TokenServiceInitializationException e) {
      throw new IllegalStateException("Error encrypting Secret Key", e);
    }
    return connectorHookStorage.saveConnectorAccount(connectorHook);
  }

  @Override
  public ConnectorHook deleteConnectorHook(String connectorName,
                                           String organizationName,
                                           String username) throws IllegalAccessException {
    if (!Utils.isRewardingManager(username)) {
      throw new IllegalAccessException("The user is not authorized to delete connector settings");
    }
    if (StringUtils.isBlank(connectorName)) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (StringUtils.isBlank(organizationName)) {
      throw new IllegalArgumentException(HOOK_ORGANIZATION_NAME_IS_MANDATORY);
    }
    return connectorHookStorage.deleteConnectorHook(connectorName, organizationName);
  }

  @Override
  public String getConnectorHookSecret(String connectorName, long organizationRemoteId) {
    if (StringUtils.isBlank(connectorName)) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (organizationRemoteId <= 0) {
      throw new IllegalArgumentException("organization remote technical identifier must be positive");
    }
    String encryptedSecret = connectorHookStorage.getConnectorHookSecret(connectorName, organizationRemoteId);
    if (StringUtils.isNotBlank(encryptedSecret)) {
      try {
        return codecInitializer.getCodec().decode(encryptedSecret);
      } catch (TokenServiceInitializationException e) {
        throw new IllegalStateException("Error decrypting connector hook secret", e);
      }
    }
    return null;
  }

  @Override
  public InputStream getHookImageStream(String connectorName,
                                        String organizationName,
                                        String username) throws IllegalAccessException, ObjectNotFoundException {
    if (StringUtils.isBlank(connectorName)) {
      throw new IllegalArgumentException(CONNECTOR_NAME_IS_MANDATORY);
    }
    if (StringUtils.isBlank(organizationName)) {
      throw new IllegalArgumentException(HOOK_ORGANIZATION_NAME_IS_MANDATORY);
    }
    ConnectorHook connectorHook = connectorHookStorage.getConnectorHook(connectorName, organizationName);
    if (connectorHook == null) {
      throw new ObjectNotFoundException(connectorName + "connector hook : " + organizationName + " wasn't found");
    }
    if (!Utils.isRewardingManager(username)) {
      throw new IllegalAccessException("The user is not authorized to access connector hook with id ");
    }
    if (connectorHook.getImageFileId() > 0) {
      return connectorHookStorage.getImageAsStream(connectorHook.getImageFileId());
    }
    return null;
  }
}

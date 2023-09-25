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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.security.Identity;
import org.exoplatform.web.security.codec.CodecInitializer;
import org.exoplatform.web.security.security.TokenServiceInitializationException;

import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import io.meeds.gamification.utils.Utils;

public class ConnectorSettingServiceImpl implements ConnectorSettingService {

  public static final String CONNECTOR_SETTINGS_KEY_NAME = "CONNECTOR_SETTINGS";

  private SettingService     settingService;

  private CodecInitializer   codecInitializer;

  public ConnectorSettingServiceImpl(SettingService settingService, CodecInitializer codecInitializer) {
    this.settingService = settingService;
    this.codecInitializer = codecInitializer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void saveConnectorSettings(RemoteConnectorSettings remoteConnectorSettings,
                                    Identity aclIdentity) throws IllegalAccessException {
    if (!canManageConnectorSettings(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to save or update connector settings");
    }
    Scope connectorSettingScope = Scope.APPLICATION.id(StringUtils.capitalize(remoteConnectorSettings.getName()));
    try {
      remoteConnectorSettings.setSecretKey(codecInitializer.getCodec().encode(remoteConnectorSettings.getSecretKey()));
    } catch (TokenServiceInitializationException e) {
      throw new IllegalStateException("Error encrypting Secret Key", e);
    }
    remoteConnectorSettings.setApiKey(remoteConnectorSettings.getApiKey());
    String settingsString = Utils.toJsonString(remoteConnectorSettings);

    this.settingService.set(Context.GLOBAL,
                            connectorSettingScope,
                            CONNECTOR_SETTINGS_KEY_NAME,
                            SettingValue.create(settingsString));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteConnectorSettings(String connectorName, Identity aclIdentity) throws IllegalAccessException {
    if (!canManageConnectorSettings(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to delete connector settings");
    }
    Scope connectorSettingScope = Scope.APPLICATION.id(StringUtils.capitalize(connectorName));
    this.settingService.remove(Context.GLOBAL, connectorSettingScope, CONNECTOR_SETTINGS_KEY_NAME);
  }

  @Override
  public RemoteConnectorSettings getConnectorSettings(String connectorName, Identity aclIdentity) throws IllegalAccessException {
    if (!canManageConnectorSettings(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to access connector settings");
    }
    return getConnectorSettings(connectorName);
  }

  @Override
  public RemoteConnectorSettings getConnectorSettings(String connectorName) {
    Scope connectorSettingScope = Scope.APPLICATION.id(StringUtils.capitalize(connectorName));
    SettingValue<?> settingsValue = settingService.get(Context.GLOBAL, connectorSettingScope, CONNECTOR_SETTINGS_KEY_NAME);

    String settingsValueString = settingsValue == null || settingsValue.getValue() == null ? null
                                                                                           : settingsValue.getValue().toString();
    RemoteConnectorSettings remoteConnectorSettings;
    if (settingsValueString == null) {
      remoteConnectorSettings = new RemoteConnectorSettings();
      remoteConnectorSettings.setName(connectorName);
      remoteConnectorSettings.setEnabled(false);
    } else {
      remoteConnectorSettings = Utils.fromJsonString(settingsValueString, RemoteConnectorSettings.class);
      if (remoteConnectorSettings != null) {
        remoteConnectorSettings.setSecretKey(null);
      }
    }
    return remoteConnectorSettings;
  }

  @Override
  public String getConnectorSecretKey(String connectorName) {
    Scope connectorSettingScope = Scope.APPLICATION.id(StringUtils.capitalize(connectorName));
    SettingValue<?> settingsValue = settingService.get(Context.GLOBAL, connectorSettingScope, CONNECTOR_SETTINGS_KEY_NAME);

    String settingsValueString = settingsValue == null || settingsValue.getValue() == null ? null
                                                                                           : settingsValue.getValue().toString();
    RemoteConnectorSettings remoteConnectorSettings;
    if (settingsValueString != null) {
      remoteConnectorSettings = Utils.fromJsonString(settingsValueString, RemoteConnectorSettings.class);
      String decryptedSecretKey;
      try {
        decryptedSecretKey = codecInitializer.getCodec().decode(remoteConnectorSettings.getSecretKey());
        return decryptedSecretKey;
      } catch (TokenServiceInitializationException e) {
        throw new IllegalStateException("Error decrypting connector settings", e);
      }
    }
    return null;
  }

  @Override
  public List<RemoteConnectorSettings> getConnectorsSettings(ConnectorService connectorService,
                                                             Identity aclIdentity) throws IllegalAccessException {
    if (!canManageConnectorSettings(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to access connectors settings");
    }
    return connectorService.getConnectorPlugins()
                           .stream()
                           .map(ConnectorPlugin::getConnectorName)
                           .map(this::getConnectorSettings)
                           .toList();
  }

  @Override
  public boolean canManageConnectorSettings(Identity aclIdentity) {
    return aclIdentity != null && Utils.isRewardingManager(aclIdentity.getUserId());
  }
}

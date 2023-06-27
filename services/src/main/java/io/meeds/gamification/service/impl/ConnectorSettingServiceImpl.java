package io.meeds.gamification.service.impl;

import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import io.meeds.gamification.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Identity;
import org.exoplatform.web.security.codec.CodecInitializer;
import org.exoplatform.web.security.security.TokenServiceInitializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectorSettingServiceImpl implements ConnectorSettingService {

  private static final Log   LOG                         = ExoLogger.getLogger(ConnectorSettingServiceImpl.class);

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
    String encryptedApiKey;
    String encryptedSecretKey;
    try {
      encryptedApiKey = codecInitializer.getCodec().encode(remoteConnectorSettings.getApiKey());
      encryptedSecretKey = codecInitializer.getCodec().encode(remoteConnectorSettings.getSecretKey());
      remoteConnectorSettings.setApiKey(encryptedApiKey);
      remoteConnectorSettings.setSecretKey(encryptedSecretKey);
    } catch (TokenServiceInitializationException e) {
      LOG.warn("Error generating connector settings", e);
    }
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
      String decryptedApiKey;
      String decryptedSecretKey;
      try {
        decryptedApiKey = codecInitializer.getCodec().decode(remoteConnectorSettings.getApiKey());
        decryptedSecretKey = codecInitializer.getCodec().decode(remoteConnectorSettings.getSecretKey());
        remoteConnectorSettings.setApiKey(decryptedApiKey);
        remoteConnectorSettings.setSecretKey(decryptedSecretKey);
      } catch (TokenServiceInitializationException e) {
        LOG.warn("Error decrypting {} connector settings", remoteConnectorSettings.getName(), e);
      }
    }
    return remoteConnectorSettings;
  }

  @Override
  public List<RemoteConnectorSettings> getConnectorsSettings(ConnectorService connectorService,
                                                             Identity aclIdentity) throws IllegalAccessException {
    if (!canManageConnectorSettings(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to access connectors settings");
    }
    Map<String, ConnectorPlugin> connectorsPlugins = connectorService.getConnectorPlugins();
    List<RemoteConnectorSettings> connectorSettingList = new ArrayList<>();
    connectorsPlugins.forEach((s, connectorPlugin) -> {
      connectorSettingList.add(getConnectorSettings(connectorPlugin.getConnectorName()));
    });
    return connectorSettingList;
  }

  @Override
  public boolean canManageConnectorSettings(Identity aclIdentity) {
    return aclIdentity != null && Utils.isRewardingManager(aclIdentity.getUserId());
  }
}

package io.meeds.gamification.service;

import io.meeds.gamification.model.RemoteConnectorSettings;

import java.util.List;

public interface ConnectorSettingService {

  /**
   * Save connector {@link RemoteConnectorSettings} which contains the api key,
   * secret key and connector status
   *
   * @param remoteConnectorSettings {@link RemoteConnectorSettings} To Store
   */
  void saveConnectorSettings(RemoteConnectorSettings remoteConnectorSettings);

  /**
   * Delete connector settings identified by connector name
   *
   * @param connectorName connector name
   */
  void deleteConnectorSettings(String connectorName);

  /**
   * @param connectorName connector name
   * @return {@link RemoteConnectorSettings} connector settings
   */
  RemoteConnectorSettings getConnectorSettings(String connectorName);

  /**
   * Retrieves the list of Connectors setting
   *
   * @return list of {@link RemoteConnectorSettings} connector settings
   */
  List<RemoteConnectorSettings> getConnectorsSettings(ConnectorService connectorService);
}

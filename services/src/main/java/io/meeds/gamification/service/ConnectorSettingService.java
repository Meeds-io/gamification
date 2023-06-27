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

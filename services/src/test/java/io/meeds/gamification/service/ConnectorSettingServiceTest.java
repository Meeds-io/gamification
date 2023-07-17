/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.service;

import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.test.AbstractServiceTest;
import org.exoplatform.services.security.Identity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectorSettingServiceTest extends AbstractServiceTest {

  private static final String ADMIN_USER    = "root1";

  private static final String INTERNAL_USER = "internalUser";

  private Identity            adminAclIdentity;

  private Identity            internalIdentity;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adminAclIdentity = registerAdministratorUser(ADMIN_USER);
    internalIdentity = registerInternalUser(INTERNAL_USER);
  }

  @Test
  public void testSaveConnectorSettings() throws Exception {
    RemoteConnectorSettings remoteConnectorSettings = connectorSettingService.getConnectorSettings("connectorName",
                                                                                                   adminAclIdentity);
    remoteConnectorSettings.setName("connectorName");
    remoteConnectorSettings.setApiKey("12345apiKey");
    remoteConnectorSettings.setSecretKey("12345SecretKey");
    remoteConnectorSettings.setRedirectUrl("http://localhost:8080");
    remoteConnectorSettings.setEnabled(true);

    assertThrows(IllegalAccessException.class,
                 () -> connectorSettingService.saveConnectorSettings(remoteConnectorSettings, internalIdentity));

    connectorSettingService.saveConnectorSettings(remoteConnectorSettings, adminAclIdentity);

    RemoteConnectorSettings storedRemoteConnectorSettings = connectorSettingService.getConnectorSettings("connectorName",
                                                                                                         adminAclIdentity);
    String storedSecretKet = connectorSettingService.getConnectorSecretKey(remoteConnectorSettings.getName());

    String decryptedSecretKey = codecInitializer.getCodec().decode(remoteConnectorSettings.getSecretKey());

    assertEquals(remoteConnectorSettings.getName(), storedRemoteConnectorSettings.getName());
    assertEquals(remoteConnectorSettings.getApiKey(), storedRemoteConnectorSettings.getApiKey());
    assertEquals(decryptedSecretKey, storedSecretKet);
    assertEquals(remoteConnectorSettings.getRedirectUrl(), storedRemoteConnectorSettings.getRedirectUrl());
    assertEquals(remoteConnectorSettings.isEnabled(), storedRemoteConnectorSettings.isEnabled());
  }

  @Test
  public void testGetConnectorsSettings() throws Exception {
    RemoteConnectorSettings remoteConnectorSettings1 = connectorSettingService.getConnectorSettings("connectorName1",
                                                                                                    adminAclIdentity);
    remoteConnectorSettings1.setName("connectorName1");
    remoteConnectorSettings1.setApiKey("12345Name1");
    remoteConnectorSettings1.setSecretKey("12345Name1");
    remoteConnectorSettings1.setRedirectUrl("http://localhost:8080");
    remoteConnectorSettings1.setEnabled(true);

    assertThrows(IllegalAccessException.class,
                 () -> connectorSettingService.getConnectorSettings("connectorName2", internalIdentity));

    RemoteConnectorSettings remoteConnectorSettings2 = connectorSettingService.getConnectorSettings("connectorName2",
                                                                                                    adminAclIdentity);

    remoteConnectorSettings2.setName("connectorName2");
    remoteConnectorSettings2.setApiKey("12345Name2");
    remoteConnectorSettings2.setSecretKey("12345Name2");
    remoteConnectorSettings2.setRedirectUrl("http://localhost:8080");
    remoteConnectorSettings2.setEnabled(true);

    connectorSettingService.saveConnectorSettings(remoteConnectorSettings1, adminAclIdentity);
    connectorSettingService.saveConnectorSettings(remoteConnectorSettings2, adminAclIdentity);

    ConnectorPlugin connectorPlugin1 = mock(ConnectorPlugin.class);
    when(connectorPlugin1.getConnectorName()).thenReturn("connectorName1");

    ConnectorPlugin connectorPlugin2 = mock(ConnectorPlugin.class);
    when(connectorPlugin2.getConnectorName()).thenReturn("connectorName2");

    List<ConnectorPlugin> connectorsPlugins = new ArrayList<>();
    connectorsPlugins.add(connectorPlugin1);
    connectorsPlugins.add(connectorPlugin2);
    ConnectorService connectorService = mock(ConnectorService.class);
    when(connectorService.getConnectorPlugins()).thenReturn(connectorsPlugins);

    assertThrows(IllegalAccessException.class,
                 () -> connectorSettingService.getConnectorsSettings(connectorService, internalIdentity));

    List<RemoteConnectorSettings> remoteConnectorSettingsList = connectorSettingService.getConnectorsSettings(connectorService,
                                                                                                              adminAclIdentity);
    String storedSecretKet = connectorSettingService.getConnectorSecretKey(remoteConnectorSettings2.getName());

    assertEquals(2, remoteConnectorSettingsList.size());

    RemoteConnectorSettings storedRemoteConnectorSettings2 =
                                                           remoteConnectorSettingsList.stream()
                                                                                      .filter(item -> item.getName()
                                                                                                          .equals("connectorName2"))
                                                                                      .findFirst()
                                                                                      .orElse(null);

    String decryptedSecretKey = codecInitializer.getCodec().decode(remoteConnectorSettings2.getSecretKey());
    assertEquals(remoteConnectorSettings2.getApiKey(), storedRemoteConnectorSettings2.getApiKey());
    assertEquals(decryptedSecretKey, storedSecretKet);

    // Delete connector settings
    connectorSettingService.deleteConnectorSettings("connectorName2", adminAclIdentity);

    remoteConnectorSettingsList = connectorSettingService.getConnectorsSettings(connectorService, adminAclIdentity);

    storedSecretKet = connectorSettingService.getConnectorSecretKey(remoteConnectorSettings2.getName());

    assertEquals(2, remoteConnectorSettingsList.size());

    storedRemoteConnectorSettings2 = remoteConnectorSettingsList.stream()
                                                                .filter(item -> item.getName().equals("connectorName2"))
                                                                .findFirst()
                                                                .orElse(null);

    assertNull(storedRemoteConnectorSettings2.getApiKey());
    assertNull(storedSecretKet);
  }
}

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

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import java.util.Collection;
import java.util.HashSet;

public class ConnectorServiceTest extends AbstractServiceTest {

  private static final String ADMIN_USER = "root1";

  private Identity            adminAclIdentity;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adminAclIdentity = registerAdministratorUser(ADMIN_USER);
  }

  public void testGetUserRemoteConnectors() throws IllegalAccessException {

    RemoteConnectorSettings remoteConnectorSettings = connectorSettingService.getConnectorSettings("connectorName",
                                                                                                   adminAclIdentity);
    remoteConnectorSettings.setName("connectorName");
    remoteConnectorSettings.setApiKey("12345apiKey");
    remoteConnectorSettings.setSecretKey("12345SecretKey");
    remoteConnectorSettings.setRedirectUrl("http://localhost:8080");
    remoteConnectorSettings.setEnabled(true);

    connectorSettingService.saveConnectorSettings(remoteConnectorSettings, adminAclIdentity);

    assertThrows(IllegalArgumentException.class, () -> connectorService.getConnectors(null));

    setConnectorPlugin("connectorName", "connectorIdentifier");
    setConnectorPlugin("connectorName1", "connectorIdentifier2");
    Collection<RemoteConnector> remoteConnectorList = connectorService.getConnectors("root1");

    assertEquals(2, remoteConnectorList.size());

    removeConnectorPlugin("connectorName1");
    remoteConnectorList = connectorService.getConnectors("root1");

    assertEquals(1, remoteConnectorList.size());
  }

  public void testConnectorConnection() throws ObjectAlreadyExistsException {
    ConnectorPlugin connectorPlugin = mock(ConnectorPlugin.class);
    when(connectorPlugin.getConnectorName()).thenReturn("connectorName");
    when(connectorPlugin.getName()).thenReturn("connectorName");
    String connectorUserId = "root1RemoteId";
    when(connectorPlugin.validateToken(connectorUserId, "testToken")).thenReturn(connectorUserId);
    connectorService.addPlugin(connectorPlugin);
    HashSet<MembershipEntry> memberships = new HashSet<MembershipEntry>();
    memberships.add(new MembershipEntry("/platform/users", "member"));
    Identity root1 = new Identity("root1", memberships);
    identityRegistry.register(root1);

    String result = connectorService.connect("connectorName", connectorUserId, "testToken", root1);
    assertEquals(connectorUserId, result);
    verify(connectorPlugin, times(1)).validateToken(connectorUserId, "testToken");
  }

  private void removeConnectorPlugin(String connectorName) {
    connectorService.removePlugin(connectorName);
  }

  private void setConnectorPlugin(String connectorName, String connectorIdentifier) {
    removeConnectorPlugin(connectorName);
    ConnectorPlugin connectorPlugin = new ConnectorPlugin() {
      @Override
      public String validateToken(String accessToken) {
        return null;
      }

      @Override
      public String getConnectorName() {
        return connectorName;
      }
    };
    connectorPlugin.setName(connectorName);
    connectorService.addPlugin(connectorPlugin);
  }
}

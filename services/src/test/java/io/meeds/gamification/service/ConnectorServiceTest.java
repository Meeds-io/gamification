/**
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
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ConnectorServiceTest extends AbstractServiceTest {

  public void testGetUserRemoteConnectors() {

    RemoteConnectorSettings remoteConnectorSettings = connectorSettingService.getConnectorSettings("connectorName");
    remoteConnectorSettings.setName("connectorName");
    remoteConnectorSettings.setApiKey("12345apiKey");
    remoteConnectorSettings.setSecretKey("12345SecretKey");
    remoteConnectorSettings.setRedirectUrl("http://localhost:8080");
    remoteConnectorSettings.setEnabled(true);

    connectorSettingService.saveConnectorSettings(remoteConnectorSettings);

    assertThrows(IllegalArgumentException.class, () -> connectorService.getUserRemoteConnectors(null));

    setConnectorPlugin("connectorName", "connectorIdentifier");
    setConnectorPlugin("connectorName1", "connectorIdentifier2");
    List<RemoteConnector> remoteConnectorList = connectorService.getUserRemoteConnectors("root");

    assertEquals(2, remoteConnectorList.size());

    removeConnectorPlugin("connectorName1");
    remoteConnectorList = connectorService.getUserRemoteConnectors("root");

    assertEquals(1, remoteConnectorList.size());

  }

  public void testConnectorConnection() throws IOException,
                                        ExecutionException,
                                        ObjectAlreadyExistsException,
                                        ObjectNotFoundException {
    ConnectorPlugin connectorPlugin = mock(ConnectorPlugin.class);
    when(connectorPlugin.getConnectorName()).thenReturn("connectorName");
    when(connectorPlugin.getName()).thenReturn("connectorName");
    connectorService.addPlugin(connectorPlugin);
    HashSet<MembershipEntry> memberships = new HashSet<MembershipEntry>();
    memberships.add(new MembershipEntry("/platform/users", "member"));
    Identity john = new Identity("john", memberships);
    identityRegistry.register(john);

    connectorService.connect("connectorName", "testToken", john);
    verify(connectorPlugin, times(1)).connect("testToken", john);

    connectorService.disconnect("connectorName", "john");
    verify(connectorPlugin, times(1)).disconnect("john");

  }

  private void removeConnectorPlugin(String connectorName) {
    connectorService.removePlugin(connectorName);
  }

  private void setConnectorPlugin(String connectorName, String connectorIdentifier) {
    removeConnectorPlugin(connectorName);
    ConnectorPlugin connectorPlugin = new ConnectorPlugin() {
      @Override
      public String connect(String accessToken, Identity identity) {
        return null;
      }

      @Override
      public void disconnect(String username) {

      }

      @Override
      public String getIdentifier(String username) {
        return connectorIdentifier;
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

/*
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
package io.meeds.gamification.rest;

import io.meeds.gamification.plugin.ConnectorPlugin;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.ConversationState;
import io.meeds.gamification.test.AbstractServiceTest;

import javax.ws.rs.core.MultivaluedMap;

public class TestConnectorRest extends AbstractServiceTest { // NOSONAR

  private static final String CONNECTOR_NAME = "connectorName";

  protected Class<?> getComponentClass() {
    return ConnectorRest.class;
  }

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    registry(getComponentClass());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testGetUserRemoteConnectors() throws Exception {
    startSessionAs("root1");
    ContainerResponse response = getResponse("GET", getURLResource("user/connectors?username=root1"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testConnect() throws Exception {
    startSessionAs("root1");
    setConnectorPlugin();

    byte[] formData = ("connectorName=connectorName&accessToken=accessToken").getBytes();
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    headers.putSingle("Content-Type", "application/x-www-form-urlencoded");
    ContainerResponse response = launcher.service("POST",
                                                  getURLResource("user/connectors/connectorName/connect"),
                                                  "",
                                                  headers,
                                                  formData,
                                                  null);
    assertNotNull(response);
    assertEquals(204, response.getStatus());
  }

  @Test
  public void testDisconnect() throws Exception {
    startSessionAs("root1");

    setConnectorPlugin();

    byte[] formData = ("connectorName=connectorName&accessToken=accessToken").getBytes();
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    headers.putSingle("Content-Type", "application/x-www-form-urlencoded");
    ContainerResponse response = launcher.service("POST",
                                                  getURLResource("user/connectors/connectorName/connect"),
                                                  "",
                                                  headers,
                                                  formData,
                                                  null);
    assertNotNull(response);
    assertEquals(204, response.getStatus());

    response = getResponse("DELETE", getURLResource("user/connectors/connectorName"), null);
    assertNotNull(response);
    assertEquals(204, response.getStatus());
  }

  private void removeConnectorPlugin() {
    connectorService.removePlugin(CONNECTOR_NAME);
  }

  private void setConnectorPlugin() {
    removeConnectorPlugin();
    ConnectorPlugin connectorPlugin = new ConnectorPlugin() {

      @Override
      public String validateToken(String accessToken) {
        return "connectorRemoteId";
      }

      @Override
      public String getConnectorName() {
        return CONNECTOR_NAME;
      }
    };
    connectorPlugin.setName(CONNECTOR_NAME);
    connectorService.addPlugin(connectorPlugin);
  }
}

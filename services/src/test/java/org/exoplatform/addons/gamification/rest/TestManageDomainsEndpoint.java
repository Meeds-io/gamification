/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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
package org.exoplatform.addons.gamification.rest;

import java.net.URLEncoder;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.rest.model.DomainRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.wadl.research.HTTPMethods;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;

public class TestManageDomainsEndpoint extends AbstractServiceTest { // NOSONAR

  private static final String PATH_SEPARATOR = "/";                     // NOSONAR

  private static final String TEST_USER      = "root0";

  private static final String REST_PATH      = "/gamification/domains"; // NOSONAR

  protected Class<?> getComponentClass() {
    return ManageDomainsEndpoint.class;
  }

  private DomainDTO autoDomain;

  private DomainDTO manualDomain;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    registry(getComponentClass());
    autoDomain = newDomainDTO(EntityType.AUTOMATIC, "domain1", true, Collections.singleton(1l));
    manualDomain = newDomainDTO(EntityType.MANUAL, "domain2", true, Collections.singleton(1l));
  }

  @Test
  public void testGetAllDomains() throws Exception {
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(REST_PATH, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    ContainerResponse response = launcher.service("GET", REST_PATH + "?offset=-1&limit=10", "", null, null, envctx);
    assertNotNull(response); // NOSONAR
    assertEquals(400, response.getStatus());

    response = launcher.service("GET", REST_PATH + "?offset=0&limit=-10", "", null, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = launcher.service("GET", REST_PATH, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = launcher.service("GET", REST_PATH + "?offset=0&limit=10", "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = launcher.service("GET", REST_PATH + "?offset=0&limit=10&type=MANUAL&returnSize=true", "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testCreateDomain() throws Exception {
    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(REST_PATH, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    DomainDTO domain = manualDomain.clone();
    domain.setId(0);
    domain.setTitle("foo");
    domain.setDescription("fooDescription");
    byte[] data = toJsonString(domain).getBytes();

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", REST_PATH, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = launcher.service("POST", REST_PATH, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAsAdministrator(TEST_USER);
    response = launcher.service("POST", REST_PATH, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    DomainRestEntity domainDTO = (DomainRestEntity) response.getEntity();
    assertEquals("foo", domainDTO.getTitle());
    assertEquals("fooDescription", domainDTO.getDescription());
  }

  @Test
  public void testUpdateDomain() throws Exception {
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(REST_PATH, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    DomainDTO domain = autoDomain.clone();
    domain.setId(0);
    String data = toJsonString(domain);

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length());

    ContainerResponse response = launcher.service(HTTPMethods.PUT.toString(), REST_PATH, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = launcher.service(HTTPMethods.PUT.toString(), REST_PATH, "", h, data.getBytes(), envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    domain = autoDomain.clone();
    data = toJsonString(domain);

    startSessionAs(TEST_USER);
    response = launcher.service(HTTPMethods.PUT.toString(), REST_PATH, "", h, data.getBytes(), envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    domain.setTitle("TeamWork modified");
    domain.setDescription("description modified");
    data = toJsonString(domain);
    startSessionAsAdministrator(TEST_USER);
    response = launcher.service(HTTPMethods.PUT.name(), REST_PATH, "", h, data.getBytes(), envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    DomainRestEntity domainDTO = (DomainRestEntity) response.getEntity();
    assertEquals("TeamWork modified", domainDTO.getTitle());
    assertEquals("description modified", domainDTO.getDescription());
  }

  @Test
  public void testDeleteDomain() throws Exception {
    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(REST_PATH, null, 0, HTTPMethods.DELETE.name(), null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    String restPath = REST_PATH + "/0";
    ContainerResponse response = launcher.service(HTTPMethods.DELETE.name(), restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = REST_PATH + "/1555";
    response = launcher.service(HTTPMethods.DELETE.name(), restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    startSessionAs(TEST_USER);
    restPath = REST_PATH + PATH_SEPARATOR + manualDomain.getId(); // NOSONAR
    response = launcher.service(HTTPMethods.DELETE.name(), restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAsAdministrator(TEST_USER);
    restPath = REST_PATH + PATH_SEPARATOR + autoDomain.getId(); // NOSONAR
    response = launcher.service(HTTPMethods.DELETE.name(), restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(204, response.getStatus());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testCanAddProgram() throws Exception {
    String restPath = REST_PATH + "/canAddProgram";
    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    startSessionAsAdministrator("root1");
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();

    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("true", response.getEntity());

    startSessionAs("root1");
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("false", response.getEntity());
  }

  @Test
  public void testGetDomainAvatarById() throws Exception {
    long lastUpdateCoverTime = Utils.parseRFC3339Date(manualDomain.getLastModifiedDate()).getTime();
    String token = URLEncoder.encode(Utils.generateAttachmentToken(String.valueOf(manualDomain.getId()),
                                                                   Utils.TYPE,
                                                                   lastUpdateCoverTime),
                                     "UTF-8");

    String extraPath = "/cover?lastModified="; // NOSONAR
    String restPath =
                    REST_PATH + PATH_SEPARATOR + Utils.DEFAULT_IMAGE_REMOTE_ID + extraPath + lastUpdateCoverTime + "&r=" + token;

    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    restPath = REST_PATH + PATH_SEPARATOR + 155 + extraPath + lastUpdateCoverTime + "&r=" + token;
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    restPath = REST_PATH + PATH_SEPARATOR + manualDomain.getId() + extraPath + lastUpdateCoverTime + "&r=" + token;
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    restPath = REST_PATH + PATH_SEPARATOR + manualDomain.getId() + extraPath + lastUpdateCoverTime + "&r=" + "wrongToken";
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(403, response.getStatus());
  }

  private String toJsonString(DomainDTO domain) throws JsonException {
    return new JsonGeneratorImpl().createJsonObject(domain).toString();
  }
}

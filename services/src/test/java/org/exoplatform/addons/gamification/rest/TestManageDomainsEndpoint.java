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

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.rest.model.DomainRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.wadl.research.HTTPMethods;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

public class TestManageDomainsEndpoint extends AbstractServiceTest {

  private static final Log LOG = ExoLogger.getLogger(ManageDomainsEndpoint.class);

  protected Class<?> getComponentClass() {
    return ManageDomainsEndpoint.class;
  }

  private DomainDTO autoDomain;

  private DomainDTO manualDomain;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    startSessionAs("root0");

    registry(getComponentClass());
    autoDomain = newDomainDTO(EntityType.AUTOMATIC, "domain1", true, Collections.singleton(1l));
    manualDomain = newDomainDTO(EntityType.MANUAL, "domain2", true, Collections.singleton(1l));
  }

  /**
   * Testing the Status Code
   * 
   * @throws Exception
   **/
  @Test
  public void testGetAllDomains() throws Exception {
    String restPath = "/gamification/domains/";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    restPath = "/gamification/domains?offset=-1&limit=10";
    ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/domains?offset=0&limit=-10";
    response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/domains";
    response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    LOG.info("List of domains is OK ", DomainEntity.class, response.getStatus());
    restPath = "/gamification/domains?offset=0&limit=10";
    response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    LOG.info("List of domains is OK ", DomainEntity.class, response.getStatus());

    restPath = "/gamification/domains?offset=0&limit=10&type=MANUAL&returnSize=true";
    response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    LOG.info("List of domains is OK ", DomainEntity.class, response.getStatus());

  }

  /**
   * Testing the add of a new domain with the Media Type
   * 
   * @throws Exception
   **/
  @Test
  public void testAddDomain() throws Exception {
    String restPath = "/gamification/domains/";
    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("foo")
              .key("description")
              .value("description")
              .key("createdBy")
              .value("root1")
              .key("lastModifiedBy")
              .value("root1")
              .key("enabled")
              .value(true)
              .key("deleted")
              .value(false)
              .key("owners")
              .value(Collections.singleton(5l))
              .key("type")
              .value(EntityType.MANUAL)
              .endObject();

    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());


    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("foo")
              .key("description")
              .value("description")
              .key("createdBy")
              .value("root1")
              .key("lastModifiedBy")
              .value("root1")
              .key("enabled")
              .value(true)
              .key("deleted")
              .value(false)
              .endObject();

    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    DomainRestEntity domainDTO = (DomainRestEntity) response.getEntity();
    assertEquals("foo", domainDTO.getTitle());
    assertEquals("description", domainDTO.getDescription());
  }

  /**
   * Testing the add of delete of domain with the Media Type
   * 
   * @throws Exception
   **/
  @Test
  public void testUpdateDomain() throws Exception {
    String restPath = "/gamification/domains";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(0)
              .key("title")
              .value("TeamWork modified")
              .key("description")
              .value("description modified")
              .key("createdDate")
              .value(autoDomain.getCreatedDate())
              .key("createdBy")
              .value(autoDomain.getCreatedBy())
              .key("createdDate")
              .value(autoDomain.getCreatedDate())
              .endObject();

    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);

    ContainerResponse response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(manualDomain.getId())
              .key("title")
              .value("TeamWork modified")
              .key("description")
              .value("description modified")
              .key("createdDate")
              .value(autoDomain.getCreatedDate())
              .key("createdBy")
              .value(autoDomain.getCreatedBy())
              .key("createdDate")
              .value(autoDomain.getCreatedDate())
              .key("owners")
              .value(Collections.singleton(5l))
              .key("type")
              .value(EntityType.MANUAL)
              .endObject();

    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

    response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(autoDomain.getId())
              .key("title")
              .value("TeamWork modified")
              .key("description")
              .value("description modified")
              .key("createdDate")
              .value(autoDomain.getCreatedDate())
              .key("createdBy")
              .value(autoDomain.getCreatedBy())
              .key("createdDate")
              .value(autoDomain.getCreatedDate())
              .endObject();

    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

    response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    DomainRestEntity domainDTO = (DomainRestEntity) response.getEntity();
    assertEquals("TeamWork modified", domainDTO.getTitle());
    assertEquals("description modified", domainDTO.getDescription());
  }

  /**
   * Testing the add of delete of domain with the Media Type
   * 
   * @throws Exception
   **/
  @Test
  public void testDeleteDomain() throws Exception {
    String restPath = "/gamification/domains/";
    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "DELETE", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    restPath = "/gamification/domains/0";
    ContainerResponse response = launcher.service("DELETE", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/domains/1555";
    response = launcher.service("DELETE", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    restPath = "/gamification/domains/" + manualDomain.getId();
    response = launcher.service("DELETE", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    restPath = "/gamification/domains/" + autoDomain.getId();
    response = launcher.service("DELETE", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(204, response.getStatus());
    LOG.info("Delete of domains is OK ", DomainEntity.class, response.getStatus());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testCanAddProgram() throws Exception {
    startSessionAs("root0");
    String restPath = "/gamification/domains/canAddProgram";
    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetDomainAvatarById() throws Exception {
    String lastModified = manualDomain.getLastModifiedDate();
    String token = Utils.generateAttachmentToken(String.valueOf(manualDomain.getId()), lastModified, Utils.TYPE);

    startSessionAs("root0");
    String restPath = "/gamification/domains/" + Utils.DEFAULT_IMAGE_REMOTE_ID + "/cover?lastModified" + lastModified + "&r="
        + token;

    EnvironmentContext envctx = new EnvironmentContext();

    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    restPath = "/gamification/domains/" + 155 + "/cover?lastModified" + lastModified + "&r=" + token;
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    restPath = "/gamification/domains/" + manualDomain.getId() + "/cover?lastModified" + lastModified + "&r=" + token;
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    ConversationState.setCurrent(null);
    restPath = "/gamification/domains/" + manualDomain.getId() + "/cover?lastModified" + lastModified + "&r=" + "wrongToken";
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

  }
}

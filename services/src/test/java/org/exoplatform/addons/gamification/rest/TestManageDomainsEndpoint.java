/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.security.ConversationState;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.wadl.research.HTTPMethods;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

public class TestManageDomainsEndpoint extends AbstractServiceTest {

  private static final Log LOG = ExoLogger.getLogger(ManageDomainsEndpoint.class);

  protected Class<?> getComponentClass() {
    return ManageDomainsEndpoint.class;
  }

  private DomainDTO domain;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    ConversationState.setCurrent(null);
    registry(getComponentClass());
    Date createDate = new Date(System.currentTimeMillis());
    Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
    DomainDTO domain = new DomainDTO();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_SENDER);
    domain.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain.setLastModifiedBy(TEST_USER_SENDER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    this.domain = domainService.addDomain(domain);
  }

  /**
   * Testing the Status Code
   **/
  @Test
  public void testGetAllDomains() {

    try {
      String restPath = "/gamification/domains/";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(401, response.getStatus());
      startSessionAs("root0");
       response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      LOG.info("List of domains is OK ", DomainEntity.class, response.getStatus());
      ConversationState.setCurrent(null);
    } catch (Exception e) {
      LOG.error("Cannot get list of domains", e);
    }

  }

  // Then

  /**
   * Testing the add of a new domain with the Media Type
   **/
  @Test
  public void testAddDomain() {

    try {
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
                .endObject();

      byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(401, response.getStatus());
      startSessionAs("root0");
      response = launcher.service("POST", restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());

      DomainDTO domainDTO = (DomainDTO) response.getEntity();
      assertEquals("foo", domainDTO.getTitle());
      assertEquals("description", domainDTO.getDescription());
      ConversationState.setCurrent(null);

      LOG.info("List of domains is OK ", DomainEntity.class, response.getStatus());
    } catch (Exception e) {

      LOG.error("Cannot get list of domains", e);
    }

  }
  /**
   * Testing the add of delete of domain with the Media Type
   **/
  @Test
  public void testUpdateDomain() {

    try {
      String restPath = "/gamification/domains/" + this.domain.getId();
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));

      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
              .key("id")
              .value(this.domain.getId())
              .key("title")
              .value("TeamWork modified")
              .key("description")
              .value("description modified")
              .key("createdDate")
              .value(domain.getCreatedDate())
              .key("createdBy")
              .value(domain.getCreatedBy())
              .key("createdDate")
              .value(domain.getCreatedDate())
              .endObject();

      byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);

      ContainerResponse response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(401, response.getStatus());

      startSessionAs("root0");
      response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      DomainDTO domainDTO = (DomainDTO) response.getEntity();
      assertEquals("TeamWork modified", domainDTO.getTitle());
      assertEquals("description modified", domainDTO.getDescription());

    } catch (Exception e) {
    }

  }
  /**
   * Testing the add of delete of domain with the Media Type
   **/
  @Test
  public void testDeleteDomain() {
    try {
      String restPath = "/gamification/domains/" + this.domain.getId();
      EnvironmentContext envctx = new EnvironmentContext();

      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "DELETE", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));

      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
              .key("title")
              .value("foo")
              .key("description")
              .value("description")
              .endObject();

      byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service("DELETE", restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(401, response.getStatus());
      startSessionAs("root0");
      response = launcher.service("DELETE", restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      LOG.info("Delete of domains is OK ", DomainEntity.class, response.getStatus());
      ConversationState.setCurrent(null);
    } catch (Exception e) {
      LOG.error("Cannot delete the list of domains", e);
    }

  }
}

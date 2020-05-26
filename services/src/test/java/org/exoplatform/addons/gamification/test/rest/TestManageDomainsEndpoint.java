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
package org.exoplatform.addons.gamification.test.rest;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONWriter;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.rest.ManageDomainsEndpoint;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
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



  /**
   * Testing the Status Code
   **/
  @Test
  public void testgetAllDomains() {

    try {
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageDomainsEndpoint", ManageDomainsEndpoint.class);
      String restPath = "/gamification/domains/all";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      //assertEquals(200, response.getStatus());
      LOG.info("List of domains is OK ", DomainEntity.class, response.getStatus());
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
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageDomainsEndpoint", ManageDomainsEndpoint.class);
      String restPath = "/gamification/domains/add";
      EnvironmentContext envctx = new EnvironmentContext();
      registry(getComponentClass());

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

      //assertEquals(200, response.getStatus());

      DomainDTO entity = (DomainDTO) response.getEntity();
      assertEquals("foo", entity.getTitle());
      assertEquals("description", entity.getDescription());


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
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageDomainsEndpoint", ManageDomainsEndpoint.class);
      String restPath = "/gamification/domains/update";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));

      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
              .key("title")
              .value("foo")
              .key("description")
              .value("descriptionn")

              .endObject();

      byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service(HTTPMethods.PUT.toString(), restPath, "", h, data, envctx);
      assertNotNull(response);

      DomainDTO entity = (DomainDTO) response.getEntity();
      assertEquals("foo", entity.getTitle());
      assertEquals("descriptionn", entity.getDescription());

      LOG.info("List of domains is OK ", DomainEntity.class, response.getStatus());
    } catch (Exception e) {

      LOG.error("Cannot get list of domains", e);
    }

  }
  /**
   * Testing the add of delete of domain with the Media Type
   **/
  @Test
  public void testDeleteDomain() {
    try {
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageDomainsEndpoint", ManageDomainsEndpoint.class);
      String restPath = "/gamification/domains/delete";
      EnvironmentContext envctx = new EnvironmentContext();
      registry(getComponentClass());

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

      DomainDTO entity = (DomainDTO) response.getEntity();
      assertEquals("foo", entity.getTitle());
      assertEquals("description", entity.getDescription());


      LOG.info("Delete of domains is OK ", DomainEntity.class, response.getStatus());
    } catch (Exception e) {

      LOG.error("Cannot delete the list of domains", e);
    }

  }



}

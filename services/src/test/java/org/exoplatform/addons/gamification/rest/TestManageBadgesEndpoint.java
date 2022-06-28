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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONWriter;
import org.junit.After;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.rest.ManageBadgesEndpoint;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

public class TestManageBadgesEndpoint extends AbstractServiceTest {

  private static final Log LOG = ExoLogger.getLogger(TestManageBadgesEndpoint.class);

  protected Class<?> getComponentClass() {
    return ManageBadgesEndpoint.class;
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startSessionAs("root1");
    registry(getComponentClass());
  }
  @After
  public void tearDown() {
    super.tearDown();
  }


  /**
   * Testing get All badges
   **/
  @Test
  public void testGetAllBadges() {

    try {
      String restPath = "/gamification/badges/all";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      LOG.info("List of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {
      LOG.error("Cannot get list of badges", e);
    }

  }

  /**
   * Testing the add of a new badge with the Media Type
   **/
  @Test
  public void testAddBadge() {

    try {
      String restPath = "/gamification/badges/add";
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
                .key("domain")
                .value("social")
                .endObject();
      byte[] data = writer.getBuffer().toString().getBytes("UTF-8");
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      BadgeDTO entity = (BadgeDTO) response.getEntity();
      assertEquals("foo", entity.getTitle());
      assertEquals("description", entity.getDescription());
      assertEquals("social", entity.getDomain());
      LOG.info("Adding of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {
      LOG.error("Cannot add badges", e);
    }

  }

  /**
   * Testing the add of delete of badge with the Media Type
   **/
  @Test
  public void testDeleteBadge() {
    try {
      BadgeEntity badgeEntity = newBadge();
      String restPath = "/gamification/badges/delete/" + badgeEntity.getId();
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "DELETE", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
                .key("id")
                .value(badgeEntity.getId())
                .key("title")
                .value(badgeEntity.getTitle())
                .key("description")
                .value(badgeEntity.getDescription())
                .key("domain")
                .value(badgeEntity.getDomain())
                .endObject();
      byte[] data = writer.getBuffer().toString().getBytes("UTF-8");
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service("DELETE", restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      LOG.info("Delete of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {
      LOG.error("Cannot delete the list of badges", e);
    }

  }

  /**
   * Testing the add of delete of badge with the Media Type
   **/
  @Test
  public void testUpdateBadge() {

    try {
      BadgeEntity badgeEntity = newBadge();
      Map<String, Object> ssResults = new HashMap<String, Object>();
      getContainer().registerComponentInstance("ManageBadgesEndpoint", ManageBadgesEndpoint.class);
      String restPath = "/gamification/badges/update";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      StringWriter writer = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.object()
                .key("id")
                .value(badgeEntity.getId())
                .key("title")
                .value(badgeEntity.getTitle())
                .key("description")
                .value(badgeEntity.getDescription() + "_test")
                .key("domain")
                .value(badgeEntity.getDomain())
                .endObject();
      byte[] data = writer.getBuffer().toString().getBytes("UTF-8");
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("content-type", "application/json");
      h.putSingle("content-length", "" + data.length);
      ContainerResponse response = launcher.service("PUT", restPath, "", h, data, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      BadgeDTO entity = (BadgeDTO) response.getEntity();
      assertEquals(entity.getDescription(), "Description_test");
      LOG.info("Adding of badges is OK ", BadgeEntity.class, response.getStatus());
    } catch (Exception e) {
      LOG.error("Cannot add badges", e);
    }
  }
}

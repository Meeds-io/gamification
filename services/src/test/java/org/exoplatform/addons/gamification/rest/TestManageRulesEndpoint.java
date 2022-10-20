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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.addons.gamification.rest;

import java.io.StringWriter;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class TestManageRulesEndpoint extends AbstractServiceTest {

  protected Class<?> getComponentClass() {
    return ManageRulesEndpoint.class;
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    Identity adminAclIdentity = new Identity("root", Collections.singleton(new MembershipEntry(Utils.REWARDING_GROUP)));
    identityRegistry.register(adminAclIdentity);
    registry(getComponentClass());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testGetAllRules() throws Exception {
    String restPath = "/gamification/rules";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    startSessionAs("root1");
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertEquals(200, response.getStatus());
    ConversationState.setCurrent(null);
  }

  /**
   * Testing get active rules
   **/
  @Test
  public void testGetActiveRules() throws Exception {
    String restPath = "/gamification/rules/active";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertEquals(401, response.getStatus());
    startSessionAs("root1");
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertEquals(200, response.getStatus());
    ConversationState.setCurrent(null);
  }

  /**
   * Testing the add of a new rule with the Media Type
   **/
  @Test
  public void testAddRule() throws Exception {
    String restPath = "/gamification/rules";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("foo")
              .key("event")
              .value("eventName")
              .key("area")
              .value("areaName")
              .key("description")
              .value("description")
              .key("domain")
              .value(newDomainDTO())
              .key("type")
              .value("AUTOMATIC")
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes("UTF-8");
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    startSessionAsAdministrator("root");
    ContainerResponse response = launcher.service("POST", restPath, "", h, data, envctx);
    assertEquals(200, response.getStatus());
    RuleDTO entity = (RuleDTO) response.getEntity();
    assertEquals("description", entity.getDescription());
    assertEquals("foo", entity.getTitle());
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertEquals(409, response.getStatus());
  }

  /**
   * Testing delete of rule with the Media Type
   **/
  @Test
  public void testDeleteRule() throws Exception {
    RuleEntity ruleEntity = newRule();
    String restPath = "/gamification/rules/" + ruleEntity.getId();
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "DELETE", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(ruleEntity.getId())
              .key("title")
              .value(ruleEntity.getTitle())
              .key("description")
              .value(ruleEntity.getDescription())
              .key("domain")
              .value(ruleEntity.getDomainEntity().getTitle())
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes("UTF-8");
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    startSessionAsAdministrator("root");
    ContainerResponse response = launcher.service("DELETE", restPath, "", h, data, envctx);
    assertEquals(200, response.getStatus());
    ConversationState.setCurrent(null);
  }

  /**
   * Testing the update of rule with the Media Type
   **/
  @PrepareForTest({ Utils.class })
  @Test
  public void testUpdateRule() throws Exception {
    RuleDTO ruleDTO = newRuleDTO();
    String restPath = "/gamification/rules";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(ruleDTO.getId())
              .key("title")
              .value(ruleDTO.getTitle())
              .key("description")
              .value(ruleDTO.getDescription() + "_test")
              .key("event")
              .value(ruleDTO.getTitle())
              .key("area")
              .value(ruleDTO.getDomainDTO().getTitle())
              .key("type")
              .value(ruleDTO.getType())
              .key("domain")
              .value(newDomainDTO())
              .key("createdDate")
              .value(ruleDTO.getCreatedDate())
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes("UTF-8");
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("PUT", restPath, "", h, data, envctx);
    startSessionAsAdministrator("root");
    response = launcher.service("PUT", restPath, "", h, data, envctx);
    assertEquals(200, response.getStatus());
    RuleDTO entity = (RuleDTO) response.getEntity();
    assertEquals("Description_test", entity.getDescription());
    ConversationState.setCurrent(null);
  }
}

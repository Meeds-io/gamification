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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.rest.model.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.social.core.identity.model.Identity;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

public class TestAnnouncementRest extends AbstractServiceTest {

  protected Class<?> getComponentClass() {
    return AnnouncementRest.class;
  }

  private static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                                                            // NOSONAR

  private static final String startDate       = Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 3 * MILLIS_IN_A_DAY));

  private static final String endDate         = Utils.toRFC3339Date(new Date(System.currentTimeMillis() + 3 * MILLIS_IN_A_DAY));

  private static final String date            = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    registry(getComponentClass());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testCreatAnnouncement() throws Exception {
    Identity identity = identityManager.getOrCreateUserIdentity("root1");
    long identityId = Long.parseLong(identity.getId());

    ConversationState conversationState = startSessionAs("root1");
    ProgramDTO domain = newProgram();
    domain.setOwners(Collections.singleton(identityId));
    domainService.updateProgram(domain, conversationState.getIdentity());

    RuleDTO rule = new RuleDTO();
    rule.setTitle("update challenge");
    rule.setDescription("challenge description");
    rule.setStartDate(startDate);
    rule.setEndDate(endDate);
    rule.setProgram(domain);
    rule.setEnabled(true);
    rule.setScore(10);
    rule = ruleService.createRule(rule);
    String restPath = "/gamification/announcements";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "POST", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("0")
              .key("challengeId")
              .value(rule.getId())
              .key("assignee")
              .value("1")
              .key("challengeTitle")
              .value("challengeTitle")
              .key("comment")
              .value("announcement comment")
              .key("creator")
              .value("root1")
              .key("createdDate")
              .value(date)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    ConversationState.setCurrent(null);
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root2");
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("100")
              .key("challengeId")
              .value(1000)
              .key("assignee")
              .value("1")
              .key("challengeTitle")
              .value("challengeTitle")
              .key("comment")
              .value("announcement comment")
              .key("creator")
              .value("root1")
              .key("createdDate")
              .value(date)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    // test id != 0
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    // challenge do not exist
    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("0")
              .key("challengeId")
              .value(1000)
              .key("assignee")
              .value("1")
              .key("comment")
              .value("announcement comment")
              .key("creator")
              .value("root1")
              .key("createdDate")
              .value(date)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    response = launcher.service("POST", restPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void testGetAllAnnouncementByChallenge() throws Exception {
    startSessionAs("root1");
    ProgramDTO domain = newProgram();
    RuleDTO rule = new RuleDTO();
    rule.setTitle("update challenge");
    rule.setDescription("challenge description");
    rule.setStartDate(startDate);
    rule.setEndDate(endDate);
    rule.setProgram(domain);
    rule.setEnabled(true);
    rule = ruleService.createRule(rule);
    Announcement announcement = new Announcement(0,
                                                 rule.getId(),
                                                 rule.getTitle(),
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 date,
                                                 null);
    announcementService.createAnnouncement(announcement, new HashMap<>(), "root1", false);
    String restPath = "/gamification/announcements?ruleId=1&offset=1&limit=-10";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));

    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/announcements?ruleId=1&offset=-1&limit=10";
    envctx = new EnvironmentContext();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/announcements?ruleId=-1&offset=1&limit=10";
    envctx = new EnvironmentContext();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/announcements?ruleId=" + rule.getId() + "&offset=0&limit=10";
    envctx = new EnvironmentContext();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    @SuppressWarnings("unchecked")
    List<AnnouncementRestEntity> announcementRestEntityList = (List<AnnouncementRestEntity>) response.getEntity();
    assertEquals(1, announcementRestEntityList.size());
  }

}

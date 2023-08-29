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

package io.meeds.gamification.rest;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.social.core.identity.model.Identity;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class TestAnnouncementRest extends AbstractServiceTest { // NOSONAR

  protected Class<?> getComponentClass() {
    return AnnouncementRest.class;
  }

  private static final long   MILLIS_IN_A_DAY              = 1000 * 60 * 60 * 24;                                        // NOSONAR

  private static final String START_DATE                   =
                                         Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 3 * MILLIS_IN_A_DAY));

  private static final String END_DATE                     =
                                       Utils.toRFC3339Date(new Date(System.currentTimeMillis() + 3 * MILLIS_IN_A_DAY));

  private static final String DATE                         = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));

  private static final String ANNOUNCEMENTS_REST_BASE_PATH = "/gamification/announcements";                              // NOSONAR

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

    RuleDTO rule = newRuleDTO();

    ProgramDTO program = rule.getProgram();
    program.setOwnerIds(Collections.singleton(identityId));
    programService.updateProgram(program, conversationState.getIdentity());

    rule.setTitle("update challenge");
    rule.setDescription("challenge description");
    rule.setStartDate(START_DATE);
    rule.setEndDate(END_DATE);
    rule.setEnabled(true);
    rule.setScore(10);
    rule.setType(EntityType.MANUAL);
    rule = ruleService.updateRule(rule);
    String announcementRestPath = ANNOUNCEMENTS_REST_BASE_PATH;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(announcementRestPath, null, 0, "POST", null);
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
              .value(DATE)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", announcementRestPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    ConversationState.setCurrent(null);
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root2");
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
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
              .value(DATE)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    // test id != 0
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
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
              .value(DATE)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void testCreatAnnouncementInOpenProgram() throws Exception {
    Identity identity = identityManager.getOrCreateUserIdentity("root1");
    long identityId = Long.parseLong(identity.getId());

    ConversationState conversationState = startSessionAs("root1");

    RuleDTO rule = newRuleDTO();

    ProgramDTO program = rule.getProgram();
    program.setOwnerIds(Collections.singleton(identityId));
    program.setOpen(true);
    programService.updateProgram(program, conversationState.getIdentity());

    rule.setTitle("update challenge");
    rule.setDescription("challenge description");
    rule.setStartDate(START_DATE);
    rule.setEndDate(END_DATE);
    rule.setEnabled(true);
    rule.setScore(10);
    rule.setType(EntityType.MANUAL);
    rule = ruleService.updateRule(rule);
    String announcementRestPath = ANNOUNCEMENTS_REST_BASE_PATH;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(announcementRestPath, null, 0, "POST", null);
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
              .value(DATE)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    byte[] data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    ContainerResponse response = launcher.service("POST", announcementRestPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    ConversationState.setCurrent(null);
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root2");
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    startSessionAs("root1");
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
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
              .value(DATE)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    // test id != 0
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
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
              .value(DATE)
              .key("templateParams")
              .value(new HashMap<>())
              .endObject();
    data = writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
    h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/json");
    h.putSingle("content-length", "" + data.length);
    response = launcher.service("POST", announcementRestPath, "", h, data, envctx);
    assertNotNull(response);
    assertEquals(404, response.getStatus());
  }

}

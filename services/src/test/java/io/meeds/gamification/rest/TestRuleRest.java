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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.rest;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.ConversationState;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.rest.model.ProgramWithRulesRestEntity;
import io.meeds.gamification.rest.model.RuleList;
import io.meeds.gamification.rest.model.RuleRestEntity;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

@SuppressWarnings("unchecked")
public class TestRuleRest extends AbstractServiceTest { // NOSONAR

  private static final String GAMIFICATION_RULES_REST_PATH = "/gamification/rules/";// NOSONAR

  protected Class<?> getComponentClass() {
    return RuleRest.class;
  }

  private static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                                                            // NOSONAR

  private static final String START_DATE      = Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 3 * MILLIS_IN_A_DAY));

  private static final String END_DATE        = Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 2 * MILLIS_IN_A_DAY));

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    registerAdministratorUser("root1");
    registerInternalUser("user");

    registry(getComponentClass());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testCreateRule() throws Exception {
    startSessionAs("root10");

    ProgramDTO domain = newProgram();
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("Rule")
              .key("description")
              .value("Rule description")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("10")
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    ContainerResponse response = getResponse("POST", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());
    startSessionAs("root1");
    response = getResponse("POST", getURLResource("rules"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    response = getResponse("POST", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testUpdateRule() throws Exception {
    startSessionAs("root1");
    ProgramDTO domain = newProgram();
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("Rule")
              .key("description")
              .value("Rule description")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("10")
              .key("enabled")
              .value(true)
              .key("publish")
              .value(false)
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    ContainerResponse response = getResponse("POST", getURLResource("rules"), writer.getBuffer().toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RuleRestEntity rule = (RuleRestEntity) response.getEntity();
    assertNotNull(rule);
    assertFalse(rule.isPublished());
    startSessionAs("root2");
    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("0")
              .key("title")
              .value("Rule updated")
              .key("description")
              .value("Rule description updated")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("100")
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    // null data
    response = getResponse("PUT", getURLResource("rules"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("PUT", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(rule.getId())
              .key("title")
              .value("Rule updated")
              .key("description")
              .value("Rule description updated")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("100")
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    // update with root 2
    response = getResponse("PUT", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(rule.getId())
              .key("title")
              .value("Rule updated")
              .key("description")
              .value("Rule description updated")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("100")
              .key("enabled")
              .value(true)
              .key("publish")
              .value(true)
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    // update with root 2
    response = getResponse("PUT", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");

    response = getResponse("PUT", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    rule = (RuleRestEntity) response.getEntity();
    assertNotNull(rule);
    assertTrue(rule.isPublished());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("10")
              .key("title")
              .value("Rule updated")
              .key("description")
              .value("Rule description updated")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("100")
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    response = getResponse("PUT", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void testGetRuleById() throws Exception {
    startSessionAs("root1");
    ProgramDTO domain = newProgram();
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("Rule")
              .key("description")
              .value("Rule description")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("10")
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    ContainerResponse response = getResponse("POST", getURLResource("rules"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RuleRestEntity ruleRestEntity = (RuleRestEntity) response.getEntity();
    assertNotNull(ruleRestEntity);
    startSessionAs("root2");

    response = getResponse("GET", getURLResource("rules/0"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource("rules/555"), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    startSessionAs("root1");

    response = getResponse("GET", getURLResource("rules/" + ruleRestEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RuleRestEntity savedRuleRestEntity = (RuleRestEntity) response.getEntity();
    assertNotNull(savedRuleRestEntity);
    assertEquals(ruleRestEntity.getId(), savedRuleRestEntity.getId());
    assertFalse(savedRuleRestEntity.isPublished());
  }

  @Test
  public void testGetRules() throws Exception {
    ProgramEntity domainEntity = newDomain();

    newRule("rule", domainEntity.getId());
    newRule("rule1", domainEntity.getId());

    startSessionAs("root0");
    ContainerResponse response = getResponse("GET", getURLResource("rules?returnSize=true"), null);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource("rules?returnSize=true&limit=-1"), null);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource("rules?returnSize=true&offset=-1"), null);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource("rules?returnSize=true&programId=" + domainEntity.getId()), null);
    assertEquals(200, response.getStatus());
    RuleList rules = (RuleList) response.getEntity();
    assertNotNull(rules);
    assertEquals(0, rules.getSize());

    startSessionAsAdministrator("root1");
    response = getResponse("GET", getURLResource("rules?returnSize=true&programId=" + domainEntity.getId()), null);
    assertEquals(200, response.getStatus());
    rules = (RuleList) response.getEntity();
    assertNotNull(rules);
    assertEquals(2, rules.getSize());

    response = getResponse("GET", getURLResource("rules?returnSize=true&spaceId=5555"), null);
    assertEquals(200, response.getStatus());
    rules = (RuleList) response.getEntity();
    assertNotNull(rules);
    assertEquals(0, rules.getSize());

    response = getResponse("GET",
                           getURLResource("rules?returnSize=true&spaceId=" + domainEntity.getAudienceId() + "&spaceId=5555"),
                           null);
    assertEquals(200, response.getStatus());
    rules = (RuleList) response.getEntity();
    assertNotNull(rules);
    assertEquals(2, rules.getSize());
  }

  @Test
  public void testGetRulesByUser() throws Exception {
    startSessionAs("root1");
    ProgramDTO domain = newProgram();
    String restPath = GAMIFICATION_RULES_REST_PATH;
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("Rule")
              .key("description")
              .value("Rule description")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("10")
              .key("enabled")
              .value(true)
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    ContainerResponse response = getResponse("POST", restPath, writer.getBuffer().toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RuleRestEntity rule = (RuleRestEntity) response.getEntity();
    assertNotNull(rule);
    response = getResponse("POST", restPath, writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    rule = (RuleRestEntity) response.getEntity();
    assertNotNull(rule);
    startSessionAs("root2");

    restPath = GAMIFICATION_RULES_REST_PATH + "?offset=0&limit=10";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RuleList savedRules = (RuleList) response.getEntity();
    assertEquals(0, savedRules.getSize());

    startSessionAs("root1");

    restPath = GAMIFICATION_RULES_REST_PATH + "?offset=0&limit=10&programId=" + domain.getId() + "&announcements=4";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedRules = (RuleList) response.getEntity();
    assertEquals(2, savedRules.getRules().size());

    restPath = GAMIFICATION_RULES_REST_PATH + "?offset=0&limit=10&programId=0&announcements=4&groupByProgram=true";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<ProgramWithRulesRestEntity> domainWithRules = (List<ProgramWithRulesRestEntity>) response.getEntity();
    assertEquals(1, domainWithRules.size());
    assertEquals(2, domainWithRules.get(0).getRules().size());

    restPath = GAMIFICATION_RULES_REST_PATH + "?offset=0&limit=1&returnSize=true";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedRules = (RuleList) response.getEntity();
    assertEquals(2, savedRules.getSize());
    assertEquals(1, savedRules.getRules().size());
  }

  @Test
  public void testCreateAndDeleteRule() throws Exception {
    startSessionAs("root1");
    ProgramDTO domain = newProgram();
    String restPath = GAMIFICATION_RULES_REST_PATH;
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("Rule")
              .key("description")
              .value("Rule description")
              .key("startDate")
              .value(START_DATE)
              .key("endDate")
              .value(END_DATE)
              .key("points")
              .value("10")
              .key("program")
              .object()
              .key("id")
              .value(domain.getId())
              .endObject()
              .endObject();

    ContainerResponse response = getResponse("POST", restPath, writer.getBuffer().toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RuleRestEntity rule = (RuleRestEntity) response.getEntity();
    assertNotNull(rule);
    startSessionAs("root2");

    // Rule id 0
    restPath = GAMIFICATION_RULES_REST_PATH + "0";
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    // Rule id not exist
    restPath = GAMIFICATION_RULES_REST_PATH + "1100";
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    // unauthorized user
    restPath = GAMIFICATION_RULES_REST_PATH + rule.getId();
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetAutomaticRuleById() throws Exception {
    ProgramEntity domainEntity = newDomain();

    RuleEntity ruleEntity = newRule("rule", domainEntity.getTitle(), true, EntityType.AUTOMATIC);

    String resourceURL = "rules/" + ruleEntity.getId();
    ContainerResponse response = getResponse("GET", getURLResource(resourceURL), null);
    assertEquals(401, response.getStatus());

    startSessionAs("root0");

    response = getResponse("GET", getURLResource(resourceURL), null);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");

    response = getResponse("GET", getURLResource("rules/555"), null);
    assertEquals(404, response.getStatus());

    response = getResponse("GET", getURLResource(resourceURL), null);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testAddRule() throws Exception {
    ProgramDTO program = newProgram();
    JSONObject domainData = new JSONObject();
    domainData.put("id", program.getId());
    domainData.put("title", program.getTitle());

    JSONObject data = new JSONObject();
    data.put("title", "foo");
    data.put("description", "description");
    data.put("event", "eventName");
    data.put("area", program.getTitle());
    data.put("type", "AUTOMATIC");
    data.put("program", domainData);
    data.put("enabled", "true");

    startSessionAs("root10");
    ContainerResponse response = getResponse("POST", getURLResource("rules"), data.toString());
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = getResponse("POST", getURLResource("rules"), data.toString());
    assertEquals(200, response.getStatus());
    RuleRestEntity rule = (RuleRestEntity) response.getEntity();
    assertEquals("description", rule.getDescription());
    assertEquals("foo", rule.getTitle());
    response = getResponse("POST", getURLResource("rules"), data.toString());
    assertEquals(409, response.getStatus());
    response = getResponse("POST", getURLResource("rules"), null);
    assertEquals(400, response.getStatus());
  }

  @Test
  public void testDeleteRule() throws Exception {
    RuleEntity ruleEntity = newRule();
    startSessionAs("root10");
    ContainerResponse response = getResponse("DELETE", getURLResource("rules/" + ruleEntity.getId()), null);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = getResponse("DELETE", getURLResource("rules/-1"), null);
    assertEquals(400, response.getStatus());

    response = getResponse("DELETE", getURLResource("rules/20000"), null);
    assertEquals(404, response.getStatus());

    response = getResponse("DELETE", getURLResource("rules/" + ruleEntity.getId()), null);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testUpdateAutomaticRule() throws Exception {
    RuleDTO ruleDTO = newRuleDTO();

    ProgramDTO program = newProgram();
    JSONObject domainData = new JSONObject();
    domainData.put("id", program.getId());
    domainData.put("title", program.getTitle());

    startSessionAs("root1");
    ContainerResponse response = getResponse("PUT", getURLResource("rules"), null);
    assertEquals(400, response.getStatus());

    JSONObject data = new JSONObject();
    data.put("id", 0);
    data.put("title", ruleDTO.getTitle());
    data.put("description", ruleDTO.getDescription() + "_test");

    response = getResponse("PUT", getURLResource("rules"), data.toString());
    assertEquals(404, response.getStatus());

    data = new JSONObject();
    data.put("id", ruleDTO.getId());
    data.put("title", ruleDTO.getTitle());
    data.put("description", ruleDTO.getDescription() + "_test");
    data.put("event", ruleDTO.getEvent());
    data.put("area", ruleDTO.getProgram().getTitle());
    data.put("type", ruleDTO.getType());
    data.put("program", domainData);
    data.put("createdDate", ruleDTO.getCreatedDate());

    response = getResponse("PUT", getURLResource("rules"), data.toString());
    assertEquals(200, response.getStatus());
    RuleRestEntity rule = (RuleRestEntity) response.getEntity();
    assertEquals("Description_test", rule.getDescription());

    startSessionAs("root10");
    response = getResponse("PUT", getURLResource("rules"), data.toString());
    assertEquals(401, response.getStatus());
  }
}

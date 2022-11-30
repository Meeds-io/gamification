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

import java.util.Collections;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.json.JSONObject;
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
    Identity userAclIdentity = new Identity("user", Collections.singleton(new MembershipEntry("/platform/users")));
    Identity adminAclIdentity = new Identity("root", Collections.singleton(new MembershipEntry(Utils.REWARDING_GROUP)));
    identityRegistry.register(userAclIdentity);
    identityRegistry.register(adminAclIdentity);
    registry(getComponentClass());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testGetAllRules() throws Exception {

    DomainEntity domainEntity = newDomain();

    newRule("rule", domainEntity.getTitle(), true, EntityType.AUTOMATIC);
    newRule("rule1", domainEntity.getTitle(), true, EntityType.AUTOMATIC);

    startSessionAs("user");
    ContainerResponse response = getResponse("GET", getURLResource("rules?returnSize=true"), null);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource("rules?returnSize=true&limit=-1"), null);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource("rules?returnSize=true&offset=-1"), null);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource("rules?returnSize=true&domainId=" + domainEntity.getId()), null);
    assertEquals(200, response.getStatus());
  }

  /**
   * Testing get active rules
   **/
  @Test
  public void testGetActiveRules() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("rules/active"), null);
    assertEquals(401, response.getStatus());
    startSessionAs("root1");
    response = getResponse("GET", getURLResource("rules/active"), null);
    assertEquals(200, response.getStatus());
    ConversationState.setCurrent(null);
  }

  /**
   * Testing the add of a new rule with the Media Type
   **/
  @Test
  public void testAddRule() throws Exception {
    DomainDTO domainDTO = newDomainDTO();
    JSONObject domainData = new JSONObject();
    domainData.put("id", domainDTO.getId());
    domainData.put("title", domainDTO.getTitle());

    JSONObject data = new JSONObject();
    data.put("title", "foo");
    data.put("description", "description");
    data.put("event", "eventName");
    data.put("area", domainDTO.getTitle());
    data.put("type", "AUTOMATIC");
    data.put("domainDTO", domainData);

    startSessionAs("user");
    ContainerResponse response = getResponse("POST", getURLResource("rules"), data.toString());
    assertEquals(401, response.getStatus());

    startSessionAs("root");
    response = getResponse("POST", getURLResource("rules"), data.toString());
    assertEquals(200, response.getStatus());
    RuleDTO entity = (RuleDTO) response.getEntity();
    assertEquals("description", entity.getDescription());
    assertEquals("foo", entity.getTitle());
    response = getResponse("POST", getURLResource("rules"), data.toString());
    assertEquals(409, response.getStatus());
    response = getResponse("POST", getURLResource("rules"), null);
    assertEquals(400, response.getStatus());
  }

  /**
   * Testing delete of rule with the Media Type
   **/
  @Test
  public void testDeleteRule() throws Exception {
    RuleEntity ruleEntity = newRule();
    startSessionAs("user");
    ContainerResponse response = getResponse("DELETE", getURLResource("rules/" + ruleEntity.getId()), null);
    assertEquals(401, response.getStatus());

    startSessionAs("root");
    response = getResponse("DELETE", getURLResource("rules/-1"), null);
    assertEquals(400, response.getStatus());

    response = getResponse("DELETE", getURLResource("rules/20000"), null);
    assertEquals(404, response.getStatus());

    response = getResponse("DELETE", getURLResource("rules/" + ruleEntity.getId()), null);
    assertEquals(200, response.getStatus());
  }

  /**
   * Testing the update of rule with the Media Type
   **/
  @PrepareForTest({ Utils.class })
  @Test
  public void testUpdateRule() throws Exception {
    RuleDTO ruleDTO = newRuleDTO();

    DomainDTO domainDTO = newDomainDTO();
    JSONObject domainData = new JSONObject();
    domainData.put("id", domainDTO.getId());
    domainData.put("title", domainDTO.getTitle());

    startSessionAs("root");
    ContainerResponse response = getResponse("PUT", getURLResource("rules"), null);
    assertEquals(400, response.getStatus());

    JSONObject data = new JSONObject();
    data.put("id", 0);
    data.put("title", ruleDTO.getTitle());
    data.put("description", ruleDTO.getDescription() + "_test");

    startSessionAs("root");
    response = getResponse("PUT", getURLResource("rules"), data.toString());
    assertEquals(404, response.getStatus());

    data = new JSONObject();
    data.put("id", ruleDTO.getId());
    data.put("title", ruleDTO.getTitle());
    data.put("description", ruleDTO.getDescription() + "_test");
    data.put("event", ruleDTO.getEvent());
    data.put("area", ruleDTO.getDomainDTO().getTitle());
    data.put("type", ruleDTO.getType());
    data.put("domainDTO", domainData);
    data.put("createdDate", ruleDTO.getCreatedDate());

    startSessionAs("root");
    response = getResponse("PUT", getURLResource("rules"), data.toString());
    assertEquals(200, response.getStatus());
    RuleDTO entity = (RuleDTO) response.getEntity();
    assertEquals("Description_test", entity.getDescription());

    startSessionAs("user");
    response = getResponse("PUT", getURLResource("rules"), data.toString());
    assertEquals(401, response.getStatus());
  }
}

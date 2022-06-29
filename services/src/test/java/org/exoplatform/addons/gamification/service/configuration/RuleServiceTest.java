/**
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
package org.exoplatform.addons.gamification.service.configuration;

import java.util.Collections;
import java.util.Date;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import static org.exoplatform.social.core.jpa.storage.entity.MetadataEntity_.name;

public class RuleServiceTest extends AbstractServiceTest {

  @Test
  public void testFindEnableRuleByTitle() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    DomainDTO domain = newDomainDTO();
    assertNull(ruleService.findEnableRuleByTitle(RULE_NAME));
    RuleDTO rule = new RuleDTO();
    rule.setScore(Integer.parseInt(TEST__SCORE));
    rule.setTitle("rule");
    rule.setDescription("Description");
    rule.setArea(domain.getTitle());
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setEvent("rule");
    rule.setCreatedBy(TEST_USER_SENDER);
    rule.setLastModifiedBy(TEST_USER_SENDER);
    rule.setLastModifiedDate(new Date().toString());
    rule.setDomainDTO(domain);
    rule.setType(TypeRule.AUTOMATIC);
    rule.setManagers(Collections.emptyList());
    rule = ruleService.addRule(rule);
    assertNotNull(ruleService.findEnableRuleByTitle(rule.getTitle()));
    rule.setEnabled(false);
    ruleService.updateRule(rule);
    assertNull(ruleService.findEnableRuleByTitle(rule.getTitle()));
  }

  @Test
  public void testFindRuleById() {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleService.findRuleById(ruleEntity.getId()));
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertEquals(ruleService.findEnabledRulesByEvent("rule1").size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleService.findEnabledRulesByEvent("rule1").size(), 3);
    r1.setEnabled(false);
    ruleDAO.update(r1);
    assertEquals(ruleService.findEnabledRulesByEvent("rule1").size(), 2);
  }

  @Test
  public void testFindRuleByTitle() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule();
    assertNotNull(ruleService.findRuleByTitle(RULE_NAME));
  }

  @Test
  public void getAllAutomaticRules() {
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule1", "domain3");
  }

  @Test
  public void testGetActiveRules() {
    assertEquals(ruleService.getActiveRules().size(), 0);
    newRule("rule1", "domain1", false);
    newRule("rule2", "domain2", true);
    newRule("rule3", "domain3", true);
    assertEquals(ruleService.getActiveRules().size(), 2);
  }

  @Test
  public void testGetAllRulesByDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain1");
    newRule("rule3", "domain2");
    assertEquals(ruleService.getAllRulesByDomain("domain1").size(), 2);
    assertEquals(ruleService.getAllRulesByDomain("domain2").size(), 1);
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleService.getAllRulesWithNullDomain().size(), 0);
    r1.setDomainEntity(null);
    ruleDAO.update(r1);
    assertEquals(ruleService.getAllRulesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule2", "domain3");
    assertEquals(ruleService.getAllEvents().size(), 2);
  }

  @Test
  public void testGetDomainListFromRules() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain2");
    newRule("rule3", "domain2");
    assertEquals(ruleService.getDomainListFromRules().size(), 2);
  }

  @Test
  public void testDeleteRule() {
    try {
      RuleEntity ruleEntity = newRule();
      assertEquals(ruleEntity.isDeleted(), false);
      ruleService.deleteRule(ruleEntity.getId());
      assertEquals(ruleEntity.isDeleted(), true);
    } catch (Exception e) {
      fail("Error to delete rule", e);
    }

  }

  @Test
  public void testAddRule() {
    try {
      assertEquals(ruleDAO.findAll().size(), 0);
      RuleEntity rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(RULE_NAME);
      rule.setDescription("Description");
      rule.setArea(GAMIFICATION_DOMAIN);
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(RULE_NAME);
      rule.setCreatedBy(TEST_USER_SENDER);
      rule.setCreatedDate(new Date());
      rule.setLastModifiedBy(TEST_USER_SENDER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain());
      rule.setType(TypeRule.AUTOMATIC);
      ruleService.addRule(RuleMapper.ruleToRuleDTO(rule));
      assertEquals(ruleDAO.findAll().size(), 1);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testUpdateRule() {
    try {
      assertEquals(ruleDAO.findAll().size(), 0);
      RuleEntity ruleEntity = newRule();
      ruleEntity.setDescription("new_description");
      ruleService.updateRule(RuleMapper.ruleToRuleDTO(ruleEntity));
      ruleEntity = ruleDAO.find(ruleEntity.getId());
      assertEquals(ruleEntity.getDescription(), "new_description");
    } catch (Exception e) {
      fail("Error to add rule", e);
    }

  }

  @Test
  public void getFindAllRules() {
    try {
      RuleDTO rule1 = newRuleDTO();
      RuleDTO rule2 = newRuleDTO();
      RuleDTO rule3 = newRuleDTO();
      rule1.setType(TypeRule.MANUAL);
      ruleService.updateRule(rule1);
      } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }
}

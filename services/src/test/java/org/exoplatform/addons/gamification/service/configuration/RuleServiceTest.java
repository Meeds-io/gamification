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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.exoplatform.addons.gamification.service.configuration;

import java.util.Collections;
import java.util.Date;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import static org.junit.Assert.assertThrows;

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
    rule.setType(EntityType.AUTOMATIC);
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
    assertThrows(IllegalArgumentException.class, () -> ruleService.findRuleById(0));
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> ruleService.findEnabledRulesByEvent(null));
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
    assertThrows(IllegalArgumentException.class, () -> ruleService.findRuleByTitle(null));
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
    assertThrows(IllegalArgumentException.class, () -> ruleService.getAllRulesByDomain(null));
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
  public void testDeleteRule() throws Exception {

    RuleEntity ruleEntity = newRule();
    assertFalse(ruleEntity.isDeleted());
    ruleService.deleteRule(ruleEntity.getId());
    assertTrue(ruleEntity.isDeleted());
    assertThrows(IllegalArgumentException.class, () -> ruleService.deleteRule(null));
  }

  @Test
  public void testAddRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> ruleService.addRule(null));
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
    rule.setType(EntityType.AUTOMATIC);
    ruleService.addRule(RuleMapper.ruleToRuleDTO(rule));
    assertEquals(ruleDAO.findAll().size(), 1);
  }

  @Test
  public void testUpdateRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(ObjectNotFoundException.class, () -> ruleService.updateRule(new RuleDTO()));
    RuleEntity ruleEntity = newRule();
    ruleEntity.setDescription("new_description");
    ruleService.updateRule(RuleMapper.ruleToRuleDTO(ruleEntity));
    RuleDTO ruleDTO = ruleService.findRuleById(ruleEntity.getId());
    assertEquals(ruleEntity.getDescription(), ruleDTO.getDescription());
  }

  @Test
  public void getFindAllRules() {
    newRuleDTO();
    newRuleDTO();
    newRuleDTO();
    assertEquals(2, ruleService.findAllRules(0, 2).size());
    assertEquals(3, ruleService.findAllRules(0, 3).size());
  }

  @Test
  public void testGetRulesByFilter() throws Exception {
    newRuleDTO();
    RuleDTO ruleDTO1 = newRuleDTO();
    ruleDTO1.setEnabled(false);
    ruleService.updateRule(ruleDTO1);
    RuleDTO ruleDTO2 = newRuleDTO();
    ruleDTO2.setEnabled(false);
    ruleService.updateRule(ruleDTO2);
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(1, ruleService.getRulesByFilter(ruleFilter,0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, ruleService.getRulesByFilter(ruleFilter,0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.ALL);
    DomainDTO domain = domainService.getDomainByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    ruleFilter.setDomainId(domainId);
    assertEquals(3, ruleService.getRulesByFilter(ruleFilter,0, 10).size());
    ruleFilter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRulesByFilter(ruleFilter,0, 10).size());
    ruleFilter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(3, ruleService.getRulesByFilter(ruleFilter,0, 10).size());
    assertEquals(3, ruleService.findAllRules(0, 3).size());
  }

  @Test
  public void getFindRuleByEventAndDomain() {
    RuleDTO rule = newRuleDTO();
    String ruleTitle = rule.getTitle();
    String domainTitle = rule.getDomainDTO() != null ? rule.getDomainDTO().getTitle() : " ";
    assertThrows(IllegalArgumentException.class, () -> ruleService.findRuleByEventAndDomain("", domainTitle));
    assertThrows(IllegalArgumentException.class, () -> ruleService.findRuleByEventAndDomain(ruleTitle, ""));
    RuleDTO ruleDTO = ruleService.findRuleByEventAndDomain(ruleTitle, domainTitle);
    String newDomainTitle = ruleDTO.getDomainDTO() != null ? ruleDTO.getDomainDTO().getTitle() : "";
    assertEquals(ruleTitle, ruleDTO.getTitle());
    assertEquals(domainTitle, newDomainTitle);
  }
}

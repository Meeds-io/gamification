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

package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertThrows;

public class RuleStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveRule() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule = new RuleDTO();
    rule.setScore(Integer.parseInt(TEST__SCORE));
    rule.setTitle(RULE_NAME);
    rule.setDescription("Description");
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setEvent(RULE_NAME);
    rule.setCreatedBy(TEST_USER_SENDER);
    rule.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule.setLastModifiedBy(TEST_USER_SENDER);
    rule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule.setDomainDTO(newDomainDTO());
    rule.setType(EntityType.AUTOMATIC);
    ruleStorage.saveRule(rule);
    assertEquals(ruleStorage.findAllRules().size(), 1);
  }

  @Test
  public void testFindEnableRuleByTitle() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertEquals(ruleStorage.findEnableRuleByTitle(rule.getTitle()).getTitle(), rule.getTitle());
  }

  @Test
  public void testFindRuleById() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertEquals(ruleStorage.findRuleById(rule.getId()).getTitle(), rule.getTitle());
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertEquals(ruleStorage.findEnabledRulesByEvent(rule.getEvent()).size(), 1);
    rule.setEnabled(false);
    ruleStorage.saveRule(rule);
    assertEquals(ruleStorage.findEnabledRulesByEvent(rule.getEvent()).size(), 0);
  }

  @Test
  public void testFindRuleByTitle() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertEquals(ruleStorage.findEnableRuleByTitle(rule.getTitle()).getTitle(), rule.getTitle());
  }

  @Test
  public void testFindRuleByEventAndDomain() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule = newRuleDTO();
    long domainId = rule.getDomainDTO().getId();
    assertEquals(ruleStorage.findRuleByEventAndDomain(rule.getEvent(), domainId).getTitle(), rule.getTitle());
  }

  @Test
  public void testFindAllRules() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule1 = newRuleDTO();
    RuleDTO rule2 = newRuleDTO();
    RuleDTO rule3 = newRuleDTO();
    RuleDTO manualRule = new RuleDTO();
    manualRule.setScore(Integer.parseInt(TEST__SCORE));
    manualRule.setTitle(RULE_NAME);
    manualRule.setDescription("Description");
    manualRule.setEnabled(true);
    manualRule.setDeleted(false);
    manualRule.setEvent(RULE_NAME);
    manualRule.setCreatedBy(TEST_USER_SENDER);
    manualRule.setCreatedDate(Utils.toRFC3339Date(new Date()));
    manualRule.setLastModifiedBy(TEST_USER_SENDER);
    manualRule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    manualRule.setDomainDTO(newDomainDTO());
    manualRule.setType(EntityType.MANUAL);
    ruleStorage.saveRule(manualRule);
    assertEquals(ruleStorage.findAllRules().size(), 4);
  }

  @Test
  public void testGetActiveRules() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule1 = newRuleDTO();
    RuleDTO rule2 = newRuleDTO();
    RuleDTO rule3 = newRuleDTO();
    assertEquals(ruleStorage.getActiveRules().size(), 3);
    rule1.setEnabled(false);
    ruleStorage.saveRule(rule1);
    assertEquals(ruleStorage.getActiveRules().size(), 2);
  }

  @Test
  public void testGetAllRulesByDomain() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    DomainEntity domainEntity = newDomain();
    newRule("rule1", domainEntity.getId());
    newRule("rule2", domainEntity.getId());
    newRule("rule3", domainEntity.getId());
    assertEquals(ruleStorage.getAllRulesByDomain(domainEntity.getId()).size(), 3);
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule1 = newRuleDTO();
    RuleDTO rule2 = newRuleDTO();
    RuleDTO rule3 = newRuleDTO();
    assertEquals(ruleStorage.getAllRulesWithNullDomain().size(), 0);
    rule1.setDomainDTO(null);
    ruleStorage.saveRule(rule1);
    assertEquals(ruleStorage.getAllRulesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    newRule("rule1", 1L);
    newRule("rule2", 2L);
    assertEquals(ruleStorage.getAllEvents().size(), 2);
  }

  @Test
  public void testDeleteRule() throws ObjectNotFoundException {
    assertEquals(ruleStorage.findAllRules().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertEquals(ruleStorage.findRuleById(rule.getId()).getTitle(), rule.getTitle());
    assertFalse(rule.isDeleted());
    rule.setDeleted(true);
    ruleStorage.deleteRuleById(rule.getId(), "root", false);
    rule = ruleStorage.findRuleById(rule.getId());
    assertTrue(rule.isDeleted());

    assertThrows(ObjectNotFoundException.class, () ->  ruleStorage.deleteRuleById(154l, "root", false));
  }

  @Test
  public void testFindRulesIdsByFilter() {
    DomainDTO domain1 = newDomainDTO("domain1");
    DomainDTO domain2 = newDomainDTO("domain2");
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain1.getId());
    filter.setSpaceIds(Collections.singletonList(1l));

    assertEquals(ruleStorage.findRulesIdsByFilter(filter, 0, 10).size(), 0);

    RuleDTO rule1 = new RuleDTO();
    rule1.setScore(Integer.parseInt(TEST__SCORE));
    rule1.setTitle("rule1");
    rule1.setDescription("Description");
    rule1.setAudience(1l);
    rule1.setEnabled(true);
    rule1.setDeleted(false);
    rule1.setEvent(RULE_NAME);
    rule1.setCreatedBy(TEST_USER_SENDER);
    rule1.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule1.setLastModifiedBy(TEST_USER_SENDER);
    rule1.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule1.setDomainDTO(domain1);
    rule1.setType(EntityType.MANUAL);

    RuleDTO rule2 = new RuleDTO();
    rule2.setScore(Integer.parseInt(TEST__SCORE));
    rule2.setTitle("rule2");
    rule2.setDescription("Description");
    rule2.setAudience(1l);
    rule2.setEnabled(true);
    rule2.setDeleted(false);
    rule2.setEvent(RULE_NAME);
    rule2.setCreatedBy(TEST_USER_SENDER);
    rule2.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule2.setLastModifiedBy(TEST_USER_SENDER);
    rule2.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule2.setDomainDTO(domain1);
    rule2.setType(EntityType.MANUAL);

    RuleDTO rule3 = new RuleDTO();
    rule3.setScore(Integer.parseInt(TEST__SCORE));
    rule3.setTitle("rule3");
    rule3.setDescription("Description");
    rule3.setAudience(2l);
    rule3.setEnabled(true);
    rule3.setDeleted(false);
    rule3.setEvent(RULE_NAME);
    rule3.setCreatedBy(TEST_USER_SENDER);
    rule3.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule3.setLastModifiedBy(TEST_USER_SENDER);
    rule3.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule3.setDomainDTO(domain2);
    rule3.setType(EntityType.MANUAL);

    ruleStorage.saveRule(rule1);
    ruleStorage.saveRule(rule2);
    ruleStorage.saveRule(rule3);

    assertEquals(ruleStorage.findRulesIdsByFilter(filter, 0, 10).size(), 2);

    filter.setDomainId(domain2.getId());
    filter.setSpaceIds(Collections.singletonList(2l));
    assertEquals(ruleStorage.findRulesIdsByFilter(filter, 0, 10).size(), 1);
  }

  @Test
  public void testCountRulesByFilter() {
    DomainDTO domain1 = newDomainDTO("domain1");
    DomainDTO domain2 = newDomainDTO("domain2");
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain1.getId());
    filter.setSpaceIds(Collections.singletonList(1l));

    assertEquals(ruleStorage.countRulesByFilter(filter), 0);

    RuleDTO rule1 = new RuleDTO();
    rule1.setScore(Integer.parseInt(TEST__SCORE));
    rule1.setTitle("rule1");
    rule1.setDescription("Description");
    rule1.setAudience(1l);
    rule1.setEnabled(true);
    rule1.setDeleted(false);
    rule1.setEvent(RULE_NAME);
    rule1.setCreatedBy(TEST_USER_SENDER);
    rule1.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule1.setLastModifiedBy(TEST_USER_SENDER);
    rule1.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule1.setDomainDTO(domain1);
    rule1.setType(EntityType.MANUAL);

    RuleDTO rule2 = new RuleDTO();
    rule2.setScore(Integer.parseInt(TEST__SCORE));
    rule2.setTitle("rule2");
    rule2.setDescription("Description");
    rule2.setAudience(1l);
    rule2.setEnabled(true);
    rule2.setDeleted(false);
    rule2.setEvent(RULE_NAME);
    rule2.setCreatedBy(TEST_USER_SENDER);
    rule2.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule2.setLastModifiedBy(TEST_USER_SENDER);
    rule2.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule2.setDomainDTO(domain1);
    rule2.setType(EntityType.MANUAL);

    RuleDTO rule3 = new RuleDTO();
    rule3.setScore(Integer.parseInt(TEST__SCORE));
    rule3.setTitle("rule3");
    rule3.setDescription("Description");
    rule3.setAudience(2l);
    rule3.setEnabled(true);
    rule3.setDeleted(false);
    rule3.setEvent(RULE_NAME);
    rule3.setCreatedBy(TEST_USER_SENDER);
    rule3.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule3.setLastModifiedBy(TEST_USER_SENDER);
    rule3.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule3.setDomainDTO(domain2);
    rule3.setType(EntityType.MANUAL);

    ruleStorage.saveRule(rule1);
    ruleStorage.saveRule(rule2);
    ruleStorage.saveRule(rule3);

    assertEquals(ruleStorage.countRulesByFilter(filter), 2);

    filter.setDomainId(domain2.getId());
    filter.setSpaceIds(Collections.singletonList(2l));
    assertEquals(ruleStorage.countRulesByFilter(filter), 1);
  }
}

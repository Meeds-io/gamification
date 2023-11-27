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

package io.meeds.gamification.storage;

import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class RuleStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveRule() {
    assertEquals(ruleStorage.findAllRulesIds(0, -1).size(), 0);
    RuleDTO rule = new RuleDTO();
    rule.setScore(Integer.parseInt(TEST_SCORE));
    rule.setTitle(RULE_NAME);
    rule.setDescription("Description");
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setEvent(newEventDTO(RULE_NAME));
    rule.setCreatedBy(TEST_USER_EARNER);
    rule.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule.setLastModifiedBy(TEST_USER_EARNER);
    rule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule.setProgram(newProgram());
    rule.setType(EntityType.AUTOMATIC);
    ruleStorage.saveRule(rule);
    assertEquals(ruleStorage.findAllRulesIds(0, -1).size(), 1);
  }

  @Test
  public void testFindRuleById() {
    assertEquals(ruleStorage.findAllRulesIds(0, -1).size(), 0);
    RuleDTO rule = newRuleDTO();
    assertEquals(ruleStorage.findRuleById(rule.getId()).getTitle(), rule.getTitle());
  }

  @Test
  public void testFindRuleByEventAndDomain() {
    assertEquals(ruleStorage.findAllRulesIds(0, -1).size(), 0);
    RuleDTO rule = newRuleDTO();
    long domainId = rule.getProgram().getId();
    assertEquals(ruleStorage.findActiveRuleByEventAndProgramId(rule.getEvent().getTitle(), domainId).getTitle(), rule.getTitle());
  }

  @Test
  @SuppressWarnings("unused")
  public void testFindAllRules() {
    assertEquals(ruleStorage.findAllRulesIds(0, -1).size(), 0);
    RuleDTO rule1 = newRuleDTO();
    RuleDTO rule2 = newRuleDTO();
    RuleDTO rule3 = newRuleDTO();
    RuleDTO manualRule = new RuleDTO();
    manualRule.setScore(Integer.parseInt(TEST_SCORE));
    manualRule.setTitle(RULE_NAME);
    manualRule.setDescription("Description");
    manualRule.setEnabled(true);
    manualRule.setDeleted(false);
    manualRule.setEvent(newEventDTO(RULE_NAME));
    manualRule.setCreatedBy(TEST_USER_EARNER);
    manualRule.setCreatedDate(Utils.toRFC3339Date(new Date()));
    manualRule.setLastModifiedBy(TEST_USER_EARNER);
    manualRule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    manualRule.setProgram(newProgram());
    manualRule.setType(EntityType.MANUAL);
    ruleStorage.saveRule(manualRule);
    assertEquals(ruleStorage.findAllRulesIds(0, -1).size(), 4);
  }

  @Test
  public void testDeleteRule() throws ObjectNotFoundException {
    assertEquals(ruleStorage.findAllRulesIds(0, -1).size(), 0);
    RuleDTO rule = newRuleDTO();
    assertEquals(ruleStorage.findRuleById(rule.getId()).getTitle(), rule.getTitle());
    assertFalse(rule.isDeleted());
    rule.setDeleted(true);
    ruleStorage.deleteRuleById(rule.getId(), "root");
    rule = ruleStorage.findRuleById(rule.getId());
    assertTrue(rule.isDeleted());

    assertThrows(ObjectNotFoundException.class, () ->  ruleStorage.deleteRuleById(154l, "root"));
  }

  @Test
  public void testFindRulesIdsByFilter() {
    ProgramDTO domain1 = newProgram("domain1");

    ProgramDTO domain2 = newProgram("domain2");
    domain2.setSpaceId(2l);
    domainStorage.saveProgram(domain2);

    RuleFilter filter = new RuleFilter();
    filter.setProgramId(domain1.getId());
    filter.setSpaceIds(Collections.singletonList(domain1.getSpaceId()));

    List<Long> rules = ruleStorage.findRuleIdsByFilter(filter, 0, 10);
    assertEquals(0, rules.size());

    RuleDTO rule1 = new RuleDTO();
    rule1.setScore(Integer.parseInt(TEST_SCORE));
    rule1.setTitle("rule1");
    rule1.setDescription("Description");
    rule1.setEnabled(true);
    rule1.setDeleted(false);
    rule1.setEvent(newEventDTO(RULE_NAME));
    rule1.setCreatedBy(TEST_USER_EARNER);
    rule1.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule1.setLastModifiedBy(TEST_USER_EARNER);
    rule1.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule1.setProgram(domain1);
    rule1.setType(EntityType.MANUAL);

    RuleDTO rule2 = new RuleDTO();
    rule2.setScore(Integer.parseInt(TEST_SCORE));
    rule2.setTitle("rule2");
    rule2.setDescription("Description");
    rule2.setEnabled(true);
    rule2.setDeleted(false);
    rule2.setEvent(newEventDTO(RULE_NAME));
    rule2.setCreatedBy(TEST_USER_EARNER);
    rule2.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule2.setLastModifiedBy(TEST_USER_EARNER);
    rule2.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule2.setProgram(domain1);
    rule2.setType(EntityType.MANUAL);

    RuleDTO rule3 = new RuleDTO();
    rule3.setScore(Integer.parseInt(TEST_SCORE));
    rule3.setTitle("rule3");
    rule3.setDescription("Description");
    rule3.setEnabled(true);
    rule3.setDeleted(false);
    rule3.setEvent(newEventDTO(RULE_NAME));
    rule3.setCreatedBy(TEST_USER_EARNER);
    rule3.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule3.setLastModifiedBy(TEST_USER_EARNER);
    rule3.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule3.setProgram(domain2);
    rule3.setType(EntityType.MANUAL);

    rule1 = ruleStorage.saveRule(rule1);
    rule2 = ruleStorage.saveRule(rule2);
    rule3 = ruleStorage.saveRule(rule3);

    rules = ruleStorage.findRuleIdsByFilter(filter, 0, 10);
    assertEquals(2, rules.size());
    assertEquals(rule1.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));

    filter.setProgramId(domain2.getId());
    filter.setSpaceIds(Collections.singletonList(domain2.getSpaceId()));

    rules = ruleStorage.findRuleIdsByFilter(filter, 0, 10);
    assertEquals(1, rules.size());
    assertEquals(rule3.getId(), rules.get(0));

    filter.setProgramId(domain1.getId());
    filter.setSpaceIds(Collections.singletonList(domain2.getSpaceId()));

    rules = ruleStorage.findRuleIdsByFilter(filter, 0, 10);
    assertEquals(0, rules.size());

    filter.setProgramId(0);
    filter.setSpaceIds(Collections.singletonList(domain2.getSpaceId()));

    rules = ruleStorage.findRuleIdsByFilter(filter, 0, 10);
    assertEquals(1, rules.size());
    assertEquals(rule3.getId(), rules.get(0));
  }

  @Test
  public void testCountRulesByFilter() {
    ProgramDTO domain1 = newProgram("domain1");

    ProgramDTO domain2 = newProgram("domain2");
    domain2.setSpaceId(2l);
    domainStorage.saveProgram(domain2);

    RuleFilter filter = new RuleFilter();
    filter.setProgramId(domain1.getId());
    filter.setSpaceIds(Collections.singletonList(domain1.getSpaceId()));

    assertEquals(0, ruleStorage.countRulesByFilter(filter));

    RuleDTO rule1 = new RuleDTO();
    rule1.setScore(Integer.parseInt(TEST_SCORE));
    rule1.setTitle("rule1");
    rule1.setDescription("Description");
    rule1.setEnabled(true);
    rule1.setDeleted(false);
    rule1.setEvent(newEventDTO(RULE_NAME));
    rule1.setCreatedBy(TEST_USER_EARNER);
    rule1.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule1.setLastModifiedBy(TEST_USER_EARNER);
    rule1.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule1.setProgram(domain1);
    rule1.setType(EntityType.MANUAL);

    RuleDTO rule2 = new RuleDTO();
    rule2.setScore(Integer.parseInt(TEST_SCORE));
    rule2.setTitle("rule2");
    rule2.setDescription("Description");
    rule2.setEnabled(true);
    rule2.setDeleted(false);
    rule2.setEvent(newEventDTO(RULE_NAME));
    rule2.setCreatedBy(TEST_USER_EARNER);
    rule2.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule2.setLastModifiedBy(TEST_USER_EARNER);
    rule2.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule2.setProgram(domain1);
    rule2.setType(EntityType.MANUAL);

    RuleDTO rule3 = new RuleDTO();
    rule3.setScore(Integer.parseInt(TEST_SCORE));
    rule3.setTitle("rule3");
    rule3.setDescription("Description");
    rule3.setEnabled(true);
    rule3.setDeleted(false);
    rule3.setEvent(newEventDTO(RULE_NAME));
    rule3.setCreatedBy(TEST_USER_EARNER);
    rule3.setCreatedDate(Utils.toRFC3339Date(new Date()));
    rule3.setLastModifiedBy(TEST_USER_EARNER);
    rule3.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule3.setProgram(domain2);
    rule3.setType(EntityType.MANUAL);

    ruleStorage.saveRule(rule1);
    ruleStorage.saveRule(rule2);
    ruleStorage.saveRule(rule3);

    assertEquals(2, ruleStorage.countRulesByFilter(filter));

    filter.setProgramId(domain2.getId());
    filter.setSpaceIds(Collections.singletonList(domain2.getSpaceId()));
    assertEquals(1, ruleStorage.countRulesByFilter(filter));

    filter.setProgramId(domain1.getId());
    filter.setSpaceIds(Collections.singletonList(domain2.getSpaceId()));
    assertEquals(0, ruleStorage.countRulesByFilter(filter));

    filter.setProgramId(0);
    filter.setSpaceIds(Collections.singletonList(domain2.getSpaceId()));
    assertEquals(1, ruleStorage.countRulesByFilter(filter));
  }
}

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
package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.DateFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.utils.Utils;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import java.util.Collections;
import java.util.Date;

public class RuleDAOTest extends AbstractServiceTest {

  @Test
  public void testFindEnableRuleByTitle() {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertNull(ruleDAO.findEnableRuleByTitle(RULE_NAME));
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleDAO.findEnableRuleByTitle(RULE_NAME));
    ruleEntity.setEnabled(false);
    ruleDAO.update(ruleEntity);
    assertNull(ruleDAO.findEnableRuleByTitle(RULE_NAME));
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertEquals(ruleDAO.findEnabledRulesByEvent("rule1").size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleDAO.findEnabledRulesByEvent("rule1").size(), 3);
    r1.setEnabled(false);
    ruleDAO.update(r1);
    assertEquals(ruleDAO.findEnabledRulesByEvent("rule1").size(), 2);
  }

  @Test
  public void testFindRuleByTitle() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule();
    assertNotNull(ruleDAO.findRuleByTitle(RULE_NAME));
  }

  @Test
  public void testFindRuleByEventAndDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleDAO.findRuleByEventAndDomain(ruleEntity.getEvent(), ruleEntity.getArea()));
  }

  @Test
  public void testGetActiveRules() {
    assertEquals(ruleDAO.getActiveRules().size(), 0);
    newRule("rule1", "domain1", false);
    newRule("rule2", "domain2", true);
    newRule("rule3", "domain3", true);
    assertEquals(ruleDAO.getActiveRules().size(), 2);
  }

  @Test
  public void testGetAllRulesByDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain1");
    newRule("rule3", "domain2");
    assertEquals(ruleDAO.getAllRulesByDomain("domain1").size(), 2);
    assertEquals(ruleDAO.getAllRulesByDomain("domain2").size(), 1);
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleDAO.getAllRulesWithNullDomain().size(), 0);
    r1.setDomainEntity(null);
    ruleDAO.update(r1);
    assertEquals(ruleDAO.getAllRulesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetDomainList() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain2");
    newRule("rule3", "domain2");
    assertEquals(ruleDAO.getDomainList().size(), 2);
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule2", "domain3");
    assertEquals(ruleDAO.getAllEvents().size(), 2);
  }

  @Test
  public void testGetAllRules() {
    assertEquals(ruleDAO.getAllRules().size(), 0);
    newRule("rule1", "domain1");
    assertEquals(ruleDAO.getAllRules().size(), 1);
    newRule("rule1", "domain2");
    assertEquals(ruleDAO.getAllRules().size(), 2);
    newRule("rule2", "domain3");
    assertEquals(ruleDAO.getAllRules().size(), 3);
  }

  @Test
  public void testFindRulesByFilter() {
    RuleFilter filter = new RuleFilter();
    filter.setDateFilterType(DateFilterType.ALL);
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 0);
    RuleEntity ruleEntity1 = newRule("rule1", "domain1", 1l);
    filter.setDomainId(ruleEntity1.getDomainEntity().getId());
    filter.setSpaceIds(Collections.singletonList(1l));
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 1);
    newRule("rule2", "domain1", 1l);
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 2);
    newRule("rule3", "domain3", 1l);
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 2);
    filter.setDateFilterType(DateFilterType.STARTED);
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 2);
    filter.setDateFilterType(DateFilterType.NOT_STARTED);
    RuleEntity ruleEntityNotStarted = new RuleEntity();
    ruleEntityNotStarted.setScore(Integer.parseInt(TEST__SCORE));
    ruleEntityNotStarted.setTitle("ruleEntityNotStarted");
    ruleEntityNotStarted.setDescription("ruleEntityNotStarted Description");
    ruleEntityNotStarted.setArea("domain1");
    ruleEntityNotStarted.setEnabled(true);
    ruleEntityNotStarted.setDeleted(false);
    ruleEntityNotStarted.setEvent("ruleEntityNotStarted");
    ruleEntityNotStarted.setCreatedBy(TEST_USER_SENDER);
    ruleEntityNotStarted.setLastModifiedBy(TEST_USER_SENDER);
    ruleEntityNotStarted.setLastModifiedDate(new Date());
    ruleEntityNotStarted.setDomainEntity(newDomain("domain1"));
    ruleEntityNotStarted.setType(TypeRule.MANUAL);
    ruleEntityNotStarted.setAudience(1l);
    ruleEntityNotStarted.setManagers(Collections.singletonList(1l));
    ruleEntityNotStarted.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        + 10 * MILLIS_IN_A_DAY))));
    ruleEntityNotStarted.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        + 2 * MILLIS_IN_A_DAY))));
    ruleDAO.create(ruleEntityNotStarted);
    assertEquals(1, ruleDAO.findRulesByFilter(filter, 0, 10).size());
    filter.setDateFilterType(DateFilterType.ENDED);

    RuleEntity ruleEntityEnded = new RuleEntity();
    ruleEntityEnded.setScore(Integer.parseInt(TEST__SCORE));
    ruleEntityEnded.setTitle("ruleEntityEnded");
    ruleEntityEnded.setDescription("ruleEntityEnded Description");
    ruleEntityEnded.setArea("domain1");
    ruleEntityEnded.setEnabled(true);
    ruleEntityEnded.setDeleted(false);
    ruleEntityEnded.setEvent("ruleEntityEnded");
    ruleEntityEnded.setCreatedBy(TEST_USER_SENDER);
    ruleEntityEnded.setLastModifiedBy(TEST_USER_SENDER);
    ruleEntityEnded.setLastModifiedDate(new Date());
    ruleEntityEnded.setDomainEntity(newDomain("domain1"));
    ruleEntityEnded.setType(TypeRule.MANUAL);
    ruleEntityEnded.setAudience(1l);
    ruleEntityEnded.setManagers(Collections.singletonList(1l));
    ruleEntityEnded.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        - 2 * MILLIS_IN_A_DAY))));
    ruleEntityEnded.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        - 5 * MILLIS_IN_A_DAY))));
    ruleDAO.create(ruleEntityEnded);
    assertEquals(1, ruleDAO.findRulesByFilter(filter, 0, 10).size());
  }

  @Test
  public void testCountRulesByFilter() {
    RuleFilter filter = new RuleFilter();
    assertEquals(ruleDAO.countRulesByFilter(filter), 0);
    RuleEntity ruleEntity1 = newRule("rule1", "domain1", 1l);
    filter.setDomainId(ruleEntity1.getDomainEntity().getId());
    filter.setSpaceIds(Collections.singletonList(1l));
    assertEquals(ruleDAO.countRulesByFilter(filter), 1);
    newRule("rule2", "domain1", 1l);
    assertEquals(ruleDAO.countRulesByFilter(filter), 2);
    newRule("rule3", "domain3", 1l);
    assertEquals(ruleDAO.countRulesByFilter(filter), 2);
  }

}

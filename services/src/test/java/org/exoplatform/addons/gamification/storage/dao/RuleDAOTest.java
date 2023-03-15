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

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.utils.Utils;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.DateFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    DomainEntity firstDomain = newDomain("firstDomain");
    DomainEntity secondDomain = newDomain("secondDomain");
    DomainEntity thirdDomain = newDomain("thirdDomain");
    RuleEntity r1 = newRule("rule1", firstDomain.getId());
    RuleEntity r2 = newRule("rule1", secondDomain.getId());
    RuleEntity r3 = newRule("rule1", thirdDomain.getId());
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
    long domainId = ruleEntity.getDomainEntity().getId();
    assertNotNull(ruleDAO.findRuleByEventAndDomain(ruleEntity.getEvent(), domainId));
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
    DomainEntity firstDomain = newDomain("firstDomain");
    DomainEntity secondDomain = newDomain("secondDomain");
    newRule("rule1", firstDomain.getId());
    newRule("rule2", firstDomain.getId());
    newRule("rule3", secondDomain.getId());
    assertEquals(ruleDAO.getAllRulesByDomain(firstDomain.getId()).size(), 2);
    assertEquals(ruleDAO.getAllRulesByDomain(secondDomain.getId()).size(), 1);
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    DomainEntity firstDomain = newDomain("firstDomain");
    DomainEntity secondDomain = newDomain("secondDomain");
    DomainEntity thirdDomain = newDomain("thirdDomain");
    RuleEntity r1 = newRule("rule1", firstDomain.getId());
    RuleEntity r2 = newRule("rule1", secondDomain.getId());
    RuleEntity r3 = newRule("rule1", thirdDomain.getId());
    assertEquals(ruleDAO.getAllRulesWithNullDomain().size(), 0);
    r1.setDomainEntity(null);
    ruleDAO.update(r1);
    assertEquals(ruleDAO.getAllRulesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleDAO.findAll().size(), 0);
    DomainEntity domainEntity = newDomain();
    newRule("rule1", domainEntity.getId());
    newRule("rule1", domainEntity.getId());
    newRule("rule2", domainEntity.getId());
    assertEquals(ruleDAO.getAllEvents().size(), 2);
  }

  @Test
  public void testGetAllRules() {
    assertEquals(ruleDAO.getAllRules().size(), 0);
    DomainEntity domainEntity = newDomain("domain1");
    DomainEntity domainEntity2 = newDomain("domain2");
    DomainEntity domainEntity3 = newDomain("domain3");
    newRule("rule1", domainEntity.getId());
    assertEquals(1, ruleDAO.getAllRules().size());
    newRule("rule1", domainEntity2.getId());
    assertEquals(2, ruleDAO.getAllRules().size());
    newRule("rule2", domainEntity3.getId());
    assertEquals(3, ruleDAO.getAllRules().size());
  }

  @Test
  public void testFindHighestBudgetDomainIds() {
    DomainEntity firstDomain = newDomain("firstDomain");
    DomainEntity secondDomain = newDomain("secondDomain");
    DomainEntity thirdDomain = newDomain("thirdDomain");
    RuleEntity r1 = newRule("rule1", firstDomain.getId());
    RuleEntity r2 = newRule("rule2", secondDomain.getId());
    RuleEntity r3 = newRule("rule3", thirdDomain.getId());
    r1.setScore(100);
    ruleDAO.update(r1);
    r2.setScore(60);
    ruleDAO.update(r2);
    r3.setScore(20);
    ruleDAO.update(r3);
    assertEquals(firstDomain.getId(), ruleDAO.findHighestBudgetDomainIds(0, 3).get(0));
    assertEquals(secondDomain.getId(), ruleDAO.findHighestBudgetDomainIds(0, 3).get(1));
    assertEquals(thirdDomain.getId(), ruleDAO.findHighestBudgetDomainIds(0, 3).get(2));
    r1.setEnabled(false);
    ruleDAO.update(r1);
    assertEquals(secondDomain.getId(), ruleDAO.findHighestBudgetDomainIds(0, 3).get(0));

    // find highest budget domain Ids by spaces Ids
    assertTrue(ruleDAO.findHighestBudgetDomainIdsBySpacesIds(new ArrayList<>(Collections.singleton(10L)), 0, 3).isEmpty());
    assertEquals(secondDomain.getId(),
                 ruleDAO.findHighestBudgetDomainIdsBySpacesIds(new ArrayList<>(Collections.singleton(1L)), 0, 3).get(0));
  }

  @Test
  public void testGetRulesTotalScoreByDomain() {
    DomainEntity firstDomain = newDomain("firstDomain");
    DomainEntity secondDomain = newDomain("secondDomain");
    RuleEntity r1 = newRule("rule1", firstDomain.getId());
    RuleEntity r2 = newRule("rule2", firstDomain.getId());
    RuleEntity r3 = newRule("rule3", secondDomain.getId());
    r1.setScore(100);
    ruleDAO.update(r1);
    r2.setScore(60);
    ruleDAO.update(r2);
    r3.setScore(20);
    ruleDAO.update(r3);
    assertEquals(160, ruleDAO.getRulesTotalScoreByDomain(firstDomain.getId()));
    assertEquals(20, ruleDAO.getRulesTotalScoreByDomain(secondDomain.getId()));

    r1.setEnabled(false);
    ruleDAO.update(r1);
    assertEquals(60, ruleDAO.getRulesTotalScoreByDomain(firstDomain.getId()));
  }

  @Test
  public void testExcludRuleIds() {
    RuleFilter filter = new RuleFilter();
    DomainEntity domainEntity = newDomain();
    filter.setDateFilterType(DateFilterType.ALL);
    RuleEntity ruleEntity1 = newRule("rule1", domainEntity.getId());
    filter.setDomainId(ruleEntity1.getDomainEntity().getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    newRule("rule2", domainEntity.getId());
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    List<Long> excludedIds = new ArrayList<>();
    excludedIds.add(ruleEntity1.getId());
    filter.setExcludedChallengesIds(excludedIds);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
  }

  @Test
  public void testFindRulesIdsByFilter() {
    RuleFilter filter = new RuleFilter();
    DomainEntity domainEntity1 = newDomain();
    DomainEntity domainEntity3 = newDomain();
    filter.setDateFilterType(DateFilterType.ALL);
    assertEquals(0, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    RuleEntity ruleEntity1 = newManualRule("rule1", domainEntity1.getId());
    filter.setDomainId(ruleEntity1.getDomainEntity().getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    newManualRule("rule2", domainEntity1.getId());
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    newManualRule("rule3", domainEntity3.getId());
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    filter.setDateFilterType(DateFilterType.STARTED);
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    filter.setDateFilterType(DateFilterType.NOT_STARTED);
    RuleEntity ruleEntityNotStarted = new RuleEntity();
    ruleEntityNotStarted.setScore(Integer.parseInt(TEST__SCORE));
    ruleEntityNotStarted.setTitle("ruleEntityNotStarted");
    ruleEntityNotStarted.setDescription("ruleEntityNotStarted Description");
    ruleEntityNotStarted.setEnabled(true);
    ruleEntityNotStarted.setDeleted(false);
    ruleEntityNotStarted.setEvent("ruleEntityNotStarted");
    ruleEntityNotStarted.setCreatedBy(TEST_USER_SENDER);
    ruleEntityNotStarted.setLastModifiedBy(TEST_USER_SENDER);
    ruleEntityNotStarted.setLastModifiedDate(new Date());
    ruleEntityNotStarted.setDomainEntity(domainEntity1);
    ruleEntityNotStarted.setType(EntityType.MANUAL);
    ruleEntityNotStarted.setAudience(1L);
    ruleEntityNotStarted.setManagers(Collections.singletonList(1L));
    ruleEntityNotStarted.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        + 10 * MILLIS_IN_A_DAY))));
    ruleEntityNotStarted.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        + 2 * MILLIS_IN_A_DAY))));
    ruleDAO.create(ruleEntityNotStarted);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    filter.setDateFilterType(DateFilterType.ENDED);

    RuleEntity ruleEntityEnded = new RuleEntity();
    ruleEntityEnded.setScore(Integer.parseInt(TEST__SCORE));
    ruleEntityEnded.setTitle("ruleEntityEnded");
    ruleEntityEnded.setDescription("ruleEntityEnded Description");
    ruleEntityEnded.setEnabled(true);
    ruleEntityEnded.setDeleted(false);
    ruleEntityEnded.setEvent("ruleEntityEnded");
    ruleEntityEnded.setCreatedBy(TEST_USER_SENDER);
    ruleEntityEnded.setLastModifiedBy(TEST_USER_SENDER);
    ruleEntityEnded.setLastModifiedDate(new Date());
    ruleEntityEnded.setDomainEntity(domainEntity1);
    ruleEntityEnded.setType(EntityType.MANUAL);
    ruleEntityEnded.setAudience(1L);
    ruleEntityEnded.setManagers(Collections.singletonList(1L));
    ruleEntityEnded.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        - 2 * MILLIS_IN_A_DAY))));
    ruleEntityEnded.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        - 5 * MILLIS_IN_A_DAY))));
    ruleDAO.create(ruleEntityEnded);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
  }

  @Test
  public void testCountRulesByFilter() {
    RuleFilter filter = new RuleFilter();
    DomainEntity domainEntity = newDomain();
    DomainEntity domainEntity2 = newDomain();
    assertEquals(0, ruleDAO.countRulesByFilter(filter));
    RuleEntity ruleEntity1 = newRule("rule1", domainEntity.getId());
    filter.setDomainId(ruleEntity1.getDomainEntity().getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(1, ruleDAO.countRulesByFilter(filter));
    newRule("rule2", domainEntity.getId());
    assertEquals(2, ruleDAO.countRulesByFilter(filter));
    newRule("rule3", domainEntity2.getId());
    assertEquals(2, ruleDAO.countRulesByFilter(filter));
  }

}

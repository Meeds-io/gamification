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
package io.meeds.gamification.dao;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.meeds.gamification.constant.*;
import org.junit.Test;

import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class RuleDAOTest extends AbstractServiceTest {

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
    assertNotNull(ruleDAO.findActiveRuleByEventAndProgramId(ruleEntity.getEventEntity().getTitle(), domainId));
  }

  @Test
  public void testFindHighestBudgetProgramIds() {
    ProgramEntity firstDomain = newDomain("firstDomain");
    ProgramEntity secondDomain = newDomain("secondDomain");
    ProgramEntity thirdDomain = newDomain("thirdDomain");
    RuleEntity r11 = newRule("rule1-1", firstDomain.getId());
    r11.setScore(100);
    ruleDAO.update(r11);

    RuleEntity r12 = newRule("rule1-2", firstDomain.getId());
    r12.setScore(10);
    ruleDAO.update(r12);

    RuleEntity r13 = newRule("rule1-3", firstDomain.getId());
    r13.setScore(10);
    ruleDAO.update(r13);

    RuleEntity r21 = newRule("rule2-1", secondDomain.getId());
    r21.setScore(60);
    ruleDAO.update(r21);

    RuleEntity r22 = newRule("rule2-2", secondDomain.getId());
    r22.setScore(60);
    ruleDAO.update(r22);

    RuleEntity r23 = newRule("rule2-3", secondDomain.getId());
    r23.setScore(60);
    ruleDAO.update(r23);

    RuleEntity r31 = newRule("rule3-1", thirdDomain.getId());
    r31.setScore(20);
    ruleDAO.update(r31);

    RuleEntity r32 = newRule("rule3-2", thirdDomain.getId());
    r32.setScore(200);
    ruleDAO.update(r32);

    RuleEntity r33 = newRule("rule3-3", thirdDomain.getId());
    r33.setScore(2);
    ruleDAO.update(r33);

    assertEquals(thirdDomain.getId(), ruleDAO.findHighestBudgetProgramIds(0, 1).get(0));
    assertEquals(secondDomain.getId(), ruleDAO.findHighestBudgetProgramIds(1, 1).get(0));
    assertEquals(firstDomain.getId(), ruleDAO.findHighestBudgetProgramIds(2, 1).get(0));
    r32.setEnabled(false);
    ruleDAO.update(r32);

    assertEquals(secondDomain.getId(), ruleDAO.findHighestBudgetProgramIds(0, 1).get(0));
    assertEquals(firstDomain.getId(), ruleDAO.findHighestBudgetProgramIds(1, 1).get(0));
    assertEquals(thirdDomain.getId(), ruleDAO.findHighestBudgetProgramIds(2, 1).get(0));

    // find highest budget domain Ids by spaces Ids
    assertTrue(ruleDAO.findHighestBudgetProgramIdsBySpacesIds(new ArrayList<>(Collections.singleton(10L)), 0, 3).isEmpty());
    assertEquals(secondDomain.getId(),
                 ruleDAO.findHighestBudgetProgramIdsBySpacesIds(new ArrayList<>(Collections.singleton(1L)), 0, 3).get(0));
  }

  @Test
  public void testFindHighestBudgetOpenProgramIds() {
    ProgramEntity firstDomain = newOpenProgram("firstDomain");
    ProgramEntity secondDomain = newOpenProgram("secondDomain");
    ProgramEntity thirdDomain = newOpenProgram("thirdDomain");

    newOpenRule(100, "rule1-1", firstDomain.getId());
    newOpenRule(10, "rule1-2", firstDomain.getId());
    newOpenRule(10, "rule1-3", firstDomain.getId());
    newOpenRule(60, "rule2-1", secondDomain.getId());
    newOpenRule(60, "rule2-2", secondDomain.getId());
    newOpenRule(60, "rule2-3", secondDomain.getId());
    newOpenRule(20, "rule3-1", thirdDomain.getId());
    RuleEntity r32 = newOpenRule(200, "rule3-2", thirdDomain.getId());
    newOpenRule(2, "rule3-3", thirdDomain.getId());

    assertEquals(thirdDomain.getId(), ruleDAO.findHighestBudgetProgramIds(0, 1).get(0));
    assertEquals(secondDomain.getId(), ruleDAO.findHighestBudgetProgramIds(1, 1).get(0));
    assertEquals(firstDomain.getId(), ruleDAO.findHighestBudgetProgramIds(2, 1).get(0));
    r32.setEnabled(false);
    ruleDAO.update(r32);
    assertEquals(secondDomain.getId(), ruleDAO.findHighestBudgetProgramIds(0, 1).get(0));
    assertEquals(firstDomain.getId(), ruleDAO.findHighestBudgetProgramIds(1, 1).get(0));
    assertEquals(thirdDomain.getId(), ruleDAO.findHighestBudgetProgramIds(2, 1).get(0));

    // find highest budget domain Ids by spaces Ids
    List<Long> programIds = ruleDAO.findHighestBudgetOpenProgramIds(0, 3);
    assertNotNull(programIds);
    assertEquals(3, programIds.size());
    assertEquals(secondDomain.getId(), programIds.get(0));
    assertEquals(firstDomain.getId(), programIds.get(1));
    assertEquals(thirdDomain.getId(), programIds.get(2));
  }

  @Test
  public void testGetRulesTotalScoreByDomain() {
    ProgramEntity firstDomain = newDomain("firstDomain");
    ProgramEntity secondDomain = newDomain("secondDomain");
    RuleEntity r1 = newRule("rule1", firstDomain.getId());
    RuleEntity r2 = newRule("rule2", firstDomain.getId());
    RuleEntity r3 = newRule("rule3", secondDomain.getId());
    r1.setScore(100);
    ruleDAO.update(r1);
    r2.setScore(60);
    ruleDAO.update(r2);
    r3.setScore(20);
    ruleDAO.update(r3);
    assertEquals(160, ruleDAO.getRulesTotalScoreByProgramId(firstDomain.getId()));
    assertEquals(20, ruleDAO.getRulesTotalScoreByProgramId(secondDomain.getId()));

    r1.setEnabled(false);
    ruleDAO.update(r1);
    assertEquals(60, ruleDAO.getRulesTotalScoreByProgramId(firstDomain.getId()));
  }

  @Test
  public void testExcludRuleIds() {
    RuleFilter filter = new RuleFilter();
    ProgramEntity domainEntity = newDomain();
    filter.setDateFilterType(DateFilterType.ALL);
    RuleEntity ruleEntity1 = newRule("rule1", domainEntity.getId());
    filter.setProgramId(ruleEntity1.getDomainEntity().getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    newRule("rule2", domainEntity.getId());
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    List<Long> excludedIds = new ArrayList<>();
    excludedIds.add(ruleEntity1.getId());
    filter.setExcludedRuleIds(excludedIds);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
  }

  @Test
  public void testFindRulesIdsByFilter() {
    RuleFilter filter = new RuleFilter();
    ProgramEntity domainEntity1 = newDomain();
    ProgramEntity domainEntity3 = newDomain();
    filter.setDateFilterType(DateFilterType.ALL);
    assertEquals(0, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    RuleEntity ruleEntity1 = newManualRule("rule1", domainEntity1.getId());
    filter.setProgramId(ruleEntity1.getDomainEntity().getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    newManualRule("rule2", domainEntity1.getId());
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    newManualRule("rule3", domainEntity3.getId());
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    filter.setDateFilterType(DateFilterType.STARTED);
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
    RuleEntity ruleEntityNotStarted = new RuleEntity();
    ruleEntityNotStarted.setScore(Integer.parseInt(TEST_SCORE));
    ruleEntityNotStarted.setTitle("ruleEntityNotStarted");
    ruleEntityNotStarted.setDescription("ruleEntityNotStarted Description");
    ruleEntityNotStarted.setEnabled(true);
    ruleEntityNotStarted.setDeleted(false);
    ruleEntityNotStarted.setEventEntity(newEvent("ruleEntityNotStarted"));
    ruleEntityNotStarted.setCreatedBy(TEST_USER_EARNER);
    ruleEntityNotStarted.setLastModifiedBy(TEST_USER_EARNER);
    ruleEntityNotStarted.setLastModifiedDate(new Date());
    ruleEntityNotStarted.setDomainEntity(domainEntity1);
    ruleEntityNotStarted.setType(EntityType.MANUAL);
    ruleEntityNotStarted.setDefaultRealizationStatus(RealizationStatus.ACCEPTED);
    ruleEntityNotStarted.setRecurrence(RecurrenceType.NONE);
    ruleEntityNotStarted.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        + 10 * MILLIS_IN_A_DAY))));
    ruleEntityNotStarted.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        + 2 * MILLIS_IN_A_DAY))));
    ruleDAO.create(ruleEntityNotStarted);
    filter.setDateFilterType(DateFilterType.UPCOMING);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setDateFilterType(DateFilterType.ACTIVE);
    assertEquals(3, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    RuleEntity ruleEntityEnded = new RuleEntity();
    ruleEntityEnded.setScore(Integer.parseInt(TEST_SCORE));
    ruleEntityEnded.setTitle("ruleEntityEnded");
    ruleEntityEnded.setDescription("ruleEntityEnded Description");
    ruleEntityEnded.setEnabled(true);
    ruleEntityEnded.setDeleted(false);
    ruleEntityEnded.setEventEntity(newEvent("ruleEntityEnded"));
    ruleEntityEnded.setCreatedBy(TEST_USER_EARNER);
    ruleEntityEnded.setLastModifiedBy(TEST_USER_EARNER);
    ruleEntityEnded.setLastModifiedDate(new Date());
    ruleEntityEnded.setDomainEntity(domainEntity1);
    ruleEntityEnded.setType(EntityType.MANUAL);
    ruleEntityEnded.setDefaultRealizationStatus(RealizationStatus.ACCEPTED);
    ruleEntityEnded.setRecurrence(RecurrenceType.NONE);
    ruleEntityEnded.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date())));
    ruleEntityEnded.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
        - 5 * MILLIS_IN_A_DAY))));
    ruleEntityEnded = ruleDAO.create(ruleEntityEnded);

    filter.setDateFilterType(DateFilterType.ENDED);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setDateFilterType(DateFilterType.ALL);
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(0, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(4, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setDateFilterType(DateFilterType.ENDED);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setDateFilterType(DateFilterType.STARTED_WITH_END);
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setDateFilterType(DateFilterType.STARTED);
    assertEquals(2, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setDateFilterType(DateFilterType.ALL);
    ruleEntityEnded.setEnabled(false);
    ruleDAO.update(ruleEntityEnded);

    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(3, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());

    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(1, ruleDAO.findRulesIdsByFilter(filter, 0, 10).size());
  }

  @Test
  public void testSortRulesByFilter() { // NOSONAR
    RuleFilter filter = new RuleFilter();
    filter.setAllSpaces(true);
    ProgramEntity domainEntity1 = newDomain();
    ProgramEntity domainEntity2 = newDomain();

    List<Long> rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(0, rules.size());

    RuleEntity rule1 = newRule("rule1", domainEntity1.getId());
    rule1.setScore(10);
    rule1.setStartDate(Date.from(LocalDate.now().atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC)));
    rule1.setEndDate(null);
    ruleDAO.update(rule1);

    RuleEntity rule2 = newRule("rule2", domainEntity1.getId());
    rule2.setScore(5);
    rule2.setStartDate(Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)));
    rule2.setEndDate(Date.from(LocalDate.now().atStartOfDay().plusDays(5).toInstant(ZoneOffset.UTC)));
    ruleDAO.update(rule2);

    RuleEntity rule3 = newRule("rule3", domainEntity2.getId());
    rule3.setScore(50);
    rule3.setStartDate(Date.from(LocalDate.now().atStartOfDay().minusDays(1).toInstant(ZoneOffset.UTC)));
    rule3.setEndDate(Date.from(LocalDate.now().atStartOfDay().plusDays(3).toInstant(ZoneOffset.UTC)));
    ruleDAO.update(rule3);

    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule3.getId(), rules.get(0));
    assertEquals(rule1.getId(), rules.get(1));
    assertEquals(rule2.getId(), rules.get(2));

    filter.setSortBy("Score");
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule3.getId(), rules.get(0));
    assertEquals(rule1.getId(), rules.get(1));
    assertEquals(rule2.getId(), rules.get(2));

    filter.setSortBy("Score");
    filter.setSortDescending(false);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule2.getId(), rules.get(0));
    assertEquals(rule1.getId(), rules.get(1));
    assertEquals(rule3.getId(), rules.get(2));

    filter.setSortBy("createdDate");
    filter.setSortDescending(true);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule3.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));
    assertEquals(rule1.getId(), rules.get(2));

    filter.setSortBy("createdDate");
    filter.setSortDescending(false);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule1.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));
    assertEquals(rule3.getId(), rules.get(2));

    filter.setSortBy("startDate");
    filter.setSortDescending(true);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule1.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));
    assertEquals(rule3.getId(), rules.get(2));

    filter.setSortBy("startDate");
    filter.setSortDescending(false);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule3.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));
    assertEquals(rule1.getId(), rules.get(2));
    
    filter.setSortBy("endDate");
    filter.setSortDescending(true);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule1.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));
    assertEquals(rule3.getId(), rules.get(2));

    filter.setSortBy("endDate");
    filter.setSortDescending(false);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule1.getId(), rules.get(0));
    assertEquals(rule3.getId(), rules.get(1));
    assertEquals(rule2.getId(), rules.get(2));

    filter.setSortBy("endDate");
    filter.setSortDescending(true);
    filter.setDateFilterType(DateFilterType.STARTED_WITH_END);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(2, rules.size());
    assertEquals(rule2.getId(), rules.get(0));
    assertEquals(rule3.getId(), rules.get(1));

    filter.setSortBy("title");
    filter.setSortDescending(true);
    filter.setDateFilterType(null);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule3.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));
    assertEquals(rule1.getId(), rules.get(2));

    filter.setSortBy("title");
    filter.setSortDescending(false);
    filter.setDateFilterType(null);
    rules = ruleDAO.findRulesIdsByFilter(filter, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(rule1.getId(), rules.get(0));
    assertEquals(rule2.getId(), rules.get(1));
    assertEquals(rule3.getId(), rules.get(2));
  }

  @Test
  public void testCountRulesByFilter() {
    RuleFilter filter = new RuleFilter();
    ProgramEntity domainEntity = newDomain();
    ProgramEntity domainEntity2 = newDomain();
    assertEquals(0, ruleDAO.countRulesByFilter(filter));
    newRule("rule1", domainEntity.getId());
    filter.setProgramId(domainEntity.getId());
    filter.setSpaceIds(Collections.singletonList(domainEntity.getAudienceId()));
    assertEquals(1, ruleDAO.countRulesByFilter(filter));
    newRule("rule2", domainEntity.getId());
    assertEquals(2, ruleDAO.countRulesByFilter(filter));
    newRule("rule3", domainEntity2.getId());
    assertEquals(2, ruleDAO.countRulesByFilter(filter));
  }

  private RuleEntity newOpenRule(int score, String title, Long domainId) {
    RuleEntity ruleEntity = newRule(title, domainId);
    ruleEntity.setScore(score);
    return ruleDAO.update(ruleEntity);
  }

}

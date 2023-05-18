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
package io.meeds.gamification.service;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.constant.HistoryStatus;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.Period;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class RealizationServiceITTest extends AbstractServiceTest {

  public void testCreateRealizations() {
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO ruleDTO = newRuleDTO();

    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                       TEST_USER_EARNER,
                                                                       TEST_USER_RECEIVER,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(TEST_USER_EARNER, realizationEntity.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(ruleDTO.getEvent(),
                                                        TEST_SPACE_ID,
                                                        TEST_USER_RECEIVER,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    realizationEntity = realizations.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());
  }

  public void testCreateRealizationsOnOutdatedRules() throws ObjectNotFoundException {
    assertEquals(0l, realizationDAO.count().longValue());

    RuleDTO rule = newRuleDTO();
    rule.setEndDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() - MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);

    List<RealizationDTO> realizations = realizationService.createRealizations(rule.getEvent(),
                                                                              TEST_USER_EARNER,
                                                                              TEST_USER_RECEIVER,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());
    assertEquals(0l, realizationDAO.count().longValue());

    rule.setEndDate(null);
    rule.setStartDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);
    realizations = realizationService.createRealizations(rule.getEvent(),
                                                         TEST_USER_EARNER,
                                                         TEST_USER_RECEIVER,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());
    assertEquals(0l, realizationDAO.count().longValue());

    rule.setStartDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() - 2 * MILLIS_IN_A_DAY)));
    rule.setEndDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() - MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);
    realizations = realizationService.createRealizations(rule.getEvent(),
                                                         TEST_USER_EARNER,
                                                         TEST_USER_RECEIVER,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());
    assertEquals(0l, realizationDAO.count().longValue());

    rule.setEndDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);
    realizations = realizationService.createRealizations(rule.getEvent(),
                                                         TEST_USER_EARNER,
                                                         TEST_USER_RECEIVER,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    assertEquals(1l, realizationDAO.count().longValue());

    RealizationDTO realization = realizations.get(0);

    assertEquals(TEST_USER_EARNER, realization.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, realization.getReceiver());
    assertEquals(ACTIVITY_ID, realization.getObjectId());
    assertEquals(ACTIVITY_OBJECT_TYPE, realization.getObjectType());
    assertEquals(rule.getScore(), realization.getActionScore());
  }

  public void testCreateRealizationOnOnceRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.DAILY);
    assertEquals(2, realizationDAO.count().intValue());
  }

  public void testCreateRealizationOnDailyRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.DAILY);
    assertEquals(2, realizationDAO.count().intValue());
  }

  public void testCreateRealizationOnWeeklyRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.DAILY);
    assertEquals(2, realizationDAO.count().intValue());
  }

  public void testCreateRealizationOnMonthlyRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.DAILY);
    assertEquals(2, realizationDAO.count().intValue());
  }

  public void testCreateRealizationWithPrerequisiteRule() throws ObjectNotFoundException, IllegalAccessException {
    List<RealizationEntity> realizationEntities = realizationDAO.findAll();
    assertEquals(0, realizationEntities.size());
    RuleDTO rule = newRuleDTO();

    RuleDTO prerequisiteRule1 = newRuleDTO();
    prerequisiteRule1.setEvent("prerequisiteEvent1");
    prerequisiteRule1 = ruleService.updateRule(prerequisiteRule1);

    RuleDTO prerequisiteRule2 = newRuleDTO();
    prerequisiteRule2.setEvent("prerequisiteEvent2");
    prerequisiteRule2 = ruleService.updateRule(prerequisiteRule2);

    rule.setPrerequisiteRuleIds(new HashSet<>(Arrays.asList(prerequisiteRule1.getId(), prerequisiteRule2.getId())));
    rule = ruleService.updateRule(rule);

    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(0, realizationDAO.count().intValue());

    realizationService.createRealizations(prerequisiteRule1.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationDAO.count().intValue());

    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationDAO.count().intValue());

    realizationService.createRealizations(prerequisiteRule2.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(2, realizationDAO.count().intValue());

    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(3, realizationDAO.count().intValue());

    ruleService.deleteRuleById(prerequisiteRule2.getId(), "root1");
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(4, realizationDAO.count().intValue());

    ruleService.deleteRuleById(prerequisiteRule1.getId(), "root1");
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(5, realizationDAO.count().intValue());
  }

  public void testComputeTotalScore() {
    RuleDTO ruleDTO = newRuleDTO();
    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                       TEST_USER_EARNER,
                                                                       TEST_USER_RECEIVER,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    RealizationDTO realization1 = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                        TEST_USER_EARNER,
                                                                        TEST_USER_RECEIVER,
                                                                        ACTIVITY_ID,
                                                                        ACTIVITY_OBJECT_TYPE)
                                                    .get(0);
    assertNotNull(realization1);
    assertTrue(realization1.getId() > 0);

    assertEquals(realization.getActionScore() + realization1.getActionScore(),
                 realizationDAO.getTotalScore(TEST_USER_EARNER));
  }

  public void testLeaderboardRank() {
    ProgramDTO program = newProgram();
    RuleDTO rule = newRuleDTO(RULE_NAME, program.getId());
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_USER_RECEIVER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    Date date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());

    int rankUser1 = realizationService.getLeaderboardRank(TEST_USER_EARNER, date, program.getId());
    int rankUser2 = realizationService.getLeaderboardRank(TEST_USER_RECEIVER, date, program.getId());
    assertEquals(1, rankUser1);
    assertEquals(2, rankUser2);

    int rankSpace2 = realizationService.getLeaderboardRank(TEST_SPACE2_ID, date, program.getId());
    int rankSpace1 = realizationService.getLeaderboardRank(TEST_SPACE_ID, date, program.getId());
    assertEquals(1, rankSpace2);
    assertEquals(2, rankSpace1);
  }

  public void testFindLatestActionHistoryBySocialId() {
    RuleDTO ruleDTO = newRuleDTO();
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    RealizationDTO lastRealization = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                           TEST_USER_EARNER,
                                                                           TEST_USER_RECEIVER,
                                                                           ACTIVITY_ID,
                                                                           ACTIVITY_OBJECT_TYPE)
                                                       .get(0);

    RealizationDTO realization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertEquals(lastRealization.getActionScore(), realization.getActionScore());
    assertEquals(lastRealization.getActionTitle(), realization.getActionTitle());
    assertEquals(lastRealization.getProgram(), realization.getProgram());
    assertEquals(lastRealization.getGlobalScore(), realization.getGlobalScore());
    assertEquals(lastRealization.getObjectId(), realization.getObjectId());
    assertEquals(lastRealization.getReceiver(), realization.getReceiver());
    assertEquals(lastRealization.getEarnerId(), realization.getEarnerId());
    assertEquals(lastRealization.getCreatedBy(), realization.getCreatedBy());
  }

  public void testFindUserReputationBySocialId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(realizationService.getScoreByIdentityId(TEST_USER_EARNER), 0);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertTrue(realizationService.getScoreByIdentityId(TEST_USER_EARNER) > 0);
    assertTrue(realizationService.getScoreByIdentityId(TEST_SPACE_ID) > 0);
  }

  public void testBuildDomainScoreByUserId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(0, realizationService.getScorePerDomainByIdentityId(TEST_USER_EARNER).size());
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationService.getScorePerDomainByIdentityId(TEST_USER_EARNER).size());
  }

  public void testFilterByDomainId() {
    RuleDTO ruleDTO = newRuleDTO();
    LeaderboardFilter filter = new LeaderboardFilter();
    filter.setPeriod(Period.ALL.name());
    filter.setIdentityType(IdentityType.USER);
    filter.setLoadCapacity(limit);
    filter.setDomainId(ruleDTO.getProgram().getId());
    List<StandardLeaderboard> filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(0, filteredLeaderboard.size());

    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(1, filteredLeaderboard.size());
    StandardLeaderboard userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.WEEK.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.MONTH.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setIdentityType(IdentityType.SPACE);
    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(0, filteredLeaderboard.size());
  }

  public void testDeleteHistory() {
    ProgramDTO program = newProgram();
    RuleDTO ruleDTO = newRuleDTO(RULE_NAME, program.getId());
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_RECEIVER,
                                          TEST_USER_EARNER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    RealizationDTO realization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertEquals(HistoryStatus.ACCEPTED.name(), realization.getStatus());

    realization = realizationService.findLatestRealizationByIdentityId(TEST_USER_RECEIVER);
    assertEquals(HistoryStatus.ACCEPTED.name(), realization.getStatus());

    realizationService.deleteRealizations(ACTIVITY_ID, ACTIVITY_OBJECT_TYPE);

    realization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertEquals(HistoryStatus.DELETED.name(), realization.getStatus());

    realization = realizationService.findLatestRealizationByIdentityId(TEST_USER_RECEIVER);
    assertEquals(HistoryStatus.DELETED.name(), realization.getStatus());
  }

  private void makeRecurrenceTypeChecks(RecurrenceType recurrenceType) throws ObjectNotFoundException {
    List<RealizationEntity> realizationEntities = realizationDAO.findAll();
    assertEquals(0, realizationEntities.size());
    RuleDTO ruleDTO = newRuleDTO();
    ruleDTO.setRecurrence(recurrenceType);
    ruleDTO = ruleService.updateRule(ruleDTO);

    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                       TEST_USER_EARNER,
                                                                       TEST_USER_RECEIVER,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizationEntities = realizationDAO.findAll();
    assertEquals(1, realizationEntities.size());

    List<RealizationDTO> realizations = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                              TEST_USER_EARNER,
                                                                              TEST_USER_RECEIVER,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());

    realizationEntities = realizationDAO.findAll();
    assertEquals(1, realizationEntities.size());

    RealizationEntity realizationEntity = realizationEntities.get(0);
    realizationEntity.setCreatedDate(new Date(recurrenceType.getPeriodStartDate().getTime() - 1));

    realizationDAO.update(realizationEntity);

    realizations = realizationService.createRealizations(ruleDTO.getEvent(),
                                                         TEST_SPACE_ID,
                                                         TEST_USER_RECEIVER,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());

    realizationEntities = realizationDAO.findAll();
    assertEquals(2, realizationEntities.size());
  }
}

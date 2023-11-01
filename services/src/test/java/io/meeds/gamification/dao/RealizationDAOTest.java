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

package io.meeds.gamification.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class RealizationDAOTest extends AbstractServiceTest {

  @Test
  public void testFindAllActionsHistoryAgnostic() {
    assertEquals(realizationDAO.findRealizationsAgnostic(IdentityType.USER).size(), 0);
    ProgramEntity domainEntity = newDomain();
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<StandardLeaderboard> leaderboardList = realizationDAO.findRealizationsAgnostic(IdentityType.USER);
    assertEquals(1, leaderboardList.size());
    assertEquals(TEST_USER_EARNER, leaderboardList.get(0).getEarnerId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 3L, leaderboardList.get(0).getReputationScore());
    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    leaderboardList = realizationDAO.findRealizationsAgnostic(IdentityType.USER);
    assertEquals(1, leaderboardList.size());
    assertEquals(TEST_USER_EARNER, leaderboardList.get(0).getEarnerId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 2L, leaderboardList.get(0).getReputationScore());
  }

  @Test
  public void testFindActionHistoryByActionTitleAndEarnerIdAndReceiverAndObjectId() {
    ProgramEntity domainEntity = newDomain();
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    assertNotNull(realizationDAO.findLastReadlizationByRuleIdAndEarnerIdAndReceiverAndObjectId(realizationEntity.getRuleEntity().getId(),
                                                                                               TEST_USER_EARNER,
                                                                                               TEST_USER_EARNER,
                                                                                               "objectId",
                                                                                               "objectType"));
  }

  @Test
  public void testFindAllActionsHistoryByDateByDomain() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(0,
                 realizationDAO.findRealizationsByDateAndProgramId(IdentityType.USER, fromDate, domainEntity.getId())
                               .size());
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<StandardLeaderboard> leaderboardList = realizationDAO
                                                              .findRealizationsByDateAndProgramId(IdentityType.USER,
                                                                                               fromDate,
                                                                                               domainEntity.getId());
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_EARNER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3L);
    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    leaderboardList = realizationDAO.findRealizationsAgnostic(IdentityType.USER);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_EARNER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 2L);
  }

  @Test
  public void testFindAllActionsHistoryByDomain() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(0,
                 realizationDAO.findRealizationsByProgramId(IdentityType.USER, domainEntity.getId()).size());
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<StandardLeaderboard> leaderboardList = realizationDAO
                                                              .findRealizationsByProgramId(IdentityType.USER,
                                                                                        domainEntity.getId());
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_EARNER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3L);
    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    leaderboardList = realizationDAO.findRealizationsAgnostic(IdentityType.USER);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_EARNER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 2L);
  }

  @Test
  public void testFindAllActionsHistoryByDate() {
    assertEquals(realizationDAO.findRealizationsByDate(IdentityType.USER, fromDate).size(), 0);
    ProgramEntity domainEntity = newDomain();
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<StandardLeaderboard> leaderboardList = realizationDAO
                                                              .findRealizationsByDate(IdentityType.USER, fromDate);
    assertEquals(1, leaderboardList.size());
    assertEquals(TEST_USER_EARNER, leaderboardList.get(0).getEarnerId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 3L, leaderboardList.get(0).getReputationScore());
    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    leaderboardList = realizationDAO.findRealizationsByDate(IdentityType.USER, fromDate);
    assertEquals(1, leaderboardList.size());
    assertEquals(TEST_USER_EARNER, leaderboardList.get(0).getEarnerId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 2, leaderboardList.get(0).getReputationScore());
  }

  @Test
  public void testFindAllActionsHistory() {
    assertEquals(realizationDAO.findRealizations(IdentityType.USER, limit).size(), 0);
    ProgramEntity domainEntity = newDomain();
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<StandardLeaderboard> leaderboardList = realizationDAO.findRealizations(IdentityType.USER, limit);
    assertEquals(1, leaderboardList.size());
    assertEquals(TEST_USER_EARNER, leaderboardList.get(0).getEarnerId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 3L, leaderboardList.get(0).getReputationScore());
    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    leaderboardList = realizationDAO.findRealizationsByDate(IdentityType.USER, fromDate);
    assertEquals(1, leaderboardList.size());
    assertEquals(TEST_USER_EARNER, leaderboardList.get(0).getEarnerId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 2L, leaderboardList.get(0).getReputationScore());
  }

  @Test
  public void testFindActionsHistoryByDateByDomain() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(realizationDAO
                               .findRealizationsByDateByProgramId(fromDate, IdentityType.USER, domainEntity.getId(), limit)
                               .size(),
                 0);
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<StandardLeaderboard> leaderboardList = realizationDAO.findRealizationsByDateByProgramId(fromDate,
                                                                                              IdentityType.USER,
                                                                                              domainEntity.getId(),
                                                                                              limit);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_EARNER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3L);
  }

  @Test
  public void testFindStatsByUserId() {
    assertEquals(realizationDAO.findStatsByUserId(TEST_USER_EARNER, fromDate, toDate).size(), 0);
    ProgramEntity domainEntity = newDomain();
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<PiechartLeaderboard> pieChartLeaderboardList = realizationDAO.findStatsByUserId(TEST_USER_EARNER,
                                                                                         fromDate,
                                                                                         toDate);
    assertEquals(pieChartLeaderboardList.size(), 1);
    PiechartLeaderboard piechartLeaderboard = pieChartLeaderboardList.get(0);
    ProgramDTO program = programService.getProgramById(piechartLeaderboard.getProgramId());
    assertNotNull(program);
    assertEquals(GAMIFICATION_DOMAIN, program.getTitle());
  }

  @Test
  public void testGetAllPointsByDomain() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(0, realizationDAO.getAllPointsByDomain(domainEntity.getId()).size());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule2", domainEntity.getId());
    newRealizationEntity("rule3", domainEntity.getId());
    assertEquals(limit, realizationDAO.getAllPointsByDomain(domainEntity.getId()).size());
  }

  @Test
  public void testFindDomainScoreByIdentityId() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(realizationDAO.getScorePerProgramByIdentityId(TEST_USER_EARNER).size(), 0);
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    assertEquals(1, realizationDAO.getScorePerProgramByIdentityId(TEST_USER_EARNER).size());
    assertEquals(Integer.parseInt(TEST__SCORE) * 3L,
                 realizationDAO.getScorePerProgramByIdentityId(TEST_USER_EARNER).get(0).getScore());
  }

  @Test
  public void testFindUserReputationScoreBetweenDate() {
    assertEquals(realizationDAO.findUserReputationScoreBetweenDate(TEST_USER_EARNER, fromDate, toDate), 0);
    ProgramEntity domainEntity = newDomain();
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 3L,
                 realizationDAO.findUserReputationScoreBetweenDate(TEST_USER_EARNER, fromDate, toDate));
    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    assertEquals(Integer.parseInt(TEST__SCORE) * 2L,
                 realizationDAO.findUserReputationScoreBetweenDate(TEST_USER_EARNER, fromDate, toDate));
  }

  @Test
  public void testFindUsersReputationScoreBetweenDate() {

    List<String> earnersId = new ArrayList<>();
    earnersId.add(TEST_USER_EARNER);
    Map<Long, Long> scores = realizationDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
    assertEquals(Long.valueOf(0), java.util.Optional.ofNullable(scores.get(Long.parseLong(TEST_USER_EARNER))).orElse(0L));
    ProgramEntity domainEntity = newDomain();
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());

    scores = realizationDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
    Long expected = Integer.parseInt(TEST__SCORE) * 3L;
    assertEquals(expected, java.util.Optional.ofNullable(scores.get(Long.parseLong(TEST_USER_EARNER))).orElse(0L));

    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    expected = Integer.parseInt(TEST__SCORE) * 2L;
    scores = realizationDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
    assertEquals(expected, java.util.Optional.ofNullable(scores.get(Long.parseLong(TEST_USER_EARNER))).orElse(0L));
  }

  @Test
  public void testFindUserReputationScoreByMonth() {
    assertEquals(realizationDAO.findUserReputationScoreByMonth(TEST_USER_EARNER, fromDate), 0);
    ProgramEntity domainEntity = newDomain();
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 3L,
                 realizationDAO.findUserReputationScoreByMonth(TEST_USER_EARNER, fromDate));
  }

  @Test
  public void testFindUserReputationScoreByDomainBetweenDate() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(realizationDAO.findUserReputationScoreByDomainBetweenDate(TEST_USER_EARNER,
                                                                           domainEntity.getId(),
                                                                           fromDate,
                                                                           toDate),
                 0);
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    assertEquals(realizationDAO.findUserReputationScoreByDomainBetweenDate(TEST_USER_EARNER,
                                                                           domainEntity.getId(),
                                                                           fromDate,
                                                                           toDate),
                 Integer.parseInt(TEST__SCORE) * 3);
  }

  @Test
  public void testFindActionsHistoryByEarnerIdSortedByDate() {
    assertEquals(realizationDAO.findRealizationsByIdentityIdSortedByDate(TEST_USER_EARNER, limit).size(), 0);
    ProgramEntity domainEntity = newDomain();
    RealizationEntity realizationEntity = newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    assertEquals(limit, realizationDAO.findRealizationsByIdentityIdSortedByDate(TEST_USER_EARNER, limit).size());
    realizationEntity.setStatus(RealizationStatus.REJECTED);
    realizationDAO.update(realizationEntity);
    assertEquals(limit - 1, realizationDAO.findRealizationsByIdentityIdSortedByDate(TEST_USER_EARNER, limit).size());
  }

  @Test
  public void testFindAllLeaderboardBetweenDate() {
    assertEquals(realizationDAO.findAllLeaderboardBetweenDate(IdentityType.USER, fromDate, toDate).size(), 0);
    ProgramEntity domainEntity = newDomain();
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    List<StandardLeaderboard> leaderboardList = realizationDAO.findAllLeaderboardBetweenDate(IdentityType.USER,
                                                                                             fromDate,
                                                                                             toDate);
    assertEquals(1, leaderboardList.size());
    assertEquals(TEST_USER_EARNER, leaderboardList.get(0).getEarnerId());
    assertEquals(Integer.parseInt(TEST__SCORE) * 3L, leaderboardList.get(0).getReputationScore());
  }

  @Test
  public void testFindAllRealizationsByFilterSortByActionType() {
    RuleEntity rule1Automatic = newRule("testFindRealizationsByFilterSortByActionType1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Automatic = newRule("testFindRealizationsByFilterSortByActionType2", "domain2", true, EntityType.AUTOMATIC);
    RuleEntity rule3Manual = newRule("testFindRealizationsByFilterSortByActionType3", "domain3", true, EntityType.MANUAL);

    List<RealizationEntity> histories = new ArrayList<>();
    histories.add(newRealizationEntityWithRuleId(rule1Automatic.getEvent(), rule1Automatic.getId()));
    histories.add(newRealizationEntityWithRuleId("", rule3Manual.getId()));
    histories.add(newRealizationEntityWithRuleId("", rule1Automatic.getId()));
    histories.add(newRealizationEntityWithRuleId(rule3Manual.getEvent(), rule3Manual.getId()));
    histories.add(newRealizationEntityWithRuleId("", rule2Automatic.getId()));
    histories.add(newRealizationEntityWithRuleId(rule2Automatic.getEvent(), rule2Automatic.getId()));
    histories.add(newRealizationEntityWithRuleId("", rule3Manual.getId()));

    RealizationFilter dateFilter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("type");
    dateFilter.setSortDescending(true);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setEarnerType(IdentityType.getType(""));
    dateFilter.setProgramIds(domainIds);
    List<RealizationEntity> result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(6).getId(), histories.get(3).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());

    result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 4);
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals(Arrays.asList(histories.get(6).getId(),
                               histories.get(3).getId(),
                               histories.get(1).getId(),
                               histories.get(5).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());

    dateFilter.setSortDescending(false);
    result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(5).getId(), histories.get(4).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());

    result = realizationDAO.findRealizationsByFilter(dateFilter, 2, 6);
    assertNotNull(result);
    assertEquals(5, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(),
                               histories.get(0).getId(),
                               histories.get(6).getId(),
                               histories.get(3).getId(),
                               histories.get(1).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());
  }

  @Test
  public void testFindAllRealizationsByFilter() {
    // testing findRealizationsByFilter when only dates given
    RealizationFilter dateFilter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("date");
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setEarnerType(IdentityType.getType(""));
    dateFilter.setProgramIds(domainIds);
    // Test default Sort field = 'date' with sort descending = false
    List<RealizationEntity> filteredRealizations = realizationDAO.findRealizationsByFilter(dateFilter,
                                                                                           offset,
                                                                                           limit);
    assertEquals(0, filteredRealizations.size());

    List<RealizationEntity> createdActionHistories = new ArrayList<>();
    ProgramEntity domainEntity = newDomain();
    long domainId = domainEntity.getId();
    for (int i = 0; i < limit * 2; i++) {
      createdActionHistories.add(newRealizationEntity("rule", domainId));
    }

    filteredRealizations = realizationDAO.findRealizationsByFilter(dateFilter, offset, limit);
    assertEquals(limit, filteredRealizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(RealizationEntity::getId)
                                       .toList(),
                 filteredRealizations.stream()
                                     .map(RealizationEntity::getId)
                                     .toList());

    // Test explicit Sort field = 'date' with sort descending = false
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(false);
    filteredRealizations = realizationDAO.findRealizationsByFilter(dateFilter, offset, limit);
    assertEquals(limit, filteredRealizations.size());
    assertEquals(createdActionHistories.subList(0, 3)
                                       .stream()
                                       .map(RealizationEntity::getId)
                                       .toList(),
                 filteredRealizations.stream()
                                     .map(RealizationEntity::getId)
                                     .toList());

    // Test explicit Sort field = 'date' with sort descending = true
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(true);

    filteredRealizations = realizationDAO.findRealizationsByFilter(dateFilter, offset, limit);
    Collections.reverse(createdActionHistories);
    assertEquals(createdActionHistories.subList(0, 3)
                                       .stream()
                                       .map(RealizationEntity::getId)
                                       .toList(),
                 filteredRealizations.stream()
                                     .map(RealizationEntity::getId)
                                     .toList());
  }

  @Test
  public void testFindRealizationsByFilterSortByActionTypeInDateRange() {
    RuleEntity rule1Automatic = newRule("testFindRealizationsByFilterSortByActionType1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Automatic = newRule("testFindRealizationsByFilterSortByActionType2", "domain2", true, EntityType.AUTOMATIC);
    RuleEntity rule3Manual = newRule("testFindRealizationsByFilterSortByActionType3", "domain3", true, EntityType.MANUAL);

    List<RealizationEntity> histories = new ArrayList<>();
    histories.add(newRealizationToBeSortedByActionTypeInDateRange(fromDate,
                                                                                 rule1Automatic.getEvent(),
                                                                                 rule1Automatic.getId()));
    histories.add(newRealizationToBeSortedByActionTypeInDateRange(fromDate, "", rule3Manual.getId()));
    histories.add(newRealizationToBeSortedByActionTypeInDateRange(fromDate, "", rule1Automatic.getId()));
    histories.add(newRealizationToBeSortedByActionTypeInDateRange(fromDate,
                                                                                 rule3Manual.getEvent(),
                                                                                 rule3Manual.getId()));
    histories.add(newRealizationToBeSortedByActionTypeInDateRange(fromDate, "", rule2Automatic.getId()));
    histories.add(newRealizationToBeSortedByActionTypeInDateRange(fromDate,
                                                                                 rule2Automatic.getEvent(),
                                                                                 rule2Automatic.getId()));
    histories.add(newRealizationToBeSortedByActionTypeInDateRange(OutOfRangeDate,
                                                                                 rule3Manual.getEvent(),
                                                                                 rule3Manual.getId()));

    RealizationFilter dateFilter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setProgramIds(domainIds);
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("type");
    dateFilter.setSortDescending(true);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setEarnerType(IdentityType.getType(""));
    List<RealizationEntity> result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(3).getId(), histories.get(1).getId()),
                 result.stream().map(RealizationEntity::getId).toList());
    assertTrue(result.stream()
                     .map(RealizationEntity::getCreatedDate)
                     .map(this::isThisDateWithinThisRange)
                     .reduce(true, Boolean::logicalAnd));

    result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 3);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(3).getId(), histories.get(1).getId(), histories.get(5).getId()),
                 result.stream().map(RealizationEntity::getId).toList());
    assertTrue(result.stream()
                     .map(RealizationEntity::getCreatedDate)
                     .map(this::isThisDateWithinThisRange)
                     .reduce(true, Boolean::logicalAnd));

    dateFilter.setSortDescending(false);
    result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(5).getId(), histories.get(4).getId()),
                 result.stream().map(RealizationEntity::getId).toList());
    assertTrue(result.stream()
                     .map(RealizationEntity::getCreatedDate)
                     .map(this::isThisDateWithinThisRange)
                     .reduce(true, Boolean::logicalAnd));

    result = realizationDAO.findRealizationsByFilter(dateFilter, 2, 6);
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(),
                               histories.get(0).getId(),
                               histories.get(3).getId(),
                               histories.get(1).getId()),
                 result.stream().map(RealizationEntity::getId).toList());
    assertTrue(result.stream()
                     .map(RealizationEntity::getCreatedDate)
                     .map(this::isThisDateWithinThisRange)
                     .reduce(true, Boolean::logicalAnd));
  }

  @Test
  public void testFindAllRealizationsByFilterSortByStatus() {
    List<RealizationEntity> histories = new ArrayList<>();
    histories.add(newRealizationsByStatus(RealizationStatus.ACCEPTED));
    histories.add(newRealizationsByStatus(RealizationStatus.REJECTED));
    histories.add(newRealizationsByStatus(RealizationStatus.ACCEPTED));

    RealizationFilter dateFilter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setProgramIds(domainIds);
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("status");
    dateFilter.setSortDescending(true);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setEarnerType(IdentityType.getType(""));
    List<RealizationEntity> result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(1).getId(), histories.get(2).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());

    result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 3);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(1).getId(),
                               histories.get(2).getId(),
                               histories.get(0).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());

    dateFilter.setSortDescending(false);
    result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(), histories.get(0).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());

    result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 3);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(),
                               histories.get(0).getId(),
                               histories.get(1).getId()),
                 result.stream()
                       .map(RealizationEntity::getId)
                       .toList());
  }

  @Test
  public void testFindUsersRealizationsByConnectedUserType() {
    // Test get All Realizations when Admin calls, Sort field = 'actionType'
    // with sort descending = true
    List<RealizationEntity> histories = new ArrayList<>();
    histories.add(newRealizationsByEarnerId("1"));
    histories.add(newRealizationsByEarnerId("1"));
    histories.add(newRealizationsByEarnerId("1"));
    histories.add(newRealizationsByEarnerId("1"));
    histories.add(newRealizationsByEarnerId("1"));
    histories.add(newRealizationsByEarnerId("1"));
    histories.add(newRealizationsByEarnerId("2"));

    RealizationFilter dateFilter = new RealizationFilter();

    List<Long> domainIds = Collections.emptyList();
    dateFilter.setProgramIds(domainIds);
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("1")));
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("type");
    dateFilter.setSortDescending(true);
    dateFilter.setEarnerType(IdentityType.getType(""));

    List<RealizationEntity> result1 = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result1);
    assertEquals(6, result1.size());

    // Test get All Realizations when a simple user calls, Sort field =
    // 'actionType' with sort descending = false
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("2")));
    List<RealizationEntity> result3 = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result3);
    assertEquals(1, result3.size());

    // Test get All Realizations when a simple user calls, Sort field =
    // 'actionType' with sort descending = true
    dateFilter.setSortDescending(true);
    List<RealizationEntity> result4 = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result4);
    assertEquals(1, result4.size());

    // Test get All Realizations when a simple user calls, Sort field = 'date'
    // with sort descending = false
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(false);
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("2")));
    List<RealizationEntity> result7 = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result7);
    assertEquals(1, result7.size());

    // Test get All Realizations when a simple user calls, Sort field = 'date'
    // with sort descending = true
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(true);
    List<RealizationEntity> result8 = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result8);
    assertEquals(1, result8.size());

    // Test get All Realizations when a simple user calls, Sort field = 'status'
    // with sort descending = false
    dateFilter.setSortField("status");
    dateFilter.setSortDescending(false);
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("2")));
    List<RealizationEntity> result9 = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result9);
    assertEquals(1, result9.size());

    // Test get All Realizations when a simple user calls, Sort field = 'status'
    // with sort descending = true
    dateFilter.setSortField("status");
    dateFilter.setSortDescending(true);
    List<RealizationEntity> result10 = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result10);
    assertEquals(1, result10.size());
  }

  @Test
  public void testFindAllRealizationsByDomainId() {

    Date createDate = new Date(System.currentTimeMillis());
    Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
    ProgramDTO domain1 = new ProgramDTO();
    domain1.setTitle("domain1");
    domain1.setDescription("Description");
    domain1.setCreatedBy(TEST_USER_EARNER);
    domain1.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain1.setLastModifiedBy(TEST_USER_EARNER);
    domain1.setDeleted(false);
    domain1.setEnabled(true);
    domain1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    programService.createProgram(domain1);
    ProgramDTO domain2 = new ProgramDTO();
    domain2.setTitle("domain2");
    domain2.setDescription("Description");
    domain2.setCreatedBy(TEST_USER_EARNER);
    domain2.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain2.setLastModifiedBy(TEST_USER_EARNER);
    domain2.setDeleted(false);
    domain2.setEnabled(true);
    domain2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    programService.createProgram(domain2);

    RuleEntity rule1Automatic = newRule("domain1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Automatic = newRule("domain2", "domain2", true, EntityType.AUTOMATIC);
    RuleEntity rule3Manual = newRule("domain2", "domain2", true, EntityType.MANUAL);

    List<RealizationEntity> histories = new ArrayList<>();

    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule3Manual, "1"));

    RealizationFilter dateFilter = new RealizationFilter();
    List<Long> domainIds = new ArrayList<Long>();
    domainIds.add(rule1Automatic.getDomainEntity().getId());
    dateFilter.setProgramIds(domainIds);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setEarnerType(IdentityType.getType(""));
    List<RealizationEntity> result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(0).getId(),
                               histories.get(1).getId(),
                               histories.get(2).getId()),
                 result.stream().map(RealizationEntity::getId).toList());
  }

  @Test
  public void testFindAllRealizationsByDomainIdByActionType() {

    Date createDate = new Date(System.currentTimeMillis());
    Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
    ProgramDTO domain1 = new ProgramDTO();
    domain1.setTitle("domain1");
    domain1.setDescription("Description");
    domain1.setCreatedBy(TEST_USER_EARNER);
    domain1.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain1.setLastModifiedBy(TEST_USER_EARNER);
    domain1.setDeleted(false);
    domain1.setEnabled(true);
    domain1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    programService.createProgram(domain1);
    ProgramDTO domain2 = new ProgramDTO();
    domain2.setTitle("domain2");
    domain2.setDescription("Description");
    domain2.setCreatedBy(TEST_USER_EARNER);
    domain2.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain2.setLastModifiedBy(TEST_USER_EARNER);
    domain2.setDeleted(false);
    domain2.setEnabled(true);
    domain2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    programService.createProgram(domain2);

    RuleEntity rule1Automatic = newRule("domain1", "domain1", true, EntityType.MANUAL);
    RuleEntity rule2Automatic = newRule("domain2", "domain2", true, EntityType.AUTOMATIC);
    RuleEntity rule3Manual = newRule("domain2", "domain1", true, EntityType.MANUAL);

    List<RealizationEntity> histories = new ArrayList<>();

    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule3Manual, "1"));

    RealizationFilter dateFilter = new RealizationFilter();
    List<Long> domainIds = new ArrayList<Long>();
    domainIds.add(rule1Automatic.getDomainEntity().getId());
    dateFilter.setProgramIds(domainIds);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setEarnerType(IdentityType.getType(""));
    dateFilter.setSortField("type");
    dateFilter.setSortDescending(true);
    List<RealizationEntity> result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(),
                               histories.get(1).getId(),
                               histories.get(0).getId()),
                 result.stream().map(RealizationEntity::getId).toList());
  }

  @Test
  public void testFindAllRealizationsByDomainIdByEarnerId() {

    Date createDate = new Date(System.currentTimeMillis());
    Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
    ProgramDTO domain1 = new ProgramDTO();
    domain1.setTitle("domain1");
    domain1.setDescription("Description");
    domain1.setCreatedBy(TEST_USER_EARNER);
    domain1.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain1.setLastModifiedBy(TEST_USER_EARNER);
    domain1.setDeleted(false);
    domain1.setEnabled(true);
    domain1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    programService.createProgram(domain1);
    ProgramDTO domain2 = new ProgramDTO();
    domain2.setTitle("domain2");
    domain2.setDescription("Description");
    domain2.setCreatedBy(TEST_USER_EARNER);
    domain2.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain2.setLastModifiedBy(TEST_USER_EARNER);
    domain2.setDeleted(false);
    domain2.setEnabled(true);
    domain2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    programService.createProgram(domain2);

    RuleEntity rule1Automatic = newRule("domain1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Automatic = newRule("domain2", "domain2", true, EntityType.AUTOMATIC);
    RuleEntity rule3Manual = newRule("domain2", "domain2", true, EntityType.MANUAL);

    List<RealizationEntity> histories = new ArrayList<>();

    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "2"));
    histories.add(newRealizationByRuleByEarnerId(rule1Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule2Automatic, "1"));
    histories.add(newRealizationByRuleByEarnerId(rule3Manual, "1"));

    RealizationFilter dateFilter = new RealizationFilter();
    List<Long> domainIds = new ArrayList<Long>();
    domainIds.add(rule1Automatic.getDomainEntity().getId());
    dateFilter.setProgramIds(domainIds);
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("1")));
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setEarnerType(IdentityType.getType(""));
    List<RealizationEntity> result = realizationDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(0).getId(),
                               histories.get(2).getId()),
                 result.stream().map(RealizationEntity::getId).toList());
  }

}

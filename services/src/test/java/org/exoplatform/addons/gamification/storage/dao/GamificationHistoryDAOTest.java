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

package org.exoplatform.addons.gamification.storage.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;

import org.junit.Test;

public class GamificationHistoryDAOTest extends AbstractServiceTest {

  @Test
  public void testFindAllActionsHistoryAgnostic() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryAgnostic(IdentityType.USER).size(), 0);
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO
                                                                      .findAllActionsHistoryAgnostic(IdentityType.USER);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3);
    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    leaderboardList = gamificationHistoryDAO.findAllActionsHistoryAgnostic(IdentityType.USER);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 2);
  }

  @Test
  public void testFindAllActionsHistoryByDateByDomain() {
    assertEquals(
                 gamificationHistoryDAO
                                       .findAllActionsHistoryByDateByDomain(IdentityType.USER, fromDate, GAMIFICATION_DOMAIN)
                                       .size(),
                 0);
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO
                                                                      .findAllActionsHistoryByDateByDomain(IdentityType.USER,
                                                                                                           fromDate,
                                                                                                           GAMIFICATION_DOMAIN);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3L);
    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    leaderboardList = gamificationHistoryDAO.findAllActionsHistoryAgnostic(IdentityType.USER);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 2L);
  }

  @Test
  public void testFindAllActionsHistoryByDomain() {
    assertEquals(
                 gamificationHistoryDAO.findAllActionsHistoryByDomain(IdentityType.USER, GAMIFICATION_DOMAIN).size(),
                 0);
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO
                                                                      .findAllActionsHistoryByDomain(IdentityType.USER,
                                                                                                     GAMIFICATION_DOMAIN);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3);
    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    leaderboardList = gamificationHistoryDAO.findAllActionsHistoryAgnostic(IdentityType.USER);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 2);
  }

  @Test
  public void testFindAllActionsHistoryByDate() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistoryByDate(IdentityType.USER, fromDate).size(), 0);
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO
                                                                      .findAllActionsHistoryByDate(IdentityType.USER, fromDate);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3);
    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    leaderboardList = gamificationHistoryDAO.findAllActionsHistoryByDate(IdentityType.USER, fromDate);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 2);
  }

  @Test
  public void testFindActionsHistoryByEarnerId() {
    assertEquals(gamificationHistoryDAO.findActionsHistoryByEarnerId(TEST_USER_SENDER, limit).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findActionsHistoryByEarnerId(TEST_USER_SENDER, limit).size(), limit);
  }

  @Test
  public void testFindAllActionsHistory() {
    assertEquals(gamificationHistoryDAO.findAllActionsHistory(IdentityType.USER, limit).size(), 0);
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO.findAllActionsHistory(IdentityType.USER,
                                                                                             limit);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3);
    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    leaderboardList = gamificationHistoryDAO.findAllActionsHistoryByDate(IdentityType.USER, fromDate);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 2);
  }

  @Test
  public void testFindActionHistoryByDateByEarnerId() {
    assertEquals(gamificationHistoryDAO.findActionHistoryByDateByEarnerId(fromDate, TEST_USER_SENDER).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findActionHistoryByDateByEarnerId(fromDate, TEST_USER_SENDER).size(),
                 limit);
  }

  @Test
  public void testFindActionsHistoryByDateByDomain() {
    assertEquals(gamificationHistoryDAO
                                       .findActionsHistoryByDateByDomain(fromDate, IdentityType.USER, GAMIFICATION_DOMAIN, limit)
                                       .size(),
                 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO.findActionsHistoryByDateByDomain(fromDate,
                                                                                                        IdentityType.USER,
                                                                                                        GAMIFICATION_DOMAIN,
                                                                                                        limit);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3);
  }

  @Test
  public void testFindStatsByUserId() {
    assertEquals(gamificationHistoryDAO.findStatsByUserId(TEST_USER_SENDER, fromDate, toDate).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<PiechartLeaderboard> piechartLeaderboardList = gamificationHistoryDAO.findStatsByUserId(TEST_USER_SENDER,
                                                                                                 fromDate,
                                                                                                 toDate);
    assertEquals(piechartLeaderboardList.size(), 1);
    assertEquals(piechartLeaderboardList.get(0).getLabel(), GAMIFICATION_DOMAIN);
  }

  @Test
  public void testGetAllPointsByDomain() {
    assertEquals(gamificationHistoryDAO.getAllPointsByDomain(GAMIFICATION_DOMAIN).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getAllPointsByDomain(GAMIFICATION_DOMAIN).size(), limit);
  }

  @Test
  public void testGetAllPointsWithNullDomain() {
    assertEquals(gamificationHistoryDAO.getAllPointsWithNullDomain().size(), 0);

    GamificationActionsHistory ghistory1 = newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getAllPointsWithNullDomain().size(), 0);
    ghistory1.setDomainEntity(null);
    gamificationHistoryDAO.update(ghistory1);
    assertEquals(gamificationHistoryDAO.getAllPointsWithNullDomain().size(), 1);
    assertEquals(gamificationHistoryDAO.getAllPointsWithNullDomain().get(0).getGlobalScore(),
                 Integer.parseInt(TEST__SCORE));

    GamificationActionsHistory gHistory2 = newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getAllPointsWithNullDomain().size(), 1);
    gHistory2.setDomainEntity(null);
    gamificationHistoryDAO.update(gHistory2);
    assertEquals(gamificationHistoryDAO.getAllPointsWithNullDomain().size(), 2);
  }

  @Test
  public void testFindDomainScoreByIdentityId() {
    assertEquals(gamificationHistoryDAO.findDomainScoreByIdentityId(TEST_USER_SENDER).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findDomainScoreByIdentityId(TEST_USER_SENDER).size(), 1);
    assertEquals(gamificationHistoryDAO.findDomainScoreByIdentityId(TEST_USER_SENDER).get(0).getScore(),
                 Integer.parseInt(TEST__SCORE) * 3);
  }

  @Test
  public void testFindUserReputationScoreBetweenDate() {
    assertEquals(gamificationHistoryDAO.findUserReputationScoreBetweenDate(TEST_USER_SENDER, fromDate, toDate), 0);
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findUserReputationScoreBetweenDate(TEST_USER_SENDER, fromDate, toDate),
                 Integer.parseInt(TEST__SCORE) * 3);
    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    assertEquals(gamificationHistoryDAO.findUserReputationScoreBetweenDate(TEST_USER_SENDER, fromDate, toDate),
                 Integer.parseInt(TEST__SCORE) * 2);
  }

  @Test
  public void testFindUsersReputationScoreBetweenDate() {

    List<String> earnersId = new ArrayList<>();
    earnersId.add(TEST_USER_SENDER);
    Map<Long, Long> scores = gamificationHistoryDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
    assertEquals(Long.valueOf(0), java.util.Optional.ofNullable(scores.get(Long.parseLong(TEST_USER_SENDER))).orElse(0L));
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();

    scores = gamificationHistoryDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
    Long expected = Integer.parseInt(TEST__SCORE) * 3L;
    assertEquals(expected, java.util.Optional.ofNullable(scores.get(Long.parseLong(TEST_USER_SENDER))).orElse(0L));

    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    expected = Integer.parseInt(TEST__SCORE) * 2L;
    scores = gamificationHistoryDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
    assertEquals(expected, java.util.Optional.ofNullable(scores.get(Long.parseLong(TEST_USER_SENDER))).orElse(0L));
  }

  @Test
  public void testFindUserReputationScoreByMonth() {
    assertEquals(gamificationHistoryDAO.findUserReputationScoreByMonth(TEST_USER_SENDER, fromDate), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findUserReputationScoreByMonth(TEST_USER_SENDER, fromDate),
                 Integer.parseInt(TEST__SCORE) * 3);
  }

  @Test
  public void testFindUserReputationScoreByDomainBetweenDate() {
    assertEquals(gamificationHistoryDAO.findUserReputationScoreByDomainBetweenDate(TEST_USER_SENDER,
                                                                                   GAMIFICATION_DOMAIN,
                                                                                   fromDate,
                                                                                   toDate),
                 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findUserReputationScoreByDomainBetweenDate(TEST_USER_SENDER,
                                                                                   GAMIFICATION_DOMAIN,
                                                                                   fromDate,
                                                                                   toDate),
                 Integer.parseInt(TEST__SCORE) * 3);
  }

  @Test
  public void testFindActionsHistoryByEarnerIdSortedByDate() {
    assertEquals(gamificationHistoryDAO.findActionsHistoryByEarnerIdSortedByDate(TEST_USER_SENDER, limit).size(),
                 0);
    GamificationActionsHistory gHistory = newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findActionsHistoryByEarnerIdSortedByDate(TEST_USER_SENDER, limit).size(),
                 limit);
    gHistory.setStatus(HistoryStatus.REJECTED);
    gamificationHistoryDAO.update(gHistory);
    assertEquals(gamificationHistoryDAO.findActionsHistoryByEarnerIdSortedByDate(TEST_USER_SENDER, limit).size(),
                 limit - 1);

  }

  @Test
  public void testfindAllLeaderboardBetweenDate() {
    assertEquals(gamificationHistoryDAO.findAllLeaderboardBetweenDate(IdentityType.USER, fromDate, toDate).size(),
                 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO
                                                                      .findAllLeaderboardBetweenDate(IdentityType.USER,
                                                                                                     fromDate,
                                                                                                     toDate);
    assertEquals(leaderboardList.size(), 1);
    assertEquals(leaderboardList.get(0).getEarnerId(), TEST_USER_SENDER);
    assertEquals(leaderboardList.get(0).getReputationScore(), Integer.parseInt(TEST__SCORE) * 3);
  }

  @Test
  public void testGetDomainList() {
    assertEquals(gamificationHistoryDAO.getDomainList().size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.getDomainList().size(), 1);
  }

  @Test
  public void testCountAnnouncementsByChallenge() {
    assertEquals((long) gamificationHistoryDAO.countAnnouncementsByChallenge(1L), 0l);
    GamificationActionsHistoryDTO ghistory = newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
    assertEquals((long) gamificationHistoryDAO.countAnnouncementsByChallenge(ghistory.getRuleId()), limit);
  }

  @Test
  public void testFindAllAnnouncementByChallenge() {
    GamificationActionsHistoryDTO ghistory = newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
    assertEquals(gamificationHistoryDAO.findAllAnnouncementByChallenge(ghistory.getRuleId(), offset, limit).size(),
                 limit);
  }

  @Test
  public void testFindAllRealizationsByFilterSortByActionType() {
    RuleEntity rule1Automatic = newRule("testFindRealizationsByFilterSortByActionType1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Automatic = newRule("testFindRealizationsByFilterSortByActionType2", "domain2", true, EntityType.AUTOMATIC);
    RuleEntity rule3Manual = newRule("testFindRealizationsByFilterSortByActionType3", "domain3", true, EntityType.MANUAL);

    List<GamificationActionsHistory> histories = new ArrayList<>();
    histories.add(newGamificationActionsHistoryWithRuleId(rule1Automatic.getEvent(), rule1Automatic.getId()));
    histories.add(newGamificationActionsHistoryWithRuleId("", rule3Manual.getId()));
    histories.add(newGamificationActionsHistoryWithRuleId("", rule1Automatic.getId()));
    histories.add(newGamificationActionsHistoryWithRuleId(rule3Manual.getEvent(), rule3Manual.getId()));
    histories.add(newGamificationActionsHistoryWithRuleId("", rule2Automatic.getId()));
    histories.add(newGamificationActionsHistoryWithRuleId(rule2Automatic.getEvent(), rule2Automatic.getId()));
    histories.add(newGamificationActionsHistoryWithRuleId("", rule3Manual.getId()));

    RealizationsFilter dateFilter = new RealizationsFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("type");
    dateFilter.setSortDescending(true);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setIdentityType(IdentityType.getType(""));
    dateFilter.setDomainIds(domainIds);
    List<GamificationActionsHistory> result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(6).getId(), histories.get(3).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));

    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 4);
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals(Arrays.asList(histories.get(6).getId(),
                               histories.get(3).getId(),
                               histories.get(1).getId(),
                               histories.get(5).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));

    dateFilter.setSortDescending(false);
    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(5).getId(), histories.get(4).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));

    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 2, 6);
    assertNotNull(result);
    assertEquals(5, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(),
                               histories.get(0).getId(),
                               histories.get(6).getId(),
                               histories.get(3).getId(),
                               histories.get(1).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));
  }

  @Test
  public void testFindAllRealizationsByFilter() {
    // testing findRealizationsByFilter when only dates given
    RealizationsFilter dateFilter = new RealizationsFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("date");
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setIdentityType(IdentityType.getType(""));
    dateFilter.setDomainIds(domainIds);
    // Test default Sort field = 'date' with sort descending = false
    List<GamificationActionsHistory> filteredRealizations = gamificationHistoryDAO.findRealizationsByFilter(dateFilter,
                                                                                                            offset,
                                                                                                            limit);
    assertEquals(0, filteredRealizations.size());

    List<GamificationActionsHistory> createdActionHistories = new ArrayList<>();
    for (int i = 0; i < limit * 2; i++) {
      createdActionHistories.add(newGamificationActionsHistory());
    }

    filteredRealizations = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, offset, limit);
    assertEquals(limit, filteredRealizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                        .stream()
                                        .map(GamificationActionsHistory::getId)
                                        .collect(Collectors.toList()),
                 filteredRealizations.stream()
                                     .map(GamificationActionsHistory::getId)
                                     .collect(Collectors.toList()));

    // Test explicit Sort field = 'date' with sort descending = false
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(false);
    filteredRealizations = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, offset, limit);
    assertEquals(limit, filteredRealizations.size());
    assertEquals(createdActionHistories.subList(0, 3)
                                        .stream()
                                        .map(GamificationActionsHistory::getId)
                                        .collect(Collectors.toList()),
                 filteredRealizations.stream()
                                     .map(GamificationActionsHistory::getId)
                                     .collect(Collectors.toList()));

    // Test explicit Sort field = 'date' with sort descending = true
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(true);

    filteredRealizations = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, offset, limit);
    Collections.reverse(createdActionHistories);
    assertEquals(createdActionHistories.subList(0, 3)
                                        .stream()
                                        .map(GamificationActionsHistory::getId)
                                        .collect(Collectors.toList()),
                 filteredRealizations.stream()
                                     .map(GamificationActionsHistory::getId)
                                     .collect(Collectors.toList()));
  }
  
  @Test
  public void testFindRealizationsByFilterSortByActionTypeInDateRange() {
    RuleEntity rule1Automatic = newRule("testFindRealizationsByFilterSortByActionType1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Automatic = newRule("testFindRealizationsByFilterSortByActionType2", "domain2", true, EntityType.AUTOMATIC);
    RuleEntity rule3Manual = newRule("testFindRealizationsByFilterSortByActionType3", "domain3", true, EntityType.MANUAL);

    List<GamificationActionsHistory> histories = new ArrayList<>();
    histories.add(newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(fromDate, rule1Automatic.getEvent(), rule1Automatic.getId()));
    histories.add(newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(fromDate, "", rule3Manual.getId()));
    histories.add(newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(fromDate, "", rule1Automatic.getId()));
    histories.add(newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(fromDate, rule3Manual.getEvent(), rule3Manual.getId()));
    histories.add(newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(fromDate, "", rule2Automatic.getId()));
    histories.add(newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(fromDate, rule2Automatic.getEvent(), rule2Automatic.getId()));
    histories.add(newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(OutOfRangeDate, rule3Manual.getEvent(), rule3Manual.getId()));

    RealizationsFilter dateFilter = new RealizationsFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setDomainIds(domainIds);
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("type");
    dateFilter.setSortDescending(true);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setIdentityType(IdentityType.getType(""));
    List<GamificationActionsHistory> result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(3).getId(), histories.get(1).getId()),
                 result.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()));
    assertTrue(result.stream()
               .map(GamificationActionsHistory::getCreatedDate)
               .map(this::isThisDateWithinThisRange)
               .reduce(true, Boolean::logicalAnd));

    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 3);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(3).getId(), histories.get(1).getId(), histories.get(5).getId()),
                 result.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()));
    assertTrue(result.stream()
               .map(GamificationActionsHistory::getCreatedDate)
               .map(this::isThisDateWithinThisRange)
               .reduce(true, Boolean::logicalAnd));

    dateFilter.setSortDescending(false);
    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(5).getId(), histories.get(4).getId()),
                 result.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()));
    assertTrue(result.stream()
               .map(GamificationActionsHistory::getCreatedDate)
               .map(this::isThisDateWithinThisRange)
               .reduce(true, Boolean::logicalAnd));

    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 2, 6);
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(),
                               histories.get(0).getId(),
                               histories.get(3).getId(),
                               histories.get(1).getId()),
                 result.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()));
    assertTrue(result.stream()
                     .map(GamificationActionsHistory::getCreatedDate)
                     .map(this::isThisDateWithinThisRange)
                     .reduce(true, Boolean::logicalAnd));
  }
  
  @Test
  public void testFindAllRealizationsByFilterSortByStatus() {
    List<GamificationActionsHistory> histories = new ArrayList<>();
    histories.add(newGamificationActionsHistoryByStatus(HistoryStatus.ACCEPTED));
    histories.add(newGamificationActionsHistoryByStatus(HistoryStatus.REJECTED));
    histories.add(newGamificationActionsHistoryByStatus(HistoryStatus.ACCEPTED));

    RealizationsFilter dateFilter = new RealizationsFilter();
    List<Long> domainIds = Collections.emptyList();
    dateFilter.setDomainIds(domainIds);
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("status");
    dateFilter.setSortDescending(true);
    dateFilter.setEarnerIds(new ArrayList<>());
    dateFilter.setIdentityType(IdentityType.getType(""));
    List<GamificationActionsHistory> result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(1).getId(), histories.get(2).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));

    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 3);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(1).getId(),
                               histories.get(2).getId(),
                               histories.get(0).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));

    dateFilter.setSortDescending(false);
    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 2);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(), histories.get(0).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));

    result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 3);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(histories.get(2).getId(),
                               histories.get(0).getId(),
                               histories.get(1).getId()),
                 result.stream()
                       .map(GamificationActionsHistory::getId)
                       .collect(Collectors.toList()));
  }

  
  @Test
  public void testFindUsersRealizationsByConnectedUserType() {
    // Test get All Realizations when Admin calls, Sort field = 'actionType' with sort descending = true
    List<GamificationActionsHistory> histories = new ArrayList<>();
    histories.add(newGamificationActionsHistoryByEarnerId("1"));
    histories.add(newGamificationActionsHistoryByEarnerId("1"));
    histories.add(newGamificationActionsHistoryByEarnerId("1"));
    histories.add(newGamificationActionsHistoryByEarnerId("1"));
    histories.add(newGamificationActionsHistoryByEarnerId("1"));
    histories.add(newGamificationActionsHistoryByEarnerId("1"));
    histories.add(newGamificationActionsHistoryByEarnerId("2"));

    RealizationsFilter dateFilter = new RealizationsFilter();

    List<Long> domainIds = Collections.emptyList();
    dateFilter.setDomainIds(domainIds);
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("1")));
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    dateFilter.setSortField("type");
    dateFilter.setSortDescending(true);
    dateFilter.setIdentityType(IdentityType.getType(""));
    
    List<GamificationActionsHistory> result1 = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result1);
    assertEquals(6, result1.size());
    
    
    // Test get All Realizations when a simple user calls, Sort field = 'actionType' with sort descending = false
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("2")));
    List<GamificationActionsHistory> result3 = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result3);
    assertEquals(1, result3.size());
       
    // Test get All Realizations when a simple user calls, Sort field = 'actionType' with sort descending = true
    dateFilter.setSortDescending(true);
    List<GamificationActionsHistory> result4 = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result4);
    assertEquals(1, result4.size());
    
    
    // Test get All Realizations when a simple user calls, Sort field = 'date' with sort descending = false
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(false);
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("2")));
    List<GamificationActionsHistory> result7 = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result7);
    assertEquals(1, result7.size());
    
    // Test get All Realizations when a simple user calls, Sort field = 'date' with sort descending = true
    dateFilter.setSortField("date");
    dateFilter.setSortDescending(true);
    List<GamificationActionsHistory> result8 = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result8);
    assertEquals(1, result8.size());
    
    // Test get All Realizations when a simple user calls, Sort field = 'status' with sort descending = false
    dateFilter.setSortField("status");
    dateFilter.setSortDescending(false);
    dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("2")));
    List<GamificationActionsHistory> result9 = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result9);
    assertEquals(1, result9.size());

    // Test get All Realizations when a simple user calls, Sort field = 'status' with sort descending = true
    dateFilter.setSortField("status");
    dateFilter.setSortDescending(true);
    List<GamificationActionsHistory> result10 = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
    assertNotNull(result10);
    assertEquals(1, result10.size());
  }
  
  @Test
  public void testFindAllRealizationsByDomainId() {
      
      Date createDate = new Date(System.currentTimeMillis());
      Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
      DomainDTO domain1 = new DomainDTO();
      domain1.setTitle("domain1");
      domain1.setDescription("Description");
      domain1.setCreatedBy(TEST_USER_SENDER);
      domain1.setCreatedDate(Utils.toRFC3339Date(createDate));
      domain1.setLastModifiedBy(TEST_USER_SENDER);
      domain1.setDeleted(false);
      domain1.setEnabled(true);
      domain1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
      domainService.createDomain(domain1);
      DomainDTO domain2 = new DomainDTO();
      domain2.setTitle("domain2");
      domain2.setDescription("Description");
      domain2.setCreatedBy(TEST_USER_SENDER);
      domain2.setCreatedDate(Utils.toRFC3339Date(createDate));
      domain2.setLastModifiedBy(TEST_USER_SENDER);
      domain2.setDeleted(false);
      domain2.setEnabled(true);
      domain2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
      domainService.createDomain(domain2);

      RuleEntity rule1Automatic = newRule("domain1", "domain1", true, EntityType.AUTOMATIC);
      RuleEntity rule2Automatic = newRule("domain2", "domain2", true, EntityType.AUTOMATIC);
      RuleEntity rule3Manual = newRule("domain2", "domain2", true, EntityType.MANUAL);

      List<GamificationActionsHistory> histories = new ArrayList<>();

      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule3Manual, "1"));

      RealizationsFilter dateFilter = new RealizationsFilter();
      List<Long> domainIds = new ArrayList<Long>();
      domainIds.add(rule1Automatic.getDomainEntity().getId());
      dateFilter.setDomainIds(domainIds);
      dateFilter.setEarnerIds(new ArrayList<>());
      dateFilter.setFromDate(fromDate);
      dateFilter.setToDate(toDate);
      dateFilter.setIdentityType(IdentityType.getType(""));
      List<GamificationActionsHistory> result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
      assertNotNull(result);
      assertEquals(3, result.size());
      assertEquals(Arrays.asList(histories.get(0).getId(),
              histories.get(1).getId(),
              histories.get(2).getId()),
              result.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()));
  }
  
  @Test
  public void testFindAllRealizationsByDomainIdByActionType() {
      
      Date createDate = new Date(System.currentTimeMillis());
      Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
      DomainDTO domain1 = new DomainDTO();
      domain1.setTitle("domain1");
      domain1.setDescription("Description");
      domain1.setCreatedBy(TEST_USER_SENDER);
      domain1.setCreatedDate(Utils.toRFC3339Date(createDate));
      domain1.setLastModifiedBy(TEST_USER_SENDER);
      domain1.setDeleted(false);
      domain1.setEnabled(true);
      domain1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
      domainService.createDomain(domain1);
      DomainDTO domain2 = new DomainDTO();
      domain2.setTitle("domain2");
      domain2.setDescription("Description");
      domain2.setCreatedBy(TEST_USER_SENDER);
      domain2.setCreatedDate(Utils.toRFC3339Date(createDate));
      domain2.setLastModifiedBy(TEST_USER_SENDER);
      domain2.setDeleted(false);
      domain2.setEnabled(true);
      domain2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
      domainService.createDomain(domain2);

      RuleEntity rule1Automatic = newRule("domain1", "domain1", true, EntityType.MANUAL);
      RuleEntity rule2Automatic = newRule("domain2", "domain2", true, EntityType.AUTOMATIC);
      RuleEntity rule3Manual = newRule("domain2", "domain1", true, EntityType.MANUAL);

      List<GamificationActionsHistory> histories = new ArrayList<>();

      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule3Manual, "1"));

      RealizationsFilter dateFilter = new RealizationsFilter();
      List<Long> domainIds = new ArrayList<Long>();
      domainIds.add(rule1Automatic.getDomainEntity().getId());
      dateFilter.setDomainIds(domainIds);
      dateFilter.setEarnerIds(new ArrayList<>());
      dateFilter.setFromDate(fromDate);
      dateFilter.setToDate(toDate);
      dateFilter.setIdentityType(IdentityType.getType(""));
      dateFilter.setSortField("type");
      dateFilter.setSortDescending(true);
      List<GamificationActionsHistory> result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
      assertNotNull(result);
      assertEquals(3, result.size());
      assertEquals(Arrays.asList(histories.get(2).getId(),
              histories.get(1).getId(),
              histories.get(0).getId()),
              result.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()));
  }
  
  @Test
  public void testFindAllRealizationsByDomainIdByEarnerId() {
      
      Date createDate = new Date(System.currentTimeMillis());
      Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
      DomainDTO domain1 = new DomainDTO();
      domain1.setTitle("domain1");
      domain1.setDescription("Description");
      domain1.setCreatedBy(TEST_USER_SENDER);
      domain1.setCreatedDate(Utils.toRFC3339Date(createDate));
      domain1.setLastModifiedBy(TEST_USER_SENDER);
      domain1.setDeleted(false);
      domain1.setEnabled(true);
      domain1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
      domainService.createDomain(domain1);
      DomainDTO domain2 = new DomainDTO();
      domain2.setTitle("domain2");
      domain2.setDescription("Description");
      domain2.setCreatedBy(TEST_USER_SENDER);
      domain2.setCreatedDate(Utils.toRFC3339Date(createDate));
      domain2.setLastModifiedBy(TEST_USER_SENDER);
      domain2.setDeleted(false);
      domain2.setEnabled(true);
      domain2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
      domainService.createDomain(domain2);

      RuleEntity rule1Automatic = newRule("domain1", "domain1", true, EntityType.AUTOMATIC);
      RuleEntity rule2Automatic = newRule("domain2", "domain2", true, EntityType.AUTOMATIC);
      RuleEntity rule3Manual = newRule("domain2", "domain2", true, EntityType.MANUAL);

      List<GamificationActionsHistory> histories = new ArrayList<>();

      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "2"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule1Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule2Automatic, "1"));
      histories.add(newGamificationActionsHistoryByRuleByEarnerId(rule3Manual, "1"));

      RealizationsFilter dateFilter = new RealizationsFilter();
      List<Long> domainIds = new ArrayList<Long>();
      domainIds.add(rule1Automatic.getDomainEntity().getId());
      dateFilter.setDomainIds(domainIds);
      dateFilter.setEarnerIds(new ArrayList<>(Collections.singleton("1")));
      dateFilter.setFromDate(fromDate);
      dateFilter.setToDate(toDate);
      dateFilter.setIdentityType(IdentityType.getType(""));
      List<GamificationActionsHistory> result = gamificationHistoryDAO.findRealizationsByFilter(dateFilter, 0, 6);
      assertNotNull(result);
      assertEquals(2, result.size());
      assertEquals(Arrays.asList(histories.get(0).getId(),
              histories.get(2).getId()),
              result.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()));
  }
  

  
}

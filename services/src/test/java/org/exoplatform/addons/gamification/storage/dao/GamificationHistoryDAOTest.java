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

package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.DateFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.junit.Test;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
						.findAllActionsHistoryByDateByDomain(IdentityType.USER, fromDate, GAMIFICATION_DOMAIN).size(),
				0);
		GamificationActionsHistory gHistory = newGamificationActionsHistory();
		newGamificationActionsHistory();
		newGamificationActionsHistory();
		List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO
				.findAllActionsHistoryByDateByDomain(IdentityType.USER, fromDate, GAMIFICATION_DOMAIN);
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
	public void testFindAllActionsHistoryByDomain() {
		assertEquals(
				gamificationHistoryDAO.findAllActionsHistoryByDomain(IdentityType.USER, GAMIFICATION_DOMAIN).size(), 0);
		GamificationActionsHistory gHistory = newGamificationActionsHistory();
		newGamificationActionsHistory();
		newGamificationActionsHistory();
		List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO
				.findAllActionsHistoryByDomain(IdentityType.USER, GAMIFICATION_DOMAIN);
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
				.findActionsHistoryByDateByDomain(fromDate, IdentityType.USER, GAMIFICATION_DOMAIN, limit).size(), 0);
		newGamificationActionsHistory();
		newGamificationActionsHistory();
		newGamificationActionsHistory();
		List<StandardLeaderboard> leaderboardList = gamificationHistoryDAO.findActionsHistoryByDateByDomain(fromDate,
				IdentityType.USER, GAMIFICATION_DOMAIN, limit);
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
				fromDate, toDate);
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
				GAMIFICATION_DOMAIN, fromDate, toDate), 0);
		newGamificationActionsHistory();
		newGamificationActionsHistory();
		newGamificationActionsHistory();
		assertEquals(gamificationHistoryDAO.findUserReputationScoreByDomainBetweenDate(TEST_USER_SENDER,
				GAMIFICATION_DOMAIN, fromDate, toDate), Integer.parseInt(TEST__SCORE) * 3);
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
				.findAllLeaderboardBetweenDate(IdentityType.USER, fromDate, toDate);
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
  public void testFindRealizationsByFilter() {
    // testing findRealizationsByFilter when only dates given
    RealizationsFilter dateFilter = new RealizationsFilter();
    dateFilter.setFromDate(fromDate);
    dateFilter.setToDate(toDate);
    assertEquals(gamificationHistoryDAO.findRealizationsByFilter(dateFilter, offset, limit).size(), 0);
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    assertEquals(gamificationHistoryDAO.findRealizationsByFilter(dateFilter, offset, limit).size(), limit);

    // testing findRealizationsByFilter sorting
    List<GamificationActionsHistory> gamificationsSorted = new ArrayList<GamificationActionsHistory>();
    List<GamificationActionsHistory> gamificationsSortedByRuleId = new ArrayList<GamificationActionsHistory>();
    RealizationsFilter filter = new RealizationsFilter();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setIsSortedByActionTitle(true);
    filter.setSortDescending(true);
    // adding ActionTitle and RuleId as parameters
    newGamificationActionsHistoryToBeSorted("b", 2L);
    newGamificationActionsHistoryToBeSorted("a", 1L);
    newGamificationActionsHistoryToBeSorted("c", 3L);
    assertEquals(gamificationHistoryDAO.findRealizationsByFilter(filter, offset, limit).size(), limit);
    List<GamificationActionsHistory> gamificationActionHistory = gamificationHistoryDAO.findRealizationsByFilter(filter,
                                                                                                                 offset,
                                                                                                                 limit);

    // Given sorted List of ahievements
    gamificationsSorted.add(newGamificationActionsHistoryToBeSorted("a", 1L));
    gamificationsSorted.add(newGamificationActionsHistoryToBeSorted("b", 2L));
    gamificationsSorted.add(newGamificationActionsHistoryToBeSorted("c", 3L));

    int size = gamificationHistoryDAO.findRealizationsByFilter(filter, offset, limit).size();

    assertEquals(size, limit);
    for (int i = 0; i < size; i++) {
      assertEquals(gamificationActionHistory.get(i).getActionTitle(), gamificationsSorted.get(i).getActionTitle());
    }
    ;

    filter.setIsSortedByActionTitle(false);
    filter.setIsSortedByRuleId(true);
    filter.setSortDescending(true);
    assertEquals(size, limit);
    for (int i = 0; i < size; i++) {
      assertEquals(gamificationActionHistory.get(i).getRuleId(), gamificationsSorted.get(i).getRuleId());
    }
    ;

    filter.setIsSortedByActionTitle(true);
    for (int i = 0; i < size; i++) {
      assertEquals(gamificationActionHistory.get(i).getActionTitle(), gamificationsSorted.get(i).getActionTitle());
      assertEquals(gamificationActionHistory.get(i).getRuleId(), gamificationsSorted.get(i).getRuleId());
    }
  }

}

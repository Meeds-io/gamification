/*
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
package org.exoplatform.addons.gamification.service;

import static java.util.Date.from;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.constant.HistoryStatus;
import org.exoplatform.addons.gamification.constant.IdentityType;
import org.exoplatform.addons.gamification.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.entity.GamificationActionsHistoryEntity;
import org.exoplatform.addons.gamification.model.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.model.LeaderboardFilter;
import org.exoplatform.addons.gamification.model.RuleDTO;
import org.exoplatform.addons.gamification.utils.GamificationActionsHistoryMapper;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class GamificationService {

  private static final Log               LOG = ExoLogger.getLogger(GamificationService.class);

  protected final GamificationHistoryDAO gamificationHistoryDAO;

  private final IdentityManager          identityManager;

  private final SpaceService             spaceService;

  private final RuleService              ruleService;

  public GamificationService(IdentityManager identityManager,
                             SpaceService spaceService,
                             GamificationHistoryDAO gamificationHistoryDAO,
                             RuleService ruleService) {
    this.gamificationHistoryDAO = gamificationHistoryDAO;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.ruleService = ruleService;
  }

  /**
   * Get actionsHistory entities
   * 
   * @param date : filter by date
   * @param earnerId : filter by identity id
   * @return {@link List} of {@link GamificationActionsHistoryEntity}
   */
  public List<GamificationActionsHistoryEntity> findActionHistoryByDateByEarnerId(Date date, String earnerId) {
    return gamificationHistoryDAO.findActionHistoryByDateByEarnerId(date, earnerId);
  }

  public int getLeaderboardRank(String earnerId, Date date, String domain) {
    List<StandardLeaderboard> leaderboard = null;
    Identity identity = identityManager.getIdentity(earnerId); // NOSONAR :
                                                               // profile load
                                                               // is always true
    IdentityType identityType = IdentityType.getType(identity.getProviderId());
    if (date != null) {
      if (domain.equalsIgnoreCase("all")) {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDate(identityType, date);
      } else {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDateByDomain(identityType, date, domain);
      }
    } else {
      if (domain.equalsIgnoreCase("all")) {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryAgnostic(identityType);
      } else {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDomain(identityType, domain);
      }
    }
    // Get username
    StandardLeaderboard item = leaderboard.stream().filter(g -> earnerId.equals(g.getEarnerId())).findAny().orElse(null);
    return (leaderboard.indexOf(item) + 1);
  }

  /**
   * Compute reputation's score
   * 
   * @param earnerId : the current user earner id
   * @return long score of user
   */
  public long findReputationByEarnerId(String earnerId) {
    GamificationActionsHistoryEntity aHistory = this.findLatestActionHistoryByEarnerId(earnerId);
    return (aHistory != null ? aHistory.getGlobalScore() : 0);
  }

  /**
   * Compute User reputation score by Domain
   * 
   * @param earnerId earner identity id
   * @return list of objects of type ProfileReputation
   */
  public List<ProfileReputation> buildDomainScoreByIdentityId(String earnerId) {
    return gamificationHistoryDAO.findDomainScoreByIdentityId(earnerId);
  }

  /**
   * Save a GamificationActionsHistory in DB
   * 
   * @param history history entru to save
   * @return {@link GamificationActionsHistoryEntity}
   */
  public GamificationActionsHistoryEntity saveActionHistory(GamificationActionsHistoryEntity history) {
    return gamificationHistoryDAO.create(history);
  }

  public void createHistory(String event, String sender, String receiver, String object) {
    GamificationActionsHistoryEntity aHistory = null;
    List<RuleDTO> ruleDtos = null;
    // Get associated rules
    ruleDtos = ruleService.findEnabledRulesByEvent(event);

    // Process only when an enable rules are found
    if (ruleDtos != null) {
      for (RuleDTO ruleDto : ruleDtos) {
        aHistory = build(ruleDto, sender, receiver, object);
        if (aHistory != null) {
          aHistory = saveActionHistory(aHistory);
          // Gamification simple audit logger
          LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"",
                   LocalDate.now(),
                   aHistory.getEarnerId(),
                   aHistory.getGlobalScore(),
                   ruleDto.getArea(),
                   ruleDto.getEvent(),
                   ruleDto.getScore());
        }
      }
    }
  }

  public GamificationActionsHistoryEntity findLatestActionHistoryByEarnerId(String earnerId) {
    List<GamificationActionsHistoryEntity> entities = gamificationHistoryDAO.findActionsHistoryByEarnerId(earnerId, 1);
    // Return the first element since the underluing API returns entities
    // ordered by insertion date
    return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
  }

  /**
   * Filter Leaderboard logic (filter by Domain or/and by period)
   * 
   * @param filter of type {@link LeaderboardFilter}, used to filter query
   * @return list of objects of type StandardLeaderboard
   */
  public List<StandardLeaderboard> filter(LeaderboardFilter filter) {
    int limit = filter.getLoadCapacity();
    IdentityType identityType = filter.getIdentityType();
    if (identityType.isSpace()) {
      // Try to get more elements when searching, to be able to retrieve at
      // least 'limit' elements after filtering on authorized spaces
      limit = limit * 3;
    }

    List<StandardLeaderboard> result = null;
    if (StringUtils.isBlank(filter.getDomain()) || filter.getDomain().equalsIgnoreCase("all")) {
      // Compute date
      LocalDate now = LocalDate.now();
      // Check the period
      if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {
        Date fromDate = from(now.with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
        result = gamificationHistoryDAO.findAllActionsHistoryByDate(fromDate, identityType, limit);
      } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {
        Date fromDate = from(now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        result = gamificationHistoryDAO.findAllActionsHistoryByDate(fromDate, identityType, limit);
      } else {
        result = gamificationHistoryDAO.findAllActionsHistory(identityType, limit);
      }
    } else {
      // Compute date
      LocalDate now = LocalDate.now();
      // Check the period
      if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {
        Date fromDate = from(now.with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
        result = gamificationHistoryDAO.findActionsHistoryByDateByDomain(fromDate, identityType, filter.getDomain(), limit);
      } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {
        Date fromDate = from(now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        result = gamificationHistoryDAO.findActionsHistoryByDateByDomain(fromDate, identityType, filter.getDomain(), limit);
      } else {
        result = gamificationHistoryDAO.findAllActionsHistoryByDomain(filter.getDomain(), identityType, limit);
      }
    }

    // Filter on spaces switch user identity
    if (identityType.isSpace() && result != null && !result.isEmpty()) {
      final String currentUser = filter.getCurrentUser();

      if (StringUtils.isNotBlank(currentUser)) {
        result = filterAuthorizedSpaces(result, currentUser, filter.getLoadCapacity());
      }
    }

    return result;
  }

  /**
   * Build stats dashboard of a given user (based on domain)
   * 
   * @param earnerId earner identity id
   * @param startDate
   * @param endDate
   * @return a list of object of type PiechartLeaderboard
   */
  public List<PiechartLeaderboard> buildStatsByUser(String earnerId, Date startDate, Date endDate) {
    return gamificationHistoryDAO.findStatsByUserId(earnerId, startDate, endDate);
  }

  public long findUserReputationScoreBetweenDate(String earnerId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUserReputationScoreBetweenDate(earnerId, fromDate, toDate);
  }

  public Map<Long, Long> findUsersReputationScoreBetweenDate(List<String> earnersId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
  }

  public long findUserReputationScoreByMonth(String earnerId, Date currentMonth) {
    return gamificationHistoryDAO.findUserReputationScoreByMonth(earnerId, currentMonth);
  }

  public long findUserReputationScoreByDomainBetweenDate(String earnerId, String domain, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUserReputationScoreByDomainBetweenDate(earnerId, domain, fromDate, toDate);
  }

  public List<StandardLeaderboard> findAllLeaderboardBetweenDate(IdentityType earnedType, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findAllLeaderboardBetweenDate(earnedType, fromDate, toDate);
  }

  /**
   * Provided as an API from points n list to find gamification history from the
   * GamificationInformationsPortlet's earner earned points by date
   * 
   * @param earnerId earner identity Id
   * @param limit limit entries to return
   * @return {@link List} of {@link GamificationActionsHistoryEntity}
   */
  public List<GamificationActionsHistoryEntity> findActionsHistoryByEarnerId(String earnerId, int limit) {
    return gamificationHistoryDAO.findActionsHistoryByEarnerIdSortedByDate(earnerId, limit);
  }

  public GamificationActionsHistoryEntity build(RuleDTO ruleDto, String actor, String receiver, String objectId) {
    GamificationActionsHistoryEntity aHistory = null;
    // check if the current user is not a bot
    Identity actorIdentity = identityManager.getIdentity(actor);
    if (actorIdentity == null || StringUtils.isBlank(actorIdentity.getRemoteId())) {
      LOG.warn("Actor {} has earned some points but doesn't have a social identity", actor);
      return null;
    }
    if (actorIdentity.isDeleted()) {
      LOG.warn("Actor {} has earned some points but is marked as deleted", actor);
      return null;
    }
    if (!actorIdentity.isEnable()) {
      LOG.warn("Actor {} has earned some points but is marked as disabled", actor);
      return null;
    }

    // Build only an entry when a rule enable and exist
    if (ruleDto != null) {
      GamificationActionsHistoryDTO actionsHistoryDTO = new GamificationActionsHistoryDTO();
      actionsHistoryDTO.setActionScore(ruleDto.getScore());
      actionsHistoryDTO.setGlobalScore(computeTotalScore(actor) + ruleDto.getScore());
      actionsHistoryDTO.setEarnerId(actor);
      actionsHistoryDTO.setEarnerType(actorIdentity.getProviderId());
      actionsHistoryDTO.setActionTitle(ruleDto.getEvent());
      actionsHistoryDTO.setRuleId(ruleDto.getId());
      if (ruleDto.getDomainDTO() != null) {
        actionsHistoryDTO.setDomain(ruleDto.getDomainDTO().getTitle());
      }
      actionsHistoryDTO.setReceiver(receiver);
      actionsHistoryDTO.setObjectId(objectId);
      actionsHistoryDTO.setStatus(HistoryStatus.ACCEPTED.name());
      actionsHistoryDTO.setType(ruleDto.getType());
      return GamificationActionsHistoryMapper.toEntity(actionsHistoryDTO);
    }
    return aHistory;
  }

  public long computeTotalScore(String actorIdentityId) {
    return gamificationHistoryDAO.getTotalScore(actorIdentityId);
  }

  private List<StandardLeaderboard> filterAuthorizedSpaces(List<StandardLeaderboard> result,
                                                           final String currentUser,
                                                           int limit) {
    result = result.stream().filter(spacePoint -> {
      String spaceIdentityId = spacePoint.getEarnerId();
      Identity identity = identityManager.getIdentity(spaceIdentityId);
      if (identity == null) {
        LOG.debug("Space Identity with id {} was deleted, ignore it", spaceIdentityId);
        return false;
      }
      String spacePrettyName = identity.getRemoteId();
      Space space = spaceService.getSpaceByPrettyName(spacePrettyName);
      return space != null && (!Space.HIDDEN.equals(space.getVisibility()) || spaceService.isMember(space, currentUser));
    }).limit(limit).collect(Collectors.toList());
    return result;
  }
}

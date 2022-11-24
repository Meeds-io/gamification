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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.ProfileReputation;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

public class GamificationHistoryDAO extends GenericDAOJPAImpl<GamificationActionsHistory, Long> {

  private static final String        TO_DATE_PARAM_NAME     = "toDate";

  private static final String        FROM_DATE_PARAM_NAME   = "fromDate";

  private static final String        EARNER_ID_PARAM_NAME   = "earnerId";

  private static final String        EARNER_IDS_PARAM_NAME = "earnerIds";
  
  private static final String        DOMAIN_IDS_PARAM_NAME = "domainIds";

  private static final String        DOMAIN_PARAM_NAME      = "domain";

  private static final String        EARNER_TYPE_PARAM_NAME = "earnerType";

  public static final String         STATUS                 = "status";
  
  public static final String         TYPE                   = "type";

  private final Map<String, Boolean> filterNamedQueries     = new HashMap<>();

  /**
   * Get all ActionHistory records and convert them to list of type
   * StandardLeaderboard
   * 
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @return list of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findAllActionsHistoryAgnostic(IdentityType earnerType) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistory",
                                                                              StandardLeaderboard.class);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Find all gamification entries by domain and by type
   * 
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @param date : date from when we load gamification entries
   * @param domain : domain filter
   * @return list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findAllActionsHistoryByDateByDomain(IdentityType earnerType, Date date, String domain) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistoryByDateByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter("date", date).setParameter(DOMAIN_PARAM_NAME, domain).setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    return query.getResultList();
  }

  /**
   * Find all gamification entries by domain
   * 
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @param domain : domain filter
   * @return list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findAllActionsHistoryByDomain(IdentityType earnerType, String domain) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistoryByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DOMAIN_PARAM_NAME, domain);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    return query.getResultList();

  }

  /**
   * Find all gamification entries by domain
   * 
   * @param domain : domain filter
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @param limit : limit of the query
   * @return list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findAllActionsHistoryByDomain(String domain, IdentityType earnerType, int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistoryByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DOMAIN_PARAM_NAME, domain);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setMaxResults(limit);
    return Collections.emptyList();

  }

  /**
   * Find all gamification entries by date
   *
   * @param date : date from when entries are loaded
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @return list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findAllActionsHistoryByDate(IdentityType earnerType, Date date) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByDate",
                                                                              StandardLeaderboard.class);
    query.setParameter("date", date);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    return query.getResultList();
  }

  /**
   * Find all gamification entries by date
   * 
   * @param date : date from when entries are loaded
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @param limit : limit of the query
   * @return list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findAllActionsHistoryByDate(Date date, IdentityType earnerType, int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByDate",
                                                                              StandardLeaderboard.class);
    query.setParameter("date", date);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Get an ActionHistory record based on userId
   * 
   * @param earnerId : the userId used in projection
   * @param limit : limit of the query
   * @return list of objects of type GamificationActionsHistory
   */
  public List<GamificationActionsHistory> findActionsHistoryByEarnerId(String earnerId, int limit) {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByEarnerId",
                                                                                     GamificationActionsHistory.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    query.setMaxResults(limit);
    return query.getResultList();

  }

  /**
   * Get all ActionHistory records paginated
   * 
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @param limit : limit of the query
   * @return list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findAllActionsHistory(IdentityType earnerType, int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistory",
                                                                              StandardLeaderboard.class);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Get The last ActionHistory record
   * 
   * @param date : date from when we aim to track the user
   * @param earnerId identity id of earner
   * @return an instance of type GamificationActionsHistory
   */
  public List<GamificationActionsHistory> findActionHistoryByDateByEarnerId(Date date, String earnerId) {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.findActionHistoryByDateByEarnerId",
                                                                                     GamificationActionsHistory.class)
                                                                   .setParameter("date", date)
                                                                   .setParameter(EARNER_ID_PARAM_NAME, earnerId);


    return query.getResultList();


  }

  /**
   * Find actionsHistory by data and domain
   * 
   * @param date : date from when we aim to track user
   * @param earnerType : {@link IdentityType} USER or SPACE
   * @param domain : domain we aim to track
   * @param limit : how many records we should load from DB
   * @return a list of object of type StandardLraderboard
   */
  public List<StandardLeaderboard> findActionsHistoryByDateByDomain(Date date,
                                                                    IdentityType earnerType,
                                                                    String domain,
                                                                    int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByDateByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter("date", date).setParameter(EARNER_TYPE_PARAM_NAME, earnerType).setParameter(DOMAIN_PARAM_NAME, domain);
    query.setMaxResults(limit);
    return query.getResultList();
    
  }

  /**
   * Get earner stats
   * 
   * @param earnerId identity id of earner
   * @param fromDate a start {@link Date} used in query to filter
   * @param toDate a end {@link Date} used in query to filter
   * @return a list of objects of type PiechartLeaderboard
   */
  public List<PiechartLeaderboard> findStatsByUserId(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<PiechartLeaderboard> query = null;

    if (fromDate != null && toDate != null) {
      query = getEntityManager().createNamedQuery("GamificationActionsHistory.findStatsByUserByDates", PiechartLeaderboard.class);
      query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
           .setParameter(FROM_DATE_PARAM_NAME, fromDate)
           .setParameter(TO_DATE_PARAM_NAME, toDate);
    } else {
      query = getEntityManager().createNamedQuery("GamificationActionsHistory.findStatsByUser", PiechartLeaderboard.class);
      query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    }
    return query.getResultList();
    
  }

  /**
   * Compute for a given user the score earned for each doman
   * 
   * @param earnerId ProfileReputation
   * @return a list of objects of type
   */
  public List<ProfileReputation> findDomainScoreByIdentityId(String earnerId) {
    TypedQuery<ProfileReputation> query =
                                        getEntityManager().createNamedQuery("GamificationActionsHistory.findDomainScoreByUserId",
                                                                            ProfileReputation.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    return query.getResultList();
    
  }

  public long findUserReputationScoreBetweenDate(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreBetweenDate",
                                                                 Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
         .setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public Map<Long, Long> findUsersReputationScoreBetweenDate(List<String> earnersId, Date fromDate, Date toDate) {
    TypedQuery<Tuple> query =
                            getEntityManager().createNamedQuery("GamificationActionsHistory.findUsersReputationScoreBetweenDate",
                                                                Tuple.class);
    query.setParameter("earnersId", earnersId).setParameter("fromDate", fromDate).setParameter("toDate", toDate);
    query.setParameter(STATUS, HistoryStatus.REJECTED);

    return query.getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> Long.valueOf((String) tuple.get(0)), tuple -> (Long) tuple.get(1)));

  }

  public long findUserReputationScoreByMonth(String earnerId, Date currentMonth) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreByMonth",
                                                                 Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId).setParameter("currentMonth", currentMonth);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public long findUserReputationScoreByDomainBetweenDate(String earnerId, String domain, Date fromDate, Date toDate) {
    TypedQuery<Long> query =
                           getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreByDomainBetweenDate",
                                                               Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
         .setParameter(DOMAIN_PARAM_NAME, domain)
         .setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public List<StandardLeaderboard> findAllLeaderboardBetweenDate(IdentityType earnerType, Date fromDate, Date toDate) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findAllLeaderboardBetweenDate",
                                                                              StandardLeaderboard.class);
    query.setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    return query.getResultList();
    
  }

  /**
   * Find actionsHistory by data and domain and date and points
   * 
   * @param earnerId : earner identity id
   * @param limit : how many records we should load from DB
   * @return a list of object of type GamificationActionsHistory
   */
  public List<GamificationActionsHistory> findActionsHistoryByEarnerIdSortedByDate(String earnerId, int limit) {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByEarnerIdSortedByDate",
                                                                                     GamificationActionsHistory.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    query.setMaxResults(limit);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    return query.getResultList();

  }

  public long getTotalScore(String earnerId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationActionsHistory.computeTotalScore", Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public List<GamificationActionsHistory> getAllPointsByDomain(String domain) {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.getAllPointsByDomain",
                                                                                     GamificationActionsHistory.class);
    query.setParameter(DOMAIN_PARAM_NAME, domain);

    return query.getResultList();
  }

  public List<GamificationActionsHistory> getAllPointsWithNullDomain() {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.getAllPointsWithNullDomain",
                                                                                     GamificationActionsHistory.class);
    return query.getResultList();
     
  }

  public List<String> getDomainList() {
    TypedQuery<String> query = getEntityManager().createNamedQuery("GamificationActionsHistory.getDomainList", String.class);

    return query.getResultList();

  }

  public Long countAnnouncementsByChallenge(Long challengeId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationActionsHistory.countAnnouncementsByChallenge",
                                                                 Long.class);
    query.setParameter("challengeId", challengeId);
    try {
      Long count = query.getSingleResult();
      return count == null ? 0l : count.longValue();
    } catch (NoResultException e) {
      return 0l;
    }
  }

  public List<GamificationActionsHistory> findAllAnnouncementByChallenge(Long challengeId, int offset, int limit) {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.findAllAnnouncementByChallenge",
                                                                                     GamificationActionsHistory.class);
    query.setParameter("challengeId", challengeId);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    List<GamificationActionsHistory> resultList = query.getResultList();
    return resultList == null ? Collections.emptyList() : resultList;
  }

  public List<Long> findPopularChallengesByDate(RuleFilter ruleFilter, int offset, int limit) {
    TypedQuery<Long> query = null;
    if (StringUtils.isNotEmpty(ruleFilter.getFromDate().toString())
        && StringUtils.isNotEmpty(ruleFilter.getToDate().toString())) {
      query = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllRealizationsIdsByRuleIdByDate", Long.class);
      query.setParameter(FROM_DATE_PARAM_NAME, ruleFilter.getFromDate())
           .setParameter(TO_DATE_PARAM_NAME, ruleFilter.getToDate());
    } else {
      query = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllRealizationsIdsByRuleId", Long.class);
    }
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    List<Long> resultList = query.getResultList();
    return resultList == null ? Collections.emptyList() : resultList;
  }

  /**
   * Find realizations by filter with offset, limit.
   * 
   * @param realizationFilter : data Transfert Object {@link RealizationsFilter}
   * @param offset : the starting index, when supplied. Starts at 0.
   * @param limit : how many realizations we should load from DB
   * @return a list of object of type GamificationActionsHistory
   */
  public List<GamificationActionsHistory> findRealizationsByFilter(RealizationsFilter realizationFilter,
                                                                   int offset,
                                                                   int limit) {
    TypedQuery<GamificationActionsHistory> query = buildQueryFromFilter(realizationFilter, GamificationActionsHistory.class, false);
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    return query.getResultList();
  }

  public int countRealizationsByFilter(RealizationsFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  private <T> TypedQuery<T> buildQueryFromFilter(RealizationsFilter filter, Class<T> clazz, boolean count) {
    List<String> suffixes = new ArrayList<>();
    List<String> predicates = new ArrayList<>();
    buildPredicates(filter, suffixes, predicates);

    TypedQuery<T> query;
    String queryName = getQueryFilterName(suffixes, count);
    if (filterNamedQueries.containsKey(queryName)) {
      query = getEntityManager().createNamedQuery(queryName, clazz);
    } else {
      String queryContent = getQueryFilterContent(filter, predicates, count);
      query = getEntityManager().createQuery(queryContent, clazz);
      getEntityManager().getEntityManagerFactory().addNamedQuery(queryName, query);
      filterNamedQueries.put(queryName, true);
    }
    addQueryFilterParameters(filter, query);
    return query;
  }

  private void buildPredicates(RealizationsFilter filter, List<String> suffixes, List<String> predicates) {
    predicates.add("g.earnerType = :type");
    suffixes.add(filter.getSortField());
    suffixes.add(filter.isSortDescending() ? "Descending" : "Ascending");
    if (filter.getFromDate() != null && filter.getToDate() != null) {
      suffixes.add("Interval");
      predicates.add("g.createdDate >= :fromDate AND g.createdDate < :toDate");
    }
    if (CollectionUtils.isNotEmpty(filter.getEarnerIds())) {
      suffixes.add("Earner");
      predicates.add("g.earnerId IN (:earnerIds)");
    } 
    if (!filter.getDomainIds().isEmpty()) {
      suffixes.add("searchByProgramIds" + filter.getDomainIds());
      predicates.add("g.domainEntity.id IN (:domainIds)");
    }
  }

  private String getQueryFilterName(List<String> suffixes, boolean count) {
    String queryName;
    if (suffixes.isEmpty()) {
      queryName = count ? "GamificationActionsHistory.countRealizations" : "GamificationActionsHistory.findRealizations";
    } else {
      queryName = (count ? "GamificationActionsHistory.countRealizations" : "GamificationActionsHistory.findRealizations") + "By"
          + StringUtils.join(suffixes, "By");
    }
    return queryName;
  }

  private String getQueryFilterContent(RealizationsFilter filter, List<String> predicates, boolean count) {
    String querySelect = count ? "SELECT COUNT(g) FROM GamificationActionsHistory g "
                               : "SELECT DISTINCT g FROM GamificationActionsHistory g ";
    String orderBy = null;
    String sortDirection = filter.isSortDescending() ? " DESC" : " ASC";
    if (StringUtils.equals(filter.getSortField(), "date") || StringUtils.isAllEmpty(filter.getSortField())) {
      orderBy = " ORDER BY g.id " + sortDirection;
    } else {
      orderBy = " ORDER BY g." + filter.getSortField() + sortDirection + " ,g.id DESC ";
    }
    String queryContent;
    if (predicates.isEmpty()) {
      queryContent = querySelect;
    } else {
      queryContent = querySelect + " WHERE " + StringUtils.join(predicates, " AND ");
    }
    if (!count) {
      queryContent = queryContent + orderBy;
    }
    return queryContent;
  }

  private <T> void addQueryFilterParameters(RealizationsFilter filter, TypedQuery<T> query) {
    query.setParameter(FROM_DATE_PARAM_NAME, filter.getFromDate());
    query.setParameter(TO_DATE_PARAM_NAME, filter.getToDate());
    if (CollectionUtils.isNotEmpty(filter.getEarnerIds())) {
      query.setParameter(EARNER_IDS_PARAM_NAME, filter.getEarnerIds());
    }
    if (CollectionUtils.isNotEmpty(filter.getDomainIds())) {
      query.setParameter(DOMAIN_IDS_PARAM_NAME, filter.getDomainIds());
    }
    query.setParameter(TYPE, filter.getIdentityType());
  }
}

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
package org.exoplatform.addons.gamification.storage.dao;

import java.util.*;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.effective.*;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

public class GamificationHistoryDAO extends GenericDAOJPAImpl<GamificationActionsHistory, Long> {

  public static final String   STATUS                    = "status";

  private static final String  QUERY_FILTER_FIND_PREFIX  = "GamificationActionsHistory.findRealizationsByDate";

  private static final String  QUERY_FILTER_COUNT_PREFIX = "GamificationActionsHistory.countAllRealizations";

  private Map<String, Boolean> filterNamedQueries        = new HashMap<>();

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
    query.setParameter("earnerType", earnerType);
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
    query.setParameter("date", date).setParameter("domain", domain).setParameter("earnerType", earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("domain", domain);
    query.setParameter("earnerType", earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("domain", domain);
    query.setParameter("earnerType", earnerType);
    query.setMaxResults(limit);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("earnerType", earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("earnerType", earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    query.setMaxResults(limit);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("earnerId", earnerId);
    query.setMaxResults(limit);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("earnerType", earnerType);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    query.setMaxResults(limit);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
                                                                   .setParameter("earnerId", earnerId);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

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
    query.setParameter("date", date).setParameter("earnerType", earnerType).setParameter("domain", domain);
    query.setMaxResults(limit);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Get earner stats
   * 
   * @param earnerId identity id of earner
   * @param fromDate
   * @param toDate
   * @return a list of objects of type PiechartLeaderboard
   */
  public List<PiechartLeaderboard> findStatsByUserId(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<PiechartLeaderboard> query = null;

    if (fromDate != null && toDate != null) {
      query = getEntityManager().createNamedQuery("GamificationActionsHistory.findStatsByUserByDates", PiechartLeaderboard.class);
      query.setParameter("earnerId", earnerId).setParameter("fromDate", fromDate).setParameter("toDate", toDate);
    } else {
      query = getEntityManager().createNamedQuery("GamificationActionsHistory.findStatsByUser", PiechartLeaderboard.class);
      query.setParameter("earnerId", earnerId);
    }
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("earnerId", earnerId);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public long findUserReputationScoreBetweenDate(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreBetweenDate",
                                                                 Long.class);
    query.setParameter("earnerId", earnerId).setParameter("fromDate", fromDate).setParameter("toDate", toDate);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public long findUserReputationScoreByMonth(String earnerId, Date currentMonth) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreByMonth",
                                                                 Long.class);
    query.setParameter("earnerId", earnerId).setParameter("currentMonth", currentMonth);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public long findUserReputationScoreByDomainBetweenDate(String earnerId, String domain, Date fromDate, Date toDate) {
    TypedQuery<Long> query =
                           getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreByDomainBetweenDate",
                                                               Long.class);
    query.setParameter("earnerId", earnerId)
         .setParameter("domain", domain)
         .setParameter("fromDate", fromDate)
         .setParameter("toDate", toDate);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public List<StandardLeaderboard> findAllLeaderboardBetweenDate(IdentityType earnerType, Date fromDate, Date toDate) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("GamificationActionsHistory.findAllLeaderboardBetweenDate",
                                                                              StandardLeaderboard.class);
    query.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setParameter("earnerType", earnerType);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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
    query.setParameter("earnerId", earnerId);
    query.setMaxResults(limit);
    query.setParameter(STATUS, HistoryStatus.REJECTED);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public long getTotalScore(String earnerId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationActionsHistory.computeTotalScore", Long.class);
    query.setParameter("earnerId", earnerId);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public List<GamificationActionsHistory> getAllPointsByDomain(String domain) {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.getAllPointsByDomain",
                                                                                     GamificationActionsHistory.class);
    query.setParameter("domain", domain);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public List<GamificationActionsHistory> getAllPointsWithNullDomain() {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.getAllPointsWithNullDomain",
                                                                                     GamificationActionsHistory.class);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public List<String> getDomainList() {
    TypedQuery<String> query = getEntityManager().createNamedQuery("GamificationActionsHistory.getDomainList", String.class);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
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

  public List<GamificationActionsHistory> getAllRealizationsByDate(Date fromDate, Date toDate, int offset, int limit) {
    TypedQuery<GamificationActionsHistory> query =
                                                 getEntityManager().createNamedQuery("GamificationActionsHistory.findRealizationsByDate",
                                                                                     GamificationActionsHistory.class);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("type", IdentityType.USER);
    if (limit != 0) {
      query.setFirstResult(offset);
      query.setMaxResults(limit);
    }
    List<GamificationActionsHistory> resultList = query.getResultList();
    return resultList == null ? Collections.emptyList() : resultList;
  }

  public List<GamificationActionsHistory> findRealizationsByFilter(RealizationsFilter filter, int offset, int limit) {
    TypedQuery<GamificationActionsHistory> query = buildQueryFromFilter(filter, GamificationActionsHistory.class, false);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  public int countRealizationsByFilter(RealizationsFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  private <T> TypedQuery<T> buildQueryFromFilter(RealizationsFilter filter, Class<T> clazz, boolean count) {
    List<String> suffixes = new ArrayList<>();
    List<String> predicates = new ArrayList<>();
    List<String> datePredicates = new ArrayList<>();
    List<String> sortPredicates = new ArrayList<>();
    buildPredicates(filter, suffixes, predicates, datePredicates, sortPredicates);

    TypedQuery<T> query;
    String queryName = getQueryFilterName(suffixes, count);
    if (filterNamedQueries.containsKey(queryName)) {
      query = getEntityManager().createNamedQuery(queryName, clazz);
    } else {
      String queryContent = getQueryFilterContent(predicates, datePredicates, sortPredicates, count);
      query = getEntityManager().createQuery(queryContent, clazz);
      getEntityManager().getEntityManagerFactory().addNamedQuery(queryName, query);
      filterNamedQueries.put(queryName, true);
    }

    addQueryFilterParameters(filter, query);
    return query;
  }

  private <T> void addQueryFilterParameters(RealizationsFilter filter, TypedQuery<T> query) {
    query.setParameter("fromDate", filter.getFromDate());
    query.setParameter("toDate", filter.getToDate());
    query.setParameter("type", IdentityType.USER);
    if (filter.getIsSortedByActionTitle() == true) {
      query.setParameter("actionTitle", filter.getIsSortedByActionTitle());
    }
    if (filter.getIsSortedByRuleId() == true) {
      query.setParameter("ids", filter.getIsSortedByRuleId());
    }
    if (filter.getSortDescending() != null) {
      query.setParameter("sortDescending", filter.getSortDescending());
    }
  }

  private void buildPredicates(RealizationsFilter filter,
                               List<String> suffixes,
                               List<String> predicates,
                               List<String> datePredicates,
                               List<String> sortPredicates) {
    suffixes.add("type");
    predicates.add(":type");
    if (filter.getFromDate() != null) {
      suffixes.add("fromDate");
      datePredicates.add(":fromDate :fromDate");
    }
    if (filter.getToDate() != null) {
      suffixes.add("toDate");
      datePredicates.add(":toDate :toDate");
    }
    suffixes.add("sortDescending");
    if (filter.getIsSortedByActionTitle() == true) {
      suffixes.add("ActionTitle");
      sortPredicates.add(":actionTitle :sortDescending");
    }
    if (filter.getIsSortedByRuleId() == true) {
      suffixes.add("ids");
      sortPredicates.add(":ids :sortDescending");
    }
  }

  private String getQueryFilterName(List<String> suffixes, boolean count) {
    String queryName;
    if (suffixes.isEmpty()) {
      queryName = count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX;
    } else {
      queryName = (count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX) + "By" + StringUtils.join(suffixes, "By");
    }
    return queryName;
  }

  private String getQueryFilterContent(List<String> predicates,
                                       List<String> datePredicates,
                                       List<String> sortPredicates,
                                       boolean count) {
    String querySelect = count ? "SELECT COUNT(g) FROM GamificationActionsHistory g "
                               : "SELECT DISTINCT g FROM GamificationActionsHistory g ";
    String queryContent;
    if (predicates.isEmpty()) {
      queryContent = querySelect;
    } else {
      queryContent = querySelect + " WHERE " + StringUtils.join(predicates, " AND ") + "AND g.date BETWEEN"
          + StringUtils.join(datePredicates, " AND ") + "ORDER BY g." + StringUtils.join(sortPredicates, " AND g.");
    }
    return queryContent;
  }

}

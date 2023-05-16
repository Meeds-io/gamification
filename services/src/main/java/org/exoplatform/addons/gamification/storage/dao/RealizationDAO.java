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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.RealizationEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.ProfileReputation;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

public class RealizationDAO extends GenericDAOJPAImpl<RealizationEntity, Long> {

  private static final String        DATE_PARAM_NAME         = "date";

  private static final String        TO_DATE_PARAM_NAME      = "toDate";

  private static final String        FROM_DATE_PARAM_NAME    = "fromDate";

  private static final String        NOW_DATE_PARAM_NAME     = "nowDate";

  private static final String        EARNER_ID_PARAM_NAME    = "earnerId";

  private static final String        EARNER_IDS_PARAM_NAME   = "earnerIds";

  private static final String        DOMAIN_IDS_PARAM_NAME   = "domainIds";

  private static final String        DOMAIN_ID_PARAM_NAME    = "domainId";

  private static final String        EARNER_TYPE_PARAM_NAME  = "earnerType";

  public static final String         STATUS                  = "status";

  public static final String         TYPE                    = "type";

  private static final String        RULE_ID_PARAM_NAME      = "ruleId";

  private static final String        ACTION_TITLE_PARAM_NAME = "actionTitle";

  private static final String        RECEIVER_ID_PARAM_NAME  = "receiverId";

  private static final String        OBJECT_ID_PARAM_NAME    = "objectId";

  private static final String        OBJECT_TYPE_PARAM_NAME  = "objectType";

  private final Map<String, Boolean> filterNamedQueries      = new HashMap<>();

  /**
   * Get all ActionHistory records and convert them to list of type
   * StandardLeaderboard
   * 
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @return            list of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsAgnostic(IdentityType earnerType) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizations",
                                                                              StandardLeaderboard.class);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Find all gamification entries by domain and by type
   * 
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  date       : date from when we load gamification entries
   * @param  domainId   : domain Id
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByDateAndDomain(IdentityType earnerType, Date date, long domainId) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizationsByDateByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, date)
         .setParameter(DOMAIN_ID_PARAM_NAME, domainId)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    return query.getResultList();
  }

  /**
   * Find all gamification entries by earnerId and by type
   *
   * @param  earnerId : the userId used in projection
   * @param  type     : The Type of action
   * @return          list of objects of type {@link RealizationEntity}
   */
  public List<RealizationEntity> findRealizationsByIdentityIdAndByType(String earnerId, EntityType type) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByEarnerIdAndByType",
                                                                            RealizationEntity.class);
    query.setParameter(TYPE, type);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    return query.getResultList();
  }

  /**
   * Find all gamification entries by domain
   * 
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  domainId   : domain Id
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByDomain(IdentityType earnerType, long domainId) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizationsByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DOMAIN_ID_PARAM_NAME, domainId);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    return query.getResultList();

  }

  /**
   * Find all gamification entries by domain
   * 
   * @param  domainId   : domain id
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  limit      : limit of the query
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByDomain(long domainId, IdentityType earnerType, int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizationsByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DOMAIN_ID_PARAM_NAME, domainId);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Find all gamification entries by date
   *
   * @param  date       : date from when entries are loaded
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByDate(IdentityType earnerType, Date date) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByDate",
                                                                              StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, date);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    return query.getResultList();
  }

  /**
   * Find all gamification entries by date
   * 
   * @param  date       : date from when entries are loaded
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  limit      : limit of the query
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByDate(Date date, IdentityType earnerType, int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByDate",
                                                                              StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, date);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Get an ActionHistory record based on userId
   * 
   * @param  earnerId : the userId used in projection
   * @param  limit    : limit of the query
   * @return          list of objects of type {@link RealizationEntity}
   */
  public List<RealizationEntity> findRealizationsByEarnerId(String earnerId, int limit) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByEarnerId",
                                                                            RealizationEntity.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Get all ActionHistory records paginated
   * 
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  limit      : limit of the query
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizations(IdentityType earnerType, int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizations",
                                                                              StandardLeaderboard.class);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Get The last ActionHistory record
   * 
   * @param  date     : date from when we aim to track the user
   * @param  earnerId identity id of earner
   * @return          an instance of type {@link RealizationEntity}
   */
  public List<RealizationEntity> findRealizationsByDateAndIdentityId(Date date, String earnerId) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.findActionHistoryByDateByEarnerId",
                                                                            RealizationEntity.class)
                                                          .setParameter(DATE_PARAM_NAME, date)
                                                          .setParameter(EARNER_ID_PARAM_NAME, earnerId);

    return query.getResultList();

  }

  /**
   * Find {@link RealizationEntity} by data and domain
   * 
   * @param  date       : date from when we aim to track user
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  domainId   : domain Id we aim to track
   * @param  limit      : how many records we should load from DB
   * @return            a list of object of type StandardLraderboard
   */
  public List<StandardLeaderboard> findRealizationsByDateByDomain(Date date,
                                                                  IdentityType earnerType,
                                                                  long domainId,
                                                                  int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByDateByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, date)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType)
         .setParameter(DOMAIN_ID_PARAM_NAME, domainId);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Get earner stats
   * 
   * @param  earnerId identity id of earner
   * @param  fromDate a start {@link Date} used in query to filter
   * @param  toDate   a end {@link Date} used in query to filter
   * @return          a list of objects of type PiechartLeaderboard
   */
  public List<PiechartLeaderboard> findStatsByUserId(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<PiechartLeaderboard> query = null;

    if (fromDate != null && toDate != null) {
      query = getEntityManager().createNamedQuery("RealizationEntity.findStatsByUserByDates", PiechartLeaderboard.class);
      query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
           .setParameter(FROM_DATE_PARAM_NAME, fromDate)
           .setParameter(TO_DATE_PARAM_NAME, toDate);
    } else {
      query = getEntityManager().createNamedQuery("RealizationEntity.findStatsByUser", PiechartLeaderboard.class);
      query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    }
    return query.getResultList();

  }

  /**
   * Compute for a given user the score earned for each doman
   * 
   * @param  earnerId ProfileReputation
   * @return          a list of objects of type
   */
  public List<ProfileReputation> getScorePerDomainByIdentityId(String earnerId) {
    TypedQuery<ProfileReputation> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.findDomainScoreByUserId",
                                                                            ProfileReputation.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    return query.getResultList();

  }

  public long findUserReputationScoreBetweenDate(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.findUserReputationScoreBetweenDate",
                                                                 Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
         .setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    Long count = query.getSingleResult();
    return count == null ? 0 : count;
  }

  public Map<Long, Long> findUsersReputationScoreBetweenDate(List<String> earnersId, Date fromDate, Date toDate) {
    TypedQuery<Tuple> query =
                            getEntityManager().createNamedQuery("RealizationEntity.findUsersReputationScoreBetweenDate",
                                                                Tuple.class)
                                              .setParameter("earnersId", earnersId)
                                              .setParameter(FROM_DATE_PARAM_NAME, fromDate)
                                              .setParameter(TO_DATE_PARAM_NAME, toDate)
                                              .setParameter(STATUS, HistoryStatus.ACCEPTED);

    return query.getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> Long.valueOf((String) tuple.get(0)), tuple -> (Long) tuple.get(1)));

  }

  public long findUserReputationScoreByMonth(String earnerId, Date currentMonth) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.findUserReputationScoreByMonth",
                                                                 Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId).setParameter("currentMonth", currentMonth);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public long findUserReputationScoreByDomainBetweenDate(String earnerId, long domainId, Date fromDate, Date toDate) {
    TypedQuery<Long> query =
                           getEntityManager().createNamedQuery("RealizationEntity.findUserReputationScoreByDomainBetweenDate",
                                                               Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
         .setParameter(DOMAIN_ID_PARAM_NAME, domainId)
         .setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public List<StandardLeaderboard> findAllLeaderboardBetweenDate(IdentityType earnerType, Date fromDate, Date toDate) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllLeaderboardBetweenDate",
                                                                              StandardLeaderboard.class);
    query.setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    return query.getResultList();

  }

  /**
   * Find {@link RealizationEntity} by data and domain and date and points
   * 
   * @param  earnerId : earner identity id
   * @param  limit    : how many records we should load from DB
   * @return          a list of object of type {@link RealizationEntity}
   */
  public List<RealizationEntity> findRealizationsByIdentityIdSortedByDate(String earnerId, int limit) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByEarnerIdSortedByDate",
                                                                            RealizationEntity.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    query.setMaxResults(limit);
    query.setParameter(STATUS, HistoryStatus.ACCEPTED);
    return query.getResultList();

  }

  public long getTotalScore(String earnerId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.computeTotalScore", Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    Long count = query.getSingleResult();
    return count == null ? 0 : count.longValue();
  }

  public List<RealizationEntity> getAllPointsByDomain(long domainId) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.getAllPointsByDomain",
                                                                            RealizationEntity.class);
    query.setParameter(DOMAIN_ID_PARAM_NAME, domainId);

    return query.getResultList();
  }

  public int countRealizationsByRuleId(long ruleId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.countRealizationsByRuleId",
                                                                 Long.class);
    query.setParameter(RULE_ID_PARAM_NAME, ruleId);
    try {
      Long count = query.getSingleResult();
      return count == null ? 0 : count.intValue();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public int countRealizationsByRuleIdAndEarnerType(long ruleId, IdentityType earnerType) {
    TypedQuery<Long> query =
                           getEntityManager().createNamedQuery("RealizationEntity.countRealizationsByRuleIdAndEarnerType",
                                                               Long.class);
    query.setParameter(RULE_ID_PARAM_NAME, ruleId);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    try {
      Long count = query.getSingleResult();
      return count == null ? 0 : count.intValue();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public List<RealizationEntity> findRealizationsByRuleId(long ruleId,
                                                          int offset,
                                                          int limit,
                                                          PeriodType periodType,
                                                          IdentityType earnerType) {
    TypedQuery<RealizationEntity> query = null;
    if (periodType != null && periodType.equals(PeriodType.WEEK)) {
      if (earnerType != null) {
        query = getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByRuleIdAndDateAndEarnerType",
                                                    RealizationEntity.class);
        query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
      } else {
        query = getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByRuleIdAndDate",
                                                    RealizationEntity.class);
      }
      LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      LocalDate sunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
      Date utilFromDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
      Date utilToDate = Date.from(sunday.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
      query.setParameter(FROM_DATE_PARAM_NAME, utilFromDate).setParameter(TO_DATE_PARAM_NAME, utilToDate);
    } else {
      if (earnerType != null) {
        query = getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByRuleIdAndEarnerType",
                                                    RealizationEntity.class);

        query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
      } else {
        query = getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByRuleId",
                                                    RealizationEntity.class);
      }
    }
    query.setParameter(RULE_ID_PARAM_NAME, ruleId);
    if (offset >= 0) {
      query.setFirstResult(offset);
    }
    if (limit >= 0) {
      query.setMaxResults(limit);
    }
    List<RealizationEntity> result = query.getResultList();
    return result == null ? Collections.emptyList() : result;
  }

  public List<Long> findMostRealizedRuleIds(List<Long> spacesIds, int offset, int limit, EntityType type) {
    List<Long> resultList = null;
    if (CollectionUtils.isNotEmpty(spacesIds)) {
      TypedQuery<Long> query;
      query = getEntityManager().createNamedQuery("RealizationEntity.findMostRealizedRuleIds", Long.class);
      query.setParameter("spacesIds", spacesIds);
      LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      LocalDate sunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
      Date utilFromDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
      Date utilToDate = Date.from(sunday.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
      Date now = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
      query.setParameter(FROM_DATE_PARAM_NAME, utilFromDate)
           .setParameter(TO_DATE_PARAM_NAME, utilToDate)
           .setParameter(TYPE, type)
           .setParameter(NOW_DATE_PARAM_NAME, now);
      query.setFirstResult(offset);
      query.setMaxResults(limit);
      resultList = query.getResultList();
    }
    return resultList == null ? Collections.emptyList() : resultList;
  }

  public RealizationEntity findActionHistoryByActionTitleAndEarnerIdAndReceiverAndObjectId(String actionTitle,
                                                                                           long domainId,
                                                                                           String earnerId,
                                                                                           String receiverId,
                                                                                           String objectId,
                                                                                           String objectType) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.findActionHistoryByActionTitleAndEarnerIdAndReceiverAndObjectId",
                                                                            RealizationEntity.class);
    query.setParameter(ACTION_TITLE_PARAM_NAME, actionTitle);
    query.setParameter(DOMAIN_ID_PARAM_NAME, domainId);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    query.setParameter(RECEIVER_ID_PARAM_NAME, receiverId);
    query.setParameter(OBJECT_ID_PARAM_NAME, objectId);
    query.setParameter(OBJECT_TYPE_PARAM_NAME, objectType);
    List<RealizationEntity> resultList = query.getResultList();
    return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
  }

  public List<RealizationEntity> getRealizationsByObjectIdAndObjectType(String objectId, String objectType) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.getRealizationsByObjectIdAndObjectType",
                                                                            RealizationEntity.class);
    query.setParameter(OBJECT_ID_PARAM_NAME, objectId);
    query.setParameter(OBJECT_TYPE_PARAM_NAME, objectType);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Find realizations by filter with offset, limit.
   * 
   * @param  realizationFilter : data Transfert Object {@link RealizationFilter}
   * @param  offset            : the starting index, when supplied. Starts at 0.
   * @param  limit             : how many realizations we should load from DB
   * @return                   a list of object of type
   *                           {@link RealizationEntity}
   */
  public List<RealizationEntity> findRealizationsByFilter(RealizationFilter realizationFilter, int offset,
                                                          int limit) {
    TypedQuery<RealizationEntity> query =
                                        buildQueryFromFilter(realizationFilter,
                                                             RealizationEntity.class,
                                                             false);
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    return query.getResultList();
  }

  public int countRealizationsByFilter(RealizationFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  private <T> TypedQuery<T> buildQueryFromFilter(RealizationFilter filter, Class<T> clazz, boolean count) {
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

  private void buildPredicates(RealizationFilter filter, List<String> suffixes, List<String> predicates) {
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
      queryName = count ? "RealizationEntity.countRealizations" : "RealizationEntity.findRealizations";
    } else {
      queryName = (count ? "RealizationEntity.countRealizations" : "RealizationEntity.findRealizations") + "By"
          + StringUtils.join(suffixes, "By");
    }
    return queryName;
  }

  private String getQueryFilterContent(RealizationFilter filter, List<String> predicates, boolean count) {
    String querySelect = count ? "SELECT COUNT(g) FROM RealizationEntity g "
                               : "SELECT DISTINCT g FROM RealizationEntity g ";
    String orderBy = null;
    String sortDirection = filter.isSortDescending() ? " DESC" : " ASC";
    if (StringUtils.equals(filter.getSortField(), DATE_PARAM_NAME) || StringUtils.isAllEmpty(filter.getSortField())) {
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

  private <T> void addQueryFilterParameters(RealizationFilter filter, TypedQuery<T> query) {
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

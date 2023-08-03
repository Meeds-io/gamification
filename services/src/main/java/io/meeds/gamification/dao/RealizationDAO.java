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

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProfileReputation;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.RealizationFilter;

public class RealizationDAO extends GenericDAOJPAImpl<RealizationEntity, Long> {

  private static final String        DATE_PARAM_NAME         = "date";

  private static final String        TO_DATE_PARAM_NAME      = "toDate";

  private static final String        FROM_DATE_PARAM_NAME    = "fromDate";

  private static final String        EARNER_ID_PARAM_NAME    = "earnerId";

  private static final String        EARNER_IDS_PARAM_NAME   = "earnerIds";

  private static final String        PROGRAM_IDS_PARAM_NAME  = "programIds";

  private static final String        RULE_IDS_PARAM_NAME     = "ruleIds";

  private static final String        PROGRAM_ID_PARAM_NAME   = "domainId";

  private static final String        EARNER_TYPE_PARAM_NAME  = "earnerType";

  public static final String         STATUS_PARAM_NAME       = "status";

  private static final String        RULE_ID_PARAM_NAME      = "ruleId";

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
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
   * @param  domainId   : program Id
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByDateAndProgramId(IdentityType earnerType, Date date, long domainId) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizationsByDateByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, date)
         .setParameter(PROGRAM_ID_PARAM_NAME, domainId)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    return query.getResultList();
  }

  /**
   * Find all gamification entries by domain
   * 
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  domainId   : program Id
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByProgramId(IdentityType earnerType, long domainId) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizationsByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(PROGRAM_ID_PARAM_NAME, domainId);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    return query.getResultList();

  }

  /**
   * Find all gamification entries by domain
   * 
   * @param  domainId   : program id
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  limit      : limit of the query
   * @return            list of object of type StandardLeaderboard
   */
  public List<StandardLeaderboard> findRealizationsByProgramId(long domainId, IdentityType earnerType, int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findAllRealizationsByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(PROGRAM_ID_PARAM_NAME, domainId);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType);
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  public long getScoreByIdentityId(String earnerIdentityId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.getScoreByIdentityId",
                                                                 Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerIdentityId);
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    try {
      Long result = query.getSingleResult();
      return result == null ? 0 : result.longValue();
    } catch (NoResultException e) {
      return 0;
    }
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
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  /**
   * Find {@link RealizationEntity} by data and domain
   * 
   * @param  date       : date from when we aim to track user
   * @param  earnerType : {@link IdentityType} USER or SPACE
   * @param  domainId   : program Id we aim to track
   * @param  limit      : how many records we should load from DB
   * @return            a list of object of type StandardLraderboard
   */
  public List<StandardLeaderboard> findRealizationsByDateByProgramId(Date date,
                                                                     IdentityType earnerType,
                                                                     long domainId,
                                                                     int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.findRealizationsByDateByDomain",
                                                                              StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, date)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType)
         .setParameter(PROGRAM_ID_PARAM_NAME, domainId);
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
  public List<ProfileReputation> getScorePerProgramByIdentityId(String earnerId) {
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
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
                                              .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);

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
         .setParameter(PROGRAM_ID_PARAM_NAME, domainId)
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
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
    query.setParameter(PROGRAM_ID_PARAM_NAME, domainId);

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

  public int countRealizationsByRuleIdAndEarnerId(String earnerIdentityId, long ruleId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.countRealizationsByRuleIdAndEarnerId",
                                                                 Long.class);
    query.setParameter(RULE_ID_PARAM_NAME, ruleId);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerIdentityId);
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    try {
      Long count = query.getSingleResult();
      return count == null ? 0 : count.intValue();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public long countParticipantsBetweenDates(Date fromDate, Date toDate) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.countParticipantsBetweenDates",
                                                                 Long.class);
    query.setParameter(FROM_DATE_PARAM_NAME, fromDate);
    query.setParameter(TO_DATE_PARAM_NAME, toDate);
    query.setParameter(EARNER_TYPE_PARAM_NAME, IdentityType.USER);
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    try {
      Long count = query.getSingleResult();
      return count == null ? 0 : count.intValue();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public int countRealizationsByRuleIdAndEarnerIdSinceDate(String earnerIdentityId, long ruleId, Date sinceDate) {
    TypedQuery<Long> query =
                           getEntityManager().createNamedQuery("RealizationEntity.countRealizationsByRuleIdAndEarnerIdSinceDate",
                                                               Long.class);
    query.setParameter(DATE_PARAM_NAME, sinceDate);
    query.setParameter(RULE_ID_PARAM_NAME, ruleId);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerIdentityId);
    query.setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    try {
      Long count = query.getSingleResult();
      return count == null ? 0 : count.intValue();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public RealizationEntity findLastReadlizationByRuleIdAndEarnerIdAndReceiverAndObjectId(long ruleId,
                                                                                         String earnerId,
                                                                                         String receiverId,
                                                                                         String objectId,
                                                                                         String objectType) {
    TypedQuery<RealizationEntity> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.findReadlizationsByRuleIdAndEarnerIdAndReceiverAndObjectId",
                                                                            RealizationEntity.class);
    query.setParameter(RULE_ID_PARAM_NAME, ruleId);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId);
    query.setParameter(RECEIVER_ID_PARAM_NAME, receiverId);
    query.setParameter(OBJECT_ID_PARAM_NAME, objectId);
    query.setParameter(OBJECT_TYPE_PARAM_NAME, objectType);
    query.setMaxResults(1);

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
  public List<RealizationEntity> findRealizationsByFilter(RealizationFilter realizationFilter,
                                                          int offset,
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
    if (filter.getEarnerType() != null) {
      suffixes.add("EarnerType");
      predicates.add("g.earnerType = :" + EARNER_TYPE_PARAM_NAME);
    }

    if (filter.getFromDate() != null && filter.getToDate() != null) {
      suffixes.add("Interval");
      predicates.add("g.createdDate >= :" + FROM_DATE_PARAM_NAME + " AND g.createdDate < :" + TO_DATE_PARAM_NAME);
    }
    if (CollectionUtils.isNotEmpty(filter.getEarnerIds())) {
      suffixes.add("EarnerIds");
      predicates.add("g.earnerId IN (:" + EARNER_IDS_PARAM_NAME + ")");
    }
    if (CollectionUtils.isNotEmpty(filter.getProgramIds())) {
      suffixes.add("ProgramIds");
      predicates.add("g.domainEntity.id IN (:" + PROGRAM_IDS_PARAM_NAME + ")");
    }
    if (CollectionUtils.isNotEmpty(filter.getRuleIds())) {
      suffixes.add("RuleIds");
      predicates.add("g.ruleEntity.id IN (:" + RULE_IDS_PARAM_NAME + ")");
    }
    if (filter.getStatus() != null) {
      suffixes.add("Status");
      predicates.add("g.status = :" + STATUS_PARAM_NAME);
    }

    suffixes.add(getSortField(filter));
    suffixes.add(filter.isSortDescending() ? "Descending" : "Ascending");
  }

  private String getQueryFilterName(List<String> suffixes, boolean count) {
    String queryName;
    if (suffixes.isEmpty()) {
      queryName = count ? "RealizationEntity.countRealizations" : "RealizationEntity.findRealizations";
    } else {
      queryName = (count ? "RealizationEntity.countRealizations" : "RealizationEntity.findRealizations") + "By"
          + StringUtils.join(suffixes, "And");
    }
    return queryName;
  }

  private String getQueryFilterContent(RealizationFilter filter, List<String> predicates, boolean count) {
    String querySelect = count ? "SELECT COUNT(g) FROM RealizationEntity g "
                               : "SELECT DISTINCT g FROM RealizationEntity g ";
    String queryContent;
    if (predicates.isEmpty()) {
      queryContent = querySelect;
    } else {
      queryContent = querySelect + " WHERE " + StringUtils.join(predicates, " AND ");
    }

    if (!count) {
      String sortDirection = filter.isSortDescending() ? "DESC" : "ASC";
      String sortField = getSortField(filter);
      if (StringUtils.equals(sortField, DATE_PARAM_NAME)) {
        queryContent += " ORDER BY g.id " + sortDirection;
      } else {
        queryContent += " ORDER BY g." + sortField + " " + sortDirection + " ,g.id DESC ";
      }
    }
    return queryContent;
  }

  private <T> void addQueryFilterParameters(RealizationFilter filter, TypedQuery<T> query) {
    if (filter.getEarnerType() != null) {
      query.setParameter(EARNER_TYPE_PARAM_NAME, filter.getEarnerType());
    }
    if (filter.getFromDate() != null && filter.getToDate() != null) {
      query.setParameter(FROM_DATE_PARAM_NAME, filter.getFromDate());
      query.setParameter(TO_DATE_PARAM_NAME, filter.getToDate());
    }
    if (CollectionUtils.isNotEmpty(filter.getEarnerIds())) {
      query.setParameter(EARNER_IDS_PARAM_NAME, filter.getEarnerIds());
    }
    if (CollectionUtils.isNotEmpty(filter.getProgramIds())) {
      query.setParameter(PROGRAM_IDS_PARAM_NAME, filter.getProgramIds());
    }
    if (CollectionUtils.isNotEmpty(filter.getRuleIds())) {
      query.setParameter(RULE_IDS_PARAM_NAME, filter.getRuleIds());
    }
    if (filter.getStatus() != null) {
      query.setParameter(STATUS_PARAM_NAME, filter.getStatus());
    }
  }

  private String getSortField(RealizationFilter filter) {
    if (StringUtils.isBlank(filter.getSortField())) {
      return DATE_PARAM_NAME;
    }
    return switch (filter.getSortField()) {
    case "status", "type", "earnerId", "receiver", "objectId", "objectType", "earnerType", "actionScore", "globalScore", DATE_PARAM_NAME: {
      yield filter.getSortField();
    }
    default:
      throw new IllegalArgumentException("Unexpected Sort Field value: " + filter.getSortField());
    };
  }

}

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
import javax.persistence.Query;
import javax.persistence.TemporalType;
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

  private static final String        DATE_PARAM_NAME        = "date";

  private static final String        TO_DATE_PARAM_NAME     = "toDate";

  private static final String        FROM_DATE_PARAM_NAME   = "fromDate";

  private static final String        EARNER_ID_PARAM_NAME   = "earnerId";

  private static final String        EARNER_IDS_PARAM_NAME  = "earnerIds";

  private static final String        PROGRAM_IDS_PARAM_NAME = "programIds";

  private static final String        RULE_IDS_PARAM_NAME    = "ruleIds";

  private static final String        PROGRAM_ID_PARAM_NAME  = "domainId";

  private static final String        EARNER_TYPE_PARAM_NAME = "earnerType";

  public static final String         STATUS_PARAM_NAME      = "status";

  private static final String        RULE_ID_PARAM_NAME     = "ruleId";

  private static final String        RECEIVER_ID_PARAM_NAME = "receiverId";

  private static final String        OBJECT_ID_PARAM_NAME   = "objectId";

  private static final String        OBJECT_TYPE_PARAM_NAME = "objectType";

  private final Map<String, Boolean> filterNamedQueries     = new HashMap<>();

  public int getLeaderboardRank(IdentityType earnerType, String earnerIdentityId) {
    Query query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardRank");
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType.ordinal())
         .setParameter(EARNER_ID_PARAM_NAME, Long.parseLong(earnerIdentityId))
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED.ordinal());
    try {
      Object result = query.getSingleResult();
      return result == null ? 0 : Integer.parseInt(result.toString());
    } catch (NoResultException e) {
      return 0;
    }
  }

  public int getLeaderboardRankByDateAndProgramId(IdentityType earnerType, String earnerIdentityId, Date date, long domainId) {
    Query query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardRankByDateAndProgramId");
    query.setParameter(DATE_PARAM_NAME, date, TemporalType.DATE)
         .setParameter(PROGRAM_ID_PARAM_NAME, domainId)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType.ordinal())
         .setParameter(EARNER_ID_PARAM_NAME, Long.parseLong(earnerIdentityId))
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED.ordinal());
    try {
      Object result = query.getSingleResult();
      return result == null ? 0 : Integer.parseInt(result.toString());
    } catch (NoResultException e) {
      return 0;
    }
  }

  public int getLeaderboardRankByProgramId(IdentityType earnerType, String earnerIdentityId, long domainId) {
    Query query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardRankByProgramId");
    query.setParameter(PROGRAM_ID_PARAM_NAME, domainId)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType.ordinal())
         .setParameter(EARNER_ID_PARAM_NAME, Long.parseLong(earnerIdentityId))
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED.ordinal());
    try {
      Object result = query.getSingleResult();
      return result == null ? 0 : Integer.parseInt(result.toString());
    } catch (NoResultException e) {
      return 0;
    }
  }

  public int getLeaderboardRankByDate(IdentityType earnerType, String earnerIdentityId, Date fromDate) {
    Query query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardRankByDate");
    query.setParameter(DATE_PARAM_NAME, fromDate, TemporalType.DATE)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType.ordinal())
         .setParameter(EARNER_ID_PARAM_NAME, Long.parseLong(earnerIdentityId))
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED.ordinal());
    try {
      Object result = query.getSingleResult();
      return result == null ? 0 : Integer.parseInt(result.toString());
    } catch (NoResultException e) {
      return 0;
    }
  }

  public List<StandardLeaderboard> getLeaderboard(IdentityType earnerType, int offset, int limit) {
    TypedQuery<StandardLeaderboard> query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboard",
                                                                                StandardLeaderboard.class);
    query.setParameter(EARNER_TYPE_PARAM_NAME, earnerType)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  public List<StandardLeaderboard> getLeaderboardByProgramId(long domainId, IdentityType earnerType, int offset, int limit) {
    TypedQuery<StandardLeaderboard> query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardByProgramId",
                                                                                StandardLeaderboard.class);
    query.setParameter(PROGRAM_ID_PARAM_NAME, domainId)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  public List<StandardLeaderboard> getLeaderboardByDate(Date fromDate, IdentityType earnerType, int offset, int limit) {
    TypedQuery<StandardLeaderboard> query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardByDate",
                                                                                StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, fromDate)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  public List<StandardLeaderboard> getLeaderboardByDateAndProgramId(Date fromDate,
                                                                    IdentityType earnerType,
                                                                    long domainId,
                                                                    int offset,
                                                                    int limit) {
    TypedQuery<StandardLeaderboard> query =
                                          getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardByDateAndProgramId",
                                                                              StandardLeaderboard.class);
    query.setParameter(DATE_PARAM_NAME, fromDate)
         .setParameter(EARNER_TYPE_PARAM_NAME, earnerType)
         .setParameter(PROGRAM_ID_PARAM_NAME, domainId)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    return query.getResultList();
  }

  public long getScoreByIdentityId(String earnerIdentityId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.getScoreByIdentityId",
                                                                 Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerIdentityId)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    try {
      Long result = query.getSingleResult();
      return result == null ? 0 : result.longValue();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public List<PiechartLeaderboard> getLeaderboardStatsByIdentityId(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<PiechartLeaderboard> query = null;
    if (fromDate != null && toDate != null) {
      query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardStatsByIdentityIdAndDates",
                                                  PiechartLeaderboard.class);
      query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
           .setParameter(FROM_DATE_PARAM_NAME, fromDate)
           .setParameter(TO_DATE_PARAM_NAME, toDate)
           .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    } else {
      query = getEntityManager().createNamedQuery("RealizationEntity.getLeaderboardStatsByIdentityId", PiechartLeaderboard.class);
      query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
           .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    }
    return query.getResultList();
  }

  /**
   * Compute for a given user the score earned for each doman
   * 
   * @param earnerId ProfileReputation
   * @return a list of objects of type
   */
  public List<ProfileReputation> getScorePerProgramByIdentityId(String earnerId) {
    TypedQuery<ProfileReputation> query =
                                        getEntityManager().createNamedQuery("RealizationEntity.getScorePerProgramByIdentityId",
                                                                            ProfileReputation.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    return query.getResultList();

  }

  public long getScoreByIdentityIdAndBetweenDates(String earnerId, Date fromDate, Date toDate) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.getScoreByIdentityIdAndBetweenDates",
                                                                 Long.class);
    query.setParameter(EARNER_ID_PARAM_NAME, earnerId)
         .setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
    Long count = query.getSingleResult();
    return count == null ? 0 : count;
  }

  public Map<Long, Long> getScoreByIdentityIdsAndBetweenDates(List<String> earnersId, Date fromDate, Date toDate) {
    TypedQuery<Tuple> query =
                            getEntityManager().createNamedQuery("RealizationEntity.getScoreByIdentityIdsAndBetweenDates",
                                                                Tuple.class);
    query.setParameter("earnersId", earnersId)
         .setParameter(FROM_DATE_PARAM_NAME, fromDate)
         .setParameter(TO_DATE_PARAM_NAME, toDate)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);

    return query.getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> Long.valueOf((String) tuple.get(0)), tuple -> (Long) tuple.get(1)));

  }

  public int countRealizationsByRuleIdAndEarnerId(String earnerIdentityId, long ruleId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("RealizationEntity.countRealizationsByRuleIdAndEarnerId",
                                                                 Long.class);
    query.setParameter(RULE_ID_PARAM_NAME, ruleId)
         .setParameter(EARNER_ID_PARAM_NAME, earnerIdentityId)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
    query.setParameter(DATE_PARAM_NAME, sinceDate)
         .setParameter(RULE_ID_PARAM_NAME, ruleId)
         .setParameter(EARNER_ID_PARAM_NAME, earnerIdentityId)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
    query.setParameter(RULE_ID_PARAM_NAME, ruleId)
         .setParameter(EARNER_ID_PARAM_NAME, earnerId)
         .setParameter(RECEIVER_ID_PARAM_NAME, receiverId)
         .setParameter(OBJECT_ID_PARAM_NAME, objectId)
         .setParameter(OBJECT_TYPE_PARAM_NAME, objectType)
         .setParameter(STATUS_PARAM_NAME, RealizationStatus.ACCEPTED);
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
   * @param realizationFilter : data Transfert Object {@link RealizationFilter}
   * @param offset : the starting index, when supplied. Starts at 0.
   * @param limit : how many realizations we should load from DB
   * @return a list of object of type {@link RealizationEntity}
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
      queryName = (count ? "RealizationEntity.countRealizations" : "RealizationEntity.findRealizations") + "By" +
          StringUtils.join(suffixes, "And");
    }
    return queryName;
  }

  private String getQueryFilterContent(RealizationFilter filter, List<String> predicates, boolean count) {
    String querySelect = count ? "SELECT COUNT(g) FROM RealizationEntity g " : "SELECT DISTINCT g FROM RealizationEntity g ";
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
    case STATUS_PARAM_NAME, "type", EARNER_ID_PARAM_NAME, "receiver", OBJECT_ID_PARAM_NAME, OBJECT_TYPE_PARAM_NAME, EARNER_TYPE_PARAM_NAME, "actionScore", "globalScore", DATE_PARAM_NAME: {
      yield filter.getSortField();
    }
    default:
      throw new IllegalArgumentException("Unexpected Sort Field value: " + filter.getSortField());
    };
  }

}

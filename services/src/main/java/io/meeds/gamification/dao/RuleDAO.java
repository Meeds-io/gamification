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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.filter.RuleFilter;

public class RuleDAO extends GenericDAOJPAImpl<RuleEntity, Long> implements GenericDAO<RuleEntity, Long> {

  private static final String  DATE_PARAM_NAME           = "date";

  private static final String  EVENT_PARAM_NAME          = "event";

  private static final String  QUERY_FILTER_FIND_PREFIX  = "Rule.findAllRules";

  private static final String  QUERY_FILTER_COUNT_PREFIX = "Rule.countAllRules";

  private static final String  DOMAIN_ID_PARAM_NAME      = "domainId";

  private Map<String, Boolean> filterNamedQueries        = new HashMap<>();

  public List<Long> findHighestBudgetDomainIds(int offset, int limit) {
    TypedQuery<Tuple> query = getEntityManager().createNamedQuery("Rule.getHighestBudgetDomainIds", Tuple.class);
    query.setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());
    List<Tuple> result = query.getResultList();
    if (result == null) {
      return Collections.emptyList();
    } else {
      Stream<Long> resultStream = result.stream().map(tuple -> tuple.get(0, Long.class));
      if (offset > 0) {
        resultStream = resultStream.skip(offset);
      }
      if (limit > 0) {
        resultStream = resultStream.limit(limit);
      }
      return resultStream.toList();
    }
  }

  public List<Long> findHighestBudgetDomainIdsBySpacesIds(List<Long> spacesIds, int offset, int limit) {
    TypedQuery<Tuple> query = getEntityManager().createNamedQuery("Rule.getHighestBudgetDomainIdsBySpacesIds", Tuple.class);
    query.setParameter("spacesIds", spacesIds);
    query.setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());
    List<Tuple> result = query.getResultList();
    if (result == null) {
      return Collections.emptyList();
    } else {
      Stream<Long> resultStream = result.stream().map(tuple -> tuple.get(0, Long.class));
      if (offset > 0) {
        resultStream = resultStream.skip(offset);
      }
      if (limit > 0) {
        resultStream = resultStream.limit(limit);
      }
      return resultStream.toList();
    }
  }

  public List<RuleEntity> findActiveRulesByEvent(String event) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findActiveRulesByEvent", RuleEntity.class)
                                                     .setParameter(EVENT_PARAM_NAME, event)
                                                     .setParameter("type", EntityType.AUTOMATIC)
                                                     .setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public RuleEntity findRuleByTitle(String ruleTitle) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findRuleByTitle", RuleEntity.class);
    query.setParameter("ruleTitle", ruleTitle);
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      List<RuleEntity> ruleEntities = query.getResultList();
      return !ruleEntities.isEmpty() ? ruleEntities.get(0) : null;
    } catch (NoResultException e) {
      return null;
    }

  }

  public RuleEntity findActiveRuleByEventAndDomain(String event, long domainId) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findActiveRuleByEventAndDomain", RuleEntity.class)
                                                     .setParameter(EVENT_PARAM_NAME, event)
                                                     .setParameter(DOMAIN_ID_PARAM_NAME, domainId)
                                                     .setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      List<RuleEntity> ruleEntities = query.getResultList();
      return !ruleEntities.isEmpty() ? ruleEntities.get(0) : null;
    } catch (NoResultException e) {
      return null;
    }
  }

  public long getRulesTotalScoreByDomain(long domainId) throws PersistenceException {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("Rule.getRulesTotalScoreByDomain", Long.class)
                                               .setParameter(DOMAIN_ID_PARAM_NAME, domainId)
                                               .setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());

    Long score = query.getSingleResult();
    return score == null ? 0 : score.intValue();
  }

  public List<String> getAllEvents() throws PersistenceException {
    TypedQuery<String> query = getEntityManager().createNamedQuery("Rule.getEventList", String.class);
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public List<Long> findRulesIdsByFilter(RuleFilter filter, int offset, int limit) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, false);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    return query.getResultList();
  }

  public int countRulesByFilter(RuleFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  private <T> TypedQuery<T> buildQueryFromFilter(RuleFilter filter, Class<T> clazz, boolean count) {
    List<String> suffixes = new ArrayList<>();
    List<String> predicates = new ArrayList<>();
    buildPredicates(filter, suffixes, predicates);

    TypedQuery<T> query;
    String queryName = getQueryFilterName(suffixes, count);
    if (filterNamedQueries.containsKey(queryName)) {
      query = getEntityManager().createNamedQuery(queryName, clazz);
    } else {
      String queryContent = getQueryFilterContent(predicates, count);
      query = getEntityManager().createQuery(queryContent, clazz);
      getEntityManager().getEntityManagerFactory().addNamedQuery(queryName, query);
      filterNamedQueries.put(queryName, true);
    }

    addQueryFilterParameters(filter, query);
    return query;
  }

  private <T> void addQueryFilterParameters(RuleFilter filter, TypedQuery<T> query) { // NOSONAR
    if (StringUtils.isNotBlank(filter.getTerm())) {
      query.setParameter("term", "%" + StringUtils.lowerCase(filter.getTerm()) + "%");
    }
    if (filter.getDomainId() > 0) {
      query.setParameter(DOMAIN_ID_PARAM_NAME, filter.getDomainId());
    }
    if (CollectionUtils.isNotEmpty(filter.getSpaceIds())) {
      query.setParameter("ids", filter.getSpaceIds());
    }
    if (CollectionUtils.isNotEmpty(filter.getExcludedRuleIds())) {
      query.setParameter("excludedIds", filter.getExcludedRuleIds());
    }
    DateFilterType dateFilterType = filter.getDateFilterType();
    if (dateFilterType != null && dateFilterType != DateFilterType.ALL) {
      query.setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());
    }
    EntityFilterType entityFilterType = filter.getEntityFilterType();
    if (entityFilterType != null && entityFilterType != EntityFilterType.ALL) {
      query.setParameter("filterType", EntityType.valueOf(filter.getEntityFilterType().name()));
    }
  }

  private String getQueryFilterName(List<String> suffixes, boolean count) {
    String queryName;
    if (suffixes.isEmpty()) {
      queryName = count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX;
    } else {
      queryName = (count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX) + "By" + StringUtils.join(suffixes, "And");
    }
    return queryName;
  }

  private String getQueryFilterContent(List<String> predicates, boolean count) {
    String querySelect = count ? "SELECT COUNT(r) FROM Rule r " : "SELECT r.id FROM Rule r ";
    String orderBy = " ORDER BY r.score DESC";

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

  private void buildPredicates(RuleFilter filter, List<String> suffixes, List<String> predicates) { // NOSONAR
    suffixes.add("ExcludeDeleted");
    predicates.add("r.isDeleted = false");

    if (StringUtils.isNotBlank(filter.getTerm())) {
      suffixes.add("Term");
      predicates.add("LOWER(r.title) LIKE :term");
    }
    if (filter.getDomainId() > 0) {
      suffixes.add("Domain");
      predicates.add("r.domainEntity.id = :domainId");
    }
    if (CollectionUtils.isNotEmpty(filter.getSpaceIds())) {
      suffixes.add("Audience");
      predicates.add("(r.domainEntity.audienceId in (:ids) OR r.domainEntity.audienceId IS NULL)");
    }
    if (CollectionUtils.isNotEmpty(filter.getExcludedRuleIds())) {
      suffixes.add("ExcludeIds");
      predicates.add("r.id NOT IN :excludedIds");
    }

    DateFilterType dateFilterType = filter.getDateFilterType();
    EntityStatusType entityStatusType = filter.getEntityStatusType();
    EntityFilterType entityFilterType = filter.getEntityFilterType();

    applyDateFilter(suffixes, predicates, dateFilterType);
    applyTypeFilter(suffixes, predicates, entityFilterType);
    applyStatusFilter(suffixes, predicates, entityStatusType);
  }

  private void applyTypeFilter(List<String> suffixes, List<String> predicates, EntityFilterType entityFilterType) {
    if (entityFilterType != null && entityFilterType != EntityFilterType.ALL) {
      suffixes.add("Type");
      predicates.add("r.type = :filterType");
    }
  }

  private void applyDateFilter(List<String> suffixes, List<String> predicates, DateFilterType dateFilterType) {
    if (dateFilterType == null || dateFilterType == DateFilterType.ALL) {
      return;
    }
    switch (dateFilterType) {
    case STARTED:
      suffixes.add("StartDateAndEndDate");
      predicates.add("((r.startDate IS NULL OR r.startDate <= :date)" +
          " AND (r.endDate IS NULL OR r.endDate >= :date))");
      break;
    case NOT_STARTED:
      suffixes.add("StartDate");
      predicates.add("r.startDate > :date");
      break;
    case ENDED:
      suffixes.add("EndDate");
      predicates.add("r.endDate < :date");
      break;
    default:
      break;
    }
  }

  private void applyStatusFilter(List<String> suffixes,
                                 List<String> predicates,
                                 EntityStatusType entityStatusType) {
    if (entityStatusType == null || entityStatusType == EntityStatusType.ALL) {
      return;
    }
    boolean filterEnabledRules = entityStatusType == EntityStatusType.ENABLED;
    suffixes.add(filterEnabledRules ? "StatusEnabled" : "StatusDisabled");
    predicates.add("r.isEnabled = " + filterEnabledRules);
  }

}

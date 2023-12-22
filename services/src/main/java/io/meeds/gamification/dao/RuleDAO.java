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

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.filter.RuleFilter;

public class RuleDAO extends GenericDAOJPAImpl<RuleEntity, Long> implements GenericDAO<RuleEntity, Long> {

  private static final String  DATE_PARAM_NAME           = "date";

  private static final String  EVENT_PARAM_NAME          = "event";

  private static final String  QUERY_FILTER_FIND_PREFIX  = "Rule.findAllRules";

  private static final String  QUERY_FILTER_COUNT_PREFIX = "Rule.countAllRules";

  private static final String  DOMAIN_ID_PARAM_NAME      = "domainId";

  private final Map<String, Boolean> filterNamedQueries        = new HashMap<>();

  public List<Long> findHighestBudgetProgramIds(int offset, int limit) {
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

  public List<Long> findHighestBudgetOpenProgramIds(int offset, int limit) {
    TypedQuery<Tuple> query = getEntityManager().createNamedQuery("Rule.getHighestBudgetOpenDomainIds", Tuple.class);
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

  public List<Long> findHighestBudgetProgramIdsBySpacesIds(List<Long> spacesIds, int offset, int limit) {
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

  public RuleEntity findActiveRuleByEventAndProgramId(String event, long domainId) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findActiveRuleByEventAndDomain", RuleEntity.class)
                                                     .setParameter(EVENT_PARAM_NAME, event)
                                                     .setParameter(DOMAIN_ID_PARAM_NAME, domainId)
                                                     .setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime())
                                                     .setParameter("type", EntityType.AUTOMATIC);
    try {
      List<RuleEntity> ruleEntities = query.getResultList();
      return !ruleEntities.isEmpty() ? ruleEntities.get(0) : null;
    } catch (NoResultException e) {
      return null;
    }
  }

  public long getRulesTotalScoreByProgramId(long domainId) throws PersistenceException {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("Rule.getRulesTotalScoreByDomain", Long.class)
                                               .setParameter(DOMAIN_ID_PARAM_NAME, domainId)
                                               .setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());

    Long score = query.getSingleResult();
    return score == null ? 0 : score.intValue();
  }

  public List<Long> findRulesIdsByFilter(RuleFilter filter, int offset, int limit) {
    TypedQuery<Tuple> query = buildQueryFromFilter(filter, Tuple.class, false);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    List<Tuple> result = query.getResultList();
    if (CollectionUtils.isEmpty(result)) {
      return Collections.emptyList();
    } else {
      return result.stream().map(t -> t.get(0, Long.class)).toList();
    }
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
    String queryName = getQueryFilterName(filter.getSortBy(), filter.isSortDescending(), suffixes, count);
    if (filterNamedQueries.containsKey(queryName)) {
      query = getEntityManager().createNamedQuery(queryName, clazz);
    } else {
      String queryContent = getQueryFilterContent(filter.getSortBy(), filter.isSortDescending(), predicates, count);
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
    if (StringUtils.isNotBlank(filter.getEventName())) {
      query.setParameter(EVENT_PARAM_NAME, filter.getEventName());
    }
    if (filter.getProgramId() > 0) {
      query.setParameter(DOMAIN_ID_PARAM_NAME, filter.getProgramId());
    }
    if (CollectionUtils.isNotEmpty(filter.getSpaceIds())) {
      query.setParameter("ids", filter.getSpaceIds());
    }
    if (CollectionUtils.isNotEmpty(filter.getExcludedRuleIds())) {
      query.setParameter("excludedIds", filter.getExcludedRuleIds());
    }
    if (CollectionUtils.isNotEmpty(filter.getRuleIds())) {
      query.setParameter("ruleIds", filter.getRuleIds());
    }
    DateFilterType dateFilterType = filter.getDateFilterType();
    if (dateFilterType != null && dateFilterType != DateFilterType.ALL) {
      query.setParameter(DATE_PARAM_NAME, Calendar.getInstance().getTime());
    }
    EntityFilterType entityFilterType = filter.getType();
    if (entityFilterType != null && entityFilterType != EntityFilterType.ALL) {
      query.setParameter("filterType", EntityType.valueOf(filter.getType().name()));
    }
    if ((CollectionUtils.isNotEmpty(filter.getSpaceIds()) && !filter.isExcludeNoSpace())
        || (CollectionUtils.isEmpty(filter.getSpaceIds()) && !filter.isAllSpaces())) {
      query.setParameter("openVisibility", EntityVisibility.OPEN);
    }
  }

  private String getQueryFilterName(String sortField,
                                    boolean sortDescending,
                                    List<String> suffixes,
                                    boolean count) {
    String queryName;
    if (suffixes.isEmpty()) {
      queryName = count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX;
    } else {
      queryName = (count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX) + "By" + StringUtils.join(suffixes, "And");
    }
    if (StringUtils.isBlank(sortField)) {
      sortField = "Score";
    }
    queryName += "OrderBy" + StringUtils.capitalize(sortField) + (sortDescending ? "Desc" : "Asc");
    return queryName;
  }

  private String getQueryFilterContent(String sortField,
                                       boolean sortDescending,
                                       List<String> predicates,
                                       boolean count) {
    String querySelect = count ? "SELECT COUNT(r) FROM Rule r "
                               : "SELECT DISTINCT(r.id), " + getSortFieldName(sortField) + " FROM Rule r ";

    String queryContent;
    if (predicates.isEmpty()) {
      queryContent = querySelect;
    } else {
      queryContent = querySelect + " WHERE " + StringUtils.join(predicates, " AND ");
    }
    if (!count) {
      queryContent += " ORDER BY " + getSortFieldName(sortField) + (sortDescending ? " DESC " : " ASC ");
    }
    return queryContent;
  }

  private String getSortFieldName(String sortField) {
    if (StringUtils.isBlank(sortField)) {
      sortField = "Score";
    }
    return switch (sortField) {
    case "title": {
      yield "r.title";
    }
    case "id", "createdDate": {
      yield "r.id";
    }
    case "modifiedDate": {
      yield "r.lastModifiedDate";
    }
    case "startDate": {
      yield "r.startDate";
    }
    case "endDate": {
      yield "r.endDate";
    }
    case "type": {
      yield "r.type";
    }
    case "recurrence": {
      yield "r.recurrence";
    }
    default:
      yield "r.score";
    };
  }

  private void buildPredicates(RuleFilter filter, List<String> suffixes, List<String> predicates) { // NOSONAR
    if (!filter.isIncludeDeleted()) {
      suffixes.add("ExcludeDeleted");
      predicates.add("r.isDeleted = false");
      predicates.add("r.domainEntity.isDeleted = false");
    }

    if (CollectionUtils.isNotEmpty(filter.getRuleIds())) {
      suffixes.add("RuleIds");
      predicates.add("r.id IN :ruleIds");
    }
    if (StringUtils.isNotBlank(filter.getTerm())) {
      suffixes.add("Term");
      predicates.add("LOWER(r.title) LIKE :term");
    }
    if (StringUtils.isNotBlank(filter.getEventName())) {
      suffixes.add("Event");
      predicates.add("r.eventEntity.title = :event");
    }
    if (filter.getProgramId() > 0) {
      suffixes.add("Domain");
      predicates.add("r.domainEntity.id = :domainId");
    }
    if (CollectionUtils.isNotEmpty(filter.getSpaceIds())) {
      if (filter.isExcludeNoSpace()) {
        suffixes.add("StrictAudience");
        predicates.add("r.domainEntity.audienceId in (:ids)");
      } else {
        suffixes.add("Audience");
        predicates.add("(r.domainEntity.audienceId IS NULL OR r.domainEntity.visibility = :openVisibility OR r.domainEntity.audienceId in (:ids))");
      }
    } else if (!filter.isAllSpaces()) {
      suffixes.add("OpenAudience");
      predicates.add("(r.domainEntity.audienceId IS NULL OR r.domainEntity.visibility = :openVisibility)");
    }
    if (CollectionUtils.isNotEmpty(filter.getExcludedRuleIds())) {
      suffixes.add("ExcludeIds");
      predicates.add("r.id NOT IN :excludedIds");
    }

    DateFilterType dateFilterType = filter.getDateFilterType();
    EntityStatusType ruleStatus = filter.getStatus();
    EntityStatusType programStatus = filter.getProgramStatus();
    EntityFilterType ruleType = filter.getType();

    applyDateFilter(suffixes, predicates, dateFilterType);
    applyTypeFilter(suffixes, predicates, ruleType);
    applyStatusFilter(suffixes, predicates, ruleStatus, programStatus);

    if (StringUtils.isNotBlank(filter.getSortBy())) {
      suffixes.add("SortBy");
      suffixes.add(filter.getSortBy());
      if (filter.isSortDescending()) {
        suffixes.add("Descending");
      } else {
        suffixes.add("Ascending");
      }
    }
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
    case ACTIVE:
      suffixes.add("ActiveDate");
      predicates.add("(r.endDate IS NULL OR r.endDate > :date)");
      break;
    case STARTED:
      suffixes.add("StartDateAndEndDate");
      predicates.add("((r.startDate IS NULL OR r.startDate <= :date)" + " AND (r.endDate IS NULL OR r.endDate > :date))");
      break;
    case STARTED_WITH_END:
      suffixes.add("StartDateAndEndDateNotNull");
      predicates.add("r.endDate IS NOT NULL");
      predicates.add("r.endDate > :date");
      predicates.add("(r.startDate IS NULL OR r.startDate <= :date)");
      break;
    case UPCOMING:
      suffixes.add("StartDate");
      predicates.add("r.startDate IS NOT NULL");
      predicates.add("r.startDate > :date");
      break;
    case ENDED:
      suffixes.add("EndDate");
      predicates.add("r.endDate <= :date");
      break;
    default:
      break;
    }
  }

  private void applyStatusFilter(List<String> suffixes, // NOSONAR
                                 List<String> predicates,
                                 EntityStatusType ruleStatus,
                                 EntityStatusType programStatus) {
    boolean filterByProgramStatus = programStatus != null && programStatus != EntityStatusType.ALL;
    boolean filterByRuleStatus = ruleStatus != null && ruleStatus != EntityStatusType.ALL;
    boolean enabledRules = ruleStatus == EntityStatusType.ENABLED;
    boolean enabledPrograms = programStatus == EntityStatusType.ENABLED;
    if (filterByRuleStatus && filterByProgramStatus && !enabledRules && !enabledPrograms) { // Disabled
                                                                                            // Rules
      suffixes.add("StatusDeactivated");
      predicates.add("(r.isEnabled = false OR r.domainEntity.isEnabled = false)");
    } else {
      if (filterByRuleStatus) {
        suffixes.add(enabledRules ? "StatusEnabled" : "StatusDisabled");
        predicates.add("r.isEnabled = " + enabledRules);
      }
      if (filterByProgramStatus) {
        suffixes.add(enabledPrograms ? "ProgramStatusEnabled" : "ProgramStatusDisabled");
        predicates.add("r.domainEntity.isEnabled = " + enabledPrograms);
      }
    }
  }

}

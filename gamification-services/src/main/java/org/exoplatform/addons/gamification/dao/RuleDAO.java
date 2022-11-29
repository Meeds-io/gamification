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
package org.exoplatform.addons.gamification.dao;

import java.util.*;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import org.exoplatform.addons.gamification.constant.EntityFilterType;
import org.exoplatform.addons.gamification.constant.EntityStatusType;
import org.exoplatform.addons.gamification.constant.EntityType;
import org.exoplatform.addons.gamification.entity.RuleEntity;
import org.exoplatform.addons.gamification.model.DateFilterType;
import org.exoplatform.addons.gamification.model.RuleFilter;
import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class RuleDAO extends GenericDAOJPAImpl<RuleEntity, Long> implements GenericDAO<RuleEntity, Long> {

  private static final String  QUERY_FILTER_FIND_PREFIX  = "Rule.findAllRules";

  private static final String  QUERY_FILTER_COUNT_PREFIX = "Rule.countAllRules";

  private static final String  DOMAIN_ID                 = "domainId";

  private static final Log     LOG                       = ExoLogger.getLogger(RuleDAO.class);

  private Map<String, Boolean> filterNamedQueries        = new HashMap<>();

  public RuleEntity findEnableRuleByTitle(String ruleTitle) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findEnabledRuleByTitle", RuleEntity.class)
                                                     .setParameter("ruleTitle", ruleTitle);
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      List<RuleEntity> ruleEntities = query.getResultList();
      return !ruleEntities.isEmpty() ? ruleEntities.get(0) : null;
    } catch (NoResultException e) {
      return null;
    }

  }

  public List<Long> findHighestBudgetDomainIds(int offset, int limit) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("Rule.getHighestBudgetDomainIds", Long.class);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    return query.getResultList();

  }

  public List<RuleEntity> findEnabledRulesByEvent(String event) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findEnabledRulesByEvent", RuleEntity.class)
                                                     .setParameter("event", event);
    query.setParameter("type", EntityType.AUTOMATIC);
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

  public RuleEntity findRuleByEventAndDomain(String event, long domainId) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findRuleByEventAndDomain", RuleEntity.class)
                                                     .setParameter("event", event)
                                                     .setParameter(DOMAIN_ID, domainId);
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      List<RuleEntity> ruleEntities = query.getResultList();
      return !ruleEntities.isEmpty() ? ruleEntities.get(0) : null;
    } catch (NoResultException e) {
      return null;
    }

  }

  public List<RuleEntity> getAllRules() throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRules", RuleEntity.class);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public List<RuleEntity> getActiveRules() {
    try {
      TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getEnabledRules", RuleEntity.class)
                                                       .setParameter("isEnabled", true);
      query.setParameter("type", EntityType.AUTOMATIC);
      return query.getResultList();
    } catch (PersistenceException e) {
      LOG.error("Error : Unable to fetch active rules", e);
      return Collections.emptyList();
    }
  }

  public List<RuleEntity> getAllRulesByDomain(String domain) throws PersistenceException {

    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRulesByDomain", RuleEntity.class)
                                                     .setParameter("domain", domain);
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public List<RuleEntity> getAllRulesWithNullDomain() throws PersistenceException {

    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRulesWithNullDomain", RuleEntity.class);
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public List<String> getDomainList() throws PersistenceException {
    TypedQuery<String> query = getEntityManager().createNamedQuery("Rule.getDomainList", String.class);
    query.setParameter("type", EntityType.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public long getRulesTotalScoreByDomain(long domainId) throws PersistenceException {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("Rule.getRulesTotalScoreByDomain", Long.class)
                                               .setParameter(DOMAIN_ID, domainId);
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

  public List<String> getRuleEventsByType(EntityType ruleType) {
    TypedQuery<String> query = getEntityManager().createNamedQuery("Rule.getEventList", String.class);
    query.setParameter("type", ruleType);
    return query.getResultList();
  }

  public List<Long> getRuleIdsByType(EntityType ruleType) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("Rule.getRuleIdsByType", Long.class);
    query.setParameter("type", ruleType);
    return query.getResultList();
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

  private <T> void addQueryFilterParameters(RuleFilter filter, TypedQuery<T> query) {
    if (filter.getDomainId() > 0) {
      query.setParameter(DOMAIN_ID, filter.getDomainId());
    }
    if (CollectionUtils.isNotEmpty(filter.getSpaceIds())) {
      query.setParameter("ids", filter.getSpaceIds());
    }
    DateFilterType dateFilterType = filter.getDateFilterType();
    if (dateFilterType != null && dateFilterType != DateFilterType.ALL) {
      query.setParameter("date", Calendar.getInstance().getTime());
    }
    EntityFilterType entityFilterType = filter.getEntityFilterType();
    if (entityFilterType != null && entityFilterType != EntityFilterType.ALL) {
      query.setParameter("filterType", EntityType.valueOf(filter.getEntityFilterType().name()));
    }
    EntityStatusType entityStatusType = filter.getEntityStatusType();
    if (entityStatusType == null || entityStatusType == EntityStatusType.ALL) {
      if (filter.getEntityFilterType() != EntityFilterType.MANUAL) {
        query.setParameter("date", Calendar.getInstance().getTime());
      }
    } else {
      switch (filter.getEntityStatusType()) {
      case ENABLED:
        query.setParameter("enabled", true);
        query.setParameter("date", Calendar.getInstance().getTime());
        break;
      case DISABLED:
        query.setParameter("enabled", false);
        query.setParameter("date", Calendar.getInstance().getTime());
        break;
      default:
        break;
      }
    }
  }

  private void buildPredicates(RuleFilter filter, List<String> suffixes, List<String> predicates) {
    if (filter.getDomainId() > 0) {
      suffixes.add("Domain");
      predicates.add("r.domainEntity.id = :domainId");
    }
    if (CollectionUtils.isNotEmpty(filter.getSpaceIds())) {
      suffixes.add("Audience");
      predicates.add("r.audience in (:ids)");
    }
    if (filter.getDateFilterType() != null) {
      DateFilterType dateFilterType = filter.getDateFilterType();
      suffixes.add("Enabled");
      predicates.add("r.isEnabled = true");
      switch (dateFilterType) {
      case STARTED:
        suffixes.add("StartDateAndEndDate");
        predicates.add("r.startDate <= :date AND r.endDate >= :date");
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
    EntityFilterType entityFilterType = filter.getEntityFilterType();
    if (entityFilterType != null && entityFilterType != EntityFilterType.ALL) {
      suffixes.add("FilterType");
      predicates.add("r.type = :filterType");
    }
    if (!filter.isIncludeDeleted()) {
      suffixes.add("ExcludeDeleted");
      predicates.add("r.isDeleted = false");
    }
    EntityStatusType entityStatusType = filter.getEntityStatusType();
    if (entityStatusType == null || entityStatusType == EntityStatusType.ALL) {
      if (filter.getEntityFilterType() != EntityFilterType.MANUAL) {
        suffixes.add("FilterByALL");
        predicates.add("(r.type = 0 OR (r.startDate <= :date AND r.endDate >= :date AND r.type = 1))");
      }
    } else {
      switch (filter.getEntityStatusType()) {
      case ENABLED:
        suffixes.add("FilterByEnabled");
        predicates.add("r.isEnabled = :enabled AND (r.type = 0 OR (r.startDate <= :date AND r.endDate >= :date AND r.type = 1))");
        break;
      case DISABLED:
        suffixes.add("FilterByDisabled");
        predicates.add("r.isEnabled = :enabled AND (r.type = 0 OR (r.startDate <= :date AND r.endDate >= :date AND r.type = 1))");
        break;
      default:
        break;
      }
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
}

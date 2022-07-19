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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.addons.gamification.storage.dao;

import java.util.*;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.FilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class RuleDAO extends GenericDAOJPAImpl<RuleEntity, Long> implements GenericDAO<RuleEntity, Long> {

  private static final String  QUERY_FILTER_FIND_PREFIX  = "Rule.findAllRules";

  private static final String  QUERY_FILTER_COUNT_PREFIX = "Rule.countAllRules";

  private static final Log     LOG                       = ExoLogger.getLogger(RuleDAO.class);

  private Map<String, Boolean> filterNamedQueries        = new HashMap<>();

  public RuleEntity findEnableRuleByTitle(String ruleTitle) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findEnabledRuleByTitle", RuleEntity.class)
                                                     .setParameter("ruleTitle", ruleTitle);
    query.setParameter("type", TypeRule.AUTOMATIC);
    try {
      List<RuleEntity> ruleEntities = query.getResultList();
      return !ruleEntities.isEmpty() ? ruleEntities.get(0) : null;
    } catch (NoResultException e) {
      return null;
    }

  }

  public List<RuleEntity> findEnabledRulesByEvent(String event) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findEnabledRulesByEvent", RuleEntity.class)
                                                     .setParameter("event", event);
    query.setParameter("type", TypeRule.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public RuleEntity findRuleByTitle(String ruleTitle) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findRuleByTitle", RuleEntity.class);
    query.setParameter("ruleTitle", ruleTitle);
    query.setParameter("type", TypeRule.AUTOMATIC);
    try {
      List<RuleEntity> ruleEntities = query.getResultList();
      return !ruleEntities.isEmpty() ? ruleEntities.get(0) : null;
    } catch (NoResultException e) {
      return null;
    }

  }

  public RuleEntity findRuleByEventAndDomain(String event, String domain) throws PersistenceException {
    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findRuleByEventAndDomain", RuleEntity.class)
                                                     .setParameter("event", event)
                                                     .setParameter("domain", domain);
    query.setParameter("type", TypeRule.AUTOMATIC);
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
      query.setParameter("type", TypeRule.AUTOMATIC);
      return query.getResultList();
    } catch (PersistenceException e) {
      LOG.error("Error : Unable to fetch active rules", e);
      return Collections.emptyList();
    }
  }

  public List<RuleEntity> getAllRulesByDomain(String domain) throws PersistenceException {

    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRulesByDomain", RuleEntity.class)
                                                     .setParameter("domain", domain);
    query.setParameter("type", TypeRule.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public List<RuleEntity> getAllRulesWithNullDomain() throws PersistenceException {

    TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRulesWithNullDomain", RuleEntity.class);
    query.setParameter("type", TypeRule.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public List<String> getDomainList() throws PersistenceException {
    TypedQuery<String> query = getEntityManager().createNamedQuery("Rule.getDomainList", String.class);
    query.setParameter("type", TypeRule.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public List<String> getAllEvents() throws PersistenceException {
    TypedQuery<String> query = getEntityManager().createNamedQuery("Rule.getEventList", String.class);
    query.setParameter("type", TypeRule.AUTOMATIC);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public List<RuleEntity> findRulesByFilter(RuleFilter filter, int offset, int limit) {
    TypedQuery<RuleEntity> query = buildQueryFromFilter(filter, RuleEntity.class, false);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
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
      query.setParameter("domainId", filter.getDomainId());
    }
    if (CollectionUtils.isNotEmpty(filter.getSpaceIds())) {
      query.setParameter("ids", filter.getSpaceIds());
    }
    if (filter.getFilterType() != null && !FilterType.ALL.equals(filter.getFilterType())) {
      query.setParameter("date", Utils.parseSimpleDate( Utils.toSimpleDateFormat(new Date(System.currentTimeMillis()))));
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
    if (filter.getFilterType() != null) {
      FilterType filterType = filter.getFilterType();
      if (FilterType.STARTED.equals(filterType)) {
        suffixes.add("StartDateAndEndDate");
        predicates.add("r.startDate < :date AND r.endDate >= :date");
      }
      if (FilterType.NOT_STARTED.equals(filterType)) {
        suffixes.add("StartDate");
        predicates.add("r.startDate > :date");
      }
      if (FilterType.ENDED.equals(filterType)) {
        suffixes.add("EndDate");
        predicates.add("r.endDate < :date");
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
    String querySelect = count ? "SELECT COUNT(r) FROM Rule r " : "SELECT r FROM Rule r ";
    String orderBy = " ORDER BY r.endDate DESC";

    String queryContent;
    if (predicates.isEmpty()) {
      queryContent = querySelect ;
    } else {
      queryContent = querySelect + " WHERE " + StringUtils.join(predicates, " AND ");
    }
    if (!count) {
      queryContent = queryContent + orderBy;
    }
    return queryContent;
  }
}

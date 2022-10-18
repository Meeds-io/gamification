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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

public class DomainDAO extends GenericDAOJPAImpl<DomainEntity, Long> implements GenericDAO<DomainEntity, Long> {

  private static final String  QUERY_FILTER_FIND_PREFIX  = "GamificationDomain.findAllDomains";

  private static final String  QUERY_FILTER_COUNT_PREFIX = "GamificationDomain.countAllDomains";

  public static final String   DOMAIN_TITLE              = "domainTitle";

  private Map<String, Boolean> filterNamedQueries        = new HashMap<>();

  public DomainEntity findByIdWithOwners(Long id) {
    TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findByIdWithOwners",
                                                                         DomainEntity.class);
    query.setParameter("id", id);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public DomainEntity findEnabledDomainByTitle(String domainTitle) {
    TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findEnabledDomainByTitle",
                                                                         DomainEntity.class);
    query.setParameter(DOMAIN_TITLE, domainTitle);
    List<DomainEntity> domainEntities = query.getResultList();
    return !domainEntities.isEmpty() ? domainEntities.get(0) : null;
  }

  public DomainEntity getDomainByTitle(String domainTitle) {
    TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findDomainByTitle",
                                                                         DomainEntity.class);
    query.setParameter(DOMAIN_TITLE, domainTitle);
    List<DomainEntity> domainEntities = query.getResultList();
    return !domainEntities.isEmpty() ? domainEntities.get(0) : null;
  }

  public List<DomainEntity> getAllDomains(int offset, int limit, DomainFilter filter) {
    TypedQuery<DomainEntity> query = buildQueryFromFilter(filter, DomainEntity.class, false);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    return query.getResultList();
  }

  public int countAllDomains(DomainFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  public List<DomainEntity> getEnabledDomains() {
    TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.getEnabledDomains",
                                                                         DomainEntity.class);
    return query.getResultList();
  }

  private <T> TypedQuery<T> buildQueryFromFilter(DomainFilter filter, Class<T> clazz, boolean count) {
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

  private <T> void addQueryFilterParameters(DomainFilter filter, TypedQuery<T> query) {
    EntityFilterType entityFilterType = filter.getEntityFilterType();
    if (entityFilterType != null && entityFilterType != EntityFilterType.ALL) {
      query.setParameter("type", EntityType.valueOf(entityFilterType.name()));
    }
    if (StringUtils.isNotEmpty(filter.getDomainTitle())) {
      query.setParameter("searchingKey", "%" + filter.getDomainTitle() + "%");
    }
  }

  private void buildPredicates(DomainFilter filter, List<String> suffixes, List<String> predicates) {
    if (filter.getEntityFilterType() != null && filter.getEntityFilterType() != EntityFilterType.ALL) {
      suffixes.add("Type");
      predicates.add("d.type = :type");
    }
    if (StringUtils.isNotEmpty(filter.getDomainTitle())) {
      suffixes.add("SearchBy");
      predicates.add(" UPPER(d.title) like UPPER(:searchingKey) ");
    }
    EntityStatusType entityStatusType = filter.getEntityStatusType();
    if (entityStatusType != null && entityStatusType != EntityStatusType.ALL) {
      switch (entityStatusType) {
      case ENABLED:
        suffixes.add("EnabledStatus");
        predicates.add("d.isDeleted = false AND d.isEnabled = true");
        break;
      case DISABLED:
        suffixes.add("DisabledStatus");
        predicates.add("d.isDeleted = false AND d.isEnabled = false");
        break;
      case DELETED:
        suffixes.add("DeletedStatus");
        predicates.add("d.isDeleted = true AND d.isEnabled = false");
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
    String querySelect = count ? "SELECT COUNT(d) FROM GamificationDomain d "
                               : "SELECT DISTINCT d FROM GamificationDomain d LEFT JOIN FETCH d.owners ";
    String orderBy = " ORDER BY d.createdDate DESC";

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

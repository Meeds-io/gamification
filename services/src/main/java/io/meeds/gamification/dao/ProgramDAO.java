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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.filter.ProgramFilter;

public class ProgramDAO extends GenericDAOJPAImpl<ProgramEntity, Long> implements GenericDAO<ProgramEntity, Long> {

  private static final String        QUERY_FILTER_FIND_PREFIX  = "GamificationDomain.findDomains";

  private static final String        QUERY_FILTER_COUNT_PREFIX = "GamificationDomain.countDomains";

  public static final String         DOMAIN_TITLE              = "domainTitle";

  private final Map<String, Boolean> filterNamedQueries        = new HashMap<>();

  public ProgramEntity findByIdWithOwners(Long id) {
    TypedQuery<ProgramEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findByIdWithOwners",
                                                                         ProgramEntity.class);
    query.setParameter("id", id);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public ProgramEntity getDomainByTitle(String domainTitle) {
    TypedQuery<ProgramEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findDomainByTitle",
                                                                         ProgramEntity.class);
    query.setParameter(DOMAIN_TITLE, domainTitle);
    List<ProgramEntity> domainEntities = query.getResultList();
    return !domainEntities.isEmpty() ? domainEntities.get(0) : null;
  }

  public List<Long> getDomainsByFilter(int offset, int limit, ProgramFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, false);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    return query.getResultList();
  }

  public int countDomains(ProgramFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  public List<ProgramEntity> getEnabledDomains() {
    TypedQuery<ProgramEntity> query = getEntityManager().createNamedQuery("GamificationDomain.getEnabledDomains",
                                                                         ProgramEntity.class);
    return query.getResultList();
  }

  private <T> TypedQuery<T> buildQueryFromFilter(ProgramFilter filter, Class<T> clazz, boolean count) {
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

  private <T> void addQueryFilterParameters(ProgramFilter filter, TypedQuery<T> query) {
    if (CollectionUtils.isNotEmpty(filter.getSpacesIds())) {
      query.setParameter("spacesIds", filter.getSpacesIds());
    }
    EntityFilterType entityFilterType = filter.getEntityFilterType();
    if (entityFilterType != null && entityFilterType != EntityFilterType.ALL) {
      query.setParameter("type", EntityType.valueOf(entityFilterType.name()));
    }
    if (StringUtils.isNotEmpty(filter.getDomainTitle())) {
      query.setParameter("searchingKey", "%" + filter.getDomainTitle() + "%");
    }
    if (filter.getOwnerId() > 0) {
      query.setParameter("ownerId", filter.getOwnerId());
      if (CollectionUtils.isNotEmpty(filter.getSpacesIds())) {
        query.setParameter("spacesIds", filter.getSpacesIds());
      }
    }
  }

  private void buildPredicates(ProgramFilter filter, List<String> suffixes, List<String> predicates) {
    if (filter.getEntityFilterType() != null && filter.getEntityFilterType() != EntityFilterType.ALL) {
      suffixes.add("Type");
      predicates.add("d.type = :type");
    }
    if (StringUtils.isNotEmpty(filter.getDomainTitle())) {
      suffixes.add("SearchBy");
      predicates.add(" UPPER(d.title) like UPPER(:searchingKey) ");
    }
    if (filter.isIncludeDeleted()) {
      suffixes.add("IncludeDeleted");
    } else {
      predicates.add("d.isDeleted = false");
    }
    EntityStatusType entityStatusType = filter.getEntityStatusType();
    if (entityStatusType != null && entityStatusType != EntityStatusType.ALL) {
      switch (entityStatusType) {
      case ENABLED:
        suffixes.add("EnabledStatus");
        predicates.add("d.isEnabled = true");
        break;
      case DISABLED:
        suffixes.add("DisabledStatus");
        predicates.add("d.isEnabled = false");
        break;
      default:
        break;
      }
    }
    if (filter.getOwnerId() > 0) {
      if (CollectionUtils.isEmpty(filter.getSpacesIds())) {
        suffixes.add("ByOwnerId");
        predicates.add(":ownerId member of d.owners");
      } else {
        suffixes.add("ByOwnerOrSpaceIds");
        predicates.add("(:ownerId member of d.owners OR d.audienceId in (:spacesIds))");
      }
    } else if (CollectionUtils.isNotEmpty(filter.getSpacesIds())) {
      suffixes.add("Audience");
      predicates.add("(d.audienceId in (:spacesIds) OR d.audienceId IS NULL)");
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
                               : "SELECT d.id FROM GamificationDomain d ";
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

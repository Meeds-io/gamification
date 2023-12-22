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

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.filter.ProgramFilter;

@SuppressWarnings("deprecation")
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

  public ProgramEntity getProgramByTitle(String domainTitle) {
    TypedQuery<ProgramEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findDomainByTitle",
                                                                          ProgramEntity.class);
    query.setParameter(DOMAIN_TITLE, domainTitle);
    List<ProgramEntity> domainEntities = query.getResultList();
    return !domainEntities.isEmpty() ? domainEntities.get(0) : null;
  }

  public List<Long> getProgramIdsByFilter(int offset, int limit, ProgramFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, false);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    return query.getResultList();
  }

  public int countPrograms(ProgramFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  public int countProgramColor(String color) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationDomain.countProgramColor",
                                                                 Long.class);
    query.setParameter("color", StringUtils.upperCase(color));
    try {
      Long result = query.getSingleResult();
      return result == null ? 0 : result.intValue();
    } catch (NoResultException e) {
      return 0;
    }
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
      String queryContent = getQueryFilterContent(filter.getSortBy(), filter.isSortDescending(), predicates, count);
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
    if (filter.getOwnerId() == 0 && (CollectionUtils.isNotEmpty(filter.getSpacesIds()) || !filter.isAllSpaces())) {
      query.setParameter("openVisibility", EntityVisibility.OPEN);
    }
    EntityFilterType type = filter.getType();
    if (type != null && type != EntityFilterType.ALL) {
      query.setParameter("type", EntityType.valueOf(type.name()));
    }
    if (StringUtils.isNotEmpty(filter.getProgramTitle())) {
      query.setParameter("searchingKey", "%" + filter.getProgramTitle() + "%");
    }
    if (filter.getOwnerId() > 0) {
      query.setParameter("ownerId", filter.getOwnerId());
      if (CollectionUtils.isNotEmpty(filter.getSpacesIds())) {
        query.setParameter("spacesIds", filter.getSpacesIds());
      }
    }
  }

  private void buildPredicates(ProgramFilter filter, List<String> suffixes, List<String> predicates) { // NOSONAR
    if (filter.getType() != null && filter.getType() != EntityFilterType.ALL) {
      suffixes.add("Type");
      predicates.add("d.type = :type");
    }
    if (StringUtils.isNotEmpty(filter.getProgramTitle())) {
      suffixes.add("SearchBy");
      predicates.add(" UPPER(d.title) like UPPER(:searchingKey) ");
    }
    if (filter.isIncludeDeleted()) {
      suffixes.add("IncludeDeleted");
    } else {
      predicates.add("d.isDeleted = false");
    }
    EntityStatusType programStatus = filter.getStatus();
    if (programStatus != null && programStatus != EntityStatusType.ALL) {
      switch (programStatus) {
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
      predicates.add("(d.audienceId IS NULL OR d.visibility = :openVisibility OR d.audienceId in (:spacesIds))");
    } else if (!filter.isAllSpaces()) {
      suffixes.add("OpenAudience");
      predicates.add("(d.audienceId IS NULL OR d.visibility = :openVisibility)");
    }
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

  private String getQueryFilterName(List<String> suffixes, boolean count) {
    String queryName;
    if (suffixes.isEmpty()) {
      queryName = count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX;
    } else {
      queryName = (count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX) + "By" + StringUtils.join(suffixes, "By");
    }
    return queryName;
  }

  private String getQueryFilterContent(String sortField,
                                       boolean sortDescending,
                                       List<String> predicates,
                                       boolean count) {
    String querySelect = count ? "SELECT COUNT(d) FROM GamificationDomain d "
                               : "SELECT d.id FROM GamificationDomain d ";

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
      sortField = "title";
    }
    return switch (sortField) {
    case "title": {
      yield "d.title";
    }
    case "id", "createdDate": {
      yield "d.id";
    }
    case "modifiedDate": {
      yield "d.lastModifiedDate";
    }
    case "type": {
      yield "d.type";
    }
    case "recurrence": {
      yield "d.recurrence";
    }
    case "priority": {
      yield "d.priority";
    }
    case "audience": {
      yield "d.audienceId";
    }
    default:
      yield "d.title";
    };
  }

}

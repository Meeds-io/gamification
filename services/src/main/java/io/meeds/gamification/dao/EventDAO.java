/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
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

import java.util.*;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import io.meeds.gamification.entity.EventEntity;

import io.meeds.gamification.model.filter.EventFilter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

public class EventDAO extends GenericDAOJPAImpl<EventEntity, Long> {

  public static final String         TYPE                      = "type";

  public static final String         TRIGGERS                  = "triggers";

  private static final String        QUERY_FILTER_FIND_PREFIX  = "Event.findAllEvents";

  private static final String        QUERY_FILTER_COUNT_PREFIX = "Event.countAllEvents";

  public static final String         TITLE                     = "title";

  public static final String         TRIGGER                   = "trigger";

  private final Map<String, Boolean> filterNamedQueries        = new HashMap<>();

  /**
   * Get gamification event by event title and trigger name
   *
   * @param title event title
   * @param trigger trigger name
   * @return list of type EventEntity
   */
  public EventEntity getEventByTitleAndTrigger(String title, String trigger) {
    TypedQuery<EventEntity> query =
                                  getEntityManager().createNamedQuery("EventEntity.getEventByTitleAndTrigger", EventEntity.class);
    query.setParameter(TITLE, title);
    query.setParameter(TRIGGER, trigger);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  /**
   * Get gamification event by event title and trigger name
   *
   * @param type event type
   * @param title event title
   * @return list of type EventEntity
   */
  public EventEntity getEventByTypeAndTitle(String type, String title) {
    TypedQuery<EventEntity> query = getEntityManager().createNamedQuery("EventEntity.getEventByTypeAndTitle", EventEntity.class);
    query.setParameter(TYPE, type);
    query.setParameter(TITLE, title);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public List<EventEntity> getEventsByTitle(String title, int offset, int limit) {
    TypedQuery<EventEntity> query = getEntityManager().createNamedQuery("EventEntity.getEventsByTitle", EventEntity.class);
    query.setParameter(TITLE, title);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Get all gamification events by filter
   *
   * @param filter {@link EventFilter}
   * @param offset Offset of result
   * @param limit Limit of result
   * @return list of type EventEntity
   */
  public List<EventEntity> findEventsByFilter(EventFilter filter, int offset, int limit) {
    TypedQuery<EventEntity> query = buildQueryFromFilter(filter, EventEntity.class, false);
    if (offset > 0) {
      query.setFirstResult(offset);
    }
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Count all gamification events by filter
   *
   * @param filter {@link EventFilter}
   * @return events count
   */
  public int countEventByFilter(EventFilter filter) {
    TypedQuery<Long> query = buildQueryFromFilter(filter, Long.class, true);
    return query.getSingleResult().intValue();
  }

  private <T> TypedQuery<T> buildQueryFromFilter(EventFilter filter, Class<T> clazz, boolean count) {
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

  private String getQueryFilterName(List<String> suffixes, boolean count) {
    String queryName;
    if (suffixes.isEmpty()) {
      queryName = count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX;
    } else {
      queryName = (count ? QUERY_FILTER_COUNT_PREFIX : QUERY_FILTER_FIND_PREFIX) + "By" + StringUtils.join(suffixes, "By");
    }
    return queryName;
  }

  private <T> void addQueryFilterParameters(EventFilter filter, TypedQuery<T> query) {
    if (StringUtils.isNotBlank(filter.getType())) {
      query.setParameter(TYPE, filter.getType());
    }
    if (CollectionUtils.isNotEmpty(filter.getTriggers())) {
      query.setParameter(TRIGGERS, filter.getTriggers());
    }
  }

  private String getQueryFilterContent(List<String> predicates, boolean count) {
    String querySelect = count ? "SELECT COUNT(event) FROM EventEntity event " : "SELECT DISTINCT event FROM EventEntity event ";
    String queryContent;
    if (predicates.isEmpty()) {
      queryContent = querySelect;
    } else {
      queryContent = querySelect + " WHERE " + org.apache.commons.lang3.StringUtils.join(predicates, " AND ");
    }
    if (!count) {
      queryContent += " ORDER BY event.id  DESC ";
    }
    return queryContent;
  }

  private void buildPredicates(EventFilter filter, List<String> suffixes, List<String> predicates) {
    if (StringUtils.isNotBlank(filter.getType())) {
      suffixes.add("Type");
      predicates.add("event.type = :type");
    }
    if (CollectionUtils.isNotEmpty(filter.getTriggers())) {
      suffixes.add("Triggers");
      predicates.add("event.trigger IN :triggers");
    }
  }

}

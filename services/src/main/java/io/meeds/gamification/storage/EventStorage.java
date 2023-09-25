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
package io.meeds.gamification.storage;

import java.util.List;
import io.meeds.gamification.dao.EventDAO;
import io.meeds.gamification.entity.EventEntity;
import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.filter.EventFilter;
import io.meeds.gamification.storage.mapper.EventMapper;

public class EventStorage {

  private final EventDAO eventDAO;

  public EventStorage(EventDAO eventDAO) {
    this.eventDAO = eventDAO;
  }

  /**
   * Get all gamification events by filter
   *
   * @param eventFilter {@link EventFilter} used to filter events
   * @param offset Offset of result
   * @param limit Limit of result
   * @return {@link List} of {@link EventDTO}
   */
  public List<EventDTO> findEventsByFilter(EventFilter eventFilter, int offset, int limit) {
    return EventMapper.fromEntities(eventDAO.findEventsByFilter(eventFilter, offset, limit));
  }

  /**
   * Count gamification events by filter
   *
   * @param eventFilter {@link EventFilter} used to filter events
   * @return count events by filter
   */
  public int countEventsByFilter(EventFilter eventFilter) {
    return eventDAO.countEventByFilter(eventFilter);
  }

  /**
   * Get gamification event by event title and trigger name
   *
   * @param title event title
   * @param trigger trigger name
   * @return {@link EventDTO}
   */
  public EventDTO getEventByTitleAndTrigger(String title, String trigger) {
    return EventMapper.fromEntity(eventDAO.getEventByTitleAndTrigger(title, trigger));
  }

  /**
   * Get gamification event by event title and trigger name
   *
   * @param type event type
   * @param title event title
   * @return {@link EventDTO}
   */
  public EventDTO getEventByTypeAndTitle(String type, String title) {
    return EventMapper.fromEntity(eventDAO.getEventByTypeAndTitle(type, title));
  }

  /**
   * save Event
   *
   * @param eventDTO {@link EventDTO} to create
   */
  public EventDTO saveEvent(EventDTO eventDTO) {
    EventEntity eventEntity = EventMapper.toEntity(eventDTO);

    if (eventEntity.getId() == null) {
      eventEntity = eventDAO.create(eventEntity);
    } else {
      eventEntity = eventDAO.update(eventEntity);
    }
    return EventMapper.fromEntity(eventEntity);
  }

  /**
   * Updates Event
   *
   * @param eventDTO {@link EventDTO} to update
   */
  public void updateEvent(EventDTO eventDTO) {
    EventEntity eventEntity = EventMapper.toEntity(eventDTO);
    eventDAO.update(eventEntity);
  }

  /**
   * Retrieves gamification event by event id
   *
   * @param eventId Event Identifier
   * @return {@link EventDTO}
   */
  public EventDTO getEventById(long eventId) {
    return EventMapper.fromEntity(eventDAO.find(eventId));
  }
}

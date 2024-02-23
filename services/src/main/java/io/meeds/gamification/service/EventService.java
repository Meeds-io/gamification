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
package io.meeds.gamification.service;

import java.util.List;

import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.filter.EventFilter;
import io.meeds.gamification.plugin.EventPlugin;
import org.exoplatform.commons.exception.ObjectNotFoundException;

public interface EventService {

  /**
   * Add a new {@link EventPlugin} for a given Event Type
   *
   * @param eventPlugin {@link EventPlugin}
   */
  void addPlugin(EventPlugin eventPlugin);

  /**
   * Removes a {@link EventPlugin} identified by its eventType
   *
   * @param eventType Event type
   */
  void removePlugin(String eventType);

  /**
   * Gets a {@link EventPlugin} identified by its eventType
   *
   * @return eventPlugin {@link EventPlugin}
   */
  EventPlugin getEventPlugin(String eventType);

  /**
   * Get events by filter using offset and limit.
   *
   * @param eventFilter {@link EventFilter} used to filter events
   * @param offset Offset of result
   * @param limit Limit of result
   * @return {@link List} of {@link EventDTO}
   */
  List<EventDTO> getEvents(EventFilter eventFilter, int offset, int limit);

  /**
   * @param title {@link EventDTO} title
   * @param offset Offset of result
   * @param limit Limit of result
   * @return {@link List} of {@link EventDTO}
   */
  List<EventDTO> getEventsByTitle(String title, int offset, int limit);

  /**
   * @param eventFilter {@link EventFilter} used to count associated events
   * @return count events by filter
   */
  int countEvents(EventFilter eventFilter);

  /**
   * Get gamification event by event title and trigger name
   *
   * @param title event title
   * @param trigger trigger name
   * @return {@link EventDTO}
   */
  EventDTO getEventByTitleAndTrigger(String title, String trigger);

  /**
   * Add Event to DB
   *
   * @param eventDTO {@link EventDTO} to create
   * @return {@link EventDTO}
   */
  EventDTO createEvent(EventDTO eventDTO);

  /**
   * Update event
   *
   * @param eventDTO {@link EventDTO} to update
   * @return updated {@link EventDTO}
   * @throws ObjectNotFoundException when event doesn't exists
   */
  EventDTO updateEvent(EventDTO eventDTO) throws ObjectNotFoundException;

  /**
   * Get events by canceller trigger
   *
   * @param cancellerTrigger canceller trigger
   * @param eventType        event Type
   * @param offset           Offset of result
   * @param limit            Limit of result
   * @return {@link List} of {@link EventDTO}
   */
  default List<EventDTO> getEventsByCancellerTrigger(String eventType, String cancellerTrigger, int offset, int limit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Retrieves gamification event by event id
   *
   * @param eventId Event Identifier
   * @return {@link EventDTO}
   */
  EventDTO getEvent(long eventId);

  /**
   * Deletes an existing event
   *
   * @param eventId Event technical identifier to delete
   * @return deleted {@link EventDTO}
   */
  EventDTO deleteEventById(long eventId) throws ObjectNotFoundException;

}

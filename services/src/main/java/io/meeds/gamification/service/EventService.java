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
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;

public interface EventService {

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
   * @throws ObjectAlreadyExistsException when event already exists
   */
  EventDTO createEvent(EventDTO eventDTO) throws ObjectAlreadyExistsException;

  /**
   * Retrieves gamification event by event id
   *
   * @param eventId Event Identifier
   * @return {@link EventDTO}
   */
  EventDTO getEvent(long eventId);

  /**
   * Enables/disables connector project event
   *
   * @param eventId event Id
   * @param projectId connector remote project id
   * @param enabled true to enabled, else false
   * @param currentUser user name attempting to enables/disables event.
   * @throws IllegalAccessException when user is not authorized enables/disables
   *           connector project event
   */
  void setEventEnabledForProject(long eventId, long projectId, boolean enabled, String currentUser) throws IllegalAccessException,
                                                                                                    ObjectNotFoundException;

}

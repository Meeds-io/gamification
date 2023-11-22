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
package io.meeds.gamification.service.impl;

import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.filter.EventFilter;
import io.meeds.gamification.service.EventService;
import io.meeds.gamification.storage.EventStorage;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.*;

public class EventServiceImpl implements EventService {

  private final EventStorage eventStorage;

  public EventServiceImpl(EventStorage eventStorage) {
    this.eventStorage = eventStorage;
  }

  @Override
  public List<EventDTO> getEvents(EventFilter eventFilter, int offset, int limit) {
    return eventStorage.findEventsByFilter(eventFilter, offset, limit);
  }

  @Override
  public int countEvents(EventFilter eventFilter) {
    return eventStorage.countEventsByFilter(eventFilter);
  }

  @Override
  public EventDTO getEventByTitleAndTrigger(String title, String trigger) {
    return eventStorage.getEventByTitleAndTrigger(title, trigger);
  }

  @Override
  public EventDTO getEventByTypeAndTitle(String type, String title) {
    return eventStorage.getEventByTypeAndTitle(type, title);
  }

  @Override
  public EventDTO createEvent(EventDTO eventDTO) throws ObjectAlreadyExistsException {
    if (eventDTO == null) {
      throw new IllegalArgumentException("event object is mandatory");
    }
    EventDTO similarEvent = eventStorage.getEventByTitleAndTrigger(eventDTO.getTitle(), eventDTO.getTrigger());
    if (similarEvent != null) {
      throw new ObjectAlreadyExistsException("Event with same title and trigger already exist");
    }
    return eventStorage.saveEvent(eventDTO);
  }

  @Override
  public EventDTO updateEvent(EventDTO eventDTO) throws ObjectNotFoundException {
    if (eventDTO.getId() <= 0) {
      throw new IllegalArgumentException("Event id must not be null");
    }
    EventDTO storedEvent = eventStorage.getEventById(eventDTO.getId());
    if (storedEvent == null) {
      throw new ObjectNotFoundException("Event with id " + eventDTO.getId() + " is not found");
    }
    return eventStorage.saveEvent(eventDTO);
  }

  public EventDTO getEvent(long eventId) {
    return eventStorage.getEventById(eventId);
  }

  @Override
  public EventDTO deleteEventById(long eventId) throws ObjectNotFoundException {
    EventDTO eventDTO;
    try {
      eventDTO = eventStorage.deleteEventById(eventId);
    } catch (ObjectNotFoundException e) {
      throw new ObjectNotFoundException("Event with id " + eventId + " is not found");
    }
    return eventDTO;
  }
}

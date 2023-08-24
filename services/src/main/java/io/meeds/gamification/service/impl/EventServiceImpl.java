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
import io.meeds.gamification.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.*;

public class EventServiceImpl implements EventService {

  public static final String ENABLED = ".enabled";

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

  public EventDTO getEvent(long eventId) {
    return eventStorage.getEventById(eventId);
  }

  @Override
  public void setEventEnabledForProject(long eventId,
                                        long projectId,
                                        boolean enabled,
                                        String currentUser) throws IllegalAccessException, ObjectNotFoundException {
    if (!Utils.isRewardingManager(currentUser)) {
      throw new IllegalAccessException("The user is not authorized to enable/disable event");
    }
    EventDTO eventDTO = eventStorage.getEventById(eventId);
    if (eventDTO == null) {
      throw new ObjectNotFoundException("event not found");
    }
    Map<String, String> eventProperties = eventDTO.getProperties();
    if (eventProperties != null && !eventProperties.isEmpty()) {
      String eventProjectStatus = eventProperties.get(projectId + ENABLED);
      if (StringUtils.isNotBlank(eventProjectStatus)) {
        eventProperties.remove(projectId + ENABLED);
        eventProperties.put(projectId + ENABLED, String.valueOf(enabled));
        eventDTO.setProperties(eventProperties);
      }
    } else {
      Map<String, String> properties = new HashMap<>();
      properties.put(projectId + ENABLED, String.valueOf(enabled));
      eventDTO.setProperties(properties);
    }
    eventStorage.updateEvent(eventDTO);
  }
}

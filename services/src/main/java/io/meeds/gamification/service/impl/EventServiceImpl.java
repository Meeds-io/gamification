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
import io.meeds.gamification.model.Trigger;
import io.meeds.gamification.model.filter.EventFilter;
import io.meeds.gamification.plugin.EventPlugin;
import io.meeds.gamification.service.EventRegistry;
import io.meeds.gamification.service.EventService;
import io.meeds.gamification.storage.EventStorage;
import org.apache.commons.collections.CollectionUtils;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.*;

public class EventServiceImpl implements EventService {

  private final EventStorage             eventStorage;

  private final EventRegistry            eventRegistry;

  private final Map<String, EventPlugin> eventPlugins = new HashMap<>();

  public EventServiceImpl(EventStorage eventStorage, EventRegistry eventRegistry) {
    this.eventStorage = eventStorage;
    this.eventRegistry = eventRegistry;
  }

  @Override
  public void addPlugin(EventPlugin eventPlugin) {
    eventPlugins.put(eventPlugin.getEventType(), eventPlugin);
  }

  @Override
  public void removePlugin(String eventType) {
    eventPlugins.remove(eventType);
  }

  @Override
  public EventPlugin getEventPlugin(String eventName) {
    return eventPlugins.values()
                       .stream()
                       .filter(eventPlugin -> eventPlugin.getTriggers().contains(eventName))
                       .findFirst()
                       .orElse(null);
  }

  @Override
  public List<EventDTO> getEvents(EventFilter eventFilter, int offset, int limit) {
    return eventStorage.findEventsByFilter(eventFilter, offset, limit);
  }

  @Override
  public List<EventDTO> getEventsByTitle(String title, int offset, int limit) {
    return eventStorage.getEventsByTitle(title, offset, limit);
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
  public EventDTO createEvent(EventDTO eventDTO) {
    if (eventDTO == null) {
      throw new IllegalArgumentException("event object is mandatory");
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

  @Override
  public List<EventDTO> getEventsByCancellerTrigger(String eventType, String cancellerTrigger, int offset, int limit) {
    List<String> triggers = eventRegistry.getTriggers(eventType)
                                         .stream()
                                         .filter(t -> CollectionUtils.isNotEmpty(t.getCanceller())
                                             && t.getCanceller().contains(cancellerTrigger))
                                         .map(Trigger::getTitle)
                                         .toList();
    EventFilter eventFilter = new EventFilter();
    eventFilter.setTriggers(triggers);

    return getEvents(eventFilter, offset, limit);
  }

  @Override
  public boolean canVariableRewarding(String triggerType, String triggerName) {
    Trigger trigger = eventRegistry.getTrigger(triggerType, triggerName);
    if (trigger != null) {
      return trigger.isCanVariableRewarding();
    }
    return false;
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

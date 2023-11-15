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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.filter.EventFilter;
import io.meeds.gamification.plugin.EventConfigPlugin;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import io.meeds.gamification.service.EventRegistry;
import io.meeds.gamification.service.EventService;

public class EventRegistryImpl implements Startable, EventRegistry {

  private static final Log                     LOG                  = ExoLogger.getLogger(EventRegistryImpl.class);

  private final Map<String, EventConfigPlugin> eventConfigPluginMap = new HashMap<>();

  private final EventService                   eventService;

  public EventRegistryImpl(EventService eventService) {
    this.eventService = eventService;
  }

  @Override
  public void addPlugin(EventConfigPlugin eventConfigPlugin) {
    if (StringUtils.isNotBlank(eventConfigPlugin.getName())) {
      eventConfigPluginMap.put(eventConfigPlugin.getName(), eventConfigPlugin);
    }
  }

  @Override
  public boolean remove(EventConfigPlugin eventConfigPlugin) {
    eventConfigPluginMap.remove(eventConfigPlugin.getName());
    return true;
  }

  @Override
  public void start() {
    for (EventConfigPlugin eventConfigPlugin : eventConfigPluginMap.values()) {
      EventDTO eventConfig = eventConfigPlugin.getEvent();
      EventDTO eventDTO = eventService.getEventByTypeAndTitle(eventConfig.getType(), eventConfig.getTitle());
      if (eventDTO == null) {
        eventDTO = new EventDTO();
        eventDTO.setTitle(eventConfig.getTitle());
        eventDTO.setType(eventConfig.getType());
        eventDTO.setTrigger(eventConfig.getTrigger());
        eventDTO.setCancellerEvents(eventConfig.getCancellerEvents());
        try {
          eventService.createEvent(eventDTO);
        } catch (ObjectAlreadyExistsException e) {
          throw new IllegalStateException(String.format("Event '%s' seems already exists", eventConfig.getTitle()), e);
        }
      } else if (!Objects.equals(eventDTO.getCancellerEvents(), eventConfig.getCancellerEvents())) {
        eventDTO.setCancellerEvents(eventConfig.getCancellerEvents());
        try {
          eventService.updateEvent(eventDTO);
        } catch (ObjectNotFoundException e) {
          throw new IllegalStateException(String.format("Event '%s' not found", eventConfig.getTitle()), e);
        }
      }
    }
    List<EventDTO> eventDAOList = eventService.getEvents(new EventFilter(), 0, -1);
    eventDAOList.forEach(eventDTO -> {
      EventConfigPlugin eventConfigPlugin = eventConfigPluginMap.values()
                                                                .stream()
                                                                .filter(eventConfig -> eventConfig.getEvent()
                                                                                                  .getType()
                                                                                                  .equals(eventDTO.getType())
                                                                    && eventConfig.getEvent()
                                                                                  .getTitle()
                                                                                  .equals(eventDTO.getTitle()))
                                                                .findFirst()
                                                                .orElse(null);
      if (eventConfigPlugin == null) {
        try {
          eventService.deleteEventById(eventDTO.getId());
        } catch (ObjectNotFoundException e) {
          LOG.warn("Error while clean gamification event {}", eventDTO.getId(), e);
        }
      }
    });
  }

  @Override
  public void stop() {
    // Nothing to change
  }
}

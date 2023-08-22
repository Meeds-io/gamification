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
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventServiceImpl implements EventService {

  private static final Scope DISABLED_EVENTS_SCOPE  = Scope.APPLICATION.id("disabledEvents");

  private final EventStorage eventStorage;

  private final SettingService settingService;

  public EventServiceImpl(EventStorage eventStorage, SettingService settingService) {
    this.eventStorage = eventStorage;
    this.settingService = settingService;
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
  public void createEvent(EventDTO eventDTO) throws ObjectAlreadyExistsException {
    if (eventDTO == null) {
      throw new IllegalArgumentException("event object is mandatory");
    }
    EventDTO similarEvent = eventStorage.getEventByTitleAndTrigger(eventDTO.getTitle(), eventDTO.getTrigger());
    if (similarEvent != null) {
      throw new ObjectAlreadyExistsException("Event with same title and trigger already exist");
    }
    eventStorage.saveEvent(eventDTO);
  }

  @Override
  public boolean isEventEnabled(String type, long projectId, String event) {
    List<String> disabledEventList = new ArrayList<>();
    SettingValue<?> settingValue = settingService.get(Context.GLOBAL.id(type), DISABLED_EVENTS_SCOPE, String.valueOf(projectId));
    if (settingValue != null && settingValue.getValue() != null && StringUtils.isNotBlank(settingValue.getValue().toString())) {
      disabledEventList = Arrays.stream(settingValue.getValue().toString().split(":")).toList();
    }
    return !disabledEventList.contains(event);
  }

  @Override
  public void setEventEnabledForProject(String type,
                                        long projectId,
                                        String event,
                                        boolean enabled,
                                        String currentUser) throws IllegalAccessException {
    if (!Utils.isRewardingManager(currentUser)) {
      throw new IllegalAccessException("The user is not authorized to enable/disable event");
    }
    List<String> disabledEventList = new ArrayList<>();
    SettingValue<?> settingValue = settingService.get(Context.GLOBAL.id(type), DISABLED_EVENTS_SCOPE, String.valueOf(projectId));
    if (settingValue != null && settingValue.getValue() != null && StringUtils.isNotBlank(settingValue.getValue().toString())) {
      disabledEventList = Arrays.stream(settingValue.getValue().toString().split(":")).collect(Collectors.toList());
    }
    if (!enabled) {
      if (!disabledEventList.contains(event)) {
        disabledEventList.add(event);
      }
    } else {
      disabledEventList.remove(event);
    }
    String disabledEvents = String.join(":", disabledEventList);
    settingService.set(Context.GLOBAL.id(type),
                       DISABLED_EVENTS_SCOPE,
                       String.valueOf(projectId),
                       SettingValue.create(disabledEvents));
  }
}

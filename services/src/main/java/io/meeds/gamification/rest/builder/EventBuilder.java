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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.rest.builder;

import java.util.List;

import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.rest.model.EventRestEntity;
import io.meeds.gamification.service.EventService;
import org.apache.commons.lang3.StringUtils;

public class EventBuilder {

  private EventBuilder() {
    // Class with static methods
  }

  public static EventRestEntity toRestEntity(EventService eventService, String type, long projectId, EventDTO eventDTO) {
    Boolean isEnabledEvent = null;
    if (StringUtils.isNotBlank(type) && projectId > 0) {
      isEnabledEvent = eventService.isEventEnabled(type, projectId, eventDTO.getTitle());
    }

    return new EventRestEntity(eventDTO.getTitle(),
                               eventDTO.getType(),
                               eventDTO.getTrigger(),
                               isEnabledEvent,
                               eventDTO.isCanCancel());
  }

  public static List<EventRestEntity> toRestEntities(EventService eventService,
                                                     String type,
                                                     long projectId,
                                                     List<EventDTO> eventDTOList) {
    return eventDTOList.stream().map(eventDTO -> toRestEntity(eventService, type, projectId, eventDTO)).toList();
  }
}

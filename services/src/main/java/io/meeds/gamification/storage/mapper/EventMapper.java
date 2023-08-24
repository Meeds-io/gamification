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
package io.meeds.gamification.storage.mapper;

import io.meeds.gamification.entity.EventEntity;
import io.meeds.gamification.model.EventDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class EventMapper {

  private EventMapper() {
    // Class with static methods
  }

  public static EventEntity toEntity(EventDTO eventDTO) {
    if (eventDTO == null) {
      return null;
    }
    EventEntity eventEntity = new EventEntity();
    if (eventDTO.getId() > 0) {
      eventEntity.setId(eventDTO.getId());
    }
    if (StringUtils.isNotBlank(eventDTO.getTitle())) {
      eventEntity.setTitle(eventDTO.getTitle());
    }
    if (StringUtils.isNotBlank(eventDTO.getType())) {
      eventEntity.setType(eventDTO.getType());
    }
    if (StringUtils.isNotBlank(eventDTO.getTrigger())) {
      eventEntity.setTrigger(eventDTO.getTrigger());
    }
    if (eventDTO.getProperties() != null) {
      eventEntity.setProperties(eventDTO.getProperties());
    }
    eventEntity.setCanCancel(eventDTO.isCanCancel());
    return eventEntity;
  }

  public static EventDTO fromEntity(EventEntity eventEntity) {
    if (eventEntity == null) {
      return null;
    }
    return new EventDTO(eventEntity.getId() != null ? eventEntity.getId() : 0L,
                        eventEntity.getTitle(),
                        eventEntity.getType(),
                        eventEntity.getTrigger(),
                        eventEntity.isCanCancel(),
                        eventEntity.getProperties());
  }

  public static List<EventDTO> fromEntities(List<EventEntity> eventEntities) {
    return eventEntities.stream().filter(Objects::nonNull).map(EventMapper::fromEntity).toList();
  }
}

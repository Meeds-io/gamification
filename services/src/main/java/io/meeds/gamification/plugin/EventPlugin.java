/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.plugin;

import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.service.EventService;
import org.exoplatform.container.component.BaseComponentPlugin;

import java.util.List;

/**
 * A plugin that will be used by {@link EventService} to check event by its
 * properties
 */
public abstract class EventPlugin extends BaseComponentPlugin {

  /**
   * @return event types that plugin handles
   */
  public abstract String getEventType();

  /**
   * @return List of available triggers
   */
  public abstract List<String> getTriggers();

  /**
   * Check if event properties match properties coming from an external trigger
   */
  public abstract boolean isValidEvent(EventDTO eventDTO, String triggerDetails);
}

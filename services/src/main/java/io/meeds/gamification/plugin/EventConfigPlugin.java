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
package io.meeds.gamification.plugin;

import io.meeds.gamification.model.EventDTO;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;

@Getter
public class EventConfigPlugin extends BaseComponentPlugin {

  private static final String EVENT_PARAM_NAME = "event";

  private EventDTO            event;

  public EventConfigPlugin(InitParams params) {
    if (params != null && params.containsKey(EVENT_PARAM_NAME)) {
      this.event = (EventDTO) params.getObjectParam(EVENT_PARAM_NAME).getObject();
    }
    if (this.event == null || StringUtils.isBlank(this.event.getTitle()) || StringUtils.isBlank(this.event.getType())
        || StringUtils.isBlank(this.event.getTrigger())) {
      throw new IllegalStateException("Event is mandatory");
    }
  }

  @Override
  public String getName() {
    return this.event.getTitle();
  }
}

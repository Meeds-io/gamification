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

import lombok.Getter;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

@Getter
public class EventConfigPlugin extends BaseComponentPlugin {

  private String  title;

  private String  type;

  private String  trigger;

  private boolean canCancel;

  public EventConfigPlugin(InitParams params) {
    ValueParam titleParam = params.getValueParam("event-title");
    if (titleParam != null) {
      title = titleParam.getValue();
    }
    ValueParam typeParam = params.getValueParam("event-type");
    if (typeParam != null) {
      type = typeParam.getValue();
    }
    ValueParam triggerParam = params.getValueParam("event-trigger");
    if (triggerParam != null) {
      trigger = triggerParam.getValue();
    }
    ValueParam canCancelParam = params.getValueParam("event-can-cancel");
    if (canCancelParam != null) {
      canCancel = Boolean.parseBoolean(canCancelParam.getValue());
    }
  }
}

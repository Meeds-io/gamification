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

import io.meeds.gamification.model.Trigger;
import io.meeds.gamification.plugin.EventConfigPlugin;
import org.apache.commons.lang3.StringUtils;
import org.picocontainer.Startable;
import io.meeds.gamification.service.EventRegistry;

public class EventRegistryImpl implements EventRegistry {

  private final Map<String, EventConfigPlugin> eventConfigPluginMap = new HashMap<>();

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
  public Trigger getTrigger(String triggerType, String triggerName) {
    if (StringUtils.isBlank(triggerType) || StringUtils.isBlank(triggerName)) {
      return null;
    }
    return eventConfigPluginMap.values()
                               .stream()
                               .filter(eventConfigPlugin -> eventConfigPlugin.getEvent().getType().equals(triggerType)
                                   && eventConfigPlugin.getEvent().getTrigger().equals(triggerName))
                               .map(this::fromEventConfig)
                               .findFirst()
                               .orElse(null);
  }

  @Override
  public List<Trigger> getTriggers(String connectorName) {
    if (StringUtils.isNotBlank(connectorName)) {
      return eventConfigPluginMap.values()
                                 .stream()
                                 .filter(eventConfigPlugin -> eventConfigPlugin.getEvent().getType().equals(connectorName))
                                 .map(this::fromEventConfig)
                                 .toList();
    }
    return eventConfigPluginMap.values().stream().map(this::fromEventConfig).toList();
  }

  private Trigger fromEventConfig(EventConfigPlugin eventConfigPlugin) {
    return new Trigger(eventConfigPlugin.getEvent().getTrigger(),
                       eventConfigPlugin.getEvent().getType(),
                       eventConfigPlugin.getEvent().getCancellerEvents(),
                       eventConfigPlugin.isCanVariableRewarding());
  }
}

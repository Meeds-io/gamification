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
package io.meeds.gamification.service;

import io.meeds.gamification.model.Trigger;
import io.meeds.gamification.plugin.EventConfigPlugin;
import java.util.List;

public interface EventRegistry {

  /**
   * Add a new {@link EventConfigPlugin} for a given trigger name
   *
   * @param eventConfigPlugin {@link EventConfigPlugin}
   */
  void addPlugin(EventConfigPlugin eventConfigPlugin);

  /**
   * Removes a {@link EventConfigPlugin}
   *
   * @param eventConfigPlugin {@link EventConfigPlugin}
   */
  boolean remove(EventConfigPlugin eventConfigPlugin);

  /**
   * Gets a configured triggers by type and name
   *
   * @param triggerType trigger type
   * @param triggerName trigger name
   * @return {@link Trigger}
   */
  Trigger getTrigger(String triggerType, String triggerName);

  /**
   * Gets a all configured triggers by type
   *
   * @param connectorName connector name
   * @return {@link List} of {@link Trigger}
   */
  List<Trigger> getTriggers(String connectorName);

}

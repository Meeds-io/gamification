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

import io.meeds.gamification.model.TriggerProperties;

public interface TriggerService {

  /**
   * Retrieves gamification trigger properties
   *
   * @param triggerName trigger name
   * @return {@link TriggerProperties}
   */
  TriggerProperties getTriggerProperties(String triggerName);

  /**
   * Enables/disables organization trigger
   *
   * @param triggerName trigger name
   * @param accountId account Id
   * @param enabled true to enabled, else false
   * @param currentUser user name attempting to enables/disables trigger.
   * @throws IllegalAccessException when user is not authorized enables/disables
   *           trigger
   */
  void setTriggerEnabledForAccount(String triggerName,
                                   long accountId,
                                   boolean enabled,
                                   String currentUser) throws IllegalAccessException;

  /**
   * Checks if a trigger is enabled for account or not.
   *
   * @param triggerName The trigger to be checked.
   * @param accountId The account Id.
   * @return true if the trigger enabled for account, false if not.
   */
  boolean isTriggerEnabledForAccount(String triggerName,
                                   long accountId);
}

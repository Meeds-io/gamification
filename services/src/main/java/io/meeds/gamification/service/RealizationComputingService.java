/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.service;

import java.util.List;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;

public interface RealizationComputingService {

  /**
   * Get Rules accessible for a given user by filter using offset and limit. The
   * returned List of rules will include only Rule which are locking other rules
   * and which is possible to achieve by the current user
   *
   * @param ruleFilter {@link RuleFilter} used to filter rules
   * @param username User name accessing Programs
   * @param offset Offset of result
   * @param limit Limit of result
   * @return {@link List} of {@link RuleDTO}
   */
  List<RuleDTO> getLockingRules(RuleFilter ruleFilter, String username, int offset, int limit);

}

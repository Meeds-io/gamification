/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
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
package io.meeds.gamification.mcp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;
import io.meeds.mcp.server.plugin.SpaceScopeResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * Tells which space a quest (a rule) belongs to: the space of its campaign,
 * when that is a space campaign. A quest of an open campaign, a deleted quest
 * and a quest without a campaign belong to no space. Read without any access
 * check, for the space scope decision only.
 */
@Service("gamificationRuleSpaceScopeResolver")
@Profile("mcp-server")
@Slf4j
public class RuleSpaceScopeResolver implements SpaceScopeResolver {

  /** The name the quest tools' {@code space_scope.resolver} gives. */
  public static final String NAME = "rule";

  /** The service the quest is read from, without access check. */
  private final RuleService ruleService;

  /**
   * @param ruleService the rule service
   */
  public RuleSpaceScopeResolver(RuleService ruleService) {
    this.ruleService = ruleService;
  }

  /**
   * @return {@value #NAME}
   */
  @Override
  public String getName() {
    return NAME;
  }

  /**
   * @param objectId the quest id
   * @return the id of the space of the quest's campaign, or null for a quest
   *         of an open campaign or of no space, a deleted or missing quest, an
   *         id that isn't a number, or a lookup that fails
   */
  @Override
  public Long spaceOf(String objectId) {
    if (!StringUtils.isNumeric(objectId)) {
      return null;
    }
    try {
      RuleDTO rule = ruleService.findRuleById(Long.parseLong(objectId));
      return rule == null || rule.isDeleted() ? null : ProgramSpace.of(rule.getProgram());
    } catch (RuntimeException e) {
      log.debug("The space of quest '{}' couldn't be read", objectId, e);
      return null;
    }
  }

}

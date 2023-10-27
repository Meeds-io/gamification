/*
 * 
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package io.meeds.gamification.plugin;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.social.translation.plugin.TranslationPlugin;

public class RuleTranslationPlugin extends TranslationPlugin {

  public static final String RULE_OBJECT_TYPE            = "rule";

  public static final String RULE_DESCRIPTION_FIELD_NAME = "description";

  public static final String RULE_TITLE_FIELD_NAME       = "title";

  private RuleService        ruleService;

  private ProgramService     programService;

  private SpaceService       spaceService;

  private IdentityManager    identityManager;

  public RuleTranslationPlugin(ProgramService programService,
                               RuleService ruleService,
                               SpaceService spaceService,
                               IdentityManager identityManager) {
    this.programService = programService;
    this.ruleService = ruleService;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
  }

  @Override
  public String getObjectType() {
    return RULE_OBJECT_TYPE;
  }

  @Override
  public boolean hasAccessPermission(long ruleId, String username) throws ObjectNotFoundException {
    try {
      RuleDTO rule = this.ruleService.findRuleById(ruleId, username);
      return rule != null && programService.canViewProgram(rule.getProgramId(), username);
    } catch (IllegalAccessException e) {
      return false;
    }
  }

  @Override
  public boolean hasEditPermission(long ruleId, String username) throws ObjectNotFoundException {
    try {
      RuleDTO rule = this.ruleService.findRuleById(ruleId, username);
      return rule != null && programService.isProgramOwner(rule.getProgramId(), username);
    } catch (IllegalAccessException e) {
      return false;
    }
  }

  @Override
  public long getAudienceId(long ruleId) throws ObjectNotFoundException {
    long spaceId = getSpaceId(ruleId);
    if (spaceId == 0) {
      return 0;
    }
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      throw new ObjectNotFoundException(String.format("Space with id %s wasn't found",
                                                      spaceId));
    }
    Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
    return Long.parseLong(spaceIdentity.getId());
  }

  @Override
  public long getSpaceId(long ruleId) throws ObjectNotFoundException {
    RuleDTO rule = this.ruleService.findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException(String.format("Rule with id %s wasn't found",
                                                      ruleId));
    }
    return rule.getProgram().getSpaceId();
  }

}

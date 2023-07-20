/**
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.plugin;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.ActivityTypePlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.space.SpaceUtils;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;

public class RuleActivityTypePlugin extends ActivityTypePlugin {

  private RuleService ruleService;

  public RuleActivityTypePlugin(RuleService ruleService, InitParams params) {
    super(params);
    this.ruleService = ruleService;
  }

  @Override
  public boolean isActivityViewable(ExoSocialActivity activity, Identity userAclIdentity) {
    long ruleId = Long.parseLong(activity.getMetadataObjectId());
    RuleDTO rule = ruleService.findRuleById(ruleId);
    if (rule == null || !rule.isOpen() || !userAclIdentity.isMemberOf(SpaceUtils.PLATFORM_USERS_GROUP)) {
      throw new UnsupportedOperationException();
    } else {
      return true;
    }
  }

}

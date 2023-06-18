/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2022 Meeds Association contact@meeds.io
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

import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_OBJECT_TYPE;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_PARAM_RULE_ID;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_TYPE;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

public class ActivityRuleProcessor extends BaseActivityProcessorPlugin {

  public ActivityRuleProcessor(InitParams initParams) {
    super(initParams);
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (activity.isComment()
        || !StringUtils.equals(RULE_ACTIVITY_TYPE, activity.getType())
        || activity.getTemplateParams() == null
        || !activity.getTemplateParams().containsKey(RULE_ACTIVITY_PARAM_RULE_ID)) {
      return;
    }
    activity.setMetadataObjectType(RULE_ACTIVITY_OBJECT_TYPE);
    activity.setMetadataObjectId(activity.getTemplateParams().get(RULE_ACTIVITY_PARAM_RULE_ID));
  }

}

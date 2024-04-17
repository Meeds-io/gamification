/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
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
package io.meeds.gamification.notification.plugin;

import static io.meeds.gamification.utils.Utils.*;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.plugin.BaseNotificationPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.notification.plugin.SocialNotificationUtils;

import io.meeds.gamification.model.RealizationDTO;

public class ContributionRejectedNotificationPlugin extends BaseNotificationPlugin {

  private final ActivityManager activityManager;

  public ContributionRejectedNotificationPlugin(ActivityManager activityManager, InitParams initParams) {
    super(initParams);
    this.activityManager = activityManager;
  }

  @Override
  public String getId() {
    return CONTRIBUTION_REJECTED_NOTIFICATION_ID;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    return true;
  }

  @Override
  public NotificationInfo makeNotification(NotificationContext ctx) {
    RealizationDTO realizationDTO = ctx.value(REALIZATION_NOTIFICATION_PARAMETER);
    if (realizationDTO == null) {
      return null;
    }
    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(realizationDTO.getActivityId()));
    if (activity == null) {
      return null;
    }

    return NotificationInfo.instance()
                           .to(realizationDTO.getCreatedBy())
                           .with(REALIZATION_ID_NOTIFICATION_PARAM, String.valueOf(realizationDTO.getId()))
                           .with(SocialNotificationUtils.ACTIVITY_ID.getKey(), String.valueOf(realizationDTO.getActivityId()))
                           .key(getId())
                           .end();
  }
}

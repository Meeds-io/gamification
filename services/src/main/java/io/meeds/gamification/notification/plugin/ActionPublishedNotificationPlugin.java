/*
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
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.notification.plugin;

import static io.meeds.gamification.utils.Utils.RULE_NOTIFICATION_PARAMETER;
import static io.meeds.gamification.utils.Utils.RULE_ID_NOTIFICATION_PARAM;
import static io.meeds.gamification.utils.Utils.RULE_PUBLISHED_NOTIFICATION_ID;
import static io.meeds.gamification.utils.Utils.RULE_PUBLISHER_NOTIFICATION_PARAM;
import static io.meeds.gamification.utils.Utils.RULE_PUBLISHER_NOTIFICATION_PARAMETER;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.plugin.BaseNotificationPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.notification.plugin.SocialNotificationUtils;

import io.meeds.gamification.model.RuleDTO;

public class ActionPublishedNotificationPlugin extends BaseNotificationPlugin {

  private SpaceService spaceService;

  public ActionPublishedNotificationPlugin(SpaceService spaceService, InitParams initParams) {
    super(initParams);
    this.spaceService = spaceService;
  }

  @Override
  public String getId() {
    return RULE_PUBLISHED_NOTIFICATION_ID;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    RuleDTO rule = ctx.value(RULE_NOTIFICATION_PARAMETER);
    return rule != null && rule.getActivityId() > 0 && rule.getSpaceId() > 0;
  }

  @Override
  public NotificationInfo makeNotification(NotificationContext ctx) {
    RuleDTO rule = ctx.value(RULE_NOTIFICATION_PARAMETER);
    String username = ctx.value(RULE_PUBLISHER_NOTIFICATION_PARAMETER);
    Space space = spaceService.getSpaceById(String.valueOf(rule.getSpaceId()));
    if (space == null) {
      return null;
    }
    List<String> targetUsers = Arrays.stream(space.getMembers())
                                     .filter(targetUser -> !StringUtils.equals(targetUser, username))
                                     .toList();
    if (targetUsers.isEmpty()) {
      return null;
    }
    return NotificationInfo.instance()
                           .to(targetUsers)
                           .with(RULE_ID_NOTIFICATION_PARAM, String.valueOf(rule.getId()))
                           .with(RULE_PUBLISHER_NOTIFICATION_PARAM, username)
                           .with(SocialNotificationUtils.ACTIVITY_ID.getKey(), String.valueOf(rule.getActivityId()))
                           .key(getId())
                           .end();
  }
}

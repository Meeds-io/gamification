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

import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_ID_NOTIFICATION_PARAM;
import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_NOTIFICATION_PARAMETER;
import static io.meeds.gamification.utils.Utils.RULE_ANNOUNCED_NOTIFICATION_ID;
import static io.meeds.gamification.utils.Utils.RULE_ID_NOTIFICATION_PARAM;
import static io.meeds.gamification.utils.Utils.getUserRemoteId;

import java.util.*;

import io.meeds.gamification.model.ProgramDTO;
import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.plugin.BaseNotificationPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.notification.plugin.SocialNotificationUtils;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;

public class ActionAnnouncedNotificationPlugin extends BaseNotificationPlugin {

  private final RuleService     ruleService;

  private final ActivityManager activityManager;

  private final SpaceService    spaceService;

  public ActionAnnouncedNotificationPlugin(RuleService ruleService,
                                           ActivityManager activityManager,
                                           InitParams initParams,
                                           SpaceService spaceService) {
    super(initParams);
    this.ruleService = ruleService;
    this.activityManager = activityManager;
    this.spaceService = spaceService;
  }

  @Override
  public String getId() {
    return RULE_ANNOUNCED_NOTIFICATION_ID;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    Announcement announcement = ctx.value(ANNOUNCEMENT_NOTIFICATION_PARAMETER);
    return announcement != null && announcement.getCreator() != null && announcement.getCreator() > 0
        && announcement.getActivityId() > 0;
  }

  @Override
  public NotificationInfo makeNotification(NotificationContext ctx) {
    Announcement announcement = ctx.value(ANNOUNCEMENT_NOTIFICATION_PARAMETER);
    if (announcement == null) {
      return null;
    }
    RuleDTO rule = ruleService.findRuleById(announcement.getChallengeId());
    if (rule == null) {
      return null;
    }
    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(rule.getActivityId()));
    if (activity == null) {
      return null;
    }
    List<String> targetUsers = new ArrayList<>();
    Space space = spaceService.getSpaceById(String.valueOf(rule.getSpaceId()));
    if (space != null) {
      targetUsers = Arrays.stream(space.getManagers())
                          .filter(targetUser -> !Objects.equals(getUserRemoteId(String.valueOf(announcement.getCreator())),
                                                                targetUser))
                          .toList();
    }
    Set<String> finalTargetUsers = new HashSet<>(targetUsers);
    ProgramDTO programDTO = rule.getProgram();
    programDTO.getOwnerIds().forEach(userId -> {
      String userRemoteId = getUserRemoteId(String.valueOf(userId));
      finalTargetUsers.add(userRemoteId);
    });

    if (targetUsers.isEmpty()) {
      return null;
    }

    return NotificationInfo.instance()
                           .to(new ArrayList<>(finalTargetUsers))
                           .setSpaceId(rule.getSpaceId())
                           .with(RULE_ID_NOTIFICATION_PARAM, String.valueOf(announcement.getChallengeId()))
                           .with(ANNOUNCEMENT_ID_NOTIFICATION_PARAM, String.valueOf(announcement.getId()))
                           .with(SocialNotificationUtils.ACTIVITY_ID.getKey(), String.valueOf(announcement.getActivityId()))
                           .key(getId())
                           .end();
  }

}

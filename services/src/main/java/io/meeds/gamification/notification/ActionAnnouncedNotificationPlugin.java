/*
 * Copyright (C) 2003-2018 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.meeds.gamification.notification;

import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_DETAILS_PARAMETER;
import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_ID_NOTIFICATION_PARAM;
import static io.meeds.gamification.utils.Utils.RULE_ANNOUNCED_NOTIFICATION_ID;
import static io.meeds.gamification.utils.Utils.getUserRemoteId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.plugin.BaseNotificationPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;

public class ActionAnnouncedNotificationPlugin extends BaseNotificationPlugin {

  private RuleService     ruleService;

  private ActivityManager activityManager;

  public ActionAnnouncedNotificationPlugin(RuleService ruleService,
                                           ActivityManager activityManager,
                                           InitParams initParams) {
    super(initParams);
    this.ruleService = ruleService;
    this.activityManager = activityManager;
  }

  @Override
  public String getId() {
    return RULE_ANNOUNCED_NOTIFICATION_ID;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    Announcement announcement = ctx.value(ANNOUNCEMENT_DETAILS_PARAMETER);
    return announcement != null
        && announcement.getCreator() != null
        && announcement.getCreator() > 0
        && announcement.getActivityId() > 0;
  }

  @Override
  public NotificationInfo makeNotification(NotificationContext ctx) {
    Announcement announcement = ctx.value(ANNOUNCEMENT_DETAILS_PARAMETER);
    RuleDTO rule = ruleService.findRuleById(announcement.getChallengeId());
    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(rule.getActivityId()));

    Set<String> receivers = new HashSet<>();

    addReceivers(receivers, activity.getCommentedIds());
    addReceivers(receivers, activity.getPosterId());
    addReceivers(receivers, activity.getLikeIdentityIds());
    addReceivers(receivers, activity.getMentionedIds());
    receivers.remove(getUserRemoteId(String.valueOf(announcement.getCreator())));
    if (receivers.isEmpty()) {
      return null;
    }
    return NotificationInfo.instance()
                           .to(receivers.stream().toList())
                           .with(ANNOUNCEMENT_ID_NOTIFICATION_PARAM, String.valueOf(announcement.getId()))
                           .key(getId())
                           .end();
  }

  private void addReceivers(Set<String> receivers, String... userIdentityIds) {
    if (userIdentityIds != null && userIdentityIds.length > 0) {
      Arrays.stream(userIdentityIds)
            .map(Utils::getUserRemoteId)
            .forEach(receivers::add);
    }
  }

}

/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
 * contact@meeds.io
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
package org.exoplatform.addons.gamification.listener.challenges;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;

public class AnnouncementActivityUpdater extends ActivityListenerPlugin {

  private static final Log    LOG = ExoLogger.getLogger(AnnouncementActivityUpdater.class);

  private ActivityManager activityManager;

  private AnnouncementService announcementService;

  public AnnouncementActivityUpdater(ActivityManager activityManager, AnnouncementService announcementService) {
    this.activityManager = activityManager;
    this.announcementService = announcementService;
  }

  @Override
  public void updateActivity(ActivityLifeCycleEvent activityLifeCycleEvent) {
    ExoSocialActivity activity = activityLifeCycleEvent.getSource();
    if (!StringUtils.equals(activity.getType(), ANNOUNCEMENT_ACTIVITY_TYPE)) {
      return;
    }
    long announcementId = Long.parseLong(activity.getTemplateParams().get("announcementId"));
    Announcement announcement = announcementService.getAnnouncementById(announcementId);
    if (announcement != null) {
      try {
        announcement.setComment(activity.getTitle());
        announcementService.updateAnnouncement(announcement);
      } catch (ObjectNotFoundException e) {
        LOG.warn("Announcement with id {} wasn't found, only the activity message will be updated", announcementId, e);
      }
    }
    if (activity.getTemplateParams().containsKey("announcementComment")) {
      activity.getTemplateParams().put("announcementComment", activity.getTitle());
      activityManager.updateActivity(activity, false);
    }
  }
}
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.listener;

import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;
import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_COMMENT_TYPE;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.RealizationService;

public class AnnouncementActivityUpdater extends ActivityListenerPlugin {

  public static final String  ANNOUNCEMENT_ID_PARAM      = "announcementId";

  public static final String  ANNOUNCEMENT_COMMENT_PARAM = "announcementComment";

  private static final Log    LOG                        = ExoLogger.getLogger(AnnouncementActivityUpdater.class);

  private ActivityManager     activityManager;

  private AnnouncementService announcementService;

  private RealizationService  realizationService;

  public AnnouncementActivityUpdater(ActivityManager activityManager,
                                     AnnouncementService announcementService,
                                     RealizationService realizationService) {
    this.activityManager = activityManager;
    this.announcementService = announcementService;
    this.realizationService = realizationService;
  }

  @Override
  public void updateActivity(ActivityLifeCycleEvent event) {
    processAnnouncementActivityUpdated(event);
  }

  @Override
  public void updateComment(ActivityLifeCycleEvent event) {
    processAnnouncementActivityUpdated(event);
  }

  @Override
  public void deleteActivity(ActivityLifeCycleEvent event) {
    processAnnouncementActivityDeleted(event);
  }

  @Override
  public void deleteComment(ActivityLifeCycleEvent event) {
    processAnnouncementActivityDeleted(event);
  }

  private void processAnnouncementActivityUpdated(ActivityLifeCycleEvent activityLifeCycleEvent) {
    ExoSocialActivity activity = activityLifeCycleEvent.getSource();
    if (!isActivityAnnouncementType(activity)) {
      return;
    }
    long announcementId = Long.parseLong(activity.getTemplateParams().get(ANNOUNCEMENT_ID_PARAM));
    try {
      announcementService.updateAnnouncementComment(announcementId, activity.getTitle());
    } catch (ObjectNotFoundException e) {
      LOG.warn("Announcement with id {} wasn't found, only the activity message will be updated", announcementId, e);
    }
    if (activity.getTemplateParams().containsKey(ANNOUNCEMENT_COMMENT_PARAM)) {
      activity.getTemplateParams().put(ANNOUNCEMENT_COMMENT_PARAM, null);
      activityManager.updateActivity(activity, false);
    }
  }

  private void processAnnouncementActivityDeleted(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    if (!isActivityAnnouncementType(activity)) {
      return;
    }
    long realizationId = Long.parseLong(activity.getTemplateParams().get(ANNOUNCEMENT_ID_PARAM));
    try {
      RealizationDTO realization = realizationService.getRealizationById(realizationId);
      if (!RealizationStatus.CANCELED.name().equals(realization.getStatus())) {
        realizationService.updateRealizationStatus(realizationId, RealizationStatus.DELETED);
      }
    } catch (ObjectNotFoundException e) {
      LOG.warn("Realization with id {} does not exist", realizationId, e);
    }
  }

  private boolean isActivityAnnouncementType(ExoSocialActivity activity) {
    return StringUtils.equals(activity.getType(), ANNOUNCEMENT_ACTIVITY_TYPE)
        || StringUtils.equals(activity.getType(), ANNOUNCEMENT_COMMENT_TYPE);
  }

}

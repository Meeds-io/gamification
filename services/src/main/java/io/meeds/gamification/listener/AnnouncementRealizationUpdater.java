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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package io.meeds.gamification.listener;

import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.service.RealizationService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.gamification.model.RealizationDTO;

import java.util.Map;

import static io.meeds.gamification.utils.Utils.*;

public class AnnouncementRealizationUpdater extends Listener<Object, Object> {

  private final ActivityManager    activityManager;

  private final RealizationService realizationService;

  public AnnouncementRealizationUpdater(ActivityManager activityManager, RealizationService realizationService) {
    this.activityManager = activityManager;
    this.realizationService = realizationService;
  }

  @Override
  public void onEvent(Event<Object, Object> event) throws Exception {
    RealizationDTO realization = getRealization(event.getSource());
    if (realization == null) {
      return;
    }
    if (!RealizationStatus.CANCELED.name().equals(realization.getStatus())
        && !RealizationStatus.DELETED.name().equals(realization.getStatus())) {
      ExoSocialActivity announcementComment = activityManager.getActivity(String.valueOf(realization.getActivityId()));
      if (announcementComment != null) {
        Map<String, String> templateParams = announcementComment.getTemplateParams();
        templateParams.put(REALIZATION_STATUS_TEMPLATE_PARAM, String.valueOf(realization.getStatus()));
        announcementComment.setTemplateParams(templateParams);
        activityManager.updateActivity(announcementComment);
      }
    }
  }

  private RealizationDTO getRealization(Object object) {
    if (object instanceof RealizationDTO realization) {
      return realization;
    } else if (object instanceof Announcement announcement) {
      return realizationService.getRealizationById(announcement.getId());
    }
    return null;
  }
}

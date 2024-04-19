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
package io.meeds.gamification.activity.processor;

import static io.meeds.gamification.listener.AnnouncementActivityUpdater.ANNOUNCEMENT_ID_PARAM;
import static io.meeds.gamification.utils.Utils.REALIZATION_CREATED_DATE_PARAM;
import static io.meeds.gamification.utils.Utils.REALIZATION_STATUS_TEMPLATE_PARAM;

import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.service.RealizationService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

import io.meeds.gamification.utils.Utils;

import java.util.Map;

public class AnnouncementActivityPreProcessor extends BaseActivityProcessorPlugin {

  private final RealizationService realizationService;

  public AnnouncementActivityPreProcessor(RealizationService realizationService, InitParams params) {
    super(params);
    this.realizationService = realizationService;
  }

  @Override
  public boolean isPreActivityProcessor() {
    return true;
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (!Utils.ANNOUNCEMENT_COMMENT_TYPE.equals(activity.getType())) {
      return;
    }
    long realizationId = Long.parseLong(activity.getTemplateParams().get(ANNOUNCEMENT_ID_PARAM));
    RealizationDTO realization = realizationService.getRealizationById(realizationId);
    if (realization == null) {
      return;
    }
    if (!RealizationStatus.CANCELED.name().equals(realization.getStatus())
        && !RealizationStatus.DELETED.name().equals(realization.getStatus()) && realization.getActivityId() != null) {
      Map<String, String> templateParams = activity.getTemplateParams();
      templateParams.put(REALIZATION_STATUS_TEMPLATE_PARAM, String.valueOf(realization.getStatus()));
      templateParams.put(REALIZATION_CREATED_DATE_PARAM, realization.getCreatedDate());
      activity.setTemplateParams(templateParams);
    }
  }
}

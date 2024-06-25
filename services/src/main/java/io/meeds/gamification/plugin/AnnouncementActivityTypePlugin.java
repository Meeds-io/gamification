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
package io.meeds.gamification.plugin;

import io.meeds.gamification.constant.RealizationStatus;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.ActivityTypePlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.IdentityManager;

import static io.meeds.gamification.utils.Utils.REALIZATION_STATUS_TEMPLATE_PARAM;

public class AnnouncementActivityTypePlugin extends ActivityTypePlugin {

  private final IdentityManager identityManager;

  public AnnouncementActivityTypePlugin(InitParams params, IdentityManager identityManager) {
    super(params);
    this.identityManager = identityManager;
  }

  @Override
  public boolean isActivityEditable(ExoSocialActivity activity, Identity userAclIdentity) {
    String realizationStatus = activity.getTemplateParams().get(REALIZATION_STATUS_TEMPLATE_PARAM);
    if (RealizationStatus.REJECTED.name().equals(realizationStatus)) {
      return false;
    }
    org.exoplatform.social.core.identity.model.Identity identity =
                                                                 identityManager.getOrCreateUserIdentity(userAclIdentity.getUserId());
    return identity != null && StringUtils.equals(identity.getId(), activity.getPosterId());
  }
}

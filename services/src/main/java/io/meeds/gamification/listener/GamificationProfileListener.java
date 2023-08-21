/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
package io.meeds.gamification.listener;

import static io.meeds.gamification.constant.GamificationConstant.BROADCAST_GAMIFICATION_EVENT_ERROR;
import static io.meeds.gamification.constant.GamificationConstant.EVENT_NAME;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_CONTACT_INFORMATION;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_WORK_EXPERIENCE;
import static io.meeds.gamification.constant.GamificationConstant.IDENTITY_OBJECT_TYPE;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_ID_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_TYPE_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.RECEIVER_ID;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_ID;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;

public class GamificationProfileListener extends ProfileListenerPlugin {

  private static final Log LOG = ExoLogger.getLogger(GamificationProfileListener.class);

  private ListenerService  listenerService;

  public GamificationProfileListener(ListenerService listenerService) {
    this.listenerService = listenerService;
  }

  @Override
  public void avatarUpdated(ProfileLifeCycleEvent event) {
    createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR, event.getProfile(), event.getModifierUsername());
  }

  @Override
  public void bannerUpdated(ProfileLifeCycleEvent event) {
    createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER, event.getProfile(), event.getModifierUsername());
  }

  @Override
  public void aboutMeUpdated(ProfileLifeCycleEvent event) {
    createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME, event.getProfile(), event.getModifierUsername());
  }

  @Override
  public void contactSectionUpdated(ProfileLifeCycleEvent event) {
    createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_CONTACT_INFORMATION, event.getProfile(), event.getModifierUsername());
  }

  @Override
  public void experienceSectionUpdated(ProfileLifeCycleEvent event) {
    createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_WORK_EXPERIENCE, event.getProfile(), event.getModifierUsername());
  }

  private void createRealizations(String gamificationEventName, Profile profile, String modifierUsername) {
    if (profile == null
        || StringUtils.isBlank(modifierUsername)
        || !StringUtils.equals(profile.getIdentity().getRemoteId(), modifierUsername)) {
      return;
    }
    String identityId = profile.getIdentity().getId();

    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, identityId);
      gam.put(OBJECT_TYPE_PARAM, IDENTITY_OBJECT_TYPE);
      gam.put(SENDER_ID, identityId);
      gam.put(RECEIVER_ID, identityId);
      listenerService.broadcast(GENERIC_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

}

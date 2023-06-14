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
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_NOTIFICATION_SETTING;
import static io.meeds.gamification.constant.GamificationConstant.IDENTITY_OBJECT_TYPE;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_ID_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_TYPE_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.RECEIVER_ID;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_ID;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.commons.api.notification.model.UserSetting;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

@Asynchronous
public class GamificationNotificationListener extends Listener<String, UserSetting> {

  private static final Log  LOG = ExoLogger.getLogger(GamificationNotificationListener.class);

  protected IdentityManager identityManager;

  protected ListenerService listenerService;

  public GamificationNotificationListener(IdentityManager identityManager,
                                          ListenerService listenerService) {
    this.identityManager = identityManager;
    this.listenerService = listenerService;
  }

  @Override
  public void onEvent(Event<String, UserSetting> event) {
    String username = event.getSource();
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    if (identity != null && !identity.isDeleted() && identity.isEnable()) {
      createRealizations(identity.getId());
    }
  }

  private void createRealizations(String identityId) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, GAMIFICATION_SOCIAL_PROFILE_ADD_NOTIFICATION_SETTING);
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

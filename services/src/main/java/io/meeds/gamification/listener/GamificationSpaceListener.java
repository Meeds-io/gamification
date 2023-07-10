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
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_ADD;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_JOIN;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_APPLICATIONS;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_AVATAR;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_BANNER;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_DESCRIPTION;
import static io.meeds.gamification.constant.GamificationConstant.*;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_ID_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_TYPE_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.RECEIVER_ID;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_ID;
import static io.meeds.gamification.listener.GamificationGenericListener.CANCEL_EVENT_NAME;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;
import static io.meeds.gamification.utils.Utils.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.service.RuleService;

public class GamificationSpaceListener extends SpaceListenerPlugin {

  private static final Log  LOG = ExoLogger.getLogger(GamificationSpaceListener.class);

  protected RuleService     ruleService;

  protected IdentityManager identityManager;

  protected SpaceService    spaceService;

  protected ListenerService listenerService;

  public GamificationSpaceListener(RuleService ruleService,
                                   IdentityManager identityManager,
                                   SpaceService spaceService,
                                   ListenerService listenerService) {
    this.ruleService = ruleService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.listenerService = listenerService;
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createRealization(username, space, GAMIFICATION_SOCIAL_SPACE_ADD);
  }

  @Override
  public void spaceDescriptionEdited(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createRealization(username, space, GAMIFICATION_SOCIAL_SPACE_UPDATE_DESCRIPTION);
  }

  @Override
  public void spaceAvatarEdited(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createRealization(username, space, GAMIFICATION_SOCIAL_SPACE_UPDATE_AVATAR);
  }

  @Override
  public void spaceBannerEdited(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createRealization(username, space, GAMIFICATION_SOCIAL_SPACE_UPDATE_BANNER);
  }

  @Override
  public void applicationAdded(SpaceLifeCycleEvent event) {
    triggerSpaceApplicationCustomizationEvent(event);
  }

  @Override
  public void applicationRemoved(SpaceLifeCycleEvent event) {
    triggerSpaceApplicationCustomizationEvent(event);
  }

  @Override
  public void joined(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createRealization(username, space, GAMIFICATION_SOCIAL_SPACE_JOIN);
  }

  @Override
  public void left(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    cancelRealization(username, space, GAMIFICATION_SOCIAL_SPACE_JOIN);
  }

  @Override
  public void grantedLead(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createRealization(username, space, GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD);
  }

  @Override
  public void addInvitedUser(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    triggerRealization(space,
                       getCurrentIdentityId(),
                       SPACE_MEMBERSHIP_OBJECT_TYPE,
                       space.getId() + "-" + getUserIdentityId(username),
                       GAMIFICATION_SOCIAL_SPACE_INVITE_USER,
                       GENERIC_EVENT_NAME);
  }

  @Override
  public void removeInvitedUser(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    triggerRealization(space,
                       getCurrentIdentityId(),
                       SPACE_MEMBERSHIP_OBJECT_TYPE,
                       space.getId() + "-" + getUserIdentityId(username),
                       GAMIFICATION_SOCIAL_SPACE_INVITE_USER,
                       CANCEL_EVENT_NAME);
  }

  private void createRealization(String username, Space space, String gamificationEventName) {
    triggerRealization(space,
                       getModifierIdentityId(username),
                       IDENTITY_OBJECT_TYPE,
                       null,
                       gamificationEventName,
                       GENERIC_EVENT_NAME);
  }

  private void cancelRealization(String username, Space space, String gamificationEventName) {
    triggerRealization(space,
                       getModifierIdentityId(username),
                       IDENTITY_OBJECT_TYPE,
                       null,
                       gamificationEventName,
                       CANCEL_EVENT_NAME);
  }

  private void triggerRealization(Space space,
                                  String earnerIdentityId,
                                  String objectType,
                                  String objectId,
                                  String gamificationEventName,
                                  String eventName) {
    // Compute user id
    if (space == null) {
      LOG.warn("Can't gamify on null space with modification of type {}", gamificationEventName);
      return;
    }
    if (StringUtils.isBlank(earnerIdentityId)) {
      LOG.info("Can't determine user to gamify space {} modification of type {}",
               space.getDisplayName(),
               gamificationEventName);
      return;
    }
    Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
    if (spaceIdentity == null) {
      LOG.warn("Can't gamify on space {} having null identity for modification of type {}",
               space.getDisplayName(),
               gamificationEventName);
      return;
    }
    String receiverId = spaceIdentity.getId();
    if (StringUtils.equals(objectType, IDENTITY_OBJECT_TYPE)) {
      objectId = receiverId;
    }
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, objectId);
      gam.put(OBJECT_TYPE_PARAM, objectType);
      gam.put(SENDER_ID, earnerIdentityId);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(eventName, gam, null);
    } catch (Exception e) {
      LOG.error(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
    try {
      gam = new HashMap<>();
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, objectId);
      gam.put(OBJECT_TYPE_PARAM, objectType);
      gam.put(SENDER_ID, receiverId);
      gam.put(RECEIVER_ID, earnerIdentityId);
      listenerService.broadcast(eventName, gam, null);
    } catch (Exception e) {
      LOG.error(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void triggerSpaceApplicationCustomizationEvent(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createRealization(username, space, GAMIFICATION_SOCIAL_SPACE_UPDATE_APPLICATIONS);
  }

  private String getModifierIdentityId(String username) {
    if (StringUtils.isNotBlank(username)) {
      long userIdentityId = getUserIdentityId(username);
      if (userIdentityId > 0) {
        return String.valueOf(userIdentityId);
      }
    }
    return getCurrentIdentityId();
  }

  private String getCurrentIdentityId() {
    long currentUserIdentityId = getCurrentUserIdentityId();
    if (currentUserIdentityId > 0) {
      return String.valueOf(currentUserIdentityId);
    }
    return null;
  }

}

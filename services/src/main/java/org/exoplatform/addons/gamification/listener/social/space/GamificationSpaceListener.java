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
package org.exoplatform.addons.gamification.listener.social.space;

import static org.exoplatform.addons.gamification.GamificationConstant.*;
import static org.exoplatform.addons.gamification.listener.generic.GamificationGenericListener.CANCEL_EVENT_NAME;
import static org.exoplatform.addons.gamification.listener.generic.GamificationGenericListener.GENERIC_EVENT_NAME;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;
import org.exoplatform.social.core.space.spi.SpaceService;

public class GamificationSpaceListener extends SpaceListenerPlugin {

  private static final Log  LOG = ExoLogger.getLogger(GamificationSpaceListener.class);

  protected RuleService     ruleService;

  protected IdentityManager identityManager;

  protected SpaceService    spaceService;

  protected ListenerService listenerService;

  public GamificationSpaceListener() {
    this.ruleService = CommonsUtils.getService(RuleService.class);
    this.identityManager = CommonsUtils.getService(IdentityManager.class);
    this.spaceService = CommonsUtils.getService(SpaceService.class);
    this.listenerService = CommonsUtils.getService(ListenerService.class);
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createGamificationHistoryEntry(username, space, GAMIFICATION_SOCIAL_SPACE_ADD);
  }

  @Override
  public void spaceRemoved(SpaceLifeCycleEvent event) {
  }

  @Override
  public void applicationAdded(SpaceLifeCycleEvent event) {
  }

  @Override
  public void applicationRemoved(SpaceLifeCycleEvent event) {
  }

  @Override
  public void applicationActivated(SpaceLifeCycleEvent event) {
  }

  @Override
  public void applicationDeactivated(SpaceLifeCycleEvent event) {
  }

  @Override
  public void joined(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createGamificationHistoryEntry(username, space, GAMIFICATION_SOCIAL_SPACE_JOIN);
    clearUserChallengeCache();
  }

  @Override
  public void left(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    cancelGamificationHistoryEntry(username, space, GAMIFICATION_SOCIAL_SPACE_JOIN);
    clearUserChallengeCache();
  }

  @Override
  public void grantedLead(SpaceLifeCycleEvent event) {
    String username = event.getSource();
    Space space = event.getSpace();

    createGamificationHistoryEntry(username, space, GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD);
  }

  @Override
  public void revokedLead(SpaceLifeCycleEvent event) {
  }

  @Override
  public void spaceRenamed(SpaceLifeCycleEvent event) {
  }

  @Override
  public void spaceDescriptionEdited(SpaceLifeCycleEvent event) {
  }

  @Override
  public void spaceAvatarEdited(SpaceLifeCycleEvent event) {
  }

  @Override
  public void spaceAccessEdited(SpaceLifeCycleEvent event) {
  }

  @Override
  public void addInvitedUser(SpaceLifeCycleEvent event) {
  }

  @Override
  public void addPendingUser(SpaceLifeCycleEvent event) {
  }

  @Override
  public void spaceRegistrationEdited(SpaceLifeCycleEvent event) {

  }

  @Override
  public void spaceBannerEdited(SpaceLifeCycleEvent event) {
  }

  private void createGamificationHistoryEntry(String username, Space space, String ruleTitle) {
    // Compute user id
    String senderId = identityManager.getOrCreateUserIdentity(username).getId();
    String receiverId = identityManager.getOrCreateSpaceIdentity(space.getPrettyName()).getId();
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(RULE_TITLE, ruleTitle);
      gam.put(OBJECT_ID_PARAM, receiverId);
      gam.put(OBJECT_TYPE_PARAM, IDENTITY_OBJECT_TYPE);
      gam.put(SENDER_ID, senderId);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(GENERIC_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.error(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
    try {
      gam = new HashMap<>();
      gam.put(RULE_TITLE, ruleTitle);
      gam.put(OBJECT_ID_PARAM, receiverId);
      gam.put(OBJECT_TYPE_PARAM, IDENTITY_OBJECT_TYPE);
      gam.put(SENDER_ID, receiverId);
      gam.put(RECEIVER_ID, senderId);
      listenerService.broadcast(GENERIC_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.error(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void cancelGamificationHistoryEntry(String username, Space space, String ruleTitle) {
    // Compute user id
    String senderId = identityManager.getOrCreateUserIdentity(username).getId();
    String receiverId = identityManager.getOrCreateSpaceIdentity(space.getPrettyName()).getId();
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(RULE_TITLE, ruleTitle);
      gam.put(OBJECT_ID_PARAM, receiverId);
      gam.put(OBJECT_TYPE_PARAM, IDENTITY_OBJECT_TYPE);
      gam.put(SENDER_ID, senderId);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(CANCEL_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.error(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
    try {
      gam = new HashMap<>();
      gam.put(RULE_TITLE, ruleTitle);
      gam.put(OBJECT_ID_PARAM, receiverId);
      gam.put(OBJECT_TYPE_PARAM, IDENTITY_OBJECT_TYPE);
      gam.put(SENDER_ID, receiverId);
      gam.put(RECEIVER_ID, senderId);
      listenerService.broadcast(CANCEL_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.error(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void clearUserChallengeCache() {
    ChallengeService challengeService = CommonsUtils.getService(ChallengeService.class);
    if (challengeService != null) {
      challengeService.clearUserChallengeCache();
    }
  }

}

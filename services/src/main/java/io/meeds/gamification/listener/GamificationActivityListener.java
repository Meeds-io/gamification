/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.listener;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;
import static io.meeds.gamification.constant.GamificationConstant.BROADCAST_GAMIFICATION_EVENT_ERROR;
import static io.meeds.gamification.constant.GamificationConstant.EVENT_NAME;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_ACTIVITY;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_COMMENT;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PIN_ACTIVITY_SPACE;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_POST_ACTIVITY;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY_COMMENT;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_ID_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_TYPE_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.RECEIVER_ID;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_ID;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_TYPE;
import static io.meeds.gamification.listener.GamificationGenericListener.CANCEL_EVENT_NAME;
import static io.meeds.gamification.listener.GamificationGenericListener.DELETE_EVENT_NAME;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ActivityStream;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.service.RuleService;

public class GamificationActivityListener extends ActivityListenerPlugin {

  private static final Log        LOG = ExoLogger.getLogger(GamificationActivityListener.class);

  protected final RuleService     ruleService;

  protected final IdentityManager identityManager;

  protected final SpaceService    spaceService;

  protected final ActivityManager activityManager;

  protected final ListenerService listenerService;

  public GamificationActivityListener(RuleService ruleService,
                                      IdentityManager identityManager,
                                      ActivityManager activityManager,
                                      SpaceService spaceService,
                                      ListenerService listenerService) {
    this.ruleService = ruleService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.activityManager = activityManager;
    this.listenerService = listenerService;
  }

  @Override
  public void saveActivity(ActivityLifeCycleEvent event) { // NOSONAR
    ExoSocialActivity activity = event.getSource();
    if (activity == null || StringUtils.equals(activity.getType(), "SPACE_ACTIVITY")) {
      return;
    }

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           activity.getStreamId(),
                                           GAMIFICATION_SOCIAL_POST_ACTIVITY,
                                           activity.getId());

    // Add activity on Space Stream : Compute actor reward
    Space space = getSpaceOfActivity(activity);
    if (space == null) {
      if (!StringUtils.equals(activity.getStreamId(), activity.getPosterId())) {
        createActivityGamificationHistoryEntry(activity.getStreamId(),
                                               activity.getPosterId(),
                                               GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY,
                                               activity.getId());
      }
    } else {
      if (space.getManagers() != null && space.getManagers().length > 0) {
        String[] spaceManagers = space.getManagers();
        for (String spaceManager : spaceManagers) {
          Identity spaceManagerIdentity = identityManager.getOrCreateUserIdentity(spaceManager);
          if (spaceManagerIdentity == null
              || spaceManagerIdentity.isDeleted()
              || !spaceManagerIdentity.isEnable()
              || StringUtils.equals(spaceManagerIdentity.getId(), activity.getPosterId())) {
            continue;
          }
          createActivityGamificationHistoryEntry(spaceManagerIdentity.getId(),
                                                 activity.getPosterId(),
                                                 GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY,
                                                 activity.getId());
        }
      }
    }
  }

  @Override
  public void pinActivity(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    String userIdentityId = event.getUserId();

    createActivityGamificationHistoryEntry(userIdentityId,
                                           userIdentityId,
                                           GAMIFICATION_SOCIAL_PIN_ACTIVITY_SPACE,
                                           activity.getId());
  }

  @Override
  public void deleteActivity(ActivityLifeCycleEvent activityLifeCycleEvent) {
    ExoSocialActivity activity = activityLifeCycleEvent.getSource();
    markActivityGamificationHistoryAsDeleted(activity.getId());
    Arrays.stream(activity.getReplyToId()).forEach(this::markActivityGamificationHistoryAsDeleted);
  }

  @Override
  public void saveComment(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    ExoSocialActivity parent = activityManager.getParentActivity(activity);
    if (parent == null) {
      return;
    }

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           parent.getPosterId(),
                                           GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT,
                                           activity.getId());
    createActivityGamificationHistoryEntry(parent.getPosterId(),
                                           activity.getPosterId(),
                                           GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT,
                                           activity.getId());
    Space space = getSpaceOfActivity(parent);
    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          activity.getPosterId(),
                                          GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT,
                                          activity.getId());
    }
  }

  @Override
  public void deleteComment(ActivityLifeCycleEvent activityLifeCycleEvent) {
    ExoSocialActivity activity = activityLifeCycleEvent.getSource();
    markActivityGamificationHistoryAsDeleted(activity.getId());
  }

  @Override
  public void likeActivity(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    String likerIdentityId = event.getUserId();
    if (StringUtils.equalsIgnoreCase(activity.getPosterId(), likerIdentityId)) {
      return;
    }

    createActivityGamificationHistoryEntry(likerIdentityId,
                                           activity.getPosterId(),
                                           GAMIFICATION_SOCIAL_LIKE_ACTIVITY,
                                           activity.getId());
    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           likerIdentityId,
                                           GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY,
                                           activity.getId());

    Space space = getSpaceOfActivity(activity);
    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          likerIdentityId,
                                          GAMIFICATION_SOCIAL_LIKE_ACTIVITY,
                                          activity.getId());
    }
  }

  @Override
  public void deleteLikeActivity(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    String likerIdentityId = event.getUserId();

    cancelActivityGamificationHistoryEntry(likerIdentityId,
                                           activity.getPosterId(),
                                           GAMIFICATION_SOCIAL_LIKE_ACTIVITY,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    cancelActivityGamificationHistoryEntry(activity.getPosterId(),
                                           likerIdentityId,
                                           GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    Space space = getSpaceOfActivity(activity);
    if (space != null) {
      cancelSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          likerIdentityId,
                                          GAMIFICATION_SOCIAL_LIKE_ACTIVITY,
                                          activity.getId(),
                                          ACTIVITY_OBJECT_TYPE);
    }
  }

  @Override
  public void likeComment(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    String likerIdentityId = event.getUserId();
    if (StringUtils.equalsIgnoreCase(activity.getPosterId(), likerIdentityId)) {
      return;
    }

    createActivityGamificationHistoryEntry(likerIdentityId,
                                           activity.getPosterId(),
                                           GAMIFICATION_SOCIAL_LIKE_ACTIVITY_COMMENT,
                                           activity.getId());

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           likerIdentityId,
                                           GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY_COMMENT,
                                           activity.getId());

    Space space = getSpaceOfActivity(activity);
    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          likerIdentityId,
                                          GAMIFICATION_SOCIAL_LIKE_ACTIVITY_COMMENT,
                                          activity.getId());
    }
  }

  @Override
  public void deleteLikeComment(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    String likerIdentityId = event.getUserId();

    cancelActivityGamificationHistoryEntry(likerIdentityId,
                                           activity.getPosterId(),
                                           GAMIFICATION_SOCIAL_LIKE_ACTIVITY_COMMENT,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    cancelActivityGamificationHistoryEntry(activity.getPosterId(),
                                           likerIdentityId,
                                           GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY_COMMENT,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    Space space = getSpaceOfActivity(activity);
    if (space != null) {
      cancelSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          likerIdentityId,
                                          GAMIFICATION_SOCIAL_LIKE_ACTIVITY_COMMENT,
                                          activity.getId(),
                                          ACTIVITY_OBJECT_TYPE);
    }
  }

  private Space getSpaceOfActivity(ExoSocialActivity activity) {
    if (activity.getParentId() != null) {
      activity = activityManager.getParentActivity(activity);
    }
    return isSpaceActivity(activity) ? spaceService.getSpaceByPrettyName(activity.getActivityStream().getPrettyId()) : null;
  }

  private boolean isSpaceActivity(ExoSocialActivity activity) {
    return activity.getActivityStream() != null && ActivityStream.Type.SPACE.equals(activity.getActivityStream().getType());
  }

  private void createActivityGamificationHistoryEntry(String earnerIdentityId,
                                                      String receiverId,
                                                      String gamificationEventName,
                                                      String activityId) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, activityId);
      gam.put(OBJECT_TYPE_PARAM, ACTIVITY_OBJECT_TYPE);
      gam.put(SENDER_ID, earnerIdentityId);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(GENERIC_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void cancelActivityGamificationHistoryEntry(String earnerIdentityId,
                                                      String receiverId,
                                                      String gamificationEventName,
                                                      String objectId,
                                                      String objectType) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, objectId);
      gam.put(OBJECT_TYPE_PARAM, objectType);
      gam.put(SENDER_ID, earnerIdentityId);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(CANCEL_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void cancelSpaceGamificationHistoryEntry(String earnerIdentityId,
                                                   String receiverId,
                                                   String gamificationEventName,
                                                   String objectId,
                                                   String objectType) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, objectId);
      gam.put(OBJECT_TYPE_PARAM, objectType);
      gam.put(SENDER_ID, earnerIdentityId);
      gam.put(SENDER_TYPE, SpaceIdentityProvider.NAME);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(CANCEL_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void createSpaceGamificationHistoryEntry(String spacePrettyName,
                                                   String receiverId,
                                                   String gamificationEventName,
                                                   String activityId) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, activityId);
      gam.put(OBJECT_TYPE_PARAM, ACTIVITY_OBJECT_TYPE);
      gam.put(SENDER_ID, spacePrettyName);
      gam.put(SENDER_TYPE, SpaceIdentityProvider.NAME);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(GENERIC_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void markActivityGamificationHistoryAsDeleted(String activityId) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(OBJECT_ID_PARAM, activityId);
      gam.put(OBJECT_TYPE_PARAM, ACTIVITY_OBJECT_TYPE);
      listenerService.broadcast(DELETE_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

}

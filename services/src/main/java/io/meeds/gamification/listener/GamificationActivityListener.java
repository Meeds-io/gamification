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

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;
import static io.meeds.gamification.constant.GamificationConstant.BROADCAST_GAMIFICATION_EVENT_ERROR;
import static io.meeds.gamification.constant.GamificationConstant.EVENT_NAME;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_ADD_ACTIVITY_MY_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_ADD_ACTIVITY_NETWORK_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.*;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_TARGET;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_ADD_ACTIVITY_TARGET_USER_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_ADD_COMMENT_NETWORK_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_ADD_COMMENT_SPACE_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_NETWORK_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_TARGET;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_TARGET_USER_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM_TARGET;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM_TARGET;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RECEIVE_COMMENT_NETWORK_STREAM;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RECEIVE_COMMENT_SPACE_STREAM;
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
    // Target Activity
    ExoSocialActivity activity = event.getSource();
    // This listener track all kind of activities

    // Add activity on Space Stream : Compute actor reward
    Space space = getSpaceOfActivity(activity);
    if (space != null) {
      if (activity.getType() == null || !activity.getType().equals("SPACE_ACTIVITY")) {
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getPosterId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM,
                                               activity.getId());

        if (space.getManagers() != null && space.getManagers().length > 0) {
          String[] spaceManagers = space.getManagers();
          for (String spaceManager : spaceManagers) {
            createActivityGamificationHistoryEntry(spaceManager,
                                                   spaceManager,
                                                   GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_TARGET,
                                                   activity.getId());
          }
        }

        createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                            activity.getPosterId(),
                                            GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM,
                                            activity.getId());
      }
    } else { // Comment in the context of User Stream

      // User comment on his own Stream : no XP should be assigned
      if (StringUtils.equalsIgnoreCase(activity.getPosterId(), activity.getStreamId())) {
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getStreamId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_MY_STREAM,
                                               activity.getId());
      } else { // User add an activity on his network's Stream
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getStreamId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_TARGET_USER_STREAM,
                                               activity.getId());

        // Each user who get a new activity on his stream will be rewarded
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getPosterId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_NETWORK_STREAM,
                                               activity.getId());
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

    // Target Activity
    ExoSocialActivity activity = event.getSource();

    /**
     * Three usescase Case 1 : Assign XP to user who made a comment on the
     * Stream of a space Case 2 : Assign XP to user who made a comment on the
     * Stream of one of his network Case 3 : Assign XP to user who made a
     * comment on his own stream : NO
     */

    // Get ActivityStream
    ExoSocialActivity parent = activityManager.getParentActivity(activity);
    if (parent == null || StringUtils.equalsIgnoreCase(activity.getPosterId(), activity.getStreamId())) {
      return;
    }

    Space space = getSpaceOfActivity(parent);
    boolean isSpaceActivity = space != null;

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           parent.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_ADD_COMMENT_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_ADD_COMMENT_NETWORK_STREAM,
                                           activity.getId());

    createActivityGamificationHistoryEntry(parent.getPosterId(),
                                           activity.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_RECEIVE_COMMENT_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_RECEIVE_COMMENT_NETWORK_STREAM,
                                           activity.getId());

    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          activity.getPosterId(),
                                          GAMIFICATION_SOCIAL_ADD_COMMENT_SPACE_STREAM,
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
    // Target Activity
    ExoSocialActivity activity = event.getSource();
    if (StringUtils.equalsIgnoreCase(activity.getPosterId(), activity.getStreamId())) {
      return;
    }

    // This listener track all kind of activities
    /**
     * Case 1 : Assign XP to user who like an activity on a space stream Case 2
     * : Assign XP to user who like an activity on a network stream Case 3 :
     * Assign XP to user who has an activity liked on his own stream Case 4 :
     * Assign XP to user who has an activity liked on a space stream
     */

    // a user like an activity on space stream
    String[] likersId = activity.getLikeIdentityIds();
    String liker = identityManager.getIdentity(likersId[likersId.length - 1]).getId();

    Space space = getSpaceOfActivity(activity);
    boolean isSpaceActivity = space != null;

    createActivityGamificationHistoryEntry(liker,
                                           activity.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_LIKE_ACTIVITY_NETWORK_STREAM,
                                           activity.getId());

    // Reward user when his activity within a stream is liked by someone
    // else Get associated rule : a user like an activity on space stream
    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           liker,
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_TARGET
                                                           : GAMIFICATION_SOCIAL_LIKE_ACTIVITY_TARGET_USER_STREAM,
                                           activity.getId());

    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          liker,
                                          GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM,
                                          activity.getId());
    }
  }

  @Override
  public void deleteLikeActivity(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    String userId = event.getUserId();
    if (StringUtils.equalsIgnoreCase(activity.getPosterId(), activity.getStreamId())) {
      return;
    }

    Space space = getSpaceOfActivity(activity);
    boolean isSpaceActivity = space != null;

    cancelActivityGamificationHistoryEntry(userId,
                                           activity.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_LIKE_ACTIVITY_NETWORK_STREAM,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    cancelActivityGamificationHistoryEntry(activity.getPosterId(),
                                           userId,
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_TARGET
                                                           : GAMIFICATION_SOCIAL_LIKE_ACTIVITY_TARGET_USER_STREAM,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    if (space != null) {
      cancelSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          userId,
                                          GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM,
                                          activity.getId(),
                                          ACTIVITY_OBJECT_TYPE);
    }
  }

  @Override
  public void likeComment(ActivityLifeCycleEvent event) {

    // Target Activity
    ExoSocialActivity activity = event.getSource();
    if (StringUtils.equalsIgnoreCase(activity.getPosterId(), activity.getStreamId())) {
      return;
    }

    String[] likersId = activity.getLikeIdentityIds();
    String liker = identityManager.getIdentity(likersId[likersId.length - 1]).getId();
    /**
     * Three usescase Case 1 : Assign XP to user who has a comment liked on his
     * own stream Case 2 : Assign XP to user who has a comment liked on his own
     * stream Case 3 : Assign XP to each user who like a comment
     */

    // This listener track all kind of activities
    /**
     * Case 1 : Assign XP to user who like an activity on a space stream Case 2
     * : Assign XP to user who like an activity on a network stream Case 3 :
     * Assign XP to user who has an activity liked on his own stream Case 4 :
     * Assign XP to user who has an activity liked on a space stream
     */
    Space space = getSpaceOfActivity(activity);
    boolean isSpaceActivity = space != null;

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           liker,
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM_TARGET
                                                           : GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM_TARGET,
                                           activity.getId());

    // a user like a comment made by another user on the stream of other user
    createActivityGamificationHistoryEntry(liker,
                                           activity.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM,
                                           activity.getId());

    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          liker,
                                          GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM_TARGET,
                                          activity.getId());
    }
  }

  @Override
  public void deleteLikeComment(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getSource();
    String userId = event.getUserId();
    if (StringUtils.equalsIgnoreCase(activity.getPosterId(), activity.getStreamId())) {
      return;
    }
    Space space = getSpaceOfActivity(activity);
    boolean isSpaceActivity = space != null;

    cancelActivityGamificationHistoryEntry(activity.getPosterId(),
                                           userId,
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM_TARGET
                                                           : GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM_TARGET,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    cancelActivityGamificationHistoryEntry(userId,
                                           activity.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM,
                                           activity.getId(),
                                           ACTIVITY_OBJECT_TYPE);

    if (space != null) {
      cancelSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          userId,
                                          GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM_TARGET,
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

  private void createActivityGamificationHistoryEntry(String earnerIdentityId, String receiverId, String gamificationEventName,
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

  private void cancelActivityGamificationHistoryEntry(String senderId,
                                                      String receiverId,
                                                      String gamificationEventName,
                                                      String objectId,
                                                      String objectType) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, objectId);
      gam.put(OBJECT_TYPE_PARAM, objectType);
      gam.put(SENDER_ID, senderId);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(CANCEL_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }

  private void cancelSpaceGamificationHistoryEntry(String senderId,
                                                   String receiverId,
                                                   String gamificationEventName,
                                                   String objectId,
                                                   String objectType) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, gamificationEventName);
      gam.put(OBJECT_ID_PARAM, objectId);
      gam.put(OBJECT_TYPE_PARAM, objectType);
      gam.put(SENDER_ID, senderId);
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

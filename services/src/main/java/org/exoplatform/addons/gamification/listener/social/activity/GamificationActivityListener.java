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
package org.exoplatform.addons.gamification.listener.social.activity;

import static org.exoplatform.addons.gamification.GamificationConstant.*;
import static org.exoplatform.addons.gamification.listener.generic.GamificationGenericListener.EVENT_NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.commons.utils.CommonsUtils;
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

public class GamificationActivityListener extends ActivityListenerPlugin {


  private static final Log  LOG = ExoLogger.getLogger(GamificationActivityListener.class);

  protected RuleService     ruleService;

  protected IdentityManager identityManager;

  protected SpaceService    spaceService;

  protected ActivityManager activityManager;

  protected ListenerService listenerService;

  public GamificationActivityListener() {
    this.ruleService = CommonsUtils.getService(RuleService.class);
    this.identityManager = CommonsUtils.getService(IdentityManager.class);
    this.spaceService = CommonsUtils.getService(SpaceService.class);
    this.activityManager = CommonsUtils.getService(ActivityManager.class);
    this.listenerService = CommonsUtils.getService(ListenerService.class);
  }

  @Override
  public void saveActivity(ActivityLifeCycleEvent event) {
    // Target Activity
    ExoSocialActivity activity = event.getSource();
    // This listener track all kind of activities

    /**
     * Three usescase Case 1 : Assign XP to user who add an activity on space
     * stream Case 2 : Assign XP to user who add an activity on his network
     * stream Case 3 : Assign XP to user who add an activity on his own space
     * Case 4 : Assign XP to owner of Stream on which an activity has been added
     * (except the user himself) Case 5 : Assign XP to space's manager on which
     * an activity has been added
     */
    String activityUrl = getActivityUrl(activity);
    if (isDocumentShareActivity(activity)) {
      createActivityGamificationHistoryEntry(activity.getPosterId(),
                                             activity.getPosterId(),
                                             GAMIFICATION_KNOWLEDGE_SHARE_UPLOAD__DOCUMENT_NETWORK_STREAM,
                                             activityUrl);
    }

    // Add activity on Space Stream : Compute actor reward
    Space space = getSpaceOfActivity(activity);
    if (space != null) {
      if (!activity.getType().equals("SPACE_ACTIVITY")) {
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getPosterId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM,
                                               activityUrl);

        if (space.getManagers() != null && space.getManagers().length > 0) {
          String spaceManager = space.getManagers()[0];
          createActivityGamificationHistoryEntry(spaceManager,
                                                 spaceManager,
                                                 GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM,
                                                 activityUrl);
        }

        createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                            activity.getPosterId(),
                                            GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM,
                                            activityUrl);
      }
    } else { // Comment in the context of User Stream

      // User comment on his own Stream : no XP should be assigned
      if (StringUtils.equalsIgnoreCase(activity.getPosterId(), activity.getStreamId())) {
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getStreamId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_MY_STREAM,
                                               activityUrl);
      } else { // User add an activity on his network's Stream
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getStreamId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_TARGET_USER_STREAM,
                                               activityUrl);

        // Each user who get a new activity on his stream will be rewarded
        createActivityGamificationHistoryEntry(activity.getPosterId(),
                                               activity.getPosterId(),
                                               GAMIFICATION_SOCIAL_ADD_ACTIVITY_NETWORK_STREAM,
                                               activityUrl);
      }
    }
  }

  @Override
  public void updateActivity(ActivityLifeCycleEvent event) {
    // Update activity abstract method was modified untill the spec will be
    // provided

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

    String activityUrl = getActivityUrl(activity);
    Space space = getSpaceOfActivity(parent);

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           activity.getPosterId(),
                                           GAMIFICATION_SOCIAL_COMMENT_ADD,
                                           activityUrl);

    boolean isSpaceActivity = space != null;
    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           parent.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_COMMENT_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_COMMENT_NETWORK_STREAM,
                                           activityUrl);

    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          activity.getPosterId(),
                                          GAMIFICATION_SOCIAL_COMMENT_ADD,
                                          activityUrl);
    }
  }

  public void updateComment(ActivityLifeCycleEvent activityLifeCycleEvent) {
    // Waiting for spec to be implemented
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
    String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();

    String activityUrl = getActivityUrl(activity);
    Space space = getSpaceOfActivity(activity);
    boolean isSpaceActivity = space != null;

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           liker,
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_LIKE_ACTIVITY_NETWORK_STREAM,
                                           activityUrl);

    // Reward user when his activity within a stream is liked by someone
    // else Get associated rule : a user like an activity on space stream
    createActivityGamificationHistoryEntry(liker,
                                           activity.getPosterId(),
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_TARGET
                                                           : GAMIFICATION_SOCIAL_LIKE_ACTIVITY_TARGET_USER_STREAM,
                                           activityUrl);

    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          liker,
                                          GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM,
                                          activityUrl);
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
    String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();
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
    String activityUrl = getActivityUrl(activity);
    Space space = getSpaceOfActivity(activity);
    boolean isSpaceActivity = space != null;

    createActivityGamificationHistoryEntry(activity.getPosterId(),
                                           liker,
                                           isSpaceActivity ? GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM
                                                           : GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM,
                                           activityUrl);

    // a user like a comment made by another user on the stream of other user
    createActivityGamificationHistoryEntry(liker,
                                           activity.getPosterId(),
                                           GAMIFICATION_SOCIAL_LIKE_COMMENT,
                                           activityUrl);

    if (space != null) {
      createSpaceGamificationHistoryEntry(space.getPrettyName(),
                                          liker,
                                          GAMIFICATION_SOCIAL_LIKE_COMMENT,
                                          activityUrl);
    }
  }

  private Space getSpaceOfActivity(ExoSocialActivity activity) {
    if (activity.getParentId() != null) {
      activity = activityManager.getParentActivity(activity);
    }
    return isSpaceActivity(activity) ? spaceService.getSpaceByPrettyName(activity.getActivityStream().getPrettyId()) : null;
  }

  private boolean isSpaceActivity(ExoSocialActivity activity) {
    return activity.getActivityStream() != null
        && ActivityStream.Type.SPACE.equals(activity.getActivityStream().getType());
  }

  private boolean isDocumentShareActivity(ExoSocialActivity activity) {
    return activity.getType().equalsIgnoreCase("files:spaces") || activity.getType().equalsIgnoreCase("DOC_ACTIVITY")
        || activity.getType().equalsIgnoreCase("contents:spaces");
  }

  private String getActivityUrl(ExoSocialActivity activity) {
    String activityId = activity.getParentId() == null ? activity.getId() : activity.getParentId();
    String commentId = activity.getParentId() == null ? null : activity.getId();

    String activityUrl = "/portal/intranet/activity?id=" + activityId;
    if (commentId != null) {
      activityUrl += "&commentId=" + commentId;
    }
    return activityUrl;
  }

  private void createActivityGamificationHistoryEntry(String senderId, String receiverId, String ruleTitle, String activityUrl) {
    try {
      Map<String, String> gam = new HashMap<>();
      gam.put("ruleTitle", ruleTitle);
      gam.put("object", activityUrl);
      gam.put("senderId", senderId);
      gam.put("receiverId", receiverId);
      listenerService.broadcast(EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.error("Cannot broadcast gamification event", e);
    }
  }

  private void createSpaceGamificationHistoryEntry(String spacePrettyName,
                                                   String receiverId,
                                                   String ruleTitle,
                                                   String activityUrl) {
    try {
      Map<String, String> gam = new HashMap<>();
      gam.put("ruleTitle", ruleTitle);
      gam.put("object", activityUrl);
      gam.put("senderId", spacePrettyName);
      gam.put("senderType", SpaceIdentityProvider.NAME);
      gam.put("receiverId", receiverId);
      listenerService.broadcast(EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.error("Cannot broadcast gamification event", e);
    }
  }

}

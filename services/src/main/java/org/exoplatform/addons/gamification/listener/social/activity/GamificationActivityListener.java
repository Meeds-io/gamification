package org.exoplatform.addons.gamification.listener.social.activity;

import static org.exoplatform.addons.gamification.GamificationConstant.*;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

@Asynchronous
public class GamificationActivityListener extends ActivityListenerPlugin {

  private static final Log      LOG = ExoLogger.getLogger(GamificationActivityListener.class);

  protected RuleService         ruleService;

  protected IdentityManager     identityManager;

  protected SpaceService        spaceService;

  protected GamificationService gamificationService;

  protected ActivityManager     activityManager;

  public GamificationActivityListener() {
    this.ruleService = CommonsUtils.getService(RuleService.class);
    this.identityManager = CommonsUtils.getService(IdentityManager.class);
    this.spaceService = CommonsUtils.getService(SpaceService.class);
    this.gamificationService = CommonsUtils.getService(GamificationService.class);
    this.activityManager = CommonsUtils.getService(ActivityManager.class);

  }

  @Override
  public void saveActivity(ActivityLifeCycleEvent event) {

    // Target Activity
    ExoSocialActivity activity = event.getSource();
    // This listener track all kind of activities

    /**
     * Three usescase Case 1 : Assign XP to user who add an activity on space stream
     * Case 2 : Assign XP to user who add an activity on his network stream Case 3 :
     * Assign XP to user who add an activity on his own space Case 4 : Assign XP to
     * owner of Stream on which an activity has been added (except the user himself)
     * Case 5 : Assign XP to space's manager on which an activity has been added
     */
    GamificationActionsHistory aHistory = null;
    // To hold GamificationRule
    RuleDTO ruleDto = null;

    if (!activity.getType().equals("SPACE_ACTIVITY")) {
      if (activity.getType().equalsIgnoreCase("files:spaces") || activity.getType().equalsIgnoreCase("DOC_ACTIVITY")
          || activity.getType().equalsIgnoreCase("contents:spaces")) {

        gamificationService.createHistory(GAMIFICATION_KNOWLEDGE_SHARE_UPLOAD__DOCUMENT_NETWORK_STREAM,
                                          activity.getPosterId(),
                                          activity.getPosterId(),
                                          "/portal/intranet/activity?id=" + activity.getId());

      }
    }

    // Add activity on Space Stream : Compute actor reward
    if (isSpaceActivity(activity)) {
      if (!activity.getType().equals("SPACE_ACTIVITY")) {

        gamificationService.createHistory(GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM,
                                          activity.getPosterId(),
                                          activity.getPosterId(),
                                          "/portal/intranet/activity?id=" + activity.getId());

        String spaceManager = spaceService.getSpaceByPrettyName(identityManager.getIdentity(activity.getStreamId(), false)
                                                                               .getRemoteId())
                                          .getManagers()[0];

        /** Apply gamification rule on space's manager */
        gamificationService.createHistory(GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_TARGET,
                                          activity.getPosterId(),
                                          spaceManager,
                                          "/portal/intranet/activity?id=" + activity.getId());

      }

    } else { // Comment in the context of User Stream

      // User comment on his own Stream : no XP should be assigned
      if (activity.getPosterId().equalsIgnoreCase(activity.getStreamId())) {

        gamificationService.createHistory(GAMIFICATION_SOCIAL_ADD_ACTIVITY_MY_STREAM,
                                          activity.getPosterId(),
                                          activity.getStreamId(),
                                          "/portal/intranet/activity?id=" + activity.getId());

      } else { // User add an activity on his network's Stream

        // a user add an activity on the Stream of one of his network
        gamificationService.createHistory(GAMIFICATION_SOCIAL_ADD_ACTIVITY_TARGET_USER_STREAM,
                                          activity.getPosterId(),
                                          activity.getStreamId(),
                                          "/portal/intranet/activity?id=" + activity.getId());

        // Each user who get a new activity on his stream will be rewarded

        gamificationService.createHistory(GAMIFICATION_SOCIAL_ADD_ACTIVITY_NETWORK_STREAM,
                                          activity.getPosterId(),
                                          activity.getPosterId(),
                                          "/portal/intranet/activity?id=" + activity.getId());

        if ((!activity.getType().equalsIgnoreCase("DOC_ACTIVITY"))) {
          return;
        }
      }
    }
  }

  @Override
  public void updateActivity(ActivityLifeCycleEvent event) {
    // Update activity abstract method was modified untill the spec will be provided

  }

  @Override
  public void saveComment(ActivityLifeCycleEvent event) {

    // Target Activity
    ExoSocialActivity activity = event.getSource();

    /**
     * Three usescase Case 1 : Assign XP to user who made a comment on the Stream of
     * a space Case 2 : Assign XP to user who made a comment on the Stream of one of
     * his network Case 3 : Assign XP to user who made a comment on his own stream :
     * NO
     */

    // Get ActivityStream
    ExoSocialActivity parent = activityManager.getParentActivity(activity);

    if (activity.getPosterId().equalsIgnoreCase(activity.getStreamId()))
      return;

    if (parent != null) {

      if (!parent.getType().equals("SPACE_ACTIVITY")) {
        gamificationService.createHistory(GAMIFICATION_SOCIAL_COMMENT_NETWORK_STREAM,
                                          activity.getPosterId(),
                                          parent.getPosterId(),
                                          "/portal/intranet/activity?id=" + parent.getId() + "#comment-" + activity.getId());

        gamificationService.createHistory(GAMIFICATION_SOCIAL_COMMENT_ADD,
                                          activity.getPosterId(),
                                          activity.getPosterId(),
                                          "/portal/intranet/activity?id=" + parent.getId() + "#comment-" + activity.getId());

      } else {
        gamificationService.createHistory(GAMIFICATION_SOCIAL_COMMENT_SPACE_STREAM,
                                          activity.getPosterId(),
                                          parent.getPosterId(),
                                          "/portal/intranet/activity?id=" + parent.getId() + "#comment-" + activity.getId());

        gamificationService.createHistory(GAMIFICATION_SOCIAL_COMMENT_ADD,
                                          activity.getPosterId(),
                                          activity.getPosterId(),
                                          "/portal/intranet/activity?id=" + parent.getId() + "#comment-" + activity.getId());

      }
    }

  }

  public void updateComment(ActivityLifeCycleEvent activityLifeCycleEvent) {
    // Waiting for spec to be implemented
  }

  @Override
  public void likeActivity(ActivityLifeCycleEvent event) {

    // Target Activity
    ExoSocialActivity activity = event.getSource();

    // This listener track all kind of activities
    /**
     * Case 1 : Assign XP to user who like an activity on a space stream Case 2 :
     * Assign XP to user who like an activity on a network stream Case 3 : Assign XP
     * to user who has an activity liked on his own stream Case 4 : Assign XP to
     * user who has an activity liked on a space stream
     */

    GamificationActionsHistory aHistory = null;
    // To hold GamificationRule
    RuleDTO ruleDto = null;

    // Like Activity on Space Stream:
    if (isSpaceActivity(activity)) {

      // a user like an activity on space stream
      String[] likersId = activity.getLikeIdentityIds();
      String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();
      gamificationService.createHistory(GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM,
                                        activity.getPosterId(),
                                        liker,
                                        "/portal/intranet/activity?id=" + activity.getId() + "#comment-" + activity.getId());

      // Reward user when his activity within a space stream is liked by someone else
      // Get associated rule : a user like an activity on space stream
      gamificationService.createHistory(GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_TARGET,
                                        liker,
                                        activity.getPosterId(),
                                        "/portal/intranet/activity?id=" + activity.getId());

    } else { // Like activity on user's streams

      // Compute activity's liker
      String[] likersId = activity.getLikeIdentityIds();
      String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();

      // User like an activity on his own Stream
      if (activity.getPosterId().equalsIgnoreCase(liker)) {
        return;
      } else {
        // a user like an activity on network stream
        gamificationService.createHistory(GAMIFICATION_SOCIAL_LIKE_ACTIVITY_NETWORK_STREAM,
                                          activity.getPosterId(),
                                          liker,
                                          "/portal/intranet/activity?id=" + activity.getId() + "#comment-" + activity.getId());

        // Reward a user each time another user likes his activity
        gamificationService.createHistory(GAMIFICATION_SOCIAL_LIKE_ACTIVITY_TARGET_USER_STREAM,
                                          liker,
                                          activity.getPosterId(),
                                          "/portal/intranet/activity?id=" + activity.getId() + "#comment-" + activity.getId());
      }
    }

  }

  @Override
  public void likeComment(ActivityLifeCycleEvent event) {

    // Target Activity
    ExoSocialActivity activity = event.getSource();
    String[] likersId = activity.getLikeIdentityIds();
    String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();
    /**
     * Three usescase Case 1 : Assign XP to user who has a comment liked on his own
     * stream Case 2 : Assign XP to user who has a comment liked on his own stream
     * Case 3 : Assign XP to each user who like a comment
     */
    GamificationActionsHistory aHistory = null;
    // To hold GamificationRule
    RuleDTO ruleDto = null;
    // Like in the context of Space Stream
    if (isSpaceActivity(activity)) {
      gamificationService.createHistory(GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM,
                                        liker,
                                        activity.getPosterId(),
                                        "/portal/intranet/activity?id=" + activity.getParentId() + "#comment-"
                                            + activity.getId());

    } else { // Like in the context of User Stream

      // User like a comment on his own Stream : no XP should be assigned
      if (activity.getPosterId().equalsIgnoreCase(activity.getStreamId())) {
        return;
      } else { // User like a comment on his network's Stream

        // a user like a comment made by another user on the stream of other user
        gamificationService.createHistory(GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM,
                                          activity.getPosterId(),
                                          liker,
                                          "/portal/intranet/activity?id=" + activity.getParentId() + "#comment-"
                                              + activity.getId());

      }

    }

    // a user like a comment made by another user on the stream of other user
    gamificationService.createHistory(GAMIFICATION_SOCIAL_LIKE_COMMENT,
                                      liker,
                                      activity.getPosterId(),
                                      "/portal/intranet/activity?id=" + activity.getParentId() + "#comment-" + activity.getId());
  }

  // TODO : use eXo stack
  public boolean isSpaceActivity(ExoSocialActivity activity) {
    Identity id = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, activity.getStreamOwner(), false);
    return (id != null);
  }

}

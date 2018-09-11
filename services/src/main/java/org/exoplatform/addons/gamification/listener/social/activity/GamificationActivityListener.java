package org.exoplatform.addons.gamification.listener.social.activity;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationContextHolder;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Asynchronous
public class GamificationActivityListener extends ActivityListenerPlugin implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationActivityListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;
    protected ActivityManager activityManager;


    public GamificationActivityListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationProcessor = CommonsUtils.getService(GamificationProcessor.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
        this.activityManager = CommonsUtils.getService(ActivityManager.class);

    }

    @Override
    public void saveActivity(ActivityLifeCycleEvent event) {

        // Target Activity
        ExoSocialActivity activity = event.getSource();

        // This listener track only classic activities
        if (!activity.getType().equalsIgnoreCase("DEFAULT_ACTIVITY")) return;
        /**
         *  Three usescase
         *  Case 1 : Assign XP to user who add an activity on space stream
         *  Case 2 : Assign XP to user who add an activity on his network stream
         *  Case 3 : Assign XP to user who add an activity on his own space
         *  Case 4 : Assign XP to owner of Stream on which an activity has been added (except the user himself)
         *  Case 5 : Assign XP to space's manager  on which an activity has been added
         */

        List<GamificationContextHolder> gamificationContextEntityList = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Add activity on Space Stream : Compute actor reward
        if (isSpaceActivity(activity)) {
            // Get associated rule :
            ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_STREAM);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    gamificationContextEntityList = gamify(ruleDto, activity.getPosterId());
                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }
            }

            /** Apply gamification rule on space's manager*/
            // Get associated rule :
            ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ADD_ACTIVITY_SPACE_TARGET);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    gamificationContextEntityList.addAll(gamify(ruleDto, activity.getPosterId()));
                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }
            }

        } else { // Comment in the context of User Stream

            // User comment on his own Stream : no XP should be assigned
            if (activity.getPosterId().equalsIgnoreCase(activity.getStreamId())) {

                // Get associated rule : Reward a user when he add an activity on his own stream
                ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ADD_ACTIVITY_MY_STREAM);

                // Process only when an enable rule is found
                if (ruleDto != null) {
                    try {
                        gamificationContextEntityList = gamify(ruleDto, activity.getStreamId());
                    } catch (Exception e) {
                        LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                    }
                }

            } else { // User add an activity on his network's Stream

                // Get associated rule : a user add an activity on the Stream of one of his network
                ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ADD_ACTIVITY_NETWORK_STREAM);

                // Process only when an enable rule is found
                if (ruleDto != null) {
                    try {
                        gamificationContextEntityList = gamify(ruleDto, activity.getPosterId());
                    } catch (Exception e) {
                        LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                    }
                }

                // Get associated rule : Each user who get a new activity on his stream will be rewarded
                ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ADD_ACTIVITY_TARGET_USER_STREAM);

                // Process only when an enable rule is found
                if (ruleDto != null) {
                    try {
                        gamificationContextEntityList.addAll(gamify(ruleDto, activity.getStreamId()));
                    } catch (Exception e) {
                        LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                    }
                }

            }

        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);

    }

    @Override
    public void updateActivity(ActivityLifeCycleEvent event) {


    }

    @Override
    public void saveComment(ActivityLifeCycleEvent event) {

        // Target Activity
        ExoSocialActivity activity = event.getSource();

        /**
         *  Three usescase
         *  Case 1 : Assign XP to user who made a comment on the Stream of a space
         *  Case 2 : Assign XP to user who made a comment on the Stream of one of his network
         *  Case 3 : Assign XP to user who made a comment on his own stream : NO
         */

        List<GamificationContextHolder> gamificationContextEntityList = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get ActivityStream
        ExoSocialActivity parent = activityManager.getParentActivity(activity);

        // This listener track only classic activities
        if (!parent.getType().equalsIgnoreCase("DEFAULT_ACTIVITY")) return;

        // Comment in the context of Space Stream
        if ((parent != null) && (isSpaceActivity(parent))) {
            // Get associated rule :
            ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_COMMENT_SPACE_STREAM);

            // Process only when an enable rule is found
            if (ruleDto != null) {

                try {
                    gamificationContextEntityList = gamify(ruleDto, activity.getPosterId());
                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }
            }

        } else { // Comment in the context of User Stream

            // User comment on his own Stream : no XP should be assigned
            if (activity.getPosterId().equalsIgnoreCase(activity.getStreamId())) {

            } else { // User add a comment on the stream of his network

                // Get associated rule : User add a comment on network's stream
                ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_COMMENT_NETWORK_STREAM);

                // Process only when an enable rule is found
                if (ruleDto != null) {

                    try {
                        gamificationContextEntityList = gamify(ruleDto, activity.getPosterId());
                    } catch (Exception e) {
                        LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                    }

                }
            }
        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);


    }

    @Override
    public void likeActivity(ActivityLifeCycleEvent event) {

        // Target Activity
        ExoSocialActivity activity = event.getSource();

        // This listener track only classic activities
        if (!activity.getType().equalsIgnoreCase("DEFAULT_ACTIVITY")) return;
        /**
         *
         *  Case 1 : Assign XP to user who like an activity on a space stream
         *  Case 2 : Assign XP to user who like an activity on a network stream
         *  Case 3 : Assign XP to user who has an activity liked on his own stream
         *  Case 4 : Assign XP to user who has an activity liked on a space stream
         */


        List<GamificationContextHolder> gamificationContextEntityList = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Like Activity on Space Stream:
        if (isSpaceActivity(activity)) {
            // Get associated rule : a user like an activity on space stream
            ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_STREAM);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    // Compute activity's liker
                    String[] likersId = activity.getLikeIdentityIds();
                    String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();
                    gamificationContextEntityList = gamify(ruleDto, liker);
                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }
            }

            // Reward user when his activity within a space stream is liked by someone else
            // Get associated rule : a user like an activity on space stream
            ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_SPACE_TARGET);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    gamificationContextEntityList.addAll(gamify(ruleDto, activity.getPosterId()));
                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }
            }


        } else { // Like activity on user's streams

            // Compute activity's liker
            String[] likersId = activity.getLikeIdentityIds();
            String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();

            // User like an activity on his own Stream
            if (activity.getPosterId().equalsIgnoreCase(liker)) {

            } else {
                // Get associated rule : a user like an activity on network stream
                ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_NETWORK_STREAM);

                // Process only when an enable rule is found
                if (ruleDto != null) {
                    try {
                        gamificationContextEntityList = gamify(ruleDto, liker);
                    } catch (Exception e) {
                        LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                    }
                }

                // Get associated rule : Reward a user each time another user likes his activity
                ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_LIKE_ACTIVITY_TARGET_USER_STREAM);

                // Process only when an enable rule is found
                if (ruleDto != null) {
                    try {
                        gamificationContextEntityList.addAll(gamify(ruleDto, activity.getPosterId()));
                    } catch (Exception e) {
                        LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                    }
                }

            }
        }
        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);

    }

    @Override
    public void likeComment(ActivityLifeCycleEvent event) {

        // Target Activity
        ExoSocialActivity activity = event.getSource();

        /**
         *  Three usescase
         *  Case 1 : Assign XP to user who has a comment liked on his own stream
         *  Case 2 : Assign XP to user who has a comment liked on his own stream
         *  Case 3 : Assign XP to each user who like a comment
         */

        List<GamificationContextHolder> gamificationContextEntityList = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Like in the context of Space Stream
        if (isSpaceActivity(activity)) {

            // Get associated rule :
            ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_LIKE_COMMENT_SPACE_STREAM);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    gamificationContextEntityList = gamify(ruleDto, activity.getPosterId());
                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }

            }
        } else { // Like in the context of User Stream

            // User like a comment on his own Stream : no XP should be assigned
            if (activity.getPosterId().equalsIgnoreCase(activity.getStreamId())) {

            } else { // User like a comment on his network's Stream

                // Get comment's owner
                //String commentOwner = activity.getPosterId();
                // Get coment's owner username
                //String liker = identityManager.getIdentity(commentOwner, false).getRemoteId();

                // Start build GamificationContext entry

                // Get associated rule : a user like a comment made by another user on the stream of other user
                ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_LIKE_COMMENT_NETWORK_STREAM);


                // Process only when an enable rule is found
                if (ruleDto != null) {

                    try {
                        gamificationContextEntityList = gamify(ruleDto, activity.getPosterId());
                    } catch (Exception e) {
                        LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                    }

                }
            }

        }

        // Liker get XP

        // Get associated rule : a user like a comment made by another user on the stream of other user
        ruleDto = ruleService.findEnableRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_LIKE_COMMENT);

        // Process only when an enable rule is found
        if (ruleDto != null) {

            // Compute comment's liker
            String[] likersId = activity.getLikeIdentityIds();
            String liker = identityManager.getIdentity(likersId[likersId.length - 1], false).getId();
            try {
                gamificationContextEntityList.addAll(gamify(ruleDto, activity.getPosterId()));
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }

        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);

    }


    /**
     * Compute GamificationContext for each social action made by enduser
     *
     * @param ruleDto
     * @param actor
     * @return GamificationContextHolder : a list of GamificationContextHolder to be persist in DB
     * @throws Exception
     */
    @Override
    public List<GamificationContextHolder> gamify(RuleDTO ruleDto, String actor) throws Exception {

        List<GamificationContextHolder> gamificationContextEntityList = new ArrayList<GamificationContextHolder>();

        // Build GamificationContextHolder
        GamificationContextHolder contextHolder = null;

        // Build a gamificationContext entry
        GamificationContextEntity gamificationContextEntity = null;

        // Process only when an enable rule is found
        if (ruleDto != null) {
            //Find if a gamificationContext exists for the current user
            gamificationContextEntity = gamificationService.findGamificationContextByUsername(actor);

            // Start building GamificationContextHolder
            contextHolder = new GamificationContextHolder();

            if (gamificationContextEntity != null) {

                // Load gamification  items
                final String title = ruleDto.getTitle();
                Set<GamificationContextItemEntity> gamificationContextItemEntitySet = gamificationContextEntity.getGamificationItems()
                        .stream()
                        .filter(item -> item.getOpType().equalsIgnoreCase(title))
                        .collect(Collectors.toSet());

                if (gamificationContextItemEntitySet != null && !gamificationContextItemEntitySet.isEmpty()) {
                    gamificationContextItemEntitySet.forEach(item -> {
                        item.setOccurrence(item.getOccurrence() + 1);
                        item.setScore(item.getScore()+ruleDto.getScore());
                    });

                    // Update user's global score
                    gamificationContextEntity.setScore(gamificationContextEntity.getScore() + ruleDto.getScore());

                } else { // Create a new item entry
                    GamificationContextItemEntity gamificationContextItemEntity = new GamificationContextItemEntity();
                    gamificationContextItemEntity.setOpType(title);
                    gamificationContextItemEntity.setZone(ruleDto.getArea());

                    gamificationContextItemEntity.setOccurrence(1);

                    // Set the score
                    gamificationContextItemEntity.setScore(ruleDto.getScore());

                    // Compute Global Score
                    gamificationContextEntity.setScore(gamificationContextEntity.getScore()+ruleDto.getScore());
                    // Link GamificationItem : parent/child
                    gamificationContextEntity.addGamificationItem(gamificationContextItemEntity);

                }

            } else {

                // Create new Gamification for current user
                gamificationContextEntity = new GamificationContextEntity();
                //gamificationContextEntity.setId(null);
                gamificationContextEntity.setUsername(actor);
                gamificationContextEntity.setScore(ruleDto.getScore());

                // Create specific gamification item for ingoing action
                GamificationContextItemEntity gamificationContextItemEntity = new GamificationContextItemEntity();

                gamificationContextItemEntity.setOccurrence(1);

                gamificationContextItemEntity.setScore(gamificationContextItemEntity.getScore()+ruleDto.getScore());

                gamificationContextItemEntity.setOpType(ruleDto.getTitle());

                gamificationContextItemEntity.setZone(ruleDto.getArea());

                // compute current score
                gamificationContextItemEntity.setScore(ruleDto.getScore());

                // Link GamificationItem to its parent
                gamificationContextItemEntity.setGamificationUserEntity(gamificationContextEntity);

                // Add GamificationItem as child to GamificationContext
                gamificationContextEntity.getGamificationItems().add(gamificationContextItemEntity);

                // Udapte action (create enw enity)
                contextHolder.setNew(true);

            }

            // Gamification simple audit logger
            LOG.info("Add new auditing entry service=gamification operation=social parameters=\"data:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),actor,gamificationContextEntity.getScore(),ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());

            contextHolder.setGamificationContextEntity(gamificationContextEntity);

            // Add the GamificationContext entry to list
            gamificationContextEntityList.add(contextHolder);
        }

        return gamificationContextEntityList;

    }

    //TODO : use eXo stack
    public boolean isSpaceActivity(ExoSocialActivity activity) {
        Identity id = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, activity.getStreamOwner(), false);
        return (id != null);
    }


}

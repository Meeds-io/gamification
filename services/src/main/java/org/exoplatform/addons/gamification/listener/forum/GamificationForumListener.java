package org.exoplatform.addons.gamification.listener.forum;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.forum.service.*;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.List;

@Asynchronous
public class GamificationForumListener extends ForumEventListener implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationForumListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;
    protected ActivityManager activityManager;
    protected ForumService forumService;

    public GamificationForumListener(RuleService ruleService, GamificationProcessor gamificationProcessor, IdentityManager identityManager
            , SpaceService spaceService, GamificationService gamificationService, ActivityManager activityManager, ForumService forumService) {
        this.ruleService = ruleService;
        this.gamificationProcessor = gamificationProcessor;
        this.identityManager = identityManager;
        this.spaceService = spaceService;
        this.gamificationService = gamificationService;
        this.activityManager = activityManager;
        this.forumService = forumService;
    }

    @Override
    public void saveCategory(Category category) {
    }

    @Override
    public void saveForum(Forum forum) {
    }

    @Override
    public void addPost(Post post) {
        Forum forum;
        /**
         // Compute Activity
         String activityId = forumService.getActivityIdForOwnerId(post.getTopicId());

         //
         if (Utils.isEmpty(activityId)) {
         ExoSocialActivity forumPostActivity = activityManager.getActivity(activityId);

         //
         if (forumPostActivity == null) {

         }

         }
         */

        GamificationActionsHistory aHistory = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule : Reward user who send a relationship request
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_FORUM_ADD_POST);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                String receiver= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, post.getOwner(), false).getId();
                aHistory = build(ruleDto,identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, post.getOwner(), false).getId(),receiver,ForumUtils.createdSubForumLink(post.toString(),post.getTopicId(),true));

                // Save Gamification Context
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

    }


    @Override
    public void updatePost(Post post) {


    }

    @Override
    public void updatePost(Post post, int type) {

    }

    @Override
    public void addTopic(Topic topic) {

        GamificationActionsHistory aHistory = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule : Reward user who send a relationship request
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_FORUM_ADD_TOPIC);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                String receiver= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, topic.getOwner(), false).getId();
                aHistory = build(ruleDto, identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, topic.getOwner(), false).getId(),receiver,ForumUtils.createdSubForumLink(topic.toString(),topic.getId(),true));
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());

            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

    }

    @Override
    public void updateTopic(Topic topic) {

        PropertyChangeEvent[] events = topic.getChangeEvent();

        for (int i = 0; i < events.length; i++) {
            processUpdateTopicType(events[i], topic);
        }


    }

    @Override
    public void updateTopics(List<Topic> topics, boolean isLock) {

    }


    @Override
    public void moveTopic(Topic topic, String toCategoryName, String toForumName) {

    }


    @Override
    public void mergeTopic(Topic newTopic, String removeActivityId1, String removeActivityId2) {

    }

    @Override
    public void splitTopic(Topic newTopic, Topic splitedTopic, String removeActivityId) {

    }

    @Override
    public void removeActivity(String activityId) {

    }

    @Override
    public void removeComment(String activityId, String commentId) {

    }

    @Override
    public void movePost(List<Post> list, List<String> list1, String s) {

    }

    @Override
    public void openTopic(String userId, String topicId) {
        GamificationActionsHistory aHistory = null;
        GamificationActionsHistory aHistory1 = null;

        Topic topic = new Topic();
        // To hold GamificationRule

            RuleDTO ruleDto = null;
            // Get associated rule : Reward user who send a relationship request
            ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_FORUM_OPEN_TOPIC);
            // Topic Owner
            String topicOwner = null;
            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    // Get Topic owner
                    topicOwner = ((Topic) forumService.getObjectNameById(topicId, Utils.TOPIC)).getOwner();

                    if (topicOwner != null && topicOwner.length() != 0 && (!topicOwner.equals(userId)) ) {


                       String sender= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false).getId();
                        String receiver= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, topicOwner, false).getId();
                        // ForumUtils.createdForumLink();
                        aHistory = build(ruleDto,receiver,receiver, ForumUtils.createdSubForumLink(topic.toString(), topicId, true));

                        gamificationProcessor.execute(aHistory);
                        // Gamification simple audit logger
                        LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(), aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
                    }

                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }

        }
    }

    private void processUpdateTopicType(PropertyChangeEvent event, Topic topic) {

        // Start gamification process only when a topic is voted
        if (Topic.TOPIC_RATING.equals(event.getPropertyName())) {

            GamificationActionsHistory aHistory = null;

            // To hold GamificationRule
            RuleDTO ruleDto = null;

            // Get associated rule : Reward user who votes in the forum
            ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_FORUM_VOTE_TOPIC);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    String receiver =identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, topic.getOwner(), false).getId();
                    aHistory = build(ruleDto,identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, topic.getOwner(), false).getId(),receiver,ForumUtils.createdSubForumLink(topic.toString(),topic.getId(),true));

                    // Save Gamification Context
                    gamificationProcessor.execute(aHistory);
                    // Gamification simple audit logger
                    LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());

                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }
            }

        }

    }
}
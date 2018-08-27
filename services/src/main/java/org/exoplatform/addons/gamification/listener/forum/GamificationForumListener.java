package org.exoplatform.addons.gamification.listener.forum;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationContextHolder;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.forum.service.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<GamificationContextHolder> gamificationContextEntityList = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule : Reward user who send a relationship request
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_FORUM_ADD_POST);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto,identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, post.getOwner(), false).getId());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        if (!gamificationContextEntityList.isEmpty()) {

            // Save Gamification Context
            gamificationProcessor.process(gamificationContextEntityList);

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

        List<GamificationContextHolder> gamificationContextEntityList = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule : Reward user who send a relationship request
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_FORUM_ADD_TOPIC);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, topic.getOwner(), false).getId());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        if (!gamificationContextEntityList.isEmpty()) {

            // Save Gamification Context
            gamificationProcessor.process(gamificationContextEntityList);

        }

    }

    @Override
    public void updateTopic(Topic topic) {


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
                        // Compute the current score
                        item.setScore(item.getScore()+ ruleDto.getScore());
                    });

                    // Update user's global score
                    gamificationContextEntity.setScore(gamificationContextEntity.getScore() + ruleDto.getScore());

                } else { // Create a new item entry
                    GamificationContextItemEntity gamificationContextItemEntity = new GamificationContextItemEntity();
                    gamificationContextItemEntity.setOpType(title);
                    gamificationContextItemEntity.setZone(ruleDto.getArea());

                    gamificationContextItemEntity.setOccurrence(1);

                    // Compute current score
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
            contextHolder.setGamificationContextEntity(gamificationContextEntity);

            // Add the GamificationContext entry to list
            gamificationContextEntityList.add(contextHolder);
        }

        return gamificationContextEntityList;
    }
}

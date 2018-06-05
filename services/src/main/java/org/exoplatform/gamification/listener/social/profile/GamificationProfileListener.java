package org.exoplatform.gamification.listener.social.profile;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.gamification.listener.GamificationListener;
import org.exoplatform.gamification.service.configuration.RuleService;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.gamification.service.dto.effective.GamificationContextHolder;
import org.exoplatform.gamification.service.effective.GamificationProcessor;
import org.exoplatform.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GamificationProfileListener extends ProfileListenerPlugin implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationProfileListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationProfileListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationProcessor = CommonsUtils.getService(GamificationProcessor.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void avatarUpdated(ProfileLifeCycleEvent event) {

        List<GamificationContextHolder> gamificationContextEntityList = null;

        Long lastUpdate = event.getProfile().getAvatarLastUpdated();

        // Do not reward a user when he update his avatar, reward user only when he add an avatar for the first time
        if (lastUpdate != null) return;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule :
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, event.getProfile().getId());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);


    }

    @Override
    public void bannerUpdated(ProfileLifeCycleEvent event) {
        List<GamificationContextHolder> gamificationContextEntityList = null;

        Long lastUpdate = event.getProfile().getBannerLastUpdated();

        // Do not reward a user when he update his avatar, reward user only when he add an avatar for the first time
        if (lastUpdate != null) return;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule :
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, event.getProfile().getId());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);
    }

    @Override
    public void basicInfoUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void contactSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void experienceSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void headerSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void createProfile(ProfileLifeCycleEvent event) {


    }

    @Override
    public void aboutMeUpdated(ProfileLifeCycleEvent event) {
        List<GamificationContextHolder> gamificationContextEntityList = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule : Reward user each time he update «about me» section
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, event.getProfile().getId());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);

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
            gamificationContextEntity = gamificationService.findGamificationContextByUername(actor);

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

                    // Compute the current score
                    gamificationContextItemEntity.setScore(ruleDto.getScore());


                    // Link GamificationItem to its parent
                    gamificationContextItemEntity.setGamificationUserEntity(gamificationContextEntity);

                    // Add GamificationItem as child to GamificationContext
                    gamificationContextEntity.getGamificationItems().add(gamificationContextItemEntity);

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

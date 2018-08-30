package org.exoplatform.addons.gamification.listener.social.space;

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
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Asynchronous
public class GamificationSpaceListener extends SpaceListenerPlugin implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationSpaceListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationSpaceListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationProcessor = CommonsUtils.getService(GamificationProcessor.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void spaceCreated(SpaceLifeCycleEvent event) {
        List<GamificationContextHolder> gamificationContextEntityList = null;

        // Get the created space
        //Space space = event.getSpace();
        // Get the space's creator username
        String actorUsername = event.getSource();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        // Get associated rule
        RuleDTO ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_SPACE_ADD);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, actorId);
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);

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
        List<GamificationContextHolder> gamificationContextEntityList = null;

        // Get the created space
        //Space space = event.getSpace();
        // Get the space's creator username
        String actorUsername = event.getSource();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule :
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_SPACE_JOIN);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, actorId);
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);
    }

    @Override
    public void left(SpaceLifeCycleEvent event) {
    }

    @Override
    public void grantedLead(SpaceLifeCycleEvent event) {
        List<GamificationContextHolder> gamificationContextEntityList = null;

        // Get the created space
        //Space space = event.getSpace();
        // Get the space's creator username
        String actorUsername = event.getSource();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule :
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, actorId);
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);
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
    public void spaceBannerEdited(SpaceLifeCycleEvent event) {
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

                        // Compute current score
                        item.setScore(item.getScore()+ruleDto.getScore());

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

                // set score
                gamificationContextItemEntity.setScore(ruleDto.getScore());

                // Compute current score
                gamificationContextEntity.setScore(ruleDto.getScore());

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

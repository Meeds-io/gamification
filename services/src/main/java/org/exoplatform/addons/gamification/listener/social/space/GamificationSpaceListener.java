package org.exoplatform.addons.gamification.listener.social.space;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
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

import java.time.LocalDate;

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

        GamificationActionsHistory aHistory = null;

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
                String receiver = actorId;
                aHistory = build(ruleDto, actorId,receiver,event.getSpace().getId());

                // Save actionHistory entry
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

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

        GamificationActionsHistory aHistory = null;

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
                String receiver = actorId;
                aHistory = build(ruleDto, actorId,receiver,event.getSpace().getId());

                // Save actionHistory entry
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }
    }

    @Override
    public void left(SpaceLifeCycleEvent event) {
    }

    @Override
    public void grantedLead(SpaceLifeCycleEvent event) {

        GamificationActionsHistory aHistory = null;

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
                String receiver=actorId;
                aHistory = build(ruleDto, actorId,receiver,event.getSpace().getId());

                // Save actionHistory entry
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }
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
}

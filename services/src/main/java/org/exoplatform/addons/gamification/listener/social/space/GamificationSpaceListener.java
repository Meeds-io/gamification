package org.exoplatform.addons.gamification.listener.social.space;

import static org.exoplatform.addons.gamification.GamificationConstant.*;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
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

@Asynchronous
public class GamificationSpaceListener extends SpaceListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationSpaceListener.class);
    protected RuleService ruleService;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationSpaceListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void spaceCreated(SpaceLifeCycleEvent event) {

        // Get the space's creator username
        String actorUsername = event.getSource();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        gamificationService.createHistory(GAMIFICATION_SOCIAL_SPACE_ADD, actorId, actorId,"/portal/g/:spaces:"+event.getSpace().getPrettyName().toString()+event.getSpace().getGroupId().replace("/spaces","").toString());

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

        // Get the space's creator username
        String actorUsername = event.getSource();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        gamificationService.createHistory(GAMIFICATION_SOCIAL_SPACE_JOIN, actorId, actorId,"/portal/g/:spaces:"+event.getSpace().getPrettyName().toString()+event.getSpace().getGroupId().replace("/spaces","").toString());

    }

    @Override
    public void left(SpaceLifeCycleEvent event) {
    }

    @Override
    public void grantedLead(SpaceLifeCycleEvent event) {

        // Get the created space
        //Space space = event.getSpace();
        // Get the space's creator username
        String actorUsername = event.getSource();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        gamificationService.createHistory(GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD, actorId,actorId,"/portal/g/:spaces:"+event.getSpace().getPrettyName().toString()+event.getSpace().getGroupId().replace("/spaces","").toString());
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

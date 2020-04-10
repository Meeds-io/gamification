package org.exoplatform.addons.gamification.listener.social.relationship;

import static org.exoplatform.addons.gamification.GamificationConstant.GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER;
import static org.exoplatform.addons.gamification.GamificationConstant.GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.RelationshipListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;

public class GamificationRelationshipListener extends RelationshipListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationRelationshipListener.class);

    protected RuleService ruleService;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationRelationshipListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void confirmed(RelationshipEvent event) {

        GamificationActionsHistory aHistory = null;

        // Get the request sender
        Identity sender = event.getPayload().getSender();
        // Get the request receiver
        Identity receiver = event.getPayload().getReceiver();

        gamificationService.createHistory(GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER,receiver.getId(),sender.getId(),"/portal/intranet/profile/"+receiver.getRemoteId());

        //  Reward user who receive a relationship request
        gamificationService.createHistory(GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER,sender.getId(),receiver.getId(),"/portal/intranet/profile/"+sender.getRemoteId());

    }

    @Override
    public void ignored(RelationshipEvent event) {

    }

    @Override
    public void removed(RelationshipEvent event) {

    }

    @Override
    public void requested(RelationshipEvent event) {

    }

    @Override
    public void denied(RelationshipEvent event) {

    }
}

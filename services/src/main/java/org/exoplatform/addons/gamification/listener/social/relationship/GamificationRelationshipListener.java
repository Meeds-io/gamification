package org.exoplatform.addons.gamification.listener.social.relationship;

import static org.exoplatform.addons.gamification.GamificationConstant.*;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.RelationshipListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.time.LocalDate;

@Asynchronous
public class GamificationRelationshipListener extends RelationshipListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationRelationshipListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationRelationshipListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationProcessor = CommonsUtils.getService(GamificationProcessor.class);
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
        // To hold GamificationRule
        RuleDTO ruleDto = null;
        // Get associated rule : Reward user who send a relationship request
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {

                aHistory = gamificationService.build(ruleDto,receiver.getId(),sender.getId(),"/portal/intranet/profile/"+receiver.getRemoteId());
                //Save actionHistory entry
                if(aHistory!=null) {
                    gamificationProcessor.execute(aHistory);
                    // Gamification simple audit logger
                    LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(), aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
                }
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }



        // Get associated rule : Reward user who receive a relationship request
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {

                aHistory = gamificationService.build(ruleDto,sender.getId(),receiver.getId(),"/portal/intranet/profile/"+sender.getRemoteId());

                //Save actionHistory entry
                if(aHistory!=null) {
                    gamificationProcessor.execute(aHistory);
                    // Gamification simple audit logger
                    LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(), aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
                }
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

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

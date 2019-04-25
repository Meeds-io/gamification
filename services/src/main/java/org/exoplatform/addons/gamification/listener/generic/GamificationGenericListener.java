package org.exoplatform.addons.gamification.listener.generic;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import java.time.LocalDate;
import java.util.Map;

public class GamificationGenericListener extends Listener<Map<String,String>,String> implements GamificationListener  {


    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected GamificationService gamificationService;

    public GamificationGenericListener(RuleService ruleService,
                                           GamificationProcessor gamificationProcessor,
                                           IdentityManager identityManager,
                                           GamificationService gamificationService) {
        this.ruleService = ruleService;
        this.gamificationProcessor = gamificationProcessor;
        this.identityManager = identityManager;
        this.gamificationService = gamificationService;
    }

    @Override
    public void onEvent(Event<Map<String, String>, String> event) throws Exception {
        GamificationActionsHistory aHistory = null;

        RuleDTO ruleDto = null;

        String ruleTitle=(String) event.getSource().get("ruleTitle");
        String senderId=(String) event.getSource().get("senderId");
        String receiverId=(String) event.getSource().get("receiverId");
        String obj=(String) event.getSource().get("object");

        ruleDto = ruleService.findEnableRuleByTitle(ruleTitle);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                String sender= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, senderId, false).getId();
                String receiver=identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, receiverId, false).getId();
                if(senderId != receiverId){
                    aHistory = build(ruleDto,receiver,receiver,obj);}
                else {


                    aHistory = build(ruleDto,sender,receiver,obj);}
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

package org.exoplatform.addons.gamification.listener.generic;

import java.util.Map;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;


public class GamificationGenericListener extends Listener<Map<String,String>,String>   {

        private static final Log LOG = ExoLogger.getLogger(GamificationGenericListener.class);
        protected RuleService ruleService;
        protected IdentityManager identityManager;
        protected GamificationService gamificationService;

        public GamificationGenericListener(RuleService ruleService,
                                           IdentityManager identityManager,
                                           GamificationService gamificationService) {
            this.ruleService = ruleService;
            this.identityManager = identityManager;
            this.gamificationService = gamificationService;
        }

        @Override
        public void onEvent(Event<Map<String, String>, String> event) throws Exception {
            String ruleTitle=(String) event.getSource().get("ruleTitle");
            String senderId=(String) event.getSource().get("senderId");
            String receiverId=(String) event.getSource().get("receiverId");
            String obj=(String) event.getSource().get("object");
            String sender = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, senderId, false).getId();
            String receiver = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, receiverId, false).getId();
            gamificationService.createHistory(ruleTitle,sender,receiver,obj);

        }
    }
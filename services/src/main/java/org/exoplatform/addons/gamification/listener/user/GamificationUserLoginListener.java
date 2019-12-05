package org.exoplatform.addons.gamification.listener.user;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;


import static org.exoplatform.addons.gamification.GamificationConstant.GAMIFICATION_ATTENDANCE_USER_LOGIN;

@Asynchronous
public class GamificationUserLoginListener extends Listener<ConversationRegistry, ConversationState> {
    private static final Log LOG = ExoLogger.getLogger(GamificationUserLoginListener.class);

    protected RuleService ruleService;
    protected IdentityManager identityManager;
    protected GamificationService gamificationService;

    public GamificationUserLoginListener(RuleService ruleService,
                                         IdentityManager identityManager,
                                         GamificationService gamificationService) {
        this.ruleService = ruleService;
        this.identityManager = identityManager;
        this.gamificationService = gamificationService;
    }
    @Override
    public void onEvent(Event<ConversationRegistry, ConversationState> event) {
        String username = event.getData().getIdentity().getUserId();
        String sender = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username, false).getId();
        gamificationService.createHistory(GAMIFICATION_ATTENDANCE_USER_LOGIN, sender, sender, null);
        LOG.debug("User Login Gamification for {}", username);
    }
}
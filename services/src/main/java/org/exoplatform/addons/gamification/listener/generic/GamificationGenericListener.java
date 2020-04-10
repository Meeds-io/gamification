package org.exoplatform.addons.gamification.listener.generic;

import java.util.Map;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.*;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

@Asynchronous
public class GamificationGenericListener extends Listener<Map<String, String>, String> {

  public static final String    EVENT_NAME = "exo.gamification.generic.action";

  protected PortalContainer     container;

  protected RuleService         ruleService;

  protected IdentityManager     identityManager;

  protected GamificationService gamificationService;

  public GamificationGenericListener(PortalContainer container,
                                     RuleService ruleService,
                                     IdentityManager identityManager,
                                     GamificationService gamificationService) {
    this.container = container;
    this.ruleService = ruleService;
    this.identityManager = identityManager;
    this.gamificationService = gamificationService;
  }

  @Override
  public void onEvent(Event<Map<String, String>, String> event) throws Exception {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    try {
      String ruleTitle = event.getSource().get("ruleTitle");
      String senderId = event.getSource().get("senderId");
      String receiverId = event.getSource().get("receiverId");
      String obj = event.getSource().get("object");
      String sender = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, senderId).getId();
      String receiver = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, receiverId).getId();
      gamificationService.createHistory(ruleTitle, sender, receiver, obj);
    } finally {
      RequestLifeCycle.end();
    }
  }
}

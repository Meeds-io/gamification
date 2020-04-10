package org.exoplatform.addons.gamification.listener.generic;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.*;
import org.exoplatform.social.core.identity.model.Identity;
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
  @SuppressWarnings("deprecation")
  public void onEvent(Event<Map<String, String>, String> event) throws Exception {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    try {
      String ruleTitle = event.getSource().get("ruleTitle");
      String senderId = event.getSource().get("senderId");
      String senderType = event.getSource().get("senderType");
      String receiverId = event.getSource().get("receiverId");
      String obj = event.getSource().get("object");

      Identity senderIdentity = null;
      if (senderType != null) {
        String providerId = IdentityType.getType(senderType).getProviderId();
        senderIdentity = identityManager.getOrCreateIdentity(providerId, senderId);
      }
      if (senderIdentity == null && NumberUtils.isDigits(senderId)) {
        senderIdentity = identityManager.getIdentity(senderId); // NOSONAR
      }
      if (senderIdentity == null) {
        senderIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, senderId); // NOSONAR
      }
      if (senderIdentity == null) {
        throw new IllegalStateException("Can't find identity with senderId = " + senderId);
      }

      Identity receiverIdentity = null;
      if (NumberUtils.isDigits(receiverId)) {
        receiverIdentity = identityManager.getIdentity(receiverId); // NOSONAR
      }
      if (receiverIdentity == null) {
        receiverIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, receiverId); // NOSONAR
      }
      if (receiverIdentity == null) {
        throw new IllegalStateException("Can't find identity with receiverId = " + receiverId);
      }
      gamificationService.createHistory(ruleTitle, senderIdentity.getId(), receiverIdentity.getId(), obj);
    } finally {
      RequestLifeCycle.end();
    }
  }
}

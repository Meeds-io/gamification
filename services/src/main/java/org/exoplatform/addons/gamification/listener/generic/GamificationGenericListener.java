/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
import org.exoplatform.social.core.manager.IdentityManager;

@Asynchronous
public class GamificationGenericListener extends Listener<Map<String, String>, String> {

  public static final String    GENERIC_EVENT_NAME = "exo.gamification.generic.action";

  public static final String    CANCEL_EVENT_NAME  = "gamification.cancel.event.action";

  public static final String    DELETE_EVENT_NAME  = "gamification.delete.event.action";

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
      String receiverType = event.getSource().get("receiverType");
      String objectId = event.getSource().get("objectId");
      String objectType = event.getSource().get("objectType");

      if (event.getEventName().equals(DELETE_EVENT_NAME)) {
        gamificationService.deleteHistory(objectId, objectType);
        return;
      }
      Identity senderIdentity = getIdentity(senderType, senderId);
      Identity receiverIdentity = getIdentity(receiverType, receiverId);
      switch (event.getEventName()) {
      case GENERIC_EVENT_NAME -> gamificationService.createHistory(ruleTitle,
                                                                   senderIdentity.getId(),
                                                                   receiverIdentity.getId(),
                                                                   objectId,
                                                                   objectType);

      case CANCEL_EVENT_NAME -> gamificationService.cancelHistory(ruleTitle,
                                                                  senderIdentity.getId(),
                                                                  receiverIdentity.getId(),
                                                                  objectId,
                                                                  objectType);

      default -> throw new IllegalArgumentException("Unexpected listener event name: " + event.getEventName());
      }
    } finally {
      RequestLifeCycle.end();
    }
  }

  private Identity getIdentity(String identityType, String identityId) {
    String providerId = IdentityType.getType(identityType).getProviderId();
    Identity identity = identityManager.getOrCreateIdentity(providerId, identityId);
    if (identity == null && NumberUtils.isDigits(identityId)) {
      identity = identityManager.getIdentity(identityId); // NOSONAR
    }
    if (identity == null) {
      throw new IllegalStateException("Can't find identity with identityId = " + identityId);
    }
    return identity;
  }
}

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
package io.meeds.gamification.listener;

import static io.meeds.gamification.constant.GamificationConstant.EVENT_NAME;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_ID_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_TYPE_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.RECEIVER_ID;
import static io.meeds.gamification.constant.GamificationConstant.RECEIVER_TYPE;
import static io.meeds.gamification.constant.GamificationConstant.RULE_TITLE;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_ID;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_TYPE;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

@Asynchronous
public class GamificationGenericListener extends Listener<Map<String, String>, String> {

  private static final Log     LOG                = ExoLogger.getLogger(GamificationGenericListener.class);

  public static final String   GENERIC_EVENT_NAME = "exo.gamification.generic.action";

  public static final String   CANCEL_EVENT_NAME  = "gamification.cancel.event.action";

  public static final String   DELETE_EVENT_NAME  = "gamification.delete.event.action";

  protected PortalContainer    container;

  protected RuleService        ruleService;

  protected IdentityManager    identityManager;

  protected RealizationService realizationService;

  public GamificationGenericListener(PortalContainer container,
                                     RuleService ruleService,
                                     IdentityManager identityManager,
                                     RealizationService realizationService) {
    this.container = container;
    this.ruleService = ruleService;
    this.identityManager = identityManager;
    this.realizationService = realizationService;
  }

  @Override
  @SuppressWarnings("removal")
  public void onEvent(Event<Map<String, String>, String> event) throws Exception {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    try {
      String gamificationEventId = event.getSource().get(EVENT_NAME);
      if (StringUtils.isBlank(gamificationEventId)) {
        gamificationEventId = event.getSource().get(RULE_TITLE);
      }
      String senderId = event.getSource().get(SENDER_ID);
      String senderType = event.getSource().get(SENDER_TYPE);
      String receiverId = event.getSource().get(RECEIVER_ID);
      String receiverType = event.getSource().get(RECEIVER_TYPE);
      String objectId = event.getSource().get(OBJECT_ID_PARAM);
      String objectType = event.getSource().get(OBJECT_TYPE_PARAM);

      Identity senderIdentity = getIdentity(senderType, senderId);
      Identity receiverIdentity = getIdentity(receiverType, receiverId);

      switch (event.getEventName()) {
      case GENERIC_EVENT_NAME -> realizationService.createRealizations(gamificationEventId,
                                                                            senderIdentity != null ? senderIdentity.getId()
                                                                                                   : null,
                                                                            receiverIdentity != null ? receiverIdentity.getId()
                                                                                                     : null,
                                                                            objectId,
                                                                            objectType);
      case DELETE_EVENT_NAME -> realizationService.deleteRealizations(objectId, objectType);
      case CANCEL_EVENT_NAME -> realizationService.cancelRealizations(gamificationEventId,
                                                                      senderIdentity != null ? senderIdentity.getId() : null,
                                                                      receiverIdentity != null ? receiverIdentity.getId() : null,
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
      LOG.info("Can't find identity with identityId = {}", identityId);
      return null;
    }
    return identity;
  }

}

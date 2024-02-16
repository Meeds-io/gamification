/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
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
 *
 */
package io.meeds.gamification.listener;

import io.meeds.gamification.utils.Utils;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;

import java.util.HashMap;
import java.util.Map;

import static io.meeds.gamification.constant.GamificationConstant.*;
import static io.meeds.gamification.constant.GamificationConstant.BROADCAST_GAMIFICATION_EVENT_ERROR;
import static io.meeds.gamification.listener.GamificationGenericListener.*;

public class GamificationRuleListener extends Listener<Object, String> {

  private static final Log      LOG = ExoLogger.getLogger(GamificationRuleListener.class);

  private final ListenerService listenerService;

  private final RuleService     ruleService;

  private final IdentityManager identityManager;

  public GamificationRuleListener(ListenerService listenerService, RuleService ruleService, IdentityManager identityManager) {
    this.listenerService = listenerService;
    this.ruleService = ruleService;
    this.identityManager = identityManager;
  }

  @Override
  public void onEvent(Event<Object, String> event) {
    Object object = event.getSource();
    boolean ruleDeleted = Utils.POST_DELETE_RULE_EVENT.equals(event.getEventName());
    RuleDTO rule = ruleDeleted ? (RuleDTO) object : ruleService.findRuleById((Long) object);
    String createdBy = rule.getCreatedBy();
    String userIdentityId = identityManager.getOrCreateUserIdentity(createdBy).getId();
    createRuleGamificationHistoryEntry(userIdentityId,
                                       userIdentityId,
                                       String.valueOf(rule.getId()),
                                       ruleDeleted ? DELETE_EVENT_NAME : GENERIC_EVENT_NAME);
  }

  private void createRuleGamificationHistoryEntry(String earnerIdentityId,
                                                  String receiverId,
                                                  String ruleId,
                                                  String gamificationEventName) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(EVENT_NAME, GAMIFICATION_CONTRIBUTIONS_CREATE_RULE);
      gam.put(OBJECT_ID_PARAM, ruleId);
      gam.put(OBJECT_TYPE_PARAM, ACTIVITY_OBJECT_TYPE);
      gam.put(SENDER_ID, earnerIdentityId);
      gam.put(RECEIVER_ID, receiverId);
      listenerService.broadcast(gamificationEventName, gam, null);
    } catch (Exception e) {
      LOG.warn(BROADCAST_GAMIFICATION_EVENT_ERROR, gam, e);
    }
  }
}

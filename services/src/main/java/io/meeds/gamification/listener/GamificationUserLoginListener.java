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

import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_ATTENDANCE_USER_LOGIN;

import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

@Asynchronous
public class GamificationUserLoginListener extends Listener<ConversationRegistry, ConversationState> {
  private static final Log     LOG = ExoLogger.getLogger(GamificationUserLoginListener.class);

  protected RuleService        ruleService;

  protected IdentityManager    identityManager;

  protected RealizationService realizationService;

  public GamificationUserLoginListener(RuleService ruleService,
                                       IdentityManager identityManager,
                                       RealizationService realizationService) {
    this.ruleService = ruleService;
    this.identityManager = identityManager;
    this.realizationService = realizationService;
  }

  @Override
  public void onEvent(Event<ConversationRegistry, ConversationState> event) {
    String username = event.getData().getIdentity().getUserId();
    String sender = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username).getId();
    realizationService.createRealizationsAsync(GAMIFICATION_ATTENDANCE_USER_LOGIN, null, sender, sender, null, null);
    LOG.debug("User Login Gamification for {}", username);
  }

}

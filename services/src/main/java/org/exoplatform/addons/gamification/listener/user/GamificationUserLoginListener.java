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
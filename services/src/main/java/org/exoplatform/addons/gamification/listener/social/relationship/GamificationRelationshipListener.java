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
package org.exoplatform.addons.gamification.listener.social.relationship;

import static org.exoplatform.addons.gamification.GamificationConstant.GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER;
import static org.exoplatform.addons.gamification.GamificationConstant.GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.RelationshipListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;

public class GamificationRelationshipListener extends RelationshipListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationRelationshipListener.class);

    protected RuleService ruleService;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationRelationshipListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void confirmed(RelationshipEvent event) {

        GamificationActionsHistory aHistory = null;

        // Get the request sender
        Identity sender = event.getPayload().getSender();
        // Get the request receiver
        Identity receiver = event.getPayload().getReceiver();

        gamificationService.createHistory(GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER,receiver.getId(),sender.getId(),"/portal/intranet/profile/"+receiver.getRemoteId());

        //  Reward user who receive a relationship request
        gamificationService.createHistory(GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER,sender.getId(),receiver.getId(),"/portal/intranet/profile/"+sender.getRemoteId());

    }

    @Override
    public void ignored(RelationshipEvent event) {

    }

    @Override
    public void removed(RelationshipEvent event) {

    }

    @Override
    public void requested(RelationshipEvent event) {

    }

    @Override
    public void denied(RelationshipEvent event) {

    }
}

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.listener;

import static io.meeds.gamification.constant.GamificationConstant.*;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.RelationshipListenerPlugin;

import io.meeds.gamification.service.RealizationService;

public class GamificationRelationshipListener extends RelationshipListenerPlugin {

  protected RealizationService realizationService;

  public GamificationRelationshipListener(RealizationService realizationService) {
    this.realizationService = realizationService;
  }

  @Override
  public void confirmed(RelationshipEvent event) {
    // Get the request sender
    Identity sender = event.getPayload().getSender();
    // Get the request receiver
    Identity receiver = event.getPayload().getReceiver();

    // Reward user who sent a relationship request
    realizationService.createRealizationsAsync(GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER,
                                               sender.getId(),
                                               receiver.getId(),
                                               receiver.getId(),
                                               IDENTITY_OBJECT_TYPE);
    // Reward user who receive a relationship request
    realizationService.createRealizationsAsync(GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER,
                                               receiver.getId(),
                                               sender.getId(),
                                               sender.getId(),
                                               IDENTITY_OBJECT_TYPE);

  }
}

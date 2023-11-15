/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER;
import static io.meeds.gamification.constant.GamificationConstant.IDENTITY_OBJECT_TYPE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.RelationshipEvent.Type;
import org.exoplatform.social.core.relationship.model.Relationship;

import io.meeds.gamification.service.RealizationService;

@RunWith(MockitoJUnitRunner.class)
public class GamificationRelationshipListenerTest {

  @Mock
  protected RealizationService realizationService;

  @Test
  public void testRelationshipConfirmed() {
    GamificationRelationshipListener gamificationRelationshipListener = new GamificationRelationshipListener(realizationService);
    Identity sender = mock(Identity.class);
    when(sender.getId()).thenReturn("1");
    Identity receiver = mock(Identity.class);
    when(receiver.getId()).thenReturn("2");

    Relationship relationship = new Relationship(sender, receiver);

    RelationshipEvent event = new RelationshipEvent(Type.CONFIRM, null, relationship);
    gamificationRelationshipListener.confirmed(event);

    verify(realizationService, times(1)).createRealizationsAsync(GAMIFICATION_SOCIAL_RELATIONSHIP_SENDER,
                                                                 sender.getId(),
                                                                 receiver.getId(),
                                                                 receiver.getId(),
                                                                 IDENTITY_OBJECT_TYPE);
    verify(realizationService, times(1)).createRealizationsAsync(GAMIFICATION_SOCIAL_RELATIONSHIP_RECEIVER,
                                                                 receiver.getId(),
                                                                 sender.getId(),
                                                                 sender.getId(),
                                                                 IDENTITY_OBJECT_TYPE);
  }
}

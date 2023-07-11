/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2023 Meeds Association
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
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_ADD;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_INVITE_USER;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_JOIN;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_APPLICATIONS;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_AVATAR;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_BANNER;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_UPDATE_DESCRIPTION;
import static io.meeds.gamification.listener.GamificationGenericListener.CANCEL_EVENT_NAME;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.service.RuleService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GamificationSpaceListenerTest {

  @Mock
  private RuleService     ruleService;

  @Mock
  private IdentityManager identityManager;

  @Mock
  private SpaceService    spaceService;

  @Mock
  private ListenerService listenerService;

  @Test
  public void testSpaceJoin() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_JOIN, (listener, event) -> listener.joined(event));
  }

  @Test
  public void testSpaceLeave() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_JOIN, (listener, event) -> listener.left(event), true);
  }

  @Test
  public void testAddAppLicationSpace() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_UPDATE_APPLICATIONS, (listener, event) -> listener.applicationAdded(event));
  }

  @Test
  public void testRemoveAppLicationSpace() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_UPDATE_APPLICATIONS, (listener, event) -> listener.applicationRemoved(event));
  }

  @Test
  public void testSpaceBannerEdited() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_UPDATE_BANNER, (listener, event) -> listener.spaceBannerEdited(event));
  }

  @Test
  public void testSpaceAvatarEdited() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_UPDATE_AVATAR, (listener, event) -> listener.spaceAvatarEdited(event));
  }

  @Test
  public void testCreateSpace() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_ADD, (listener, event) -> listener.spaceCreated(event));
  }

  @Test
  public void testUpdateSpaceDescription() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_UPDATE_DESCRIPTION, (listener, event) -> listener.spaceDescriptionEdited(event));
  }

  @Test
  public void testBecomeSpaceManager() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_GRANT_AS_LEAD, (listener, event) -> listener.grantedLead(event));
  }

  @Test
  public void testAddInvitedUser() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_INVITE_USER, (listener, event) -> listener.addInvitedUser(event));
  }

  @Test
  public void testRemoveInvitedUser() throws Exception {
    testEventTrigger(GAMIFICATION_SOCIAL_SPACE_INVITE_USER, (listener, event) -> listener.removeInvitedUser(event), true);
  }

  private void testEventTrigger(String expectedGamifiedEvent,
                                BiConsumer<GamificationSpaceListener, SpaceLifeCycleEvent> consumer) throws Exception {
    testEventTrigger(expectedGamifiedEvent, consumer, false);
  }

  private void testEventTrigger(String expectedGamifiedEvent,
                                BiConsumer<GamificationSpaceListener, SpaceLifeCycleEvent> consumer,
                                boolean canceled) throws Exception {
    GamificationSpaceListener gamificationSpaceListener = new GamificationSpaceListener(ruleService,
                                                                                        identityManager,
                                                                                        spaceService,
                                                                                        listenerService);

    Identity userIdentity = mock(Identity.class);
    when(userIdentity.getId()).thenReturn("1");
    when(identityManager.getOrCreateUserIdentity("root")).thenReturn(userIdentity);

    Space space = new Space();
    space.setDisplayName("space1");
    space.setPrettyName(space.getDisplayName());

    Identity spaceIdentity = mock(Identity.class);
    when(spaceIdentity.getId()).thenReturn("2");
    when(identityManager.getOrCreateSpaceIdentity("space1")).thenReturn(spaceIdentity);

    SpaceLifeCycleEvent spaceLifeCycleEvent = new SpaceLifeCycleEvent(space, "root", null);
    consumer.accept(gamificationSpaceListener, spaceLifeCycleEvent);

    verify(listenerService,
           times(2)).broadcast(eq(canceled ? CANCEL_EVENT_NAME : GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(EVENT_NAME).equals(expectedGamifiedEvent)),
                               eq(null));
  }

}

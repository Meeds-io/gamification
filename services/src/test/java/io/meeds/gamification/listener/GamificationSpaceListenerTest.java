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
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_SPACE_JOIN;
import static io.meeds.gamification.listener.GamificationGenericListener.CANCEL_EVENT_NAME;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Objects;

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
import io.meeds.gamification.test.AbstractServiceTest;

@RunWith(MockitoJUnitRunner.class)
public class GamificationSpaceListenerTest extends AbstractServiceTest {

  @Mock
  private RuleService     ruleService;

  @Mock
  private IdentityManager identityManager;

  @Mock
  private SpaceService    spaceService;

  @Mock
  private ListenerService listenerService;

  @Test
  public void testDeleteLikeActivity() throws Exception {
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

    SpaceLifeCycleEvent spaceLifeCycleEvent = new SpaceLifeCycleEvent(space, "root", SpaceLifeCycleEvent.Type.LEFT);
    gamificationSpaceListener.left(spaceLifeCycleEvent);

    verify(listenerService,
           times(2)).broadcast(argThat((String name) -> name.equals(CANCEL_EVENT_NAME)),
                               argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                             .equals(GAMIFICATION_SOCIAL_SPACE_JOIN)),
                               argThat(Objects::isNull));

  }

}

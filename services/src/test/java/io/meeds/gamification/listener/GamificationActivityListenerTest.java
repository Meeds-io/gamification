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
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PIN_ACTIVITY_SPACE;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT;
import static io.meeds.gamification.listener.GamificationActivityListener.EXCLUDE_COMMENT_TYPES_PARAM;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.service.RuleService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GamificationActivityListenerTest {

  @Mock
  private RuleService     ruleService;

  @Mock
  private ActivityManager activityManager;

  @Mock
  private IdentityManager identityManager;

  @Mock
  private SpaceService    spaceService;

  @Mock
  private ListenerService listenerService;

  @Test
  public void testActivityPin() throws Exception {
    GamificationActivityListener gamificationActivityListener = new GamificationActivityListener(ruleService,
                                                                                                 identityManager,
                                                                                                 activityManager,
                                                                                                 spaceService,
                                                                                                 listenerService,
                                                                                                 null);

    ExoSocialActivity activity = mock(ExoSocialActivity.class);
    when(activity.getId()).thenReturn("5");

    ActivityLifeCycleEvent event = new ActivityLifeCycleEvent(null, activity, "1");
    gamificationActivityListener.pinActivity(event);

    verify(listenerService).broadcast(eq(GENERIC_EVENT_NAME),
                                      argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                                    .equals(GAMIFICATION_SOCIAL_PIN_ACTIVITY_SPACE)),
                                      eq(null));

  }

  @Test
  public void testActivityComment() throws Exception {
    GamificationActivityListener gamificationActivityListener = new GamificationActivityListener(ruleService,
                                                                                                 identityManager,
                                                                                                 activityManager,
                                                                                                 spaceService,
                                                                                                 listenerService,
                                                                                                 null);

    ExoSocialActivity activity = mock(ExoSocialActivity.class);
    ExoSocialActivity parent = mock(ExoSocialActivity.class);
    when(activity.getId()).thenReturn("5");
    when(activityManager.getParentActivity(activity)).thenReturn(parent);

    ActivityLifeCycleEvent event = new ActivityLifeCycleEvent(null, activity, "1");
    gamificationActivityListener.saveComment(event);

    verify(listenerService).broadcast(eq(GENERIC_EVENT_NAME),
                                      argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                                    .equals(GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT)),
                                      eq(null));
    verify(listenerService).broadcast(eq(GENERIC_EVENT_NAME),
                                      argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                                    .equals(GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT)),
                                      eq(null));

  }

  @Test
  public void testActivityCommentWithExcludedType() throws Exception {
    InitParams params = mock(InitParams.class);
    ValuesParam values = mock(ValuesParam.class);
    when(params.containsKey(EXCLUDE_COMMENT_TYPES_PARAM)).thenReturn(true);
    when(params.getValuesParam(EXCLUDE_COMMENT_TYPES_PARAM)).thenReturn(values);
    String excludedCommentType = "testCommentType";
    when(values.getValues()).thenReturn(Collections.singletonList(excludedCommentType));

    GamificationActivityListener gamificationActivityListener = new GamificationActivityListener(ruleService,
                                                                                                 identityManager,
                                                                                                 activityManager,
                                                                                                 spaceService,
                                                                                                 listenerService,
                                                                                                 params);

    ExoSocialActivity activity = mock(ExoSocialActivity.class);
    ExoSocialActivity parent = mock(ExoSocialActivity.class);
    when(activity.getId()).thenReturn("5");
    when(activity.getType()).thenReturn(excludedCommentType);
    when(activityManager.getParentActivity(activity)).thenReturn(parent);

    ActivityLifeCycleEvent event = new ActivityLifeCycleEvent(null, activity, "1");
    gamificationActivityListener.saveComment(event);

    verify(listenerService,
           never()).broadcast(eq(GENERIC_EVENT_NAME),
                              argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                            .equals(GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT)),
                              eq(null));
    verify(listenerService,
           never()).broadcast(eq(GENERIC_EVENT_NAME),
                              argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                            .equals(GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT)),
                              eq(null));

  }

}

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
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_CONTACT_INFORMATION;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_WORK_EXPERIENCE;
import static io.meeds.gamification.constant.GamificationConstant.IDENTITY_OBJECT_TYPE;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_ID_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.OBJECT_TYPE_PARAM;
import static io.meeds.gamification.constant.GamificationConstant.RECEIVER_ID;
import static io.meeds.gamification.constant.GamificationConstant.SENDER_ID;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;

import io.meeds.gamification.model.Announcement;

@RunWith(MockitoJUnitRunner.class)
public class GamificationProfileListenerTest {

  private static final String USERNAME       = "test";

  private static final String OTHER_USERNAME = "test2";

  @Mock
  private ListenerService     listenerService;

  @Mock
  private Identity            identity;

  @Mock
  private Profile             profile;

  @Before
  public void setup() {
    when(identity.getId()).thenReturn("1");
    when(identity.getRemoteId()).thenReturn(USERNAME);
    when(profile.getIdentity()).thenReturn(identity);
  }

  @Test
  public void testUpdateContactSectionUpdated() throws Exception {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(listenerService);
    Announcement announcement = new Announcement();
    announcement.setActivityId(1L);

    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED,
                                                            USERNAME,
                                                            profile,
                                                            OTHER_USERNAME);
    gamificationProfileListener.contactSectionUpdated(event);

    verifyNoInteractions(listenerService);
    event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED, USERNAME, profile, USERNAME);
    gamificationProfileListener.contactSectionUpdated(event);

    verify(listenerService,
           times(1)).broadcast(eq(GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                             .equals(GAMIFICATION_SOCIAL_PROFILE_ADD_CONTACT_INFORMATION)
                                   && source.get(SENDER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(RECEIVER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(OBJECT_TYPE_PARAM)
                                            .equals(IDENTITY_OBJECT_TYPE)
                                   && source.get(OBJECT_ID_PARAM)
                                            .equals(identity.getId())),
                               eq(null));
  }

  @Test
  public void testUpdateUserAvatar() throws Exception {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(listenerService);
    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED,
                                                            USERNAME,
                                                            profile,
                                                            OTHER_USERNAME);
    gamificationProfileListener.avatarUpdated(event);

    verifyNoInteractions(listenerService);
    event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED, USERNAME, profile, USERNAME);
    gamificationProfileListener.avatarUpdated(event);

    verify(listenerService,
           times(1)).broadcast(eq(GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                             .equals(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR)
                                   && source.get(SENDER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(RECEIVER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(OBJECT_TYPE_PARAM)
                                            .equals(IDENTITY_OBJECT_TYPE)
                                   && source.get(OBJECT_ID_PARAM)
                                            .equals(identity.getId())),
                               eq(null));
  }

  @Test
  public void testUpdateUserBanner() throws Exception {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(listenerService);

    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED,
                                                            USERNAME,
                                                            profile,
                                                            OTHER_USERNAME);
    gamificationProfileListener.bannerUpdated(event);

    verifyNoInteractions(listenerService);
    event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED, USERNAME, profile, USERNAME);
    gamificationProfileListener.bannerUpdated(event);

    verify(listenerService,
           times(1)).broadcast(eq(GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                             .equals(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER)
                                   && source.get(SENDER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(RECEIVER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(OBJECT_TYPE_PARAM)
                                            .equals(IDENTITY_OBJECT_TYPE)
                                   && source.get(OBJECT_ID_PARAM)
                                            .equals(identity.getId())),
                               eq(null));
  }

  @Test
  public void testUpdateWorkExperience() throws Exception {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(listenerService);

    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED,
                                                            USERNAME,
                                                            profile,
                                                            OTHER_USERNAME);
    gamificationProfileListener.experienceSectionUpdated(event);

    verifyNoInteractions(listenerService);
    event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED, USERNAME, profile, USERNAME);
    gamificationProfileListener.experienceSectionUpdated(event);

    verify(listenerService,
           times(1)).broadcast(eq(GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(EVENT_NAME)
                                                                             .equals(GAMIFICATION_SOCIAL_PROFILE_ADD_WORK_EXPERIENCE)
                                   && source.get(SENDER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(RECEIVER_ID)
                                            .equals(String.valueOf(identity.getId()))
                                   && source.get(OBJECT_TYPE_PARAM)
                                            .equals(IDENTITY_OBJECT_TYPE)
                                   && source.get(OBJECT_ID_PARAM)
                                            .equals(identity.getId())),
                               eq(null));
  }

}

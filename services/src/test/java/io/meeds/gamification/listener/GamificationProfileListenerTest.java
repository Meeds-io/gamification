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

import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER;
import static io.meeds.gamification.constant.GamificationConstant.IDENTITY_OBJECT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.storage.cache.CachedActivityStorage;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

@RunWith(MockitoJUnitRunner.class)
public class GamificationProfileListenerTest {

  @Mock
  private AnnouncementService   announcementService;

  @Mock
  private CachedActivityStorage activityStorage;

  @Mock
  private RealizationService    realizationService;

  @Mock
  private RuleService           ruleService;

  @Mock
  private SpaceService          spaceService;

  @Mock
  private IdentityManager       identityManager;

  @Mock
  private Identity              identity;

  @Mock
  private Profile               profile;

  @Before
  public void setup() {
    when(identityManager.getOrCreateUserIdentity("root1")).thenReturn(identity);
    when(identity.getId()).thenReturn("1");
    when(identity.getProfile()).thenReturn(profile);
    when(profile.getIdentity()).thenReturn(identity);
  }

  @Test
  public void testUpdateContactSectionUpdated() {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(ruleService,
                                                                                              identityManager,
                                                                                              spaceService,
                                                                                              realizationService,
                                                                                              announcementService,
                                                                                              activityStorage);
    Announcement announcement = new Announcement();
    announcement.setActivityId(1L);
    Identity rootIdentity = identityManager.getOrCreateUserIdentity("root1");
    Profile profile = rootIdentity.getProfile();
    when(announcementService.findAnnouncements(rootIdentity.getId())).thenReturn(Collections.singletonList(announcement));

    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED, "roo1", profile);
    gamificationProfileListener.contactSectionUpdated(event);
    verify(activityStorage, times(1)).clearActivityCached(argThat(new ArgumentMatcher<String>() {
      @Override
      public boolean matches(String activityId) {
        assertEquals("1", activityId);
        return true;
      }
    }));
  }

  @Test
  public void testUpdateUserAvatar() {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(ruleService,
                                                                                              identityManager,
                                                                                              spaceService,
                                                                                              realizationService,
                                                                                              announcementService,
                                                                                              activityStorage);
    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.AVATAR_UPDATED, "roo1", profile);
    gamificationProfileListener.avatarUpdated(event);
    verify(realizationService,
           times(1)).createRealizationsAsync(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR,
                                             identity.getId(),
                                             identity.getId(),
                                             identity.getId(),
                                             IDENTITY_OBJECT_TYPE);

  }

  @Test
  public void testUpdateUserBanner() {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(ruleService,
                                                                                              identityManager,
                                                                                              spaceService,
                                                                                              realizationService,
                                                                                              announcementService,
                                                                                              activityStorage);

    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.BANNER_UPDATED, "roo1", profile);
    gamificationProfileListener.bannerUpdated(event);
    verify(realizationService,
           times(1)).createRealizationsAsync(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER,
                                             identity.getId(),
                                             identity.getId(),
                                             identity.getId(),
                                             IDENTITY_OBJECT_TYPE);

  }

}

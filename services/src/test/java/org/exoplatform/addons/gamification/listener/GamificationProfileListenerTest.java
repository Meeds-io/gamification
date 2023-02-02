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
package org.exoplatform.addons.gamification.listener;

import org.exoplatform.addons.gamification.listener.social.profile.GamificationProfileListener;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.social.core.storage.cache.CachedActivityStorage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GamificationProfileListenerTest extends AbstractServiceTest {

  @Mock
  private AnnouncementService announcementService;

  @Mock
  private ActivityStorage activityStorage;

  @Test
  public void testUpdateContactSectionUpdated() {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(ruleService,
                                                                                              identityManager,
                                                                                              spaceService,
                                                                                              gamificationService,
                                                                                              announcementService,
                                                                                              activityStorage);
    Announcement announcement = new Announcement();
    announcement.setActivityId(1L);
    Identity rootIdentity = identityManager.getOrCreateUserIdentity("root1");
    Profile profile = rootIdentity.getProfile();
    when(announcementService.getAnnouncementsByEarnerId(rootIdentity.getId())).thenReturn(Collections.singletonList(announcement));

    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED, "roo1", profile);
    gamificationProfileListener.contactSectionUpdated(event);
    verify((CachedActivityStorage) activityStorage, times(1)).clearActivityCached(argThat(new ArgumentMatcher<String>() {
      @Override
      public boolean matches(String activityId) {
        assertEquals("1", activityId);
        return true;
      }
    }));
  }
}

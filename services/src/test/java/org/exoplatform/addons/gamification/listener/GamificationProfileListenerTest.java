package org.exoplatform.addons.gamification.listener;

import org.exoplatform.addons.gamification.listener.social.profile.GamificationProfileListener;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
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
  private AnnouncementService   announcementService;

  @Mock
  private CachedActivityStorage cachedActivityStorage;

  @Test
  public void testUpdateContactSectionUpdated() {
    GamificationProfileListener gamificationProfileListener = new GamificationProfileListener(ruleService,
                                                                                              identityManager,
                                                                                              spaceService,
                                                                                              gamificationService,
                                                                                              announcementService,
                                                                                              cachedActivityStorage);
    Announcement announcement = new Announcement();
    announcement.setActivityId(1L);
    Identity rootIdentity = identityManager.getOrCreateUserIdentity("root1");
    Profile profile = rootIdentity.getProfile();
    when(announcementService.getAnnouncementsByEarnerId(rootIdentity.getId())).thenReturn(Collections.singletonList(announcement));

    ProfileLifeCycleEvent event = new ProfileLifeCycleEvent(ProfileLifeCycleEvent.Type.CONTACT_UPDATED, "roo1", profile);
    gamificationProfileListener.contactSectionUpdated(event);
    verify(cachedActivityStorage, times(1)).clearActivityCached(argThat(new ArgumentMatcher<String>() {
      @Override
      public boolean matches(String activityId) {
        assertEquals("1", activityId);
        return true;
      }
    }));
  }
}

/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

import static org.exoplatform.addons.gamification.listener.challenges.AnnouncementActivityGeneratorListener.ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM;
import static org.exoplatform.addons.gamification.listener.challenges.AnnouncementActivityGeneratorListener.ANNOUNCEMENT_ID_TEMPLATE_PARAM;
import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.addons.gamification.listener.challenges.AnnouncementActivityGeneratorListener;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.listener.Event;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

@RunWith(MockitoJUnitRunner.class)
public class AnnouncementActivityGeneratorListenerTest {

  @Mock
  private AnnouncementService announcementService;

  @Mock
  private ExoContainer        container;

  @Mock
  private SpaceService        spaceService;

  @Mock
  private ActivityManager     activityManager;

  @Mock
  private ChallengeService    challengeService;

  @Mock
  IdentityManager             identityManager;

  @Test
  public void testGenerateAnnouncementActivity() throws Exception {
    AnnouncementActivityGeneratorListener activityGeneratorListener = new AnnouncementActivityGeneratorListener(spaceService,
                                                                                                                identityManager,
                                                                                                                activityManager,
                                                                                                                challengeService);
    String spaceId = "5";
    String userIdentityId = "3";
    String activityId = "9";

    Challenge challenge = new Challenge(1234,
                                        "new challenge",
                                        "challenge description",
                                        Long.parseLong(spaceId),
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        2l,
                                        true);

    AnnouncementActivity announcementActivity = new AnnouncementActivity(6,
                                                                         challenge.getId(),
                                                                         challenge.getTitle(),
                                                                         Long.parseLong(userIdentityId),
                                                                         "announcement comment",
                                                                         Long.parseLong(userIdentityId),
                                                                         new Date(System.currentTimeMillis()).toString(),
                                                                         null,
                                                                         null);

    Space space = new Space();
    space.setId(spaceId);
    space.setPrettyName("test_space");
    space.setDisplayName("test space");

    Identity spaceIdentity = new Identity();
    spaceIdentity.setId(userIdentityId);
    spaceIdentity.setProviderId(SpaceIdentityProvider.NAME);
    spaceIdentity.setRemoteId(space.getPrettyName());

    Identity userIdentity = new Identity();
    userIdentity.setId(userIdentityId);
    userIdentity.setProviderId(OrganizationIdentityProvider.NAME);
    userIdentity.setRemoteId("testuser");

    when(spaceService.getSpaceById(spaceId)).thenReturn(space);
    when(identityManager.getOrCreateSpaceIdentity(space.getPrettyName())).thenReturn(spaceIdentity);
    when(identityManager.getIdentity(userIdentity.getId())).thenReturn(userIdentity);
    when(challengeService.getChallengeById(challenge.getId(), userIdentity.getRemoteId())).thenReturn(challenge);
    when(announcementService.updateAnnouncement(any())).thenAnswer(invocation -> invocation.getArgument(0));
    doAnswer(invocation -> {
      ExoSocialActivityImpl activity = invocation.getArgument(1);
      activity.setId(activityId);
      when(activityManager.getActivity(activityId)).thenReturn(activity);
      return null;
    }).when(activityManager).saveActivityNoReturn(any(), any());

    Event<AnnouncementService, AnnouncementActivity> event = new Event<>(Utils.ANNOUNCEMENT_ACTIVITY_EVENT,
                                                                         announcementService,
                                                                         announcementActivity);
    activityGeneratorListener.onEvent(event);
    verify(activityManager, times(1)).saveActivityNoReturn(argThat(new ArgumentMatcher<Identity>() {
      @Override
      public boolean matches(Identity spaceIdentity) {
        return StringUtils.equals(space.getPrettyName(), String.valueOf(spaceIdentity.getRemoteId()));
      }
    }), argThat(new ArgumentMatcher<ExoSocialActivity>() {
      @Override
      public boolean matches(ExoSocialActivity activity) {
        assertEquals(ANNOUNCEMENT_ACTIVITY_TYPE, activity.getType());
        assertEquals(announcementActivity.getComment(), activity.getTitle());
        assertEquals(userIdentityId, activity.getUserId());
        assertEquals(String.valueOf(announcementActivity.getId()),
                     activity.getTemplateParams().get(ANNOUNCEMENT_ID_TEMPLATE_PARAM));
        assertEquals(challenge.getTitle(),
                     activity.getTemplateParams().get(ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM));
        return true;
      }
    }));

    verify(announcementService, times(1)).updateAnnouncement(argThat(new ArgumentMatcher<Announcement>() {
      @Override
      public boolean matches(Announcement announcement) {
        return StringUtils.equals(activityId, String.valueOf(announcement.getActivityId()));
      }
    }));
  }

}

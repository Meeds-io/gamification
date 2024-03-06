/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
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
 *
 */
package io.meeds.gamification.listener;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.model.RealizationDTO;
import org.exoplatform.services.listener.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.service.RealizationService;

@RunWith(MockitoJUnitRunner.class)
public class AnnouncementRealizationUpdaterTest {

  @Mock
  private ActivityManager       activityManager;

  @Mock
  private RealizationService    realizationService;

  @Mock
  private Event<Object, Object> event;

  @Test
  public void testUpdateRealizationStatus() throws Exception {
    AnnouncementRealizationUpdater announcementActivityUpdater = new AnnouncementRealizationUpdater(activityManager,
                                                                                                    realizationService);

    Announcement announcement = new Announcement(1L,
                                                 1L,
                                                 "challenge title",
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 new Date().toString(),
                                                 null);
    when(event.getSource()).thenReturn(announcement);

    RealizationDTO realization = new RealizationDTO();
    realization.setRuleId(1L);
    realization.setEarnerId(String.valueOf(1L));
    realization.setEarnerType(IdentityType.USER.name());
    realization.setType(EntityType.MANUAL);
    realization.setStatus(RealizationStatus.ACCEPTED.name());
    realization.setActivityId(1L);
    realization.setReceiver("root");
    ExoSocialActivity activity = mock(ExoSocialActivity.class);
    when(realizationService.getRealizationById(anyLong())).thenReturn(realization);
    when(activityManager.getActivity(anyString())).thenReturn(activity);
    // When
    announcementActivityUpdater.onEvent(event);
    // then
    verify(activityManager, times(1)).updateActivity(activity);
  }
}

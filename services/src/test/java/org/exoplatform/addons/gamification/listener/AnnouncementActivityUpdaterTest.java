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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.addons.gamification.listener;

import org.exoplatform.addons.gamification.listener.challenges.AnnouncementActivityUpdater;
import org.exoplatform.addons.gamification.model.Announcement;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.manager.ActivityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnnouncementActivityUpdaterTest {

  @Mock
  private ActivityManager     activityManager;

  @Mock
  private AnnouncementService announcementService;

  @Test
  public void testUpdateActivity() throws ObjectNotFoundException {
    AnnouncementActivityUpdater announcementActivityUpdater =
                                                            new AnnouncementActivityUpdater(activityManager, announcementService);

    Announcement announcement = new Announcement(1l, 1l, "challenge title", 1L, "announcement comment", 1L, new Date().toString(), null);
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setType("not announcement activity");
    ActivityLifeCycleEvent activityLifeCycleEvent = new ActivityLifeCycleEvent(ActivityLifeCycleEvent.Type.UPDATE_ACTIVITY,
                                                                               activity);
    announcementActivityUpdater.updateActivity(activityLifeCycleEvent);
    verify(announcementService, times(0)).getAnnouncementById(anyLong());

    ExoSocialActivityImpl announcementActivity = new ExoSocialActivityImpl();
    announcementActivity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
    announcementActivity.setTitle(announcement.getComment());
    Map<String, String> params = new HashMap<>();
    params.put("announcementId", String.valueOf(announcement.getId()));
    params.put("announcementComment", "new announcement comment ");
    announcementActivity.setTemplateParams(params);
    ActivityLifeCycleEvent announcementActivityLifeCycleEvent =
                                                              new ActivityLifeCycleEvent(ActivityLifeCycleEvent.Type.UPDATE_ACTIVITY,
                                                                                         announcementActivity);

    when(announcementService.getAnnouncementById(announcement.getId())).thenReturn(announcement);

    announcementActivityUpdater.updateActivity(announcementActivityLifeCycleEvent);
    verify(announcementService, times(1)).getAnnouncementById(anyLong());
    verify(announcementService, times(1)).updateAnnouncement(any(Announcement.class));
    verify(activityManager, times(1)).updateActivity(any(ExoSocialActivity.class), anyBoolean());

    doThrow(ObjectNotFoundException.class).when(announcementService).updateAnnouncement(any(Announcement.class));
    announcementActivityUpdater.updateActivity(announcementActivityLifeCycleEvent);
    verify(announcementService, times(2)).getAnnouncementById(anyLong());
    verify(announcementService, times(2)).updateAnnouncement(any(Announcement.class));
    verify(activityManager, times(2)).updateActivity(any(ExoSocialActivity.class), anyBoolean());
  }
}

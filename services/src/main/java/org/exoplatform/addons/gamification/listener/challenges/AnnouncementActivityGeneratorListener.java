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
package org.exoplatform.addons.gamification.listener.challenges;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.social.websocket.ActivityStreamWebSocketService;
import org.exoplatform.social.websocket.entity.ActivityStreamModification;

import java.util.HashMap;
import java.util.Map;

import static org.exoplatform.addons.gamification.service.EntityBuilder.fromAnnouncementActivity;
import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;

public class AnnouncementActivityGeneratorListener extends Listener<AnnouncementService, AnnouncementActivity> {
  private static final Log               LOG                        =
                                             ExoLogger.getLogger(AnnouncementActivityGeneratorListener.class);


  private ExoContainer                   container;

  private ActivityStorage                activityStorage;

  private ChallengeService               challengeService;

  private ActivityStreamWebSocketService activityStreamWebSocketService;

  public AnnouncementActivityGeneratorListener(ExoContainer container,
                                               ActivityStorage activityStorage,
                                               ChallengeService challengeService,
                                               ActivityStreamWebSocketService activityStreamWebSocketService) {
    this.container = container;
    this.activityStorage = activityStorage;
    this.challengeService = challengeService;
    this.activityStreamWebSocketService = activityStreamWebSocketService;
  }

  @Override
  public void onEvent(Event<AnnouncementService, AnnouncementActivity> event) throws ObjectNotFoundException, IllegalAccessException {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    try {
      AnnouncementActivity announcementActivity = event.getData();
      Announcement announcement = fromAnnouncementActivity(announcementActivity);
      AnnouncementService announcementService = event.getSource();
      Challenge challenge = challengeService.getChallengeById(announcement.getChallengeId(), Utils.getCurrentUser());
      ExoSocialActivity activity = createActivity(announcement, announcementActivity.getTemplateParams(), challenge);
      announcement.setActivityId(Long.parseLong(activity.getId()));
      announcementService.updateAnnouncement(announcement);
      Space space = Utils.getSpaceById(String.valueOf(challenge.getAudience()));
      ActivityStreamModification activityStreamModification = new ActivityStreamModification(activity.getId(), "createActivity",space.getId());
      activityStreamWebSocketService.sendMessage(activityStreamModification);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private ExoSocialActivity createActivity(Announcement announcement, Map<String, String> params, Challenge challenge) throws ObjectNotFoundException {
    if (params == null) {
      params = new HashMap<>();
    }
    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    activity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
    activity.setTitle(challenge.getTitle());
    activity.setUserId(String.valueOf(announcement.getCreator()));
    params.put("announcementId", String.valueOf(announcement.getId()));
    params.put("announcementDescription", challenge.getTitle());
    activity.setTitle(announcement.getComment());
    activity.setTemplateParams(params);
    Space space = Utils.getSpaceById(String.valueOf(challenge.getAudience()));
    if (space == null) {
      throw new ObjectNotFoundException("space does not exist");
    }
    Identity owner = Utils.getIdentityByTypeAndId("space", space.getPrettyName());
    return activityStorage.saveActivity(owner, activity);
  }

}

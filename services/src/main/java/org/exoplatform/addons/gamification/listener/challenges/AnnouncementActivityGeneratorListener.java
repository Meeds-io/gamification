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
package org.exoplatform.addons.gamification.listener.challenges;

import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class AnnouncementActivityGeneratorListener extends Listener<AnnouncementService, AnnouncementActivity> {

  public static final String ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM = "announcementDescription";

  public static final String ANNOUNCEMENT_ID_TEMPLATE_PARAM          = "announcementId";

  private static final Log   LOG                                     =
                                 ExoLogger.getLogger(AnnouncementActivityGeneratorListener.class);

  private IdentityManager    identityManager;

  private SpaceService       spaceService;

  private ActivityManager    activityManager;

  private ChallengeService   challengeService;

  public AnnouncementActivityGeneratorListener(SpaceService spaceService,
                                               IdentityManager identityManager,
                                               ActivityManager activityManager,
                                               ChallengeService challengeService) {
    this.spaceService = spaceService;
    this.identityManager = identityManager;
    this.activityManager = activityManager;
    this.challengeService = challengeService;
  }

  @Override
  public void onEvent(Event<AnnouncementService, AnnouncementActivity> event) {
    AnnouncementActivity announcementActivity = event.getData();
    try {
      Announcement announcement = EntityMapper.fromAnnouncementActivity(announcementActivity);
      AnnouncementService announcementService = event.getSource();
      Identity creatorIdentity = identityManager.getIdentity(String.valueOf(announcement.getCreator()));
      Challenge challenge = challengeService.getChallengeById(announcement.getChallengeId(), creatorIdentity.getRemoteId());
      ExoSocialActivity activity = createActivity(announcement, announcementActivity.getTemplateParams(), challenge);
      announcement.setActivityId(Long.parseLong(activity.getId()));
      announcementService.updateAnnouncement(announcement);
    } catch (Exception e) {
      LOG.warn("Error while creating activity for announcement with id {} made by user {}",
               announcementActivity.getId(),
               announcementActivity.getCreator(),
               e);
    }
  }

  private ExoSocialActivity createActivity(Announcement announcement,
                                           Map<String, String> params,
                                           Challenge challenge) throws ObjectNotFoundException {
    if (params == null) {
      params = new HashMap<>();
    }
    Space space = spaceService.getSpaceById(String.valueOf(challenge.getAudience()));
    if (space == null) {
      throw new ObjectNotFoundException("space doesn't exists");
    }
    Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
    if (spaceIdentity == null) {
      throw new ObjectNotFoundException("space doesn't exists");
    }

    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    activity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
    activity.setTitle(announcement.getComment());
    activity.setUserId(String.valueOf(announcement.getCreator()));
    params.put(ANNOUNCEMENT_ID_TEMPLATE_PARAM, String.valueOf(announcement.getId()));
    params.put(ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM, challenge.getTitle());
    Utils.buildActivityParams(activity, params);

    activityManager.saveActivityNoReturn(spaceIdentity, activity);
    return activityManager.getActivity(activity.getId());
  }

}

/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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

import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR;
import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER;
import static io.meeds.gamification.constant.GamificationConstant.IDENTITY_OBJECT_TYPE;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.social.core.storage.cache.CachedActivityStorage;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

public class GamificationProfileListener extends ProfileListenerPlugin {

  protected RuleService         ruleService;

  protected IdentityManager     identityManager;

  protected SpaceService        spaceService;

  protected RealizationService  realizationService;

  protected AnnouncementService announcementService;

  private ActivityStorage       activityStorage;

  public GamificationProfileListener(RuleService ruleService,
                                     IdentityManager identityManager,
                                     SpaceService spaceService,
                                     RealizationService realizationService,
                                     AnnouncementService announcementService,
                                     ActivityStorage activityStorage) {
    this.ruleService = ruleService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.realizationService = realizationService;
    this.announcementService = announcementService;
    this.activityStorage = activityStorage;
  }

  @Override
  public void avatarUpdated(ProfileLifeCycleEvent event) {

    Long lastUpdate = event.getProfile().getAvatarLastUpdated();
    String identityId = event.getProfile().getIdentity().getId();

    // Do not reward a user when he update his avatar, reward user only when he
    // add
    // an avatar for the first time
    if (lastUpdate != null) {
      return;
    }
    realizationService.createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR,
                                          identityId,
                                          identityId,
                                          identityId,
                                          IDENTITY_OBJECT_TYPE);
  }

  @Override
  public void bannerUpdated(ProfileLifeCycleEvent event) {

    Long lastUpdate = event.getProfile().getBannerLastUpdated();
    String identityId = event.getProfile().getIdentity().getId();

    // Do not reward a user when he update his banner, reward user only when he
    // add
    // a banner for the first time
    if (lastUpdate != null) {
      return;
    }

    realizationService.createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER,
                                          identityId,
                                          identityId,
                                          identityId,
                                          IDENTITY_OBJECT_TYPE);
  }

  @Override
  public void basicInfoUpdated(ProfileLifeCycleEvent event) {
    // Nothing to gamify
  }

  @Override
  public void contactSectionUpdated(ProfileLifeCycleEvent event) {
    String userIdentityId = event.getProfile().getIdentity().getId();
    clearUserActivitiesCache(userIdentityId);
  }

  @Override
  public void experienceSectionUpdated(ProfileLifeCycleEvent event) {
    // Nothing to gamify
  }

  @Override
  public void headerSectionUpdated(ProfileLifeCycleEvent event) {
    // Nothing to gamify
  }

  @Override
  public void createProfile(ProfileLifeCycleEvent event) {
    // Nothing to gamify
  }

  @Override
  public void aboutMeUpdated(ProfileLifeCycleEvent event) {

    String identityId = event.getProfile().getIdentity().getId();

    realizationService.createRealizations(GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME,
                                          identityId,
                                          identityId,
                                          identityId,
                                          IDENTITY_OBJECT_TYPE);
  }

  private void clearUserActivitiesCache(String userIdentityId) {
    if (!(activityStorage instanceof CachedActivityStorage cachedActivityStorage)) {
      return;
    }
    List<Announcement> announcements = announcementService.findAnnouncements(userIdentityId);
    if (CollectionUtils.isNotEmpty(announcements)) {
      announcements.stream()
                   .map(Announcement::getActivityId)
                   .map(String::valueOf)
                   .forEach(cachedActivityStorage::clearActivityCached);
    }
  }
}

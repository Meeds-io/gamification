/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2023 Meeds Association contact@meeds.io
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
 */
package io.meeds.gamification.storage.cached;

import java.io.Serializable;

import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.storage.AnnouncementStorage;
import io.meeds.gamification.storage.RealizationStorage;
import io.meeds.gamification.storage.RuleStorage;

public class AnnouncementCachedStorage extends AnnouncementStorage {

  private static final int                               ANNOUNCEMENT_ID_CONTEXT = 0;

  private static final String                            ANNOUNCEMENT_CACHE_NAME = "gamification.announcement";

  private FutureExoCache<Serializable, Integer, Integer> announcementFutureCache;

  public AnnouncementCachedStorage(RealizationStorage realizationsStorage,
                                   RuleStorage ruleStorage,
                                   CacheService cacheService) {
    super(realizationsStorage, ruleStorage);

    ExoCache<Serializable, Integer> announcementCache = cacheService.getCacheInstance(ANNOUNCEMENT_CACHE_NAME);
    Loader<Serializable, Integer, Integer> announcementLoader = new Loader<Serializable, Integer, Integer>() {
      @Override
      public Integer retrieve(Integer context, Serializable key) throws Exception {
        if (context == ANNOUNCEMENT_ID_CONTEXT) {
          return AnnouncementCachedStorage.super.countAnnouncements((Long) key);
        } else {
          throw new IllegalStateException("Unknown context id " + context);
        }
      }
    };
    this.announcementFutureCache = new FutureExoCache<>(announcementLoader, announcementCache);
  }

  @Override
  public Announcement createAnnouncement(Announcement announcement) {
    announcement = super.createAnnouncement(announcement);
    if (announcement != null) {
      this.announcementFutureCache.remove(announcement.getChallengeId());
    }
    return announcement;
  }

  @Override
  public Announcement deleteAnnouncement(long announcementId) {
    Announcement announcement = super.deleteAnnouncement(announcementId);
    if (announcement != null) {
      this.announcementFutureCache.remove(announcement.getChallengeId());
    }
    return announcement;
  }

  @Override
  public int countAnnouncements(long ruleId) {
    return this.announcementFutureCache.get(ANNOUNCEMENT_ID_CONTEXT, ruleId);
  }

}

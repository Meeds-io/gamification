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

    ExoCache<Serializable, Integer> domainCache = cacheService.getCacheInstance(ANNOUNCEMENT_CACHE_NAME);
    Loader<Serializable, Integer, Integer> domainLoader = new Loader<Serializable, Integer, Integer>() {
      @Override
      public Integer retrieve(Integer context, Serializable key) throws Exception {
        if (context == ANNOUNCEMENT_ID_CONTEXT) {
          return AnnouncementCachedStorage.super.countAnnouncements((Long) key);
        } else {
          throw new IllegalStateException("Unknown context id " + context);
        }
      }
    };
    this.announcementFutureCache = new FutureExoCache<>(domainLoader, domainCache);
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

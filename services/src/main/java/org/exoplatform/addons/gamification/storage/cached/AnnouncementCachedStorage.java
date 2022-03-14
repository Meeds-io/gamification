package org.exoplatform.addons.gamification.storage.cached;

import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import java.io.Serializable;

public class AnnouncementCachedStorage extends AnnouncementStorage {

    private static final int ANNOUNCEMENT_ID_CONTEXT = 0;
    private static final String ANNOUNCEMENT_CACHE_NAME = "gamification.announcement";

    private FutureExoCache<Serializable, Object, Integer> announcementFutureCache;
    public AnnouncementCachedStorage (GamificationHistoryDAO gamificationHistoryDAO, RuleStorage ruleStorage , CacheService cacheService){
        super(gamificationHistoryDAO, ruleStorage);

        ExoCache<Serializable, Object> domainCache = cacheService.getCacheInstance(ANNOUNCEMENT_CACHE_NAME);
        Loader<Serializable, Object, Integer> domainLoader = new Loader<Serializable, Object, Integer>() {
            @Override
            public Object retrieve(Integer context, Serializable key) throws Exception {
                if (context == ANNOUNCEMENT_ID_CONTEXT) {
                    return AnnouncementCachedStorage.super.countAnnouncementsByChallenge((Long) key);
                } else {
                    throw new IllegalStateException("Unknown context id " + context);
                }

            }
        };
        this.announcementFutureCache = new FutureExoCache<>(domainLoader, domainCache);
    }

    @Override
    public Long countAnnouncementsByChallenge(Long challengeId) {
        return (Long) this.announcementFutureCache.get(ANNOUNCEMENT_ID_CONTEXT,challengeId);
    }

    @Override
    public Announcement saveAnnouncement(Announcement announcement) {
        try {
            return super.saveAnnouncement(announcement);
        } finally {
            this.announcementFutureCache.remove(announcement.getChallengeId());
        }
    }
}

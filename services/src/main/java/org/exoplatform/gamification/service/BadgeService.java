package org.exoplatform.gamification.service;

import org.exoplatform.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.gamification.storage.dao.BadgeDAO;

public class BadgeService {

    protected final BadgeDAO badgeStorage;

    public BadgeService(BadgeDAO badgeDAO) {

        this.badgeStorage = badgeDAO;

    }

    public void createBadge (BadgeEntity badgeEntity) {

        //--- find the badge by Id
        BadgeEntity badgeE = badgeStorage.find(badgeEntity.getId());

    }

    public void deleteBadge () {

    }

    public void getAllBadges () {
        badgeStorage.findAll();

    }
}

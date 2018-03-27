package org.exoplatform.gamification.service.configuration;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.gamification.service.mapper.BadgeMapper;
import org.exoplatform.gamification.storage.dao.BadgeDAO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class BadgeService {

    private static final Log LOG = ExoLogger.getLogger(BadgeService.class);

    protected final BadgeDAO badgeStorage;
    protected final BadgeMapper badgeMapper;

    public BadgeService() {
        this.badgeStorage = CommonsUtils.getService(BadgeDAO.class);
        this.badgeMapper = CommonsUtils.getService(BadgeMapper.class);

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

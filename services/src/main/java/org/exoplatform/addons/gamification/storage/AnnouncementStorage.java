package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;

import java.util.List;

public class AnnouncementStorage {

  private GamificationHistoryDAO gamificationHistoryDAO;


  public AnnouncementStorage(GamificationHistoryDAO gamificationHistoryDAO) {
    this.gamificationHistoryDAO = gamificationHistoryDAO;
  }

  public Long countAnnouncementsByChallenge(Long challengeId) {
    Long countAnnounce = gamificationHistoryDAO.countAnnouncementsByChallenge(challengeId);
    return countAnnounce;
  }

  public List<Announcement> findAllAnnouncementByChallenge(Long challengeId, int offset, int limit) {
    List<GamificationActionsHistory> announcementEntities= gamificationHistoryDAO.findAllAnnouncementByChallenge(challengeId, offset, limit);
    return EntityMapper.fromAnnouncementEntities(announcementEntities);
  }
}

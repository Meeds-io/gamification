package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;

public class AnnouncementStorage {

  private GamificationHistoryDAO gamificationHistoryDAO;


  public AnnouncementStorage(GamificationHistoryDAO gamificationHistoryDAO) {
    this.gamificationHistoryDAO = gamificationHistoryDAO;
  }

  public Long countAnnouncementsByChallenge(Long challengeId) {
    Long countAnnounce = gamificationHistoryDAO.countAnnouncementsByChallenge(challengeId);
    return countAnnounce;
  }

}

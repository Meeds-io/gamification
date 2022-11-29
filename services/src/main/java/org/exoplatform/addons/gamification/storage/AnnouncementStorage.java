package org.exoplatform.addons.gamification.storage;

import java.util.Date;
import java.util.List;

import org.exoplatform.addons.gamification.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.dao.RuleDAO;
import org.exoplatform.addons.gamification.entity.RuleEntity;
import org.exoplatform.addons.gamification.entity.GamificationActionsHistoryEntity;
import org.exoplatform.addons.gamification.model.Announcement;
import org.exoplatform.addons.gamification.utils.EntityMapper;

public class AnnouncementStorage {

  public static final long             MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24; // NOSONAR

  private final GamificationHistoryDAO announcementDAO;

  private final RuleDAO                ruleDAO;

  public AnnouncementStorage(GamificationHistoryDAO announcementDAO, RuleDAO ruleDAO) {
    this.announcementDAO = announcementDAO;
    this.ruleDAO = ruleDAO;
  }

  public Announcement saveAnnouncement(Announcement announcement) {
    if (announcement == null) {
      throw new IllegalArgumentException("Announcement argument is null");
    }
    RuleEntity ruleEntity = ruleDAO.find(announcement.getChallengeId());
    GamificationActionsHistoryEntity announcementEntity = EntityMapper.toEntity(announcement, ruleEntity);
    Date nextToEndDate = new Date(ruleEntity.getEndDate().getTime() + MILLIS_IN_A_DAY);
    if (!announcementEntity.getCreatedDate().before(nextToEndDate)) {
      throw new IllegalArgumentException("announcement is not allowed when challenge is ended ");
    }
    if (!announcementEntity.getCreatedDate().after(ruleEntity.getStartDate())) {
      throw new IllegalArgumentException("announcement is not allowed when challenge is not started ");
    }
    if (announcementEntity.getId() == null) {
      announcementEntity = announcementDAO.create(announcementEntity);
    } else {
      announcementEntity = announcementDAO.update(announcementEntity);
    }
    return EntityMapper.fromEntity(announcementEntity);
  }

  public Announcement getAnnouncementById(long announcementId) {
    GamificationActionsHistoryEntity announcementEntity = this.announcementDAO.find(announcementId);
    return EntityMapper.fromEntity(announcementEntity);
  }

  public List<Announcement> findAllAnnouncementByChallenge(Long challengeId, int offset, int limit) {
    List<GamificationActionsHistoryEntity> announcementEntities = announcementDAO.findAllAnnouncementByChallenge(challengeId,
                                                                                                                 offset,
                                                                                                                 limit);
    return EntityMapper.fromAnnouncementEntities(announcementEntities);
  }

  public Long countAnnouncementsByChallenge(Long challengeId) {
    return announcementDAO.countAnnouncementsByChallenge(challengeId);
  }
}

package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.utils.Utils;

import java.util.Date;
import java.util.List;

public class AnnouncementStorage {

  private GamificationHistoryDAO announcementDAO;

  private RuleStorage       challengeStorage;

  public static final long       MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24; // NOSONAR

  public AnnouncementStorage(GamificationHistoryDAO announcementDAO, RuleStorage challengeStorage) {
    this.announcementDAO = announcementDAO;
    this.challengeStorage = challengeStorage;
  }

  public Announcement saveAnnouncement(Announcement announcement) {
    if (announcement == null) {
      throw new IllegalArgumentException("Announcement argument is null");
    }
    Challenge challenge = challengeStorage.getChallengeById(announcement.getChallengeId());
    RuleEntity challengeEntity = EntityMapper.toEntity(challenge);
    GamificationActionsHistory announcementEntity = EntityMapper.toEntity(announcement);
    Date nextToEndDate = new Date(challengeEntity.getEndDate().getTime() + MILLIS_IN_A_DAY);

    if (!announcementEntity.getCreatedDate().before(nextToEndDate)) {
      throw new IllegalArgumentException("announcement is not allowed when challenge is ended ");
    }
    if (!announcementEntity.getCreatedDate().after(challengeEntity.getStartDate())) {
      throw new IllegalArgumentException("announcement is not allowed when challenge is not started ");
    }
    DomainEntity domainEntity = DomainMapper.domainDTOToDomain(Utils.getDomainByTitle(challenge.getProgram()));
    announcementEntity.setEarnerType(IdentityType.USER);
    announcementEntity.setActionTitle(challengeEntity.getTitle());
    announcementEntity.setActionScore(challengeEntity.getScore());
    announcementEntity.setGlobalScore(Utils.getUserGlobalScore(String.valueOf(announcement.getAssignee())));
    announcementEntity.setDomainEntity(domainEntity);
    announcementEntity.setDomain(domainEntity.getTitle());
    if (announcementEntity.getId() == null) {
      announcementEntity = announcementDAO.create(announcementEntity);
    } else {
      announcementEntity = announcementDAO.update(announcementEntity);
    }
    return EntityMapper.fromEntity(announcementEntity);
  }

  public Announcement getAnnouncementById(long announcementId) {
    GamificationActionsHistory announcementEntity = this.announcementDAO.find(announcementId);
    return EntityMapper.fromEntity(announcementEntity);
  }

  public List<Announcement> findAllAnnouncementByChallenge(Long challengeId, int offset, int limit) {
    List<GamificationActionsHistory> announcementEntities = announcementDAO.findAllAnnouncementByChallenge(challengeId,
                                                                                                           offset,
                                                                                                           limit);
    return EntityMapper.fromAnnouncementEntities(announcementEntities);
  }

  public Long countAnnouncementsByChallenge(Long challengeId) {
    return announcementDAO.countAnnouncementsByChallenge(challengeId);
  }
}

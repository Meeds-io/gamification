package org.exoplatform.addons.gamification.service.configuration;

import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_EVENT;

import java.util.List;
import java.util.Map;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

public class AnnouncementServiceImpl implements AnnouncementService {

  private static final Log    LOG = ExoLogger.getLogger(AnnouncementServiceImpl.class);

  private AnnouncementStorage announcementStorage;

  private IdentityManager     identityManager;

  private ChallengeService    challengeService;

  private ListenerService     listenerService;

  public AnnouncementServiceImpl(AnnouncementStorage announcementStorage,
                                 ChallengeService challengeService,
                                 IdentityManager identityManager,
                                 ListenerService listenerService) {
    this.announcementStorage = announcementStorage;
    this.identityManager = identityManager;
    this.challengeService = challengeService;
    this.listenerService = listenerService;
  }

  @Override
  public Announcement createAnnouncement(Announcement announcement,
                                         Map<String, String> templateParams,
                                         String currentUser,
                                         boolean system) throws IllegalArgumentException,
                                                         ObjectNotFoundException,
                                                         IllegalAccessException {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (announcement.getId() != 0) {
      throw new IllegalArgumentException("announcement id must be equal to 0");
    }
    Challenge challenge = challengeService.getChallengeById(announcement.getChallengeId());
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    Long assignee = announcement.getAssignee();
    if (assignee == null) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    Identity assigneeIdentity = identityManager.getIdentity(assignee.toString());
    if (assigneeIdentity == null || assigneeIdentity.isDeleted() || !assigneeIdentity.isEnable()) {
      throw new ObjectNotFoundException("Assignee with id " + assignee + " does not exist");
    }

    if (!Utils.canAnnounce(String.valueOf(challenge.getAudience()), currentUser)) {
      throw new IllegalAccessException("user " + currentUser + " is not allowed to announce challenge on  space with id "
          + challenge.getAudience());
    }
    Identity creatorIdentity = identityManager.getOrCreateUserIdentity(currentUser);
    announcement.setCreator(Long.parseLong(creatorIdentity.getId()));
    announcement = announcementStorage.saveAnnouncement(announcement);
    if (!system) {
      try {
        listenerService.broadcast(ANNOUNCEMENT_ACTIVITY_EVENT, this, EntityMapper.toAnnouncementActivity(announcement, templateParams));
      } catch (Exception e) {
        LOG.error("Unexpected error", e);
      }
    }
    return getAnnouncementById(announcement.getId());
  }

  @Override
  public List<Announcement> findAllAnnouncementByChallenge(long challengeId,
                                                           int offset,
                                                           int limit,
                                                           PeriodType periodType,
                                                           IdentityType earnerType) {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    return announcementStorage.findAllAnnouncementByChallenge(challengeId, offset, limit, periodType, earnerType);
  }

  @Override
  public Long countAllAnnouncementsByChallenge(long challengeId) throws ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeService.getChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    return announcementStorage.countAnnouncementsByChallenge(challengeId);
  }

  @Override
  public Announcement updateAnnouncement(Announcement announcement) throws ObjectNotFoundException {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (announcement.getId() == 0) {
      throw new IllegalArgumentException("announcement id must not be equal to 0");
    }

    Announcement oldAnnouncement = announcementStorage.getAnnouncementById(announcement.getId());
    if (oldAnnouncement == null) {
      throw new ObjectNotFoundException("Announcement does not exist");
    }
    return announcementStorage.saveAnnouncement(announcement);
  }

  @Override
  public Announcement getAnnouncementById(Long announcementId) {
    if (announcementId == null || announcementId <= 0) {
      throw new IllegalArgumentException("announcement id is mandatory");
    }
    return announcementStorage.getAnnouncementById(announcementId);
  }
}

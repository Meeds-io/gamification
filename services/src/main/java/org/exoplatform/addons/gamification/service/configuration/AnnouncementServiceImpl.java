package org.exoplatform.addons.gamification.service.configuration;

import static org.exoplatform.addons.gamification.utils.Utils.*;

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
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

public class AnnouncementServiceImpl implements AnnouncementService {

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
                                         String username,
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

    if (!Utils.canAnnounce(String.valueOf(challenge.getAudience()), username)) {
      throw new IllegalAccessException("user " + username + " is not allowed to announce challenge on  space with id "
          + challenge.getAudience());
    }
    Identity creatorIdentity = identityManager.getOrCreateUserIdentity(username);
    long creatorId = Long.parseLong(creatorIdentity.getId());
    announcement.setCreator(creatorId);
    announcement = announcementStorage.saveAnnouncement(announcement);
    broadcastEvent(listenerService, POST_CREATE_ANNOUNCEMENT_EVENT, announcement, creatorId);

    if (!system) {
      broadcastEvent(listenerService, ANNOUNCEMENT_ACTIVITY_EVENT, this, EntityMapper.toAnnouncementActivity(announcement, templateParams));
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
  public List<Announcement> getAnnouncementsByEarnerId(String earnerId) {
    return announcementStorage.getAnnouncementsByEarnerId(earnerId);
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
  public Long countAnnouncementsByChallengeAndEarnerType(long challengeId, IdentityType earnerType) throws ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeService.getChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    return announcementStorage.countAnnouncementsByChallengeAndEarnerType(challengeId, earnerType);
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
    Announcement savedAnnouncement = announcementStorage.saveAnnouncement(announcement);
    broadcastEvent(listenerService, POST_UPDATE_ANNOUNCEMENT_EVENT, announcement, announcement.getCreator());
    return savedAnnouncement;
  }

  @Override
  public Announcement getAnnouncementById(Long announcementId) {
    if (announcementId == null || announcementId <= 0) {
      throw new IllegalArgumentException("announcement id is mandatory");
    }
    return announcementStorage.getAnnouncementById(announcementId);
  }
}

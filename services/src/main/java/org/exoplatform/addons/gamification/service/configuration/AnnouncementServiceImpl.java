package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;

import java.util.List;

public class AnnouncementServiceImpl implements AnnouncementService {

  private static final Log    LOG = ExoLogger.getLogger(AnnouncementServiceImpl.class);

  private AnnouncementStorage announcementStorage;

  private ChallengeStorage    challengeStorage;

  private ListenerService     listenerService;

  public AnnouncementServiceImpl(AnnouncementStorage announcementStorage,
                                 ChallengeStorage challengeStorage,
                                 ListenerService listenerService) {
    this.announcementStorage = announcementStorage;
    this.challengeStorage = challengeStorage;
    this.listenerService = listenerService;
  }

  @Override
  public Announcement createAnnouncement(Announcement announcement, String currentUser) throws IllegalArgumentException,
                                                                                        ObjectNotFoundException,
                                                                                        IllegalAccessException {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (announcement.getId() != 0) {
      throw new IllegalArgumentException("announcement id must be equal to 0");
    }
    Challenge challenge = challengeStorage.getChallengeById(announcement.getChallengeId());
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    if (announcement.getAssignee() == null) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    if (!Utils.canAnnounce(String.valueOf(challenge.getAudience()))) {
      throw new IllegalAccessException("user is not allowed to announce challenge");
    }

    Identity identity = Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, currentUser);
    announcement.setCreator(Long.parseLong(identity.getId()));
    announcement = announcementStorage.saveAnnouncement(announcement);
    return announcement;
  }

  @Override
  public List<Announcement> findAllAnnouncementByChallenge(long challengeId,
                                                           int offset,
                                                           int limit) throws ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    return announcementStorage.findAllAnnouncementByChallenge(challengeId, offset, limit);
  }

  @Override
  public Long countAllAnnouncementsByChallenge(long challengeId) throws ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    return announcementStorage.countAnnouncementsByChallenge(challengeId);
  }

  @Override
  public Announcement updateAnnouncement(Announcement announcement) throws IllegalArgumentException, ObjectNotFoundException {
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
  public Announcement getAnnouncementById(Long announcementId) throws IllegalArgumentException {
    if (announcementId == null || announcementId <= 0) {
      throw new IllegalArgumentException("announcement id is mandatory");
    }
    return announcementStorage.getAnnouncementById(announcementId);
  }
}

package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

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
  public List<Announcement> findAllAnnouncementByChallenge(long challengeId, int offset, int limit) throws  ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    return announcementStorage.findAllAnnouncementByChallenge(challengeId, offset, limit);
  }

}

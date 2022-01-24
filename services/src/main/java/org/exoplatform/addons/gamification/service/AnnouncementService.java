package org.exoplatform.addons.gamification.service;

import org.exoplatform.commons.exception.ObjectNotFoundException;

public interface AnnouncementService {

  /**
   * Retrieves number of all Announcements by challengeId.
   *
   * @return A {@link Long} number of announcements
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  Long countAllAnnouncementsByChallenge(long challengeId) throws ObjectNotFoundException;

}

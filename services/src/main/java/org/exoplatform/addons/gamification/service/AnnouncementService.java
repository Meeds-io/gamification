package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;

public interface AnnouncementService {

  /**
   * Retrieves number of all Announcements by challengeId.
   *
   * @return A {@link Long} number of announcements
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  Long countAllAnnouncementsByChallenge(long challengeId) throws ObjectNotFoundException;

  /**
   * Retrieves all Announcements by challengeId.
   *
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List <Announcement>} object
   * @throws IllegalAccessException when user is not authorized to access
   *           announcement
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  List<Announcement> findAllAnnouncementByChallenge(long challengeId, int offset, int limit) throws IllegalAccessException,
          ObjectNotFoundException;

}

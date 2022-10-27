package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Map;

public interface AnnouncementService {

  /**
   * Retrieves all Announcements by challengeId.
   *
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List &lt;Announcement&gt;} object
   * @throws IllegalAccessException when user is not authorized to access
   *           announcement
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  List<Announcement> findAllAnnouncementByChallenge(long challengeId, int offset, int limit) throws IllegalAccessException,
                                                                                             ObjectNotFoundException;

  /**
   * Creates a new announcement
   *
   * @param announcement {@link Announcement} object to create
   * @param username User name accessing announcement
   * @param system check if announcement created by system
   * @return created {@link Announcement} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *           announcement for the designated owner defined in object
   */
  Announcement createAnnouncement(Announcement announcement, Map<String, String> templateParams , String username, boolean system) throws IllegalAccessException,
                                                                              ObjectNotFoundException;

  /**
   * Update announcement
   *
   * @param announcement {@link Announcement} object to Update
   * @return a {@link Announcement} Object
   * @throws ObjectNotFoundException when the announcement identified by its
   *           technical identifier is not found
   */

  Announcement updateAnnouncement(Announcement announcement) throws ObjectNotFoundException;

  /**
   * Retrieves a announcement identified by its technical identifier.
   *
   * @param announcementId technical identifier of a challenge
   * @return A {@link Announcement} object
   *
   */

  Announcement getAnnouncementById(Long announcementId) ;

  /**
   * Retrieves number of all Announcements by challengeId.
   *
   * @return A {@link Long} number of announcements
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  Long countAllAnnouncementsByChallenge(long challengeId) throws ObjectNotFoundException;

}

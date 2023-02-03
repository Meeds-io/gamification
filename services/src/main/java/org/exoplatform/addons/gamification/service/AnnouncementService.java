package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Map;

public interface AnnouncementService {

  /**
   * Retrieves all Announcements by challengeId.
   *
   * @param challengeId technical identifier of a challenge
   * @param offset      Offset
   * @param limit       Limit
   * @param periodType  periodType
   * @param earnerType  earnerType
   * @return A {@link List &lt;Announcement&gt;} object
   * @throws IllegalAccessException when user is not authorized to access announcement
   */
  List<Announcement> findAllAnnouncementByChallenge(long challengeId, int offset, int limit, PeriodType periodType, IdentityType earnerType) throws IllegalAccessException;

  /**
   * Creates a new announcement
   *
   * @param announcement {@link Announcement} object to create
   * @param templateParams Activity Template params
   * @param username User name accessing announcement
   * @param system check if announcement created by system
   * @return created {@link Announcement} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *           announcement for the designated owner defined in object
   * @throws ObjectNotFoundException 
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
   * Retrieves all announcements by earnerId.
   *
   * @param earnerId : the userId used in projection
   * @return A {@link List &lt;Announcement&gt;} object
   */
  List<Announcement> getAnnouncementsByEarnerId(String earnerId);

  /**
   * Retrieves number of all Announcements by challenge identifier.
   * 
   * @param challengeId Challenge technical identifier
   * @return A {@link Long} number of announcements
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  Long countAllAnnouncementsByChallenge(long challengeId) throws ObjectNotFoundException;

  /**
   * Retrieves number of all Announcements by challenge identifier.
   *
   * @param challengeId Challenge technical identifier
   * @param earnerType  the earner identity type
   * @return A {@link Long} number of announcements
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  Long countAnnouncementsByChallengeAndEarnerType(long challengeId, IdentityType earnerType) throws ObjectNotFoundException;


}

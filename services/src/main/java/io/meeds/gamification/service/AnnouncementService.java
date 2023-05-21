package io.meeds.gamification.service;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;

import java.util.List;
import java.util.Map;

public interface AnnouncementService {

  /**
   * Retrieves all Announcements by Rule Identifier.
   *
   * @param  ruleId                  technical identifier of a Rule
   * @param  offset                  Offset
   * @param  limit                   Limit
   * @param  periodType              periodType
   * @param  earnerType              earnerType
   * @param  username                user name accessing announcements
   * @return                         A {@link List &lt;Announcement&gt;} object
   * @throws IllegalAccessException  when user is not authorized to create a
   *                                   announcement for the designated owner
   *                                   defined in object
   * @throws ObjectNotFoundException when {@link RuleDTO} isn't found
   */
  List<Announcement> findAnnouncements(long ruleId,
                                       int offset,
                                       int limit,
                                       PeriodType periodType,
                                       IdentityType earnerType,
                                       String username) throws IllegalAccessException, ObjectNotFoundException;

  /**
   * Creates a new announcement
   *
   * @param  announcement            {@link Announcement} object to create
   * @param  templateParams          Activity Template params
   * @param  username                User name accessing announcement
   * @return                         created {@link Announcement} with generated
   *                                 technical identifier
   * @throws IllegalAccessException  when user is not authorized to create a
   *                                   announcement for the designated owner
   *                                   defined in object
   * @throws ObjectNotFoundException
   */
  Announcement createAnnouncement(Announcement announcement,
                                  Map<String, String> templateParams,
                                  String username) throws IllegalAccessException, ObjectNotFoundException;

  /**
   * @param  rule     {@link RuleDTO}
   * @param  username User name willing to announce a realization
   * @return          true if can announce it else returns false
   */
  boolean canAnnounce(RuleDTO rule, String username);

  /**
   * Update announcement
   *
   * @param  announcementId          {@link Announcement} id which references
   *                                   the {@link RealizationDTO} id
   * @param  comment                 new user comment
   * @return                         a {@link Announcement} Object
   * @throws ObjectNotFoundException when the announcement identified by its
   *                                   technical identifier is not found
   */
  Announcement updateAnnouncementComment(long announcementId, String comment) throws ObjectNotFoundException;

  /**
   * Deletes announcement
   *
   * @param  announcementId          technical identifier of announcement
   * @param  username                Username who want to delete announcement
   * @return                         a {@link Announcement} Object
   * @throws ObjectNotFoundException when the announcement identified by its
   *                                   technical identifier is not found
   * @throws IllegalAccessException  when user is not allowed to delete the
   *                                   announce
   */
  Announcement deleteAnnouncement(long announcementId, String username) throws ObjectNotFoundException, IllegalAccessException;

  /**
   * Retrieves a announcement identified by its technical identifier.
   *
   * @param  announcementId technical identifier of a {@link RealizationDTO}
   * @return                A {@link Announcement} object
   */

  Announcement getAnnouncementById(long announcementId);

  /**
   * Retrieves all announcements by earnerId.
   *
   * @param  earnerIdentityId : the userId used in projection
   * @return                  A {@link List &lt;Announcement&gt;} object
   */
  List<Announcement> findAnnouncements(String earnerIdentityId);

  /**
   * Retrieves number of all Announcements by Rule identifier.
   * 
   * @param  ruleId                  Rule technical identifier
   * @return                         A {@link Long} number of announcements
   */
  int countAnnouncements(long ruleId);

  /**
   * Retrieves number of all Announcements by {@link RuleDTO} identifier.
   *
   * @param  ruleId                  {@link RuleDTO} technical identifier
   * @param  earnerType              the earner identity type
   * @return                         A {@link Long} number of announcements
   */
  int countAnnouncements(long ruleId, IdentityType earnerType);

}

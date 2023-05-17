package io.meeds.gamification.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProfileReputation;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.model.filter.RealizationFilter;

public interface RealizationService {

  /**
   * Retrieves all Realizations by Filter.
   *
   * @param  filter                 used to filter realizations using
   *                                  {@link RealizationFilter}
   * @param  userAclIdentity        current identity {@link Identity}
   * @param  offset                 Offset
   * @param  limit                  Limit
   * @return                        A {@link List &lt;RealizationDTO&gt;} object
   * @throws IllegalAccessException when User doesn't have enough privileges to
   *                                  access achievements of user
   */
  List<RealizationDTO> getRealizationsByFilter(RealizationFilter filter,
                                               Identity userAclIdentity,
                                               int offset,
                                               int limit) throws IllegalAccessException;

  /**
   * Count realizations by filter
   *
   * @param  filter                 used to filter realizations using
   *                                  {@link RealizationFilter}
   * @param  userAclIdentity        current {@link Identity}
   * @return                        realizations count
   * @throws IllegalAccessException when User doesn't have enough privileges to
   *                                  access achievements of user
   */
  int countRealizationsByFilter(RealizationFilter filter, Identity userAclIdentity) throws IllegalAccessException;

  /**
   * Retrieves a realization identified by its technical identifier.
   *
   * @param  realizationId           technical identifier of a realization
   * @param  userAclIdentity         current identity {@link Identity}
   * @return                         A {@link RealizationDTO} object
   * @throws IllegalAccessException  when user is not authorized to access
   *                                   realization
   * @throws ObjectNotFoundException when the realization identified by its
   *                                   technical identifier is not found
   */
  RealizationDTO getRealizationById(long realizationId, Identity userAclIdentity) throws IllegalAccessException,
                                                                                  ObjectNotFoundException;

  /**
   * Retrieves a realization identified by its technical identifier.
   *
   * @param  realizationId technical identifier of a realization
   * @return               A {@link RealizationDTO} object
   */
  RealizationDTO getRealizationById(long realizationId);

  /**
   * Export realizations found switch filter into an {@link InputStream}
   * containing a file of format XLS
   * 
   * @param  filter                 used to filter realizations using
   *                                  {@link RealizationFilter}
   * @param  userAclIdentity        current {@link Identity}
   * @param  fileName               fileName to export
   * @param  locale                 used {@link Locale} for XLS header labels
   * @return                        {@link InputStream} of a file of format XLS
   * @throws IllegalAccessException when User doesn't have enough privileges to
   *                                  access achievements of user
   */
  InputStream exportXlsx(RealizationFilter filter,
                         Identity userAclIdentity,
                         String fileName,
                         Locale locale) throws IllegalAccessException;

  /**
   * Retrieves the list of realizations identified by object Id and object Type.
   *
   * @param  objectId   object Id
   * @param  objectType object Type
   * @return            A {@link List &lt;RealizationDTO&gt;} object
   */
  List<RealizationDTO> findRealizationsByObjectIdAndObjectType(String objectId, String objectType);

  /**
   * Get realization entities
   * 
   * @param  date             filter by date
   * @param  earnerIdentityId filter by
   *                            {@link org.exoplatform.social.core.identity.model.Identity}
   *                            id
   * @return                  {@link List} of {@link RealizationDTO}
   */
  List<RealizationDTO> findRealizationsByDateAndIdentityId(Date date, String earnerIdentityId);

  /**
   * Retrieves Leaderboard rank of an earner
   * {@link org.exoplatform.social.core.identity.model.Identity} id and
   * {@link ProgramDTO} id for the given date
   * 
   * @param  earnerIdentityId
   * @param  date
   * @param  domainId
   * @return                  identity leaderboard rank in {@link Integer}
   */
  int getLeaderboardRank(String earnerIdentityId, Date date, Long domainId);

  /**
   * Compute User reputation score by Domain
   * 
   * @param  earnerIdentityId earner
   *                            {@link org.exoplatform.social.core.identity.model.Identity}
   *                            id
   * @return                  list of objects of type {@link ProfileReputation}
   */
  List<ProfileReputation> getScorePerDomainByIdentityId(String earnerIdentityId);

  /**
   * Creates new Realizations switch an event name for a given object identified
   * by its id and type
   * 
   * @param  event              {@link RuleDTO} event name
   * @param  earnerIdentityId   {@link org.exoplatform.social.core.identity.model.Identity}
   *                              id
   * @param  receiverIdentityId {@link org.exoplatform.social.core.identity.model.Identity}
   *                              id
   * @param  objectId           the designated object type identifier
   * @param  objectType         an object type, like 'activity', 'kudos' ...
   * @return                    list of created {@link RealizationDTO}
   */
  List<RealizationDTO> createRealizations(String event,
                                          String earnerIdentityId,
                                          String receiverIdentityId,
                                          String objectId,
                                          String objectType);

  /**
   * Updates an existing realization
   *
   * @param  realization             {@link RealizationDTO} object to update
   * @param  userAclIdentity         current identity {@link Identity}
   * @return                         {@link RealizationDTO}
   * @throws IllegalAccessException  when user is not authorized to update the
   *                                   realization
   * @throws ObjectNotFoundException when the realization identified by its
   *                                   technical identifier is not found
   */
  RealizationDTO updateRealization(RealizationDTO realization,
                                   Identity userAclIdentity) throws IllegalAccessException, ObjectNotFoundException;

  /**
   * Updates an existing realization
   *
   * @param  realization             {@link RealizationDTO} object to update
   * @return                         {@link RealizationDTO}
   * @throws ObjectNotFoundException when the realization identified by its
   *                                   technical identifier is not found
   */
  RealizationDTO updateRealization(RealizationDTO realization) throws ObjectNotFoundException;

  /**
   * Cancels an existing Realization with an event name for a given object
   * identified by its id and type
   * 
   * @param  event              {@link RuleDTO} event name
   * @param  earnerIdentityId   {@link org.exoplatform.social.core.identity.model.Identity}
   *                              id
   * @param  receiverIdentityId {@link org.exoplatform.social.core.identity.model.Identity}
   *                              id
   * @param  objectId           the designated object type identifier
   * @param  objectType         an object type, like 'activity', 'kudos' ...
   * @return                    list of cancelled {@link RealizationDTO}
   */
  List<RealizationDTO> cancelRealizations(String event,
                                          String earnerIdentityId,
                                          String receiverIdentityId,
                                          String objectId,
                                          String objectType);

  /**
   * Marks all realizations of a given object, generally due to deletion of
   * originating object
   * 
   * @param  objectId   the designated object type identifier
   * @param  objectType an object type, like 'activity', 'kudos' ...
   * @return            list of marked as deleted {@link RealizationDTO}
   */
  List<RealizationDTO> deleteRealizations(String objectId, String objectType);

  /**
   * Checks whether earner can create a realization on a designated rule at this
   * moment or not
   * 
   * @param  rule {@link RuleDTO}
   * @param  earnerIdentityId {@link org.exoplatform.social.core.identity.model.Identity} id
   * @return true if can create a new realization, else false
   */
  boolean canCreateRealization(RuleDTO rule, String earnerIdentityId);

  /**
   * Retrieves latest Achievement made by an
   * {@link org.exoplatform.social.core.identity.model.Identity} designated by
   * its id
   * 
   * @param  earnerIdentityId {@link org.exoplatform.social.core.identity.model.Identity}
   *                            id
   * @return                  {@link RealizationDTO}
   */
  RealizationDTO findLatestRealizationByIdentityId(String earnerIdentityId);

  /**
   * Retrieves identities total score between designated dates
   * 
   * @param  earnerIdentityId {@link org.exoplatform.social.core.identity.model.Identity}
   *                            id
   * @param  fromDate         From date
   * @param  toDate           End date
   * @return                  total score
   */
  long getScoreByIdentityIdAndBetweenDates(String earnerIdentityId, Date fromDate, Date toDate);

  /**
   * Retrieves {@link org.exoplatform.social.core.identity.model.Identity} total
   * score
   * 
   * @param  earnerIdentityId {@link org.exoplatform.social.core.identity.model.Identity}
   *                            id
   * @return                  total score
   */
  long getScoreByIdentityId(String earnerIdentityId);

  /**
   * Retrieves scores per doamin of a given
   * {@link org.exoplatform.social.core.identity.model.Identity}
   * 
   * @param  earnerIdentityId earner identity id
   * @param  startDate
   * @param  endDate
   * @return                  a list of object of type PiechartLeaderboard
   */
  List<PiechartLeaderboard> getStatsByIdentityId(String earnerIdentityId, Date startDate, Date endDate);

  /**
   * Retrieves Leaderboard switch designated {@link IdentityType} and between
   * two dates
   * 
   * @param  earnedType {@link IdentityType}
   * @param  fromDate   From date of type {@link Date}
   * @param  toDate     To date of type {@link Date}
   * @return            {@link List} of objects of type StandardLeaderboard
   */
  List<StandardLeaderboard> getLeaderboardBetweenDate(IdentityType earnedType, Date fromDate, Date toDate);

  /**
   * Retrieves Leaderboard switch designated filter
   * 
   * @param  filter of type {@link LeaderboardFilter}, used to filter query
   * @return        {@link List} of objects of type StandardLeaderboard
   */
  List<StandardLeaderboard> getLeaderboard(LeaderboardFilter filter);

  /**
   * Retrieves identities total score between designated dates
   * 
   * @param  earnerIdentityIds
   * @param  fromDate
   * @param  toDate
   * @return                   {@link Map} with
   *                           {@link org.exoplatform.social.core.identity.model.Identity}
   *                           id as key and score as value
   */
  Map<Long, Long> getScoresByIdentityIdsAndBetweenDates(List<String> earnerIdentityIds, Date fromDate, Date toDate);

  /**
   * Provided as an API from points n list to find gamification history from the
   * GamificationInformationsPortlet's earner earned points by date
   * 
   * @param  earnerIdentityId earner identity Id
   * @param  limit            limit entries to return
   * @return                  {@link List} of {@link RealizationDTO}
   */
  List<RealizationDTO> findRealizationsByIdentityId(String earnerIdentityId, int limit);

}

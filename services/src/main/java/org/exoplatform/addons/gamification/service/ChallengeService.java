package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;

public interface ChallengeService {

  /**
   * Creates a new challenge
   *
   * @param challenge {@link Challenge} object to create
   * @param username User name creating challenge
   * @return created {@link Challenge} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *           challenge for the designated owner defined in object
   */
  Challenge createChallenge(Challenge challenge, String username) throws IllegalAccessException;

  /**
   * Creates a new challenge
   *
   * @param challenge {@link Challenge} object to create
   * @return created {@link Challenge} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *           challenge for the designated owner defined in object
   */
  Challenge createChallenge(Challenge challenge);

  /**
   * Retrieves a challenge identified by its technical identifier.
   *
   * @param challengeId technical identifier of a challenge
   * @param username User name accessing challenge
   * @return A {@link Challenge} object
   * @throws IllegalAccessException when user is not authorized to access
   *           challenge
   */
  Challenge getChallengeById(long challengeId, String username) throws IllegalAccessException;

  /**
   * Updates an existing challenge
   *
   * @param  challenge {@link Challenge} object to update
   * @param username User name updating challenge
   * @throws IllegalArgumentException when user is not authorized to update the
   *           challenge
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   * @throws IllegalAccessException when user is not authorized to create a
   *           challenge for the designated owner defined in object
   */
  Challenge updateChallenge(Challenge challenge, String username) throws  IllegalArgumentException, ObjectNotFoundException, IllegalAccessException;

  /**
   * Return a boolean that indicates if the current user can add a challenge or not
   *
   * @return if the user can add a challenge or not    */
  boolean canAddChallenge();

  /**
   * Retrieves all challenges by user.
   * @param offset Offset
   * @param limit Limit
   * @param username User name accessing challenges
   * @return A {@link List <Challenge>} object
   */
  List<Challenge> getChallengesByUser(int offset, int limit, String username);

  /**
   * Retrieves all challenges by user and a selected domain identified by its
   * technical id
   * 
   * @param domainId technical identifier
   * @param offset index of the search
   * @param limit limit of results to return
   * @param username User name accessing challenges
   * @return {@link List} of {@link Challenge}
   */
  List<Challenge> getChallengesByUserAndDomain(long domainId, int offset, int limit, String username);

  /**
   * Count all challenges by user and a selected domain identified by its
   * technical id
   * 
   * @param domainId technical identifier
   * @param username Username accessing challenges
   * @return challenges count
   */
  int countChallengesByUserAndDomain(long domainId, String username);

  /**
   * clear challenges cache.
   */
  void clearUserChallengeCache();

  /**
   * Retrieves all challenges by user.
   * 
   * @param challengeId Offset
   * @param username Username who want to delete challenge
   * @throws IllegalAccessException when user is not authorized to delete
   *           challenges
   * @throws ObjectNotFoundException challenge not found
   * @throws IllegalArgumentException when challenge has announcements or did not
   *           ended yet
   */
  void deleteChallenge(Long challengeId, String username) throws IllegalAccessException, ObjectNotFoundException;

}

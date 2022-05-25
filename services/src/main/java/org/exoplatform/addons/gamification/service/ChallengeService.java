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
   * Retrieves a challenge identified by its technical identifier.
   *
   * @param challengeId technical identifier of a challenge
   * @return A {@link Challenge} object
   */
  Challenge getChallengeById(long challengeId);

  /**
   * Updates an existing challenge
   *
   * @param challenge {@link Challenge} object to update
   * @param username Username updating challenge
   * @throws IllegalArgumentException when user is not authorized to update the
   *           challenge
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   * @throws IllegalAccessException when user is not authorized to create a
   *           challenge for the designated owner defined in object
   */
  Challenge updateChallenge(Challenge challenge,
                            String username) throws IllegalArgumentException, ObjectNotFoundException, IllegalAccessException;

  /**
   * Return a boolean that indicates if the current user can add a challenge or
   * not
   *
   * @return if the user can add a challenge or not
   */
  boolean canAddChallenge();

  /**
   * Retrieves all challenges by user.
   * 
   * @param offset Offset
   * @param limit Limit
   * @param username Username accessing challenge
   * @return A {@link List <Challenge>} object
   * @throws IllegalAccessException when user is not authorized to access
   *           challenges
   * @throws Exception can't get list of spaces
   */
  List<Challenge> getAllChallengesByUser(int offset, int limit, String username) throws Exception;

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

  /**
   * Search news by term
   *
   * @param term term
   * @param offset Offset
   * @param limit Limit
   * @param username Username accessing challenge
   * @throws IllegalAccessException when user is not authorized to delete
   *           challenges
   * @throws ObjectNotFoundException challenge not found
   * @throws IllegalArgumentException when challenge has announcements or did not
   *           ended yet
   */
  List<Challenge> search(String term, int offset, int limit, String username);

  /**
   * Retrieves all challenges .
   **/
  List<Challenge> getAllChallenges(int offset, int limit);
}

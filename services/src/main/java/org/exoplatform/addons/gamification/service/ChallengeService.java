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
   * @param username Username updating challenge
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
   * @param currentUser
   * @return if the user can add a challenge or not    */
  boolean canAddChallenge(String currentUser) throws Exception;

  /**
   * Retrieves all challenges by user.
   * @param offset Offset
   * @param limit Limit
   * @param username User name accessing challenge
   * @return A {@link List <Challenge>} object
   * @throws IllegalAccessException when user is not authorized to access
   *           challenges
   */
  List<Challenge> getAllChallengesByUser(int offset, int limit, String username) throws IllegalAccessException;
}

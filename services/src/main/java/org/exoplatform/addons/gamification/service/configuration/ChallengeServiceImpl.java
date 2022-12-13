package org.exoplatform.addons.gamification.service.configuration;

import static org.exoplatform.addons.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_DELETE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class ChallengeServiceImpl implements ChallengeService {

  private static final String CHALLENGE_IS_MANDATORY_MESSAGE = "Challenge is mandatory";

  public static final String  ENGAGEMENT_CENTER_FEATURE_NAME = "engagementCenter";

  private static final Log    LOG                            = ExoLogger.getExoLogger(ChallengeServiceImpl.class);

  private ChallengeStorage    challengeStorage;

  private SpaceService        spaceService;

  private ExoFeatureService   featureService;

  private ListenerService     listenerService;

  public ChallengeServiceImpl(ChallengeStorage challengeStorage,
                              SpaceService spaceService,
                              ExoFeatureService featureService,
                              ListenerService listenerService) {
    this.challengeStorage = challengeStorage;
    this.spaceService = spaceService;
    this.featureService = featureService;
    this.listenerService = listenerService;
  }

  @Override
  public Challenge createChallenge(Challenge challenge, String username) throws IllegalAccessException {
    if (challenge == null) {
      throw new IllegalArgumentException(CHALLENGE_IS_MANDATORY_MESSAGE);
    }
    if (challenge.getId() != 0) {
      throw new IllegalArgumentException("challenge id must be equal to 0");
    }
    checkChallengePermissionAndDates(challenge, username);
    challenge = challengeStorage.saveChallenge(challenge, username);
    try {
      listenerService.broadcast(POST_CREATE_RULE_EVENT, this, challenge.getId());
    } catch (Exception e) {
      LOG.error("Error broadcasting challenge with id {} creation event", challenge.getId(), e);
    }
    return challenge;
  }

  @Override
  public Challenge createChallenge(Challenge challenge) {
    if (challenge == null) {
      throw new IllegalArgumentException(CHALLENGE_IS_MANDATORY_MESSAGE);
    }
    applyDomainAttributes(challenge, true);
    return challengeStorage.saveChallenge(challenge, Utils.SYSTEM_USERNAME);
  }

  @Override
  public Challenge getChallengeById(long challengeId, String username) throws IllegalAccessException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = buildChallengeById(challengeId);
    if (challenge == null) {
      return null;
    }
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isNotBlank(idSpace)) {
      Space space = spaceService.getSpaceById(idSpace);
      if (space != null
          && !spaceService.isMember(space, username)
          && !Utils.isSuperManager(username)) {
        throw new IllegalAccessException("user is not allowed to access to the challenge");
      }
    }
    return challenge;
  }

  @Override
  public Challenge getChallengeById(long challengeId) {
    return buildChallengeById(challengeId);
  }

  @Override
  public Challenge updateChallenge(Challenge challenge, String username) throws IllegalArgumentException,
                                                                         ObjectNotFoundException,
                                                                         IllegalAccessException {
    if (challenge == null) {
      throw new IllegalArgumentException(CHALLENGE_IS_MANDATORY_MESSAGE);
    }
    if (challenge.getId() == 0) {
      throw new IllegalArgumentException("challenge id must not be equal to 0");
    }
    Challenge oldChallenge = buildChallengeById(challenge.getId());
    if (oldChallenge == null) {
      throw new ObjectNotFoundException("challenge is not exist");
    }
    checkChallengePermissionAndDates(oldChallenge, username); // Test if user
                                                              // was manager
    checkChallengePermissionAndDates(challenge, username); // Test if user is
                                                           // remaining manager
    challenge = challengeStorage.saveChallenge(challenge, username);
    try {
      listenerService.broadcast(POST_UPDATE_RULE_EVENT, this, challenge.getId());
    } catch (Exception e) {
      LOG.error("Error broadcasting challenge with id {} update event", challenge.getId(), e);
    }
    return challenge;
  }

  @Override
  public boolean canAddChallenge(org.exoplatform.services.security.Identity identity) {
    return Utils.isSuperManager(identity.getUserId());
  }

  @Override
  public void deleteChallenge(Long challengeId, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = buildChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge doesn't exist");
    }
    if (!Utils.isChallengeManager(challenge, challenge.getAudience(), username)) {
      throw new IllegalAccessException("User is not allowed to delete challenge with id " + challenge.getId());
    }
    if (Utils.countAnnouncementsByChallenge(challengeId) > 0) {
      throw new IllegalArgumentException("challenge already have announcements");
    }
    Date endDate = Utils.parseSimpleDate(challenge.getEndDate());
    Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

    if (endDate.after(currentDate) || endDate.equals(currentDate)) {
      throw new IllegalArgumentException("Challenge does not ended yet");
    }
    challengeStorage.deleteChallenge(challengeId, username);
    try {
      listenerService.broadcast(POST_DELETE_RULE_EVENT, this, challengeId);
    } catch (Exception e) {
      LOG.error("Error broadcasting chanllenge with id {} deletion event", challenge.getId(), e);
    }
  }

  @Override
  public List<Challenge> getChallengesByFilterAndUser(RuleFilter challengeFilter, int offset, int limit, String username) {
    List<String> spaceIds = spaceService.getMemberSpacesIds(username, 0, -1);
    if (spaceIds.isEmpty()) {
      return Collections.emptyList();
    }
    setFilterAudience(challengeFilter, spaceIds);
    List<Long> challengesIds = null;
    if (challengeFilter.isOrderByRealizations()) {
      challengesIds = challengeStorage.findMostRealizedChallengesIds(challengeFilter.getSpaceIds(), offset, limit);
    } else {
      challengesIds = challengeStorage.findChallengesIdsByFilter(challengeFilter, offset, limit);
    }
    List<Challenge> challenges = new ArrayList<>();
    for (Long challengeId : challengesIds) {
      Challenge challenge = getChallengeById(challengeId);
      challenges.add(challenge);
    }
    return challenges;
  }

  @Override
  public int countChallengesByFilterAndUser(RuleFilter challengeFilter, String username) {
    List<String> spaceIds = spaceService.getMemberSpacesIds(username, 0, -1);
    if (spaceIds.isEmpty()) {
      return 0;
    }
    setFilterAudience(challengeFilter, spaceIds);
    return challengeStorage.countChallengesByFilter(challengeFilter);
  }

  @Override
  public void clearUserChallengeCache() {
    challengeStorage.clearCache();
  }

  @Override
  public boolean isEngagementCenterEnabled() {
    return featureService.isActiveFeature(ENGAGEMENT_CENTER_FEATURE_NAME);
  }

  private Challenge buildChallengeById(long challengeId) {
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge != null) {
      applyDomainAttributes(challenge, false);
    }
    return challenge;
  }

  @SuppressWarnings("unchecked")
  private void setFilterAudience(RuleFilter challengeFilter, List<String> spaceIds) {
    List<Long> userSpaceIds = spaceIds.stream().map(Long::parseLong).collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(challengeFilter.getSpaceIds())) {
      userSpaceIds = (List<Long>) CollectionUtils.intersection(userSpaceIds, challengeFilter.getSpaceIds());
    }
    challengeFilter.setSpaceIds(userSpaceIds);
  }

  private void checkChallengePermissionAndDates(Challenge challenge, String username) throws IllegalAccessException {
    applyDomainAttributes(challenge, true);
    if (!Utils.isChallengeManager(challenge, username)) {
      if (challenge.getId() > 0) {
        throw new IllegalAccessException("User " + username + " is not allowed to update challenge with id " + challenge.getId());
      } else {
        throw new IllegalAccessException("User " + username
            + " is not allowed to create challenge that he/she can't manage");
      }
    }
    Date startDate = Utils.parseSimpleDate(challenge.getStartDate());
    Date endDate = Utils.parseSimpleDate(challenge.getEndDate());
    if (startDate != null && endDate != null && endDate.compareTo(startDate) <= 0) {
      throw new IllegalStateException("endDate must be greater than startDate");
    }
  }

  @SuppressWarnings("deprecation")
  private void applyDomainAttributes(Challenge challenge, boolean throwWhenNotfound) {
    DomainDTO domain = Utils.getChallengeDomainDTO(challenge);
    if (domain == null) {
      if (challenge.getAudience() == 0 && throwWhenNotfound) {
        // Should be ObjectNotFoundException, but avoid changing API signature
        throw new IllegalArgumentException("Audience is Mandatory");
      }
    } else {
      challenge.setProgramId(domain.getId());
      challenge.setProgram(domain.getTitle());

      if (isEngagementCenterEnabled()) {
        if (CollectionUtils.isEmpty(domain.getOwners())) {
          challenge.setManagers(Collections.emptyList());
        } else {
          challenge.setManagers(new ArrayList<>(domain.getOwners()));
        }
        challenge.setAudience(domain.getAudienceId());
      }
    }
  }

}

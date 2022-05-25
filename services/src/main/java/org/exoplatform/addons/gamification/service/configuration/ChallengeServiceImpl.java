package org.exoplatform.addons.gamification.service.configuration;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.search.ChallengeSearchConnector;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.ChallengeSearchEntity;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import static org.exoplatform.addons.gamification.utils.Utils.*;

public class ChallengeServiceImpl implements ChallengeService {

  private static final Log         LOG                = ExoLogger.getExoLogger(ChallengeServiceImpl.class);

  private RuleStorage              challengeStorage;

  private SpaceService             spaceService;

  private String                   groupOfCreators;

  private static final String      CREATORS_GROUP_KEY = "challenge.creator.group";

  private ListenerService          listenerService;

  private ChallengeSearchConnector challengeSearchConnector;

  public ChallengeServiceImpl(RuleStorage challengeStorage,
                              SpaceService spaceService,
                              InitParams params,
                              ListenerService listenerService,
                              ChallengeSearchConnector challengeSearchConnector) {
    this.challengeStorage = challengeStorage;
    this.spaceService = spaceService;
    this.listenerService = listenerService;
    this.challengeSearchConnector = challengeSearchConnector;
    if (params != null && params.containsKey(CREATORS_GROUP_KEY)) {
      this.groupOfCreators = params.getValueParam(CREATORS_GROUP_KEY).getValue();
    }
  }

  @Override
  public Challenge createChallenge(Challenge challenge, String username) throws IllegalAccessException {
    if (challenge == null) {
      throw new IllegalArgumentException("Challenge is mandatory");
    }
    if (challenge.getId() != 0) {
      throw new IllegalArgumentException("challenge id must be equal to 0");
    }
    Identity identity = Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, username);
    if (identity == null) {
      throw new IllegalArgumentException("identity is not exist");
    }
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isBlank(idSpace)) {
      throw new IllegalArgumentException("space id must not be null or empty");
    }
    Date startDate = Utils.parseSimpleDate(challenge.getStartDate());
    Date endDate = Utils.parseSimpleDate(challenge.getEndDate());
    if (startDate != null && endDate != null && endDate.compareTo(startDate) <= 0) {
      throw new IllegalArgumentException("endDate must be greater than startDate");
    }
    Space space = spaceService.getSpaceById(idSpace);
    if (!spaceService.isManager(space, username)) {
      throw new IllegalAccessException("user is not allowed to create challenge");
    }
    challenge = challengeStorage.saveChallenge(challenge, username);
    try {
      listenerService.broadcast(POST_CREATE_CHALLENGE_EVENT, this, challenge.getId());
    } catch (Exception e) {
      LOG.error("Unexpected error", e);
    }
    return challenge;
  }

  @Override
  public Challenge createChallenge(Challenge challenge) {
    return challengeStorage.saveChallenge(challenge, "SYSTEM");
  }

  @Override
  public Challenge getChallengeById(long challengeId, String username) throws IllegalAccessException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge == null) {
      return null;
    }
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isBlank(idSpace)) {
      throw new IllegalArgumentException("space id must not be null or empty");
    }
    Space space = spaceService.getSpaceById(idSpace);
    if (!spaceService.isManager(space, username) && !spaceService.isMember(space, username)) {
      throw new IllegalAccessException("user is not allowed to access to the challenge");
    }
    return challenge;
  }

  @Override
  public Challenge getChallengeById(long challengeId) {
    return challengeStorage.getChallengeById(challengeId);
  }

  @Override
  public Challenge updateChallenge(Challenge challenge, String username) throws IllegalArgumentException,
                                                                         ObjectNotFoundException,
                                                                         IllegalAccessException {
    if (challenge == null) {
      throw new IllegalArgumentException("Challenge is mandatory");
    }
    if (challenge.getId() == 0) {
      throw new IllegalArgumentException("challenge id must not be equal to 0");
    }
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isBlank(idSpace)) {
      throw new IllegalArgumentException("space id must not be null or empty");
    }
    Space space = spaceService.getSpaceById(idSpace);
    if (!spaceService.isManager(space, username)) {
      throw new IllegalAccessException("user is not allowed to modify challenge");
    }
    Challenge oldChallenge = challengeStorage.getChallengeById(challenge.getId());
    if (oldChallenge == null) {
      throw new ObjectNotFoundException("challenge is not exist");
    }
    if (oldChallenge.equals(challenge)) {
      throw new IllegalArgumentException("there are no changes to save");
    }
    challenge = challengeStorage.saveChallenge(challenge, username);
    try {
      listenerService.broadcast(POST_UPDATE_CHALLENGE_EVENT, this, challenge.getId());
    } catch (Exception e) {
      LOG.error("Unexpected error", e);
    }
    return challenge;
  }

  @Override
  public boolean canAddChallenge()  {
    if (groupOfCreators != null){
      return ConversationState.getCurrent().getIdentity().isMemberOf(groupOfCreators);
    }
    return false;
  }

  @Override
  public List<Challenge> getChallengesByUser(int offset, int limit, String userName) {
    if (StringUtils.isBlank(userName)) {
      throw new IllegalArgumentException("user name must not be null");
    }
    List<String> listIdSpace = spaceService.getMemberSpacesIds(userName, 0, -1);
    if (listIdSpace.isEmpty()) {
      return Collections.emptyList();
    }
    List<RuleEntity> challengeEntities = challengeStorage.findAllChallengesByUser(offset,
                                                                                  limit,
                                                                                  listIdSpace.stream()
                                                                                             .map(Long::parseLong)
                                                                                             .collect(Collectors.toList()));
    return EntityMapper.fromChallengeEntities(challengeEntities);
  }

  @Override
  public List<Challenge> getChallengesByUserAndDomain(long domainId, int offset, int limit, String userName) {
    if (StringUtils.isBlank(userName)) {
      throw new IllegalArgumentException("user name must not be null");
    }
    List<String> listIdSpace = spaceService.getMemberSpacesIds(userName, 0, -1);
    if (listIdSpace.isEmpty()) {
      return Collections.emptyList();
    }
    List<RuleEntity> challengeEntities = challengeStorage.findAllChallengesByUserByDomain(domainId,
                                                                                          offset,
                                                                                          limit,
                                                                                          listIdSpace.stream()
                                                                                                     .map(Long::parseLong)
                                                                                                     .collect(Collectors.toList()));
    return EntityMapper.fromChallengeEntities(challengeEntities);
  }

  @Override
  public int countChallengesByUserAndDomain(long domainId, String userName) {
    if (StringUtils.isBlank(userName)) {
      throw new IllegalArgumentException("user name must not be null");
    }
    List<String> listIdSpace = spaceService.getMemberSpacesIds(userName, 0, -1);
    if (listIdSpace.isEmpty()) {
      return 0;
    }
    return challengeStorage.countAllChallengesByUserByDomain(domainId,
                                                            listIdSpace.stream()
                                                                       .map(Long::parseLong)
                                                                       .collect(Collectors.toList()));
  }

  @Override
  public void clearUserChallengeCache() {
    challengeStorage.clearCache();
  }

  @Override
  public void deleteChallenge(Long challengeId, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge is not exist");
    }
    if (!Utils.canEditChallenge(challenge.getManagers(), String.valueOf(challenge.getAudience()))) {
      throw new IllegalAccessException("your are not able to delete challenge");
    }
    if (Utils.countAnnouncementsByChallenge(challengeId) > 0) {
      throw new IllegalArgumentException("challenge already have announcements");
    }
    Date endDate = Utils.parseSimpleDate(challenge.getEndDate());
    Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

    if (endDate.after(currentDate) || endDate.equals(currentDate)) {
      throw new IllegalArgumentException("Challenge does not ended yet");
    }
    challengeStorage.deleteChallenge(challenge);
    try {
      listenerService.broadcast(POST_DELETE_CHALLENGE_EVENT, this, challenge.getId());
    } catch (Exception e) {
      LOG.error("Unexpected error", e);
    }
  }

  @Override
  public List<ChallengeSearchEntity> search(String term, Long domainId, int offset, int limit, String username) {
    List<String> listIdSpace = spaceService.getMemberSpacesIds(username, 0, -1);
    if (listIdSpace.isEmpty()) {
      return Collections.emptyList();
    }
    return challengeSearchConnector.search(listIdSpace.stream().map(String::valueOf).collect(Collectors.toSet()),
                                           term,
                                           domainId,
                                           offset,
                                           limit);
  }

  @Override
  public List<Challenge> getAllChallenges(int offset, int limit) {
    return challengeStorage.getAllChallenges(offset, limit);
  }
}

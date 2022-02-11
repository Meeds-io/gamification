package org.exoplatform.addons.gamification.service.configuration;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChallengeServiceImpl implements ChallengeService {

  private ChallengeStorage challengeStorage;

  private SpaceService     spaceService;

  private String     groupOfCreators;

  private static final String     CREATORS_GROUP_KEY             = "challenge.creator.group";


  public ChallengeServiceImpl(ChallengeStorage challengeStorage, SpaceService spaceService, InitParams params) {
    this.challengeStorage = challengeStorage;
    this.spaceService = spaceService;
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
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isBlank(idSpace)) {
      throw new IllegalArgumentException("space id must not be null or empty");
    }
    Space space = spaceService.getSpaceById(idSpace);
    if (!spaceService.isManager(space, username)) {
      throw new IllegalAccessException("user is not allowed to create challenge");
    }
    return challengeStorage.saveChallenge(challenge, username);
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
    return challengeStorage.saveChallenge(challenge, username);
  }

  @Override
  public boolean canAddChallenge()  {
    if (groupOfCreators != null){
      return ConversationState.getCurrent().getIdentity().isMemberOf(groupOfCreators);
    }
    return false;
  }

  @Override
  public List<Challenge> getAllChallengesByUser(int offset, int limit, String username) throws Exception {
    if (StringUtils.isBlank(username)) {
      throw new IllegalAccessException("user name must not be null");
    }
    List<Long> listIdSpace = new ArrayList<>();
    ListAccess<Space> userSpaces = spaceService.getMemberSpaces(username);
    int spacesSize = userSpaces.getSize();
    int offsetToFetch = 0;
    int limitToFetch = spacesSize > 20 ? 20 : spacesSize;
    while (limitToFetch > 0) {
      Space[] spaces = userSpaces.load(offsetToFetch, limitToFetch);
      Arrays.stream(spaces).forEach(space -> {
        listIdSpace.add(Long.valueOf(space.getId()));
      });
      offsetToFetch += limitToFetch;
      limitToFetch = (spacesSize - offsetToFetch) > 20 ? 20 : (spacesSize - offsetToFetch);
    }
    if (listIdSpace.isEmpty()) {
      return Collections.emptyList();
    }
    List<RuleEntity> challengeEntities = challengeStorage.findAllChallengesByUser(offset, limit, listIdSpace);
    return EntityMapper.fromChallengeEntities(challengeEntities);
  }
}

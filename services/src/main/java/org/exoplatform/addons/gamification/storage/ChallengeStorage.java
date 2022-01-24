package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;

import java.util.List;

public class ChallengeStorage {

  private RuleDAO ruleDAO;

  public ChallengeStorage(RuleDAO ruleDAO) {
    this.ruleDAO = ruleDAO;
  }

  public Challenge saveChallenge(Challenge challenge, String username) throws IllegalArgumentException {
    if (challenge == null) {
      throw new IllegalArgumentException("challenge argument is null");
    }
    Identity identity = Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, username);
    if (identity == null) {
      throw new IllegalArgumentException("identity is not exist");
    }
    RuleEntity challengeEntity = EntityMapper.toEntity(challenge);
    if (challengeEntity.getEndDate().compareTo(challengeEntity.getStartDate()) < 0
        || challengeEntity.getEndDate().equals(challengeEntity.getStartDate())) {
      throw new IllegalArgumentException("endDate must be greater than startDate");
    }
    if (challenge.getId() == 0) {
      challengeEntity.setId(null);
      challengeEntity = ruleDAO.create(challengeEntity);
    } else {
      challengeEntity = ruleDAO.update(challengeEntity);
    }

    return EntityMapper.fromEntity(challengeEntity);
  }

  public Challenge getChallengeById(long challengeId) {
    RuleEntity challengeEntity = this.ruleDAO.find(challengeId);
    return EntityMapper.fromEntity(challengeEntity);
  }

  public List<RuleEntity> findAllChallengesByUser(int offset, int limit, List<Long> ids) {
    return ruleDAO.findAllChallengesByUser(offset, limit, ids);
  }

}

package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
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

  public Challenge saveChallenge(Challenge challenge, String username) {
    RuleEntity challengeEntity = EntityMapper.toEntity(challenge);

    if (challenge.getId() == 0) {
      challengeEntity.setId(null);
      challengeEntity.setCreatedBy(username);
      challengeEntity.setType(TypeRule.MANUAL);
      challengeEntity.setEvent(challengeEntity.getTitle());
      challengeEntity = ruleDAO.create(challengeEntity);
    } else {
      RuleEntity ruleEntity = ruleDAO.find(challengeEntity.getId());
      challengeEntity.setCreatedBy(ruleEntity.getCreatedBy());
      challengeEntity.setType(ruleEntity.getType());
      challengeEntity.setEvent(ruleEntity.getEvent());
      challengeEntity = ruleDAO.update(challengeEntity);
    }

    return EntityMapper.fromEntity(challengeEntity);
  }

  public Challenge getChallengeById(long challengeId) {
    RuleEntity challengeEntity = this.ruleDAO.find(challengeId);
    if(challengeEntity.getType() == TypeRule.AUTOMATIC) {
      return null ;
    }
    return EntityMapper.fromEntity(challengeEntity);
  }

  public List<RuleEntity> findAllChallengesByUser(int offset, int limit, List<Long> ids) {
    return ruleDAO.findAllChallengesByUser(offset, limit, ids);
  }

}

package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;

import java.util.Date;
import java.util.List;

public class RuleStorage {

  private RuleDAO ruleDAO;

  public RuleStorage(RuleDAO ruleDAO) {
    this.ruleDAO = ruleDAO;
  }

  public RuleDTO saveRule(RuleDTO ruleDTO) {
    RuleEntity ruleEntity = RuleMapper.ruleDTOToRule(ruleDTO);
    if (ruleEntity.getId() == null) {
      ruleEntity = ruleDAO.create(ruleEntity);
    } else if (!ruleEntity.isDeleted()) {
      ruleEntity.setLastModifiedDate(new Date());
      ruleEntity = ruleDAO.update(ruleEntity);
    }
    return RuleMapper.ruleToRuleDTO(ruleEntity);
  }

  public RuleDTO findEnableRuleByTitle(String ruleTitle) {
    return RuleMapper.ruleToRuleDTO(ruleDAO.findEnableRuleByTitle(ruleTitle));
  }

  public RuleDTO findRuleById(Long id) {
    return RuleMapper.ruleToRuleDTO(ruleDAO.find(id));
  }

  public List<RuleDTO> findEnabledRulesByEvent(String event) {
    List<RuleEntity> entities = ruleDAO.findEnabledRulesByEvent(event);
    return RuleMapper.rulesToRuleDTOs(entities);

  }

  public RuleDTO findRuleByTitle(String ruleTitle) {
    return RuleMapper.ruleToRuleDTO(ruleDAO.findRuleByTitle(ruleTitle));
  }

  public RuleDTO findRuleByEventAndDomain(String event, String domain) {
    return RuleMapper.ruleToRuleDTO(ruleDAO.findRuleByEventAndDomain(event, domain));
  }

  public List<RuleDTO> getAllAutomaticRules() {
    return RuleMapper.rulesToRuleDTOs(ruleDAO.getAllAutomaticRules());
  }

  public List<RuleDTO> findAllRules() {
    return RuleMapper.rulesToRuleDTOs(ruleDAO.getAllRules());
  }

  public List<RuleDTO> getActiveRules() {
    return RuleMapper.rulesToRuleDTOs(ruleDAO.getActiveRules());
  }

  public List<RuleDTO> getAllRulesByDomain(String domain) {
    return RuleMapper.rulesToRuleDTOs(ruleDAO.getAllRulesByDomain(domain));
  }

  public List<RuleDTO> getAllRulesWithNullDomain() {
    return RuleMapper.rulesToRuleDTOs(ruleDAO.getAllRulesWithNullDomain());
  }

  public List<String> getAllEvents() {
    return ruleDAO.getAllEvents();
  }

  public List<String> getDomainListFromRules() {
    return ruleDAO.getDomainList();
  }

  public void deleteRule(RuleDTO rule) {
    RuleEntity ruleEntity = RuleMapper.ruleDTOToRule(rule);
    ruleDAO.update(ruleEntity);
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
    if (challengeEntity == null || challengeEntity.getType() == TypeRule.AUTOMATIC) {
      return null;
    }
    return EntityMapper.fromEntity(challengeEntity);
  }

  public List<RuleEntity> findAllChallengesByUser(int offset, int limit, List<Long> ids) {
    return ruleDAO.findAllChallengesByUser(offset, limit, ids);
  }

  public void clearCache() { // NOSONAR
    // implemented in cached storage
  }

}

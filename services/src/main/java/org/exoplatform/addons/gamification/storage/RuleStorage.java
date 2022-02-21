package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
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

}

package org.exoplatform.addons.gamification.storage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.search.RuleSearchConnector;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.exception.ObjectNotFoundException;

public class RuleStorage {

  private RuleSearchConnector ruleSearchConnector;

  private RuleDAO             ruleDAO;

  public RuleStorage(RuleDAO ruleDAO, RuleSearchConnector ruleSearchConnector) {
    this.ruleSearchConnector = ruleSearchConnector;
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

  public List<RuleDTO> findRulesByFilter(RuleFilter ruleFilter, int offset, int limit) {
    if (StringUtils.isBlank(ruleFilter.getTerm())) {
      List<RuleEntity> ruleEntities = ruleDAO.findRulesByFilter(ruleFilter, offset, limit);
      return ruleEntities.stream().map(RuleMapper::ruleToRuleDTO).collect(Collectors.toList());
    } else {
     return  ruleSearchConnector.search(ruleFilter, offset, limit);
    }
  }

  public int countRulesByFilter(RuleFilter ruleFilter) {
    if (StringUtils.isBlank(ruleFilter.getTerm())) {
      return ruleDAO.countRulesByFilter(ruleFilter);
    } else {
      return  ruleSearchConnector.count(ruleFilter, 0, 10);
    }
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

  public void deleteRule(long ruleId) throws ObjectNotFoundException {
    deleteRule(ruleId, true);
  }

  public RuleDTO deleteRule(long ruleId, boolean force) throws ObjectNotFoundException {
    RuleEntity ruleEntity = ruleDAO.find(ruleId);
    if (ruleEntity == null) {
      throw new ObjectNotFoundException("Rule with id " + ruleId + " does not exist");
    }
    if (force) {
      ruleDAO.delete(ruleEntity);
    } else {
      ruleEntity.setDeleted(true);
      ruleDAO.update(ruleEntity);
    }
    return RuleMapper.ruleToRuleDTO(ruleEntity);
  }

  public void clearCache() { // NOSONAR
    // implemented in cached storage
  }

}

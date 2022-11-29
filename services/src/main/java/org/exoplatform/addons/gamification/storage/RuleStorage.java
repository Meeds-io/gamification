package org.exoplatform.addons.gamification.storage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.search.RuleSearchConnector;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;

public class RuleStorage {

  private RuleSearchConnector    ruleSearchConnector;

  private RuleDAO                ruleDAO;

  private GamificationHistoryDAO gamificationHistoryDAO;

  public RuleStorage(RuleDAO ruleDAO, RuleSearchConnector ruleSearchConnector, GamificationHistoryDAO gamificationHistoryDAO) {
    this.ruleSearchConnector = ruleSearchConnector;
    this.ruleDAO = ruleDAO;
    this.gamificationHistoryDAO = gamificationHistoryDAO;
  }

  public RuleDTO saveRule(RuleDTO ruleDTO) {
    RuleEntity ruleEntity = RuleMapper.ruleDTOToRule(ruleDTO);
    ruleEntity.setLastModifiedDate(new Date());
    DomainDTO domainDTO = ruleDTO.getDomainDTO();
    if (domainDTO != null) {
      DomainEntity domainEntity = Utils.getDomainById(domainDTO.getId());
      ruleEntity.setDomainEntity(domainEntity);
    }
    if (ruleEntity.getId() == null) {
      ruleEntity.setCreatedDate(new Date());
      ruleEntity = ruleDAO.create(ruleEntity);
    } else if (!ruleEntity.isDeleted()) {
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

  public RuleDTO findRuleByEventAndDomain(String event, long domainId) {
    return RuleMapper.ruleToRuleDTO(ruleDAO.findRuleByEventAndDomain(event, domainId));
  }

  public List<Long> findRulesIdsByFilter(RuleFilter ruleFilter, int offset, int limit) {
    List<Long> rulesIds;
    if (StringUtils.isBlank(ruleFilter.getTerm())) {
      rulesIds = ruleDAO.findRulesIdsByFilter(ruleFilter, offset, limit);
    } else {
      rulesIds =
               ruleSearchConnector.search(ruleFilter, offset, limit).stream().map(RuleEntity::getId).collect(Collectors.toList());
    }
    return rulesIds;
  }

  public int countRulesByFilter(RuleFilter ruleFilter) {
    if (ruleFilter == null) {
      return ruleDAO.count().intValue();
    } else if (StringUtils.isBlank(ruleFilter.getTerm())) {
      return ruleDAO.countRulesByFilter(ruleFilter);
    } else {
      return ruleSearchConnector.count(ruleFilter);
    }
  }

  public List<RuleDTO> findAllRules() {
    return RuleMapper.rulesToRuleDTOs(ruleDAO.getAllRules());
  }

  public List<Long> findAllRulesIds(int offset, int limit) {
    return ruleDAO.findRulesIdsByFilter(new RuleFilter(), offset, limit);
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

  public long getRulesTotalScoreByDomain(long domainId) {
    return ruleDAO.getRulesTotalScoreByDomain(domainId);
  }

  public RuleDTO deleteRuleById(long ruleId, String userId) throws ObjectNotFoundException {
    return deleteRuleById(ruleId, userId, false);
  }

  public RuleDTO deleteRuleById(long ruleId, String userId, boolean force) throws ObjectNotFoundException {
    RuleEntity ruleEntity = ruleDAO.find(ruleId);
    if (ruleEntity == null) {
      throw new ObjectNotFoundException("Rule with id " + ruleId + " does not exist");
    }
    if (force) {
      ruleDAO.delete(ruleEntity);
    } else {
      ruleEntity.setLastModifiedBy(userId);
      ruleEntity.setLastModifiedDate(new Date());
      ruleEntity.setDeleted(true);
      ruleDAO.update(ruleEntity);
    }
    return RuleMapper.ruleToRuleDTO(ruleEntity);
  }

  public List<Long> findMostRealizedRuleIds(int offset, int limit, EntityType type) {
    return gamificationHistoryDAO.findMostRealizedRuleIds(offset, limit, type);
  }

  public void clearCache() { // NOSONAR
    // implemented in cached storage
  }

}

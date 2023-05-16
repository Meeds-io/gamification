package io.meeds.gamification.storage;

import java.util.Date;
import java.util.List;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.utils.RuleBuilder;

public class RuleStorage {

  private ProgramStorage programStorage;

  private ProgramDAO     programDAO;

  private RuleDAO        ruleDAO;

  public RuleStorage(ProgramStorage programStorage,
                     ProgramDAO programDAO,
                     RuleDAO ruleDAO) {
    this.programStorage = programStorage;
    this.ruleDAO = ruleDAO;
    this.programDAO = programDAO;
  }

  public RuleDTO saveRule(RuleDTO ruleDTO) {
    RuleEntity ruleEntity = RuleBuilder.toEntity(ruleDTO);
    ruleEntity.setLastModifiedDate(new Date());
    ProgramDTO program = ruleDTO.getProgram();
    if (program != null) {
      ProgramEntity domainEntity = programDAO.find(program.getId());
      ruleEntity.setDomainEntity(domainEntity);
    }
    if (ruleEntity.getId() == null) {
      ruleEntity.setCreatedDate(new Date());
      ruleEntity = ruleDAO.create(ruleEntity);
    } else {
      ruleEntity = ruleDAO.update(ruleEntity);
    }
    return RuleBuilder.fromEntity(programStorage, ruleEntity);
  }

  public RuleDTO findRuleById(Long id) {
    return RuleBuilder.fromEntity(programStorage, ruleDAO.find(id));
  }

  public List<RuleDTO> findActiveRulesByEvent(String event) {
    List<RuleEntity> entities = ruleDAO.findActiveRulesByEvent(event);
    return RuleBuilder.fromEntities(programStorage, entities);
  }

  public RuleDTO findRuleByTitle(String ruleTitle) {
    return RuleBuilder.fromEntity(programStorage, ruleDAO.findRuleByTitle(ruleTitle));
  }

  public RuleDTO findActiveRuleByEventAndDomain(String event, long domainId) {
    return RuleBuilder.fromEntity(programStorage, ruleDAO.findActiveRuleByEventAndDomain(event, domainId));
  }

  public List<Long> findRulesIdsByFilter(RuleFilter ruleFilter, int offset, int limit) {
    return ruleDAO.findRulesIdsByFilter(ruleFilter, offset, limit);
  }

  public int countRulesByFilter(RuleFilter ruleFilter) {
    if (ruleFilter != null) {
      return ruleDAO.countRulesByFilter(ruleFilter);
    } else {
      return ruleDAO.count().intValue();
    }
  }

  public List<Long> findAllRulesIds(int offset, int limit) {
    return ruleDAO.findRulesIdsByFilter(new RuleFilter(), offset, limit);
  }

  public List<String> getAllEvents() {
    return ruleDAO.getAllEvents();
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
    return RuleBuilder.fromEntity(programStorage, ruleEntity);
  }

  public void clearCache() { // NOSONAR
    // implemented in cached storage
  }

}

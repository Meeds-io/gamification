package io.meeds.gamification.storage;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.storage.mapper.RuleMapper;

public class RuleStorage {

  private ProgramStorage programStorage;

  private EventStorage   eventStorage;

  private ProgramDAO     programDAO;

  private RuleDAO        ruleDAO;

  public RuleStorage(ProgramStorage programStorage, EventStorage eventStorage, ProgramDAO programDAO, RuleDAO ruleDAO) {
    this.programStorage = programStorage;
    this.eventStorage = eventStorage;
    this.ruleDAO = ruleDAO;
    this.programDAO = programDAO;
  }

  public RuleDTO saveRule(RuleDTO ruleDTO) {
    RuleEntity ruleEntity = RuleMapper.toEntity(ruleDTO);
    ruleEntity.setLastModifiedDate(new Date());
    ProgramDTO program = ruleDTO.getProgram();
    if (program != null) {
      ProgramEntity programEntity = programDAO.find(program.getId());
      ruleEntity.setDomainEntity(programEntity);
    }
    if (ruleEntity.getId() == null) {
      ruleEntity.setCreatedDate(new Date());
      ruleEntity = ruleDAO.create(ruleEntity);
    } else {
      ruleEntity = ruleDAO.update(ruleEntity);
    }
    return RuleMapper.fromEntity(programStorage, eventStorage, ruleEntity);
  }

  public RuleDTO findRuleById(Long id) {
    return RuleMapper.fromEntity(programStorage, eventStorage, ruleDAO.find(id));
  }

  public RuleDTO findRuleByTitle(String ruleTitle) {
    return RuleMapper.fromEntity(programStorage, eventStorage, ruleDAO.findRuleByTitle(ruleTitle));
  }

  public RuleDTO findActiveRuleByEventAndProgramId(String event, long programId) {
    return RuleMapper.fromEntity(programStorage, eventStorage, ruleDAO.findActiveRuleByEventAndProgramId(event, programId));
  }

  public List<Long> findRuleIdsByFilter(RuleFilter ruleFilter, int offset, int limit) {
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
    return ruleDAO.findRulesIdsByFilter(new RuleFilter(true), offset, limit);
  }

  public RuleDTO deleteRuleById(long ruleId, String userId) throws ObjectNotFoundException {
    RuleEntity ruleEntity = ruleDAO.find(ruleId);
    if (ruleEntity == null) {
      throw new ObjectNotFoundException("Rule with id " + ruleId + " does not exist");
    }
    if (StringUtils.isNotBlank(userId)) {
      ruleEntity.setLastModifiedBy(userId);
      ruleEntity.setLastModifiedDate(new Date());
    }
    ruleEntity.setDeleted(true);
    ruleDAO.update(ruleEntity);
    return RuleMapper.fromEntity(programStorage, eventStorage, ruleEntity);
  }

  public void clearCache() { // NOSONAR
    // implemented in cached storage
  }

  public void clearCache(RuleDTO rule) {
    // implemented in cached storage
  }

}

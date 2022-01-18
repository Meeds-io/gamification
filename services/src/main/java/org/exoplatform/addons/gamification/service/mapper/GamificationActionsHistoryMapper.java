package org.exoplatform.addons.gamification.service.mapper;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.Utils.Utils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;

public class GamificationActionsHistoryMapper {

  private DomainMapper domainMapper;

  private RuleMapper   ruleMapper;

  public GamificationActionsHistoryMapper(DomainMapper domainMapper, RuleMapper ruleMapper) {
    this.domainMapper = domainMapper;
    this.ruleMapper = ruleMapper;
  }

  public GamificationActionsHistoryDTO fromEntity(GamificationActionsHistory gamificationActionsHistoryEntity) {
    return new GamificationActionsHistoryDTO(gamificationActionsHistoryEntity);
  }

  public GamificationActionsHistory toEntity(GamificationActionsHistoryDTO gamificationActionsHistoryDTO) {
    GamificationActionsHistory gHistoryEntity = new GamificationActionsHistory();
    gHistoryEntity.setId(gamificationActionsHistoryDTO.getId());
    gHistoryEntity.setDomainEntity(domainMapper.domainDTOToDomain(Utils.getDomainByTitle(gamificationActionsHistoryDTO.getDomain())));
    gHistoryEntity.setDomain(gamificationActionsHistoryDTO.getDomain());
    gHistoryEntity.setActionTitle(gamificationActionsHistoryDTO.getActionTitle());
    gHistoryEntity.setActionScore(gamificationActionsHistoryDTO.getActionScore());
    gHistoryEntity.setGlobalScore(gamificationActionsHistoryDTO.getGlobalScore());
    gHistoryEntity.setActivityId(gamificationActionsHistoryDTO.getActivityId());
    gHistoryEntity.setObjectId(gamificationActionsHistoryDTO.getObjectId());
    gHistoryEntity.setReceiver(gamificationActionsHistoryDTO.getReceiver());
    gHistoryEntity.setEarnerId(gamificationActionsHistoryDTO.getEarnerId());
    gHistoryEntity.setEarnerType(IdentityType.getType(gamificationActionsHistoryDTO.getEarnerType()));
    gHistoryEntity.setContext(gamificationActionsHistoryDTO.getContext());
    gHistoryEntity.setDate(Utils.parseRFC3339Date(gamificationActionsHistoryDTO.getDate()));
    gHistoryEntity.setComment(gamificationActionsHistoryDTO.getComment());
    if (gamificationActionsHistoryDTO.getRuleId() != null) {
      gHistoryEntity.setRuleEntity(ruleMapper.ruleDTOToRule(Utils.getRuleById(gamificationActionsHistoryDTO.getRuleId())));
    }
    return gHistoryEntity;
  }
}

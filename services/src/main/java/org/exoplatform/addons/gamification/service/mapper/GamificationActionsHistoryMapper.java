package org.exoplatform.addons.gamification.service.mapper;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationActionsHistoryDTO;

public class GamificationActionsHistoryMapper {

  private GamificationActionsHistoryMapper() {
  }

  public static GamificationActionsHistoryDTO fromEntity(GamificationActionsHistory gamificationActionsHistoryEntity) {
    GamificationActionsHistoryDTO gActionsHistoryDTO = new GamificationActionsHistoryDTO();
    gActionsHistoryDTO.setId(gamificationActionsHistoryEntity.getId());
    gActionsHistoryDTO.setDate(Utils.toRFC3339Date(gamificationActionsHistoryEntity.getDate()));
    gActionsHistoryDTO.setEarnerId(gamificationActionsHistoryEntity.getEarnerId());
    gActionsHistoryDTO.setEarnerType(gamificationActionsHistoryEntity.getEarnerType().toString());
    gActionsHistoryDTO.setGlobalScore(gamificationActionsHistoryEntity.getGlobalScore());
    gActionsHistoryDTO.setActionTitle(gamificationActionsHistoryEntity.getActionTitle());
    gActionsHistoryDTO.setDomain(gamificationActionsHistoryEntity.getDomain());
    gActionsHistoryDTO.setContext(gamificationActionsHistoryEntity.getContext());
    gActionsHistoryDTO.setActionScore(gamificationActionsHistoryEntity.getActionScore());
    gActionsHistoryDTO.setReceiver(gamificationActionsHistoryEntity.getReceiver());
    gActionsHistoryDTO.setObjectId(gamificationActionsHistoryEntity.getObjectId());
    gActionsHistoryDTO.setRuleId(gamificationActionsHistoryEntity.getRuleId());
    gActionsHistoryDTO.setActivityId(gamificationActionsHistoryEntity.getActivityId());
    gActionsHistoryDTO.setComment(gamificationActionsHistoryEntity.getComment());
    gActionsHistoryDTO.setCreator(gamificationActionsHistoryEntity.getCreator());
    return gActionsHistoryDTO;
  }

  public static GamificationActionsHistory toEntity(GamificationActionsHistoryDTO gamificationActionsHistoryDTO) {
    GamificationActionsHistory gHistoryEntity = new GamificationActionsHistory();
    gHistoryEntity.setId(gamificationActionsHistoryDTO.getId());
    gHistoryEntity.setDomainEntity(DomainMapper.domainDTOToDomain(Utils.getDomainByTitle(gamificationActionsHistoryDTO.getDomain())));
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
    gHistoryEntity.setRuleId(gHistoryEntity.getRuleId());
    gHistoryEntity.setCreator(gHistoryEntity.getCreator());
    return gHistoryEntity;
  }
}

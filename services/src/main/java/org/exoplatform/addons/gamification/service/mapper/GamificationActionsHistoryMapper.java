package org.exoplatform.addons.gamification.service.mapper;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationActionsHistoryDTO;

public class GamificationActionsHistoryMapper {


    public GamificationActionsHistoryMapper() {

    }

  public static GamificationActionsHistoryDTO fromEntity(GamificationActionsHistory gamificationActionsHistoryEntity) {
    return new GamificationActionsHistoryDTO(gamificationActionsHistoryEntity.getId(),
            Utils.toRFC3339Date(gamificationActionsHistoryEntity.getDate()),
            gamificationActionsHistoryEntity.getEarnerId(),
            gamificationActionsHistoryEntity.getEarnerType().toString(),
            gamificationActionsHistoryEntity.getGlobalScore(),
            gamificationActionsHistoryEntity.getActionTitle(),
            gamificationActionsHistoryEntity.getDomain(),
            gamificationActionsHistoryEntity.getContext(),
            gamificationActionsHistoryEntity.getActionScore(),
            gamificationActionsHistoryEntity.getReceiver(),
            gamificationActionsHistoryEntity.getObjectId(),
            gamificationActionsHistoryEntity.getRuleEntity() != null ? gamificationActionsHistoryEntity.getRuleEntity().getId(): 0L,
            gamificationActionsHistoryEntity.getActivityId(),
            gamificationActionsHistoryEntity.getComment());
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
        if (gamificationActionsHistoryDTO.getRuleId() != null) {
            gHistoryEntity.setRuleEntity(RuleMapper.ruleDTOToRule(Utils.getRuleById(gamificationActionsHistoryDTO.getRuleId())));
        }
        return gHistoryEntity;
    }
}
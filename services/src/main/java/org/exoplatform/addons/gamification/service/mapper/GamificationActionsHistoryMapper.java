package org.exoplatform.addons.gamification.service.mapper;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationActionsHistoryDTO;

import java.util.Date;

public class GamificationActionsHistoryMapper {

  private GamificationActionsHistoryMapper() {
  }

  public static GamificationActionsHistoryDTO fromEntity(GamificationActionsHistory gamificationActionsHistoryEntity) {
    GamificationActionsHistoryDTO gActionsHistoryDTO = new GamificationActionsHistoryDTO(
            gamificationActionsHistoryEntity.getId(),
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
            gamificationActionsHistoryEntity.getRuleId(),
            gamificationActionsHistoryEntity.getActivityId(),
            gamificationActionsHistoryEntity.getComment(),
            gamificationActionsHistoryEntity.getCreator(),
            gamificationActionsHistoryEntity.getCreatedBy(),
            Utils.toRFC3339Date(gamificationActionsHistoryEntity.getCreatedDate()),
            gamificationActionsHistoryEntity.getLastModifiedBy(),
            Utils.toRFC3339Date(gamificationActionsHistoryEntity.getLastModifiedDate())
            );
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
    if(gamificationActionsHistoryDTO.getCreatedDate() != null) {
      gHistoryEntity.setCreatedDate(Utils.parseRFC3339Date(gamificationActionsHistoryDTO.getCreatedDate()));
    }
    if(gamificationActionsHistoryDTO.getLastModifiedDate() != null) {
      gHistoryEntity.setLastModifiedDate(Utils.parseRFC3339Date(gamificationActionsHistoryDTO.getLastModifiedDate()));
    }
    gHistoryEntity.setCreatedBy(gamificationActionsHistoryDTO.getCreatedBy() != null ? gamificationActionsHistoryDTO.getCreatedBy() : "Gamification Inner Process");
    gHistoryEntity.setLastModifiedBy(gamificationActionsHistoryDTO.getLastModifiedBy() != null ? gamificationActionsHistoryDTO.getLastModifiedBy() : "Gamification Inner Process");
    return gHistoryEntity;
  }
}

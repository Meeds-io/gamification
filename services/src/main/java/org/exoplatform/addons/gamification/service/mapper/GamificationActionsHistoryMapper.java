package org.exoplatform.addons.gamification.service.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GamificationActionsHistoryMapper {

  private GamificationActionsHistoryMapper() {
  }

  public static GamificationActionsHistoryDTO fromEntity(GamificationActionsHistory gamificationActionsHistoryEntity) {
        return new GamificationActionsHistoryDTO(
                gamificationActionsHistoryEntity.getId(),
                Utils.toRFC3339Date(new Date(gamificationActionsHistoryEntity.getDate().getTime())),
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
                Utils.toRFC3339Date(gamificationActionsHistoryEntity.getLastModifiedDate()),
                gamificationActionsHistoryEntity.getStatus().name());
      }

  public static List<GamificationActionsHistoryDTO> fromEntities(List<GamificationActionsHistory> gamificationActionsHistoryEntities) {
    if (CollectionUtils.isEmpty(gamificationActionsHistoryEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {

      return gamificationActionsHistoryEntities.stream()
                                               .map(gamificationActionsHistoryEntity -> fromEntity(gamificationActionsHistoryEntity))
                                               .collect(Collectors.toList());
    }
  }

  public static GamificationActionsHistory toEntity(GamificationActionsHistoryDTO gamificationActionsHistoryDTO) {
    if (gamificationActionsHistoryDTO == null) {
      return null;
    }
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
    gHistoryEntity.setRuleId(gamificationActionsHistoryDTO.getRuleId());
    gHistoryEntity.setCreator(gamificationActionsHistoryDTO.getCreator());
    gHistoryEntity.setStatus(HistoryStatus.valueOf(gamificationActionsHistoryDTO.getStatus()));
    if (gamificationActionsHistoryDTO.getCreatedDate() != null) {
      gHistoryEntity.setCreatedDate(Utils.parseRFC3339Date(gamificationActionsHistoryDTO.getCreatedDate()));
    }
    if (gamificationActionsHistoryDTO.getLastModifiedDate() != null) {
      gHistoryEntity.setLastModifiedDate(Utils.parseRFC3339Date(gamificationActionsHistoryDTO.getLastModifiedDate()));
    }
    gHistoryEntity.setCreatedBy(gamificationActionsHistoryDTO.getCreatedBy() != null ? gamificationActionsHistoryDTO.getCreatedBy()
                                                                                     : "Gamification Inner Process");
    gHistoryEntity.setLastModifiedBy(gamificationActionsHistoryDTO.getLastModifiedBy() != null ? gamificationActionsHistoryDTO.getLastModifiedBy()
                                                                                               : "Gamification Inner Process");
    return gHistoryEntity;
  }

  public static GamificationActionsHistoryRestEntity toRestEntity(GamificationActionsHistoryDTO gHistory) {
    return new GamificationActionsHistoryRestEntity( gHistory.getId(),
            Utils.getUserById(Long.valueOf(gHistory.getEarnerId()),gHistory.getRuleId()),
            gHistory.getRuleId() !=null ? Utils.getRuleById(gHistory.getRuleId()) : Utils.getRuleByTitle(gHistory.getActionTitle()),
            gHistory.getActionTitle(),
            gHistory.getActionScore(),
            gHistory.getComment(),
            Utils.getUserById(gHistory.getCreator() != null ? gHistory.getCreator() : Long.valueOf(gHistory.getReceiver()),gHistory.getRuleId()),
            gHistory.getCreatedDate(),
            gHistory.getStatus(),
            gHistory.getRuleId() != null ? Utils.getSpaceById(String.valueOf(Utils.getRuleById(gHistory.getRuleId()).getAudience())).getDisplayName() : null);
  }

public static List<GamificationActionsHistoryRestEntity> toRestEntities(List<GamificationActionsHistoryDTO> gamificationActionsHistories) {
    if (CollectionUtils.isEmpty(gamificationActionsHistories)) {
      return new ArrayList<>(Collections.emptyList());
    } else {

      return gamificationActionsHistories.stream()
              .map(gamificationActionsHistory -> toRestEntity(gamificationActionsHistory))
              .collect(Collectors.toList());
    }
  }

}

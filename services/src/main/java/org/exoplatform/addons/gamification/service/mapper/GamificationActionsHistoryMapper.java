package org.exoplatform.addons.gamification.service.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GamificationActionsHistoryMapper {

  private static final Log LOG = ExoLogger.getLogger(GamificationActionsHistoryMapper.class);

  private GamificationActionsHistoryMapper() {
  }

  public static GamificationActionsHistoryDTO fromEntity(GamificationActionsHistory gamificationActionsHistoryEntity) {
    return new GamificationActionsHistoryDTO(gamificationActionsHistoryEntity.getId(),
                                             gamificationActionsHistoryEntity.getEarnerId(),
                                             gamificationActionsHistoryEntity.getEarnerType().toString(),
                                             gamificationActionsHistoryEntity.getGlobalScore(),
                                             gamificationActionsHistoryEntity.getActionTitle(),
                                             gamificationActionsHistoryEntity.getDomainEntity().getTitle(),
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
                                               .map(GamificationActionsHistoryMapper::fromEntity)
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
    try {
      return new GamificationActionsHistoryRestEntity(gHistory.getId(),
                                                      Utils.getUserRemoteId(gHistory.getEarnerId()),
                                                      gHistory.getRuleId() != null
                                                          && gHistory.getRuleId() != 0 ? Utils.getRuleById(gHistory.getRuleId())
                                                                                       : Utils.getRuleByTitle(gHistory.getActionTitle()),
                                                      Utils.getDomainByTitle(gHistory.getDomain()),
                                                      gHistory.getActionTitle(),
                                                      gHistory.getActionScore(),
                                                      Utils.getUserRemoteId(gHistory.getCreator() != null ? String.valueOf(gHistory.getCreator())
                                                                                                          : gHistory.getReceiver()),
                                                      gHistory.getCreatedDate(),
                                                      gHistory.getStatus(),
                                                      gHistory.getRuleId() != null
                                                          && gHistory.getRuleId() != 0 ? Utils.getSpaceById(String.valueOf(Utils.getRuleById(gHistory.getRuleId()).getAudience())).getDisplayName() : Utils.getSpaceFromObjectID(gHistory.getObjectId()));

    } catch (Exception e) {
      LOG.error("Error while mapping history with id {}", gHistory.getId(), e);
      return null;
    }
 }

  public static List<GamificationActionsHistoryRestEntity> toRestEntities(List<GamificationActionsHistoryDTO> gamificationActionsHistories) {
    if (CollectionUtils.isEmpty(gamificationActionsHistories)) {
      return new ArrayList<>(Collections.emptyList());
    } else {

      return gamificationActionsHistories.stream()
                                         .map(GamificationActionsHistoryMapper::toRestEntity)
                                         .collect(Collectors.toList());
    }
  }

}

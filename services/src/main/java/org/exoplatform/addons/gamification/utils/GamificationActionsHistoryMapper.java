package org.exoplatform.addons.gamification.utils;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.addons.gamification.constant.HistoryStatus;
import org.exoplatform.addons.gamification.constant.IdentityType;
import org.exoplatform.addons.gamification.entity.DomainEntity;
import org.exoplatform.addons.gamification.entity.GamificationActionsHistoryEntity;
import org.exoplatform.addons.gamification.model.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.model.RuleDTO;
import org.exoplatform.addons.gamification.rest.model.GamificationActionsHistoryRestEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.model.Space;

public class GamificationActionsHistoryMapper {

  private static final Log LOG = ExoLogger.getLogger(GamificationActionsHistoryMapper.class);

  private GamificationActionsHistoryMapper() {
  }

  public static GamificationActionsHistoryDTO fromEntity(GamificationActionsHistoryEntity gamificationActionsHistoryEntity) {
    String objectId = "";
    if (gamificationActionsHistoryEntity.getActivityId() != null && gamificationActionsHistoryEntity.getActivityId() != 0) {
      objectId = "/" + LinkProvider.getPortalName("") + "/" + LinkProvider.getPortalOwner("") + "/activity?id="
          + gamificationActionsHistoryEntity.getActivityId();
    } else {
      objectId = gamificationActionsHistoryEntity.getObjectId();
    }
    DomainEntity domainEntity = gamificationActionsHistoryEntity.getDomainEntity();
    return new GamificationActionsHistoryDTO(gamificationActionsHistoryEntity.getId(),
                                             gamificationActionsHistoryEntity.getEarnerId(),
                                             gamificationActionsHistoryEntity.getEarnerType().toString(),
                                             gamificationActionsHistoryEntity.getGlobalScore(),
                                             gamificationActionsHistoryEntity.getActionTitle(),
                                             domainEntity != null ? domainEntity.getTitle()
                                                                  : gamificationActionsHistoryEntity.getDomain(),
                                             gamificationActionsHistoryEntity.getContext(),
                                             gamificationActionsHistoryEntity.getActionScore(),
                                             gamificationActionsHistoryEntity.getReceiver(),
                                             objectId,
                                             gamificationActionsHistoryEntity.getRuleId(),
                                             gamificationActionsHistoryEntity.getActivityId(),
                                             gamificationActionsHistoryEntity.getComment(),
                                             gamificationActionsHistoryEntity.getCreator(),
                                             gamificationActionsHistoryEntity.getCreatedBy(),
                                             Utils.toRFC3339Date(gamificationActionsHistoryEntity.getCreatedDate()),
                                             gamificationActionsHistoryEntity.getLastModifiedBy(),
                                             Utils.toRFC3339Date(gamificationActionsHistoryEntity.getLastModifiedDate()),
                                             gamificationActionsHistoryEntity.getStatus().name(),
                                             gamificationActionsHistoryEntity.getType());
  }

  public static List<GamificationActionsHistoryDTO> fromEntities(List<GamificationActionsHistoryEntity> gamificationActionsHistoryEntities) {
    if (CollectionUtils.isEmpty(gamificationActionsHistoryEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {

      return gamificationActionsHistoryEntities.stream()
                                               .map(GamificationActionsHistoryMapper::fromEntity)
                                               .collect(Collectors.toList());
    }
  }

  public static GamificationActionsHistoryEntity toEntity(GamificationActionsHistoryDTO gamificationActionsHistoryDTO) {
    if (gamificationActionsHistoryDTO == null) {
      return null;
    }
    GamificationActionsHistoryEntity gHistoryEntity = new GamificationActionsHistoryEntity();
    gHistoryEntity.setId(gamificationActionsHistoryDTO.getId());
    gHistoryEntity.setDomainEntity(DomainMapper.domainDTOToDomainEntity(Utils.getEnabledDomainByTitle(gamificationActionsHistoryDTO.getDomain())));
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
    gHistoryEntity.setType(gamificationActionsHistoryDTO.getType());
    if (gamificationActionsHistoryDTO.getCreatedDate() != null) {
      gHistoryEntity.setCreatedDate(Utils.parseRFC3339Date(gamificationActionsHistoryDTO.getCreatedDate()));
    } else {
      gHistoryEntity.setCreatedDate(new Date());
    }
    if (gamificationActionsHistoryDTO.getLastModifiedDate() != null) {
      gHistoryEntity.setLastModifiedDate(Utils.parseRFC3339Date(gamificationActionsHistoryDTO.getLastModifiedDate()));
    } else {
      gHistoryEntity.setLastModifiedDate(new Date());
    }
    gHistoryEntity.setCreatedBy(gamificationActionsHistoryDTO.getCreatedBy() != null ? gamificationActionsHistoryDTO.getCreatedBy()
                                                                                     : "Gamification Inner Process");
    gHistoryEntity.setLastModifiedBy(gamificationActionsHistoryDTO.getLastModifiedBy() != null ? gamificationActionsHistoryDTO.getLastModifiedBy()
                                                                                               : "Gamification Inner Process");
    return gHistoryEntity;
  }

  public static GamificationActionsHistoryRestEntity toRestEntity(GamificationActionsHistoryDTO gHistory, IdentityManager identityManager) { // NOSONAR
    try {
      String spaceName = "";
      if (gHistory.getRuleId() != null && gHistory.getRuleId() != 0) {
        RuleDTO rule = Utils.getRuleById(gHistory.getRuleId());
        if (rule != null) {
          long spaceId = rule.getAudience();
          if (spaceId > 0) {
            Space space = Utils.getSpaceById(String.valueOf(spaceId));
            if (space != null) {
              spaceName = space.getDescription();
            }
          }
        }
      } else {
        spaceName = Utils.getSpaceFromObjectID(gHistory.getObjectId());
      }
      RuleDTO rule = gHistory.getRuleId() != null && gHistory.getRuleId() != 0 ? Utils.getRuleById(gHistory.getRuleId())
                                                                               : Utils.getRuleByTitle(gHistory.getActionTitle());

      return new GamificationActionsHistoryRestEntity(gHistory.getId(),
                                                      Utils.getIdentityEntity(identityManager, Long.parseLong(gHistory.getEarnerId())),
                                                      rule,
                                                      Utils.getDomainByTitle(gHistory.getDomain()),
                                                      gHistory.getActionTitle() != null ? gHistory.getActionTitle()
                                                                                        : Objects.requireNonNull(rule).getTitle(),
                                                      gHistory.getActionScore(),
                                                      Utils.getUserFullName(gHistory.getCreator() != null ? String.valueOf(gHistory.getCreator())
                                                                                                          : gHistory.getReceiver()),
                                                      gHistory.getCreatedDate(),
                                                      gHistory.getStatus(),
                                                      spaceName,
                                                      gHistory.getObjectId());

    } catch (Exception e) {
      LOG.error("Error while mapping history with id {}", gHistory.getId(), e);
      return null;
    }
  }

  public static List<GamificationActionsHistoryRestEntity> toRestEntities(List<GamificationActionsHistoryDTO> gamificationActionsHistories,
                                                                          IdentityManager identityManager) {
    if (CollectionUtils.isEmpty(gamificationActionsHistories)) {
      return new ArrayList<>(Collections.emptyList());
    } else {

      return gamificationActionsHistories.stream()
                                         .map(gamificationActionsHistoryDTO -> toRestEntity(gamificationActionsHistoryDTO,
                                                                                            identityManager))
                                         .collect(Collectors.toList());
    }
  }
}

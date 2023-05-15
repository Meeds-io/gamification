package org.exoplatform.addons.gamification.service.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.RealizationEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.storage.ProgramStorage;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;

public class RealizationMapper {

  private RealizationMapper() {
    // Class with static methods
  }

  public static RealizationDTO fromEntity(ProgramStorage programStorage,
                                          RealizationEntity realizationEntity) {
    if (realizationEntity == null) {
      return null;
    }
    String objectId;
    if (realizationEntity.getActivityId() != null && realizationEntity.getActivityId() != 0) {
      objectId = String.valueOf(realizationEntity.getActivityId());
    } else {
      objectId = realizationEntity.getObjectId();
    }
    return new RealizationDTO(realizationEntity.getId(),
                              realizationEntity.getEarnerId(),
                              realizationEntity.getEarnerType().toString(),
                              realizationEntity.getGlobalScore(),
                              realizationEntity.getActionTitle(),
                              realizationEntity.getDomainEntity() == null ? null
                                                                          : programStorage.getDomainById(realizationEntity.getDomainEntity()
                                                                                                                         .getId()),
                              realizationEntity.getDomain(),
                              realizationEntity.getContext(),
                              realizationEntity.getActionScore(),
                              realizationEntity.getReceiver(),
                              objectId,
                              realizationEntity.getObjectType(),
                              realizationEntity.getRuleEntity().getId(),
                              realizationEntity.getActivityId(),
                              realizationEntity.getComment(),
                              realizationEntity.getCreator(),
                              realizationEntity.getCreatedBy(),
                              Utils.toRFC3339Date(realizationEntity.getCreatedDate()),
                              realizationEntity.getLastModifiedBy(),
                              Utils.toRFC3339Date(realizationEntity.getLastModifiedDate()),
                              realizationEntity.getStatus().name(),
                              realizationEntity.getType());
  }

  public static List<RealizationDTO> fromEntities(ProgramStorage programStorage,
                                                  List<RealizationEntity> realizationEntities) {
    if (CollectionUtils.isEmpty(realizationEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return realizationEntities.stream()
                                .map(entity -> fromEntity(programStorage, entity))
                                .toList();
    }
  }

  public static RealizationEntity toEntity(RuleStorage ruleStorage, RealizationDTO realization) {
    if (realization == null) {
      return null;
    }
    RealizationEntity gHistoryEntity = new RealizationEntity();
    gHistoryEntity.setId(realization.getId());
    gHistoryEntity.setDomainEntity(ProgramBuilder.toEntity(realization.getProgram()));
    gHistoryEntity.setDomain(realization.getProgram().getTitle());
    gHistoryEntity.setActionTitle(realization.getActionTitle());
    gHistoryEntity.setActionScore(realization.getActionScore());
    gHistoryEntity.setGlobalScore(realization.getGlobalScore());
    gHistoryEntity.setActivityId(realization.getActivityId());
    gHistoryEntity.setObjectId(realization.getObjectId());
    gHistoryEntity.setObjectType(realization.getObjectType());
    gHistoryEntity.setReceiver(realization.getReceiver());
    gHistoryEntity.setEarnerId(realization.getEarnerId());
    gHistoryEntity.setEarnerType(IdentityType.getType(realization.getEarnerType()));
    gHistoryEntity.setContext(realization.getContext());
    gHistoryEntity.setComment(realization.getComment());
    gHistoryEntity.setRuleEntity(RuleBuilder.ruleDTOToRule(ruleStorage.findRuleById(realization.getRuleId())));
    gHistoryEntity.setCreator(realization.getCreator());
    gHistoryEntity.setStatus(HistoryStatus.valueOf(realization.getStatus()));
    gHistoryEntity.setType(realization.getType());
    if (realization.getCreatedDate() != null) {
      gHistoryEntity.setCreatedDate(Utils.parseRFC3339Date(realization.getCreatedDate()));
    } else {
      gHistoryEntity.setCreatedDate(new Date());
    }
    if (realization.getLastModifiedDate() != null) {
      gHistoryEntity.setLastModifiedDate(Utils.parseRFC3339Date(realization.getLastModifiedDate()));
    } else {
      gHistoryEntity.setLastModifiedDate(new Date());
    }
    gHistoryEntity.setCreatedBy(realization.getCreatedBy() != null ? realization.getCreatedBy()
                                                                   : Utils.SYSTEM_USERNAME);
    gHistoryEntity.setLastModifiedBy(realization.getLastModifiedBy() != null ? realization.getLastModifiedBy()
                                                                             : Utils.SYSTEM_USERNAME);
    return gHistoryEntity;
  }

}

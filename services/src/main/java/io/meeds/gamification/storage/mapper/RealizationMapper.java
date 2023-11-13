package io.meeds.gamification.storage.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.utils.Utils;

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
                                                                          : programStorage.getProgramById(realizationEntity.getDomainEntity()
                                                                                                                           .getId()),
                              realizationEntity.getDomain(),
                              realizationEntity.getContext(),
                              realizationEntity.getActionScore(),
                              realizationEntity.getReceiver(),
                              objectId,
                              realizationEntity.getObjectType(),
                              realizationEntity.getRuleEntity() == null ? null : realizationEntity.getRuleEntity().getId(),
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
    RealizationEntity realizationEntity = new RealizationEntity();
    realizationEntity.setId(realization.getId());
    realizationEntity.setDomainEntity(ProgramMapper.toEntity(realization.getProgram()));
    realizationEntity.setDomain(realization.getProgram().getTitle());
    realizationEntity.setActionTitle(realization.getActionTitle());
    realizationEntity.setActionScore(realization.getActionScore());
    realizationEntity.setGlobalScore(realization.getGlobalScore());
    realizationEntity.setActivityId(realization.getActivityId());
    realizationEntity.setObjectId(realization.getObjectId());
    realizationEntity.setObjectType(realization.getObjectType());
    realizationEntity.setReceiver(realization.getReceiver());
    realizationEntity.setEarnerId(realization.getEarnerId());
    realizationEntity.setEarnerType(IdentityType.getType(realization.getEarnerType()));
    realizationEntity.setContext(realization.getContext());
    realizationEntity.setComment(realization.getComment());
    realizationEntity.setRuleEntity(RuleMapper.toEntity(ruleStorage.findRuleById(realization.getRuleId())));
    realizationEntity.setCreator(realization.getCreator());
    realizationEntity.setStatus(RealizationStatus.valueOf(realization.getStatus()));
    realizationEntity.setType(realization.getType());
    if (realization.getCreatedDate() != null) {
      realizationEntity.setCreatedDate(Utils.parseRFC3339Date(realization.getCreatedDate()));
    } else {
      realizationEntity.setCreatedDate(new Date());
    }
    if (realization.getLastModifiedDate() != null) {
      realizationEntity.setLastModifiedDate(Utils.parseRFC3339Date(realization.getLastModifiedDate()));
    } else {
      realizationEntity.setLastModifiedDate(new Date());
    }
    realizationEntity.setCreatedBy(realization.getCreatedBy() != null ? realization.getCreatedBy()
                                                                   : Utils.SYSTEM_USERNAME);
    realizationEntity.setLastModifiedBy(realization.getLastModifiedBy() != null ? realization.getLastModifiedBy()
                                                                             : Utils.SYSTEM_USERNAME);
    return realizationEntity;
  }

}

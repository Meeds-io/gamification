package io.meeds.gamification.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;

import io.meeds.gamification.constant.HistoryStatus;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.rest.model.RealizationRestEntity;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

public class RealizationBuilder {

  private static final Log LOG = ExoLogger.getLogger(RealizationBuilder.class);

  private RealizationBuilder() {
    // Class with static methods
  }

  public static RealizationDTO toRealization(RealizationService realizationService,
                                             IdentityManager identityManager,
                                             RuleDTO ruleDto,
                                             String earnerIdentityId,
                                             String receiverIdentityId,
                                             String objectId,
                                             String objectType) {
    if (ruleDto == null) {
      LOG.debug("Actor {} cannot earn points since rule is null", earnerIdentityId);
      return null;
    }
    if (!ruleDto.isEnabled()) {
      LOG.debug("Actor {} cannot earn points since rule is disabled", earnerIdentityId);
      return null;
    }
    // check if the current user is not a bot
    org.exoplatform.social.core.identity.model.Identity actorIdentity = identityManager.getIdentity(earnerIdentityId);
    if (actorIdentity == null || StringUtils.isBlank(actorIdentity.getRemoteId())) {
      LOG.debug("Actor {} has earned some points but doesn't have a social identity", earnerIdentityId);
      return null;
    }
    if (actorIdentity.isDeleted()) {
      LOG.debug("Actor {} has earned some points but is marked as deleted", earnerIdentityId);
      return null;
    }
    if (!actorIdentity.isEnable()) {
      LOG.debug("Actor {} has earned some points but is marked as disabled", earnerIdentityId);
      return null;
    }
    if (Utils.isUserMemberOfGroupOrUser(actorIdentity.getRemoteId(), Utils.BLACK_LIST_GROUP)) {
      LOG.debug("Actor {} cannot earn points since has been blacklisted", earnerIdentityId);
      return null;
    }

    // Build only an entry when a rule enable and exist
    ProgramDTO program = ruleDto.getProgram();
    if (program != null) {
      long audienceId = program.getAudienceId();
      if (program.isDeleted()
          || !program.isEnabled()
          || audienceId == 0
          || (audienceId > 0 && actorIdentity.isUser() && !Utils.isSpaceMember(audienceId, actorIdentity.getRemoteId()))) {
        LOG.debug("Actor {} cannot earn points since he is not a member of the domain audience", earnerIdentityId);
        return null;
      }
    }
    RealizationDTO realization = new RealizationDTO();
    realization.setActionScore(ruleDto.getScore());
    realization.setGlobalScore(realizationService.getScoreByIdentityId(earnerIdentityId) + ruleDto.getScore());
    realization.setEarnerId(earnerIdentityId);
    realization.setEarnerType(actorIdentity.getProviderId());
    realization.setActionTitle(ruleDto.getTitle());
    realization.setRuleId(ruleDto.getId());
    if (ruleDto.getProgram() != null) {
      realization.setProgram(ruleDto.getProgram());
    }
    realization.setReceiver(receiverIdentityId);
    realization.setObjectId(objectId);
    realization.setObjectType(objectType);
    realization.setStatus(HistoryStatus.ACCEPTED.name());
    realization.setType(ruleDto.getType());
    return realization;
  }

  public static RealizationRestEntity toRestEntity(RuleService ruleService, // NOSONAR
                                                   IdentityManager identityManager,
                                                   RealizationDTO realization) {
    try {
      String spaceName = "";
      if (realization.getRuleId() != null && realization.getRuleId() != 0) {
        RuleDTO rule = Utils.getRuleById(ruleService, realization.getRuleId());
        if (rule != null) {
          long spaceId = rule.getAudienceId();
          if (spaceId > 0) {
            Space space = Utils.getSpaceById(String.valueOf(spaceId));
            if (space != null) {
              spaceName = space.getDescription();
            }
          }
        }
      } else {
        spaceName = Utils.getSpaceFromObjectID(realization.getObjectId());
      }
      RuleDTO rule = realization.getRuleId() != null
          && realization.getRuleId() != 0 ? Utils.getRuleById(ruleService, realization.getRuleId())
                                          : Utils.getRuleByTitle(ruleService, realization.getActionTitle());

      return new RealizationRestEntity(realization.getId(),
                                       Utils.getIdentityEntity(identityManager, Long.parseLong(realization.getEarnerId())),
                                       rule,
                                       realization.getProgram(),
                                       realization.getDomainLabel(),
                                       realization.getActionTitle() != null ? realization.getActionTitle()
                                                                            : Objects.requireNonNull(rule).getTitle(),
                                       realization.getActionScore(),
                                       Utils.getUserFullName(realization.getCreator() != null ? String.valueOf(realization.getCreator())
                                                                                              : realization.getReceiver()),
                                       realization.getCreatedDate(),
                                       realization.getStatus(),
                                       spaceName,
                                       realization.getObjectId(),
                                       realization.getObjectType());

    } catch (Exception e) {
      LOG.error("Error while mapping history with id {}", realization.getId(), e);
      return null;
    }
  }

  public static List<RealizationRestEntity> toRestEntities(RuleService ruleService,
                                                           IdentityManager identityManager,
                                                           List<RealizationDTO> realizations) {
    if (CollectionUtils.isEmpty(realizations)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return realizations.stream()
                         .map(realization -> toRestEntity(ruleService, identityManager, realization))
                         .toList();
    }
  }
}

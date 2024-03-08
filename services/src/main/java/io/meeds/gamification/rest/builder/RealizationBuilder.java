package io.meeds.gamification.rest.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.xmlprocessor.XMLProcessor;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.UserInfo;
import io.meeds.gamification.rest.model.ProgramRestEntity;
import io.meeds.gamification.rest.model.RealizationRestEntity;
import io.meeds.gamification.rest.model.RuleRestEntity;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;

public class RealizationBuilder {

  private static final Log LOG = ExoLogger.getLogger(RealizationBuilder.class);

  private RealizationBuilder() {
    // Class with static methods
  }

  public static RealizationRestEntity toRestEntity(ProgramService programService, // NOSONAR
                                                   RuleService ruleService,
                                                   TranslationService translationService,
                                                   IdentityManager identityManager,
                                                   XMLProcessor xmlProcessor,
                                                   UserACL userAcl,
                                                   RealizationDTO realization,
                                                   String currentUsername,
                                                   Locale locale) {
    try {
      boolean anonymous = StringUtils.isBlank(currentUsername);
      ProgramDTO program = realization.getProgram();
      boolean canViewProgram = Utils.isRewardingManager(currentUsername)
                               || (program != null && programService.canViewProgram(program.getId(), currentUsername));
      boolean canViewTitle = canViewProgram
                             || StringUtils.equals(currentUsername, realization.getEarnerId())
                             || StringUtils.equals(String.valueOf(Utils.getCurrentUserIdentityId()), realization.getEarnerId());

      ProgramRestEntity programRestEntity = null;
      if (canViewProgram && program != null) {
        programRestEntity = ProgramBuilder.toRestEntity(programService,
                                                        ruleService,
                                                        translationService,
                                                        userAcl,
                                                        program,
                                                        locale,
                                                        currentUsername,
                                                        null,
                                                        false);
      }
      RuleDTO rule = realization.getRuleId() != null
          && realization.getRuleId() != 0 ? ruleService.findRuleById(realization.getRuleId())
                                          : ruleService.findRuleByTitle(realization.getActionTitle());
      RuleRestEntity ruleRestEntity = null;
      if (rule != null && canViewProgram) {
        ruleRestEntity = RuleBuilder.toRestEntity(programService,
                                                  ruleService,
                                                  null,
                                                  translationService,
                                                  null,
                                                  identityManager,
                                                  null,
                                                  xmlProcessor,
                                                  userAcl,
                                                  rule,
                                                  locale,
                                                  null,
                                                  0,
                                                  true,
                                                  anonymous,
                                                  null);
      }

      boolean actionLabelChanged = canViewProgram && (rule == null || !StringUtils.equals(realization.getActionTitle(), rule.getTitle()));
      boolean programLabelChanged = canViewProgram && (rule == null || rule.getProgram() == null || !StringUtils.equals(realization.getProgramLabel(), rule.getProgram().getTitle()));
      String spaceDisplayName = programRestEntity != null && programRestEntity.getSpace() != null ?
                                                                                                  programRestEntity.getSpace()
                                                                                                                   .getDisplayName() :
                                                                                                  null;
      Identity earnerIdentity = identityManager.getIdentity(realization.getEarnerId());
      return new RealizationRestEntity(realization.getId(),
                                       toUserInfo(earnerIdentity, anonymous),
                                       ruleRestEntity,
                                       programRestEntity,
                                       canViewTitle ? realization.getProgramLabel() : null,
                                       canViewTitle ? realization.getActionTitle() : null,
                                       realization.getActionScore(),
                                       anonymous ?
                                                 null :
                                                 getCreatorFullName(realization),
                                       realization.getCreatedDate(),
                                       realization.getSendingDate(),
                                       realization.getStatus(),
                                       canViewProgram && !anonymous ? spaceDisplayName : null,
                                       realization.getObjectId(),
                                       realization.getObjectType(),
                                       realization.getActivityId(),
                                       actionLabelChanged,
                                       programLabelChanged);

    } catch (Exception e) {
      LOG.error("Error while mapping history with id {}", realization.getId(), e);
      return null;
    }
  }

  public static void translatedLabels(TranslationService translationService,
                                      RealizationDTO realization,
                                      RuleDTO rule,
                                      Locale locale) {
    RuleBuilder.translatedLabels(translationService, rule, locale);
    if (Objects.equals(realization.getProgram(), rule.getProgram())) {
      realization.setProgram(rule.getProgram()); // Get the already translated
                                                 // program
    } else {
      ProgramBuilder.translatedLabels(translationService, realization.getProgram(), locale);
    }
  }

  public static List<RealizationRestEntity> toRestEntities(ProgramService programService, // NOSONAR
                                                           RuleService ruleService,
                                                           TranslationService translationService,
                                                           IdentityManager identityManager,
                                                           XMLProcessor xmlProcessor,
                                                           UserACL userAcl,
                                                           List<RealizationDTO> realizations,
                                                           String currentUsername,
                                                           Locale locale) {
    if (CollectionUtils.isEmpty(realizations)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return realizations.stream()
                         .map(realization -> toRestEntity(programService,
                                                          ruleService,
                                                          translationService,
                                                          identityManager,
                                                          xmlProcessor,
                                                          userAcl,
                                                          realization,
                                                          currentUsername,
                                                          locale))
                         .toList();
    }
  }

  private static String getCreatorFullName(RealizationDTO realization) {
    return Utils.getUserFullName(realization.getCreator()
        != null ? String.valueOf(realization.getCreator()) :
                realization.getReceiver());
  }

  private static UserInfo toUserInfo(Identity identity, boolean anonymous) {
    if (identity == null) {
      return null;
    }
    UserInfo userInfo = new UserInfo();
    mapUserInfo(userInfo, identity, anonymous);
    return userInfo;
  }

  private static <E extends UserInfo> void mapUserInfo(E userInfo, Identity identity, boolean anonymous) {
    if (identity != null) {
      userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
      userInfo.setFullName(identity.getProfile().getFullName());
      userInfo.setId(identity.getId());
      if (!anonymous) {
        userInfo.setRemoteId(identity.getRemoteId());
      }
    }
  }

}

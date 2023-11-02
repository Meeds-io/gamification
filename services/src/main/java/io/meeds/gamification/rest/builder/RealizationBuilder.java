package io.meeds.gamification.rest.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
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
                                                   RealizationDTO realization,
                                                   String currentUsername,
                                                   Locale locale) {
    try {
      boolean anonymous = StringUtils.isBlank(currentUsername);
      ProgramDTO program = realization.getProgram();
      boolean canViewProgram = Utils.isRewardingManager(currentUsername)
                               || (program != null
                                   && (StringUtils.equals(currentUsername, realization.getEarnerId())
                                       || StringUtils.equals(String.valueOf(Utils.getCurrentUserIdentityId()),
                                                             realization.getEarnerId())
                                       || programService.canViewProgram(program.getId(), currentUsername)
                                       || (program.isDeleted()
                                           && programService.wasProgramMember(program.getId(), currentUsername))));

      ProgramRestEntity programRestEntity = null;
      if (canViewProgram && program != null) {
        programRestEntity = ProgramBuilder.toRestEntity(programService,
                                                        ruleService,
                                                        translationService,
                                                        program,
                                                        locale,
                                                        null,
                                                        currentUsername);
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
      return new RealizationRestEntity(realization.getId(),
                                       anonymous ? null :
                                                 Utils.getIdentityEntity(identityManager,
                                                                         Long.parseLong(realization.getEarnerId())),
                                       ruleRestEntity,
                                       programRestEntity,
                                       canViewProgram ? realization.getProgramLabel() : null,
                                       canViewProgram ? realization.getActionTitle() : null,
                                       realization.getActionScore(),
                                       anonymous ?
                                                 null :
                                                 getCreatorFullName(realization),
                                       realization.getCreatedDate(),
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

  public static List<RealizationRestEntity> toRestEntities(ProgramService programService,
                                                           RuleService ruleService,
                                                           TranslationService translationService,
                                                           IdentityManager identityManager,
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

}

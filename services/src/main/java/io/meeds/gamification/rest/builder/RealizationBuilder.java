package io.meeds.gamification.rest.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;

import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.rest.model.RealizationRestEntity;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;

public class RealizationBuilder {

  private static final Log LOG = ExoLogger.getLogger(RealizationBuilder.class);

  private RealizationBuilder() {
    // Class with static methods
  }

  public static RealizationRestEntity toRestEntity(RuleService ruleService, // NOSONAR
                                                   TranslationService translationService,
                                                   IdentityManager identityManager,
                                                   RealizationDTO realization,
                                                   Locale locale) {
    try {
      String spaceName = "";
      if (realization.getRuleId() != null && realization.getRuleId() != 0) {
        RuleDTO rule = ruleService.findRuleById(realization.getRuleId());
        if (rule != null) {
          long spaceId = rule.getSpaceId();
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
          && realization.getRuleId() != 0 ? ruleService.findRuleById(realization.getRuleId())
                                          : ruleService.findRuleByTitle(realization.getActionTitle());

      boolean actionLabelChanged = !StringUtils.equals(realization.getActionTitle(), rule.getTitle());
      boolean programLabelChanged = !StringUtils.equals(realization.getProgramLabel(), rule.getProgram().getTitle());
      translatedLabels(translationService, realization, rule, locale);

      return new RealizationRestEntity(realization.getId(),
                                       Utils.getIdentityEntity(identityManager, Long.parseLong(realization.getEarnerId())),
                                       rule,
                                       realization.getProgram(),
                                       realization.getProgramLabel(),
                                       realization.getActionTitle() != null ? realization.getActionTitle()
                                                                            : Objects.requireNonNull(rule).getTitle(),
                                       realization.getActionScore(),
                                       Utils.getUserFullName(realization.getCreator() != null ? String.valueOf(realization.getCreator())
                                                                                              : realization.getReceiver()),
                                       realization.getCreatedDate(),
                                       realization.getStatus(),
                                       spaceName,
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

  public static List<RealizationRestEntity> toRestEntities(RuleService ruleService,
                                                           TranslationService translationService,
                                                           IdentityManager identityManager,
                                                           List<RealizationDTO> realizations,
                                                           Locale locale) {
    if (CollectionUtils.isEmpty(realizations)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return realizations.stream()
                         .map(realization -> toRestEntity(ruleService,
                                                          translationService,
                                                          identityManager,
                                                          realization,
                                                          locale))
                         .toList();
    }
  }
}

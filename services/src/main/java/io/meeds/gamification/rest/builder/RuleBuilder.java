/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.rest.builder;

import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_DESCRIPTION_FIELD_NAME;
import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_OBJECT_TYPE;
import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_TITLE_FIELD_NAME;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.UserInfoContext;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.rest.model.RealizationRestEntity;
import io.meeds.gamification.rest.model.RealizationValidityContext;
import io.meeds.gamification.rest.model.RuleRestEntity;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;

public class RuleBuilder {

  private RuleBuilder() {
    // Class with static methods
  }

  public static RuleRestEntity toRestEntity(ProgramService programService, // NOSONAR
                                            RuleService ruleService,
                                            RealizationService realizationService,
                                            TranslationService translationService,
                                            RuleDTO rule,
                                            Locale locale,
                                            List<String> expandFields,
                                            int realizationsLimit,
                                            boolean noProgram,
                                            PeriodType periodType) {
    if (rule == null) {
      return null;
    }
    boolean retrieveRealizations = realizationsLimit > 0;
    List<RealizationRestEntity> realizationEntities = null;
    if (retrieveRealizations) {
      List<RealizationDTO> realizations = getRealizations(realizationService,
                                                          rule.getId(),
                                                          periodType,
                                                          realizationsLimit);
      realizationEntities = RealizationBuilder.toRestEntities(ruleService,
                                                              ExoContainerContext.getService(IdentityManager.class),
                                                              realizations);
    }

    boolean countRealizations = retrieveRealizations || (expandFields != null && expandFields.contains("countRealizations"));
    long realizationsCount = 0;
    if (countRealizations) {
      realizationsCount = countRealizations(realizationService, rule.getId(), periodType);
    }
    List<RuleDTO> prerequisiteRules = ruleService.getPrerequisiteRules(rule.getId())
                                                 .stream()
                                                 .map(r -> {
                                                   r.setProgram(null);
                                                   return r;
                                                 })
                                                 .toList();
    ProgramDTO program = noProgram ? null : rule.getProgram();
    UserInfoContext userContext = toUserContext(programService,
                                                realizationService,
                                                rule,
                                                Utils.getCurrentUser());
    String title = rule.getTitle();
    String description = rule.getDescription();

    if (locale != null) {
      String translatedTitle = translationService.getTranslationLabel(RULE_OBJECT_TYPE,
                                                                      rule.getId(),
                                                                      RULE_TITLE_FIELD_NAME,
                                                                      locale);
      if (StringUtils.isNotBlank(translatedTitle)) {
        title = translatedTitle;
      }
      String translatedDescription = translationService.getTranslationLabel(RULE_OBJECT_TYPE,
                                                                            rule.getId(),
                                                                            RULE_DESCRIPTION_FIELD_NAME,
                                                                            locale);
      if (StringUtils.isNotBlank(translatedDescription)) {
        description = translatedDescription;
      }
    }

    return new RuleRestEntity(rule.getId(),
                              title,
                              description,
                              rule.getScore(),
                              program,
                              rule.isEnabled(),
                              rule.isDeleted(),
                              rule.getCreatedBy(),
                              rule.getCreatedDate(),
                              rule.getLastModifiedBy(),
                              rule.getEvent(),
                              rule.getLastModifiedDate(),
                              rule.getStartDate(),
                              rule.getEndDate(),
                              rule.getPrerequisiteRuleIds(),
                              rule.getType(),
                              rule.getRecurrence(),
                              rule.getAudienceId(),
                              rule.getManagers(),
                              realizationEntities,
                              realizationsCount,
                              userContext,
                              prerequisiteRules);
  }

  public static UserInfoContext toUserContext(ProgramService programService,
                                              RealizationService realizationService,
                                              RuleDTO rule,
                                              String username) {
    UserInfoContext userContext = ProgramBuilder.toUserContext(programService, rule.getProgram(), username);
    RealizationValidityContext realizationRestriction = realizationService.getRealizationValidityContext(rule,
                                                                                                         String.valueOf(Utils.getUserIdentityId(username)));
    userContext.setContext(realizationRestriction);
    userContext.setAllowedToRealize(realizationRestriction.isValid());
    return userContext;
  }

  private static List<RealizationDTO> getRealizations(RealizationService realizationService,
                                                      long ruleId,
                                                      PeriodType periodType,
                                                      int limit) {
    if (limit > 0) {
      return realizationService.getRealizationsByFilter(getRealizationsFilter(ruleId, periodType),
                                                        0,
                                                        limit);
    } else {
      return Collections.emptyList();
    }
  }

  private static int countRealizations(RealizationService realizationService,
                                       long ruleId,
                                       PeriodType periodType) {
    return realizationService.countRealizationsByFilter(getRealizationsFilter(ruleId, periodType));
  }

  private static RealizationFilter getRealizationsFilter(long ruleId, PeriodType periodType) {
    return new RealizationFilter(null,
                                 "date",
                                 true,
                                 periodType.getFromDate(),
                                 periodType.getToDate(),
                                 IdentityType.USER,
                                 RealizationStatus.ACCEPTED,
                                 Collections.singletonList(ruleId));
  }

}

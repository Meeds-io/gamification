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
package io.meeds.gamification.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.rest.api.RestUtils;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.UserInfoContext;
import io.meeds.gamification.rest.model.AnnouncementRestEntity;
import io.meeds.gamification.rest.model.RuleRestEntity;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.storage.ProgramStorage;

public class RuleBuilder {

  private static final Log LOG = ExoLogger.getLogger(RuleBuilder.class);

  private RuleBuilder() {
    // Class with static methods
  }

  public static RuleRestEntity toRestEntity(ProgramService programService, // NOSONAR
                                            RuleService ruleService,
                                            RealizationService realizationService,
                                            AnnouncementService announcementService,
                                            RuleDTO rule,
                                            List<String> expandFields,
                                            int announcementsLimit,
                                            boolean noDomain,
                                            PeriodType periodType) {
    if (rule == null) {
      return null;
    }
    boolean isManual = rule.getType() == EntityType.MANUAL;
    boolean retrieveAnnouncements = announcementsLimit > 0;
    List<AnnouncementRestEntity> announcementEntities = null;
    if (isManual && retrieveAnnouncements) {
      List<Announcement> announcements = getAnnouncements(announcementService,
                                                          rule,
                                                          announcementsLimit,
                                                          periodType == null ? PeriodType.ALL : periodType);
      announcementEntities = AnnouncementBuilder.fromAnnouncementList(announcements);
    }

    boolean countAnnouncements = retrieveAnnouncements || (expandFields != null && expandFields.contains("countAnnouncements"));
    long announcementsCount = 0;
    if (isManual && countAnnouncements) {
      announcementsCount = Utils.countAnnouncementsByRuleIdAndEarnerType(announcementService,
                                                                         rule.getId(),
                                                                         IdentityType.USER);
    }
    List<RuleDTO> prerequisiteRules = ruleService.getPrerequisiteRules(rule.getId())
                                                 .stream()
                                                 .map(r -> {
                                                   r.setProgram(null);
                                                   return r;
                                                 })
                                                 .toList();
    ProgramDTO program = noDomain ? null : rule.getProgram();
    UserInfoContext userContext = Utils.toUserContext(realizationService,
                                                      rule,
                                                      Utils.getCurrentUser());
    return new RuleRestEntity(rule.getId(),
                              rule.getTitle(),
                              rule.getDescription(),
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
                              announcementEntities,
                              announcementsCount,
                              userContext,
                              prerequisiteRules);
  }

  public static RuleEntity toEntity(RuleDTO rule) {
    if (rule == null) {
      return null;
    }
    RuleEntity ruleEntity = new RuleEntity();
    ruleEntity.setId(rule.getId());
    ruleEntity.setScore(rule.getScore());
    ruleEntity.setTitle(rule.getTitle());
    ruleEntity.setDescription(rule.getDescription());
    ruleEntity.setEnabled(rule.isEnabled());
    ruleEntity.setDeleted(rule.isDeleted());
    ruleEntity.setEvent(rule.getEvent());
    ruleEntity.setCreatedBy(rule.getCreatedBy());
    ruleEntity.setPrerequisiteRules(rule.getPrerequisiteRuleIds());
    if (rule.getStartDate() != null) {
      ruleEntity.setStartDate(Utils.parseSimpleDate(rule.getStartDate()));
    }
    if (rule.getEndDate() != null) {
      ruleEntity.setEndDate(Utils.parseSimpleDate(rule.getEndDate()));
    }
    if (rule.getType() != null) {
      ruleEntity.setType(rule.getType());
    } else {
      ruleEntity.setType(EntityType.AUTOMATIC);
    }
    if (rule.getCreatedDate() != null) {
      ruleEntity.setCreatedDate(Utils.parseRFC3339Date(rule.getCreatedDate()));
    }
    if (rule.getLastModifiedDate() != null) {
      ruleEntity.setCreatedDate(Utils.parseRFC3339Date(rule.getCreatedDate()));
    }
    ruleEntity.setLastModifiedBy(rule.getLastModifiedBy());
    ruleEntity.setDomainEntity(ProgramBuilder.toEntity(rule.getProgram()));
    ruleEntity.setRecurrence(rule.getRecurrence() == null ? RecurrenceType.NONE : rule.getRecurrence());
    return ruleEntity;
  }

  public static RuleDTO fromEntity(ProgramStorage programStorage,
                                   RuleEntity ruleEntity) {
    if (ruleEntity == null) {
      return null;
    } else {
      RuleDTO rule = new RuleDTO();
      rule.setId(ruleEntity.getId());
      rule.setScore(ruleEntity.getScore());
      rule.setTitle(ruleEntity.getTitle());
      rule.setDescription(ruleEntity.getDescription());
      rule.setEnabled(ruleEntity.isEnabled());
      rule.setDeleted(ruleEntity.isDeleted());
      rule.setEvent(ruleEntity.getEvent());
      rule.setCreatedBy(ruleEntity.getCreatedBy());
      rule.setPrerequisiteRuleIds(ruleEntity.getPrerequisiteRules());
      if (ruleEntity.getStartDate() != null) {
        rule.setStartDate(Utils.toSimpleDateFormat(ruleEntity.getStartDate()));
      }
      if (ruleEntity.getEndDate() != null) {
        rule.setEndDate(Utils.toSimpleDateFormat(ruleEntity.getEndDate()));
      }
      if (ruleEntity.getType() != null) {
        rule.setType(ruleEntity.getType());
      } else {
        rule.setType(EntityType.AUTOMATIC);
      }
      rule.setCreatedDate(Utils.toRFC3339Date(ruleEntity.getCreatedDate()));
      rule.setLastModifiedDate(Utils.toRFC3339Date(ruleEntity.getLastModifiedDate()));
      rule.setLastModifiedBy(ruleEntity.getLastModifiedBy());
      rule.setProgram(ruleEntity.getDomainEntity() == null ? null
                                                           : programStorage.getProgramById(ruleEntity.getDomainEntity().getId()));
      rule.setRecurrence(ruleEntity.getRecurrence());
      if (ruleEntity.getRecurrence() == RecurrenceType.NONE) {
        rule.setRecurrence(null);
      }
      return rule;
    }
  }

  public static List<RuleDTO> fromEntities(ProgramStorage programStorage,
                                           List<RuleEntity> rules) {
    return rules.stream()
                .filter(Objects::nonNull)
                .map(entity -> fromEntity(programStorage, entity))
                .toList();
  }

  private static List<Announcement> getAnnouncements(AnnouncementService announcementService,
                                                     RuleDTO rule,
                                                     int limit,
                                                     PeriodType period) {
    if (limit > 0) {
      try {
        return announcementService.findAnnouncements(rule.getId(),
                                                     0,
                                                     limit,
                                                     period,
                                                     null,
                                                     RestUtils.getCurrentUser());
      } catch (Exception e) {
        LOG.warn("Error while retrieving announcements list on rule {} for user {}. Returning empty list",
                 rule.getId(),
                 Utils.getCurrentUser(),
                 e);
        return Collections.emptyList();
      }
    } else {
      return Collections.emptyList();
    }
  }

}

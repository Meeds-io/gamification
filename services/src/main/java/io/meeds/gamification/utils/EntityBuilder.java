/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.rest.api.RestUtils;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.UserInfo;
import io.meeds.gamification.rest.model.AnnouncementRestEntity;
import io.meeds.gamification.rest.model.ProgramRestEntity;
import io.meeds.gamification.rest.model.RuleRestEntity;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;

public class EntityBuilder {
  private static final Log LOG = ExoLogger.getLogger(EntityBuilder.class);

  private EntityBuilder() {
    // Class with static methods
  }

  public static List<AnnouncementRestEntity> fromAnnouncementList(List<Announcement> announcements) {
    if (CollectionUtils.isEmpty(announcements)) {
      return Collections.emptyList();
    } else {
      return announcements.stream().map(AnnouncementBuilder::fromAnnouncement).toList();
    }
  }

  public static RuleRestEntity toRestEntity(ProgramService programService, // NOSONAR
                                            RuleService ruleService,
                                            AnnouncementService announcementService,
                                            RuleDTO rule,
                                            List<String> expandFields,
                                            int announcementsLimit,
                                            boolean noDomain,
                                            PeriodType periodType) {
    if (rule == null) {
      return null;
    }
    boolean expandUserAnnouncements = expandFields != null && expandFields.contains("userAnnouncements");
    if (expandUserAnnouncements && announcementsLimit <= 0) {
      announcementsLimit = 3;
    }
    List<AnnouncementRestEntity> announcementEntities = null;
    long announcementsCount = 0;
    if (periodType != null && announcementsLimit > 0 && rule.getType() == EntityType.MANUAL) {
      List<Announcement> announcements = getAnnouncements(announcementService, rule, announcementsLimit, periodType);
      announcementEntities = fromAnnouncementList(announcements);
      announcementsCount = expandUserAnnouncements ? Utils.countAnnouncementsByRuleIdAndEarnerType(announcementService,
                                                                                                   rule.getId(),
                                                                                                   IdentityType.USER)
                                                   : 0;
    }

    List<RuleDTO> prerequisiteRules = ruleService.getPrerequisiteRules(rule.getId())
                                                 .stream()
                                                 .map(r -> {
                                                   r.setProgram(null);
                                                   return r;
                                                 })
                                                 .toList();
    ProgramDTO program = noDomain ? null : rule.getProgram();
    UserInfo userInfo = Utils.toUserInfo(programService, rule.getDomainId(), Utils.getCurrentUser());

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
                              userInfo,
                              prerequisiteRules);
  }

  public static ProgramRestEntity toRestEntity(ProgramService programService, ProgramDTO program, String username) {
    if (program == null) {
      return null;
    }
    return new ProgramRestEntity(program.getId(),
                                 program.getTitle(),
                                 program.getDescription(),
                                 program.getAudienceId() > 0 ? Utils.getSpaceById(String.valueOf(program.getAudienceId())) : null,
                                 program.getPriority(),
                                 program.getCreatedBy(),
                                 program.getCreatedDate(),
                                 program.getLastModifiedBy(),
                                 program.getLastModifiedDate(),
                                 program.isEnabled(),
                                 program.isDeleted(),
                                 program.getBudget(),
                                 program.getType(),
                                 program.getCoverUrl(),
                                 program.getRulesTotalScore(),
                                 Utils.getProgramOwnersByIds(program.getOwners(), program.getAudienceId()),
                                 Utils.toUserInfo(programService, program.getId(), username));
  }

  public static List<ProgramRestEntity> toRestEntities(ProgramService programService, List<ProgramDTO> domains, String username) {
    return domains.stream().map(program -> toRestEntity(programService, program, username)).toList();
  }

  public static List<Announcement> getAnnouncements(AnnouncementService announcementService,
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

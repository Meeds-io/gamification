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

package org.exoplatform.addons.gamification.rest;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.rest.model.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.rest.model.ProgramRestEntity;
import org.exoplatform.addons.gamification.rest.model.RuleRestEntity;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.configuration.ProgramService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.mapper.AnnouncementBuilder;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.rest.api.RestUtils;

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

  public static RuleRestEntity toRestEntity(ProgramService domainService,
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
    ProgramDTO domain = noDomain ? null : rule.getProgram();
    UserInfo userInfo = Utils.toUserInfo(domainService, rule.getDomainId(), Utils.getCurrentUser());

    return new RuleRestEntity(rule.getId(),
                              rule.getTitle(),
                              rule.getDescription(),
                              rule.getScore(),
                              domain,
                              rule.isEnabled(),
                              rule.isDeleted(),
                              rule.getCreatedBy(),
                              rule.getCreatedDate(),
                              rule.getLastModifiedBy(),
                              rule.getEvent(),
                              rule.getLastModifiedDate(),
                              rule.getAudienceId(),
                              rule.getStartDate(),
                              rule.getEndDate(),
                              rule.getType(),
                              rule.getManagers(),
                              announcementEntities,
                              announcementsCount,
                              userInfo);
  }

  public static ProgramRestEntity toRestEntity(ProgramService domainService, ProgramDTO domain, String username) {
    if (domain == null) {
      return null;
    }
    return new ProgramRestEntity(domain.getId(),
                                domain.getTitle(),
                                domain.getDescription(),
                                domain.getAudienceId() > 0 ? Utils.getSpaceById(String.valueOf(domain.getAudienceId())) : null,
                                domain.getPriority(),
                                domain.getCreatedBy(),
                                domain.getCreatedDate(),
                                domain.getLastModifiedBy(),
                                domain.getLastModifiedDate(),
                                domain.isEnabled(),
                                domain.isDeleted(),
                                domain.getBudget(),
                                domain.getType(),
                                domain.getCoverUrl(),
                                domain.getRulesTotalScore(),
                                Utils.getDomainOwnersByIds(domain.getOwners(), domain.getAudienceId()),
                                Utils.toUserInfo(domainService, domain.getId(), username));
  }

  public static List<ProgramRestEntity> toRestEntities(ProgramService domainService, List<ProgramDTO> domains, String username) {
    return domains.stream().map(program -> toRestEntity(domainService, program, username)).toList();
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

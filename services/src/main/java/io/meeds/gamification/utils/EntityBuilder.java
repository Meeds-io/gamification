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
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.rest.api.RestUtils;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.UserInfo;
import io.meeds.gamification.model.UserInfoContext;
import io.meeds.gamification.rest.model.AnnouncementRestEntity;
import io.meeds.gamification.rest.model.ProgramRestEntity;
import io.meeds.gamification.rest.model.RuleRestEntity;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
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

  public static ProgramRestEntity toRestEntity(ProgramDTO program, String username) {
    if (program == null) {
      return null;
    }
    return new ProgramRestEntity(program.getId(), // NOSONAR
                                 program.getTitle(),
                                 program.getDescription(),
                                 program.getAudienceId(),
                                 program.getPriority(),
                                 program.getCreatedBy(),
                                 program.getCreatedDate(),
                                 program.getLastModifiedBy(),
                                 program.getLastModifiedDate(),
                                 program.isDeleted(),
                                 program.isEnabled(),
                                 program.getBudget(),
                                 program.getType(),
                                 null,
                                 program.getCoverFileId(),
                                 program.getCoverUrl(),
                                 program.getOwnerIds(),
                                 program.getRulesTotalScore(),
                                 program.getAudienceId() > 0 ? Utils.getSpaceById(String.valueOf(program.getAudienceId())) : null,
                                 Utils.toUserContext(program, username),
                                 getProgramOwnersByIds(program.getOwnerIds(), program.getAudienceId()));
  }

  public static List<ProgramRestEntity> toRestEntities(List<ProgramDTO> domains, String username) {
    return domains.stream().map(program -> toRestEntity(program, username)).toList();
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

  private static List<UserInfo> getProgramOwnersByIds(Set<Long> ids, long spaceId) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    return ids.stream()
              .distinct()
              .map(id -> {
                try {
                  Identity identity = identityManager.getIdentity(String.valueOf(id));
                  if (identity != null
                      && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())
                      && !spaceService.isManager(space, identity.getRemoteId())
                      && spaceService.isMember(space, identity.getRemoteId())) {
                    return Utils.toUserInfo(identity);
                  }
                } catch (Exception e) {
                  LOG.warn("Error when getting domain owner with id {}. Ignore retrieving identity information", id, e);
                }
                return null;
              })
              .filter(Objects::nonNull)
              .toList();
  }

}

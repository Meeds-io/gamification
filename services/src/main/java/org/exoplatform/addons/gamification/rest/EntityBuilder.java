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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.rest.model.*;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.model.Space;

public class EntityBuilder {

  private EntityBuilder() { // NOSONAR
  }

  public static List<AnnouncementRestEntity> fromAnnouncementList(List<Announcement> announcements) {
    if (CollectionUtils.isEmpty(announcements)) {
      return Collections.emptyList();
    } else {
      return announcements.stream().map(EntityMapper::fromAnnouncement).toList();
    }
  }

  public static ChallengeRestEntity fromChallenge(AnnouncementService announcementService,
                                                  Challenge challenge,
                                                  int announcementsPerChallenge,
                                                  boolean noDomain,
                                                  PeriodType periodType) throws IllegalAccessException {
    if (challenge == null) {
      return null;
    }
    List<Announcement> challengeAnnouncements = null;
    if (announcementsPerChallenge != 0 ) {
      challengeAnnouncements =
                             announcementService.findAllAnnouncementByChallenge(challenge.getId(), 0, announcementsPerChallenge, periodType, null);
    } else {
      challengeAnnouncements = Collections.emptyList();
    }
    return fromChallenge(challenge, challengeAnnouncements, noDomain);
  }

  public static ChallengeRestEntity fromChallenge(Challenge challenge, List<Announcement> challengeAnnouncements) {
    return fromChallenge(challenge, challengeAnnouncements, false);
  }

  public static ChallengeRestEntity fromChallenge(Challenge challenge,
                                                  List<Announcement> challengeAnnouncements,
                                                  boolean noDomain) {
    Space space = Utils.getSpaceById(String.valueOf(challenge.getAudience()));
    return new ChallengeRestEntity(challenge.getId(),
                                   challenge.getTitle(),
                                   challenge.getDescription(),
                                   space,
                                   challenge.getStartDate(),
                                   challenge.getEndDate(),
                                   Utils.toUserInfo(challenge,
                                                    Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME,
                                                                                 Utils.getCurrentUser())),
                                   Utils.getOwners(challenge),
                                   Utils.countAnnouncementsByChallenge(challenge.getId()),
                                   fromAnnouncementList(challengeAnnouncements),
                                   challenge.getPoints(),
                                   noDomain ? null : Utils.getChallengeDomainDTO(challenge),
                                   challenge.isEnabled());
  }

  public static DomainRestEntity toRestEntity(DomainDTO domain, String username) {
    if (domain == null) {
      return null;
    }
    return new DomainRestEntity(domain.getId(),
                                domain.getTitle(),
                                domain.getDescription(),
                                domain.getAudienceId() > 0 ? Utils.getSpaceById(String.valueOf(domain.getAudienceId())) : null,
                                domain.getPriority(),
                                domain.getCreatedBy(),
                                domain.getCreatedDate(),
                                domain.getLastModifiedBy(),
                                domain.getLastModifiedDate(),
                                domain.isEnabled(),
                                domain.getBudget(),
                                domain.getType(),
                                domain.getCoverUrl(),
                                Utils.getRulesTotalScoreByDomain(domain.getId()),
                                Utils.getDomainOwnersByIds(domain.getOwners(), domain.getAudienceId()),
                                Utils.toUserInfo(domain.getId(), username));
  }

  public static List<DomainRestEntity> toRestEntities(List<DomainDTO> domains, String username) {
    return domains.stream().map((DomainDTO domainDTO) -> toRestEntity(domainDTO, username)).toList();
  }

  public static List<RuleRestEntity> ruleListToRestEntities(List<RuleDTO> rules, String username, int offset, int limit, IdentityType earnerType, String expand) {
    return rules.stream().map((RuleDTO ruleDTO) -> ruleToRestEntity(ruleDTO, username, offset, limit, earnerType, expand)).toList();
  }

  public static RuleRestEntity ruleToRestEntity(RuleDTO rule, String username, int offset, int limit, IdentityType earnerType, String expand) {
    List<AnnouncementRestEntity> announcementsRestEntities = null;
    if (rule == null) {
      return null;
    }
    List<String> expandFields = null;
    if (StringUtils.isBlank(expand)) {
      expandFields = Collections.emptyList();
    } else {
      expandFields = Arrays.asList(expand.split(","));
    }

    List<Announcement> announcementList = null;
    if (expandFields.contains("announcements")) {
      announcementList = Utils.findAllAnnouncementByChallenge(rule.getId(), offset, limit, earnerType);
    }
    if (announcementList != null) {
      announcementsRestEntities =
          announcementList.stream().map(EntityMapper::fromAnnouncement).toList();
    }
    return new RuleRestEntity(rule.getId(),
                              rule.getTitle(),
                              rule.getDescription(),
                              rule.getScore(),
                              rule.getArea(),
                              rule.getDomainDTO(),
                              rule.isEnabled(),
                              rule.isDeleted(),
                              rule.getCreatedBy(),
                              rule.getCreatedDate(),
                              rule.getLastModifiedBy(),
                              rule.getEvent(),
                              rule.getLastModifiedDate(),
                              rule.getAudience(),
                              rule.getStartDate(),
                              rule.getEndDate(),
                              rule.getType(),
                              rule.getManagers(),
                              announcementsRestEntities,
                              Utils.toUserInfo(rule.getId(), username));
  }
}

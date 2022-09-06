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

package org.exoplatform.addons.gamification.service.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.rest.model.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.utils.Utils;

public class EntityMapper {

  private EntityMapper() {
  }

  public static RuleEntity toEntity(Challenge challenge) {
    if (challenge == null) {
      return null;
    }
    RuleEntity challengeEntity = new RuleEntity();
    if (challenge.getId() != 0) {
      challengeEntity.setId(challenge.getId());
    }
    if (StringUtils.isNotBlank(challenge.getTitle())) {
      challengeEntity.setTitle(challenge.getTitle());
    }
    if (StringUtils.isNotBlank(challenge.getDescription())) {
      challengeEntity.setDescription(challenge.getDescription());
    }
    if (challenge.getEndDate() != null) {
      challengeEntity.setEndDate(Utils.parseSimpleDate(challenge.getEndDate()));
    }
    if (challenge.getStartDate() != null) {
      challengeEntity.setStartDate(Utils.parseSimpleDate(challenge.getStartDate()));
    }
    if (challenge.getManagers() == null || challenge.getManagers().isEmpty()) {
      challengeEntity.setManagers(Collections.emptyList());
    } else {
      challengeEntity.setManagers(challenge.getManagers());
    }
    challengeEntity.setAudience(challenge.getAudience());
    challengeEntity.setManagers(challenge.getManagers());
    challengeEntity.setScore(challenge.getPoints().intValue());

    DomainDTO domain = Utils.getEnabledDomainByTitle(challenge.getProgram());
    if (domain != null) {
      challengeEntity.setDomainEntity(DomainMapper.domainDTOToDomainEntity(domain));
    }
    return challengeEntity;
  }

  public static AnnouncementRestEntity fromAnnouncement(Announcement announcement) {
    if (announcement == null) {
      return null;
    }
    return new AnnouncementRestEntity(announcement.getId(),
                                      Utils.getUserRemoteId(String.valueOf(announcement.getAssignee() != null ? announcement.getAssignee()
                                                                                                              : announcement.getCreator())),
                                      announcement.getCreatedDate(),
                                      announcement.getActivityId());
  }

  public static Announcement fromEntity(GamificationActionsHistory announcementEntity) {
    if (announcementEntity == null) {
      return null;
    }
    return new Announcement(announcementEntity.getId(),
                            announcementEntity.getRuleId(),
                            announcementEntity.getActionTitle(),
                            Long.parseLong(announcementEntity.getEarnerId()),
                            announcementEntity.getComment(),
                            announcementEntity.getCreator(),
                            Utils.toRFC3339Date(announcementEntity.getCreatedDate()),
                            announcementEntity.getActivityId());
  }

  public static GamificationActionsHistory toEntity(Announcement announcement, RuleEntity ruleEntity) {
    if (announcement == null) {
      return null;
    }
    GamificationActionsHistory announcementEntity = new GamificationActionsHistory();
    if (announcement.getId() != 0) {
      announcementEntity.setId(announcement.getId());
    }
    if (announcement.getActivityId() != null) {
      announcementEntity.setActivityId(announcement.getActivityId());
    }
    if (announcement.getAssignee() != null) {
      announcementEntity.setEarnerId(String.valueOf(announcement.getAssignee()));
    }

    Date createDate = Utils.parseRFC3339Date(announcement.getCreatedDate());
    announcementEntity.setComment(announcement.getComment());
    announcementEntity.setCreatedDate(createDate);
    announcementEntity.setRuleId(announcement.getChallengeId());
    announcementEntity.setActionTitle(announcement.getChallengeTitle() != null ? announcement.getChallengeTitle()
                                                                               : ruleEntity.getTitle());
    announcementEntity.setCreator(announcement.getCreator());
    announcementEntity.setDate(createDate != null ? createDate : new Date(System.currentTimeMillis()));
    announcementEntity.setCreatedDate(createDate != null ? createDate : new Date(System.currentTimeMillis()));
    announcementEntity.setReceiver(String.valueOf(announcement.getCreator()));
    announcementEntity.setStatus(HistoryStatus.ACCEPTED);
    if (createDate != null) {
      announcementEntity.setCreatedDate(createDate);
    }
    announcementEntity.setLastModifiedDate(new Date(System.currentTimeMillis()));

    String creator = Utils.getUserRemoteId(String.valueOf(announcement.getCreator()));
    announcementEntity.setCreatedBy(creator != null ? creator : "Gamification Inner Process");
    announcementEntity.setLastModifiedBy(creator != null ? creator : "Gamification Inner Process");

    DomainEntity domainEntity = ruleEntity.getDomainEntity();
    announcementEntity.setEarnerType(IdentityType.USER);
    announcementEntity.setActionScore(ruleEntity.getScore());
    announcementEntity.setGlobalScore(Utils.getUserGlobalScore(String.valueOf(announcement.getAssignee())));
    announcementEntity.setDomainEntity(domainEntity);
    announcementEntity.setDomain(domainEntity.getTitle());
    announcementEntity.setObjectId("");
    return announcementEntity;
  }

  public static List<Announcement> fromAnnouncementEntities(List<GamificationActionsHistory> announcementEntities) {
    if (CollectionUtils.isEmpty(announcementEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return announcementEntities.stream().map(EntityMapper::fromEntity).collect(Collectors.toList());
    }
  }

  public static Announcement fromAnnouncementActivity(AnnouncementActivity announcementActivity) {
    return new Announcement(announcementActivity.getId(),
                            announcementActivity.getChallengeId(),
                            announcementActivity.getChallengeTitle(),
                            announcementActivity.getAssignee(),
                            announcementActivity.getComment(),
                            announcementActivity.getCreator(),
                            announcementActivity.getCreatedDate(),
                            announcementActivity.getActivityId());

  }

  public static AnnouncementActivity toAnnouncementActivity(Announcement announcement, Map<String, String> templateParams) {
    return new AnnouncementActivity(announcement.getId(),
                                    announcement.getChallengeId(),
                                    announcement.getChallengeTitle(),
                                    announcement.getAssignee(),
                                    announcement.getComment(),
                                    announcement.getCreator(),
                                    announcement.getCreatedDate(),
                                    announcement.getActivityId(),
                                    templateParams);
  }

  public static RuleDTO fromChallengeToRule(Challenge challenge) {
    if (challenge == null) {
      return null;
    }
    RuleDTO rule = new RuleDTO();
    if (challenge.getId() > 0) {
      rule.setId(challenge.getId());
    }
    rule.setScore(challenge.getPoints() == null ? 0 : challenge.getPoints().intValue());
    rule.setTitle(challenge.getTitle());
    rule.setDescription(challenge.getDescription());
    rule.setArea(challenge.getProgram());
    rule.setEnabled(false);
    rule.setDeleted(false);
    rule.setDomainDTO(Utils.getDomainByTitle(challenge.getProgram()));
    if (challenge.getAudience() > 0) {
      rule.setAudience(challenge.getAudience());
    }
    if (challenge.getEndDate() != null) {
      rule.setEndDate(challenge.getEndDate());
    }
    if (challenge.getStartDate() != null) {
      rule.setStartDate(challenge.getStartDate());
    }
    rule.setType(EntityType.MANUAL);
    if (challenge.getManagers() != null) {
      rule.setManagers(challenge.getManagers());
    } else {
      rule.setManagers(Collections.emptyList());
    }
    return rule;
  }

  public static Challenge fromRuleToChallenge(RuleDTO ruleDTO) {
    if (ruleDTO == null) {
      return null;
    }
    return new Challenge(ruleDTO.getId(),
                         ruleDTO.getTitle(),
                         ruleDTO.getDescription(),
                         ruleDTO.getAudience(),
                         ruleDTO.getStartDate(),
                         ruleDTO.getEndDate(),
                         ruleDTO.getManagers(),
                         (long) ruleDTO.getScore(),
                         ruleDTO.getDomainDTO() == null ? null : ruleDTO.getDomainDTO().getTitle());
  }

}

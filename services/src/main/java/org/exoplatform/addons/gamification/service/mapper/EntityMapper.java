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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.exoplatform.addons.gamification.service.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.*;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.model.Space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMapper {

  private EntityMapper() {
  }

  public static Challenge fromEntity(RuleEntity challengeEntity) {
    if (challengeEntity == null) {
      return null;
    }
    return new Challenge(challengeEntity.getId(),
                         challengeEntity.getTitle(),
                         challengeEntity.getDescription(),
                         challengeEntity.getAudience(),
                         challengeEntity.getStartDate() == null ? null : Utils.toSimpleDateFormat(challengeEntity.getStartDate()),
                         challengeEntity.getEndDate() == null ? null : Utils.toSimpleDateFormat(challengeEntity.getEndDate()),
                         challengeEntity.getManagers(),
                         (long) challengeEntity.getScore(),
                         challengeEntity.getDomainEntity() != null ? challengeEntity.getDomainEntity().getTitle() : null);
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
      challengeEntity.setDomainEntity(DomainMapper.domainDTOToDomain(domain));
    }
    return challengeEntity;
  }

  public static List<Challenge> fromChallengeEntities(List<RuleEntity> challengeEntities) {
    if (CollectionUtils.isEmpty(challengeEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return challengeEntities.stream().map(EntityMapper::fromEntity).collect(Collectors.toList());
    }
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
                            Long.parseLong(announcementEntity.getEarnerId()),
                            announcementEntity.getComment(),
                            announcementEntity.getCreator(),
                            Utils.toRFC3339Date(announcementEntity.getCreatedDate()),
                            announcementEntity.getActivityId());
  }

  public static GamificationActionsHistory toEntity(Announcement announcement) {
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

    return announcementEntity;
  }

  public static List<Announcement> fromAnnouncementEntities(List<GamificationActionsHistory> announcementEntities) {
    if (CollectionUtils.isEmpty(announcementEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return announcementEntities.stream().map(EntityMapper::fromEntity).collect(Collectors.toList());
    }
  }

  public static List<AnnouncementRestEntity> fromAnnouncementList(List<Announcement> announcements) {
    if (CollectionUtils.isEmpty(announcements)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return announcements.stream().map(EntityMapper::fromAnnouncement).collect(Collectors.toList());
    }
  }

  public static ChallengeRestEntity fromChallenge(AnnouncementService announcementService,
                                                  Challenge challenge,
                                                  int announcementsPerChallenge,
                                                  boolean noDomain) throws IllegalAccessException, ObjectNotFoundException {
    if (challenge == null) {
      return null;
    }
    List<Announcement> challengeAnnouncements = null;
    if (announcementsPerChallenge > 0) {
      challengeAnnouncements =
                             announcementService.findAllAnnouncementByChallenge(challenge.getId(), 0, announcementsPerChallenge);
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
                                   Utils.createUser(Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME,
                                                                                 Utils.getCurrentUser()),
                                                    space,
                                                    challenge.getManagers()),
                                   Utils.getManagersByIds(challenge.getManagers()),
                                   Utils.countAnnouncementsByChallenge(challenge.getId()),
                                   fromAnnouncementList(challengeAnnouncements),
                                   challenge.getPoints(),
                                   noDomain ? null : Utils.getDomainByTitle(challenge.getProgram()));
  }

  public static ChallengeRestEntity fromChallengeSearchEntity(AnnouncementService announcementService,
                                                              ChallengeSearchEntity challengeSearchEntity,
                                                              int announcementsPerChallenge,
                                                              boolean noDomain) throws IllegalAccessException,
                                                                                ObjectNotFoundException {
    if (challengeSearchEntity == null) {
      return null;
    }
    List<Announcement> challengeAnnouncements = null;
    if (announcementsPerChallenge > 0) {
      challengeAnnouncements = announcementService.findAllAnnouncementByChallenge(challengeSearchEntity.getId(),
                                                                                  0,
                                                                                  announcementsPerChallenge);
    } else {
      challengeAnnouncements = Collections.emptyList();
    }
    return fromChallengeSearchEntity(challengeSearchEntity, challengeAnnouncements, noDomain);
  }

  public static ChallengeRestEntity fromChallengeSearchEntity(ChallengeSearchEntity challengeSearchEntity,
                                                              List<Announcement> challengeAnnouncements,
                                                              boolean noDomain) {
    Space space = Utils.getSpaceById(String.valueOf(challengeSearchEntity.getAudience()));
    return new ChallengeRestEntity(challengeSearchEntity.getId(),
                                   challengeSearchEntity.getTitle(),
                                   challengeSearchEntity.getDescription(),
                                   space,
                                   challengeSearchEntity.getStartDate(),
                                   challengeSearchEntity.getEndDate(),
                                   Utils.createUser(Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME,
                                                                                 Utils.getCurrentUser()),
                                                    space,
                                                    challengeSearchEntity.getManagers()),
                                   Utils.getManagersByIds(challengeSearchEntity.getManagers()),
                                   Utils.countAnnouncementsByChallenge(challengeSearchEntity.getId()),
                                   fromAnnouncementList(challengeAnnouncements),
                                   challengeSearchEntity.getPoints(),
                                   noDomain ? null : Utils.getDomainById(challengeSearchEntity.getId()));
  }

  public static ChallengeSearchEntity fromChallenge (Challenge challenge) {
    if (challenge == null) {
      return null;
    }
    DomainDTO challengeProgram = Utils.getDomainByTitle(challenge.getProgram());
    return new ChallengeSearchEntity(challenge.getId(),
            challenge.getTitle(),
            challenge.getDescription(),
            challenge.getAudience(),
            challenge.getStartDate(),
            challenge.getEndDate(),
            challenge.getManagers(),
            challenge.getPoints(),
            challengeProgram != null ? challengeProgram.getId() : 0);
  }

}

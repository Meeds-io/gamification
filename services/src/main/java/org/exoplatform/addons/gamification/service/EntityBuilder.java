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

package org.exoplatform.addons.gamification.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.ChallengeRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.ChallengeSearchEntity;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.model.Space;

public class EntityBuilder {

  private EntityBuilder() { // NOSONAR
  }

  public static List<AnnouncementRestEntity> fromAnnouncementList(List<Announcement> announcements) {
    if (CollectionUtils.isEmpty(announcements)) {
      return Collections.emptyList();
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
                                   Utils.toUserInfo(Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME,
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

}

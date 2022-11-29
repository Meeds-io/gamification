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

package org.exoplatform.addons.gamification.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.addons.gamification.model.Announcement;
import org.exoplatform.addons.gamification.model.Challenge;
import org.exoplatform.addons.gamification.model.DomainDTO;
import org.exoplatform.addons.gamification.rest.model.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.rest.model.ChallengeRestEntity;
import org.exoplatform.addons.gamification.rest.model.DomainRestEntity;
import org.exoplatform.addons.gamification.service.AnnouncementService;
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
                                   noDomain ? null : Utils.getDomainByTitle(challenge.getProgram()),
                                   challenge.isEnabled());
  }

  public static DomainRestEntity toRestEntity(DomainDTO domain, String username) {
    if (domain == null) {
      return null;
    }
    return new DomainRestEntity(domain.getId(),
                                domain.getTitle(),
                                domain.getDescription(),
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
                                Utils.getDomainOwnersByIds(domain.getOwners()),
                                Utils.toUserInfo(username, domain.getOwners()));
  }

  public static List<DomainRestEntity> toRestEntities(List<DomainDTO> domains, String username) {
    return domains.stream().map((DomainDTO domainDTO) -> toRestEntity(domainDTO, username)).collect(Collectors.toList());
  }

}

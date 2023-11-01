/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.rest.builder;

import static io.meeds.gamification.plugin.ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE;
import static io.meeds.gamification.plugin.ProgramTranslationPlugin.PROGRAM_TITLE_FIELD_NAME;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.rest.model.LeaderboardInfo;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.social.translation.service.TranslationService;

public class LeaderboardBuilder {

  private static final Log    LOG                   = ExoLogger.getLogger(LeaderboardBuilder.class);

  private static final String YOUR_CURRENT_RANK_MSG = "Your current rank";

  private LeaderboardBuilder() {
    // Utils Class
  }

  public static List<PiechartLeaderboard> buildLeaderboardEntities(ProgramService programService,
                                                                   TranslationService translationService,
                                                                   List<PiechartLeaderboard> userStats,
                                                                   String currentUser,
                                                                   Locale locale,
                                                                   String expand) {
    if (expand != null && Arrays.asList(StringUtils.split(expand, ",")).contains("programTitle")) {
      userStats.forEach(stat -> {
        long programId = stat.getProgramId();
        if (programService.canViewProgram(programId, currentUser)) {
          ProgramDTO program = programService.getProgramById(programId);
          String programdTitle = translationService.getTranslationLabel(PROGRAM_OBJECT_TYPE,
                                                                        program.getId(),
                                                                        PROGRAM_TITLE_FIELD_NAME,
                                                                        locale);
          stat.setLabel(StringUtils.isBlank(programdTitle) ? program.getTitle() : programdTitle);
        }
      });
    }
    return userStats;
  }

  public static LeaderboardInfo buildCurrentUserRank(RealizationService realizationService,
                                                     IdentityManager identityManager,
                                                     Date date,
                                                     Long programId,
                                                     List<LeaderboardInfo> leaderboardList) {
    if (leaderboardList.isEmpty()) {
      return null;
    }

    String currentUser = ConversationState.getCurrent().getIdentity().getUserId();
    LeaderboardInfo leaderboardInfo = null;
    try {
      String earnerIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUser).getId();
      if (!isEarnerInTopTen(earnerIdentity, leaderboardList)) {
        // Get GaamificationScore for current user
        int rank = realizationService.getLeaderboardRank(earnerIdentity, date, programId);
        if (rank > 0) {
          leaderboardInfo = new LeaderboardInfo();
          leaderboardInfo.setRank(rank);
          leaderboardInfo.setRemoteId(YOUR_CURRENT_RANK_MSG);
          leaderboardInfo.setFullname(YOUR_CURRENT_RANK_MSG);
          leaderboardInfo.setAvatarUrl(YOUR_CURRENT_RANK_MSG);
          leaderboardInfo.setProfileUrl(YOUR_CURRENT_RANK_MSG);
        }
      }
    } catch (Exception e) {
      LOG.error("Error building Rank for user {} ", currentUser, e);
    }
    return leaderboardInfo;
  }

  public static LeaderboardInfo toLeaderboardInfo(SpaceService spaceService,
                                                  StandardLeaderboard element,
                                                  Identity identity,
                                                  boolean isAnonymous,
                                                  int index) {
    LeaderboardInfo leaderboardInfo = new LeaderboardInfo();
    leaderboardInfo.setSocialId(identity.getId());
    leaderboardInfo.setFullname(identity.getProfile().getFullName());
    leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    leaderboardInfo.setScore(element.getReputationScore());
    leaderboardInfo.setRank(index);
    if (!isAnonymous) {
      leaderboardInfo.setRemoteId(identity.getRemoteId());
      String technicalId = computeTechnicalId(spaceService, identity);
      leaderboardInfo.setTechnicalId(technicalId);
      leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());
    }
    return leaderboardInfo;
  }

  private static boolean isEarnerInTopTen(String username, List<LeaderboardInfo> leaderboard) {
    if (leaderboard.isEmpty())
      return false;
    return leaderboard.stream().map(LeaderboardInfo::getSocialId).anyMatch(username::equals);
  }

  private static String computeTechnicalId(SpaceService spaceService, Identity identity) {
    if (!SpaceIdentityProvider.NAME.equals(identity.getProviderId())) {
      return null;
    }
    Space space = spaceService.getSpaceByPrettyName(identity.getRemoteId());
    return space == null ? null : space.getId();
  }

}

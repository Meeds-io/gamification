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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.rest.model.LeaderboardInfo;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;

public class LeaderboardBuilder {

  private static final String MONTH_PERIOD_NAME = "MONTH";

  private static final String WEEK_PERIOD_NAME  = "WEEK";

  private LeaderboardBuilder() {
    // Utils Class
  }

  public static List<LeaderboardInfo> buildLeaderboardInfos(RealizationService realizationService, // NOSONAR
                                                            IdentityManager identityManager,
                                                            SpaceService spaceService,
                                                            List<StandardLeaderboard> standardLeaderboards,
                                                            IdentityType identityType,
                                                            Long identityId,
                                                            Long programId,
                                                            String period,
                                                            boolean isAnonymous) {
    List<LeaderboardInfo> leaderboardList = new ArrayList<>();
    int index = 1;
    for (StandardLeaderboard element : standardLeaderboards) {
      Identity identity = identityManager.getIdentity(element.getEarnerId());
      if (identity == null) {
        continue;
      }
      leaderboardList.add(toLeaderboardInfo(spaceService, element, identity, isAnonymous, index));
      index++;
    }

    if (identityId != null && identityId > 0 && identityType.isUser()) {
      LeaderboardInfo leader = buildRank(realizationService,
                                         identityManager,
                                         spaceService,
                                         leaderboardList,
                                         period,
                                         programId,
                                         identityId,
                                         isAnonymous);
      if (leader != null) {
        leaderboardList.add(leader);
      }
    }
    return leaderboardList;
  }

  public static List<PiechartLeaderboard> buildPiechartLeaderboards(ProgramService programService,
                                                                    TranslationService translationService,
                                                                    List<PiechartLeaderboard> userStats,
                                                                    String currentUser,
                                                                    Locale locale) {
    boolean rewardingManager = Utils.isRewardingManager(currentUser);
    userStats.forEach(stat -> {
      long programId = stat.getProgramId();
      if (rewardingManager || programService.canViewProgram(programId, currentUser)) {
        ProgramDTO program = programService.getProgramById(programId);
        String programdTitle = translationService.getTranslationLabel(PROGRAM_OBJECT_TYPE,
                                                                      program.getId(),
                                                                      PROGRAM_TITLE_FIELD_NAME,
                                                                      locale);
        stat.setLabel(StringUtils.isBlank(programdTitle) ? program.getTitle() : programdTitle);
      }
    });
    return userStats;
  }

  public static LeaderboardInfo buildRank(RealizationService realizationService, // NOSONAR
                                          IdentityManager identityManager,
                                          SpaceService spaceService,
                                          List<LeaderboardInfo> leaderboardList,
                                          String period,
                                          Long programId,
                                          long identityId,
                                          boolean isAnonymous) {
    if (!isEarnerInTopTen(identityId, leaderboardList)) {
      // Check if the current user is already in top10
      Date fromDate = getCurrentPeriodStartDate(period);
      int rank = realizationService.getLeaderboardRank(String.valueOf(identityId),
                                                       fromDate,
                                                       programId);
      if (rank > 0) {
        Identity identity = identityManager.getIdentity(String.valueOf(identityId));
        LeaderboardInfo leaderboardInfo = new LeaderboardInfo();
        leaderboardInfo.setIdentityId(Long.parseLong(identity.getId()));
        leaderboardInfo.setFullname(identity.getProfile().getFullName());
        leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
        leaderboardInfo.setScore(realizationService.getScoreByIdentityIdAndBetweenDates(identity.getId(),
                                                                                        fromDate,
                                                                                        new Date()));
        leaderboardInfo.setRank(rank);
        if (!isAnonymous) {
          leaderboardInfo.setRemoteId(identity.getRemoteId());
          String technicalId = computeTechnicalId(spaceService, identity);
          leaderboardInfo.setTechnicalId(technicalId);
          leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());
        }
        return leaderboardInfo;
      }
    }
    return null;
  }

  public static Date getCurrentPeriodStartDate(String period) {
    Date fromDate;
    switch (period) {
    case WEEK_PERIOD_NAME:
      fromDate = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
      break;
    case MONTH_PERIOD_NAME:
      fromDate = Date.from(LocalDate.now()
                                    .with(TemporalAdjusters.firstDayOfMonth())
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant());
      break;
    default:
      fromDate = null;
    }
    return fromDate;
  }

  public static LeaderboardInfo toLeaderboardInfo(SpaceService spaceService,
                                                  StandardLeaderboard element,
                                                  Identity identity,
                                                  boolean isAnonymous,
                                                  int index) {
    LeaderboardInfo leaderboardInfo = new LeaderboardInfo();
    leaderboardInfo.setIdentityId(Long.parseLong(identity.getId()));
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

  private static boolean isEarnerInTopTen(long identityId, List<LeaderboardInfo> leaderboard) {
    if (leaderboard.isEmpty())
      return false;
    return leaderboard.stream().anyMatch(l -> identityId == l.getIdentityId());
  }

  private static String computeTechnicalId(SpaceService spaceService, Identity identity) {
    if (!SpaceIdentityProvider.NAME.equals(identity.getProviderId())) {
      return null;
    }
    Space space = spaceService.getSpaceByPrettyName(identity.getRemoteId());
    return space == null ? null : space.getId();
  }

}

/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
package io.meeds.gamification.rest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.Period;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.utils.Utils;
import io.meeds.portal.security.service.SecuritySettingService;

import io.swagger.v3.oas.annotations.Parameter;

@Path("/gamification/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
public class LeaderboardEndpoint implements ResourceContainer {

  private static final Log         LOG                   = ExoLogger.getLogger(LeaderboardEndpoint.class);

  private static final String      YOUR_CURRENT_RANK_MSG = "Your current rank";

  private static final int         DEFAULT_LOAD_CAPACITY = 10;

  private static final int         MAX_LOAD_CAPACITY     = 100;

  protected IdentityManager        identityManager       = null;

  protected RealizationService     realizationService    = null;

  protected RelationshipManager    relationshipManager;

  protected SpaceService           spaceService;

  protected SecuritySettingService securitySettingService;

  public LeaderboardEndpoint(IdentityManager identityManager,
                             RealizationService realizationService,
                             RelationshipManager relationshipManager,
                             SpaceService spaceService,
                             SecuritySettingService securitySettingService) {
    this.identityManager = identityManager;
    this.realizationService = realizationService;
    this.relationshipManager = relationshipManager;
    this.spaceService = spaceService;
    this.securitySettingService = securitySettingService;
  }

  @GET
  @Path("rank/all")
  public Response getAllLeadersByRank(
                                      @Context
                                      UriInfo uriInfo,
                                      @Parameter(description = "Get leaderboard of user or space")
                                      @DefaultValue("user")
                                      @QueryParam("earnerType")
                                      String earnerType,
                                      @Parameter(description = "Limit of identities to retrieve")
                                      @DefaultValue("10")
                                      @QueryParam("limit")
                                      int limit,
                                      @Parameter(description = "Period name, possible values: WEEK, MONTH or ALL")
                                      @DefaultValue("ALL")
                                      @QueryParam("period")
                                      String period,
                                      @Parameter(description = "Get only the top 10 or all")
                                      @DefaultValue("true")
                                      @QueryParam("loadCapacity")
                                      boolean loadCapacity) {
    if (!Utils.canAccessAnonymousResources(securitySettingService)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    LeaderboardFilter leaderboardFilter = new LeaderboardFilter();
    IdentityType identityType = IdentityType.getType(earnerType);
    leaderboardFilter.setIdentityType(identityType);
    if (limit <= 0) {
      if (loadCapacity) {
        limit = DEFAULT_LOAD_CAPACITY;
      } else {
        limit = MAX_LOAD_CAPACITY;
      }
    }
    leaderboardFilter.setLoadCapacity(limit);
    if (StringUtils.isBlank(period)) {
      period = Period.ALL.name();
    }
    leaderboardFilter.setPeriod(period);
    String currentUser = Utils.getCurrentUser();
    leaderboardFilter.setCurrentUser(currentUser);

    List<LeaderboardInfo> leaderboardList = new ArrayList<>();

    try {
      List<StandardLeaderboard> standardLeaderboards = realizationService.getLeaderboard(leaderboardFilter);
      if (standardLeaderboards == null) {
        return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).build();
      }
      int index = 1;
      boolean isAnonymous = StringUtils.isBlank(currentUser);
      for (StandardLeaderboard element : standardLeaderboards) {
        Identity identity = identityManager.getIdentity(element.getEarnerId());
        if (identity == null) {
          continue;
        }
        LeaderboardInfo leaderboardInfo = new LeaderboardInfo();
        leaderboardInfo.setFullname(identity.getProfile().getFullName());
        leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
        leaderboardInfo.setScore(element.getReputationScore());
        leaderboardInfo.setRank(index);
        if (!isAnonymous) {
          leaderboardInfo.setRemoteId(identity.getRemoteId());
          leaderboardInfo.setSocialId(identity.getId());
          String technicalId = computeTechnicalId(identity);
          leaderboardInfo.setTechnicalId(technicalId);
          leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());
        }
        leaderboardList.add(leaderboardInfo);
        index++;
      }

      if (identityType.isUser()) {
        Date date = null;
        switch (leaderboardFilter.getPeriod()) {
        case "WEEK":
          date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
          break;
        case "MONTH":
          date = Date.from(LocalDate.now()
                                    .with(TemporalAdjusters.firstDayOfMonth())
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant());
          break;
        }

        if (!isAnonymous) {
          // Check if the current user is already in top10
          LeaderboardInfo leader = buildCurrentUserRank(date,
                                                        leaderboardFilter.getProgramId(),
                                                        leaderboardList);
          // Complete the final leaderboard
          if (leader != null) {
            leaderboardList.add(leader);
          }
        }
      }
      return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
      LOG.error("Error building leaderboard ", e);
      return Response.serverError()

                     .entity("Error building leaderboard")
                     .build();
    }
  }

  @GET
  @Path("filter")
  @RolesAllowed("users")
  public Response filter(
                         @Context
                         UriInfo uriInfo,
                         @QueryParam("programId")
                         Long programId,
                         @QueryParam("period")
                         String period,
                         @QueryParam("capacity")
                         int capacity) {
    // Init search criteria
    LeaderboardFilter leaderboardFilter = new LeaderboardFilter();
    leaderboardFilter.setProgramId(programId);
    if (StringUtils.isBlank(period)) {
      leaderboardFilter.setPeriod(Period.WEEK.name());
    } else {
      leaderboardFilter.setPeriod(period);
    }

    if (capacity <= 0) {
      leaderboardFilter.setLoadCapacity(DEFAULT_LOAD_CAPACITY);
    } else {
      leaderboardFilter.setLoadCapacity(capacity);
    }

    // hold leaderboard flow
    LeaderboardInfo leaderboardInfo = null;

    try {

      List<StandardLeaderboard> standardLeaderboards = realizationService.getLeaderboard(leaderboardFilter);
      List<LeaderboardInfo> leaderboardInfoList = new ArrayList<>();
      if (standardLeaderboards == null || standardLeaderboards.isEmpty()) {
        return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).build();
      }

      for (StandardLeaderboard leader : standardLeaderboards) {
        Identity identity = identityManager.getIdentity(leader.getEarnerId());
        leaderboardInfo = new LeaderboardInfo();
        String technicalId = computeTechnicalId(identity);
        leaderboardInfo.setTechnicalId(technicalId);
        leaderboardInfo.setSocialId(identity.getId());
        leaderboardInfo.setScore(leader.getReputationScore());
        leaderboardInfo.setRemoteId(identity.getRemoteId());
        leaderboardInfo.setFullname(identity.getProfile().getFullName());
        leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
        leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());
        leaderboardInfoList.add(leaderboardInfo);
      }
      // Check if the current user is already in top10
      Date date = null;
      switch (leaderboardFilter.getPeriod()) {
      case "WEEK":
        date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
        break;
      case "MONTH":
        date = Date.from(LocalDate.now()
                                  .with(TemporalAdjusters.firstDayOfMonth())
                                  .atStartOfDay(ZoneId.systemDefault())
                                  .toInstant());
        break;
      }
      LeaderboardInfo leader = buildCurrentUserRank(date,
                                                    leaderboardFilter.getProgramId(),
                                                    leaderboardInfoList);
      // Complete the final leaderboard
      if (leader != null)
        leaderboardInfoList.add(leader);

      return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).build();

    } catch (Exception e) {

      LOG.error("Error filtering leaderbaord by Doamin : {} and by Period {} ",
                leaderboardFilter.getProgramId(),
                leaderboardFilter.getPeriod(),
                e);

      return Response.serverError()
                     .entity("Error filtering leaderboard")
                     .build();
    }
  }

  @GET
  @Path("stats")
  @RolesAllowed("users")
  public Response stats(@Context
  UriInfo uriInfo, @QueryParam("username")
  String userSocialId, @QueryParam("period")
  String period) {
    ConversationState conversationState = ConversationState.getCurrent();
    if (conversationState != null) {
      try {
        if (userSocialId != null) {
          userSocialId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userSocialId).getId();
        }
        period = StringUtils.isBlank(period) ? Period.ALL.name() : period.toUpperCase();
        // Check if the current user is already in top10
        Date startDate = null;
        switch (period) {
        case "WEEK":
          startDate = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
          break;
        case "MONTH":
          startDate = Date.from(LocalDate.now()
                                         .with(TemporalAdjusters.firstDayOfMonth())
                                         .atStartOfDay(ZoneId.systemDefault())
                                         .toInstant());
          break;
        }

        // Find user's stats
        List<PiechartLeaderboard> userStats = realizationService.getStatsByIdentityId(userSocialId,
                                                                                      startDate,
                                                                                      Calendar.getInstance().getTime());

        return Response.ok(userStats, MediaType.APPLICATION_JSON).build();

      } catch (Exception e) {

        LOG.error("Error building statistics for user {} ", userSocialId, e);

        return Response.serverError()
                       .entity("Error building statistics")
                       .build();
      }

    } else {
      return Response.status(Response.Status.UNAUTHORIZED)
                     .entity("Unauthorized user")
                     .build();
    }
  }

  private boolean isEarnerInTopTen(String username, List<LeaderboardInfo> leaderboard) {
    if (leaderboard.isEmpty())
      return false;
    return leaderboard.stream().map(LeaderboardInfo::getSocialId).anyMatch(username::equals);
  }

  private String computeTechnicalId(Identity identity) {
    if (!SpaceIdentityProvider.NAME.equals(identity.getProviderId())) {
      return null;
    }
    Space space = spaceService.getSpaceByPrettyName(identity.getRemoteId());
    return space == null ? null : space.getId();
  }

  private LeaderboardInfo buildCurrentUserRank(Date date,
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

  public static class LeaderboardInfo {

    String technicalId;

    String socialId;

    String avatarUrl;

    String remoteId;

    String fullname;

    long   score;

    String profileUrl;

    int    rank;

    public int getRank() {
      return rank;
    }

    public void setRank(int rank) {
      this.rank = rank;
    }

    public String getAvatarUrl() {
      return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
    }

    public String getRemoteId() {
      return remoteId;
    }

    public void setRemoteId(String remoteId) {
      this.remoteId = remoteId;
    }

    public String getFullname() {
      return fullname;
    }

    public void setFullname(String fullname) {
      this.fullname = fullname;
    }

    public long getScore() {
      return score;
    }

    public void setScore(long score) {
      this.score = score;
    }

    public String getProfileUrl() {
      return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
      this.profileUrl = profileUrl;
    }

    public String getSocialId() {
      return socialId;
    }

    public void setSocialId(String socialId) {
      this.socialId = socialId;
    }

    public String getTechnicalId() {
      return technicalId;
    }

    public void setTechnicalId(String technicalId) {
      this.technicalId = technicalId;
    }
  }

}

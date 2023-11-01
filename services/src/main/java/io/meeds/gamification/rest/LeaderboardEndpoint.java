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

import static io.meeds.gamification.rest.builder.LeaderboardBuilder.buildCurrentUserRank;
import static io.meeds.gamification.rest.builder.LeaderboardBuilder.buildLeaderboardEntities;
import static io.meeds.gamification.rest.builder.LeaderboardBuilder.toLeaderboardInfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.Period;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.rest.model.LeaderboardInfo;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.utils.Utils;
import io.meeds.portal.security.service.SecuritySettingService;
import io.meeds.social.translation.service.TranslationService;

import io.swagger.v3.oas.annotations.Parameter;

@Path("/gamification/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
public class LeaderboardEndpoint implements ResourceContainer {

  private static final String      MONTH_PERIOD_NAME     = "MONTH";

  private static final String      WEEK_PERIOD_NAME      = "WEEK";

  private static final int         DEFAULT_LOAD_CAPACITY = 10;

  private static final int         MAX_LOAD_CAPACITY     = 100;

  protected IdentityManager        identityManager       = null;

  protected RealizationService     realizationService    = null;

  protected RelationshipManager    relationshipManager;

  protected SpaceService           spaceService;

  protected ProgramService         programService;

  protected TranslationService     translationService;

  protected SecuritySettingService securitySettingService;

  public LeaderboardEndpoint(IdentityManager identityManager,
                             RealizationService realizationService,
                             RelationshipManager relationshipManager,
                             SpaceService spaceService,
                             ProgramService programService,
                             TranslationService translationService,
                             SecuritySettingService securitySettingService) {
    this.identityManager = identityManager;
    this.realizationService = realizationService;
    this.relationshipManager = relationshipManager;
    this.spaceService = spaceService;
    this.programService = programService;
    this.translationService = translationService;
    this.securitySettingService = securitySettingService;
  }

  @GET
  @Path("rank/all")
  public Response getAllLeadersByRank( // NOSONAR
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
    period = StringUtils.isBlank(period) ? Period.ALL.name() : period.toUpperCase();
    leaderboardFilter.setPeriod(period);
    String currentUser = Utils.getCurrentUser();
    leaderboardFilter.setCurrentUser(currentUser);

    List<LeaderboardInfo> result = new ArrayList<>();
    List<StandardLeaderboard> standardLeaderboards = realizationService.getLeaderboard(leaderboardFilter);
    if (standardLeaderboards == null) {
      return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
    int index = 1;
    boolean isAnonymous = StringUtils.isBlank(currentUser);
    for (StandardLeaderboard element : standardLeaderboards) {
      Identity identity = identityManager.getIdentity(element.getEarnerId());
      if (identity == null) {
        continue;
      }
      result.add(toLeaderboardInfo(spaceService, element, identity, isAnonymous, index));
      index++;
    }

    if (identityType.isUser()) {
      Date date = null;
      switch (leaderboardFilter.getPeriod()) {
      case WEEK_PERIOD_NAME:
        date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
        break;
      case MONTH_PERIOD_NAME:
        date = Date.from(LocalDate.now()
                                  .with(TemporalAdjusters.firstDayOfMonth())
                                  .atStartOfDay(ZoneId.systemDefault())
                                  .toInstant());
        break;
      default:
        date = null;
      }

      if (!isAnonymous) {
        // Check if the current user is already in top10
        LeaderboardInfo leader = buildCurrentUserRank(realizationService,
                                                      identityManager,
                                                      date,
                                                      leaderboardFilter.getProgramId(),
                                                      result);
        // Complete the final leaderboard
        if (leader != null) {
          result.add(leader);
        }
      }
    }
    return Response.ok(result, MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("filter")
  public Response filter( // NOSONAR
                         @Context
                         UriInfo uriInfo,
                         @QueryParam("programId")
                         Long programId,
                         @QueryParam("period")
                         String period,
                         @QueryParam("capacity")
                         int capacity) {
    if (!Utils.canAccessAnonymousResources(securitySettingService)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    // Init search criteria
    LeaderboardFilter leaderboardFilter = new LeaderboardFilter();
    leaderboardFilter.setProgramId(programId);
    String currentUser = Utils.getCurrentUser();
    if (programId != null
        && programId > 0
        && !programService.canViewProgram(programId, currentUser)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    leaderboardFilter.setPeriod(StringUtils.isBlank(period) ? Period.WEEK.name() : period.toUpperCase());
    leaderboardFilter.setLoadCapacity(capacity <= 0 ? DEFAULT_LOAD_CAPACITY : capacity);

    // hold leaderboard flow
    List<StandardLeaderboard> standardLeaderboards = realizationService.getLeaderboard(leaderboardFilter);
    List<LeaderboardInfo> leaderboardList = new ArrayList<>();
    if (standardLeaderboards == null || standardLeaderboards.isEmpty()) {
      return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).build();
    }

    boolean isAnonymous = StringUtils.isBlank(currentUser);
    int index = 1;
    for (StandardLeaderboard element : standardLeaderboards) {
      Identity identity = identityManager.getIdentity(element.getEarnerId());
      if (identity == null) {
        continue;
      }
      leaderboardList.add(toLeaderboardInfo(spaceService, element, identity, isAnonymous, index));
      index++;
    }
    // Check if the current user is already in top10
    Date date = null;
    switch (leaderboardFilter.getPeriod()) {
    case WEEK_PERIOD_NAME:
      date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
      break;
    case MONTH_PERIOD_NAME:
      date = Date.from(LocalDate.now()
                                .with(TemporalAdjusters.firstDayOfMonth())
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant());
      break;
    default:
      date = null;
    }

    if (!isAnonymous) {
      // Check if the current user is already in top10
      LeaderboardInfo leader = buildCurrentUserRank(realizationService,
                                                    identityManager,
                                                    date,
                                                    leaderboardFilter.getProgramId(),
                                                    leaderboardList);
      // Complete the final leaderboard
      if (leader != null) {
        leaderboardList.add(leader);
      }
    }
    return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("stats")
  @RolesAllowed("users")
  public Response stats(
                        @Context
                        HttpServletRequest request,
                        @QueryParam("username")
                        String username,
                        @QueryParam("period")
                        String period) {
    if (StringUtils.isBlank(username)) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    if (identity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    return getIdentityStats(request, identity.getId(), period, "programTitle");
  }

  @GET
  @Path("stats/{identityId}")
  public Response getIdentityStats(
                                   @Context
                                   HttpServletRequest request,
                                   @PathParam("identityId")
                                   String identityId,
                                   @QueryParam("period")
                                   String period,
                                   @QueryParam("expand")
                                   String expand) {
    if (!Utils.canAccessAnonymousResources(securitySettingService)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    period = StringUtils.isBlank(period) ? Period.ALL.name() : period.toUpperCase();
    // Check if the current user is already in top10
    Date startDate = null;
    switch (period) {
    case WEEK_PERIOD_NAME:
      startDate = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
      break;
    case MONTH_PERIOD_NAME:
      startDate = Date.from(LocalDate.now()
                                     .with(TemporalAdjusters.firstDayOfMonth())
                                     .atStartOfDay(ZoneId.systemDefault())
                                     .toInstant());
      break;
    default:
      startDate = null;
    }

    // Find user's stats
    List<PiechartLeaderboard> userStats = realizationService.getStatsByIdentityId(identityId,
                                                                                  startDate,
                                                                                  Calendar.getInstance().getTime());

    userStats = buildLeaderboardEntities(programService,
                                         translationService,
                                         userStats,
                                         Utils.getCurrentUser(),
                                         request.getLocale(),
                                         expand);
    return Response.ok(userStats, MediaType.APPLICATION_JSON).build();
  }

}

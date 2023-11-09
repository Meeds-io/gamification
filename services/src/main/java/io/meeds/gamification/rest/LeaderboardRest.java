/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.rest;

import static io.meeds.gamification.rest.builder.LeaderboardBuilder.buildLeaderboardInfos;
import static io.meeds.gamification.rest.builder.LeaderboardBuilder.buildPiechartLeaderboards;
import static io.meeds.gamification.rest.builder.LeaderboardBuilder.getCurrentPeriodStartDate;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.rest.resource.ResourceContainer;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/gamification/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
public class LeaderboardRest implements ResourceContainer {

  private static final int         DEFAULT_LOAD_CAPACITY = 10;

  protected IdentityManager        identityManager       = null;

  protected RealizationService     realizationService    = null;

  protected RelationshipManager    relationshipManager;

  protected SpaceService           spaceService;

  protected ProgramService         programService;

  protected TranslationService     translationService;

  protected SecuritySettingService securitySettingService;

  public LeaderboardRest(IdentityManager identityManager,
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
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Retrieve the list leaderboard users/spaces including the selected identity to retrieve its rank", method = "GET")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Request fulfilled"),
    @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
  })
  public Response getIdentityLeaderboard( // NOSONAR
                                         @Parameter(description = "Program technical identifier to filter")
                                         @QueryParam("programId")
                                         Long programId,
                                         @Parameter(description = "Type of leaderboard, whether users or spaces")
                                         @DefaultValue("USER")
                                         @QueryParam("identityType")
                                         IdentityType identityType,
                                         @Parameter(description = "Identity technical identifier to filter")
                                         @QueryParam("identityId")
                                         Long identityId,
                                         @Parameter(description = "Current period: WEEK, MONTH or ALL")
                                         @DefaultValue("WEEK")
                                         @QueryParam("period")
                                         String period,
                                         @Parameter(description = "Current period: WEEK, MONTH or ALL")
                                         @DefaultValue("0")
                                         @QueryParam("limit")
                                         int limit) {
    if (!Utils.canAccessAnonymousResources(securitySettingService)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    String currentUser = Utils.getCurrentUser();
    boolean isAnonymous = StringUtils.isBlank(currentUser);
    if (identityType.isSpace() && isAnonymous) {
      return Response.status(Status.UNAUTHORIZED).build();
    } else if (identityType.isUser() && (identityId == null || identityId == 0)) {
      identityId = Utils.getCurrentUserIdentityId();
    }

    LeaderboardFilter leaderboardFilter = new LeaderboardFilter();
    leaderboardFilter.setIdentityType(identityType);
    leaderboardFilter.setProgramId(programId);
    leaderboardFilter.setIdentityId(identityId);
    leaderboardFilter.setPeriod(StringUtils.isBlank(period) ? Period.WEEK.name() : period.toUpperCase());
    leaderboardFilter.setLoadCapacity(limit < 0 ? DEFAULT_LOAD_CAPACITY : limit);
    try {
      List<StandardLeaderboard> standardLeaderboards = limit == 0 ? Collections.emptyList() :
                                                                    realizationService.getLeaderboard(leaderboardFilter, currentUser);
      List<LeaderboardInfo> leaderboardList = buildLeaderboardInfos(realizationService,
                                                                    identityManager,
                                                                    spaceService,
                                                                    standardLeaderboards,
                                                                    identityType,
                                                                    identityId,
                                                                    programId,
                                                                    period,
                                                                    isAnonymous);
      return Response.ok(leaderboardList).build();
    } catch (IllegalAccessException e) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
  }

  @GET
  @Path("stats/{identityId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Retrieves identity gamification statistics classified by program, ready to be displayed in a pie chart", method = "GET")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Request fulfilled"),
    @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
  })
  public Response getIdentityStats(
                                   @Context
                                   HttpServletRequest request,
                                   @Parameter(description = "Identity technical identifier")
                                   @PathParam("identityId")
                                   String identityId,
                                   @Parameter(description = "Current period to consider. Possible values: WEEK, MONTH or ALL")
                                   @DefaultValue("WEEK")
                                   @QueryParam("period")
                                   String period) {
    if (!Utils.canAccessAnonymousResources(securitySettingService)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    period = StringUtils.isBlank(period) ? Period.ALL.name() : period.toUpperCase();
    List<PiechartLeaderboard> userStats = realizationService.getLeaderboardStatsByIdentityId(identityId,
                                                                                  getCurrentPeriodStartDate(period),
                                                                                  Calendar.getInstance().getTime());
    userStats = buildPiechartLeaderboards(programService,
                                          translationService,
                                          userStats,
                                          Utils.getCurrentUser(),
                                          request == null ? Locale.ENGLISH : request.getLocale());
    return Response.ok(userStats).build();
  }

}

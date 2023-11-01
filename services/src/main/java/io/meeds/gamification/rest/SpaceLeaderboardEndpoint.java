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
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.rest.model.LeaderboardInfo;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.utils.GamificationUtils;

@Path("/gamification/space/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class SpaceLeaderboardEndpoint implements ResourceContainer {

  private static final Log      LOG                   = ExoLogger.getLogger(SpaceLeaderboardEndpoint.class);

  private static final String   YOUR_CURRENT_RANK_MSG = "Your current rank";

  private final CacheControl    cacheControl;

  protected RealizationService realizationService   = null;

  protected IdentityManager     identityManager       = null;

  protected SpaceService        spaceService          = null;

  public SpaceLeaderboardEndpoint(RealizationService realizationService,
                                  IdentityManager identityManager,
                                  SpaceService spaceService) {

    this.cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    this.realizationService = realizationService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
  }

  @GET
  @RolesAllowed("users")
  @Path("overall")
  public Response getAllLeadersByRank(@Context UriInfo uriInfo, @QueryParam("url") String url) {
    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      // Init search criteria
      LeaderboardFilter leaderboardFilter = new LeaderboardFilter();

      // Hold leaderboard flow
      List<LeaderboardInfo> leaderboardList = new ArrayList<>();

      LeaderboardInfo leaderboardInfo = null;

      Identity identity = null;

      try {
        // Find current space
        Space space = GamificationUtils.extractSpaceNameFromUrl(url);
        // Filter users to add to leaderboard according to filter criteria
        List<StandardLeaderboard> standardLeaderboards = realizationService.getLeaderboard(leaderboardFilter);

        if (standardLeaderboards == null) {
          return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        }

        // Build StandardLeaderboard flow only when the returned list is not
        // null
        for (StandardLeaderboard element : standardLeaderboards) {

          // Load Social identity
          identity = identityManager.getIdentity(element.getEarnerId());

          if (spaceService.isMember(space, identity.getRemoteId())) {
            leaderboardInfo = new LeaderboardInfo();
            String technicalId = computeTechnicalId(identity);
            leaderboardInfo.setTechnicalId(technicalId);
            leaderboardInfo.setSocialId(identity.getId());
            leaderboardInfo.setScore(element.getReputationScore());
            leaderboardInfo.setRemoteId(identity.getRemoteId());
            leaderboardInfo.setFullname(identity.getProfile().getFullName());
            leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
            leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());
            leaderboardList.add(leaderboardInfo);
          }
          if (leaderboardList.size() == 10)
            return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        }
        return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

      } catch (Exception e) {

        LOG.error("Error building leaderboard ", e);

        return Response.serverError()
                       .cacheControl(cacheControl)
                       .entity("Error building leaderboard")
                       .build();
      }

    } else {
      return Response.status(Response.Status.UNAUTHORIZED)
                     .cacheControl(cacheControl)
                     .entity("Unauthorized user")
                     .build();
    }
  }

  @GET
  @RolesAllowed("users")
  @Path("filter")
  public Response filter(@Context UriInfo uriInfo, // NOSONAR
                         @QueryParam("program") Long programId,
                         @QueryParam("period") String period,
                         @QueryParam("url") String url,
                         @QueryParam("capacity") String capacity) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      // Init search criteria
      LeaderboardFilter leaderboardFilter = new LeaderboardFilter();

        leaderboardFilter.setProgramId(programId);

      if (StringUtils.isNotBlank(period))
        leaderboardFilter.setPeriod(period);

      // hold leaderboard flow
      LeaderboardInfo leaderboardInfo = null;

      // Build leaderboard list
      List<LeaderboardInfo> leaderboardInfoList = null;

      try {
        // Find current space
        Space space = GamificationUtils.extractSpaceNameFromUrl(url);

        List<StandardLeaderboard> standardLeaderboards = realizationService.getLeaderboard(leaderboardFilter);

        if (standardLeaderboards == null) {
          return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        }

        leaderboardInfoList = new ArrayList<>();

        Identity identity = null;
        for (StandardLeaderboard leader : standardLeaderboards) {

          // Load Social identity
          identity = identityManager.getIdentity(leader.getEarnerId());

          if (spaceService.isMember(space, identity.getRemoteId()) && leaderboardInfoList.size() < Integer.parseInt(capacity)) {
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
        }
        // Check if the current user is already in top10
        Date date = null;
        switch (leaderboardFilter.getPeriod()) { // NOSONAR
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
        LeaderboardInfo leader = buildCurrentUserRank(conversationState.getIdentity().getUserId(),
                                                                          date,
                                                                          leaderboardFilter.getProgramId(),
                                                                          leaderboardInfoList);
        // Complete the final leaderboard
        if (leader != null)
          leaderboardInfoList.add(leader);

        return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

      } catch (Exception e) {

        LOG.error("Error filtering leaderbaord by Doamin : {} and by Period {} ",
                  leaderboardFilter.getProgramId(),
                  leaderboardFilter.getPeriod(),
                  e);

        return Response.serverError()
                       .cacheControl(cacheControl)
                       .entity("Error filtering leaderboard")
                       .build();
      }

    } else {
      return Response.status(Response.Status.UNAUTHORIZED)
                     .cacheControl(cacheControl)
                     .entity("Unauthorized user")
                     .build();
    }
  }

  private String computeTechnicalId(Identity identity) {
    if(!SpaceIdentityProvider.NAME.equals(identity.getProviderId())) {
      return null;
    }
    Space space = spaceService.getSpaceByPrettyName(identity.getRemoteId());
    return space == null ? null : space.getId();
  }

  private LeaderboardInfo buildCurrentUserRank(String identityId,
                                                                   Date date,
                                                                   Long programId,
                                                                   List<LeaderboardInfo> leaderboardList) {
    if (CollectionUtils.isEmpty(leaderboardList)) {
      return null;
    }
    LeaderboardInfo leaderboardInfo = null;
    String currentUser = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identityId).getId();
    if (!isCurrentUserInTopTen(currentUser, leaderboardList)) {

      // Get GaamificationScore for current user
      int rank = realizationService.getLeaderboardRank(currentUser, date, programId);

      if (rank > 0) {

        leaderboardInfo = new LeaderboardInfo();

        // Set score
        leaderboardInfo.setRank(rank);

        // Set username
        leaderboardInfo.setRemoteId(YOUR_CURRENT_RANK_MSG);

        // Set FullName
        leaderboardInfo.setFullname(YOUR_CURRENT_RANK_MSG);

        // Set avatar
        leaderboardInfo.setAvatarUrl(YOUR_CURRENT_RANK_MSG);

        // Set profile URL
        leaderboardInfo.setProfileUrl(YOUR_CURRENT_RANK_MSG);
      }
    }
    return leaderboardInfo;

  }

  private boolean isCurrentUserInTopTen(String username, List<LeaderboardInfo> leaderboard) {

    if (leaderboard.isEmpty())
      return false;

    return leaderboard.stream().map(LeaderboardInfo::getSocialId).anyMatch(username::equals);
  }
}

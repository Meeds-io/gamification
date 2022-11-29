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
package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.model.LeaderboardFilter;
import org.exoplatform.addons.gamification.utils.GamificationUtils;
import org.exoplatform.addons.gamification.service.GamificationService;
import org.exoplatform.addons.gamification.service.StandardLeaderboard;
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

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/gamification/space/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class SpaceLeaderboardEndpoint implements ResourceContainer {

  private static final Log      LOG                   = ExoLogger.getLogger(ManageBadgesEndpoint.class);

  private final static String   YOUR_CURRENT_RANK_MSG = "Your current rank";

  private final CacheControl    cacheControl;

  protected GamificationService gamificationService;

  protected IdentityManager     identityManager;

  protected SpaceService        spaceService;

  public SpaceLeaderboardEndpoint(GamificationService gamificationService,
                                  IdentityManager identityManager,
                                  SpaceService spaceService) {

    this.cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    this.gamificationService = gamificationService;
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
      List<LeaderboardEndpoint.LeaderboardInfo> leaderboardList = new ArrayList<>();

      LeaderboardEndpoint.LeaderboardInfo leaderboardInfo = null;

      Identity identity = null;

      try {
        // Find current space
        Space space = GamificationUtils.extractSpaceNameFromUrl(url);
        // Filter users to add to leaderboard according to filter criteria
        List<StandardLeaderboard> standardLeaderboards = gamificationService.filter(leaderboardFilter);

        if (standardLeaderboards == null) {
          return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        }

        // Build StandardLeaderboard flow only when the returned list is not
        // null
        for (StandardLeaderboard element : standardLeaderboards) {

          // Load Social identity
          identity = identityManager.getIdentity(element.getEarnerId());

          if (spaceService.isMember(space, identity.getRemoteId())) {
            leaderboardInfo = new LeaderboardEndpoint.LeaderboardInfo();
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
  public Response filter(@Context UriInfo uriInfo,
                         @QueryParam("domain") String domain,
                         @QueryParam("period") String period,
                         @QueryParam("url") String url,
                         @QueryParam("capacity") String capacity) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      // Init search criteria
      LeaderboardFilter leaderboardFilter = new LeaderboardFilter();

      if (StringUtils.isNotBlank(domain) && !domain.equalsIgnoreCase("null"))
        leaderboardFilter.setDomain(domain);

      if (StringUtils.isNotBlank(period))
        leaderboardFilter.setPeriod(period);

      // hold leaderboard flow
      LeaderboardEndpoint.LeaderboardInfo leaderboardInfo = null;

      // Build leaderboard list
      List<LeaderboardEndpoint.LeaderboardInfo> leaderboardInfoList = null;

      try {
        // Find current space
        Space space = GamificationUtils.extractSpaceNameFromUrl(url);

        List<StandardLeaderboard> standardLeaderboards = gamificationService.filter(leaderboardFilter);

        if (standardLeaderboards == null) {
          return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        }

        leaderboardInfoList = new ArrayList<LeaderboardEndpoint.LeaderboardInfo>();

        Identity identity = null;

        // Get current User identity

        Identity currentIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                                                                       conversationState.getIdentity().getUserId());

        for (StandardLeaderboard leader : standardLeaderboards) {

          // Load Social identity
          identity = identityManager.getIdentity(leader.getEarnerId());

          if (spaceService.isMember(space, identity.getRemoteId()) && leaderboardInfoList.size() < Integer.parseInt(capacity)) {
            leaderboardInfo = new LeaderboardEndpoint.LeaderboardInfo();
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
          // if (leaderboardInfoList.size() == 10) return
          // Response.ok(leaderboardInfoList,
          // MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
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
        LeaderboardEndpoint.LeaderboardInfo leader = buildCurrentUserRank(conversationState.getIdentity().getUserId(),
                                                                          date,
                                                                          leaderboardFilter.getDomain(),
                                                                          leaderboardInfoList);
        // Complete the final leaderboard
        if (leader != null)
          leaderboardInfoList.add(leader);

        return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

      } catch (Exception e) {

        LOG.error("Error filtering leaderbaord by Doamin : {} and by Period {} ",
                  leaderboardFilter.getDomain(),
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

  private LeaderboardEndpoint.LeaderboardInfo buildCurrentUserRank(String identityId,
                                                                   Date date,
                                                                   String domain,
                                                                   List<LeaderboardEndpoint.LeaderboardInfo> leaderboardList) {
    if (leaderboardList.size() == 0)
      return null;
    LeaderboardEndpoint.LeaderboardInfo leaderboardInfo = null;
    String currentUser = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identityId).getId();
    if (!isCurrentUserInTopTen(currentUser, leaderboardList)) {

      // Get GaamificationScore for current user
      int rank = gamificationService.getLeaderboardRank(currentUser, date, domain);

      if (rank > 0) {

        leaderboardInfo = new LeaderboardEndpoint.LeaderboardInfo();

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

  private boolean isCurrentUserInTopTen(String username, List<LeaderboardEndpoint.LeaderboardInfo> leaderboard) {

    if (leaderboard.isEmpty())
      return false;

    return leaderboard.stream().map(LeaderboardEndpoint.LeaderboardInfo::getSocialId).anyMatch(username::equals);
  }
}

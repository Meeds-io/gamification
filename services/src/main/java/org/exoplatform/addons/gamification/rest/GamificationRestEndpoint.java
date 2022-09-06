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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter.Period;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import java.text.ParseException;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

@Path("/gamification/api/v1")
@RolesAllowed("users")
public class GamificationRestEndpoint implements ResourceContainer {
    private static final Log LOG = ExoLogger.getLogger(GamificationRestEndpoint.class);
    private final CacheControl cacheControl;
    private GamificationService gamificationService;
    private IdentityManager identityManager;
    private DomainService domainService;
    private RuleService ruleService;
    private static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATEFORMAT = "dd-MM-yyyy";

    public GamificationRestEndpoint(GamificationService gamificationService, IdentityManager identityManager, DomainService domainService, RuleService ruleService) {
        this.cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        this.gamificationService = gamificationService;
        this.identityManager = identityManager;
        this.domainService = domainService;
        this.ruleService = ruleService;
    }

    /**
     * Return all earned points by a user
     *
     * @param userId : user social id
     * @param period : Period of time
     * @return : and object of type GamificationPoints
     */
    @Path("points")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getAllPointsByUserId(
                                         @QueryParam("userId")
                                         String userId,
                                         @QueryParam("period")
                                         String period) {
      if (StringUtils.isBlank(userId)) {
        LOG.warn("Enable to serve request due to bad request parameter «userId»");
        return Response.ok(new GamificationPoints().userId(userId)
                                                   .points(0L)
                                                   .code("2")
                                                   .message("userId parameter must be specified"))
                       .build();
      }
      Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
      Long earnedXP = 0L;
      if (period == null || StringUtils.equalsIgnoreCase(Period.ALL.name(), period)) {
        earnedXP = gamificationService.findReputationByEarnerId(identity.getId());
      } else {
        period = period.toUpperCase();
        // Check if the current user is already in top10
        Date fromDate = null;
        switch (period) {
        case "WEEK":
          fromDate = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
          break;
        case "MONTH":
          fromDate = Date.from(LocalDate.now()
                                        .with(TemporalAdjusters.firstDayOfMonth())
                                        .atStartOfDay(ZoneId.systemDefault())
                                        .toInstant());
          break;
        }
        Date toDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        earnedXP = gamificationService.findUserReputationScoreBetweenDate(identity.getId(), fromDate, toDate);
      }
      return Response.ok(new GamificationPoints().userId(userId)
                                                 .points(earnedXP)
                                                 .code("0")
                                                 .message("Gamification API is called successfully"))
                     .build();
    }

    /**
     * Return earned points by a user during a given period
     *
     * @param userId         : user social id
     * @param startDateEntry : Date from when gamification api filter earned points
     * @param endDateEntry   : Date until when gamification api filter eearned points
     * @return : and object of type GamificationPoints
     */
    @Path("points/date")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getAllPointsByUserIdByDate(@QueryParam("userId") String userId, @QueryParam("startDate") String startDateEntry, @QueryParam("endDate") String endDateEntry) {
      if (StringUtils.isBlank(userId)) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("'userId' field is mandatory")
                       .build();
      }
      if (StringUtils.isBlank(startDateEntry)) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("'startDate' field is mandatory")
                       .build();
      }
      if (StringUtils.isBlank(endDateEntry)) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("'endDate' field is mandatory")
                       .build();
      }
      Date startDate;
      try {
        startDate = DateUtils.parseDate(startDateEntry, DATETIMEFORMAT, DATEFORMAT);
      } catch (ParseException pe) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("'startDate' has to use format 'yyyy-MM-dd HH:mm:ss' or 'dd-MM-yyyy'")
                       .build();
      }
      Date endDate;
      try {
        endDate = DateUtils.parseDate(endDateEntry, DATETIMEFORMAT, DATEFORMAT);
      } catch (ParseException pe) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("'endDate' has to use format 'yyyy-MM-dd HH:mm:ss' or 'dd-MM-yyyy'")
                       .build();
      }
      try {
        if (startDate.after(endDate)) {
          return Response.status(Status.BAD_REQUEST)
                         .entity("'endDate' has to be after 'startDate'")
                         .build();
        }
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
        Long earnedXP = gamificationService.findUserReputationScoreBetweenDate(identity.getId(), startDate, endDate);
        return Response.ok(new GamificationPoints().userId(userId)
                                                   .points(earnedXP)
                                                   .code("0")
                                                   .message("Gamification API is called successfully"))
                       .build();
      } catch (Exception e) {
        LOG.warn("Error while fetching earned points for user {} in the specified period - Gamification public API", userId, e);
        return Response.serverError()
                       .entity(new GamificationPoints().userId(userId)
                                                       .points(0L)
                                                       .code("2")
                                                       .message("Error while fetching earned points by period"))
                       .build();
      }
    }

    @Path("leaderboard/date")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getLeaderboardByDate(@Context UriInfo uriInfo,
                                         @QueryParam("earnerType") String earnerType,
                                         @QueryParam("startDate") String startDateEntry,
                                         @QueryParam("endDate")
                                         String endDateEntry) {
      if (StringUtils.isBlank(startDateEntry)) {
        return Response.status(Status.BAD_REQUEST).entity("'startDate' field is mandatory").build();
      }
      if (StringUtils.isBlank(endDateEntry)) {
        return Response.status(Status.BAD_REQUEST).entity("'endDate' field is mandatory").build();
      }
      Date startDate;
      try {
        startDate = DateUtils.parseDate(startDateEntry, DATETIMEFORMAT, DATEFORMAT);
      } catch (ParseException pe) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("'startDate' has to use format 'yyyy-MM-dd HH:mm:ss' or 'dd-MM-yyyy'")
                       .build();
      }
      Date endDate;
      try {
        endDate = DateUtils.parseDate(endDateEntry, DATETIMEFORMAT, DATEFORMAT);
      } catch (ParseException pe) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("'endDate' has to use format 'yyyy-MM-dd HH:mm:ss' or 'dd-MM-yyyy'")
                       .build();
      }
      if (startDate.after(endDate)) {
        return Response.status(Status.BAD_REQUEST).entity("'endDate' has to be after 'startDate'").build();
      }
      List<StandardLeaderboard> leaderboard = gamificationService.findAllLeaderboardBetweenDate(IdentityType.getType(earnerType),
                                                                                                startDate,
                                                                                                endDate);
      return Response.ok(new GamificationPoints().code("0")
                                                 .leaderboard(leaderboard)
                                                 .message("Gamification API is called successfully"))
                     .build();

    }

    /**
     * Return enabled domains
     *
     * @return : list of enabled domains
     */
    @Path("domains")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getDomains() {
      return Response.ok(domainService.getEnabledDomains()).build();
    }


    /**
     * Return all events
     *
     * @return : list of all events
     */
    @Path("events")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getAllEvents() {
      return Response.ok(ruleService.getAllEvents()).build();
    }

    public static class GamificationPoints {
        private String userId;
        private Long points;
        private String code;
        private String message;
        private List<StandardLeaderboard> leaderboard;

        public GamificationPoints() {
        }

        public GamificationPoints userId(String userId) {
            this.userId = userId;
            return this;
        }
        public GamificationPoints points(Long points) {
            this.points = points;
            return this;
        }
        public GamificationPoints code(String code) {
            this.code = code;
            return this;
        }
        public GamificationPoints message(String message) {
            this.message = message;
            return this;
        }
        public GamificationPoints leaderboard(List<StandardLeaderboard> leaderboard) {
            this.leaderboard = leaderboard;
            return this;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getPoints() {
            return points;
        }

        public void setPoints(Long points) {
            this.points = points;
        }

        public List<StandardLeaderboard> getLeaderboard() {
            return leaderboard;
        }

        public void setLeaderboard(List<StandardLeaderboard> leaderboard) {
            this.leaderboard = leaderboard;
        }
    }

}

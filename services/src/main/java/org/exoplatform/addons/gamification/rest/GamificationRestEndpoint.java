package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.utils.CommonsUtils;
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
import java.text.ParseException;
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
     * @return : and object of type GamificationPoints
     */
    @Path("points")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getAllPointsByUserId(@QueryParam("userId") String userId) {
        if (StringUtils.isBlank(userId)) {
            LOG.warn("Enable to serve request due to bad request parameter «userId»");
            return Response.ok(new GamificationPoints().userId(userId).points(0L).code("2").message("userId parameter must be specified")).build();
        }
        try {
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
            Long earnedXP = gamificationService.findUserReputationBySocialId(identity.getId());
            return Response.ok(new GamificationPoints().userId(userId).points(earnedXP).code("0").message("Gamification API is called successfully")).build();

        } catch (Exception e) {
            LOG.error("Error while fetching earned points for user {} - Gamification public API", userId, e);
            return Response.ok(new GamificationPoints().userId(userId).points(0L).code("2").message("Error while fetching all earned points")).build();
        }

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
            LOG.warn("Enable to serve request due to bad request parameter «userId»");
            return Response.ok(new GamificationPoints().userId(userId).points(0L).code("2").message("userId parameter must be specified")).build();
        }
        try {
            Date startDate = DateUtils.parseDate(startDateEntry, "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy");
            Date endDate = DateUtils.parseDate(endDateEntry, "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy");

            if (startDate.after(endDate)) {
                return Response.ok(new GamificationPoints().userId(userId).points(0L).code("1").message("date parameters are not correctly set")).build();
            }
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
            Long earnedXP = gamificationService.findUserReputationScoreBetweenDate(identity.getId(), startDate, endDate);
            return Response.ok(new GamificationPoints().userId(userId).points(earnedXP).code("0").message("Gamification API is called successfully")).build();
        } catch (ParseException pe) {
            LOG.error("Error to parse parameters {} or {} ", startDateEntry, endDateEntry);
            return Response.serverError()
                    .cacheControl(cacheControl)
                    .entity("Error to parse startDate or endDate to Date object please use the following pattern : dd-MM-yyyy")
                    .build();
        } catch (Exception e) {
            LOG.error("Error while fetching earned points for user {} in the specified period - Gamification public API", userId, e);
            return Response.ok(new GamificationPoints().userId(userId).points(0L).code("2").message("Error while fetching earned points by period")).build();
        }
    }
    @Path("leaderboard/date")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getLeaderboardByDate(@Context UriInfo uriInfo, @QueryParam("startDate") String startDateEntry, @QueryParam("endDate") String endDateEntry) {

        try {
            Date startDate = DateUtils.parseDate(startDateEntry, "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy");
            Date endDate = DateUtils.parseDate(endDateEntry, "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy");

            if (startDate.after(endDate)) {
                return Response.ok(new GamificationPoints().code("2").message("Dates parameters are not set correctly")).build();
            }
            List<StandardLeaderboard> leaderboard = gamificationService.findAllLeaderboardBetweenDate(startDate, endDate);
            return Response.ok(new GamificationPoints().code("0").leaderboard(leaderboard).message("Gamification API is called successfully")).build();

        } catch (ParseException pe) {
            LOG.error("Error to parse parameters {} or {} ", startDateEntry, endDateEntry);
            return Response.serverError()
                    .cacheControl(cacheControl)
                    .entity("Error to parse startDate or endDate to Date object please use the following pattern : dd-MM-yyyy")
                    .build();
        } catch (Exception e) {
            LOG.error("Error while building gloabl leaderboard between dates {} and {} - Gamification public API", startDateEntry, endDateEntry, e);
            return Response.ok(new GamificationPoints().code("2").message("Error while fetching earned points by period")).build();
        }

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

        try {
            return Response.ok(domainService.getEnabledDomains()).build();

        } catch (Exception e) {
            LOG.error("Error while fetching Enabled Domains", e);
            return Response.serverError().entity("Error while fetching enabled domains").build();
        }

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

        try {
            return Response.ok(ruleService.getAllEvents()).build();

        } catch (Exception e) {
            LOG.error("Error while fetching All Events", e);
            return Response.serverError().entity("Error while fetching all events").build();
        }

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

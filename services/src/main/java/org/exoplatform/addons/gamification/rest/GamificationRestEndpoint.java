package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
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
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

@Path("/gamification/api/v1")
@RolesAllowed("users")
public class GamificationRestEndpoint implements ResourceContainer {
    private static final Log LOG = ExoLogger.getLogger(GamificationRestEndpoint.class);
    private final CacheControl cacheControl;
    private GamificationService gamificationService;
    private IdentityManager identityManager;

    public GamificationRestEndpoint(GamificationService gamificationService, IdentityManager identityManager) {
        this.cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        this.gamificationService = gamificationService;
        this.identityManager = identityManager;
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

            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "userId parameter must be specified")).build();
        }
        try {
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
            Long earnedXP = gamificationService.findUserReputationBySocialId(identity.getId());
            return Response.ok(new GamificationPoints().build(userId, earnedXP, "0", "Gamification API is called successfully")).build();

        } catch (Exception e) {
            LOG.error("Error while fetching earned points for user {} - Gamification public API", userId, e);
            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "Error while fetching all earned points")).build();
        } finally {

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
            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "userId parameter must be specified")).build();
        }
        try {
            Date startDate = DateUtils.parseDate(startDateEntry, new String[]{"yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy"});
            Date endDate = DateUtils.parseDate(endDateEntry, new String[]{"yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy"});

            if (startDate.after(endDate)) {
                return Response.ok(new GamificationPoints().build(userId, 0L, "1", "date parameters are not correctly set")).build();
            }
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
            Long earnedXP = gamificationService.findUserReputationScoreBetweenDate(identity.getId(), startDate, endDate);

            return Response.ok(new GamificationPoints().build(userId, earnedXP, "0", "Gamification API is called successfully")).build();

        } catch (ParseException pe) {
            LOG.error("Error to parse parameters {} or {} ", startDateEntry, endDateEntry);
            return Response.serverError()
                    .cacheControl(cacheControl)
                    .entity("Error to parse startDate or endDate to Date object please use the following pattern : dd-MM-yyyy")
                    .build();
        } catch (Exception e) {
            LOG.error("Error while fetching earned points for user {} in the specified period - Gamification public API", userId, e);
            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "Error while fetching earned points by period")).build();
        } finally {

        }
    }

    /**
     * Return earned points by a user during the current month
     *
     * @param userId : user social id
     * @return : and object of type GamificationPoints
     */
    @Path("points/month")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getAllPointsByUserIdAndCurrentMonth(@QueryParam("userId") String userId) {
        if (StringUtils.isBlank(userId)) {
            LOG.warn("Enable to serve request due to bad request parameter «userId»");
            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "userId parameter must be specified")).build();
        }
        try {
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
            Long earnedXP = gamificationService.findUserReputationScoreByMonth(identity.getId(), (Date.from(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant())));
            return Response.ok(new GamificationPoints().build(userId, earnedXP, "0", "Gamification API is called successfully")).build();

        } catch (Exception e) {
            LOG.error("Error while fetching earned points for user {} in current month- Gamification public API", userId, e);
            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "Error while fetching earned points during the current month")).build();
        } finally {

        }
    }

    /**
     * Return earned points by a user during the current week
     *
     * @param userId : user social id
     * @return : : and object of type GamificationPoints
     */
    @Path("points/week")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response getAllPointsByUserIdAndCurrentWeek(@QueryParam("userId") String userId) {
        if (StringUtils.isBlank(userId)) {
            LOG.warn("Enable to serve request due to bad request parameter «userId»");
            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "userId parameter must be specified")).build();
        }
        try {
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
            Long earnedXP = gamificationService.findUserReputationScoreByMonth(identity.getId(), (Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant())));
            return Response.ok(new GamificationPoints().build(userId, earnedXP, "0", "Gamification API is called successfully")).build();

        } catch (Exception e) {
            LOG.error("Error while fetching earned points for user {} in current week- Gamification public API", userId, e);
            return Response.ok(new GamificationPoints().build(userId, 0L, "2", "Error while fetching earned points during the current week")).build();
        } finally {

        }
    }

    public static class GamificationPoints {
        private String userId;
        private Long points;
        private String code;
        private String message;

        public GamificationPoints() {
        }

        public GamificationPoints(String userId, Long points, String code, String message) {
            this.userId = userId;
            this.points = points;
            this.code = code;
            this.message = message;
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

        public GamificationPoints build(String userId, Long points, String code, String message) {

            return new GamificationPoints(userId, points, code, message);
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
    }

}

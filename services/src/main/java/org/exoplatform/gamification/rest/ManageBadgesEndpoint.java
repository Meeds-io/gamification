package org.exoplatform.gamification.rest;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.service.configuration.BadgeService;
import org.exoplatform.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/gamification/badges")
public class ManageBadgesEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

    protected BadgeService badgeService = null;

    public ManageBadgesEndpoint() {

        badgeService = CommonsUtils.getService(BadgeService.class);

    }

    @GET
    @RolesAllowed("users")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public Response getAllBadges(@Context UriInfo uriInfo) {

        CacheControl cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        try {
            List<BadgeDTO> allBadges = badgeService.getAllBadges();

            return Response.ok(allBadges, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        }

    }

    @PUT
    @RolesAllowed("users")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add")
    public Response AddBadge(@Context UriInfo uriInfo) {

        CacheControl cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        try {
            List<BadgeDTO> allRules = badgeService.getAllBadges();

            return Response.ok(allRules, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        }

    }

    @DELETE
    @RolesAllowed("users")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public Response deleteBadge(@Context UriInfo uriInfo, @QueryParam("badgeTitle") String ruleTitle) {

        CacheControl cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        try {
            //--- Remove the rule
            badgeService.deleteBadge(ruleTitle);


            return Response.ok("Deleted " + ruleTitle, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        }

    }
}

package org.exoplatform.gamification.rest;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.service.configuration.BadgeService;
import org.exoplatform.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Path("/gamification/badges")
@Produces(MediaType.APPLICATION_JSON)
public class ManageBadgesEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

    private final CacheControl cacheControl;

    protected BadgeService badgeService = null;

    public ManageBadgesEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        badgeService = CommonsUtils.getService(BadgeService.class);

    }

    @GET
    @RolesAllowed("users")
    @Path("/all")
    public Response getAllBadges(@Context UriInfo uriInfo) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            try {
                List<BadgeDTO> allBadges = badgeService.getAllBadges();

                return Response.ok(allBadges, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error listing all badges ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all badges")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }

    }

    @POST
    @RolesAllowed("administrators")
    @Path("/add")
    public Response addBadge(@Context UriInfo uriInfo, BadgeDTO badgeDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();

            try {

                // Compute rule's data
                badgeDTO.setId(null);
                badgeDTO.setCreatedBy(currentUserName);
                badgeDTO.setLastModifiedBy(currentUserName);
                badgeDTO.setLastModifiedDate(new Date());

                //--- Add badge
                badgeDTO = badgeService.addBadge(badgeDTO);

                return Response.ok().cacheControl(cacheControl).entity(badgeDTO).build();


            } catch (Exception e) {

                LOG.error("Error adding new badge {} by {} ", badgeDTO.getTitle(), currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error adding new rule")
                        .build();

            }

        } else {

            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }

    }

    @PUT
    @RolesAllowed("administrators")
    @Path("/update")
    public Response updateBadge(@Context UriInfo uriInfo, @Context HttpServletRequest request, BadgeDTO badgeDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();
            try {

                //TODO : Load locale
                Locale lc = request.getLocale();

                // Compute rule's data
                badgeDTO.setCreatedBy(currentUserName);
                badgeDTO.setLastModifiedBy(currentUserName);
                badgeDTO.setLastModifiedDate(new Date());

                //--- Add rule
                badgeDTO = badgeService.updateBadge(badgeDTO);

                return Response.ok().cacheControl(cacheControl).entity(badgeDTO).build();

            } catch (Exception e) {

                LOG.error("Error updating badge {} by {} ", badgeDTO.getTitle(), currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error adding new badge")
                        .build();
            }

        } else {

            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }


    }

    @DELETE
    @RolesAllowed("administrators")
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

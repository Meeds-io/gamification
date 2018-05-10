package org.exoplatform.gamification.rest;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

@Path("/gamification/reputation")
@Produces(MediaType.APPLICATION_JSON)
public class UserReputationEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(UserReputationEndpoint.class);

    private final CacheControl cacheControl;

    //protected ReputationService reputationService = null;

    public UserReputationEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        //reputationService = CommonsUtils.getService(ReputationService.class);
    }

    @GET
    @RolesAllowed("users")
    @Path("point/status")
    public Response getReputationStatus(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {
            try {
                //List<ReputationDTO> allRules = reputationService.getAllRules();

                JSONObject reputation = new JSONObject();
                reputation.put("points", 3);
                reputation.put("max", 10);


                return Response.ok().cacheControl(cacheControl).entity(reputation.toString()).build();

            } catch (Exception e) {

                LOG.error("Error listing all rules ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all rules")
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
    @Path("badge/all")
    public Response getUserBadges(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {
            try {
                //List<ReputationDTO> allRules = reputationService.getBadgesByUser();

                /** This is a fake */
                JSONArray allBadges = new JSONArray();
                JSONObject reputation = null;

                for (int i=0; i < 3; i++) {
                    reputation = new JSONObject();

                    reputation.put("url", "https://image.flaticon.com/icons/svg/511/511131.svg");
                    reputation.put("description", "This is a discription ");
                    reputation.put("id", i);
                    reputation.put("title", "Badge Expert");
                    reputation.put("neededScore", 100);
                    allBadges.put(reputation);

                }

                return Response.ok().cacheControl(cacheControl).entity(allBadges.toString()).build();

            } catch (Exception e) {

                LOG.error("Error listing all rules ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all rules")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }
}

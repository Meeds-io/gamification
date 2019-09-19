package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Path("/gamification/rules")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("administrators")
public class ManageRulesEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageRulesEndpoint.class);

    private final CacheControl cacheControl;

    protected RuleService ruleService = null;

    protected IdentityManager  identityManager  = null;

    public ManageRulesEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        ruleService = CommonsUtils.getService(RuleService.class);

        identityManager = CommonsUtils.getService(IdentityManager.class);

    }

    @GET
    @Path("/all")
    public Response getAllRules(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {
            try {
                List<RuleDTO> allRules = ruleService.getAllRules();

                return Response.ok().cacheControl(cacheControl).entity(allRules).build();

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

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/add")
    public Response addRule(@Context SecurityContext securityContext, @Context UriInfo uriInfo, RuleDTO ruleDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();

            try {
                // Compute rule's data
                ruleDTO.setId(null);
                ruleDTO.setCreatedBy(currentUserName);
                ruleDTO.setLastModifiedBy(currentUserName);
                ruleDTO.setLastModifiedDate(new Date());

                //--- Add rule
                ruleDTO = ruleService.addRule(ruleDTO);

                return Response.ok().cacheControl(cacheControl).entity(ruleDTO).build();

            } catch (Exception e) {

                LOG.error("Error adding new rule {} by {} ", ruleDTO.getTitle(), currentUserName, e);

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
    @Path("/update")
    public Response updateRule(@Context UriInfo uriInfo, @Context HttpServletRequest request, RuleDTO ruleDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();
            try {

                //TODO : Load locale
                Locale lc = request.getLocale();

                // Compute rule's data
                ruleDTO.setCreatedBy(currentUserName);
                ruleDTO.setLastModifiedBy(currentUserName);
                ruleDTO.setLastModifiedDate(new Date());

                //--- Add rule
                ruleDTO = ruleService.updateRule(ruleDTO);

                // Compute user id
                String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false).getId();
                LOG.info("service=gamification operation=edit-rule parameters=\"user_social_id:{},rule_id:{},rule_title:{},rule_description:{}\"", actorId, ruleDTO.getId(), ruleDTO.getTitle(), ruleDTO.getDescription());

                return Response.ok().cacheControl(cacheControl).entity(ruleDTO).build();

            } catch (Exception e) {

                LOG.error("Error updating rule {} by {} ", ruleDTO.getTitle(), currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error updating a rule")
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
    @Path("/delete")
    public Response deleteRule(@Context UriInfo uriInfo, @QueryParam("ruleTitle") String ruleTitle) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();

            try {
                //--- Remove the rule
                ruleService.deleteRule(ruleTitle);

                return Response.ok().cacheControl(cacheControl).entity("Rule " + ruleTitle + " has been removed successfully ").build();

            } catch (Exception e) {

                LOG.error("Error deleting rule {} by {} ", ruleTitle, currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error deleting a rule")
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

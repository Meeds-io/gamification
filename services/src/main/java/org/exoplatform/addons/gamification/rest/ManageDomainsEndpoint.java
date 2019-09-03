package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.commons.utils.CommonsUtils;
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

@Path("/gamification/domains")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("administrators")
public class ManageDomainsEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageDomainsEndpoint.class);

    private final CacheControl cacheControl;

    protected DomainService domainService = null;

    public ManageDomainsEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        domainService = CommonsUtils.getService(DomainService.class);

    }


    @GET
    @Path("/all")
    public Response getAllDomains(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {
            try {
                List<DomainDTO> allDomains = domainService.getAllDomains();

                return Response.ok().cacheControl(cacheControl).entity(allDomains).build();

            } catch (Exception e) {

                LOG.error("Error listing all domains ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all domains")
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
    public Response addDomain(@Context SecurityContext securityContext, @Context UriInfo uriInfo, DomainDTO domainDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();

            try {
                // Compute domain's data
                domainDTO.setId(null);
                domainDTO.setCreatedBy(currentUserName);
                domainDTO.setLastModifiedBy(currentUserName);
                domainDTO.setLastModifiedDate(String.valueOf(new Date()));


                //--- Add domain
                domainDTO = domainService.addDomain(domainDTO);

                return Response.ok().cacheControl(cacheControl).entity(domainDTO).build();

            } catch (Exception e) {

                LOG.error("Error adding new domain {} by {} ", domainDTO.getTitle(), currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error adding new domain")
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
    public Response updateDomain(@Context UriInfo uriInfo, @Context HttpServletRequest request, DomainDTO domainDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();
            try {
                domainDTO.setId(null);
                domainDTO.setCreatedBy(currentUserName);
                domainDTO.setLastModifiedBy(currentUserName);
                domainDTO.setLastModifiedDate(String.valueOf(new Date()));


                //--- Add domain
                domainDTO = domainService.updateDomain(domainDTO);


                return Response.ok().cacheControl(cacheControl).entity(domainDTO).build();

            } catch (Exception e) {

                LOG.error("Error updating domain {} by {} ", domainDTO.getTitle(), currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error updating a domain")
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
    public Response deleteDomain(@Context UriInfo uriInfo, @QueryParam("id") Long id) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();

            try {
                //--- Remove the domain
                domainService.deleteDomain(id);

                return Response.ok().cacheControl(cacheControl).entity("Domain " + id + " has been removed successfully ").build();

            } catch (Exception e) {

                LOG.error("Error deleting domain {} by {} ", id, currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error deleting a domain")
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

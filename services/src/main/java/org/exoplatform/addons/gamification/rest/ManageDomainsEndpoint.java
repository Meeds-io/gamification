package org.exoplatform.addons.gamification.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

@Path("/gamification")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("administrators")
public class ManageDomainsEndpoint implements ResourceContainer {

  private static final Log   LOG           = ExoLogger.getLogger(ManageDomainsEndpoint.class);

  private final CacheControl cacheControl;

  protected DomainService    domainService = null;

  protected IdentityManager identityManager                = null;

  public ManageDomainsEndpoint() {

    this.cacheControl = new CacheControl();

    cacheControl.setNoCache(true);

    cacheControl.setNoStore(true);

    domainService = CommonsUtils.getService(DomainService.class);

    identityManager = CommonsUtils.getService(IdentityManager.class);

  }

  @GET
  @Path("/domains")
  public Response getAllDomains(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {
      try {
        List<DomainDTO> allDomains = domainService.getAllDomains();

        return Response.ok().cacheControl(cacheControl).entity(allDomains).build();

      } catch (Exception e) {

        LOG.error("Error listing all domains ", e);

        return Response.serverError().cacheControl(cacheControl).entity("Error listing all domains").build();
      }

    } else {
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }

  @POST
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path("/domains")
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

        // --- Add domain
        domainDTO = domainService.addDomain(domainDTO);

        return Response.ok().cacheControl(cacheControl).entity(domainDTO).build();

      } catch (Exception e) {

        LOG.error("Error adding new domain {} by {} ", domainDTO.getTitle(), currentUserName, e);

        return Response.serverError().cacheControl(cacheControl).entity("Error adding new domain").build();

      }

    } else {

      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }

  @PUT
  @Path("/domains/{id}")
  public Response updateDomain(@Context UriInfo uriInfo,
                               @Context HttpServletRequest request,
                               @PathParam("id") Long id,
                               DomainDTO domainDTO) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      String currentUserName = conversationState.getIdentity().getUserId();
      try {
        domainDTO.setId(null);
        domainDTO.setCreatedBy(currentUserName);
        domainDTO.setLastModifiedBy(currentUserName);
        domainDTO.setLastModifiedDate(String.valueOf(new Date()));

        // --- Add domain
        domainDTO = domainService.updateDomain(domainDTO);

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false).getId();
        LOG.info("service=gamification operation=edit-domain parameters=\"user_social_id:{},domain_id:{},domain_title:{},domain_description:{}\"", actorId, domainDTO.getId(), domainDTO.getTitle(), domainDTO.getDescription());


        return Response.ok().cacheControl(cacheControl).entity(domainDTO).build();

      } catch (Exception e) {

        LOG.error("Error updating domain {} by {} ", domainDTO.getTitle(), currentUserName, e);

        return Response.serverError().cacheControl(cacheControl).entity("Error updating a domain").build();
      }

    } else {

      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }

  @DELETE
  @Path("/domains/{id}")
  public Response deleteRule(@Context UriInfo uriInfo, @PathParam("id") Long id) {

    ConversationState conversationState = ConversationState.getCurrent();

    if (conversationState != null) {

      String currentUserName = conversationState.getIdentity().getUserId();

      try {
        // --- Remove the rule
        domainService.deleteDomain(id);

        return Response.ok().cacheControl(cacheControl).entity("Domain " + id + " has been removed successfully ").build();

      } catch (Exception e) {

        LOG.error("Error deleting Domain {} by {} ", id, currentUserName, e);

        return Response.serverError().cacheControl(cacheControl).entity("Error deleting a Domain").build();

      }

    } else {

      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }

  }
}

/**
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.rest.model.DomainList;
import org.exoplatform.addons.gamification.rest.model.DomainRestEntity;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.rest.api.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/gamification/domains")
@Produces(MediaType.APPLICATION_JSON)
public class ManageDomainsEndpoint implements ResourceContainer {

  private static final Log   LOG                    = ExoLogger.getLogger(ManageDomainsEndpoint.class);

  private final CacheControl cacheControl;

  protected DomainService    domainService;

  protected IdentityManager  identityManager;

  // 7 days
  private static final int   CACHE_IN_SECONDS       = 604800;

  private static final int   CACHE_IN_MILLI_SECONDS = CACHE_IN_SECONDS * 1000;

  public ManageDomainsEndpoint(DomainService domainService, IdentityManager identityManager) {
    this.cacheControl = new CacheControl();
    this.domainService = domainService;
    this.identityManager = identityManager;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of available domains", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getAllDomains(@Parameter(description = "Offset of result", required = false)
                                @QueryParam("offset")
                                @DefaultValue("0")
                                int offset,
                                @Parameter(description = "Limit of result", required = false)
                                @QueryParam("limit")
                                @DefaultValue("0")
                                int limit,
                                @Parameter(description = "domains type filtering", required = false)
                                @QueryParam("type")
                                @DefaultValue("AUTOMATIC")
                                String type,
                                @Parameter(description = "domains status filtering", required = false)
                                @QueryParam("status")
                                @DefaultValue("ENABLED")
                                String status,
                                @Parameter(description = "Whether return size of results or not", required = false)
                                @QueryParam("returnSize")
                                @DefaultValue("false")
                                boolean returnSize) {
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    DomainFilter domainFilter = new DomainFilter();
    domainFilter.setEntityFilterType(EntityFilterType.valueOf(type));
    domainFilter.setEntityStatusType(EntityStatusType.valueOf(status));
    String currentUser = Utils.getCurrentUser();
    try {
      // To be changed after implementing new services for engagementCenter
      if (limit == 0) {
        List<DomainDTO> domains = domainService.getAllDomains(offset, limit, domainFilter);
        return Response.ok(domains).build();
      } else {
        DomainList domainList = new DomainList();
        List<DomainRestEntity> domains = getDomainsRestEntitiesByFilter(offset, limit, currentUser, domainFilter);
        if(returnSize) {
          int domainsSize = domainService.countDomains(domainFilter);
          domainList.setDomainsSize(domainsSize);
        }
        domainList.setDomains(domains);
        domainList.setDomainsOffset(offset);
        domainList.setDomainsLimit(limit);
        return Response.ok(domainList).build();
      }
    } catch (Exception e) {
      LOG.warn("Error listing all domains ", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Operation(summary = "Creates a domain", method = "POST")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response addDomain(@Parameter(description = "Domain object to create", required = true)
  DomainDTO domainDTO) {
    if (domainDTO == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("domain object is mandatory").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    String currentUser = identity.getUserId();
    boolean isAdministrator = Utils.isAdministrator(identity);
    try {
      domainDTO = domainService.addDomain(domainDTO, currentUser, isAdministrator);
      return Response.ok(EntityBuilder.toRestEntity(domainDTO, currentUser)).build();

    } catch (IllegalAccessException e) {
      LOG.warn("unauthorized user {} trying to update a domain with id {}", currentUser, domainDTO.getId(), e);
      return Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized user trying to update a domain").build();
    } catch (Exception e) {
      LOG.warn("Error adding new domain {} by {} ", domainDTO.getTitle(), currentUser, e);
      return Response.serverError().cacheControl(cacheControl).entity("Error adding new domain").build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Operation(summary = "updates created domain", method = "PUT")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updateDomain(@Parameter(description = "domain object to update", required = true)
  DomainDTO domainDTO) {
    if (domainDTO == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("domain object is mandatory").build();
    }
    if (domainDTO.getId() <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("domain technical identifier must be positive").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    String currentUser = identity.getUserId();
    boolean isAdministrator = Utils.isAdministrator(identity);
    try {
      domainDTO.setLastModifiedBy(currentUser);
      domainDTO = domainService.updateDomain(domainDTO, currentUser, isAdministrator);
      return Response.ok(EntityBuilder.toRestEntity(domainDTO, currentUser)).build();
    } catch (IllegalAccessException e) {
      LOG.warn("unauthorized user {} attempts to create a challenge", currentUser, domainDTO.getId(), e);
      return Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized user trying to update a domain").build();
    } catch (Exception e) {
      LOG.warn("Error creating domain {} by {} ", domainDTO.getTitle(), currentUser, e);
      return Response.serverError().cacheControl(cacheControl).entity("Error updating a domain").build();
    }
  }

  @DELETE
  @RolesAllowed("administrators")
  @Path("{id}")
  @Operation(summary = "Deletes an existing domain identified by its id", method = "DELETE")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response deleteDomain(@Parameter(description = "domain id to be deleted", required = true)
  @PathParam("id")
  long id) {
    if (id <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("domain technical identifier must be positive").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    String currentUser = identity.getUserId();
    boolean isAdministrator = Utils.isAdministrator(identity);
    try {
      domainService.deleteDomain(id, currentUser, isAdministrator);
      return Response.noContent().build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity("domain trying to delete not found").build();
    } catch (IllegalAccessException e) {
      LOG.warn("unauthorized user {} trying to delete a domain", currentUser, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized user trying to delete a domain").build();
    } catch (Exception e) {
      LOG.warn("Error when deleting domain", e);
      return Response.serverError().entity("Error when deleting domain").build();
    }
  }

  @GET
  @Path("canAddProgram")
  @Produces(MediaType.TEXT_PLAIN)
  @RolesAllowed("users")
  @Operation(summary = "check if the current user can add a program", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User ability to add a program is returned"),
      @ApiResponse(responseCode = "401", description = "User not authorized to add a program") })

  public Response canAddProgram() {
    try {
      org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
      boolean isAdministrator = Utils.isAdministrator(identity);
      return Response.ok(String.valueOf(isAdministrator)).build();
    } catch (Exception e) {
      LOG.error("Error when checking if the authenticated user can add a challenge", e);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("{id}/cover")
  @Operation(summary = "Gets a domain cover", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Resource not found") })
  public Response getDomainCoverById(@Context
  UriInfo uriInfo, @Context
  Request request,
                                      @Parameter(description = "The value of lastModified parameter will determine whether the query should be cached by browser or not. If not set, no 'expires HTTP Header will be sent'")
                                      @QueryParam("lastModified")
                                      String lastModified,
                                      @Parameter(description = "domain id", required = true)
                                      @PathParam("id")
                                      String id,
                                      @Parameter(description = "A mandatory valid token that is used to authorize anonymous request", required = true)
                                      @QueryParam("r")
                                      String token) throws IOException {

    boolean isDefault = StringUtils.equals(Utils.DEFAULT_IMAGE_REMOTE_ID, id);
    String lastUpdated = null;
    DomainDTO domain = null;
    if (!isDefault) {
      domain = domainService.getDomainById(Long.valueOf(id));
      if (domain == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      lastUpdated = domain.getLastModifiedDate();
      isDefault = domain.getCoverFileId() == null;
    }

    Response.ResponseBuilder builder = null;
    if (isDefault) {
      lastUpdated = Utils.toRFC3339Date(new Date(Utils.DEFAULT_COVER_LAST_MODIFIED));
      builder = Utils.getDefaultCover();
    } else {
      if (RestUtils.isAnonymous() && !Utils.isAttachmentTokenValid(token, id, Utils.TYPE, lastModified)) {
        LOG.warn("An anonymous user attempts to access avatar of space {} without a valid access token", id);
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      EntityTag eTag = new EntityTag(lastUpdated);
      builder = request.evaluatePreconditions(eTag);
      if (builder == null) {
        InputStream stream = domainService.getFileDetailAsStream(domain.getCoverFileId());
        builder = Response.ok(stream, "image/png");
        builder.tag(eTag);
      }
    }
    builder.cacheControl(cacheControl);
    builder.lastModified(Utils.parseRFC3339Date(lastUpdated));
    if (StringUtils.isNotBlank(lastModified)) {
      builder.expires(new Date(System.currentTimeMillis() + CACHE_IN_MILLI_SECONDS));
    }
    return builder.build();
  }

  private List<DomainRestEntity> getDomainsRestEntitiesByFilter(int offset, int limit, String currentUser, DomainFilter filter) {
    List<DomainDTO> domains = domainService.getAllDomains(offset, limit, filter);
    return EntityBuilder.toRestEntities(domains, currentUser);
  }

}

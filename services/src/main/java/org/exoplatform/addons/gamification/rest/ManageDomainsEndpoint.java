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

import static org.exoplatform.addons.gamification.utils.Utils.DEFAULT_COVER_LAST_MODIFIED;

import java.io.ByteArrayInputStream;
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
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.PortalContainer;
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

  private static final String       DEFAULT_COVER_PATH          =
                                                       System.getProperty("meeds.engagementCenter.program.defaultCoverPath",
                                                                          "/skin/images/program_default_cover_back.png");

  private static final String       DOMAIN_NOT_FOUND_MESSAGE    = "The domain doesn't exit";

  private static final Log          LOG                         = ExoLogger.getLogger(ManageDomainsEndpoint.class);

  // 7 days
  private static final int          CACHE_IN_SECONDS            = 604800;

  private static final int          CACHE_IN_MILLI_SECONDS      = CACHE_IN_SECONDS * 1000;

  private static final CacheControl CACHECONTROL                = new CacheControl();

  static {
    CACHECONTROL.setMaxAge(CACHE_IN_SECONDS);
    CACHECONTROL.setPrivate(false);
  }

  protected PortalContainer portalContainer;

  protected DomainService   domainService;

  protected IdentityManager identityManager;

  public byte[]             defaultProgramCover = null; // NOSONAR

  public ManageDomainsEndpoint(PortalContainer portalContainer, DomainService domainService, IdentityManager identityManager) {
    this.portalContainer = portalContainer;
    this.domainService = domainService;
    this.identityManager = identityManager;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of available domains", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getDomains(@Parameter(description = "Offset of results to retrieve", required = false)
                             @QueryParam("offset")
                             @DefaultValue("0")
                             int offset,
                             @Parameter(description = "Limit of results to retrieve", required = false)
                             @QueryParam("limit")
                             @DefaultValue("0")
                             int limit,
                             @Parameter(description = "Domains type filtering, possible values: AUTOMATIC, MANUAL and ALL. Default value = AUTOMATIC.", required = false)
                             @QueryParam("type")
                             @DefaultValue("AUTOMATIC")
                             String type,
                             @Parameter(description = "Domains status filtering, possible values: ENABLED, DISABLED, DELETED and ALL. Default value = ENABLED.", required = false)
                             @QueryParam("status")
                             @DefaultValue("ENABLED")
                             String status,
                             @Parameter(description = "If true, this will return the total count of filtered domains. Possible values = true or false. Default value = false.", required = false)
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
    EntityFilterType filterType = StringUtils.isBlank(type) ? EntityFilterType.AUTOMATIC : EntityFilterType.valueOf(type);
    domainFilter.setEntityFilterType(filterType);
    EntityStatusType statusType = StringUtils.isBlank(status) ? EntityStatusType.ENABLED : EntityStatusType.valueOf(status);
    domainFilter.setEntityStatusType(statusType);
    String currentUser = Utils.getCurrentUser();
    DomainList domainList = new DomainList();
    List<DomainRestEntity> domains = getDomainsRestEntitiesByFilter(domainFilter, offset, limit, currentUser);
    if (returnSize) {
      int domainsSize = domainService.countDomains(domainFilter);
      domainList.setDomainsSize(domainsSize);
    }
    domainList.setDomains(domains);
    domainList.setDomainsOffset(offset);
    domainList.setDomainsLimit(limit);
    return Response.ok(domainList).build();
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
  public Response createDomain(@Parameter(description = "Domain object to create", required = true)
                               DomainDTO domainDTO) {
    if (domainDTO == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Domain object is mandatory").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      domainDTO = domainService.createDomain(domainDTO, identity);
      return Response.ok(EntityBuilder.toRestEntity(domainDTO, identity.getUserId())).build();
    } catch (IllegalAccessException e) {
      LOG.warn("Unauthorized user {} attempts to create a domain", identity.getUserId(), domainDTO.getId(), e);
      return Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized user trying to update a domain").build();
    }
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "updates created domain", method = "PUT")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updateDomain(@Parameter(description = "domain id", required = true)
                               @PathParam("id")
                               long domainId,
                               @Parameter(description = "domain object to update", required = true)
                               DomainDTO domainDTO) {
    if (domainDTO == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("domain object is mandatory").build();
    }
    if (domainId <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("domain technical identifier must be positive").build();
    }
    domainDTO.setId(domainId);
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      domainDTO = domainService.updateDomain(domainDTO, identity);
      return Response.ok(EntityBuilder.toRestEntity(domainDTO, identity.getUserId())).build();
    } catch (IllegalAccessException e) {
      LOG.warn("Unauthorized user {} attempts to update the domain", identity.getUserId(), domainDTO.getId(), e);
      return Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized user trying to update a domain").build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(DOMAIN_NOT_FOUND_MESSAGE).build();
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
      return Response.status(Response.Status.BAD_REQUEST).entity("The parameter 'id' must be positive integer").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      domainService.deleteDomain(id, identity);
      return Response.noContent().build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(DOMAIN_NOT_FOUND_MESSAGE).build();
    } catch (IllegalAccessException e) {
      LOG.warn("unauthorized user {} trying to delete a domain", identity.getUserId(), e);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @GET
  @Path("canAddProgram")
  @Produces(MediaType.TEXT_PLAIN)
  @RolesAllowed("users")
  @Operation(summary = "check if the current user can add a program", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User ability to add a program is returned"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response canAddProgram() {
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    return Response.ok(String.valueOf(domainService.canAddDomain(identity))).build();
  }

  @GET
  @Path("{id}/cover")
  @Produces("image/png")
  @Operation(summary = "Gets a domain cover", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "403", description = "Forbidden request"),
      @ApiResponse(responseCode = "404", description = "Resource not found") })
  public Response getDomainCoverById(@Context
                                     Request request,
                                     @Parameter(description = "The value of lastModified parameter will determine whether the query should be cached by browser or not. If not set, no 'expires HTTP Header will be sent'")
                                     @QueryParam("lastModified")
                                     Long lastModified,
                                     @Parameter(description = "domain id", required = true)
                                     @PathParam("id")
                                     String domainId,
                                     @Parameter(description = "A mandatory valid token that is used to authorize anonymous request", required = true)
                                     @QueryParam("r")
                                     String token) throws IOException {
    boolean isDefault = StringUtils.equals(Utils.DEFAULT_IMAGE_REMOTE_ID, domainId);
    String lastUpdated = null;
    DomainDTO domain = null;
    if (isDefault) {
      lastUpdated = Utils.toRFC3339Date(new Date(DEFAULT_COVER_LAST_MODIFIED));
    } else {
      domain = domainService.getDomainById(Long.valueOf(domainId));
      if (domain == null) {
        return Response.status(Response.Status.NOT_FOUND).entity(DOMAIN_NOT_FOUND_MESSAGE).build();
      }
      isDefault = domain.getCoverFileId() == 0 ;
      lastUpdated = domain.getLastModifiedDate();
    }

    try {
      if (!isDefault && RestUtils.isAnonymous() && !Utils.isAttachmentTokenValid(token, domainId, Utils.TYPE, lastModified)) {
        LOG.warn("An anonymous user attempts to access avatar of space {} without a valid access token", domainId);
        return Response.status(Response.Status.FORBIDDEN).build();
      }
      EntityTag eTag = new EntityTag(lastUpdated);
      Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
      if (builder == null) {
        InputStream stream = isDefault ? getDefaultCoverInputStream()
                                       : domainService.getFileDetailAsStream(Long.valueOf(domainId));
        builder = Response.ok(stream, "image/png");
        builder.tag(eTag);
      }
      if (lastModified != null) {
        builder.cacheControl(CACHECONTROL);
        builder.lastModified(Utils.parseRFC3339Date(lastUpdated));
        builder.expires(new Date(System.currentTimeMillis() + CACHE_IN_MILLI_SECONDS));
      }
      return builder.build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(DOMAIN_NOT_FOUND_MESSAGE).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{domainId}")
  @RolesAllowed("users")
  @Operation(summary = "Retrieves a domain by its id", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getDomainById(@Parameter(description = "domain id", required = true)
                                @PathParam("domainId")
                                long domainId) {
    if (domainId == 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("DomainId must be not null").build();
    }
    String currentUser = Utils.getCurrentUser();
    DomainDTO domain = domainService.getDomainById(Long.valueOf(domainId));
    if (domain == null) {
      return Response.status(Response.Status.NOT_FOUND).entity(DOMAIN_NOT_FOUND_MESSAGE).build();
    }
    return Response.ok(EntityBuilder.toRestEntity(domain, currentUser)).build();
  }

  private List<DomainRestEntity> getDomainsRestEntitiesByFilter(DomainFilter filter, int offset, int limit, String currentUser) {
    List<DomainDTO> domains = domainService.getAllDomains(filter, offset, limit);
    return EntityBuilder.toRestEntities(domains, currentUser);
  }

  public InputStream getDefaultCoverInputStream() throws IOException {
    if (defaultProgramCover == null) {
      InputStream is = portalContainer.getPortalContext().getResourceAsStream(DEFAULT_COVER_PATH);
      if (is == null) {
        defaultProgramCover = new byte[] {};
      } else {
        defaultProgramCover = IOUtil.getStreamContentAsBytes(is);
      }
    }
    return new ByteArrayInputStream(defaultProgramCover);
  }

}

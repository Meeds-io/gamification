/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
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
package io.meeds.gamification.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.service.ConnectorService;

import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.rest.api.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/gamification/user/connectors")
public class ConnectorRest implements ResourceContainer {

  private static final Log       LOG = ExoLogger.getLogger(ConnectorRest.class);

  private final ConnectorService connectorService;

  public ConnectorRest(ConnectorService connectorService) {
    this.connectorService = connectorService;
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of enabled connectors", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getUserRemoteConnectors() {
    String currentUsername = RestUtils.getCurrentUserIdentity().getRemoteId();
    try {
      List<RemoteConnector> connectorList = connectorService.getUserRemoteConnectors(currentUsername);
      return Response.ok(connectorList).build();
    } catch (Exception e) {
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed("users")
  @Path("{connectorName}/connect")
  @Operation(summary = "Validate Remote user identifier on a selected connector and associate it in his current profile.", description = "Validate Remote user identifier on a selected connector and associate it in his current profile.", method = "POST")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response connect(@Parameter(description = "Access Token", required = true) @PathParam("connectorName") String connectorName,
                          @Parameter(description = "Access Token", required = true) @FormParam("accessToken") String accessToken) {
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      connectorService.connect(connectorName, accessToken, identity);
      return Response.noContent().build();
    } catch (ObjectAlreadyExistsException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
    } catch (IOException | ExecutionException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("{connectorName}")
  @RolesAllowed("users")
  @Operation(summary = "Deletes an existing GitHub account", description = "Deletes an existing GitHub account", method = "DELETE")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Object not found"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response disconnect(@Parameter(description = "Github account username", required = true) @PathParam("connectorName") String connectorName) {
    if (StringUtils.isBlank(connectorName)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("connector name is mandatory").build();
    }
    String currentUser = ConversationState.getCurrent().getIdentity().getUserId();
    try {
      connectorService.disconnect(connectorName, currentUser);
      return Response.noContent().build();
    } catch (ObjectNotFoundException e) {
      LOG.debug("User '{}' attempts to delete a not existing connector account", currentUser);
      return Response.status(Response.Status.NOT_FOUND).entity("Connector account not found").build();
    }
  }
}

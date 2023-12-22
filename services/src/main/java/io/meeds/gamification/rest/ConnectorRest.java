/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.rest;

import static io.meeds.gamification.utils.Utils.getExpandOptions;

import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

import io.meeds.gamification.model.RemoteConnector;
import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.rest.builder.ConnectorBuilder;
import io.meeds.gamification.rest.model.ConnectorRestEntity;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/gamification/connectors")
public class ConnectorRest implements ResourceContainer {

  private static final Log              LOG = ExoLogger.getLogger(ConnectorRest.class);

  private final ConnectorService        connectorService;

  private final ConnectorSettingService connectorSettingService;

  public ConnectorRest(ConnectorService connectorService, ConnectorSettingService connectorSettingService) {
    this.connectorService = connectorService;
    this.connectorSettingService = connectorSettingService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of remote connectors", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
  })
  public Response getConnectors(
                                @Parameter(description = "Username", required = true)
                                @QueryParam("username")
                                String username,
                                @Parameter(description = "Used to retrieve extra information about connectors")
                                @QueryParam("expand")
                                String expand) {

    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    List<String> expandFields = getExpandOptions(expand);
    if (expandFields.contains("secretKey") && !connectorSettingService.canManageConnectorSettings(identity)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    List<ConnectorRestEntity> connectorRestEntities = getConnectorsRestEntities(expandFields, username);
    return Response.ok(connectorRestEntities).build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @RolesAllowed("users")
  @Path("oauthCallback/{connectorName}")
  @Operation(summary = "Validate Remote user identifier on a selected connector and associate it in his current profile.", description = "Validate Remote user identifier on a selected connector and associate it in his current profile.", method = "POST")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public Response connect(
                          @Parameter(description = "Connector name", required = true)
                          @PathParam("connectorName")
                          String connectorName,
                          @Parameter(description = "Access Token", required = true)
                          @QueryParam("code")
                          String code) {
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    connectorService.connect(connectorName, null, code, identity);
    return Response.ok("<html><body><script type=text/javascript>window.close();</script></body></html>").build();
  }

  @DELETE
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("disconnect/{connectorName}")
  @RolesAllowed("users")
  @Operation(summary = "Deletes an existing connector account", description = "Deletes an existing connector account", method = "DELETE")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Object not found"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response disconnect(
                             @Parameter(description = "Connector name", required = true)
                             @PathParam("connectorName")
                             String connectorName,
                             @Parameter(description = "User Remote identifier", required = true)
                             @FormParam("remoteId")
                             String remoteId) {
    if (StringUtils.isBlank(connectorName)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("connector name is mandatory").build();
    }
    connectorService.disconnect(connectorName, remoteId);
    return Response.noContent().build();
  }

  @POST
  @Path("settings")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed("users")
  @Operation(summary = "Saves gamification connector settings", description = "Saves gamification connector settings", method = "POST")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response saveConnectorSettings(
                                        @Parameter(description = "Remote connector name", required = true)
                                        @FormParam("connectorName")
                                        String connectorName,
                                        @Parameter(description = "Remote connector Api key", required = true)
                                        @FormParam("apiKey")
                                        String apiKey,
                                        @Parameter(description = "Remote connector secret key", required = true)
                                        @FormParam("secretKey")
                                        String secretKey,
                                        @Parameter(description = "Remote connector redirect Url", required = true)
                                        @FormParam("redirectUrl")
                                        String redirectUrl,
                                        @Parameter(description = "Remote connector status", required = true)
                                        @FormParam("enabled")
                                        @DefaultValue("true")
                                        boolean enabled) {
    if (org.apache.commons.lang3.StringUtils.isBlank(connectorName)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("'connectorName' parameter is mandatory").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      RemoteConnectorSettings remoteConnectorSettings = new RemoteConnectorSettings();
      remoteConnectorSettings.setName(connectorName);
      remoteConnectorSettings.setApiKey(apiKey);
      remoteConnectorSettings.setSecretKey(secretKey);
      remoteConnectorSettings.setRedirectUrl(redirectUrl);
      remoteConnectorSettings.setEnabled(enabled);
      connectorSettingService.saveConnectorSettings(remoteConnectorSettings, identity);
      return Response.noContent().build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    } catch (Exception e) {
      LOG.warn("Error saving connector '{}' settings", connectorName, e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("settings/{connectorName}")
  @RolesAllowed("users")
  @Operation(summary = "Deletes gamification connector settings", description = "Deletes gamification connector settings", method = "DELETE")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response deleteConnectorSettings(
                                          @Parameter(description = "Remote connector name", required = true)
                                          @PathParam("connectorName")
                                          String connectorName) {
    if (org.apache.commons.lang3.StringUtils.isBlank(connectorName)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("'connectorName' parameter is mandatory").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      connectorSettingService.deleteConnectorSettings(connectorName, identity);
      return Response.noContent().build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    } catch (Exception e) {
      LOG.warn("Error deleting '{}' connector settings", connectorName, e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  private List<ConnectorRestEntity> getConnectorsRestEntities(List<String> expandFields, String username) {
    Collection<RemoteConnector> connectorList = connectorService.getConnectors(username);
    return ConnectorBuilder.toRestEntities(connectorService, connectorSettingService, connectorList, expandFields, username);
  }

}

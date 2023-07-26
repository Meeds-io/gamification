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

import java.io.InputStream;
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
import javax.ws.rs.core.*;

import io.meeds.gamification.model.ConnectorHook;
import io.meeds.gamification.rest.model.ConnectorHookRestEntity;
import io.meeds.gamification.service.ConnectorHookService;
import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
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
import org.exoplatform.social.core.manager.IdentityManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static io.meeds.gamification.utils.Utils.*;

@Path("/gamification/connectors")
public class ConnectorRest implements ResourceContainer {

  private static final Log              LOG                                   = ExoLogger.getLogger(ConnectorRest.class);

  public static final String            CONNECTOR_NAME_PARAMETER_IS_MANDATORY = "'connectorName' parameter is mandatory";

  private final ConnectorService        connectorService;

  private final ConnectorHookService    connectorHookService;

  private final ConnectorSettingService connectorSettingService;

  private final IdentityManager         identityManager;

  public ConnectorRest(ConnectorService connectorService,
                       ConnectorSettingService connectorSettingService,
                       ConnectorHookService connectorHookService,
                       IdentityManager identityManager) {
    this.connectorService = connectorService;
    this.connectorHookService = connectorHookService;
    this.connectorSettingService = connectorSettingService;
    this.identityManager = identityManager;
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
    if ((expandFields.contains("secretKey") || expandFields.contains("accessToken")) && !connectorSettingService.canManageConnectorSettings(identity)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    List<ConnectorRestEntity> connectorRestEntities = getConnectorsRestEntities(expandFields, username);
    return Response.ok(connectorRestEntities).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_PLAIN)
  @RolesAllowed("users")
  @Path("connect/{connectorName}")
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
                          @Parameter(description = "User Remote identifier")
                          @FormParam("remoteId")
                          String remoteId,
                          @Parameter(description = "Access Token", required = true)
                          @FormParam("accessToken")
                          String accessToken) {
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      String identifier = connectorService.connect(connectorName, remoteId, accessToken, identity);
      return Response.ok(identifier).build();
    } catch (ObjectAlreadyExistsException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
    }
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
      return Response.status(Response.Status.BAD_REQUEST).entity(CONNECTOR_NAME_PARAMETER_IS_MANDATORY).build();
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
      return Response.status(Response.Status.BAD_REQUEST).entity(CONNECTOR_NAME_PARAMETER_IS_MANDATORY).build();
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

  @POST
  @Path("accessToken")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed("users")
  @Operation(summary = "Saves gamification connector access token used for manage hooks", description = "Saves gamification connector access token used for manage hooks", method = "POST")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response saveConnectorAccessToken(@Parameter(description = "Remote connector name", required = true) @FormParam("connectorName") String connectorName,
                                           @Parameter(description = "Remote connector access token", required = true) @FormParam("accessToken") String accessToken) {
    if (org.apache.commons.lang3.StringUtils.isBlank(connectorName)) {
      return Response.status(Response.Status.BAD_REQUEST).entity(CONNECTOR_NAME_PARAMETER_IS_MANDATORY).build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      connectorSettingService.saveConnectorAccessToken(connectorName, accessToken, identity);
      return Response.noContent().build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    } catch (Exception e) {
      LOG.warn("Error saving connector '{}' settings", connectorName, e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("hooks/{connectorName}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of remote connectors hooks", method = "GET")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"), })
  public Response getConnectorHooks(@Parameter(description = "Connector name", required = true) @PathParam("connectorName") String connectorName) {
    try {
      List<ConnectorHookRestEntity> connectorHookRestEntities = getConnectorHooksRestEntities(connectorName, getCurrentUser());
      return Response.ok(connectorHookRestEntities).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @GET
  @Path("hooks/{connectorName}/{hookName}/avatar")
  @Produces("image/png")
  @Operation(summary = "Gets a connector hook avatar", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "403", description = "Forbidden request"),
      @ApiResponse(responseCode = "404", description = "Resource not found") })
  public Response getConnectorHookAvatar(@Context Request request,
                                         @Parameter(description = "Remote connector name", required = true) @PathParam("connectorName") String connectorName,
                                         @Parameter(description = "Remote connector hook name", required = true) @PathParam("hookName") String hookName) {
    ConnectorHook connectorHook;
    String lastUpdated;
    connectorHook = connectorHookService.getConnectorHook(connectorName, hookName);
    if (connectorHook == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("The connector hook doesn't exit").build();
    }
    lastUpdated = connectorHook.getUpdatedDate();
    try {
      EntityTag eTag = new EntityTag(lastUpdated);
      Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
      if (builder == null) {
        InputStream stream = connectorHookService.getHookImageStream(connectorName, hookName, getCurrentUser());
        builder = Response.ok(stream, "image/png");
        builder.tag(eTag);
      }
      return builder.build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity("The connector hook doesn't exit").build();
    }
  }

  private List<ConnectorRestEntity> getConnectorsRestEntities(List<String> expandFields, String username) {
    Collection<RemoteConnector> connectorList = connectorService.getConnectors(username);
    return ConnectorBuilder.toRestEntities(connectorService, connectorSettingService, connectorList, expandFields, username);
  }

  private List<ConnectorHookRestEntity> getConnectorHooksRestEntities(String connectorName,
                                                                      String currentUser) throws IllegalAccessException {
    Collection<ConnectorHook> connectorHookList = connectorHookService.getConnectorHooks(connectorName, currentUser, 0, 20);
    return ConnectorBuilder.toRestEntities(identityManager, connectorHookList);
  }
}

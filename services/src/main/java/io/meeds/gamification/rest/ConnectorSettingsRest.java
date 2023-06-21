package io.meeds.gamification.rest;

import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/gamification/connectors/settings")
public class ConnectorSettingsRest implements ResourceContainer {

  private static final Log              LOG = ExoLogger.getLogger(ConnectorSettingsRest.class);

  private final ConnectorSettingService connectorSettingService;

  private final ConnectorService        connectorService;

  public ConnectorSettingsRest(ConnectorSettingService connectorSettingService, ConnectorService connectorService) {
    this.connectorSettingService = connectorSettingService;
    this.connectorService = connectorService;
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  @RolesAllowed("administrators")
  @Operation(summary = "Retrieves the list of enabled connectors", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getConnectorsSettings() {
    try {
      List<RemoteConnectorSettings> remoteConnectorSettings = connectorSettingService.getConnectorsSettings(connectorService);
      return Response.ok(remoteConnectorSettings).build();
    } catch (Exception e) {
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed("administrators")
  @Operation(summary = "Saves gamification connector settings", description = "Saves gamification connector settings", method = "POST")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response saveConnectorSettings(@Parameter(description = "Remote connector name", required = true) @FormParam("connectorName") String connectorName,
                                        @Parameter(description = "Remote connector Api key", required = true) @FormParam("apiKey") String apiKey,
                                        @Parameter(description = "Remote connector secret key", required = true) @FormParam("secretKey") String secretKey,
                                        @Parameter(description = "Remote connector redirect Url", required = true) @FormParam("redirectUrl") String redirectUrl,
                                        @Parameter(description = "Remote connector status", required = true) @FormParam("enabled") @DefaultValue("true") boolean enabled) {
    if (StringUtils.isBlank(connectorName)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("'connectorName' parameter is mandatory").build();
    }
    try {
      RemoteConnectorSettings remoteConnectorSettings = new RemoteConnectorSettings();
      remoteConnectorSettings.setName(connectorName);
      remoteConnectorSettings.setApiKey(apiKey);
      remoteConnectorSettings.setSecretKey(secretKey);
      remoteConnectorSettings.setRedirectUrl(redirectUrl);
      remoteConnectorSettings.setEnabled(enabled);
      connectorSettingService.saveConnectorSettings(remoteConnectorSettings);
      return Response.noContent().build();
    } catch (Exception e) {
      LOG.warn("Error saving connector '{}' settings", connectorName, e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("{connectorName}")
  @RolesAllowed("administrators")
  @Operation(summary = "Deletes gamification connector settings", description = "Deletes gamification connector settings", method = "DELETE")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response deleteUserConnectorSettings(@Parameter(description = "Remote connector name", required = true) @PathParam("connectorName") String connectorName) {
    if (StringUtils.isBlank(connectorName)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("'connectorName' parameter is mandatory").build();
    }
    try {
      connectorSettingService.deleteConnectorSettings(connectorName);
      return Response.noContent().build();
    } catch (Exception e) {
      LOG.warn("Error deleting '{}' connector settings", connectorName, e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }
}

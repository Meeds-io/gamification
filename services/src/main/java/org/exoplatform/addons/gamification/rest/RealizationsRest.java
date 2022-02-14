package org.exoplatform.addons.gamification.rest;

import io.swagger.annotations.*;
import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.mapper.GamificationActionsHistoryMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/gamification/realizations/api")
@Api(value = "/gamification/realizations/api", description = "Manages users realizations") // NOSONAR
@RolesAllowed("administrators")
public class RealizationsRest implements ResourceContainer {

  private static final Log    LOG = ExoLogger.getLogger(RealizationsRest.class);

  private RealizationsService realizationsService;

  public RealizationsRest(RealizationsService realizationsService) {
    this.realizationsService = realizationsService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Path("allRealizations")
  @ApiOperation(value = "Retrieves the list of challenges available for an owner", httpMethod = "GET", response = Response.class, produces = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.OK, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response getAllRealizations(@ApiParam(value = "Offset of result", required = true)
                                                @QueryParam("fromDate")
                                                String fromDate,
                                                @ApiParam(value = "Offset of result", required = true)
                                                @QueryParam("toDate")
                                                String toDate,
                                                @ApiParam(value = "Offset of result", required = false)
                                                @DefaultValue("0")
                                                @QueryParam("offset")
                                                int offset,
                                                @ApiParam(value = "Limit of result", required = false)
                                                @DefaultValue("10")
                                                @QueryParam("limit")
                                                int limit) {
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }

    try {
      List<GamificationActionsHistoryDTO> gActionsHistoryList =realizationsService.getAllRealizationsByDate(fromDate,
                                                                                                                         toDate,
                                                                                                                         offset,
                                                                                                                         limit);
      return Response.ok(GamificationActionsHistoryMapper.toRestEntities(gActionsHistoryList)).build();
    } catch (Exception e) {
      LOG.warn("Error retrieving list of Realizations", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Path("updateRealizations")
  @ApiOperation(value = "Updates an existing realization", httpMethod = "PUT", response = Response.class, consumes = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.NOT_FOUND, message = "Object not found"),
      @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response updateRealizations(@ApiParam(value = "id of realization", required = true)
  @QueryParam("realizationId")
  String realizationId,
                                     @ApiParam(value = "new status of realization", required = true)
                                     @QueryParam("status")
                                     String status) {

    String currentUser = Utils.getCurrentUser();
    try {
      GamificationActionsHistoryDTO gamificationActionsHistoryDTO =
                                                                  realizationsService.updateRealizationStatus(Long.valueOf(realizationId),
                                                                                                              HistoryStatus.valueOf(status));
      return Response.ok(gamificationActionsHistoryDTO).build();
    } catch (ObjectNotFoundException e) {
      LOG.debug("User '{}' attempts to update a not existing realization '{}'", currentUser, e);
      return Response.status(Response.Status.NOT_FOUND).entity("realization not found").build();
    } catch (Exception e) {
      LOG.warn("Error updating a challenge", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

}

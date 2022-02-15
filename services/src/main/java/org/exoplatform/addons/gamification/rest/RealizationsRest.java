package org.exoplatform.addons.gamification.rest;

import io.swagger.annotations.*;
import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/gamification/realizations/api")
@Api(value = "/gamification/realizations/api", description = "Manages users realizations") // NOSONAR
@RolesAllowed("administrators")
public class RealizationsRest implements ResourceContainer {

  private static final Log    LOG = ExoLogger.getLogger(RealizationsRest.class);

  private RealizationsService realizationsService;

  // Delimiters that must be in the CSV file
  private static final String DELIMITER = ",";

  private static final String SEPARATOR = "\n";

  private SimpleDateFormat    formater  = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");

  // File header
  private static final String HEADER    =
                                     "Date,Creator,Action ID,Action label,Action type,Program,Program label,Points,Status,Grantee,Spaces";

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
  @RolesAllowed("users")
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

  /**
   * {@inheritDoc}
   */
  @GET
  @RolesAllowed("administrators")
  @Produces("application/vnd.ms-excel")
  @Path("getExport")
  @ApiOperation(value = "Gets CSV report", httpMethod = "GET", response = Response.class, notes = "Given a a csv file of actions")
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Request fulfilled"),
      @ApiResponse(code = 500, message = "Internal server error"), @ApiResponse(code = 400, message = "Invalid query input") })
  public Response getReport(@ApiParam(value = "Offset of result", required = true)
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
      List<GamificationActionsHistoryDTO> gActionsHistoryList = realizationsService.getAllRealizationsByDate(fromDate,
                                                                                                             toDate,
                                                                                                             offset,
                                                                                                             limit);
      List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities =
                                                                                        GamificationActionsHistoryMapper.toRestEntities(gActionsHistoryList);

      String csvString = computeXLSX(gamificationActionsHistoryRestEntities);
      String filename = "report_Actions";
      filename += formater.format(new Date());
      File temp = null;
      temp = File.createTempFile(filename, ".xlsx");
      temp.deleteOnExit();
      BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
      bw.write(csvString);
      bw.close();
      Response.ResponseBuilder response = Response.ok((Object) temp);
      response.header("Content-Disposition", "attachment; filename=" + filename + ".xlsx");
      return response.build();
    } catch (Exception e) {
      LOG.error("Error when creating temp file");
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  private String computeXLSX(List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) {
    StringBuilder sbResult = new StringBuilder();
    // Add header
    sbResult.append(HEADER);
    // Add a new line after the header
    sbResult.append(SEPARATOR);

    gamificationActionsHistoryRestEntities.forEach(ga -> {
      sbResult.append(ga.getCreatedDate());
      sbResult.append(DELIMITER);
      sbResult.append(ga.getCreator() != null ? ga.getCreator().getRemoteId() : ga.getEarner().getRemoteId());
      sbResult.append(DELIMITER);
      sbResult.append(ga.getAction() != null ? ga.getAction().getEvent() : "-");
      sbResult.append(DELIMITER);
      sbResult.append(ga.getAction() != null ? ga.getAction().getTitle() :"-");
      sbResult.append(DELIMITER);
      sbResult.append(ga.getAction() != null ? ga.getAction().getType().name() : "-");
      sbResult.append(DELIMITER);
      sbResult.append(ga.getAction() != null ? ga.getAction().getDomainDTO().getTitle() : "-");
      sbResult.append(DELIMITER);
      sbResult.append(ga.getAction() != null ? ga.getAction().getDomainDTO().getDescription(): "-");
      sbResult.append(DELIMITER);
      sbResult.append(ga.getScore());
      sbResult.append(DELIMITER);
      sbResult.append(ga.getStatus());
      sbResult.append(DELIMITER);
      sbResult.append(ga.getEarner() != null ? ga.getEarner().getRemoteId() : "-");
      sbResult.append(DELIMITER);
      sbResult.append(ga.getSpace() != null ? ga.getSpace() : "-");
      sbResult.append(SEPARATOR);
    });
    return sbResult.toString();
  }
}

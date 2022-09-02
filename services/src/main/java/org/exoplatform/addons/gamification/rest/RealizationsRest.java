package org.exoplatform.addons.gamification.rest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.mapper.GamificationActionsHistoryMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.util.Date;
import java.util.List;


@Path("/gamification/realizations/api")
@Tag(name = "/gamification/realizations/api", description = "Manages users realizations")
@RolesAllowed("administrators")
public class RealizationsRest implements ResourceContainer {

  private static final Log    LOG = ExoLogger.getLogger(RealizationsRest.class);

  private RealizationsService realizationsService;

  public RealizationsRest(RealizationsService realizationsService) {
    this.realizationsService = realizationsService;
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON, "application/vnd.ms-excel"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("allRealizations")
  @Operation(
          summary = "Retrieves the list of achievements switch a filter. The returned format can be of type JSON or XLS", 
          method = "GET", 
          description = "Retrieves the list of challenges available for an owner")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  @RolesAllowed("users")
  public Response getAllRealizations(@Parameter(description = "result fromDate", required = true)
                                                @QueryParam("fromDate")
                                                String fromDate,
                                                @Parameter(description = "result toDate", required = true)
                                                @QueryParam("toDate")
                                                String toDate,
                                                @Parameter(description = "Sort field. Possible values: date or actionType.")
                                                @QueryParam("sortBy")
                                                @DefaultValue("date")
                                                String sortField,
                                                @Parameter(description = "Whether to retrieve results sorted descending or not")
                                                @QueryParam("sortDescending")
                                                @DefaultValue("true")
                                                boolean sortDescending,
                                                @Parameter(description = "Identifier of the user that will be used to filter achievements", required = false)
                                                @QueryParam("earnerId")
                                                long earnerId,
                                                @Parameter(description = "Offset of result")
                                                @DefaultValue("0")
                                                @QueryParam("offset")
                                                int offset,
                                                @Parameter(description = "Limit of result")
                                                @DefaultValue("10")
                                                @QueryParam("limit")
                                                int limit,
                                                @Parameter(description = "Response Type")
                                                @DefaultValue("")
                                                @QueryParam("returnType")
                                                String returnType) {
    if (StringUtils.isBlank(fromDate) || StringUtils.isBlank(toDate)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Dates must not be blank").build();
    }
    RealizationsFilter filter = new RealizationsFilter();
    String currentUser = Utils.getCurrentUser();
    Identity identity = ConversationState.getCurrent().getIdentity();
    filter.setEarnerId(earnerId);
    Date dateFrom = Utils.parseRFC3339Date(fromDate);
    Date dateTo = Utils.parseRFC3339Date(toDate);
    filter.setFromDate(dateFrom);
    filter.setToDate(dateTo);
    filter.setSortDescending(sortDescending);
    filter.setSortField(sortField);

    boolean isXls = StringUtils.isNotBlank(returnType) && returnType.equals("xls");
    if (StringUtils.isNotBlank(returnType) && !returnType.equals("json") && !isXls) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Unsupported returnType, possible values: xls or json").build();
    }
    if (!isXls && limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    try {
      List<GamificationActionsHistoryDTO> gActionsHistoryList = realizationsService.getRealizationsByFilter(filter,
                                                                                                            identity,
                                                                                                            isXls ? 0 : offset,
                                                                                                            isXls ? -1 : limit);
      List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities = GamificationActionsHistoryMapper.toRestEntities(gActionsHistoryList);

      if (isXls) {
        String filename = "report_Actions";
        InputStream xlsInputStream = realizationsService.exportXls(filename, gamificationActionsHistoryRestEntities);
        return Response.ok(xlsInputStream).header("Content-Disposition", "attachment; filename=" + filename + ".xlsx").build();
      } else {
        return Response.ok(gamificationActionsHistoryRestEntities).build();
      }
    } catch (IllegalAccessException e) {
      LOG.debug("User '{}' isn't authorized to access achievements with parameter : earnerId = {}", currentUser, earnerId, e);
      return Response.status(Response.Status.FORBIDDEN).build();
    } catch (Exception e) {
      LOG.warn("Error retrieving list of Realizations or exporting xls file", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Path("updateRealizations")
  @Operation(summary = "Updates an existing realization", method = "PUT", description = "Updates an existing realization")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updateRealizations(@Parameter(description = "id of realization", required = true)
  @QueryParam("realizationId")
  String realizationId,
                                     @Parameter(description = "new status of realization", required = true)
                                     @QueryParam("status")
                                     String status,
                                     @Parameter(description = "new action Label of realization")
                                     @QueryParam("actionLabel")
                                     String actionLabel,
                                     @Parameter(description = "new points of realization")
                                     @QueryParam("points")
                                     Long points,
                                     @Parameter(description = "new domain of realization")
                                     @QueryParam("domain")
                                     String domain) {

    String currentUser = Utils.getCurrentUser();
    try {
      GamificationActionsHistoryDTO gamificationActionsHistoryDTO =
          realizationsService.updateRealizationStatus(Long.valueOf(realizationId),
                                                      HistoryStatus.valueOf(status),
                                                      actionLabel,
                                                      points,
                                                      domain);
      return Response.ok(GamificationActionsHistoryMapper.toRestEntity(gamificationActionsHistoryDTO)).build();
    } catch (ObjectNotFoundException e) {
      LOG.debug("User '{}' attempts to update a not existing realization '{}'", currentUser, e);
      return Response.status(Response.Status.NOT_FOUND).entity("realization not found").build();
    } catch (Exception e) {
      LOG.warn("Error updating a challenge", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }
}

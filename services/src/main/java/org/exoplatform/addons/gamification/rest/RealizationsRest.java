package org.exoplatform.addons.gamification.rest;

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
import java.util.Locale;

import static org.exoplatform.addons.gamification.utils.Utils.*;

@Path("/gamification/realizations/api")
@Tag(name = "/gamification/realizations/api", description = "Manages users realizations")
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
                                     "Date,Grantee,Action label,Action type,Program label,Points,Status,Spaces";

  public RealizationsRest(RealizationsService realizationsService) {
    this.realizationsService = realizationsService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Path("allRealizations")
  @Operation(
          summary = "Retrieves the list of challenges available for an owner",
          method = "GET",
          description = "Retrieves the list of challenges available for an owner")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
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
                                                @Parameter(description = "Offset of result")
                                                @DefaultValue("0")
                                                @QueryParam("offset")
                                                int offset,
                                                @Parameter(description = "Limit of result")
                                                @DefaultValue("10")
                                                @QueryParam("limit")
                                                int limit) {
    if (StringUtils.isBlank(fromDate) || StringUtils.isBlank(toDate)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Dates must not be blank").build();
    }
    RealizationsFilter filter = new RealizationsFilter();
    Date dateFrom = Utils.parseRFC3339Date(fromDate);
    Date dateTo = Utils.parseRFC3339Date(toDate);
    filter.setFromDate(dateFrom);
    filter.setToDate(dateTo);
    filter.setSortDescending(sortDescending);
    filter.setSortField(sortField);

    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }

    try {
      List<GamificationActionsHistoryDTO> gActionsHistoryList =
          realizationsService.getAllRealizationsByFilter(filter, offset, limit);
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

  /**
   * {@inheritDoc}
   */
  @GET
  @RolesAllowed("administrators")
  @Produces("application/vnd.ms-excel")
  @Path("getExport")
  @Operation(summary = "Gets CSV report", method = "GET", description = "returns a csv file of actions")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
      @ApiResponse(responseCode = "400", description = "Invalid query input") })
  public Response getReport(@Parameter(description = "result fromDate", required = true)
                            @QueryParam("fromDate")
                            String fromDate,
                            @Parameter(description = "result toDate", required = true)
                            @QueryParam("toDate")
                            String toDate) {
    RealizationsFilter filter = new RealizationsFilter();
    Date dateFrom = Utils.parseRFC3339Date(fromDate);
    Date dateTo = Utils.parseRFC3339Date(toDate);
    filter.setFromDate(dateFrom);
    filter.setToDate(dateTo);

    try {
      List<GamificationActionsHistoryDTO> gActionsHistoryList = realizationsService.getAllRealizationsByFilter(filter, 0, 0);
      List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities =
          GamificationActionsHistoryMapper.toRestEntities(gActionsHistoryList);
      String xlsxString = computeXLSX(gamificationActionsHistoryRestEntities);
      String filename = "report_Actions";
      filename += formater.format(new Date());
      File temp;
      temp = File.createTempFile(filename, ".xlsx"); // NOSONAR
      temp.deleteOnExit();
      BufferedWriter bw = new BufferedWriter(new FileWriter(temp)); // NOSONAR
      bw.write(xlsxString);
      bw.close();
      Response.ResponseBuilder response = Response.ok(temp); // NOSONAR
      response.header("Content-Disposition", "attachment; filename=" + filename + ".xlsx");
      return response.build();
    } catch (Exception e) {
      LOG.error("Error when creating temp file", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  private String computeXLSX(List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) {
    Locale locale = getCurrentUserLocale();
    StringBuilder sbResult = new StringBuilder();
    // Add header
    sbResult.append(HEADER);
    // Add a new line after the header
    sbResult.append(SEPARATOR);

    gamificationActionsHistoryRestEntities.forEach(ga -> {
      try {
        String actionLabelKey = "exoplatform.gamification.gamificationinformation.rule.description.";
        String domainTitleKey = "exoplatform.gamification.gamificationinformation.domain.";
        String actionLabel = "-";
        actionLabel = getI18NMessage(locale, actionLabelKey + ga.getActionLabel());
        if (actionLabel == null && ga.getAction() != null) {
          actionLabel = escapeIllegalCharacterInMessage(ga.getAction().getTitle());
        } else {
          actionLabel = escapeIllegalCharacterInMessage(actionLabel);
        }
        String domainDescription = "-";
        if (ga.getDomain() != null) {
          domainDescription = getI18NMessage(locale, domainTitleKey + ga.getDomain().getDescription().replace(" ", ""));
          if (domainDescription == null) {
            domainDescription = ga.getDomain().getDescription();
          }
        }
        domainDescription = escapeIllegalCharacterInMessage(domainDescription);
        sbResult.append(ga.getCreatedDate());
        sbResult.append(DELIMITER);
        sbResult.append(ga.getEarner());
        sbResult.append(DELIMITER);
        sbResult.append(actionLabel);
        sbResult.append(DELIMITER);
        sbResult.append(ga.getAction() != null ? ga.getAction().getType().name() : "-");
        sbResult.append(DELIMITER);
        sbResult.append(domainDescription);
        sbResult.append(DELIMITER);
        sbResult.append(ga.getScore());
        sbResult.append(DELIMITER);
        sbResult.append(ga.getStatus());
        sbResult.append(DELIMITER);
        sbResult.append(SEPARATOR);
      } catch (Exception e) {
        LOG.error("Error when computing to XLSX ", e);
      }
    });
    return sbResult.toString();
  }
}

package io.meeds.gamification.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.http.PATCH;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.rest.builder.RealizationBuilder;
import io.meeds.gamification.rest.model.RealizationList;
import io.meeds.gamification.rest.model.RealizationRestEntity;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/gamification/realizations")
@Tag(name = "/gamification/realizations", description = "Manages users realizations")
public class RealizationRest implements ResourceContainer {

  private static final Log   LOG = ExoLogger.getLogger(RealizationRest.class);

  private RuleService        ruleService;

  private RealizationService realizationService;

  private IdentityManager    identityManager;

  public RealizationRest(RuleService ruleService,
                         RealizationService realizationService,
                         IdentityManager identityManager) {
    this.ruleService = ruleService;
    this.realizationService = realizationService;
    this.identityManager = identityManager;
  }

  @GET
  @RolesAllowed("users")
  @Produces({
      MediaType.APPLICATION_JSON,
      "application/vnd.ms-excel",
      MediaType.TEXT_PLAIN
  })
  @Operation(summary = "Retrieves the list of achievements switch a filter. The returned format can be of type JSON or XLSX", method = "GET", description = "Retrieves the list of achievements switch a filter. The returned format can be of type JSON or XLSX")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response getRealizations(
                                  @Context
                                  HttpServletRequest httpRequest,
                                  @Parameter(description = "result fromDate", required = true)
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
                                  @Parameter(description = "earnerIds, that will be used to filter achievements", required = false)
                                  @QueryParam("earnerIds")
                                  List<String> earnerIds,
                                  @Parameter(description = "Offset of result")
                                  @DefaultValue("0")
                                  @QueryParam("offset")
                                  int offset,
                                  @Parameter(description = "Limit of result")
                                  @QueryParam("limit")
                                  int limit,
                                  @Parameter(description = "Response Type")
                                  @DefaultValue("")
                                  @QueryParam("returnType")
                                  String returnType,
                                  @Parameter(description = "identity Type")
                                  @QueryParam("identityType")
                                  String identityType,
                                  @Parameter(description = "domainIds. that will be used to filter achievements", required = false)
                                  @QueryParam("domainIds")
                                  List<Long> domainIds,
                                  @Parameter(description = "If true, this will return the list of realizations, the current user can manage. Possible values = true or false. Default value = false.", required = false)
                                  @QueryParam("owned")
                                  @DefaultValue("false")
                                  boolean owned,
                                  @Parameter(description = "If true, this will return the total count of filtered realizations. Possible values = true or false. Default value = false.", required = false)
                                  @QueryParam("returnSize")
                                  @DefaultValue("false")
                                  boolean returnSize) {
    if (StringUtils.isBlank(fromDate) || StringUtils.isBlank(toDate)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Dates must not be blank").build();
    }

    Identity identity = ConversationState.getCurrent().getIdentity();
    Date dateFrom = Utils.parseRFC3339Date(fromDate);
    Date dateTo = Utils.parseRFC3339Date(toDate);

    if (domainIds == null) {
      domainIds = new ArrayList<>();
    }

    RealizationFilter filter = new RealizationFilter(earnerIds,
                                                     sortField,
                                                     sortDescending,
                                                     owned,
                                                     dateFrom,
                                                     dateTo,
                                                     IdentityType.getType(identityType),
                                                     domainIds);

    boolean isXlsx = StringUtils.isNotBlank(returnType) && returnType.equals("xlsx");
    if (StringUtils.isNotBlank(returnType) && !returnType.equals("json") && !isXlsx) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Unsupported returnType, possible values: xlsx or json").build();
    }
    if (!isXlsx && limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    try {
      if (isXlsx) {
        String filename = "report_Actions";
        InputStream xlsxInputStream = realizationService.exportXlsx(filter, identity, filename, httpRequest.getLocale());
        return Response.ok(xlsxInputStream)
                       .header("Content-Disposition", "attachment; filename=" + filename + ".xlsx")
                       .header("Content-Type", "application/vnd.ms-excel")
                       .build();
      } else {
        List<RealizationDTO> realizations = realizationService.getRealizationsByFilter(filter,
                                                                                       identity,
                                                                                       offset,
                                                                                       limit);
        List<RealizationRestEntity> realizationRestEntities = RealizationBuilder.toRestEntities(ruleService,
                                                                                                identityManager,
                                                                                                realizations);
        RealizationList realizationList = new RealizationList();
        if (returnSize) {
          int realizationsSize = realizationService.countRealizationsByFilter(filter, identity);
          realizationList.setSize(realizationsSize);
        }
        realizationList.setRealizations(realizationRestEntities);
        realizationList.setOffset(offset);
        realizationList.setLimit(limit);
        return Response.ok(realizationList).build();
      }
    } catch (IllegalAccessException e) {
      LOG.debug("User '{}' isn't authorized to access achievements with parameter : earnerId = {}",
                identity.getUserId(),
                earnerIds,
                e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
  }

  @GET
  @Path("manager")
  @Produces(MediaType.TEXT_PLAIN)
  @RolesAllowed("users")
  @Operation(summary = "Return 'true' if the current user can manage some realizations, else return 'false'", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
  })
  public Response isRealizationManager() {
    String username = ConversationState.getCurrent().getIdentity().getUserId();
    return Response.ok(String.valueOf(realizationService.isRealizationManager(username))).build();
  }

  @PATCH
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed("users")
  @Operation(summary = "Updates an existing realization", method = "POST", description = "Updates an existing realization status")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
  })
  public Response updateRealizationStatus(
                                          @Parameter(description = "Realization technical identifier", required = true)
                                          @FormParam("id")
                                          String realizationId,
                                          @Parameter(description = "Realization technical identifier", required = true)
                                          @FormParam("status")
                                          String status) {
    if (status == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("status is mandatory").build();
    }
    String username = ConversationState.getCurrent().getIdentity().getUserId();
    try {
      realizationService.updateRealizationStatus(Long.parseLong(realizationId), RealizationStatus.valueOf(status), username);
      return Response.noContent().build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

}

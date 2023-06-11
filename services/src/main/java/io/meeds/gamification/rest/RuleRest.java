/*
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
package io.meeds.gamification.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.rest.builder.RuleBuilder;
import io.meeds.gamification.rest.model.ProgramWithRulesRestEntity;
import io.meeds.gamification.rest.model.RuleList;
import io.meeds.gamification.rest.model.RuleRestEntity;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/gamification/rules")
@Tag(name = "/gamification/rules", description = "Manages rules")
public class RuleRest implements ResourceContainer {
  private final CacheControl   cacheControl;

  protected ProgramService     programService;

  protected RuleService        ruleService;

  protected RealizationService realizationService;

  protected TranslationService translationService;

  protected IdentityManager    identityManager;

  public RuleRest(ProgramService programService,
                  RuleService ruleService,
                  RealizationService realizationService,
                  TranslationService translationService,
                  IdentityManager identityManager) {
    cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    this.programService = programService;
    this.ruleService = ruleService;
    this.realizationService = realizationService;
    this.translationService = translationService;
    this.identityManager = identityManager;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of available rules", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response getRules(// NOSONAR
                           @Context
                           HttpServletRequest request,
                           @Parameter(description = "Offset of results to retrieve", required = false)
                           @QueryParam("offset")
                           @DefaultValue("0")
                           int offset,
                           @Parameter(description = "Limit of results to retrieve", required = false)
                           @QueryParam("limit")
                           @DefaultValue("20")
                           int limit,
                           @Parameter(description = "Used to filter rules by program", required = false)
                           @QueryParam("programId")
                           long programId,
                           @Parameter(description = "Rules type filtering, possible values: AUTOMATIC, MANUAL and ALL. Default value = ALL.", required = false)
                           @QueryParam("type")
                           @DefaultValue("ALL")
                           EntityFilterType ruleType,
                           @Parameter(description = "Rules status filtering, possible values: ENABLED, DISABLED and ALL. Default value = ALL.", required = false)
                           @QueryParam("status")
                           @DefaultValue("ALL")
                           EntityStatusType ruleStatus,
                           @Parameter(description = "Rule period filtering. Possible values: STARTED, NOT_STARTED, ENDED, ALL")
                           @Schema(defaultValue = "ALL")
                           @DefaultValue("ALL")
                           @QueryParam("dateFilter")
                           DateFilterType dateFilter,
                           @Parameter(description = "term to search rules with")
                           @QueryParam("term")
                           String term,
                           @Parameter(description = "Sort field. Possible values: createdDate, startDate, endDate or score.")
                           @QueryParam("sortBy")
                           @DefaultValue("score")
                           String sortField,
                           @Parameter(description = "Whether to retrieve results sorted descending or not")
                           @QueryParam("sortDescending")
                           @DefaultValue("true")
                           boolean sortDescending,
                           @Parameter(description = "Accepted users realizations count")
                           @Schema(defaultValue = "0")
                           @QueryParam("realizationsLimit")
                           int realizationsLimit,
                           @Parameter(description = "Group rules by program")
                           @Schema(defaultValue = "false")
                           @QueryParam("groupByProgram")
                           boolean groupByProgram,
                           @Parameter(description = "Whether Sort by popular rules or not")
                           @DefaultValue("false")
                           @QueryParam("orderByRealizations")
                           boolean orderByRealizations,
                           @Parameter(description = "Excluded rule Ids", required = false)
                           @QueryParam("excludedRuleIds")
                           List<Long> excludedRuleIds,
                           @Parameter(description = "Rule period filtering. Possible values: WEEK, MONTH, YEAR, ALL")
                           @Schema(defaultValue = "WEEK")
                           @DefaultValue("ALL")
                           @QueryParam("period")
                           PeriodType periodType,
                           @Parameter(description = "If true, this will return the total count of filtered programs. Possible values = true or false. Default value = false.", required = false)
                           @QueryParam("returnSize")
                           @DefaultValue("false")
                           boolean returnSize,
                           @Parameter(description = "Asking for a full representation of a specific subresource, ex: userRealizations")
                           @QueryParam("expand")
                           String expand) {

    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }

    String currentUser = Utils.getCurrentUser();
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setTerm(term);
    ruleFilter.setDateFilterType(dateFilter == null ? DateFilterType.ALL : dateFilter);
    ruleFilter.setEntityFilterType(ruleType == null ? EntityFilterType.ALL : ruleType);
    ruleFilter.setEntityStatusType(ruleStatus == null ? EntityStatusType.ALL : ruleStatus);
    ruleFilter.setOrderByRealizations(orderByRealizations);
    ruleFilter.setExcludedRuleIds(excludedRuleIds);
    ruleFilter.setProgramId(programId);
    ruleFilter.setSortBy(sortField);
    ruleFilter.setSortDescending(sortDescending);
    String[] expandFieldsArray = StringUtils.split(expand, ",");
    List<String> expandFields = expandFieldsArray == null ? Collections.emptyList() : Arrays.asList(expandFieldsArray);

    if (periodType == null) {
      periodType = PeriodType.WEEK;
    }

    try {
      if (!groupByProgram || programId > 0) {
        RuleList ruleList = new RuleList();
        List<RuleRestEntity> ruleEntities = getUserRulesByProgram(ruleFilter,
                                                                  periodType,
                                                                  getLocale(request),
                                                                  expandFields,
                                                                  currentUser,
                                                                  offset,
                                                                  limit,
                                                                  realizationsLimit,
                                                                  programId > 0);
        ruleList.setRules(ruleEntities);
        ruleList.setOffset(offset);
        ruleList.setLimit(limit);
        if (returnSize) {
          ruleList.setSize(ruleService.countRules(ruleFilter, currentUser));
        }
        return Response.ok(ruleList).build();
      } else {
        ProgramFilter programFilter = new ProgramFilter();
        programFilter.setEntityFilterType(EntityFilterType.ALL);
        programFilter.setEntityStatusType(EntityStatusType.ENABLED);
        List<ProgramDTO> programs = programService.getPrograms(programFilter, currentUser, 0, -1);
        List<ProgramWithRulesRestEntity> programsWithRules = new ArrayList<>();
        for (ProgramDTO program : programs) {
          ProgramWithRulesRestEntity programWithRule = new ProgramWithRulesRestEntity(program);
          ruleFilter.setProgramId(program.getId());
          List<RuleRestEntity> ruleEntities = getUserRulesByProgram(ruleFilter,
                                                                    periodType,
                                                                    getLocale(request),
                                                                    expandFields,
                                                                    currentUser,
                                                                    offset,
                                                                    limit,
                                                                    realizationsLimit,
                                                                    true);
          programWithRule.setRules(ruleEntities);
          programWithRule.setOffset(offset);
          programWithRule.setLimit(limit);
          if (returnSize) {
            programWithRule.setSize(ruleService.countRules(ruleFilter, currentUser));
          }
          programsWithRules.add(programWithRule);
        }
        return Response.ok(programsWithRules).build();
      }
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("{id}")
  @Operation(summary = "Retrieves the list of available rules", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public Response getRule(
                          @Context
                          HttpServletRequest request,
                          @Parameter(description = "Rule technical identifier")
                          @PathParam("id")
                          long id,
                          @Parameter(description = "Accepted users realizations count")
                          @Schema(defaultValue = "0")
                          @QueryParam("realizationsLimit")
                          int realizationsLimit,
                          @Parameter(description = "Asking for a full representation of a specific subresource, ex: countRealizations", required = false)
                          @QueryParam("expand")
                          String expand) {
    String currentUser = Utils.getCurrentUser();
    try {
      RuleDTO rule = ruleService.findRuleById(id, currentUser);
      String[] expandFieldsArray = StringUtils.split(expand, ",");
      List<String> expandFields = expandFieldsArray == null ? Collections.emptyList() : Arrays.asList(expandFieldsArray);
      RuleRestEntity ruleEntity = RuleBuilder.toRestEntity(programService,
                                                           ruleService,
                                                           realizationService,
                                                           translationService,
                                                           rule,
                                                           getLocale(request),
                                                           expandFields,
                                                           realizationsLimit,
                                                           false,
                                                           PeriodType.ALL);
      return Response.ok(ruleEntity).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
    } catch (IllegalAccessException e) {
      return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Creates a rule", method = "POST")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response createRule(
                             @Context
                             HttpServletRequest request,
                             @RequestBody(description = "rule object to save", required = true)
                             RuleDTO ruleDTO) {
    if (ruleDTO == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Rule object is mandatory").build();
    }
    String username = Utils.getCurrentUser();
    try {
      ruleDTO = ruleService.createRule(ruleDTO, username);
      return Response.ok().cacheControl(cacheControl).entity(toRestEntity(ruleDTO, getLocale(request))).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    } catch (ObjectAlreadyExistsException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Updates a rule", method = "PUT")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response updateRule(
                             @Context
                             HttpServletRequest request,
                             @RequestBody(description = "rule object to update", required = true)
                             RuleDTO ruleDTO) {
    String username = Utils.getCurrentUser();

    if (ruleDTO == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Rule object is mandatory").build();
    }
    try {
      ruleDTO = ruleService.updateRule(ruleDTO, username);
      return Response.ok().cacheControl(cacheControl).entity(toRestEntity(ruleDTO, getLocale(request))).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("{ruleId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Deletes a rule", method = "DELETE")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response deleteRule(
                             @Context
                             HttpServletRequest request,
                             @Parameter(description = "Rule technical identifier", required = true)
                             @PathParam("ruleId")
                             long ruleId) {
    if (ruleId <= 0) {
      return Response.status(Status.BAD_REQUEST).entity("Rule technical identifier must be positive").build();
    }
    String username = Utils.getCurrentUser();
    try {
      RuleDTO ruleDTO = ruleService.deleteRuleById(ruleId, username);
      return Response.ok().cacheControl(cacheControl).entity(toRestEntity(ruleDTO, getLocale(request))).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  private Locale getLocale(HttpServletRequest request) {
    return request == null ? null : request.getLocale();
  }

  private List<RuleRestEntity> getUserRulesByProgram(RuleFilter filter, // NOSONAR
                                                     PeriodType periodType,
                                                     Locale locale,
                                                     List<String> expandFields,
                                                     String username,
                                                     int offset,
                                                     int limit,
                                                     int realizationsLimit,
                                                     boolean noProgram) {
    List<RuleDTO> rules = ruleService.getRules(filter, username, offset, limit);
    return rules.stream()
                .map(rule -> RuleBuilder.toRestEntity(programService,
                                                      ruleService,
                                                      realizationService,
                                                      translationService,
                                                      rule,
                                                      locale,
                                                      expandFields,
                                                      realizationsLimit,
                                                      noProgram,
                                                      periodType))
                .toList();
  }

  private RuleRestEntity toRestEntity(RuleDTO rule, Locale locale) {
    return RuleBuilder.toRestEntity(programService,
                                    ruleService,
                                    realizationService,
                                    translationService,
                                    rule,
                                    locale,
                                    null,
                                    0,
                                    false,
                                    PeriodType.ALL);
  }
}

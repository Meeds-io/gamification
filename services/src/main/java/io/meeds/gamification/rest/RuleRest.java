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

import static io.meeds.gamification.utils.Utils.*;

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.common.xmlprocessor.XMLProcessor;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.metadata.favorite.FavoriteService;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.RulePublication;
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
import io.meeds.portal.security.service.SecuritySettingService;
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

  private final CacheControl       cacheControl;

  protected ProgramService         programService;

  protected RuleService            ruleService;

  protected RealizationService     realizationService;

  protected TranslationService     translationService;

  protected FavoriteService        favoriteService;

  protected IdentityManager        identityManager;

  protected SecuritySettingService securitySettingService;

  protected ActivityManager        activityManager;

  protected XMLProcessor           xmlProcessor;

  protected UserACL                userAcl;

  public RuleRest(ProgramService programService, // NOSONAR
                  RuleService ruleService,
                  RealizationService realizationService,
                  TranslationService translationService,
                  FavoriteService favoriteService,
                  IdentityManager identityManager,
                  SecuritySettingService securitySettingService,
                  ActivityManager activityManager,
                  XMLProcessor xmlProcessor,
                  UserACL userAcl) {
    cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    this.programService = programService;
    this.ruleService = ruleService;
    this.realizationService = realizationService;
    this.translationService = translationService;
    this.favoriteService = favoriteService;
    this.identityManager = identityManager;
    this.securitySettingService = securitySettingService;
    this.activityManager = activityManager;
    this.userAcl = userAcl;
    this.xmlProcessor = xmlProcessor;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
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
                           @Parameter(description = "Used to filter rules by space audience", required = false)
                           @QueryParam("spaceId")
                           List<Long> spaceIds,
                           @Parameter(description = "Rules type filtering, possible values: AUTOMATIC, MANUAL and ALL. Default value = ALL.", required = false)
                           @QueryParam("type")
                           @DefaultValue("ALL")
                           EntityFilterType ruleType,
                           @Parameter(description = "Programs status filtering, possible values: ENABLED, DISABLED and ALL. Default value = ENABLED.", required = false)
                           @QueryParam("programStatus")
                           @DefaultValue("ALL")
                           EntityStatusType programStatus,
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
                           @Parameter(description = "Whether to search in favorites only or not", required = false)
                           @DefaultValue("false")
                           @QueryParam("favorites")
                           boolean favorites,
                           @Parameter(description = "Whether to search in favorites only or not", required = false)
                           @QueryParam("tags")
                           List<String> tagNames,
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
                           @Schema(defaultValue = "ALL")
                           @DefaultValue("ALL")
                           @QueryParam("period")
                           PeriodType periodType,
                           @Parameter(description = "If true, this will return the total count of filtered programs. Possible values = true or false. Default value = false.", required = false)
                           @QueryParam("returnSize")
                           @DefaultValue("false")
                           boolean returnSize,
                           @Parameter(description = "Used to retrieve the title and description in requested language")
                           @QueryParam("lang")
                           String lang,
                           @Parameter(description = "Asking for a full representation of a specific subresource, ex: userRealizations")
                           @QueryParam("expand")
                           String expand) {

    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    if (!Utils.canAccessAnonymousResources(securitySettingService)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    Locale locale = getLocale(lang);

    String currentUser = getCurrentUser();
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setTerm(term);
    ruleFilter.setFavorites(favorites);
    ruleFilter.setTagNames(tagNames);
    ruleFilter.setLocale(locale);
    ruleFilter.setDateFilterType(dateFilter == null ? DateFilterType.ALL : dateFilter);
    ruleFilter.setType(ruleType == null ? EntityFilterType.ALL : ruleType);
    ruleFilter.setStatus(ruleStatus == null ? EntityStatusType.ALL : ruleStatus);
    ruleFilter.setProgramStatus(programStatus == null ? EntityStatusType.ALL : programStatus);
    ruleFilter.setOrderByRealizations(orderByRealizations);
    ruleFilter.setExcludedRuleIds(excludedRuleIds);
    ruleFilter.setProgramId(programId);
    ruleFilter.setSpaceIds(spaceIds);
    ruleFilter.setIdentityId(getCurrentUserIdentityId());
    ruleFilter.setExcludeNoSpace(CollectionUtils.isNotEmpty(spaceIds));
    ruleFilter.setSortBy(sortField);
    ruleFilter.setSortDescending(sortDescending);
    List<String> expandFields = getExpandOptions(expand);

    try {
      if (!groupByProgram || programId > 0) {
        RuleList ruleList = new RuleList();
        List<RuleRestEntity> ruleEntities = getRules(ruleFilter,
                                                     periodType,
                                                     locale,
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
        programFilter.setStatus(programStatus);
        List<ProgramDTO> programs = programService.getPrograms(programFilter, currentUser, 0, -1);
        List<ProgramWithRulesRestEntity> programsWithRules = new ArrayList<>();
        for (ProgramDTO program : programs) {
          ProgramWithRulesRestEntity programWithRule = new ProgramWithRulesRestEntity(program);
          ruleFilter.setProgramId(program.getId());
          List<RuleRestEntity> ruleEntities = getRules(ruleFilter,
                                                       periodType,
                                                       locale,
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
                          @Parameter(description = "Used to retrieve the title and description in requested language")
                          @QueryParam("lang")
                          String lang,
                          @Parameter(description = "Asking for a full representation of a specific subresource, ex: countRealizations", required = false)
                          @QueryParam("expand")
                          String expand) {
    if (!Utils.canAccessAnonymousResources(securitySettingService)) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    String currentUser = getCurrentUser();
    try {
      RuleDTO rule = ruleService.findRuleById(id, currentUser);
      List<String> expandFields = getExpandOptions(expand);
      RuleRestEntity ruleEntity = RuleBuilder.toRestEntity(programService,
                                                           ruleService,
                                                           realizationService,
                                                           translationService,
                                                           favoriteService,
                                                           identityManager,
                                                           activityManager,
                                                           xmlProcessor,
                                                           userAcl,
                                                           rule,
                                                           getLocale(lang),
                                                           expandFields,
                                                           realizationsLimit,
                                                           false,
                                                           isAnonymous(),
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
                             RulePublication rule) {
    if (rule == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Rule object is mandatory").build();
    }
    String username = getCurrentUser();
    try {
      RuleDTO createdRule = ruleService.createRule(rule, username);
      return Response.ok().cacheControl(cacheControl).entity(toRestEntity(createdRule, getLocale(request))).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
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
                             RulePublication rule) {
    String username = getCurrentUser();

    if (rule == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Rule object is mandatory").build();
    }
    try {
      RuleDTO updatedRule = ruleService.updateRule(rule, username);
      return Response.ok().cacheControl(cacheControl).entity(toRestEntity(updatedRule, getLocale(request))).build();
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
    String username = getCurrentUser();
    try {
      RuleDTO rule = ruleService.deleteRuleById(ruleId, username);
      return Response.ok().cacheControl(cacheControl).entity(toRestEntity(rule, getLocale(request))).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  private Locale getLocale(String lang) {
    return StringUtils.isBlank(lang) ? null : Locale.forLanguageTag(lang);
  }

  private Locale getLocale(HttpServletRequest request) {
    return request == null ? null : request.getLocale();
  }

  private List<String> getExpandOptions(String expand) {
    String[] expandFieldsArray = StringUtils.split(expand, ",");
    return expandFieldsArray == null ? Collections.emptyList() : Arrays.asList(expandFieldsArray);
  }

  private List<RuleRestEntity> getRules(RuleFilter filter, // NOSONAR
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
                                                      favoriteService,
                                                      identityManager,
                                                      activityManager,
                                                      xmlProcessor,
                                                      userAcl,
                                                      rule,
                                                      locale,
                                                      expandFields,
                                                      realizationsLimit,
                                                      noProgram,
                                                      isAnonymous(),
                                                      periodType))
                .toList();
  }

  private RuleRestEntity toRestEntity(RuleDTO rule, Locale locale) {
    return RuleBuilder.toRestEntity(programService,
                                    ruleService,
                                    realizationService,
                                    translationService,
                                    favoriteService,
                                    identityManager,
                                    activityManager,
                                    xmlProcessor,
                                    userAcl,
                                    rule,
                                    locale,
                                    null,
                                    0,
                                    false,
                                    isAnonymous(),
                                    PeriodType.ALL);
  }
}

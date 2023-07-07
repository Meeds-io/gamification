/**
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.rest.api.RestUtils;

import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.model.ProgramColorAlreadyExists;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.rest.builder.ProgramBuilder;
import io.meeds.gamification.rest.model.ProgramList;
import io.meeds.gamification.rest.model.ProgramRestEntity;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.social.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/gamification/programs")
public class ProgramRest implements ResourceContainer {

  private static final String       DEFAULT_COVER_PATH        =
                                                       System.getProperty("meeds.engagementCenter.program.defaultCoverPath",
                                                                          "/skin/images/program_default_cover.png");

  private static final String       DEFAULT_AVATAR_PATH       =
                                                        System.getProperty("meeds.engagementCenter.program.defaultAvatarPath",
                                                                           "/skin/images/program_default_avatar.png");

  private static final String       PROGRAM_NOT_FOUND_MESSAGE = "The program doesn't exit";

  private static final Log          LOG                       = ExoLogger.getLogger(ProgramRest.class);

  // 7 days
  private static final int          CACHE_IN_SECONDS          = 604800;

  private static final int          CACHE_IN_MILLI_SECONDS    = CACHE_IN_SECONDS * 1000;

  private static final CacheControl CACHECONTROL              = new CacheControl();

  static {
    CACHECONTROL.setMaxAge(CACHE_IN_SECONDS);
    CACHECONTROL.setPrivate(false);
  }

  protected PortalContainer    portalContainer;

  protected ProgramService     programService;

  protected RuleService        ruleService;

  protected IdentityManager    identityManager;

  protected TranslationService translationService;

  public byte[]                defaultProgramCover  = null; // NOSONAR

  public byte[]                defaultProgramAvatar = null; // NOSONAR

  public ProgramRest(PortalContainer portalContainer,
                     ProgramService programService,
                     RuleService ruleService,
                     TranslationService translationService,
                     IdentityManager identityManager) {
    this.portalContainer = portalContainer;
    this.programService = programService;
    this.ruleService = ruleService;
    this.translationService = translationService;
    this.identityManager = identityManager;
  }

  @GET
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN
  })
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of available programs", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response getPrograms(
                              @Context
                              HttpServletRequest request,
                              @Parameter(description = "Offset of results to retrieve")
                              @QueryParam("offset")
                              @DefaultValue("0")
                              int offset,
                              @Parameter(description = "Limit of results to retrieve")
                              @QueryParam("limit")
                              @DefaultValue("0")
                              int limit,
                              @Parameter(description = "Programs status filtering, possible values: ENABLED, DISABLED and ALL. Default value = ENABLED.", required = false)
                              @QueryParam("status")
                              @DefaultValue("ENABLED")
                              EntityStatusType programStatus,
                              @Parameter(description = "Whether retrieve owned programs only or not")
                              @QueryParam("owned")
                              boolean owned,
                              @Parameter(description = "If true, this will return the filtered programs sorted by budget. Possible values = true or false. Default value = false.", required = false)
                              @QueryParam("sortByBudget")
                              @DefaultValue("false")
                              boolean sortByBudget,
                              @Parameter(description = "If true, this will return the filtered programs including deleted ones. Possible values = true or false. Default value = false.", required = false)
                              @QueryParam("includeDeleted")
                              @DefaultValue("false")
                              boolean includeDeleted,
                              @Parameter(description = "If true, this will return the total count of filtered programs. Possible values = true or false. Default value = false.", required = false)
                              @QueryParam("returnSize")
                              @DefaultValue("false")
                              boolean returnSize,
                              @Parameter(description = "Used to retrieve the title and description in requested language")
                              @QueryParam("lang")
                              String lang,
                              @Parameter(description = "Term to search.")
                              @QueryParam("query")
                              String query,
                              @Parameter(description = "Sort field. Possible values: createdDate, startDate, endDate or score.")
                              @QueryParam("sortBy")
                              @DefaultValue("title")
                              String sortField,
                              @Parameter(description = "Whether to retrieve results sorted descending or not")
                              @QueryParam("sortDescending")
                              @DefaultValue("true")
                              boolean sortDescending,
                              @Parameter(description = "Used to retrieve extra information about the program")
                              @QueryParam("expand")
                              String expand) {

    ProgramFilter programFilter = new ProgramFilter();
    programFilter.setSortByBudget(sortByBudget);
    programFilter.setIncludeDeleted(includeDeleted);
    programFilter.setStatus(programStatus);
    programFilter.setSortBy(sortField);
    programFilter.setSortDescending(sortDescending);
    if (StringUtils.isNotEmpty(query)) {
      programFilter.setProgramTitle(query);
    }
    if (owned) {
      programFilter.setOwnerId(getCurrentUserIdentityId());
    }
    List<String> expandFields = getExpandOptions(expand);

    String currentUser = getCurrentUser();
    try {
      ProgramList programList = new ProgramList();
      List<ProgramRestEntity> programs = getProgramsRestEntitiesByFilter(programFilter,
                                                                         getLocale(lang),
                                                                         offset,
                                                                         limit,
                                                                         expandFields,
                                                                         currentUser);
      if (returnSize) {
        int programsSize = programService.countPrograms(programFilter, currentUser);
        programList.setSize(programsSize);
      }
      programList.setPrograms(programs);
      programList.setOffset(offset);
      programList.setLimit(limit);
      return Response.ok(programList).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
  }

  @POST
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN
  })
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Creates a program", method = "POST")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response createProgram(
                                @Context
                                HttpServletRequest request,
                                @Parameter(description = "Program object to create", required = true)
                                ProgramDTO program) {
    if (program == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Program object is mandatory").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      program = programService.createProgram(program, identity);
      return Response.ok(ProgramBuilder.toRestEntity(programService,
                                                     ruleService,
                                                     translationService,
                                                     program,
                                                     getLocale(request),
                                                     null,
                                                     identity.getUserId()))
                     .build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    } catch (ProgramColorAlreadyExists e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("program.form.programColor.programColorAlreadyUsed").type(MediaType.TEXT_PLAIN).build();
    }
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Updates an existing program", method = "PUT")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response updateProgram(
                                @Context
                                HttpServletRequest request,
                                @Parameter(description = "Program id", required = true)
                                @PathParam("id")
                                long programId,
                                @Parameter(description = "Program object to update", required = true)
                                ProgramDTO program) {
    if (program == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Program object is mandatory").build();
    }
    if (programId <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Program technical identifier must be positive").build();
    }
    program.setId(programId);
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      program = programService.updateProgram(program, identity);
      return Response.ok(ProgramBuilder.toRestEntity(programService,
                                                     ruleService,
                                                     translationService,
                                                     program,
                                                     getLocale(request),
                                                     null,
                                                     identity.getUserId()))
                     .build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(PROGRAM_NOT_FOUND_MESSAGE).build();
    } catch (ProgramColorAlreadyExists e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("program.form.programColor.programColorAlreadyUsed").type(MediaType.TEXT_PLAIN).build();
    }
  }

  @DELETE
  @RolesAllowed("users")
  @Path("{programId}")
  @Operation(summary = "Deletes an existing program identified by its id", method = "DELETE")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response deleteProgram(
                                @Context
                                HttpServletRequest request,
                                @Parameter(description = "Program id to be deleted", required = true)
                                @PathParam("programId")
                                long programId) {
    if (programId <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("The parameter 'id' must be positive integer").build();
    }
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      programService.deleteProgramById(programId, identity);
      return Response.noContent().build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("{id}/color")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_PLAIN)
  @Operation(summary = "Returns true if the color can be used, else return false", method = "POST")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled")
  })
  public Response checkProgramColorValidity(
                                            @Parameter(description = "program id", required = true)
                                            @PathParam("id")
                                            long programId,
                                            @Parameter(description = "Program color to check its validity", required = true)
                                            @FormParam("color")
                                            String color) {
    return Response.ok(String.valueOf(programService.canUseProgramColor(programId, color))).build();
  }

  @GET
  @Path("{id}/cover")
  @Produces("image/png")
  @Operation(summary = "Gets a program cover", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "403", description = "Forbidden request"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  public Response getProgramCover(
                                  @Context
                                  Request request,
                                  @Parameter(description = "The value of lastModified parameter will determine whether the query should be cached by browser or not. If not set, no 'expires HTTP Header will be sent'")
                                  @QueryParam("lastModified")
                                  Long lastModified,
                                  @Parameter(description = "program id", required = true)
                                  @PathParam("id")
                                  String programId,
                                  @Parameter(description = "A mandatory valid token that is used to authorize anonymous request", required = true)
                                  @QueryParam("r")
                                  String token) throws IOException {
    boolean isDefault = StringUtils.equals(DEFAULT_COVER_REMOTE_ID, programId);
    String lastUpdated = null;
    ProgramDTO program = null;
    if (isDefault) {
      lastUpdated = toRFC3339Date(new Date(DEFAULT_LAST_MODIFIED));
    } else {
      program = programService.getProgramById(Long.valueOf(programId));
      if (program == null) {
        return Response.status(Response.Status.NOT_FOUND).entity(PROGRAM_NOT_FOUND_MESSAGE).build();
      }
      isDefault = program.getCoverFileId() == 0;
      lastUpdated = program.getLastModifiedDate();
    }

    try {
      if (!isDefault && RestUtils.isAnonymous()
          && !isAttachmentTokenValid(token, programId, ATTACHMENT_COVER_TYPE, lastModified)) {
        LOG.warn("An anonymous user attempts to access avatar of space {} without a valid access token", programId);
        return Response.status(Response.Status.FORBIDDEN).build();
      }
      EntityTag eTag = new EntityTag(lastUpdated);
      Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
      if (builder == null) {
        InputStream stream = isDefault ? getDefaultCoverInputStream()
                                       : programService.getProgramCoverStream(Long.valueOf(programId));
        builder = Response.ok(stream, "image/png");
        builder.tag(eTag);
      }
      if (lastModified != null) {
        builder.cacheControl(CACHECONTROL);
        builder.lastModified(parseRFC3339Date(lastUpdated));
        builder.expires(new Date(System.currentTimeMillis() + CACHE_IN_MILLI_SECONDS));
      }
      return builder.build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(PROGRAM_NOT_FOUND_MESSAGE).build();
    }
  }

  @GET
  @Path("{id}/avatar")
  @Produces("image/png")
  @Operation(summary = "Gets a program avatar", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "403", description = "Forbidden request"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  public Response getProgramAvatar(
                                   @Context
                                   Request request,
                                   @Parameter(description = "The value of lastModified parameter will determine whether the query should be cached by browser or not. If not set, no 'expires HTTP Header will be sent'")
                                   @QueryParam("lastModified")
                                   Long lastModified,
                                   @Parameter(description = "program id", required = true)
                                   @PathParam("id")
                                   String programId,
                                   @Parameter(description = "A mandatory valid token that is used to authorize anonymous request", required = true)
                                   @QueryParam("r")
                                   String token) throws IOException {
    boolean isDefault = StringUtils.equals(DEFAULT_AVATAR_REMOTE_ID, programId);
    String lastUpdated = null;
    ProgramDTO program = null;
    if (isDefault) {
      lastUpdated = toRFC3339Date(new Date(DEFAULT_LAST_MODIFIED));
    } else {
      program = programService.getProgramById(Long.valueOf(programId));
      if (program == null) {
        return Response.status(Response.Status.NOT_FOUND).entity(PROGRAM_NOT_FOUND_MESSAGE).build();
      }
      isDefault = program.getAvatarFileId() == 0;
      lastUpdated = program.getLastModifiedDate();
    }

    try {
      if (!isDefault && RestUtils.isAnonymous()
          && !isAttachmentTokenValid(token, programId, ATTACHMENT_AVATAR_TYPE, lastModified)) {
        LOG.warn("An anonymous user attempts to access avatar of space {} without a valid access token", programId);
        return Response.status(Response.Status.FORBIDDEN).build();
      }
      EntityTag eTag = new EntityTag(lastUpdated);
      Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
      if (builder == null) {
        InputStream stream = isDefault ? getDefaultAvatarInputStream()
                                       : programService.getProgramAvatarStream(Long.valueOf(programId));
        builder = Response.ok(stream, "image/png");
        builder.tag(eTag);
      }
      if (lastModified != null) {
        builder.cacheControl(CACHECONTROL);
        builder.lastModified(parseRFC3339Date(lastUpdated));
        builder.expires(new Date(System.currentTimeMillis() + CACHE_IN_MILLI_SECONDS));
      }
      return builder.build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(PROGRAM_NOT_FOUND_MESSAGE).build();
    }
  }

  @DELETE
  @RolesAllowed("users")
  @Path("{programId}/cover")
  @Operation(summary = "Deletes program cover identified by program id", method = "DELETE")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
  })
  public Response deleteProgramCover(
                                     @Context
                                     HttpServletRequest request,
                                     @Parameter(description = "Program identifier", required = true)
                                     @PathParam("programId")
                                     long programId) {
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      programService.deleteProgramCoverById(programId, identity);
      return Response.noContent().build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @RolesAllowed("users")
  @Path("{programId}/avatar")
  @Operation(summary = "Deletes program avatar identified by program id", method = "DELETE")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
  })
  public Response deleteProgramAvatar(
                                      @Context
                                      HttpServletRequest request,
                                      @Parameter(description = "Program identifier", required = true)
                                      @PathParam("programId")
                                      long programId) {
    org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
    try {
      programService.deleteProgramAvatarById(programId, identity);
      return Response.noContent().build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{programId}")
  @RolesAllowed("users")
  @Operation(summary = "Retrieves a program by its technical identifier", method = "GET")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response getProgramById(
                                 @Context
                                 HttpServletRequest request,
                                 @Parameter(description = "Program technical identifier", required = true)
                                 @PathParam("programId")
                                 long programId,
                                 @Parameter(description = "Used to retrieve the title and description in requested language")
                                 @QueryParam("lang")
                                 String lang,
                                 @Parameter(description = "Used to retrieve extra information about the program")
                                 @QueryParam("expand")
                                 String expand) {
    if (programId == 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Program Id must be not null").build();
    }
    String currentUser = getCurrentUser();
    try {
      ProgramDTO program = programService.getProgramById(programId, currentUser);
      List<String> expandFields = getExpandOptions(expand);
      return Response.ok(ProgramBuilder.toRestEntity(programService,
                                                     ruleService,
                                                     translationService,
                                                     program,
                                                     getLocale(lang),
                                                     expandFields,
                                                     currentUser))
                     .build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
    } catch (IllegalAccessException e) {
      return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
    }
  }

  private Locale getLocale(String lang) {
    return StringUtils.isBlank(lang) ? null : Locale.forLanguageTag(lang);
  }

  private Locale getLocale(HttpServletRequest request) {
    return request == null ? null : request.getLocale();
  }

  private List<ProgramRestEntity> getProgramsRestEntitiesByFilter(ProgramFilter filter,
                                                                  Locale locale,
                                                                  int offset,
                                                                  int limit,
                                                                  List<String> expandFields,
                                                                  String currentUser) throws IllegalAccessException {
    List<ProgramDTO> programs = programService.getPrograms(filter, currentUser, offset, limit);
    return ProgramBuilder.toRestEntities(programService,
                                         ruleService,
                                         translationService,
                                         locale,
                                         programs,
                                         expandFields,
                                         currentUser);
  }

  private InputStream getDefaultCoverInputStream() throws IOException {
    if (defaultProgramCover == null) {
      InputStream is = portalContainer.getPortalContext().getResourceAsStream(DEFAULT_COVER_PATH);
      if (is == null) {
        defaultProgramCover = new byte[] {};
      } else {
        defaultProgramCover = IOUtil.getStreamContentAsBytes(is);
      }
    }
    return new ByteArrayInputStream(defaultProgramCover);
  }

  private InputStream getDefaultAvatarInputStream() throws IOException {
    if (defaultProgramAvatar == null) {
      InputStream is = portalContainer.getPortalContext().getResourceAsStream(DEFAULT_AVATAR_PATH);
      if (is == null) {
        defaultProgramAvatar = new byte[] {};
      } else {
        defaultProgramAvatar = IOUtil.getStreamContentAsBytes(is);
      }
    }
    return new ByteArrayInputStream(defaultProgramAvatar);
  }

}

package org.exoplatform.addons.gamification.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import javax.ws.rs.core.UriInfo;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.rest.model.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.mapper.AnnouncementBuilder;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.rest.api.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/gamification/announcements")
@Tag(name = "/gamification/announcements", description = "Manages announcement associated to users")
public class AnnouncementRest implements ResourceContainer {

  private final CacheControl  cacheControl;

  private AnnouncementService announcementService;

  public AnnouncementRest(AnnouncementService announcementService) {
    this.announcementService = announcementService;
    cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of announcements for a given rule identified by its id", method = "GET", description = "Retrieves the list of announcements for a given rule identified by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
  })
  public Response getAnnouncementsByRuleId(
                                           @Context
                                           Request request, @Context
                                           UriInfo uriInfo,
                                           @Parameter(description = "Rule technical identifier", required = true)
                                           @QueryParam("ruleId")
                                           long ruleId,
                                           @Parameter(description = "earner type, user or space", required = false)
                                           @DefaultValue("USER")
                                           @QueryParam("type")
                                           IdentityType earnerType,
                                           @Parameter(description = "Offset of result")
                                           @DefaultValue("0")
                                           @QueryParam("offset")
                                           int offset,
                                           @Parameter(description = "Limit of result")
                                           @DefaultValue("10")
                                           @QueryParam("limit")
                                           int limit) {
    if (ruleId <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("ruleId is mandatory").build();
    }
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    if (earnerType == null) {
      earnerType = IdentityType.USER;
    }
    EntityTag eTag = null;
    try {
      List<Announcement> announcements = announcementService.findAnnouncements(ruleId,
                                                                                      offset,
                                                                                      limit,
                                                                                      PeriodType.ALL,
                                                                                      earnerType,
                                                                                      RestUtils.getCurrentUser());
      List<AnnouncementRestEntity> announcementsRestEntities = announcements.stream()
                                                                            .map(AnnouncementBuilder::fromAnnouncement)
                                                                            .toList();
      eTag = new EntityTag(String.valueOf(announcementsRestEntities.hashCode()));
      Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);

      if (builder == null) {
        builder = org.exoplatform.social.rest.api.EntityBuilder.getResponseBuilder(announcementsRestEntities,
                                                                                   uriInfo,
                                                                                   RestUtils.getJsonMediaType(),
                                                                                   Response.Status.OK);
        builder.tag(eTag);
        Date date = new Date(System.currentTimeMillis());
        builder.lastModified(date);
        builder.expires(date);
        builder.cacheControl(cacheControl);
      }
      return builder.build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Creates a new Announcement", method = "POST", description = "Creates a new Announcement")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "500", description = "Internal server error"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
  })
  public Response createAnnouncement(
                                     @RequestBody(description = "Announcement object to create", required = true)
                                     AnnouncementActivity announcementActivity) {
    if (announcementActivity == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("announcement object is mandatory").build();
    }
    String currentUser = Utils.getCurrentUser();
    try {
      Announcement announcement = AnnouncementBuilder.fromAnnouncementActivity(announcementActivity);
      Announcement newAnnouncement = announcementService.createAnnouncement(announcement,
                                                                            announcementActivity.getTemplateParams(),
                                                                            currentUser,
                                                                            false);
      return Response.ok(AnnouncementBuilder.fromAnnouncement(newAnnouncement)).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (IllegalArgumentException | IllegalStateException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("{announcementId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Cancels an existing announcement", method = "DELETE", description = "Cancels an existing announcement")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
  })
  public Response cancelAnnouncement(@Parameter(description = "Announcement technical identifier", required = true)
  @PathParam("announcementId")
  long announcementId) {

    String currentUser = Utils.getCurrentUser();
    try {
      Announcement announcement = announcementService.deleteAnnouncement(announcementId, currentUser);
      return Response.ok(AnnouncementBuilder.fromAnnouncement(announcement)).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity("announcement not found").build();
    }
  }

}

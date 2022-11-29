package org.exoplatform.addons.gamification.rest;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
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

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.model.Announcement;
import org.exoplatform.addons.gamification.model.AnnouncementActivity;
import org.exoplatform.addons.gamification.rest.model.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.utils.EntityMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.api.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@Path("/gamification/announcement/api")
@Tag(name = "/gamification/announcement/api", description = "Manages announcement associated to users")
@RolesAllowed("users")
public class AnnouncementRest implements ResourceContainer {

  private static final Log    LOG = ExoLogger.getLogger(AnnouncementRest.class);

  private AnnouncementService announcementService;

  public AnnouncementRest(AnnouncementService announcementService) {
    this.announcementService = announcementService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("addAnnouncement")
  @Operation(
      summary = "Creates a new Announcement",
      method = "POST",
      description = "Creates a new Announcement"
  )
  @ApiResponses(
      value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
          @ApiResponse(responseCode = "400", description = "Invalid query input"),
          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
          @ApiResponse(responseCode = "500", description = "Internal server error"),
          @ApiResponse(responseCode = "403", description = "Forbidden operation"), }
  )
  public Response createAnnouncement(@RequestBody(description = "Announcement object to create", required = true)
                                             AnnouncementActivity announcementActivity) {
    if (announcementActivity == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("announcement object is mandatory").build();
    }
    String currentUser = Utils.getCurrentUser();
    if (StringUtils.isBlank(currentUser)) {
      LOG.warn("current User is null");
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      Announcement announcement = EntityMapper.fromAnnouncementActivity(announcementActivity);
      Announcement newAnnouncement = announcementService.createAnnouncement(announcement,
                                                                            announcementActivity.getTemplateParams(),
                                                                            currentUser,
                                                                            false);
      return Response.ok(EntityMapper.fromAnnouncement(newAnnouncement)).build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("ByChallengeId/{challengeId}")
  @Operation(
      summary = "Retrieves the list of challenges available for an owner",
      method = "GET",
      description = "Retrieves the list of challenges available for an owner"
  )
  @ApiResponses(
      value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
          @ApiResponse(responseCode = "500", description = "Internal server error"), }
  )
  public Response getAllAnnouncementByChallenge(
                                                @Context
                                                Request request,
                                                @Context
                                                UriInfo uriInfo,
                                                @Parameter(description = "id of the challenge", required = true)
                                                @PathParam("challengeId")
                                                String challengeId,
                                                @Parameter(description = "Offset of result")
                                                @DefaultValue("0")
                                                @QueryParam("offset")
                                                int offset,
                                                @Parameter(description = "Limit of result")
                                                @DefaultValue("10")
                                                @QueryParam("limit")
                                                int limit) {
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    EntityTag eTag = null;
    try {
      List<Announcement> announcements = announcementService.findAllAnnouncementByChallenge(Long.parseLong(challengeId),
                                                                                            offset,
                                                                                            limit);
      List<AnnouncementRestEntity> announcementsRestEntities = announcements.stream()
                                                                            .map(EntityMapper::fromAnnouncement)
                                                                            .collect(Collectors.toList());
      eTag = new EntityTag(String.valueOf(announcementsRestEntities.hashCode()));
      Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);

      if (builder == null) {
        builder = EntityBuilder.getResponseBuilder(announcementsRestEntities,
                                                   uriInfo,
                                                   RestUtils.getJsonMediaType(),
                                                   Response.Status.OK);
        builder.tag(eTag);
        Date date = new Date(System.currentTimeMillis());
        builder.lastModified(date);
        builder.expires(date);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        builder.cacheControl(cacheControl);
      }
      return builder.build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

}

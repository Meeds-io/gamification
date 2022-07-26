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
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.api.RestUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/gamification/announcement/api")
@Api(value = "/gamification/announcement/api", description = "Manages announcement associated to users") // NOSONAR
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
  @ApiOperation(
      value = "Creates a new Announcement",
      httpMethod = "POST",
      response = Response.class,
      consumes = "application/json"
  )
  @ApiResponses(
      value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
          @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
          @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"),
          @ApiResponse(code = HTTPStatus.FORBIDDEN, message = "Forbidden operation"), }
  )
  public Response createAnnouncement(@ApiParam(value = "Announcement object to create", required = true)
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
    } catch (Exception e) {
      LOG.warn("Error creating an announcement: { } ", announcementActivity, e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("ByChallengeId/{challengeId}")
  @ApiOperation(
      value = "Retrieves the list of challenges available for an owner",
      httpMethod = "GET",
      response = Response.class,
      produces = "application/json"
  )
  @ApiResponses(
      value = { @ApiResponse(code = HTTPStatus.OK, message = "Request fulfilled"),
          @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), }
  )
  public Response getAllAnnouncementByChallenge(
                                                @Context
                                                Request request,
                                                @Context
                                                UriInfo uriInfo,
                                                @ApiParam(value = "id of the challenge", required = true)
                                                @PathParam("challengeId")
                                                String challengeId,
                                                @ApiParam(value = "Offset of result", required = false)
                                                
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
    } catch (Exception e) {
      LOG.warn("Error retrieving list of announcements", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

}

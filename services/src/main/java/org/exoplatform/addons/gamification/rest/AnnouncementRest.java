package org.exoplatform.addons.gamification.rest;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementRestEntity;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.api.RestUtils;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/gamification/announcement/api")
@Api(value = "/gamification/announcement/api", description = "Manages announcement associated to users") // NOSONAR
@RolesAllowed("users")
public class AnnouncementRest implements ResourceContainer {

  private static final Log    LOG = ExoLogger.getLogger(AnnouncementRest.class);

  private final CacheControl cacheControl;


  private AnnouncementService announcementService;

  public AnnouncementRest(AnnouncementService announcementService) {
    this.announcementService = announcementService;
    this.cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("addAnnouncement")
  @ApiOperation(value = "Creates a new Announcement", httpMethod = "POST", response = Response.class, consumes = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response createAnnouncement(@ApiParam(value = "Announcement object to create", required = true)
  Announcement announcement) {
    if (announcement == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge object is mandatory").build();
    }
    String currentUser = Utils.getCurrentUser();
    if (StringUtils.isBlank(currentUser)) {
      LOG.warn("current User is null");
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      Announcement newAnnouncement = announcementService.createAnnouncement(announcement, currentUser, false);
      return Response.ok(EntityMapper.fromAnnouncement(newAnnouncement)).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to create an announcement", e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error creating an announcement", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("ByChallengeId/{challengeId}")
  @ApiOperation(value = "Retrieves the list of challenges available for an owner", httpMethod = "GET", response = Response.class, produces = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.OK, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response getAllAnnouncementByChallenge(@Context Request request,
                                                @Context UriInfo uriInfo,
                                                @ApiParam(value = "id of the challenge", required = true)
                                                @PathParam("challengeId")
                                                String challengeId,
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
    EntityTag eTag = null ;
    List<AnnouncementRestEntity> announcementsRestEntities = new ArrayList<>();
    eTag = new EntityTag(String.valueOf(announcementsRestEntities.hashCode()));
    try {
     List<Announcement> announcements = announcementService.findAllAnnouncementByChallenge(Long.parseLong(challengeId),
                                                                                            offset,
                                                                                            limit);
      announcementsRestEntities = EntityMapper.fromAnnouncementList(announcements);
      Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);

      if (builder == null) {
        builder = EntityBuilder.getResponseBuilder(announcementsRestEntities, uriInfo, RestUtils.getJsonMediaType(), Response.Status.OK);
        builder.tag(eTag);
        Date date = new Date(System.currentTimeMillis());
        builder.lastModified(date);
        builder.expires(date);
      }
      return builder.build();
    } catch (Exception e) {
      LOG.warn("Error retrieving list of announcements", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

}

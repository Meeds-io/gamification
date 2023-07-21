package io.meeds.gamification.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.rest.resource.ResourceContainer;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.AnnouncementActivity;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.utils.Utils;
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
      Announcement newAnnouncement = announcementService.createAnnouncement(announcementActivity,
                                                                            announcementActivity.getTemplateParams(),
                                                                            currentUser);
      return Response.ok(newAnnouncement).build();
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
      @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "404", description = "Object not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
  })
  public Response cancelAnnouncement(@Parameter(description = "Announcement technical identifier", required = true)
  @PathParam("announcementId")
  long announcementId) {

    String currentUser = Utils.getCurrentUser();
    try {
      announcementService.deleteAnnouncement(announcementId, currentUser);
      return Response.noContent().build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity("announcement not found").build();
    }
  }

}

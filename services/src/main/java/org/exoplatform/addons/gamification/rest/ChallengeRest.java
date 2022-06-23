package org.exoplatform.addons.gamification.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.*;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/gamification/challenges")
@Api(value = "/challenge/api", description = "Manages challenge associated to users") // NOSONAR
@RolesAllowed("users")
public class ChallengeRest implements ResourceContainer {

  private static final Log    LOG = ExoLogger.getLogger(ChallengeRest.class);

  private DomainService       domainService;

  private ChallengeService    challengeService;

  private AnnouncementService announcementService;

  public ChallengeRest(ChallengeService challengeService,
                       AnnouncementService announcementService,
                       DomainService domainService) {
    this.challengeService = challengeService;
    this.domainService = domainService;
    this.announcementService = announcementService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(value = "Creates a new challenge", httpMethod = "POST", response = Response.class, consumes = "application/json")
  @ApiResponses(
      value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
          @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
          @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), }
  )
  public Response createChallenge(
                                  @ApiParam(value = "Challenge object to create", required = true)
                                  Challenge challenge) {
    if (challenge == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge object is mandatory").build();
    }
    String currentUser = Utils.getCurrentUser();
    if (StringUtils.isBlank(currentUser)) {
      LOG.warn("current User is null");
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      Challenge newChallenge = challengeService.createChallenge(challenge, currentUser);
      return Response.ok(EntityBuilder.fromChallenge(newChallenge, Collections.emptyList())).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to create a challenge", e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error creating a challenge", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{challengeId}")
  @RolesAllowed("users")
  @ApiOperation(
      value = "Retrieves a challenge by its id",
      httpMethod = "GET",
      response = Response.class,
      produces = "application/json",
      notes = "returns selected challenge if exists"
  )
  @ApiResponses(
      value = { @ApiResponse(code = 200, message = "Request fulfilled"),
          @ApiResponse(code = 400, message = "Invalid query input"), @ApiResponse(code = 403, message = "Unauthorized operation"),
          @ApiResponse(code = 500, message = "Internal server error") }
  )
  public Response getChallengeById(
                                   @ApiParam(value = "Challenge technical id", required = true)
                                   @PathParam("challengeId")
                                   long challengeId,
                                   @ApiParam(value = "Offset of result", required = false)
                                   @DefaultValue("0")
                                   @QueryParam("offset")
                                   int offset,
                                   @ApiParam(value = "Limit of result", required = false)
                                   @DefaultValue("10")
                                   @QueryParam("limit")
                                   int limit) {
    if (challengeId == 0) {
      LOG.warn("Bad request sent to server with empty challengeId");
      return Response.status(400).build();
    }
    String currentUserId = Utils.getCurrentUser();
    try {
      Challenge challenge = challengeService.getChallengeById(challengeId, currentUserId);
      if (challenge == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      List<Announcement> announcementList = announcementService.findAllAnnouncementByChallenge(challengeId, offset, limit);
      return Response.ok(EntityBuilder.fromChallenge(challenge, announcementList)).build();
    } catch (Exception e) {
      LOG.error("Error getting challenge", e);
      return Response.status(500).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(
      value = "Updates an existing challenge",
      httpMethod = "PUT",
      response = Response.class,
      consumes = "application/json"
  )
  @ApiResponses(
      value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
          @ApiResponse(code = HTTPStatus.NOT_FOUND, message = "Object not found"),
          @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
          @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), }
  )
  public Response updateChallenge(
                                  @ApiParam(value = "challenge object to update", required = true)
                                  Challenge challenge) {
    if (challenge == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge object is mandatory").build();
    }
    if (challenge.getId() <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge technical identifier must be positive").build();
    }

    String currentUser = Utils.getCurrentUser();
    try {
      challenge = challengeService.updateChallenge(challenge, currentUser);
      return Response.ok(EntityBuilder.fromChallenge(challenge, Collections.emptyList())).build();
    } catch (ObjectNotFoundException e) {
      LOG.debug("User '{}' attempts to update a not existing challenge '{}'", currentUser, e);
      return Response.status(Response.Status.NOT_FOUND).entity("Challenge not found").build();
    } catch (IllegalAccessException e) {
      LOG.error("User '{}' attempts to update a challenge for owner '{}'", currentUser, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error updating a challenge", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
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
  public Response getAllChallengesByUser(
                                         @ApiParam(value = "Offset of result", required = false, defaultValue = "0")
                                         @QueryParam("offset")
                                         int offset,
                                         @ApiParam(value = "Limit of result", required = false, defaultValue = "10")
                                         @QueryParam("limit")
                                         int limit,
                                         @ApiParam(value = "Group challenges by domain", required = false, defaultValue = "false")
                                         @QueryParam("groupByDomain")
                                         boolean groupByDomain,
                                         @ApiParam(
                                             value = "Used to filter challenges by domain",
                                             required = false,
                                             defaultValue = ""
                                         )
                                         @QueryParam("domainId")
                                         long domainId,
                                         @ApiParam(
                                             value = "Number of announcements per challenge",
                                             required = false,
                                             defaultValue = "0"
                                         )
                                         @QueryParam("announcements")
                                         int announcementsPerChallenge,
                                         @ApiParam(
                                             value = "Number of announcements per challenge",
                                             required = false,
                                             defaultValue = "0"
                                         )
                                         @QueryParam("term")
                                         String term) {
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    RuleFilter filter = new RuleFilter();
    filter.setTerm(term);
    filter.setDomainId(domainId);
    String currentUser = Utils.getCurrentUser();

    try {
      LOG.info("start getting challenges");
      if (domainId > 0) {
        List<ChallengeRestEntity> challengeRestEntities = getUserChallengesByDomain(filter,
                                                                                    currentUser,
                                                                                    offset,
                                                                                    limit,
                                                                                    announcementsPerChallenge,
                                                                                    false);
        LOG.info("ended mapping challenges");
        return Response.ok(challengeRestEntities).build();
      } else if (groupByDomain) {
        List<DomainDTO> domains = domainService.getAllDomains();
        List<DomainWithChallengesRestEntity> domainsWithChallenges = domains.stream().map(domain -> {
          DomainWithChallengesRestEntity domainWithChallenge = new DomainWithChallengesRestEntity(domain);
          try {
            List<ChallengeRestEntity> challengeRestEntities = getUserChallengesByDomain(filter,
                                                                                        currentUser,
                                                                                        offset,
                                                                                        limit,
                                                                                        announcementsPerChallenge,
                                                                                        true);
            domainWithChallenge.setChallenges(challengeRestEntities);
            domainWithChallenge.setChallengesOffset(offset);
            domainWithChallenge.setChallengesLimit(limit);
            int size = challengeService.countChallengesByFilterAndUser(filter, currentUser);
            domainWithChallenge.setChallengesSize(size);
          } catch (IllegalAccessException | ObjectNotFoundException e) {
            LOG.debug("Error retrieving challenges of domain {} for user {}", domain.getTitle(), currentUser, e);
          }
          return domainWithChallenge;
        }).collect(Collectors.toList());
        return Response.ok(domainsWithChallenges).build();
      } else {
        List<Challenge> challenges = challengeService.getChallengesByFilterAndUser(filter, offset, limit, currentUser);
        List<ChallengeRestEntity> challengeRestEntities = new ArrayList<>();
        LOG.info("start mapping challenges");
        for (Challenge challenge : challenges) {
          challengeRestEntities.add(EntityBuilder.fromChallenge(announcementService,
                                                                challenge,
                                                                announcementsPerChallenge,
                                                                false));
        }
        LOG.info("ended mapping challenges");
        return Response.ok(challengeRestEntities).build();
      }
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to access not authorized challenges with owner Ids", currentUser, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error retrieving list of challenges", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("canAddChallenge")
  @Produces(MediaType.TEXT_PLAIN)
  @RolesAllowed("users")
  @ApiOperation(
      value = "check if the current user can add a challenge",
      httpMethod = "GET",
      response = Response.class,
      notes = "This checks if the current user user can add a challenge",
      consumes = "application/json"
  )
  @ApiResponses(
      value = { @ApiResponse(code = 200, message = "User ability to add a challenge is returned"),
          @ApiResponse(code = 401, message = "User not authorized to add a challenge") }
  )
  public Response canAddChallenge() {
    try {
      boolean canAddChallenge = challengeService.canAddChallenge(ConversationState.getCurrent().getIdentity());
      return Response.ok(String.valueOf(canAddChallenge)).build();
    } catch (Exception e) {
      LOG.error("Error when checking if the authenticated user can add a challenge", e);
      return Response.serverError().build();
    }
  }

  @DELETE
  @Path("/delete/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(
      value = "check if the current user can add a challenge",
      httpMethod = "GET",
      response = Response.class,
      notes = "This checks if the current user user can add a challenge",
      consumes = "application/json"
  )
  @ApiResponses(
      value = { @ApiResponse(code = 200, message = "challenge deleted"),
          @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "User not authorized to delete a challenge"),
          @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
          @ApiResponse(code = HTTPStatus.NOT_FOUND, message = "Object not found"),
          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), }
  )
  public Response deleteChallenge(
                                  @ApiParam(value = "challenge id to be deleted", required = true)
                                  @PathParam("id")
                                  Long challengeId) {
    if (challengeId == null || challengeId <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge technical identifier must be positive").build();
    }

    String currentUser = Utils.getCurrentUser();
    try {
      challengeService.deleteChallenge(challengeId, currentUser);
      return Response.ok().build();
    } catch (ObjectNotFoundException e) {
      LOG.error("challenge trying to delete not found", e);
      return Response.status(Response.Status.NOT_FOUND).entity("challenge trying to delete not found").build();
    } catch (IllegalAccessException e) {
      LOG.error("unauthorized user {} trying to delete a challenge", currentUser, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized user trying to delete a challenge").build();
    } catch (Exception e) {
      LOG.error("Error when deleting challenge", e);
      return Response.serverError().entity("Error when deleting challenge").build();
    }
  }

  private List<ChallengeRestEntity> getUserChallengesByDomain(RuleFilter filter,
                                                              String currentUser,
                                                              int offset,
                                                              int limit,
                                                              int announcementsPerChallenge,
                                                              boolean noDomain) throws IllegalAccessException,
                                                                                ObjectNotFoundException {
    List<Challenge> challenges = challengeService.getChallengesByFilterAndUser(filter, offset, limit, currentUser);
    List<ChallengeRestEntity> challengeRestEntities = new ArrayList<>();
    LOG.info("start mapping challenges");
    for (Challenge challenge : challenges) {
      challengeRestEntities.add(EntityBuilder.fromChallenge(announcementService, challenge, announcementsPerChallenge, noDomain));
    }
    return challengeRestEntities;
  }

}

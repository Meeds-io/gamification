package org.exoplatform.addons.gamification.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import org.exoplatform.addons.gamification.rest.model.ChallengeRestEntity;
import org.exoplatform.addons.gamification.rest.model.DomainWithChallengesRestEntity;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.DateFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@Path("/gamification/challenges")
@Tag(name = "/challenge/api", description = "Manages challenge associated to users")
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
  @Operation(summary = "Creates a new challenge", method = "POST", description = "Creates a new challenge")
  @ApiResponses(
      value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
          @ApiResponse(responseCode = "400", description = "Invalid query input"),
          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
          @ApiResponse(responseCode = "500", description = "Internal server error"), }
  )
  public Response createChallenge(
                                  @RequestBody(description = "Challenge object to create", required = true)
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
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{challengeId}")
  @RolesAllowed("users")
  @Operation(
      summary = "Retrieves a challenge by its id",
      method = "GET",
      description = "returns selected challenge if exists"
  )
  @ApiResponses(
      value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
          @ApiResponse(responseCode = "400", description = "Invalid query input"),
          @ApiResponse(responseCode = "403", description = "Unauthorized operation"),
          @ApiResponse(responseCode = "500", description = "Internal server error") }
  )
  public Response getChallengeById(
                                   @Parameter(description = "Challenge technical id", required = true)
                                   @PathParam("challengeId")
                                   long challengeId,
                                   @Parameter(description = "Offset of result")
                                   @DefaultValue("0")
                                   @QueryParam("offset")
                                   int offset,
                                   @Parameter(description = "Limit of result")
                                   @DefaultValue("10")
                                   @QueryParam("limit")
                                   int limit) {
    if (challengeId == 0) {
      LOG.warn("Bad request sent to server with empty challengeId");
      return Response.status(400).build();
    }
    String currentUser = Utils.getCurrentUser();
    try {
      Challenge challenge = challengeService.getChallengeById(challengeId, currentUser);
      if (challenge == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      List<Announcement> announcementList = announcementService.findAllAnnouncementByChallenge(challengeId, offset, limit);
      return Response.ok(EntityBuilder.fromChallenge(challenge, announcementList)).build();
    } catch (ObjectNotFoundException e) {
      LOG.debug("User '{}' attempts to retrieve a not existing challenge by id '{}'", currentUser, challengeId, e);
      return Response.status(Response.Status.NOT_FOUND).entity("Challenge not found").build();
    } catch (IllegalAccessException e) {
      LOG.error("User '{}' attempts to retrieve a challenge by id '{}'", currentUser, challengeId, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(
      summary = "Updates an existing challenge",
      method = "PUT",
      description = "Updates an existing challenge"
  )
  @ApiResponses(
      value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
          @ApiResponse(responseCode = "404", description = "Object not found"),
          @ApiResponse(responseCode = "400", description = "Invalid query input"),
          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
          @ApiResponse(responseCode = "500", description = "Internal server error"), }
  )
  public Response updateChallenge(
                                  @RequestBody(description = "challenge object to update", required = true)
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
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
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
  public Response getAllChallengesByUser(
                                         @Parameter(description = "Offset of result") @Schema(defaultValue = "0")
                                         @QueryParam("offset")
                                         int offset,
                                         @Parameter(description = "Limit of result") @Schema(defaultValue = "10")
                                         @QueryParam("limit")
                                         int limit,
                                         @Parameter(description = "Group challenges by domain") @Schema(defaultValue = "false")
                                         @QueryParam("groupByDomain")
                                         boolean groupByDomain,
                                         @Parameter(description = "Used to filter challenges by domain") @Schema(defaultValue = "")
                                         @QueryParam("domainId")
                                         long domainId,
                                         @Parameter(description = "Number of announcements per challenge") @Schema(defaultValue = "0")
                                         @QueryParam("announcements")
                                         int announcementsPerChallenge,
                                         @Parameter(
                                             description = "term to search challenges with"
                                         )
                                         @QueryParam("term")
                                         String term,
                                         @Parameter(description = "Challenge period filtering. Possible values: STARTED, NOT_STARTED, ENDED, ALL")
                                         @Schema(defaultValue = "ALL")
                                         @DefaultValue("ALL")
                                         @QueryParam("filter")
                                         String dateFilterType) {
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    String currentUser = Utils.getCurrentUser();
    RuleFilter filter = new RuleFilter();
    filter.setTerm(term);
    filter.setUsername(currentUser);
    filter.setDateFilterType(DateFilterType.valueOf(dateFilterType));
    try {
      LOG.info("start getting challenges");
      if (domainId > 0) {
        filter.setDomainId(domainId);
        List<ChallengeRestEntity> challengeRestEntities = getUserChallengesByDomain(filter,
                                                                                    currentUser,
                                                                                    offset,
                                                                                    limit,
                                                                                    announcementsPerChallenge,
                                                                                    false);
        LOG.info("ended mapping challenges");
        return Response.ok(challengeRestEntities).build();
      } else if (groupByDomain) {
        DomainFilter domainFilter = new DomainFilter(EntityFilterType.ALL, EntityStatusType.ENABLED, "", true, false);
        List<DomainDTO> domains = domainService.getDomainsByFilter(domainFilter, 0, -1);
        List<DomainWithChallengesRestEntity> domainsWithChallenges = new ArrayList<>();
        for (DomainDTO domain : domains) {
          DomainWithChallengesRestEntity domainWithChallenge = new DomainWithChallengesRestEntity(domain);
          filter.setDomainId(domain.getId());
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
          domainsWithChallenges.add(domainWithChallenge);
        }
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
    } catch (ObjectNotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to access not authorized challenges with owner Ids", currentUser, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("canAddChallenge")
  @Produces(MediaType.TEXT_PLAIN)
  @RolesAllowed("users")
  @Operation(
      summary = "check if the current user can add a challenge",
      method = "GET",
      description = "This checks if the current user user can add a challenge")
  @ApiResponses(
      value = { @ApiResponse(responseCode = "200", description = "User ability to add a challenge is returned"),
          @ApiResponse(responseCode = "401", description = "User not authorized to add a challenge") }
  )
  public Response canAddChallenge() {
    boolean canAddChallenge = challengeService.canAddChallenge(ConversationState.getCurrent().getIdentity());
    return Response.ok(String.valueOf(canAddChallenge)).build();
  }

  @DELETE
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(
      summary = "check if the current user can add a challenge",
      method = "GET",
      description = "This checks if the current user user can add a challenge")
  @ApiResponses(
      value = { @ApiResponse(responseCode = "200", description = "challenge deleted"),
          @ApiResponse(responseCode = "401", description = "User not authorized to delete a challenge"),
          @ApiResponse(responseCode = "400", description = "Invalid query input"),
          @ApiResponse(responseCode = "404", description = "Object not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error"), }
  )
  public Response deleteChallenge(
                                  @Parameter(description = "challenge id to be deleted", required = true)
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
      return Response.status(Response.Status.NOT_FOUND).entity("The challenge doesn't exist").build();
    } catch (IllegalAccessException e) {
      LOG.warn("User {} is not authorized to delete challenge with id {}", currentUser, challengeId, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized user trying to delete a challenge").build();
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

package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.service.effective.GamificationSearch;
import org.exoplatform.addons.gamification.service.effective.Piechart;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/gamification/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
public class LeaderboardEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

    private final CacheControl cacheControl;

    protected IdentityManager identityManager = null;

    protected GamificationService gamificationService = null;

    protected RelationshipManager relationshipManager;


    public LeaderboardEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        identityManager = CommonsUtils.getService(IdentityManager.class);

        gamificationService = CommonsUtils.getService(GamificationService.class);

        relationshipManager = CommonsUtils.getService(RelationshipManager.class);
    }

    @GET
    @RolesAllowed("users")
    @Path("rank/all")
    public Response getAllLeadersByRank(@Context UriInfo uriInfo) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            GamificationSearch gamificationSearch = new GamificationSearch();

            // Hold leaderboard flow
            List<LeaderboardInfo> leaderboardList = new ArrayList<>();

            LeaderboardInfo leaderboardInfo = null;

            try {
                // Filter users to add to leaderboard according to filter criteria
                // TODO : Use a DTO instead of JPA entity
                List<GamificationContextEntity> gamificationContextEntities = gamificationService.filter(gamificationSearch);

                if (gamificationContextEntities == null) {
                    return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }

                // Build Leaderboard flow only when the returned list is not null
                for (GamificationContextEntity game : gamificationContextEntities) {

                    leaderboardInfo = new LeaderboardInfo();

                    // Load Social identity
                    Identity identity = identityManager.getIdentity(game.getUsername(), true);

                    // Set score
                    leaderboardInfo.setScore(game.getScore());

                    // Set username
                    leaderboardInfo.setUsername(identity.getProfile().getFullName());

                    // Set avatar
                    leaderboardInfo.setUserAvatarUrl(identity.getProfile().getAvatarUrl());

                    // Leader
                    leaderboardList.add(leaderboardInfo);
                }

                return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error building leaderboard ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error building leaderboard")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }

    @GET
    @RolesAllowed("users")
    @Path("filter")
    public Response filter(@Context UriInfo uriInfo, @QueryParam("category") String category, @QueryParam("network") String network) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            GamificationSearch gamificationSearch = new GamificationSearch();

            if (StringUtils.isNotBlank(category)) gamificationSearch.setDomain(category);

            if (StringUtils.isNotBlank(network)) gamificationSearch.setNetwork(network);

            // hold leaderboard flow
            LeaderboardInfo leaderboardInfo = null;

            // Build leaderboard list
            List<LeaderboardInfo> leaderboardInfos = null;

            List<GamificationContextEntity> gamificationContextEntities = gamificationService.filter(gamificationSearch);


            try {

                if (gamificationContextEntities != null && !gamificationContextEntities.isEmpty()) {

                    leaderboardInfos = new ArrayList<LeaderboardInfo>();

                    int max = (10 > gamificationContextEntities.size()) ? gamificationContextEntities.size() : 10;

                    Identity identity = null;

                    for (int i = 0; i < max; i++) {

                        leaderboardInfo = new LeaderboardInfo();

                        // Load Social identity
                        identity = identityManager.getIdentity(gamificationContextEntities.get(i).getUsername(), true);

                        leaderboardInfo.setUserAvatarUrl(identity.getProfile().getAvatarUrl());
                        leaderboardInfo.setUsername(identity.getProfile().getFullName());
                        leaderboardInfo.setScore(gamificationContextEntities.get(i).getScore());

                        if (gamificationSearch.getNetwork().equalsIgnoreCase("my-connection")) {

                            if (isinMyConnections(identity, identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false))) {
                                leaderboardInfos.add(leaderboardInfo);
                            }


                        } else {

                            leaderboardInfos.add(leaderboardInfo);

                        }


                    }

                }

                return Response.ok(leaderboardInfos, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error filtering leaderbaord by Doamin : {} and by Network {} ",gamificationSearch.getDomain(),gamificationSearch.getNetwork(), e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error filtering leaderboard")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }

    @GET
    @RolesAllowed("users")
    @Path("stats")
    public Response stats(@Context UriInfo uriInfo, @QueryParam("username") String username) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {


            try {

                String userSocialId = null;
                if (username != null) {
                    userSocialId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username, false).getId();

                }
                //String usersocialId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false).getId();

                // Find user's stats
                List<Piechart> pieChart = gamificationService.findStatsByUserId(userSocialId);

                return Response.ok(pieChart, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error building statistics for user {} ",username, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error building statistics")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }

    private boolean isinMyConnections(Identity gameficationIdentity, Identity myIdentity) {

        Relationship gamificationRelationship = relationshipManager.get(myIdentity, gameficationIdentity);

        if (gamificationRelationship == null) return false;

        return gamificationRelationship.getStatus().name().equalsIgnoreCase(Relationship.Type.CONFIRMED.name());
    }

    public static class LeaderboardInfo {

        String userAvatarUrl;
        String username;
        long score;

        public String getUserAvatarUrl() {
            return userAvatarUrl;
        }

        public void setUserAvatarUrl(String userAvatarUrl) {
            this.userAvatarUrl = userAvatarUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getScore() {
            return score;
        }

        public void setScore(long score) {
            this.score = score;
        }
    }

}

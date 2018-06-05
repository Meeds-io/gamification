package org.exoplatform.gamification.rest;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.gamification.service.effective.GamificationSearch;
import org.exoplatform.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.profile.ProfileFilter;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Path("/gamification/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
public class LeaderboardEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

    private final CacheControl cacheControl;

    protected IdentityManager identityManager = null;

    protected GamificationService gamificationService = null;


    public LeaderboardEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        identityManager = CommonsUtils.getService(IdentityManager.class);

        gamificationService = CommonsUtils.getService(GamificationService.class);
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

                // Build Leaderboard flow
                for (GamificationContextEntity game : gamificationContextEntities) {

                    leaderboardInfo = new LeaderboardInfo();

                    // Load Social identity
                    Identity identity = identityManager.getIdentity(game.getUsername(), true);

                    // Set score
                    leaderboardInfo.setScore(String.valueOf(game.getScore()));

                    // Set username
                    leaderboardInfo.setUsername(identity.getProfile().getFullName());

                    // Set avatar
                    leaderboardInfo.setUserAvatarUrl(identity.getProfile().getAvatarUrl());

                    // Leader
                    leaderboardList.add(leaderboardInfo);
                }

                return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error listing all badges ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all badges")
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
    @Path("search")
    public Response search(@Context UriInfo uriInfo, @QueryParam("category") String category, @QueryParam("type") String type) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            GamificationSearch gamificationSearch = new GamificationSearch();

            // hold leaderboard flow
            LeaderboardInfo leaderboardInfo = new LeaderboardInfo();

            // compute user's social ID
            //String usersocialId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false).getId();

            switch (type) {
                case "rank":
                    //Load Effective Gamification by rank
                    gamificationService.filter(gamificationSearch);
                    break;
                case "badge":
                    // Load Effective Gmaification by badge
                    break;

                default:
                    //Do something
                    break;
            }


            try {

                /** This is a fake endpoint */
                ListAccess<Identity> list = identityManager.getIdentitiesByProfileFilter(OrganizationIdentityProvider.NAME, new ProfileFilter(), true);

                Identity[] identities = list.load(0, 20);
                List<LeaderboardInfo> leaders = new ArrayList<LeaderboardInfo>();
                for (Identity identity : identities) {
                    leaderboardInfo = new LeaderboardInfo();

                    leaderboardInfo.setUserAvatarUrl(identity.getProfile().getAvatarUrl());
                    leaderboardInfo.setUsername(identity.getProfile().getFullName());
                    leaderboardInfo.setScore(String.valueOf(new Random().nextInt(90) + 10));

                    leaders.add(leaderboardInfo);
                }

                return Response.ok(leaders, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error listing all badges ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all badges")
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
    public Response filter(@Context UriInfo uriInfo, @QueryParam("filter") String filter) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            LeaderboardInfo leaderboardInfo = new LeaderboardInfo();


            try {

                /** This is a fake endpoint */
                ListAccess<Identity> list = identityManager.getIdentitiesByProfileFilter(OrganizationIdentityProvider.NAME, new ProfileFilter(), true);

                Identity[] identities = list.load(0, 20);
                List<LeaderboardInfo> leaders = new ArrayList<LeaderboardInfo>();
                for (Identity identity : identities) {
                    leaderboardInfo = new LeaderboardInfo();

                    leaderboardInfo.setUserAvatarUrl(identity.getProfile().getAvatarUrl());
                    leaderboardInfo.setUsername(identity.getProfile().getFullName());
                    leaderboardInfo.setScore(String.valueOf(new Random().nextInt(90) + 10));

                    leaders.add(leaderboardInfo);
                }

                return Response.ok(leaders, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error listing all badges ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all badges")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }

    public static class LeaderboardInfo {

        String userAvatarUrl;
        String username;
        String score;

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

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }
}

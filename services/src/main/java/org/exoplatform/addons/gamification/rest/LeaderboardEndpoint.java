package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.service.effective.GamificationSearch;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.effective.Piechart;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
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
@RolesAllowed("users")
public class LeaderboardEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

    private final static String BLACK_LISTED_USERS_GROUP = "/leaderboard-blacklist-users";

    private final static String YOUR_CURRENT_RANK_MSG = "Your current rank";

    private final CacheControl cacheControl;

    protected IdentityManager identityManager = null;

    protected GamificationService gamificationService = null;

    protected RelationshipManager relationshipManager;

    private List<String> blackListedUsers;

    private OrganizationService organizationService;


    public LeaderboardEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        identityManager = CommonsUtils.getService(IdentityManager.class);

        gamificationService = CommonsUtils.getService(GamificationService.class);

        relationshipManager = CommonsUtils.getService(RelationshipManager.class);

        organizationService = CommonsUtils.getService(OrganizationService.class);


    }

    @GET
    @Path("rank/all")
    public Response getAllLeadersByRank(@Context UriInfo uriInfo) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            GamificationSearch gamificationSearch = new GamificationSearch();

            // Hold leaderboard flow
            List<LeaderboardInfo> leaderboardList = new ArrayList<>();

            LeaderboardInfo leaderboardInfo = null;

            Identity identity = null;

            try {
                // Filter users to add to leaderboard according to filter criteria
                // TODO : Use a DTO instead of JPA entity
                List<GamificationContextEntity> gamificationContextEntities = gamificationService.filter(gamificationSearch);

                if (gamificationContextEntities == null) {
                    return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }

                // Build Leaderboard flow only when the returned list is not null
                for (GamificationContextEntity game : gamificationContextEntities) {

                    // Compute blackList
                    populateBlackListedUsers();

                    // Load Social identity
                    identity = identityManager.getIdentity(game.getUsername(), true);

                    // Do not add blacklisted user to the leaderboard
                    if (isBlackListedUser(identity.getRemoteId())) continue;


                    leaderboardInfo = new LeaderboardInfo();

                    // Set score
                    leaderboardInfo.setScore(game.getScore());

                    // Set username
                    leaderboardInfo.setRemoteId(identity.getRemoteId());

                    // Set FullName
                    leaderboardInfo.setFullname(identity.getProfile().getFullName());

                    // Set avatar
                    leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());

                    // Set profile URL
                    leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());

                    // Leader
                    leaderboardList.add(leaderboardInfo);
                }

                // Get eXo Identity of current user
                identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false);
                // Check if the current user is already in top10
                if (!isCurrentUserInTopTen(identity.getId(),gamificationContextEntities)) {

                    // Get GaamificationScore for current user
                    int rank = gamificationService.loadGamification(identity.getId());

                    if (rank  > 0) {


                        leaderboardInfo = new LeaderboardInfo();

                        // Set score
                        leaderboardInfo.setScore(rank);

                        // Set username
                        leaderboardInfo.setRemoteId(YOUR_CURRENT_RANK_MSG);

                        // Set FullName
                        leaderboardInfo.setFullname(YOUR_CURRENT_RANK_MSG);

                        // Set avatar
                        leaderboardInfo.setAvatarUrl(YOUR_CURRENT_RANK_MSG);

                        // Set profile URL
                        leaderboardInfo.setProfileUrl(YOUR_CURRENT_RANK_MSG);

                        // Leader
                        leaderboardList.add(leaderboardInfo);

                    }

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
    @Path("filter")
    public Response filter(@Context UriInfo uriInfo, @QueryParam("category") String category, @QueryParam("network") String network) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            GamificationSearch gamificationSearch = new GamificationSearch();

            if (StringUtils.isNotBlank(category) && !category.equalsIgnoreCase("null")) gamificationSearch.setDomain(category);

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

                    // Get current User identity

                    Identity currentIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false);

                    // Compute blackList
                    populateBlackListedUsers();

                    for (int i = 0; i < max; i++) {



                        // Load Social identity
                        identity = identityManager.getIdentity(gamificationContextEntities.get(i).getUsername(), true);

                        // Do not add blacklisted user to the leaderboard
                        if (isBlackListedUser(identity.getRemoteId())) continue;

                        leaderboardInfo = new LeaderboardInfo();

                        leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
                        leaderboardInfo.setFullname(identity.getProfile().getFullName());
                        leaderboardInfo.setRemoteId(identity.getRemoteId());
                        leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());

                        leaderboardInfo.setScore(gamificationContextEntities.get(i).getScore());

                        if (gamificationSearch.getNetwork().equalsIgnoreCase("my-connection")) {

                            if ((identity.getId().equalsIgnoreCase(currentIdentity.getId()))|| (isInMyConnections(identity, currentIdentity))){
                                leaderboardInfos.add(leaderboardInfo);
                            }

                        } else {
                            leaderboardInfos.add(leaderboardInfo);

                        }

                    }

                }

                return Response.ok(leaderboardInfos, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error filtering leaderbaord by Doamin : {} and by Network {} ", gamificationSearch.getDomain(), gamificationSearch.getNetwork(), e);

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

                LOG.error("Error building statistics for user {} ", username, e);

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

    private boolean isInMyConnections(Identity gameficationIdentity, Identity myIdentity) {

        Relationship gamificationRelationship = relationshipManager.get(myIdentity, gameficationIdentity);

        if (gamificationRelationship == null) return false;

        return gamificationRelationship.getStatus().name().equalsIgnoreCase(Relationship.Type.CONFIRMED.name());
    }

    /**
     * Compute the list of user whithin the black list (user we shouldn't display them on leaderboard screen)
     */
    private void populateBlackListedUsers() {
        try {
            blackListedUsers = new ArrayList<String>();
            ListAccess<User> usersBlackList = organizationService.getUserHandler().findUsersByGroupId(BLACK_LISTED_USERS_GROUP);
            if(null!= usersBlackList && usersBlackList.getSize()>0){
                User[] managerUser = usersBlackList.load(0, usersBlackList.getSize());
                for (User user : managerUser) {
                    try {
                        blackListedUsers.add(user.getUserName());
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Exceptions in initListUserToDisconnect",e);
        }

    }

    private boolean isBlackListedUser(String username) {

        // Return true when there is no user in blacklist
        if (blackListedUsers == null) return false;

        return blackListedUsers.contains(username);

    }

    private boolean isCurrentUserInTopTen(String username, List<GamificationContextEntity> leaderboard) {

       return leaderboard.stream().map(GamificationContextEntity::getUsername).anyMatch(username::equals);
    }

    public static class LeaderboardInfo {

        String avatarUrl;
        String remoteId;
        String fullname;
        long score;
        String profileUrl;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getRemoteId() {
            return remoteId;
        }

        public void setRemoteId(String remoteId) {
            this.remoteId = remoteId;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public long getScore() {
            return score;
        }

        public void setScore(long score) {
            this.score = score;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }
    }

}

package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;

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

    private final static String YOUR_CURRENT_RANK_MSG = "Your current rank";

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
    @Path("rank/all")
    public Response getAllLeadersByRank(@Context UriInfo uriInfo) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            LeaderboardFilter leaderboardFilter = new LeaderboardFilter();

            // Hold leaderboard flow
            List<LeaderboardInfo> leaderboardList = new ArrayList<>();

            LeaderboardInfo leaderboardInfo = null;

            Identity identity = null;

            try {
                // Filter users to add to leaderboard according to filter criteria
                List<StandardLeaderboard> standardLeaderboards = gamificationService.filter(leaderboardFilter, true);

                if (standardLeaderboards == null) {
                    return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }

                // Build StandardLeaderboard flow only when the returned list is not null
                for (StandardLeaderboard element : standardLeaderboards) {

                    // Load Social identity
                    identity = identityManager.getIdentity(element.getUserSocialId(), true);

                    leaderboardInfo = new LeaderboardInfo();

                    // Set SocialId

                    leaderboardInfo.setSocialId(identity.getId());

                    // Set score
                    leaderboardInfo.setScore(element.getReputationScore());

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
                // Check if the current user is already in top10
                LeaderboardInfo leader = buildCurrentUserRank(conversationState.getIdentity().getUserId(), leaderboardList);
                // Complete the final leaderboard
                if (leader != null) leaderboardList.add(leader);

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
    public Response filter(@Context UriInfo uriInfo, @QueryParam("domain") String domain, @QueryParam("period") String period, @QueryParam("capacity") String capacity) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            LeaderboardFilter leaderboardFilter = new LeaderboardFilter();

            if (StringUtils.isNotBlank(domain) && !domain.equalsIgnoreCase("null"))
                leaderboardFilter.setDomain(domain);

            if (StringUtils.isNotBlank(period)) leaderboardFilter.setPeriod(period);

            if (StringUtils.isNotBlank(capacity)) leaderboardFilter.setLoadCapacity(capacity);

            // hold leaderboard flow
            LeaderboardInfo leaderboardInfo = null;

            // Build leaderboard list
            List<LeaderboardInfo> leaderboardInfoList = null;

            try {

                List<StandardLeaderboard> standardLeaderboards = gamificationService.filter(leaderboardFilter,true);

                if (standardLeaderboards == null) {
                    return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }


                leaderboardInfoList = new ArrayList<LeaderboardInfo>();

                Identity identity = null;

                // Get current User identity

                Identity currentIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false);

                for (StandardLeaderboard leader : standardLeaderboards) {


                    // Load Social identity
                    identity = identityManager.getIdentity(leader.getUserSocialId(), true);


                    leaderboardInfo = new LeaderboardInfo();

                    // Set SocialId

                    leaderboardInfo.setSocialId(identity.getId());

                    // Set score
                    leaderboardInfo.setScore(leader.getReputationScore());

                    // Set username
                    leaderboardInfo.setRemoteId(identity.getRemoteId());

                    // Set FullName
                    leaderboardInfo.setFullname(identity.getProfile().getFullName());

                    // Set avatar
                    leaderboardInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());

                    // Set profile URL
                    leaderboardInfo.setProfileUrl(identity.getProfile().getUrl());

                    leaderboardInfoList.add(leaderboardInfo);
                }
                // Check if the current user is already in top10
                LeaderboardInfo leader = buildCurrentUserRank(conversationState.getIdentity().getUserId(), leaderboardInfoList);
                // Complete the final leaderboard
                if (leader != null) leaderboardInfoList.add(leader);

                return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error filtering leaderbaord by Doamin : {} and by Period {} ", leaderboardFilter.getDomain(), leaderboardFilter.getPeriod(), e);

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
    public Response stats(@Context UriInfo uriInfo, @QueryParam("username") String userSocialId) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {


            try {

                if (userSocialId != null) {
                    userSocialId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userSocialId, false).getId();

                }
                //String usersocialId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false).getId();

                // Find user's stats
                List<PiechartLeaderboard> userStats = gamificationService.buildStatsByUser(userSocialId);

                return Response.ok(userStats, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error building statistics for user {} ", userSocialId, e);

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

    private boolean isCurrentUserInTopTen(String username, List<LeaderboardInfo> leaderboard) {

        return leaderboard.stream().map(LeaderboardInfo::getSocialId).anyMatch(username::equals);
    }


    private LeaderboardInfo buildCurrentUserRank (String identityId, List<LeaderboardInfo> leaderboardList ) {
        LeaderboardInfo leaderboardInfo = null;
        String currentUser = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,identityId, false).getId();
        if (!isCurrentUserInTopTen(currentUser, leaderboardList)) {

            // Get GaamificationScore for current user
            int rank = gamificationService.bluidCurrentUserRank(currentUser);

            if (rank > 0) {

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
            }
        }
        return leaderboardInfo;

    }

    public static class LeaderboardInfo {

        String socialId;
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

        public String getSocialId() {
            return socialId;
        }

        public void setSocialId(String socialId) {
            this.socialId = socialId;
        }
    }

}

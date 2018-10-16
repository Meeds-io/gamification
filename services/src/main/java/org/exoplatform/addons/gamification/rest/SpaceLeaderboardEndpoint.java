package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.GamificationUtils;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/gamification/space/leaderboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class SpaceLeaderboardEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

    private final CacheControl cacheControl;

    protected GamificationService gamificationService = null;

    protected IdentityManager identityManager = null;

    protected SpaceService spaceService = null;

    public SpaceLeaderboardEndpoint(GamificationService gamificationService,
                                    IdentityManager identityManager,
                                    SpaceService spaceService) {

        this.cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        this.gamificationService = gamificationService;
        this.identityManager = identityManager;
        this.spaceService = spaceService;
    }

    @GET
    @Path("overall")
    public Response getAllLeadersByRank(@Context UriInfo uriInfo, @QueryParam("url") String url) {
        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            LeaderboardFilter leaderboardFilter = new LeaderboardFilter();

            // Hold leaderboard flow
            List<LeaderboardEndpoint.LeaderboardInfo> leaderboardList = new ArrayList<>();

            LeaderboardEndpoint.LeaderboardInfo leaderboardInfo = null;

            Identity identity = null;

            try {
                // Find current space
                Space space = GamificationUtils.extractSpaceNameFromUrl(url);
                // Filter users to add to leaderboard according to filter criteria
                List<StandardLeaderboard> standardLeaderboards = gamificationService.filter(leaderboardFilter, false);

                if (standardLeaderboards == null) {
                    return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }

                // Build StandardLeaderboard flow only when the returned list is not null
                for (StandardLeaderboard element : standardLeaderboards) {

                    // Load Social identity
                    identity = identityManager.getIdentity(element.getUserSocialId(), true);

                    if (spaceService.isMember(space, identity.getRemoteId())) {
                        leaderboardInfo = new LeaderboardEndpoint.LeaderboardInfo();

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
                    if (leaderboardList.size() == 10) return Response.ok(leaderboardList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

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
    public Response filter(@Context UriInfo uriInfo, @QueryParam("domain") String domain, @QueryParam("period") String period, @QueryParam("url") String url ) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            //Init search criteria
            LeaderboardFilter leaderboardFilter = new LeaderboardFilter();

            if (StringUtils.isNotBlank(domain) && !domain.equalsIgnoreCase("null"))
                leaderboardFilter.setDomain(domain);

            if (StringUtils.isNotBlank(period)) leaderboardFilter.setPeriod(period);

            // hold leaderboard flow
            LeaderboardEndpoint.LeaderboardInfo leaderboardInfo = null;

            // Build leaderboard list
            List<LeaderboardEndpoint.LeaderboardInfo> leaderboardInfoList = null;

            try {
                // Find current space
                Space space = GamificationUtils.extractSpaceNameFromUrl(url);

                List<StandardLeaderboard> standardLeaderboards = gamificationService.filter(leaderboardFilter,false);

                if (standardLeaderboards == null) {
                    return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }


                leaderboardInfoList = new ArrayList<LeaderboardEndpoint.LeaderboardInfo>();

                Identity identity = null;

                // Get current User identity

                Identity currentIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, conversationState.getIdentity().getUserId(), false);

                for (StandardLeaderboard leader : standardLeaderboards) {


                    // Load Social identity
                    identity = identityManager.getIdentity(leader.getUserSocialId(), true);

                    if (spaceService.isMember(space, identity.getRemoteId())) {
                        leaderboardInfo = new LeaderboardEndpoint.LeaderboardInfo();

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
                    if (leaderboardInfoList.size() == 10) return Response.ok(leaderboardInfoList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }

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
}

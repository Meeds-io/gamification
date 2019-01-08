package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.effective.*;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/gamification/gameficationinformationsboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class GamificationInformationsEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageBadgesEndpoint.class);

    private final CacheControl cacheControl;

    protected IdentityManager identityManager = null;

    protected GamificationService gamificationService = null;

    protected RelationshipManager relationshipManager;

    public GamificationInformationsEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        identityManager = CommonsUtils.getService(IdentityManager.class);

        gamificationService = CommonsUtils.getService(GamificationService.class);

        relationshipManager = CommonsUtils.getService(RelationshipManager.class);

    }

    @GET
    @Path("history/all")
    public Response getAllLeadersByRank(@Context UriInfo uriInfo, @QueryParam("capacity") String capacity) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {



            List<GamificationHistoryInfo> historyList = new ArrayList<>();


            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,conversationState.getIdentity().getUserId(), false);


            try {

                int loadCapacity=10;
                if (StringUtils.isNotBlank(capacity)){
                    loadCapacity=Integer.parseInt(capacity);;
                }
                // Filter users to add to leaderboard according to filter criteria
                List<GamificationActionsHistory> ss = gamificationService.findActionsHistoryByUserId(identity.getId(),true,loadCapacity);

                if (ss == null) {
                    return Response.ok(new ArrayList<>(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
                }

                // Build StandardLeaderboard flow only when the returned list is not null
                for (GamificationActionsHistory element : ss) {

                    // Load Social identity
                    identity = identityManager.getIdentity(element.getUserSocialId(), true);

                    Profile profile= identity.getProfile();

                    GamificationHistoryInfo gamificationHistoryInfo = new GamificationHistoryInfo();

                    // Set SocialId

                    gamificationHistoryInfo.setSocialId(identity.getId());

                    // Set score
               //     earnbadgesInfo.setScore(element.getReputationScore());

                    // Set username
                    gamificationHistoryInfo.setRemoteId(identity.getRemoteId());

                    // Set FullName
                    gamificationHistoryInfo.setFullname(profile.getFullName());

                    // Set avatar
                    gamificationHistoryInfo.setAvatarUrl(profile.getAvatarUrl());

                    // Set profile URL
                    gamificationHistoryInfo.setProfileUrl(profile.getUrl());

                    gamificationHistoryInfo.setActionScore(element.getActionScore());
                    gamificationHistoryInfo.setActionTitle(element.getActionTitle());
                    gamificationHistoryInfo.setContext(element.getContext());
                    gamificationHistoryInfo.setDate(element.getDate().toGMTString());
                    gamificationHistoryInfo.setDomain(element.getDomain());
                    gamificationHistoryInfo.setGlobalScore(element.getGlobalScore());
                    // log
                    historyList.add(gamificationHistoryInfo);
                }

                return Response.ok(historyList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error building My points history ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error building My points history")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }





    public static class GamificationHistoryInfo {

        String socialId;
        String avatarUrl;
        String remoteId;
        String fullname;
        String profileUrl;
        String date;
        long globalScore;
        String actionTitle;
        String domain;
        String context;
        long actionScore;
        private int loadCapacity = 10;



        public int getLoadCapacity() {
            return loadCapacity;
        }


        public void setLoadCapacity(String loadCapacity) {
            this.loadCapacity = Integer.parseInt(loadCapacity);
        }

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
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }


        public long getGlobalScore() {
            return globalScore;
        }

        public void setGlobalScore(long globalScore) {
            this.globalScore = globalScore;
        }

        public String getActionTitle() {
            return actionTitle;
        }

        public void setActionTitle(String actionTitle) {
            this.actionTitle = actionTitle;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public long getActionScore() {
            return actionScore;
        }

        public void setActionScore(long actionScore) {
            this.actionScore = actionScore;
        }
    }

}

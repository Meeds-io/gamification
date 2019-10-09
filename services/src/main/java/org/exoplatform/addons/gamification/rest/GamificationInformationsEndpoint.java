package org.exoplatform.addons.gamification.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.GamificationUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
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

@Path("/gamification/gameficationinformationsboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class GamificationInformationsEndpoint implements ResourceContainer {

  private static final Log      LOG                 = ExoLogger.getLogger(ManageBadgesEndpoint.class);

  private final CacheControl    cacheControl;

  protected IdentityManager     identityManager     = null;

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
  public Response getAllLeadersByRank(@Context UriInfo uriInfo,
                                      @QueryParam("capacity") String capacity,
                                      @QueryParam("url") String url) {

    ConversationState conversationState = ConversationState.getCurrent();
    // Get profile owner from url
    String profileOwner = GamificationUtils.extractProfileOwnerFromUrl(url, "/");
    if (conversationState != null) {
      boolean isOwner = false;
      List<GamificationHistoryInfo> historyList = new ArrayList<>();
      Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                                                              conversationState.getIdentity().getUserId(),
                                                              false);
      Identity actorIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profileOwner, false);
      String actorId = actorIdentity != null ? actorIdentity.getId() : identity.getId();
      if (actorId.equals(identity.getId()) || conversationState.getIdentity().isMemberOf("/platform/administrators")) {
        isOwner = true;
      }
      try {

        int loadCapacity = 10;
        if (StringUtils.isNotBlank(capacity)) {
          loadCapacity = Integer.parseInt(capacity);
        }

        // find actions History by userid adding a pagination load more capacity filter
        // List<GamificationActionsHistory> ss =
        // gamificationService.findActionsHistoryByUserId(identity.getId(),true,loadCapacity);
        List<GamificationActionsHistory> ss = gamificationService.findActionsHistoryByReceiverId(actorId, true, loadCapacity);
        if (ss == null) {
          return Response.ok(new ArrayList<>(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        }
        // Build GamificationActionsHistory flow only when the returned list is not null
        for (GamificationActionsHistory element : ss) {
          // Load Social identity
          // identity = identityManager.getIdentity(element.getUserSocialId(), true);
          identity = identityManager.getIdentity(element.getUserSocialId(), true);
          Profile profile = identity.getProfile();

          GamificationHistoryInfo gamificationHistoryInfo = new GamificationHistoryInfo();
          // Set SocialId
          gamificationHistoryInfo.setSocialId(identity.getId());

          gamificationHistoryInfo.setReceiver(element.getReceiver());
          // Set username
          gamificationHistoryInfo.setRemoteId(identity.getRemoteId());
          // Set FullName
          gamificationHistoryInfo.setFullname(profile.getFullName());
          // Set avatar
          gamificationHistoryInfo.setAvatarUrl(profile.getAvatarUrl());
          // Set profile URL
          gamificationHistoryInfo.setProfileUrl(profile.getUrl());
          // Set Final Score
          gamificationHistoryInfo.setActionScore(element.getActionScore());
          // Set Action Title
          gamificationHistoryInfo.setActionTitle(element.getActionTitle());
          gamificationHistoryInfo.setContext(element.getContext());
          // Set Date-Hours-Minutes GMT Format of the creation
          gamificationHistoryInfo.setCreatedDate(element.getCreatedDate().toGMTString());
          // Set Domain
          gamificationHistoryInfo.setDomain(element.getDomain());
          // Set Global Score
          gamificationHistoryInfo.setGlobalScore(element.getGlobalScore());
          // Set event id
          if (isOwner) {
            gamificationHistoryInfo.setObjectId(element.getObjectId());
          }

          // log
          historyList.add(gamificationHistoryInfo);
        }

        return Response.ok(historyList, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
      } catch (Exception e) {
        LOG.error("Error building My points history ", e);
        return Response.serverError().cacheControl(cacheControl).entity("Error building My points history").build();
      }
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }
  }

  public static class GamificationHistoryInfo {
    String         socialId;

    String         avatarUrl;

    String         remoteId;

    String         fullname;

    String         profileUrl;

    String         createdDate;

    long           globalScore;

    String         actionTitle;

    String         domain;

    String         context;

    long           actionScore;

    private int    loadCapacity = 10;

    private String receiver;

    private String objectId;

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

    public String getCreatedDate() {
      return createdDate;
    }

    public void setCreatedDate(String createdDate) {
      this.createdDate = createdDate;
    }

    public String getReceiver() {
      return receiver;
    }

    public void setReceiver(String receiver) {
      this.receiver = receiver;
    }

    public String getObjectId() {
      return objectId;
    }

    public void setObjectId(String objectId) {
      this.objectId = objectId;
    }
  }
}

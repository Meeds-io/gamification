package org.exoplatform.addons.gamification.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

@Path("/gamification/gameficationinformationsboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class GamificationInformationsEndpoint implements ResourceContainer {

  private static final Log      LOG                 = ExoLogger.getLogger(GamificationInformationsEndpoint.class);

  protected IdentityManager     identityManager     = null;

  protected GamificationService gamificationService = null;

  protected SpaceService spaceService;

  public GamificationInformationsEndpoint(IdentityManager identityManager,
                                          SpaceService spaceService,
                                          GamificationService gamificationService) {
    this.identityManager = identityManager;
    this.gamificationService = gamificationService;
    this.spaceService = spaceService;
  }

  @GET
  @Path("history/all")
  @RolesAllowed("users")
  public Response getAllLeadersByRank(@Context UriInfo uriInfo,
                                      @QueryParam("capacity") String capacity,
                                      @QueryParam("providerId") String providerId,
                                      @QueryParam("remoteId") String remoteId) {

    if (StringUtils.isBlank(providerId)) {
      return Response.status(400).entity("identity 'providerId' parameter is mandatory").build();
    }

    if (StringUtils.isBlank(remoteId)) {
      return Response.status(400).entity("identity 'remoteId' parameter is mandatory").build();
    }

    boolean isManager = isCurrentUserSuperManager();
    boolean canShowDetails = isManager || isCurrentUser(providerId, remoteId);

    if (SpaceIdentityProvider.NAME.equals(providerId)) {
      Space space = spaceService.getSpaceByPrettyName(remoteId);
      if (space == null) {
        return Response.status(404).entity("Space with pretty name '" + remoteId + "'").build();
      }

      String currentUsername = getCurrentUsername();
      boolean isSpaceMember = spaceService.isMember(space, currentUsername) || spaceService.isSuperManager(currentUsername);
      if (!isSpaceMember) {
        return Response.status(403).build();
      }
    }

    List<GamificationHistoryInfo> historyList = new ArrayList<>();
    Identity identity = identityManager.getOrCreateIdentity(providerId, remoteId);
    try {
      int loadCapacity = 10;
      if (StringUtils.isNotBlank(capacity)) {
        loadCapacity = Integer.parseInt(capacity);
      }

      // find actions History by userid adding a pagination load more capacity filter
      List<GamificationActionsHistory> ss = gamificationService.findActionsHistoryByEarnerId(identity.getId(), loadCapacity);
      if (ss == null || ss.isEmpty()) {
        return Response.ok(historyList, MediaType.APPLICATION_JSON).build();
      }

      // Build GamificationActionsHistory flow only when the returned list is not null
      for (GamificationActionsHistory element : ss) {
        // Load Social identity
        identity = identityManager.getIdentity(element.getEarnerId(), true);
        Profile profile = identity.getProfile();
        GamificationHistoryInfo gamificationHistoryInfo = new GamificationHistoryInfo();
        // Set SocialIds
        gamificationHistoryInfo.setSocialId(identity.getId());
        gamificationHistoryInfo.setSpace(StringUtils.equals(identity.getProviderId(), SpaceIdentityProvider.NAME));
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
        if (canShowDetails) {
          gamificationHistoryInfo.setObjectId(element.getObjectId());
        }

        // log
        historyList.add(gamificationHistoryInfo);
      }

      return Response.ok(historyList, MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
      LOG.error("Error building My points history ", e);
      return Response.serverError().entity("Error building My points history").build();
    }
  }

  private boolean isCurrentUser(String providerId, String remoteId) {
    return OrganizationIdentityProvider.NAME.equals(providerId) && StringUtils.equals(getCurrentUsername(), remoteId);
  }

  private String getCurrentUsername() {
    return ConversationState.getCurrent().getIdentity().getUserId();
  }

  private boolean isCurrentUserSuperManager() {
    return ConversationState.getCurrent().getIdentity().isMemberOf("/platform/administrators");
  }

  public static class GamificationHistoryInfo {
    String         socialId;

    boolean        isSpace;

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

    public boolean isSpace() {
      return isSpace;
    }

    public void setSpace(boolean isSpace) {
      this.isSpace = isSpace;
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

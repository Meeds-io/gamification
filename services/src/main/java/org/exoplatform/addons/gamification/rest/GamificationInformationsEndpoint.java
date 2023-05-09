/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.rest.model.GamificationInformationRestEntity;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

@Path("/gamification/gameficationinformationsboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class GamificationInformationsEndpoint implements ResourceContainer {

  protected IdentityManager     identityManager     = null;

  protected GamificationService gamificationService = null;

  protected SpaceService        spaceService;

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

    providerId = IdentityType.getType(providerId).getProviderId();

    boolean isManager = Utils.isRewardingManager(remoteId);
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

    List<GamificationInformationRestEntity> gamificationInformationRestEntities = new ArrayList<>();
    Identity earnerIdentity = identityManager.getOrCreateIdentity(providerId, remoteId);
    int loadCapacity = 10;
    if (StringUtils.isNotBlank(capacity)) {
      loadCapacity = Integer.parseInt(capacity);
    }

    // find actions History by userid adding a pagination load more capacity
    // filter
    List<GamificationActionsHistory> ss = gamificationService.findActionsHistoryByEarnerId(earnerIdentity.getId(), loadCapacity);
    if (ss == null || ss.isEmpty()) {
      return Response.ok(gamificationInformationRestEntities, MediaType.APPLICATION_JSON).build();
    }

    // Build GamificationActionsHistory flow only when the returned list is not
    // null
    for (GamificationActionsHistory element : ss) {
      // Load Social identity
      Identity receiverIdentity = identityManager.getIdentity(element.getReceiver());
      Profile profile = receiverIdentity.getProfile();
      GamificationInformationRestEntity gamificationInformationRestEntity = new GamificationInformationRestEntity();
      // Set SocialIds
      gamificationInformationRestEntity.setSocialId(receiverIdentity.getId());
      gamificationInformationRestEntity.setSpace(StringUtils.equals(receiverIdentity.getProviderId(), SpaceIdentityProvider.NAME));
      gamificationInformationRestEntity.setReceiver(element.getReceiver());
      // Set username
      gamificationInformationRestEntity.setRemoteId(receiverIdentity.getRemoteId());
      // Set FullName
      gamificationInformationRestEntity.setFullname(profile.getFullName());
      // Set avatar
      gamificationInformationRestEntity.setAvatarUrl(profile.getAvatarUrl());
      // Set profile URL
      gamificationInformationRestEntity.setProfileUrl(profile.getUrl());
      // Set Final Score
      gamificationInformationRestEntity.setActionScore(element.getActionScore());
      // Set Action Title
      gamificationInformationRestEntity.setActionTitle(element.getActionTitle());
      gamificationInformationRestEntity.setContext(element.getContext());
      // Set Date-Hours-Minutes GMT Format of the creation
      gamificationInformationRestEntity.setCreatedDate(element.getCreatedDate().toGMTString());
      // Set Domain
      gamificationInformationRestEntity.setDomainDTO(DomainMapper.domainEntityToDomainDTO(element.getDomainEntity()));
      // Set Global Score
      gamificationInformationRestEntity.setGlobalScore(element.getGlobalScore());
      // Set event id
      if (canShowDetails) {
        if (element.getActivityId() != null && element.getActivityId() != 0) {
          gamificationInformationRestEntity.setObjectId("/" + LinkProvider.getPortalName("") + "/" + LinkProvider.getPortalOwner("")
              + "/activity?id=" + element.getActivityId());
        } else {
          gamificationInformationRestEntity.setObjectId(element.getObjectId());
        }
      }
      // log
      gamificationInformationRestEntities.add(gamificationInformationRestEntity);
    }

    return Response.ok(gamificationInformationRestEntities, MediaType.APPLICATION_JSON).build();
  }

  private boolean isCurrentUser(String providerId, String remoteId) {
    return OrganizationIdentityProvider.NAME.equals(providerId) && StringUtils.equals(getCurrentUsername(), remoteId);
  }

  private String getCurrentUsername() {
    return ConversationState.getCurrent().getIdentity().getUserId();
  }
}

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

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.GamificationUtils;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.ProfileReputation;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.file.services.FileStorageException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Path("/gamification/reputation")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("users")
public class UserReputationEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(UserReputationEndpoint.class);

    private final CacheControl cacheControl;

    protected GamificationService gamificationService = null;

    protected IdentityManager identityManager = null;

    protected BadgeService badgeService = null;

    public UserReputationEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        gamificationService = CommonsUtils.getService(GamificationService.class);

        identityManager = CommonsUtils.getService(IdentityManager.class);

        badgeService = CommonsUtils.getService(BadgeService.class);
    }

    @GET
    @RolesAllowed("users")
    @Path("status")
    public Response getReputationStatus(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("username") String username, @QueryParam("url") String url) {

        long userReputationScore = 0;
        int userRank = 0;

        // Get profile owner from url
        String profileOwner = StringUtils.isBlank(username) ? GamificationUtils.extractProfileOwnerFromUrl(url,"/") : username;
        if (profileOwner.equals("profile") || StringUtils.isBlank(profileOwner)) {
          Response.status(Response.Status.NOT_FOUND).build();
        }

        if (StringUtils.isNotBlank(profileOwner)) {
            try {
                // Compute user id
                Identity id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profileOwner);
                if (id == null) {
                  Response.status(Response.Status.NOT_FOUND).entity("User " + profileOwner + " not found").build();
                }

                String actorId = id.getId();

                JSONObject reputation = new JSONObject();

                userReputationScore = gamificationService.findReputationByEarnerId(actorId);

                userRank = gamificationService.getLeaderboardRank(actorId, Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant()), "all");
                
                reputation.put("score", userReputationScore);

                reputation.put("rank", userRank);


                return Response.ok().cacheControl(cacheControl).entity(reputation.toString()).build();

            } catch (Exception e) {
                LOG.error("Error to calculate repuation score for user {} ",profileOwner, e);
                return Response.serverError().build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

  @GET
  @Path("badges/{identityId}")
  @RolesAllowed("users")
  public Response getUserBadges(@PathParam("identityId") long identityId) {
    String earnerId = String.valueOf(identityId);
    Identity identity = identityManager.getIdentity(earnerId);
    if (identity == null) {
      return Response.status(400).entity("Identity not found with id " + identityId).build();
    }
    List<ProfileReputation> badgesByDomain = gamificationService.buildDomainScoreByIdentityId(earnerId);
    JSONArray profileBadges = buildProfileBadges(badgesByDomain);
    return Response.ok().cacheControl(cacheControl).entity(profileBadges.toString()).build();
  }

    @GET
    @Path("badges")
    public Response getUserBadges(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("url") String url) {

        ConversationState conversationState = ConversationState.getCurrent();
        if (conversationState != null) {
          String profilePageOwner = conversationState.getIdentity().getUserId();
          if (url != null) {
            profilePageOwner = GamificationUtils.extractProfileOwnerFromUrl(url, "/");
            if (profilePageOwner.equals("profile")) {
              profilePageOwner = conversationState.getIdentity().getUserId();
            }
          }

          /** This is a fake */
          JSONArray allBadges = new JSONArray();

          // Compute user id
          Identity id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);

          if (id == null) {
            profilePageOwner = conversationState.getIdentity().getUserId();
            id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);
          }
          String actorId = id.getId();

          List<ProfileReputation> badgesByDomain = gamificationService.buildDomainScoreByIdentityId(actorId);

          allBadges = buildProfileBadges(badgesByDomain);

          return Response.ok().cacheControl(cacheControl).entity(allBadges.toString()).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }
    @GET
    @RolesAllowed("users")
    @Path("badge/{badge}/avatar")
    public Response getBadgeAvatarById(@Context UriInfo uriInfo,
                                       @Context Request request,
                                       @PathParam("badge") Long id) throws IOException {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            InputStream stream = null;

            try {
                BadgeDTO badgeDTO = badgeService.findBadgeById(id);

                Long lastUpdated = null;
                if (badgeDTO != null) {
                    lastUpdated = (new SimpleDateFormat("yyyy-MM-dd")).parse(badgeDTO.getLastModifiedDate()).getTime();
                } else {
                    stream = UserReputationEndpoint.class.getClassLoader().getResourceAsStream("medias/images/default_badge.png");
                }
                EntityTag eTag = null;
                if (lastUpdated != null) {
                    eTag = new EntityTag(Integer.toString(lastUpdated.hashCode()));
                }
                //
                Response.ResponseBuilder builder = (eTag == null ? null : request.evaluatePreconditions(eTag));
                if (builder == null) {
                    if (stream == null) {
                        stream = getBadgeAvatarInputStream(badgeDTO);
                        if (stream == null) {
                            throw new WebApplicationException(Response.Status.NOT_FOUND);
                        }
                    }

                    builder = Response.ok(stream, "image/png");
                    builder.tag(eTag);
                }
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                builder.cacheControl(cc);
                return builder.cacheControl(cc).build();

            } catch (Exception e) {
                LOG.error("Error getting badge's avatar", e);
                return Response.serverError()
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }

    }

    private InputStream getBadgeAvatarInputStream(BadgeDTO badgeDTO) throws IOException {
        FileItem file = null;
        if (badgeDTO == null) {
            return null;
        }
        Long avatarId = badgeDTO.getIconFileId();
        if (avatarId == 0) {
            return null;
        }
        try {
            file = CommonsUtils.getService(FileService.class).getFile(avatarId);
        } catch (FileStorageException e) {
            return null;
        }
        return file == null ? null : file.getAsStream();
    }

    private JSONArray buildProfileBadges(List<ProfileReputation> reputationLis) {

        JSONArray allBadges = new JSONArray();

        if (reputationLis != null && !reputationLis.isEmpty()) {

           for (ProfileReputation rep : reputationLis) {
               // Compute won badge
               buildLatestWonBadge(rep.getDomain(), rep.getScore(), allBadges);
           }

        }

        return allBadges;

    }

    private void buildLatestWonBadge(String domain, long score, JSONArray userBadges) {

        try {
            // Get available badge within the solution
            List<BadgeDTO> allBadges = badgeService.findEnabledBadgesByDomain(domain);
            BadgeDTO badgeDTO = null;

            // A badge
            JSONObject reputation = null;
            int index = 0;
            Iterator<BadgeDTO> iterable = allBadges.iterator();
            while(iterable.hasNext()) {
                badgeDTO = iterable.next();
                if (badgeDTO.getNeededScore() < score) {
                    ++index;
                }
            }
            if (index > 0) {
                badgeDTO = allBadges.get(index - 1);
                reputation = new JSONObject();
                //computte badge's icon
                String iconUrl = "/portal/rest/gamification/reputation/badge/" + badgeDTO.getId() + "/avatar";
                reputation.put("url", iconUrl);
                reputation.put("description", badgeDTO.getDescription());
                reputation.put("id", badgeDTO.getId());
                reputation.put("title", badgeDTO.getTitle());
                reputation.put("zone", badgeDTO.getDomain());
                reputation.put("level", index);
                reputation.put("startScore", badgeDTO.getNeededScore());
                reputation.put("score", score);
                reputation.put("endScore", computeBadgeNextLevel(allBadges, index));

                userBadges.put(reputation);
            }

        } catch (Exception e) {

        }



    }

    private String computeBadgeNextLevel (List<BadgeDTO> allBadges, int index) {

        if (index >= 0 && index < allBadges.size()) {
            return String.valueOf(allBadges.get(index).getNeededScore());
        }
        return "";
    }



    private String computeLevel (List<BadgeDTO> allBadges, int index) {

        if (index >= 0 && index < allBadges.size()) {
            return String.valueOf(allBadges.get(index).getNeededScore());
        }
        return "";
    }

    @GET
    @RolesAllowed("users")
    @Path("won")
    public Response getallBadges(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("url") String url) {

        ConversationState conversationState = ConversationState.getCurrent();
        if (conversationState != null) {
            String profilePageOwner=conversationState.getIdentity().getUserId();
            try {

                if(url!=null){
                    profilePageOwner = GamificationUtils.extractProfileOwnerFromUrl(url,"/");
                    if(profilePageOwner.equals("profile")){
                        profilePageOwner= conversationState.getIdentity().getUserId();
                    }
                }

                /** This is a fake */
                JSONArray allBadges = new JSONArray();

                // Compute user id

                Identity id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);

                if(id==null){
                    profilePageOwner= conversationState.getIdentity().getUserId();
                    id= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);
                }

                String actorId = id.getId();


                List<ProfileReputation> badgesByDomain= gamificationService.buildDomainScoreByIdentityId(actorId);

                allBadges = buildProfilBadges(badgesByDomain);

                return Response.ok().cacheControl(cacheControl).entity(allBadges.toString()).build();

            } catch (Exception e) {

                LOG.error("Error loading badges belong to user : {} ",profilePageOwner, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error loading user's badges")
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
    public Response stat(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("url") String url) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {
            String profilePageOwner=conversationState.getIdentity().getUserId();

            try {

                if ( url != null) {
                    profilePageOwner = GamificationUtils.extractProfileOwnerFromUrl(url,"/");
                    if(profilePageOwner.equals("profile")){
                        profilePageOwner= conversationState.getIdentity().getUserId();
                    }

                }

                Identity id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);

                if(id==null){
                    profilePageOwner= conversationState.getIdentity().getUserId();
                    id= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);
                }

                String actorId = id.getId();


                // Find user's stats
                List<PiechartLeaderboard> userStats = gamificationService.buildStatsByUser(actorId, null, null);

                return Response.ok(userStats, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

            } catch (Exception e) {

                LOG.error("Error building statistics for user {} ",profilePageOwner, e);

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

    @GET
    @RolesAllowed("users")
    @Path("otherBadges")
    public Response getOtherBadges(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("url") String url) {

        ConversationState conversationState = ConversationState.getCurrent();
        if (conversationState != null) {
            String profilePageOwner=conversationState.getIdentity().getUserId();
            try {

                if(url!=null){
                    profilePageOwner = GamificationUtils.extractProfileOwnerFromUrl(url,"/");
                    if(profilePageOwner.equals("profile")){
                        profilePageOwner= conversationState.getIdentity().getUserId();
                    }
                }

                /** This is a fake */
                JSONArray allBadges = new JSONArray();

                // Compute user id
                Identity id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);

                if(id==null){
                    profilePageOwner= conversationState.getIdentity().getUserId();
                    id= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);
                }

                String actorId = id.getId();


                List<ProfileReputation> badgesByDomain= gamificationService.buildDomainScoreByIdentityId(actorId);

                allBadges = buildProfileNextBadges(badgesByDomain);

                return Response.ok().cacheControl(cacheControl).entity(allBadges.toString()).build();

            } catch (Exception e) {

                LOG.error("Error loading badges belong to user : {} ",profilePageOwner, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error loading user's badges")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }
    private void buildWonBadges(String domain, long score, JSONArray userBadges) {

        // Get available badge within the solution
        List<BadgeDTO> allBadges = badgeService.findEnabledBadgesByDomain(domain);

        // A badge
        JSONObject reputation = null;

        int i = 0;
        int k = 0;
        Iterator<BadgeDTO> iterable = allBadges.iterator();

        while(iterable.hasNext()) {
            BadgeDTO badgeDTO = iterable.next();
            reputation = new JSONObject();
                try {
                    //compute badge's icon
                    String iconUrl = "/portal/rest/gamification/reputation/badge/" + badgeDTO.getId() + "/avatar";
                    reputation.put("url", iconUrl);
                    reputation.put("description", badgeDTO.getDescription());
                    reputation.put("id", badgeDTO.getId());
                    reputation.put("title", badgeDTO.getTitle());
                    reputation.put("zone", badgeDTO.getDomain());
                    reputation.put("level", ++k);
                    reputation.put("startScore", badgeDTO.getNeededScore());
                    reputation.put("endScore", computeBadgeNextLevel(allBadges,i + 1));

                    userBadges.put(reputation);
                    ++i;
                } catch (Exception e) {
                    LOG.error("Error while building badges", e);
                }
          }

    }

    @GET
    @RolesAllowed("users")
    @Path("AllofBadges")
    public Response getAllOfBadges(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("url") String url) {

        ConversationState conversationState = ConversationState.getCurrent();
        if (conversationState != null) {
            String profilePageOwner=conversationState.getIdentity().getUserId();
            try {

                if(url!=null){
                    profilePageOwner = GamificationUtils.extractProfileOwnerFromUrl(url,"/");
                    if(profilePageOwner.equals("profile")){
                        profilePageOwner= conversationState.getIdentity().getUserId();
                    }
                }

                /** This is a fake */
                JSONArray allBadges = new JSONArray();

                // Compute user id
                Identity id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);

                if(id==null){
                    profilePageOwner= conversationState.getIdentity().getUserId();
                    id= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);
                }

                String actorId = id.getId();


                List<ProfileReputation> badgesByDomain= gamificationService.buildDomainScoreByIdentityId(actorId);

                allBadges = buildallBadges(badgesByDomain,url);



                return Response.ok().cacheControl(cacheControl).entity(allBadges.toString()).build();


            } catch (Exception e) {

                LOG.error("Error loading badges belong to user : {} ",profilePageOwner, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error loading user's badges")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }
    }
    private JSONArray buildProfileNextBadges(List<ProfileReputation> reputationLis) {

        JSONArray allBadges = new JSONArray();
        if (reputationLis != null && !reputationLis.isEmpty()) {

            for (ProfileReputation rep : reputationLis) {
                // Compute won badge
                buildnextWinBadges(rep.getDomain(), rep.getScore(), allBadges);
            }
        }
        return allBadges;
    }
    private JSONArray buildallBadges(List<ProfileReputation> reputationLis,@QueryParam("url") String url) {
        ConversationState conversationState = ConversationState.getCurrent();

        JSONArray allBadges = new JSONArray();
        String profilePageOwner = conversationState.getIdentity().getUserId();

        if(url!=null){
            profilePageOwner = GamificationUtils.extractProfileOwnerFromUrl(url,"/");
            if(profilePageOwner.equals("profile")){
                profilePageOwner= conversationState.getIdentity().getUserId();
            }

        }
        Identity id = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);

        if(id==null){
            profilePageOwner= conversationState.getIdentity().getUserId();
            id= identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profilePageOwner);
        }

        String actorId = id.getId();

        if (conversationState != null) {
            if (reputationLis != null && !reputationLis.isEmpty()) {

                for (ProfileReputation rep : reputationLis) {
                    // Compute won badge
                    buildWonBadges(rep.getDomain(), rep.getScore(), allBadges);

                }

            }


        }
        return allBadges;

    }
        private JSONArray buildProfilBadges(List<ProfileReputation> reputationLis) {

        JSONArray allBadges = new JSONArray();

        if (reputationLis != null && !reputationLis.isEmpty()) {

            for (ProfileReputation rep : reputationLis) {
                // Compute won badge
                buildWonBadges(rep.getDomain(), rep.getScore(), allBadges);


            }

        }

        return allBadges;

    }

    private void buildnextWinBadges(String domain, long score, JSONArray userBadges) {

        // Get available badge within the solution
        List<BadgeDTO> allBadges = badgeService.findEnabledBadgesByDomain(domain);

        // A badge
        JSONObject reputation = null;

        int i = 0;
        int k = 0;

        Iterator<BadgeDTO> iterable = allBadges.iterator();

        while(iterable.hasNext()) {
            BadgeDTO badgeDTO = iterable.next();
            if (badgeDTO.getNeededScore() > score) {

                reputation = new JSONObject();
                try {

                    //computte badge's icon
                    String iconUrl = "/portal/rest/gamification/reputation/badge/" + badgeDTO.getId() + "/avatar";
                    reputation.put("url", iconUrl);
                    reputation.put("description", badgeDTO.getDescription());
                    reputation.put("id", badgeDTO.getId());
                    reputation.put("title", badgeDTO.getTitle());
                    reputation.put("zone", badgeDTO.getDomain());
                    reputation.put("level", ++k);

                    reputation.put("startScore", badgeDTO.getNeededScore());

                    reputation.put("endScore", computeLevel(allBadges,i+1));

                    userBadges.put(reputation);
                    ++i;

                } catch (Exception e) {

                }
            }
        }

    }

}

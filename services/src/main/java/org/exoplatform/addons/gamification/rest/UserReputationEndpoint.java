package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.file.services.FileStorageException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Path("point/status")
    public Response getReputationStatus(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("url") String url) {

        ConversationState conversationState = ConversationState.getCurrent();

        // Compute social profile owner
        String profileOwner = substringAfterLast(url,"/");

        if (conversationState != null) {
            try {

                // Compute user id
                String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, profileOwner, false).getId();

                JSONObject reputation = new JSONObject();
                reputation.put("points", gamificationService.getUserGlobalScore(actorId));


                return Response.ok().cacheControl(cacheControl).entity(reputation.toString()).build();

            } catch (Exception e) {

                LOG.error("Error to calculate repuation score for user {} ",profileOwner, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error to compute the user reputaiotn points")
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
    @Path("badge/all")
    public Response getUserBadges(@Context UriInfo uriInfo, @Context HttpServletRequest request, @QueryParam("url") String url) {


        //String s = Util.getViewerId(uriInfo);

        //Identity current = Utils.getOwnerIdentity();
        ConversationState conversationState = ConversationState.getCurrent();

        String streamOwner = substringAfterLast(url,"/");

        if (conversationState != null) {
            try {
                //List<ReputationDTO> allRules = reputationService.getBadgesByUser();

                /** This is a fake */
                JSONArray allBadges = new JSONArray();

                // Compute user id
                String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, streamOwner, false).getId();

                Set<GamificationContextItemEntity> gamificationContextItemEntitySet = gamificationService.getUserGamification(actorId);

                allBadges = buildProfileBadge(gamificationContextItemEntitySet);

                return Response.ok().cacheControl(cacheControl).entity(allBadges.toString()).build();

            } catch (Exception e) {

                LOG.error("Error loading badges belong to user : {} ",streamOwner, e);

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
    @Path("badge/{badge}/avatar")
    public Response getSpaceAvatarById(@Context UriInfo uriInfo,
                                       @Context Request request,
                                       @PathParam("badge") String badgeTitle) throws IOException {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            try {
                BadgeDTO badgeDTO = badgeService.findBadgeByTitle(badgeTitle);

                Long lastUpdated = null;
                if (badgeDTO != null) {
                    lastUpdated = (new SimpleDateFormat("yyyy-MM-dd")).parse(badgeDTO.getLastModifiedDate()).getTime();
                }
                EntityTag eTag = null;
                if (lastUpdated != null) {
                    eTag = new EntityTag(Integer.toString(lastUpdated.hashCode()));
                }
                //
                Response.ResponseBuilder builder = (eTag == null ? null : request.evaluatePreconditions(eTag));
                if (builder == null) {
                    InputStream stream = getBadgeAvatarInputStream(badgeDTO);
                    if (stream == null) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
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
                        .cacheControl(cacheControl)
                        .entity("Error getting badge's avatar")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }

    }

    private InputStream getBadgeAvatarInputStream(BadgeDTO badgeDTO) throws IOException {
        FileItem file = null;
        if (badgeDTO == null) {
            return null;
        }
        Long avatarId = badgeDTO.getIconFileId();
        if (avatarId == null) {
            return null;
        }
        try {
            file = CommonsUtils.getService(FileService.class).getFile(avatarId);
        } catch (FileStorageException e) {
            return null;
        }

        if (file == null) {
            return null;
        }
        return file.getAsStream();
    }

    private JSONArray buildProfileBadge(Set<GamificationContextItemEntity> gamificationContextItemEntitySet) {

        JSONArray allBadges = new JSONArray();

        if (gamificationContextItemEntitySet != null && !gamificationContextItemEntitySet.isEmpty()) {
            JSONObject reputation = null;

            // Get available zone within the solution
            // TODO


            //TODO : Badge should be done for all zone not only SOCIAL ZONE
            int userScore = gamificationContextItemEntitySet.stream().
                    filter(i -> i.getZone().equalsIgnoreCase("social")).
                    map(GamificationContextItemEntity::getScore).
                    mapToInt(Integer::intValue).
                    sum();

            // Compute won badge
            List<BadgeDTO> badgeDTOS = buildWonBadges("social", userScore);
            int startScore = 0;
            // Var to compute Badge level
            int k = 0;

            for (int i = 0; i < badgeDTOS.size(); i++) {

                BadgeDTO badgeDTO = badgeDTOS.get(i);
                reputation = new JSONObject();
                try {
                    //computte badge's icon
                    String iconUrl = "/rest/gamification/reputation/badge/" + badgeDTO.getTitle() + "/avatar";
                    reputation.put("url", iconUrl);
                    reputation.put("description", badgeDTO.getDescription());
                    reputation.put("id", i);
                    reputation.put("title", badgeDTO.getTitle());
                    reputation.put("zone", badgeDTO.getDomain());
                    reputation.put("level", ++k);
                    reputation.put("startScore", startScore);
                    reputation.put("endScore", badgeDTO.getNeededScore());
                    startScore = startScore + badgeDTO.getNeededScore();
                    allBadges.put(reputation);

                } catch (Exception e) {

                }
            }
        }


        return allBadges;

    }

    private List<BadgeDTO> buildWonBadges(String zone, int score) {

        // Get available badge within the solution
        List<BadgeDTO> allBadges = badgeService.getAllBadges();

        return allBadges.stream().
                filter(b -> b.getDomain().equalsIgnoreCase(zone)).
                filter(b -> b.getNeededScore() < score).
                collect(Collectors.toList());

    }
    private static String substringAfterLast(String str, String separator) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (StringUtils.isEmpty(separator)) {
            return "";
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == (str.length() - separator.length())) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
}

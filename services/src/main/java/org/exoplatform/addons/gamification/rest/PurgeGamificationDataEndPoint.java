package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Path("/gamification/purge")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("administrators")
public class PurgeGamificationDataEndPoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageRulesEndpoint.class);

    private final CacheControl cacheControl;

    protected GamificationService gamificationService = null;

    public PurgeGamificationDataEndPoint(GamificationService gamificationService) {
        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        this.gamificationService = gamificationService;


    }

    @GET
    @Path("items")
    public Response purge(@Context UriInfo uriInfo, @QueryParam("id") String id, @QueryParam("domain") String domain) {
        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {
            List<GamificationContextItemEntity> gamificationContextItemEntityList = null;

            try {

                // Get all items of a user by zone
                gamificationContextItemEntityList = gamificationService.findGamificationItemsByUserIdAndDomain(id, domain);

                if (gamificationContextItemEntityList != null && !gamificationContextItemEntityList.isEmpty()) {

                    LOG.info("Gamification items for user {} in Domain {} are {} ", id, domain, gamificationContextItemEntityList.size());

                    List<String> opType = new ArrayList<>();

                    for (GamificationContextItemEntity item : gamificationContextItemEntityList) {

                        if (!opType.contains(item.getOpType())) {
                            // Add operationType to processed list
                            opType.add(item.getOpType());

                        } else {
                            // When an item with the same operationType already registered, we need to drop it
                            gamificationService.deleteItem(item);
                        }

                    }

                }

                return Response.ok("Done", MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();


            } catch (Exception e) {

                LOG.error("Error puging Gamification Items for userid {} ", id, e);

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
    @Path("score")
    public Response update(@Context UriInfo uriInfo, @QueryParam("id") String id) {
        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            List<GamificationContextItemEntity> gamificationContextItemEntityList = null;

            try {

                GamificationContextEntity gamificationContextEntity = gamificationService.findGamificationContextByUsername(id);

                if (gamificationContextEntity != null ) {

                    Set<GamificationContextItemEntity> items = gamificationContextEntity.getGamificationItems();


                    if (items != null && !items.isEmpty()) {

                        List<String> opType = new ArrayList<>();

                        int scoreSocial = 0;

                        int scoreKnowledge = 0;

                        // This algo is used to compare global score and score by items, set the global score to score by items
                        for (GamificationContextItemEntity item : items) {

                            if (item.getZone().equalsIgnoreCase("social")) {
                                if (!opType.contains(item.getOpType())) {
                                    scoreKnowledge += item.getScore();
                                }
                            } else {
                                if (!opType.contains(item.getOpType())) {
                                    scoreKnowledge += item.getScore();
                                }
                            }

                            opType.add(item.getOpType());

                        }

                        if ( (scoreSocial+scoreKnowledge) > gamificationContextEntity.getScore()) {

                            gamificationContextEntity.setScore(scoreSocial+scoreKnowledge);
                            gamificationService.updateUserGamificationContextScore(gamificationContextEntity);
                        }

                    }

                }

                return Response.ok("Done", MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();


            } catch (Exception e) {

                LOG.error("Error puging Gamification Items for userid {} ", id, e);

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

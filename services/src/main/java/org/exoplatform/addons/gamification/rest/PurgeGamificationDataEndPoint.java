package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;

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


        throw new UnsupportedOperationException();

    }

    @GET
    @Path("score")
    public Response update(@Context UriInfo uriInfo, @QueryParam("id") String id) {
        throw new UnsupportedOperationException();


    }

}

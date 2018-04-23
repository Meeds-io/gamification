package org.exoplatform.gamification.rest;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.service.configuration.RuleService;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/gamification/rules")
public class ManageRulesEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageRulesEndpoint.class);

    protected RuleService ruleService = null;

    public ManageRulesEndpoint() {

        ruleService = CommonsUtils.getService(RuleService.class);

    }

    @GET
    @RolesAllowed("users")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public Response getAllRules(@Context UriInfo uriInfo) {

        CacheControl cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        try {
            List<RuleDTO> allRules = ruleService.getAllRules();

            return Response.ok(allRules, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        }

    }

    @PUT
    @RolesAllowed("users")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add")
    public Response AddRule(@Context UriInfo uriInfo) {

        CacheControl cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        try {
            List<RuleDTO> allRules = ruleService.getAllRules();

            return Response.ok(allRules, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        }

    }

    @DELETE
    @RolesAllowed("users")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public Response deleteRule(@Context UriInfo uriInfo, @QueryParam("ruleTitle") String ruleTitle) {

        CacheControl cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        try {
            //--- Remove the rule
            ruleService.deleteRule(ruleTitle);


            return Response.ok("Deleted " + ruleTitle, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        }

    }
}

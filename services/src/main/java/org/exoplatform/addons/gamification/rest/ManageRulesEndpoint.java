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

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Path("/gamification/rules")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("administrators")
public class ManageRulesEndpoint implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ManageRulesEndpoint.class);

    private final CacheControl cacheControl;

    protected RuleService ruleService = null;

    protected IdentityManager  identityManager  = null;

    public ManageRulesEndpoint() {

        this.cacheControl = new CacheControl();

        cacheControl.setNoCache(true);

        cacheControl.setNoStore(true);

        ruleService = CommonsUtils.getService(RuleService.class);

        identityManager = CommonsUtils.getService(IdentityManager.class);

    }

    @GET
    @Path("/all")
    public Response getAllRules(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {
            try {
                List<RuleDTO> allRules = ruleService.getAllRules();

                return Response.ok().cacheControl(cacheControl).entity(allRules).build();

            } catch (Exception e) {

                LOG.error("Error listing all rules ", e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error listing all rules")
                        .build();
            }

        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }


    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/add")
    public Response addRule(@Context SecurityContext securityContext, @Context UriInfo uriInfo, RuleDTO ruleDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();

            try {
                // Compute rule's data
                ruleDTO.setId(null);
                ruleDTO.setTitle(ruleDTO.getEvent()+"_"+ruleDTO.getArea());
                ruleDTO.setCreatedBy(currentUserName);
                ruleDTO.setLastModifiedBy(currentUserName);
                ruleDTO.setLastModifiedDate(new Date());

                //--- Add rule
                ruleDTO = ruleService.addRule(ruleDTO);

                return Response.ok().cacheControl(cacheControl).entity(ruleDTO).build();

            }catch (EntityExistsException e) {

                LOG.error("Rule with event {} and domain {} already exist", ruleDTO.getEvent(), ruleDTO.getArea(), e);

                return Response.notModified()
                        .cacheControl(cacheControl)
                        .entity("Rule already exists")
                        .build();
            } catch (Exception e) {

                LOG.error("Error adding new rule {} by {} ", ruleDTO.getTitle(), currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error adding new rule")
                        .build();

            }


        } else {

            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }

    }

    @PUT
    @Path("/update")
    public Response updateRule(@Context UriInfo uriInfo, @Context HttpServletRequest request, RuleDTO ruleDTO) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();
            try {

                //TODO : Load locale
                Locale lc = request.getLocale();

                // Compute rule's data
                ruleDTO.setCreatedBy(currentUserName);
                ruleDTO.setLastModifiedBy(currentUserName);
                ruleDTO.setLastModifiedDate(new Date());

                //--- Add rule
                ruleDTO = ruleService.updateRule(ruleDTO);

                // Compute user id
                String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false).getId();
                LOG.info("service=gamification operation=edit-rule parameters=\"user_social_id:{},rule_id:{},rule_title:{},rule_description:{}\"", actorId, ruleDTO.getId(), ruleDTO.getTitle(), ruleDTO.getDescription());

                return Response.ok().cacheControl(cacheControl).entity(ruleDTO).build();

            } catch (EntityExistsException e) {

                LOG.error("Rule with event {} and domain {} already exist", ruleDTO.getEvent(), ruleDTO.getArea(), e);

                return Response.notModified()
                        .cacheControl(cacheControl)
                        .entity("Rule already exists")
                        .build();
            }catch (Exception e) {

                LOG.error("Error updating rule {} by {} ", ruleDTO.getTitle(), currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error updating a rule")
                        .build();
            }

        } else {

            return Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cacheControl)
                    .entity("Unauthorized user")
                    .build();
        }


    }


    @DELETE
    @Path("/delete/{id}")
    public Response deleteRule(@Context UriInfo uriInfo, @PathParam("id") Long id) {

        ConversationState conversationState = ConversationState.getCurrent();

        if (conversationState != null) {

            String currentUserName = conversationState.getIdentity().getUserId();

            try {
                //--- Remove the rule
                ruleService.deleteRule(id);
                return Response.ok().cacheControl(cacheControl).entity("Rule " + id + " has been removed successfully ").build();

            } catch (Exception e) {

                LOG.error("Error deleting rule {} by {} ", id, currentUserName, e);

                return Response.serverError()
                        .cacheControl(cacheControl)
                        .entity("Error deleting a rule")
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

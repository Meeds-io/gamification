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

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/gamification/rules")
@Tag(name = "/gamification/rules", description = "Manages rules")
@Produces(MediaType.APPLICATION_JSON)
public class ManageRulesEndpoint implements ResourceContainer {

  private static final Log   LOG = ExoLogger.getLogger(ManageRulesEndpoint.class);

  private final CacheControl cacheControl;

  protected RuleService      ruleService;

  protected IdentityManager  identityManager;

  public ManageRulesEndpoint(RuleService ruleService, IdentityManager identityManager) {
    this.cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    this.ruleService = ruleService;
    this.identityManager = identityManager;
  }

  @GET
  @Path("/all")
  @RolesAllowed("users")
  public Response getAllRules() {
    List<RuleDTO> allRules = ruleService.findAllRules();
    return Response.ok().cacheControl(cacheControl).entity(allRules).build();
  }

  @GET
  @Path("/active")
  @RolesAllowed("users")
  public Response getActiveRules() {
    ConversationState conversationState = ConversationState.getCurrent();
    if (conversationState != null) {
      List<RuleDTO> activesRules = ruleService.getActiveRules();
      return Response.ok().cacheControl(cacheControl).entity(activesRules).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(cacheControl).entity("Unauthorized user").build();
    }
  }

  @POST
  @Consumes({ MediaType.APPLICATION_JSON })
  @RolesAllowed("administrators")
  @Path("/add")
  public Response addRule(@RequestBody(description = "rule object to save", required = true)
                          RuleDTO ruleDTO) throws Exception {
    ConversationState conversationState = ConversationState.getCurrent();
    String currentUserName = conversationState.getIdentity().getUserId();
    try {
      // Compute rule's data
      ruleDTO.setId(null);
      ruleDTO.setTitle(ruleDTO.getEvent() + "_" + ruleDTO.getArea());
      ruleDTO.setCreatedBy(currentUserName);
      ruleDTO.setLastModifiedBy(currentUserName);

      // --- Add rule
      ruleDTO = ruleService.addRule(ruleDTO);

      return Response.ok().cacheControl(cacheControl).entity(ruleDTO).build();

    } catch (EntityExistsException e) {
      return Response.status(Status.CONFLICT).entity("Rule already exists").build();
    }
  }

  @PUT
  @RolesAllowed("administrators")
  @Path("/update")
  public Response updateRule(@Context
                             HttpServletRequest request,
                             @RequestBody(description = "rule object to update", required = true)
                             RuleDTO ruleDTO) throws Exception {
   String currentUserName = ConversationState.getCurrent().getIdentity().getUserId();

   // Compute rule's data
   ruleDTO.setCreatedBy(currentUserName);
   ruleDTO.setLastModifiedBy(currentUserName);

   // --- Add rule
   ruleDTO = ruleService.updateRule(ruleDTO);

   // Compute user id
   String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                                                        currentUserName)
                                   .getId();
   LOG.info("service=gamification operation=edit-rule parameters=\"user_social_id:{},rule_id:{},rule_title:{},rule_description:{}\"",
            actorId,
            ruleDTO.getId(),
            ruleDTO.getTitle(),
            ruleDTO.getDescription());

   return Response.ok().cacheControl(cacheControl).entity(ruleDTO).build();
 }

  @PUT
  @RolesAllowed("administrators")
  @Path("/delete/{id}")
  public Response deleteRule(@Parameter(description = "id of the rule", required = true)
                             @PathParam("id")
                             Long id) throws Exception {
    ruleService.deleteRule(id);
    return Response.ok().cacheControl(cacheControl).entity("Rule " + id + " has been removed successfully ").build();
  }
}

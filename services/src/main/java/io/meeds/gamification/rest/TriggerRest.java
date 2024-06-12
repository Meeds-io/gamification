/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.meeds.gamification.model.Trigger;
import io.meeds.gamification.rest.builder.TriggerBuilder;
import io.meeds.gamification.rest.model.TriggerRestEntity;
import io.meeds.gamification.service.EventRegistry;

import io.meeds.gamification.service.TriggerService;
import org.exoplatform.services.rest.resource.ResourceContainer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collection;
import java.util.List;

import static io.meeds.gamification.utils.Utils.getCurrentUser;
import static io.meeds.gamification.utils.Utils.getExpandOptions;

@Path("/gamification/triggers")
public class TriggerRest implements ResourceContainer {

  private final EventRegistry  eventRegistry;

  private final TriggerService triggerService;

  public TriggerRest(EventRegistry eventRegistry, TriggerService triggerService) {
    this.eventRegistry = eventRegistry;
    this.triggerService = triggerService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of gamification triggers", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled") })
  public Response getTriggers(@Parameter(description = "Used to filter triggers by Connector type") @QueryParam("type") String type,
                              @Parameter(description = "Used to retrieve extra information about triggers") @QueryParam("expand") String expand) {
    List<String> expandFields = getExpandOptions(expand);
    return Response.ok(getTriggersRestEntities(expandFields, type)).build();
  }

  @Path("status")
  @POST
  @RolesAllowed("users")
  @Operation(summary = "enables/disables trigger for connector account.", description = "enables/disables event for connector account", method = "POST")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updateAccountTriggerStatus(@Parameter(description = "Trigger name", required = true) @FormParam("trigger") String trigger,
                                             @Parameter(description = "Account Id", required = true) @FormParam("accountId") long accountId,
                                             @Parameter(description = "Trigger status enabled/disabled. possible values: true for enabled, else false", required = true) @FormParam("enabled") boolean enabled) {

    String currentUser = getCurrentUser();
    try {
      triggerService.setTriggerEnabledForAccount(trigger, accountId, enabled, currentUser);
      return Response.noContent().build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
  }

  private List<TriggerRestEntity> getTriggersRestEntities(List<String> expandFields, String type) {
    Collection<Trigger> triggerList = eventRegistry.getTriggers(type);
    return TriggerBuilder.toRestEntities(triggerService, triggerList, expandFields);
  }
}

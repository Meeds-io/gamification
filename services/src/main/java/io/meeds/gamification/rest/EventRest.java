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

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.filter.EventFilter;
import io.meeds.gamification.rest.builder.EventBuilder;
import io.meeds.gamification.rest.model.EntityList;
import io.meeds.gamification.rest.model.EventRestEntity;
import io.meeds.gamification.service.EventService;

import org.exoplatform.services.rest.resource.ResourceContainer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static io.meeds.gamification.utils.Utils.getCurrentUser;

@Path("/gamification/events")
public class EventRest implements ResourceContainer {

  private final EventService eventService;

  public EventRest(EventService eventService) {
    this.eventService = eventService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieves the list of gamification events", method = "GET")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
          @ApiResponse(responseCode = "401", description = "Unauthorized operation"), })
  public Response getEvents(@Parameter(description = "Used to filter events by Connector type") @QueryParam("type") String type,
                            @Parameter(description = "Used to know the status of the event according to the project") @QueryParam("projectId") long projectId,
                            @Parameter(description = "Used to filter events by trigger") @QueryParam("trigger") List<String> triggers,
                            @Parameter(description = "Offset of results to retrieve") @QueryParam("offset") @DefaultValue("0") int offset,
                            @Parameter(description = "Limit of results to retrieve") @QueryParam("limit") @DefaultValue("0") int limit,
                            @Parameter(description = "Returning the total count of filtered events or not.") @QueryParam("returnSize") @DefaultValue("false") boolean returnSize) {

    EventFilter eventFilter = new EventFilter();
    eventFilter.setType(type);
    eventFilter.setTriggers(triggers);
    List<EventRestEntity> eventDTOList = getEventsRestEntities(eventService, eventFilter, type, projectId, offset, limit);
    EntityList<EventRestEntity> eventDTOEntityList = new EntityList<>();
    eventDTOEntityList.setEntities(eventDTOList);
    eventDTOEntityList.setOffset(offset);
    eventDTOEntityList.setLimit(limit);
    if (returnSize) {
      eventDTOEntityList.setSize(eventService.countEvents(eventFilter));
    }
    return Response.ok(eventDTOEntityList).build();
  }

  @Path("status")
  @POST
  @RolesAllowed("users")
  @Operation(summary = "enables/disables event for connector project.", description = "enables/disables event for connector project", method = "POST")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Request fulfilled"),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updateProjectEventStatus(@Parameter(description = "connector type", required = true) @FormParam("type") String type,
                                           @Parameter(description = "connector project remote Id", required = true) @FormParam("projectId") long projectId,
                                           @Parameter(description = "Event name", required = true) @FormParam("event") String event,
                                           @Parameter(description = "Event status enabled/disabled. possible values: true for enabled, else false", required = true) @FormParam("enabled") boolean enabled) {

    String currentUser = getCurrentUser();
    try {
      eventService.setEventEnabledForProject(type, projectId, event, enabled, currentUser);
      return Response.noContent().build();
    } catch (IllegalAccessException e) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
  }

  private List<EventRestEntity> getEventsRestEntities(EventService eventService,
                                                      EventFilter eventFilter,
                                                      String type,
                                                      long projectId,
                                                      int offset,
                                                      int limit) {
    List<EventDTO> eventDTOList = eventService.getEvents(eventFilter, offset, limit);
    return EventBuilder.toRestEntities(eventService, type, projectId, eventDTOList);
  }
}

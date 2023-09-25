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
import io.meeds.gamification.rest.model.EntityList;
import io.meeds.gamification.service.EventService;

import org.exoplatform.commons.exception.ObjectNotFoundException;
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
          @ApiResponse(responseCode = "200", description = "Request fulfilled")})
  public Response getEvents(@Parameter(description = "Used to filter events by Connector type") @QueryParam("type") String type,
                            @Parameter(description = "Used to filter events by trigger") @QueryParam("trigger") List<String> triggers,
                            @Parameter(description = "Offset of results to retrieve") @QueryParam("offset") @DefaultValue("0") int offset,
                            @Parameter(description = "Limit of results to retrieve") @QueryParam("limit") @DefaultValue("0") int limit,
                            @Parameter(description = "Returning the total count of filtered events or not.") @QueryParam("returnSize") @DefaultValue("false") boolean returnSize) {

    EventFilter eventFilter = new EventFilter();
    eventFilter.setType(type);
    eventFilter.setTriggers(triggers);
    List<EventDTO> eventDTOList = eventService.getEvents(eventFilter, offset, limit);
    EntityList<EventDTO> eventDTOEntityList = new EntityList<>();
    eventDTOEntityList.setEntities(eventDTOList);
    eventDTOEntityList.setOffset(offset);
    eventDTOEntityList.setLimit(limit);
    if (returnSize) {
      eventDTOEntityList.setSize(eventService.countEvents(eventFilter));
    }
    return Response.ok(eventDTOEntityList).build();
  }
}

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
package io.meeds.gamification.service;

import static org.junit.Assert.assertThrows;

import io.meeds.gamification.entity.EventEntity;
import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.filter.EventFilter;
import io.meeds.gamification.plugin.EventConfigPlugin;
import io.meeds.gamification.storage.mapper.EventMapper;
import io.meeds.gamification.test.AbstractServiceTest;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventServiceTest extends AbstractServiceTest {

  private static final String ADMIN_USER = "root1";

  @Override
  public void setUp() throws Exception {
    super.setUp();
    registerAdministratorUser(ADMIN_USER);
  }

  public void testEnabledDisableEventForProject() throws Exception {
    EventEntity eventEntity = new EventEntity();
    eventEntity.setType("connectorName");
    eventEntity.setTitle("event1");
    eventEntity.setTrigger("trigger1");
    EventDTO eventDTO = eventService.createEvent(EventMapper.fromEntity(eventEntity));
    Map<String, String> eventProperties = eventDTO.getProperties();
    assertNull(eventProperties);
    eventService.setEventEnabledForProject(eventDTO.getId(), 1L, false, "root1");
    eventDTO = eventService.getEvent(eventDTO.getId());
    eventProperties = eventDTO.getProperties();
    assertEquals("false", eventProperties.get(1L + ".enabled"));

  }

  public void testCreateEvent() throws Exception {
    EventFilter eventFilter = new EventFilter();
    assertEquals(Collections.emptyList(), eventService.getEvents(eventFilter, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(null));
    EventEntity eventEntity = new EventEntity();
    eventEntity.setType("connectorName");
    eventEntity.setTitle("event1");
    eventEntity.setTrigger("trigger1");
    eventService.createEvent(EventMapper.fromEntity(eventEntity));
    assertNotNull(eventService.getEvents(eventFilter, offset, limit));
    assertEquals(1, eventService.countEvents(eventFilter));
  }

  public void testGetEvents() {
    EventFilter eventFilter = new EventFilter();

    List<EventDTO> allEvents = eventService.getEvents(eventFilter, offset, limit);
    assertEquals(Collections.emptyList(), allEvents);
    String eventTitle1 = "test-event1";
    String eventTitle2 = "test-event2";
    eventRegistry.addPlugin(new EventConfigPlugin(newParam(eventTitle1, "connectorName", "trigger1")));
    eventRegistry.addPlugin(new EventConfigPlugin(newParam(eventTitle2, "connectorName2", "trigger2")));
    eventRegistry.start();
    assertNotNull(eventService.getEvents(eventFilter, offset, limit));
    assertEquals(2, eventService.getEvents(eventFilter, offset, limit).size());
    assertEquals(2, eventService.countEvents(eventFilter));
  }

  private InitParams newParam(String title, String type, String trigger) {
    InitParams params = new InitParams();
    EventDTO eventDTO = new EventDTO();
    eventDTO.setTitle(title);
    eventDTO.setType(type);
    eventDTO.setTrigger(trigger);
    ObjectParameter parameter = new ObjectParameter();
    parameter.setName("event");
    parameter.setObject(eventDTO);
    params.addParameter(parameter);
    return params;
  }
}

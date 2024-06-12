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
import io.meeds.gamification.plugin.AnnouncementActivityTypePlugin;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.plugin.EventPlugin;
import io.meeds.gamification.storage.mapper.EventMapper;
import io.meeds.gamification.test.AbstractServiceTest;
import org.exoplatform.webui.config.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventServiceTest extends AbstractServiceTest {

  private static final String ADMIN_USER = "root1";

  private static final String EVENT_NAME = "eventName";

  @Override
  public void setUp() throws Exception {
    super.setUp();
    registerAdministratorUser(ADMIN_USER);
  }

  public void testCreateEvent() throws Exception {
    EventFilter eventFilter = new EventFilter();
    assertEquals(Collections.emptyList(), eventService.getEvents(eventFilter, OFFSET, LIMIT));
    assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(null));
    EventEntity eventEntity = new EventEntity();
    eventEntity.setType("connectorName");
    eventEntity.setTitle("event1");
    eventEntity.setTrigger("trigger1");
    eventService.createEvent(EventMapper.fromEntity(eventEntity));
    assertNotNull(eventService.getEvents(eventFilter, OFFSET, LIMIT));
    assertEquals(1, eventService.countEvents(eventFilter));
  }

  public void testDeleteEvent() throws Exception {
    EventFilter eventFilter = new EventFilter();
    assertEquals(Collections.emptyList(), eventService.getEvents(eventFilter, OFFSET, LIMIT));
    EventEntity eventEntity = new EventEntity();
    eventEntity.setType("connectorName");
    eventEntity.setTitle("event1");
    eventEntity.setTrigger("trigger1");
    EventDTO eventDTO = eventService.createEvent(EventMapper.fromEntity(eventEntity));
    assertNotNull(eventService.getEvents(eventFilter, OFFSET, LIMIT));
    eventService.deleteEventById(eventDTO.getId());
    assertEquals(Collections.emptyList(), eventService.getEvents(eventFilter, OFFSET, LIMIT));
  }

  public void testUpdateEvent() throws Exception {
    EventEntity eventEntity = new EventEntity();
    eventEntity.setType("connectorName");
    eventEntity.setTitle("event1");
    eventEntity.setTrigger("trigger1");
    EventDTO eventDTO = eventService.createEvent(EventMapper.fromEntity(eventEntity));
    assertNotNull(eventService.getEventByTitleAndTrigger("event1", "trigger1"));
    assertEquals("trigger1", eventService.getEvent(eventDTO.getId()).getTrigger());
    eventDTO.setTrigger("trigger2");
    eventService.updateEvent(eventDTO);
    assertEquals("trigger2", eventService.getEvent(eventDTO.getId()).getTrigger());
  }

  public void testEventPlugin() {
    setEventPlugin();
    assertEquals(List.of("trigger1", "trigger2", "trigger3"), eventService.getEventPlugin("trigger1").getTriggers());
    assertFalse(eventService.getEventPlugin("trigger1").isValidEvent(new HashMap<>(), ""));
  }

  private void removeEventPlugin() {
    eventService.removePlugin(EVENT_NAME);
  }

  private void setEventPlugin() {
    removeEventPlugin();
    EventPlugin eventPlugin = new EventPlugin() {

      @Override
      public String getEventType() {
        return EVENT_NAME;
      }

      @Override
      public List<String> getTriggers() {
        return List.of("trigger1", "trigger2", "trigger3");
      }

      @Override
      public boolean isValidEvent(Map<String, String> eventProperties, String triggerDetails) {
        return false;
      }
    };
    eventPlugin.setName(EVENT_NAME);
    eventService.addPlugin(eventPlugin);
  }
}

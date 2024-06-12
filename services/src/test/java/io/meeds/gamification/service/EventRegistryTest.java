/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
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
 *
 */
package io.meeds.gamification.service;

import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.Trigger;
import io.meeds.gamification.plugin.EventConfigPlugin;
import org.exoplatform.container.xml.ObjectParameter;

import org.exoplatform.container.xml.InitParams;
import io.meeds.gamification.test.AbstractServiceTest;

import java.util.List;

public class EventRegistryTest extends AbstractServiceTest {

  public void testAddPlugin() {
    InitParams initParams = new InitParams();
    ObjectParameter objectParam = new ObjectParameter();
    objectParam.setName("event");
    EventDTO eventDTO = newEventDTO("event");
    objectParam.setObject(eventDTO);
    initParams.addParam(objectParam);

    // Given
    eventRegistry.addPlugin(new EventConfigPlugin(initParams));
    restartTransaction();

    // When
    List<Trigger> triggers = eventRegistry.getTriggers("eventType");
    Trigger trigger = eventRegistry.getTrigger("eventType", "event");

    // Then
    assertEquals(1, triggers.size());
    assertNotNull(trigger);
    assertEquals("eventType", trigger.getType());

    // Given
    eventRegistry.remove(new EventConfigPlugin(initParams));
    restartTransaction();

    // When
    triggers = eventRegistry.getTriggers("eventType");
    trigger = eventRegistry.getTrigger("eventType", "event");

    // Then
    assertEquals(0, triggers.size());
    assertNull(trigger);
  }

}

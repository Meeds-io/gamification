/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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
package io.meeds.gamification.rest;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;

import io.meeds.gamification.entity.BadgeEntity;
import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.test.AbstractServiceTest;

public class TestBadgeRest extends AbstractServiceTest { // NOSONAR

  protected Class<?> getComponentClass() {
    return BadgeRest.class;
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    startSessionAs("root1");
    registry(getComponentClass());
  }

  @Test
  public void testGetAllBadges() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("badges/all"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testAddBadge() throws Exception {
    ProgramDTO program = newProgram();
    JSONObject programData = new JSONObject();
    programData.put("id", program.getId());
    programData.put("title", program.getTitle());

    JSONObject data = new JSONObject();
    data.put("title", "foo");
    data.put("description", "description");
    data.put("program", programData);

    ContainerResponse response = getResponse("POST", getURLResource("badges/add"), data.toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    BadgeDTO entity = (BadgeDTO) response.getEntity();
    assertEquals("foo", entity.getTitle());
    assertEquals("description", entity.getDescription());
    assertEquals(GAMIFICATION_DOMAIN, entity.getProgram().getTitle());
  }

  @Test
  public void testDeleteBadge() throws Exception {
    BadgeEntity badgeEntity = newBadge(1L);
    ContainerResponse response = getResponse("DELETE", getURLResource("badges/delete/" + badgeEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testUpdateBadge() throws Exception {
    BadgeEntity badgeEntity = newBadge(1L);
    ProgramDTO program = newProgram();
    JSONObject domainData = new JSONObject();
    domainData.put("id", program.getId());
    domainData.put("title", program.getTitle());

    JSONObject data = new JSONObject();
    data.put("id", badgeEntity.getId());
    data.put("title", badgeEntity.getTitle());
    data.put("description", badgeEntity.getDescription() + "_test");
    data.put("program", domainData);

    ContainerResponse response = getResponse("PUT", getURLResource("badges/update"), data.toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    BadgeDTO entity = (BadgeDTO) response.getEntity();
    assertEquals(entity.getDescription(), "Description_test");
  }
}

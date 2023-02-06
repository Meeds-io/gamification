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
package org.exoplatform.addons.gamification.rest;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import static org.jgroups.util.Util.assertNotNull;

public class TestManageBadgesEndpoint extends AbstractServiceTest {

  private static final Log LOG = ExoLogger.getLogger(TestManageBadgesEndpoint.class);

  protected Class<?> getComponentClass() {
    return ManageBadgesEndpoint.class;
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    startSessionAs("root1");
    registry(getComponentClass());
  }

  /**
   * Testing get All badges
   **/
  @Test
  public void testGetAllBadges() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("badges/all"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  /**
   * Testing the add of a new badge with the Media Type
   **/
  @Test
  public void testAddBadge() throws Exception {
    DomainDTO domainDTO = newDomainDTO();
    JSONObject domainData = new JSONObject();
    domainData.put("id", domainDTO.getId());
    domainData.put("title", domainDTO.getTitle());

    JSONObject data = new JSONObject();
    data.put("title", "foo");
    data.put("description", "description");
    data.put("domainDTO", domainData);

    ContainerResponse response = getResponse("POST", getURLResource("/badges/add"), data.toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    BadgeDTO entity = (BadgeDTO) response.getEntity();
    assertEquals("foo", entity.getTitle());
    assertEquals("description", entity.getDescription());
    assertEquals(GAMIFICATION_DOMAIN, entity.getDomainDTO().getTitle());
  }

  /**
   * Testing the add of delete of badge with the Media Type
   **/
  @Test
  public void testDeleteBadge() throws Exception {
    BadgeEntity badgeEntity = newBadge();
    ContainerResponse response = getResponse("DELETE", getURLResource("badges/delete/" + badgeEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  /**
   * Testing the add of delete of badge with the Media Type
   **/
  @Test
  public void testUpdateBadge() throws Exception {
    BadgeEntity badgeEntity = newBadge();
    DomainDTO domainDTO = newDomainDTO();
    JSONObject domainData = new JSONObject();
    domainData.put("id", domainDTO.getId());
    domainData.put("title", domainDTO.getTitle());

    JSONObject data = new JSONObject();
    data.put("id", badgeEntity.getId());
    data.put("title", badgeEntity.getTitle());
    data.put("description", badgeEntity.getDescription() + "_test");
    data.put("domainDTO", domainData);

    ContainerResponse response = getResponse("PUT", getURLResource("badges/update"), data.toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    BadgeDTO entity = (BadgeDTO) response.getEntity();
    assertEquals(entity.getDescription(), "Description_test");
  }
}

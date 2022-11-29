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


import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

public class TestLeaderboardEndpoint extends AbstractServiceTest {

  private static final Log LOG = ExoLogger.getLogger(TestLeaderboardEndpoint.class);

  protected Class<?> getComponentClass() {
    return LeaderboardEndpoint.class;
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    startSessionAs("root1");
    registry(getComponentClass());
    newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
    newGamificationActionsHistoryDTO();
  }

  @Test
  public void testGetAllLeadersByRank() {

    try {
      String restPath = "/gamification/leaderboard/rank/all?period=MONTH";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }

  }
  @Test
  public void testFilter() {

    try {
      String restPath = "/gamification/leaderboard/filter?period=MONTH";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }
  @Test
  public void testStats() {

    try {
      String restPath = "/gamification/leaderboard/stats?period=MONTH&userSocialId=";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }
}

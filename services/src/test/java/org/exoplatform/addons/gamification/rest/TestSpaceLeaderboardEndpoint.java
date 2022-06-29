package org.exoplatform.addons.gamification.rest;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.rest.SpaceLeaderboardEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

public class TestSpaceLeaderboardEndpoint extends AbstractServiceTest {

  protected Class<?> getComponentClass() {
    return SpaceLeaderboardEndpoint.class;
  }

  @Before
  @Override
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
      String restPath = "/gamification/space/leaderboard/overall?period=MONTH";
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
      String restPath = "/gamification/space/leaderboard/filter?period=MONTH&capacity=10";
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

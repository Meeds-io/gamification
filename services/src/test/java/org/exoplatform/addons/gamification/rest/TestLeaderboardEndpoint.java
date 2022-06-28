package org.exoplatform.addons.gamification.rest;


import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.After;
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

  @After
  public void tearDown() {
    super.tearDown();
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

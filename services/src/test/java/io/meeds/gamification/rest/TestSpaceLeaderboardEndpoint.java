package io.meeds.gamification.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import io.meeds.gamification.test.AbstractServiceTest;


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
    newRealizationDTO();
    newRealizationDTO();
    newRealizationDTO();
  }

  @Test
  public void testGetAllLeadersByRank() throws Exception {
    String restPath = "/gamification/space/leaderboard/overall?period=MONTH";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testFilter() throws Exception {
    String restPath = "/gamification/space/leaderboard/filter?period=MONTH&capacity=10";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }
}

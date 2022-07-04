package org.exoplatform.addons.gamification.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

public class TestUserReputationEndpoint extends AbstractServiceTest {

  protected Class<?> getComponentClass() {
    return UserReputationEndpoint.class;
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    startSessionAs("root1");
    registry(getComponentClass());
    newBadge();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
  }

  @Test
  public void testGetReputationStatus() throws Exception {
    String restPath = "/gamification/reputation/status?username=root1";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetUserBadges() throws Exception {
    String restPath = "/gamification/reputation/badges/1";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    restPath = "/gamification/reputation/badges";
    envctx = new EnvironmentContext();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetBadgeAvatarById() throws Exception {
    String restPath = "/gamification/reputation/badge/" + 5 + "/avatar";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetAllBadges() throws Exception {
    String restPath = "/gamification/reputation/won";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetOtherBadges() throws Exception {
    newBadgeWithScore(160);
    String restPath = "/gamification/reputation/otherBadges";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testStats() throws Exception {
    String restPath = "/gamification/reputation/stats";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetAllOfBadges() throws Exception {
    String restPath = "/gamification/reputation/AllofBadges";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }
}

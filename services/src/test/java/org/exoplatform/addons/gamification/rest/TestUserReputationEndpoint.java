package org.exoplatform.addons.gamification.rest;

import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import static org.mockito.ArgumentMatchers.any;

public class TestUserReputationEndpoint extends AbstractServiceTest {

  protected Class<?> getComponentClass() {
    return UserReputationEndpoint.class;
  }


  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startSessionAs("root1");
    registry(getComponentClass());
    newBadge();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    newGamificationActionsHistory();
  }

  @Test
  public void testGetReputationStatus() {
    try {
      String restPath = "/gamification/reputation/status?username=root1";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetUserBadges() {
    try {
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
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetBadgeAvatarById() {
    try {
      String restPath = "/gamification/reputation/badge/"+ 5 +"/avatar";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }
  @Test
  public void testGetAllBadges() {
    try {
      String restPath = "/gamification/reputation/won";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetOtherBadges() {
    newBadgeWithScore(160);
    try {
      String restPath = "/gamification/reputation/otherBadges";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }
  @Test
  public void testStats() {
    try {
      String restPath = "/gamification/reputation/stats";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }
  @Test
  public void testGetAllOfBadges() {
    try {
      String restPath = "/gamification/reputation/AllofBadges";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse response = launcher.service("GET", restPath, "", headers, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }
}

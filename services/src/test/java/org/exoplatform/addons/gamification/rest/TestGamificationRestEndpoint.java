package org.exoplatform.addons.gamification.rest;

import org.apache.commons.lang.time.DateUtils;
import org.exoplatform.addons.gamification.rest.GamificationRestEndpoint;
import org.exoplatform.addons.gamification.rest.SpaceLeaderboardEndpoint;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TestGamificationRestEndpoint extends AbstractServiceTest {
  protected Class<?> getComponentClass() {
    return GamificationRestEndpoint.class;
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
  public void testGetAllPointsByUserId() {
    try {
      String restPath = "/gamification/api/v1/points?userId=root1&period=MONTH";
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      assertEquals(150, ((GamificationRestEndpoint.GamificationPoints)response.getEntity()).getPoints().longValue());

      restPath = "/gamification/api/v1/points?userId=root1&period=WEEK";
      envctx = new EnvironmentContext();
      httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      assertEquals(150, ((GamificationRestEndpoint.GamificationPoints)response.getEntity()).getPoints().longValue());

      restPath = "/gamification/api/v1/points?period=MONTH";
      envctx = new EnvironmentContext();
      httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      assertEquals("2", ((GamificationRestEndpoint.GamificationPoints)response.getEntity()).getCode());
    } catch (Exception e) {
      fail();
    }
  }
  @Test
  public void testGetAllPointsByUserIdByDate() {
    Date today = new Date();
    Date lastWeekDate = DateUtils.addDays(today, -7);
    Date nextWeekDate = DateUtils.addDays(today, 7);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String lastWeek = dateTimeFormatter.format(lastWeekDate.toInstant().atOffset(ZoneOffset.UTC));
    String nextWeek = dateTimeFormatter.format(nextWeekDate.toInstant().atOffset(ZoneOffset.UTC));
    try {
      String restPath = "/gamification/api/v1/points/date?userId=&startDate=" + lastWeek + "&endDate=" + nextWeek;
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());

      restPath = "/gamification/api/v1/points/date?userId=root1&startDate=" + lastWeek + "&endDate=" + nextWeek;
      envctx = new EnvironmentContext();
      httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());

      restPath = "/gamification/api/v1/points/date?userId=root1";
      envctx = new EnvironmentContext();
      httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetLeaderboardByDate() {
    Date today = new Date();
    Date lastWeekDate = DateUtils.addDays(today, -7);
    Date nextWeekDate = DateUtils.addDays(today, 7);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String lastWeek = dateTimeFormatter.format(lastWeekDate.toInstant().atOffset(ZoneOffset.UTC));
    String nextWeek = dateTimeFormatter.format(nextWeekDate.toInstant().atOffset(ZoneOffset.UTC));
    try {
      String restPath = "/gamification/api/v1/leaderboard/date?earnerType=&startDate=" + lastWeek + "&endDate=" + nextWeek;
      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      assertEquals(1, ((GamificationRestEndpoint.GamificationPoints)response.getEntity()).getLeaderboard().size());

      restPath = "/gamification/api/v1/leaderboard/date?earnerType=USER&startDate=" + lastWeek + "&endDate=" + nextWeek;
      envctx = new EnvironmentContext();
      httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      assertEquals(1, ((GamificationRestEndpoint.GamificationPoints)response.getEntity()).getLeaderboard().size());

      restPath = "/gamification/api/v1/leaderboard/date?earnerType=USER";
      envctx = new EnvironmentContext();
      httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("root"));
      response = launcher.service("GET", restPath, "", null, null, envctx);
      assertNotNull(response);
      assertEquals(200, response.getStatus());
      assertEquals("2", ((GamificationRestEndpoint.GamificationPoints)response.getEntity()).getCode());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetDomains() {
    try {
      String restPath = "/gamification/api/v1/domains";
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
  public void testGetAllEvents() {
    try {
      String restPath = "/gamification/api/v1/events";
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

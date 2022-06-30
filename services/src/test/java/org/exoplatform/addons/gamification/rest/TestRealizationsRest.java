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

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.List;

public class TestRealizationsRest extends AbstractServiceTest {

  protected Class<?> getComponentClass() {
    return RealizationsRest.class;
  }

  protected static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                           // NOSONAR

  protected static final String fromDate        = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));

  protected static final String toDate          = Utils.toRFC3339Date(new Date(System.currentTimeMillis() + +MILLIS_IN_A_DAY));

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    registry(getComponentClass());
    startSessionAs("root");
  }

  @Test
  public void testGetAllRealizations() throws Exception {
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&offset=-1&limit=10";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&offset=0&limit=-10";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath =
             "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate + "&offset=0&limit=10";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<GamificationActionsHistoryRestEntity> realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(0, realizations.size());
    // add new realization
    newGamificationActionsHistory();
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(1, realizations.size());
  }

  @Test
  public void testGetReport() throws Exception {
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    String restPath = "/gamification/realizations/api/getExport?fromDate=" + fromDate + "&toDate=" + toDate;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testUpdateRealizations() throws Exception {
    GamificationActionsHistoryDTO gHistory = newGamificationActionsHistoryDTO();
    String restPath = "/gamification/realizations/api/updateRealizations?realizationId=" + gHistory.getId() + "&status="
        + HistoryStatus.EDITED + "&actionLabel=newLabel&points=100&domain=" + gHistory.getDomain();
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("PUT", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    GamificationActionsHistoryRestEntity realizations = (GamificationActionsHistoryRestEntity) response.getEntity();
    assertEquals(100, (long) realizations.getScore());
    assertEquals(HistoryStatus.EDITED.name(), realizations.getStatus());

    restPath = "/gamification/realizations/api/updateRealizations?realizationId=" + gHistory.getId() + "&status="
        + HistoryStatus.REJECTED + "&actionLabel=&points=0&domain=";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("PUT", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (GamificationActionsHistoryRestEntity) response.getEntity();
    assertEquals(HistoryStatus.REJECTED.name(), realizations.getStatus());
  }

}

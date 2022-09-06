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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestRealizationsRest extends AbstractServiceTest { // NOSONAR

  protected Class<?> getComponentClass() {
    return RealizationsRest.class;
  }

  protected static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                                                         // NOSONAR

  protected static final String FROM_DATE       = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));

  protected static final String TO_DATE         = Utils.toRFC3339Date(new Date(System.currentTimeMillis() + +MILLIS_IN_A_DAY));

  protected static final String JSON_TYPE       = "json";

  protected static final String XLSX_TYPE        = "xlsx";

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    registry(getComponentClass());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetAllRealizationsDefaultSort() throws Exception {
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId="
        + 1L + "&offset=-1&limit=10" + "&returnType=" + JSON_TYPE;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();

    startSessionAs("root1");

    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId=" + 1L
        + "&offset=0&limit=-10" + "&returnType=" + JSON_TYPE;
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId=" + 1L
        + "&offset=0&limit=10" + "&returnType=" + JSON_TYPE;
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

  @SuppressWarnings("unchecked")
  @Test
  public void testGetAllRealizationsSortByDateDescending() throws Exception {
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId="
        + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();

    startSessionAs("root1");

    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<GamificationActionsHistoryRestEntity> realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(0, realizations.size());

    // add new realization
    List<GamificationActionsHistory> createdActionHistories = new ArrayList<>();
    for (int i = 0; i < limit * 2; i++) {
      createdActionHistories.add(newGamificationActionsHistory());
    }
    Collections.reverse(createdActionHistories);

    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(GamificationActionsHistory::getId)
                                       .collect(Collectors.toList()),
                 realizations.stream().map(GamificationActionsHistoryRestEntity::getId).collect(Collectors.toList()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetAllRealizationsSortByDateAscending() throws Exception {
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId="
        + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=false" + "&returnType=" + JSON_TYPE;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<GamificationActionsHistoryRestEntity> realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(0, realizations.size());

    // add new realization
    List<GamificationActionsHistory> createdActionHistories = new ArrayList<>();
    for (int i = 0; i < limit * 2; i++) {
      createdActionHistories.add(newGamificationActionsHistory());
    }

    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(GamificationActionsHistory::getId)
                                       .collect(Collectors.toList()),
                 realizations.stream().map(GamificationActionsHistoryRestEntity::getId).collect(Collectors.toList()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetAllRealizationsSortByActionTypeDescending() throws Exception {
    RuleEntity rule1Automatic = newRule("testGetAllRealizationsSortByActionTypeDescending1", "domain1", true, TypeRule.AUTOMATIC);
    RuleEntity rule2Manual = newRule("testGetAllRealizationsSortByActionTypeDescending2", "domain2", true, TypeRule.MANUAL);

    // add new realization
    List<GamificationActionsHistory> createdActionHistories = new ArrayList<>();
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newGamificationActionsHistoryToBeSorted(rule2Manual.getEvent(), rule2Manual.getId()));
    }
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newGamificationActionsHistoryToBeSorted(rule1Automatic.getEvent(), rule1Automatic.getId()));
    }

    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId="
        + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();

    startSessionAs("root1");

    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<GamificationActionsHistoryRestEntity> realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(GamificationActionsHistory::getId)
                                       .collect(Collectors.toList()),
                 realizations.stream().map(GamificationActionsHistoryRestEntity::getId).collect(Collectors.toList()));

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId=" + 1L
        + "&offset=0&limit=" + createdActionHistories.size() + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(createdActionHistories.size(), realizations.size());
    assertEquals(createdActionHistories.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()),
                 realizations.stream().map(GamificationActionsHistoryRestEntity::getId).collect(Collectors.toList()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetAllRealizationsSortByActionTypeAscending() throws Exception {
    RuleEntity rule1Automatic = newRule("testGetAllRealizationsSortByActionTypeDescending1", "domain1", true, TypeRule.AUTOMATIC);
    RuleEntity rule2Manual = newRule("testGetAllRealizationsSortByActionTypeDescending2", "domain2", true, TypeRule.MANUAL);

    // add new realization
    List<GamificationActionsHistory> createdActionHistories = new ArrayList<>();
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newGamificationActionsHistoryToBeSorted(rule1Automatic.getEvent(), rule1Automatic.getId()));
    }
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newGamificationActionsHistoryToBeSorted(rule2Manual.getEvent(), rule2Manual.getId()));
    }

    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId="
        + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<GamificationActionsHistoryRestEntity> realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(GamificationActionsHistory::getId)
                                       .collect(Collectors.toList()),
                 realizations.stream().map(GamificationActionsHistoryRestEntity::getId).collect(Collectors.toList()));

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId=" + 1L
        + "&offset=0&limit=" + createdActionHistories.size() + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(createdActionHistories.size(), realizations.size());
    assertEquals(createdActionHistories.stream().map(GamificationActionsHistory::getId).collect(Collectors.toList()),
                 realizations.stream().map(GamificationActionsHistoryRestEntity::getId).collect(Collectors.toList()));
  }

  @Test
  public void testGetReport() throws Exception {
    startSessionAs("root1");

    GamificationActionsHistory history1 = newGamificationActionsHistory();
    GamificationActionsHistory history2 = newGamificationActionsHistory();
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerId="
        + 1L + "&returnType=" + XLSX_TYPE;
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    InputStream inputStream = (InputStream) response.getEntity();
    assertNotNull(inputStream);
    Workbook workbook = WorkbookFactory.create(inputStream);
    assertNotNull(workbook);
    Sheet sheet = workbook.getSheetAt(0);
    assertNotNull(sheet);
    Row row1 = sheet.getRow(1);
    assertNotNull(row1);
    Row row2 = sheet.getRow(2);
    assertNotNull(row2);

    assertEquals(Utils.toRFC3339Date(history2.getCreatedDate()), row1.getCell(0).getStringCellValue());
    assertEquals(Utils.toRFC3339Date(history1.getCreatedDate()), row2.getCell(0).getStringCellValue());
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

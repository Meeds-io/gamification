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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.RealizationsServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.social.core.manager.IdentityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestRealizationsRest extends AbstractServiceTest {

  protected Class<?> getComponentClass() {
    return RealizationsRest.class;
  }

  protected static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                           // NOSONAR

  protected static final String fromDate        = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));

  protected static final String toDate          = Utils.toRFC3339Date(new Date(System.currentTimeMillis() + +MILLIS_IN_A_DAY));
  
  @Mock
  IdentityManager identityManager;
  
  @Mock
  RealizationsStorage realizationsStorage;
  
  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    registry(getComponentClass());
  }

  @Test
  public void testGetAllRealizationsDefaultSort() throws Exception {
    RealizationsServiceImpl realizationsServiceImpl = new RealizationsServiceImpl(realizationsStorage, identityManager);
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L +"&offset=-1&limit=10";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();

    Identity root = new Identity("root1");
    ConversationState.setCurrent(new ConversationState(root));

    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L + "&offset=0&limit=-10";
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath =
             "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate + "&earnerId=" + 1L + "&offset=0&limit=10";
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
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    
    Identity root = new Identity("root1");
    ConversationState.setCurrent(new ConversationState(root));
    
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
                 realizations.stream()
                             .map(GamificationActionsHistoryRestEntity::getId)
                             .collect(Collectors.toList()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetAllRealizationsSortByDateAscending() throws Exception {
    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=false";
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
                 realizations.stream()
                             .map(GamificationActionsHistoryRestEntity::getId)
                             .collect(Collectors.toList()));
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

    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    
    Identity root = new Identity("root1");
    ConversationState.setCurrent(new ConversationState(root));
    
    ContainerResponse response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<GamificationActionsHistoryRestEntity> realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(GamificationActionsHistory::getId)
                                       .collect(Collectors.toList()),
                 realizations.stream()
                             .map(GamificationActionsHistoryRestEntity::getId)
                             .collect(Collectors.toList()));

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L + "&offset=0&limit=" + createdActionHistories.size() + "&sortBy=date&sortDescending=true";
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(createdActionHistories.size(), realizations.size());
    assertEquals(createdActionHistories.stream()
                                       .map(GamificationActionsHistory::getId)
                                       .collect(Collectors.toList()),
                 realizations.stream()
                             .map(GamificationActionsHistoryRestEntity::getId)
                             .collect(Collectors.toList()));
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

    String restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true";
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
                 realizations.stream()
                             .map(GamificationActionsHistoryRestEntity::getId)
                             .collect(Collectors.toList()));

    restPath = "/gamification/realizations/api/allRealizations?fromDate=" + fromDate + "&toDate=" + toDate
        + "&earnerId=" + 1L + "&offset=0&limit=" + createdActionHistories.size() + "&sortBy=date&sortDescending=true";
    response = launcher.service("GET", restPath, "", h, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizations = (List<GamificationActionsHistoryRestEntity>) response.getEntity();
    assertEquals(createdActionHistories.size(), realizations.size());
    assertEquals(createdActionHistories.stream()
                                       .map(GamificationActionsHistory::getId)
                                       .collect(Collectors.toList()),
                 realizations.stream()
                             .map(GamificationActionsHistoryRestEntity::getId)
                             .collect(Collectors.toList()));
  }

  @Test
  public void testGetReport() throws Exception {
    newGamificationActionsHistory();
    newGamificationActionsHistory();
    String restPath = "/gamification/realizations/api/getExport?fromDate=" + fromDate + "&toDate=" + toDate  + "&earnerId=" + 1L;
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

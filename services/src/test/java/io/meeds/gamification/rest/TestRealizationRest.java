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
package io.meeds.gamification.rest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.rest.model.RealizationList;
import io.meeds.gamification.rest.model.RealizationRestEntity;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class TestRealizationRest extends AbstractServiceTest { // NOSONAR

  protected Class<?> getComponentClass() {
    return RealizationRest.class;
  }

  protected static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                                                        // NOSONAR

  protected static final String FROM_DATE       = URLEncoder.encode(Utils.toRFC3339Date(new Date(System.currentTimeMillis())),
                                                                    StandardCharsets.UTF_8);

  protected static final String TO_DATE         = URLEncoder.encode(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
      + MILLIS_IN_A_DAY)),
                                                                    StandardCharsets.UTF_8);

  protected static final String JSON_TYPE       = "json";

  protected static final String XLSX_TYPE       = "xlsx";

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    registry(getComponentClass());
  }

  @Test
  public void testGetAllRealizationsDefaultSort() throws Exception {
    String restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=-1&limit=10" + "&returnType=" + JSON_TYPE;

    startSessionAs("root1");

    ContainerResponse response = getResponse("GET", getURLResource(restPath), null);

    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=-10" + "&returnType=" + JSON_TYPE;

    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=10" + "&returnType=" + JSON_TYPE;

    response = getResponse("GET", getURLResource(restPath), null);

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RealizationList realizationList = (RealizationList) response.getEntity();
    List<RealizationRestEntity> realizations = realizationList.getRealizations();
    assertEquals(0, realizations.size());
    // add new realization
    ProgramEntity domainEntity = newDomain();
    newRealizationEntity("rule", domainEntity.getId());
    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    realizations = realizationList.getRealizations();
    assertEquals(1, realizations.size());

    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=10" + "&returnType=" + JSON_TYPE + "&returnSize=true";

    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    int realizationCount = realizationList.getSize();
    realizations = realizationList.getRealizations();
    assertNotNull(realizations);
    assertEquals(1, realizationCount);

    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=10" + "&returnType=fake&returnSize=true";

    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    startSessionAs("root10");
    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=10" + "&returnType=" + JSON_TYPE + "&returnSize=true";

    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    assertNotNull(realizationList);
    assertEquals(1, realizationList.getSize());
    assertEquals(1, realizationList.getRealizations().size());

    startSessionAs("notMemberUser");
    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=10" + "&returnType=" + JSON_TYPE + "&returnSize=true";

    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    assertNotNull(realizationList);
    assertEquals(0, realizationList.getSize());
    assertEquals(0, realizationList.getRealizations().size());
  }

  @Test
  public void testGetAllRealizationsSortByDateDescending() throws Exception {
    String restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;

    startSessionAs("root1");

    ContainerResponse response = getResponse("GET", getURLResource(restPath), null);

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RealizationList realizationList = (RealizationList) response.getEntity();
    assertEquals(0, realizationList.getRealizations().size());

    // add new realization
    List<RealizationEntity> createdActionHistories = new ArrayList<>();
    ProgramEntity domainEntity = newDomain();
    for (int i = 0; i < limit * 2; i++) {
      createdActionHistories.add(newRealizationEntity("rule", domainEntity.getId()));
    }
    Collections.reverse(createdActionHistories);

    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    assertEquals(limit, realizationList.getRealizations().size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(RealizationEntity::getId)
                                       .toList(),
                 realizationList.getRealizations()
                                .stream()
                                .map(RealizationRestEntity::getId)
                                .toList());
  }

  @Test
  public void testGetAllRealizationsSortByDateAscending() throws Exception {
    String restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=false" + "&returnType=" + JSON_TYPE;

    ContainerResponse response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RealizationList realizationList = (RealizationList) response.getEntity();
    List<RealizationRestEntity> realizations = realizationList.getRealizations();
    assertEquals(0, realizations.size());

    startSessionAs("root1");
    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    realizations = realizationList.getRealizations();
    assertEquals(0, realizations.size());

    // add new realization
    List<RealizationEntity> createdActionHistories = new ArrayList<>();
    ProgramEntity domainEntity = newDomain();
    for (int i = 0; i < limit * 2; i++) {
      createdActionHistories.add(newRealizationEntity("rule", domainEntity.getId()));
    }

    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    realizations = realizationList.getRealizations();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(RealizationEntity::getId)
                                       .toList(),
                 realizations.stream().map(RealizationRestEntity::getId).toList());
  }

  @Test
  public void testGetAllRealizationsSortByActionTypeDescending() throws Exception {
    RuleEntity rule1Automatic =
                              newRule("testGetAllRealizationsSortByActionTypeDescending1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Manual = newRule("testGetAllRealizationsSortByActionTypeDescending2", "domain2", true, EntityType.MANUAL);

    // add new realization
    List<RealizationEntity> createdActionHistories = new ArrayList<>();
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newRealizationEntityWithRuleId(rule2Manual.getEvent(), rule2Manual.getId()));
    }
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newRealizationEntityWithRuleId(rule1Automatic.getEvent(), rule1Automatic.getId()));
    }

    String restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;

    startSessionAs("root1");

    ContainerResponse response = getResponse("GET", getURLResource(restPath), null);

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RealizationList realizationList = (RealizationList) response.getEntity();
    List<RealizationRestEntity> realizations = realizationList.getRealizations();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(RealizationEntity::getId)
                                       .toList(),
                 realizations.stream().map(RealizationRestEntity::getId).toList());

    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=" + createdActionHistories.size() + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;
    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    realizations = realizationList.getRealizations();
    assertEquals(createdActionHistories.size(), realizations.size());
    assertEquals(createdActionHistories.stream().map(RealizationEntity::getId).toList(),
                 realizations.stream().map(RealizationRestEntity::getId).toList());
  }

  @Test
  public void testGetAllRealizationsSortByActionTypeAscending() throws Exception {
    RuleEntity rule1Automatic =
                              newRule("testGetAllRealizationsSortByActionTypeDescending1", "domain1", true, EntityType.AUTOMATIC);
    RuleEntity rule2Manual = newRule("testGetAllRealizationsSortByActionTypeDescending2", "domain2", true, EntityType.MANUAL);

    // add new realization
    List<RealizationEntity> createdActionHistories = new ArrayList<>();
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newRealizationEntityWithRuleId(rule1Automatic.getEvent(), rule1Automatic.getId()));
    }
    for (int i = 0; i < limit; i++) {
      createdActionHistories.add(0, newRealizationEntityWithRuleId(rule2Manual.getEvent(), rule2Manual.getId()));
    }

    String restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=" + limit + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;

    startSessionAs("root1");

    ContainerResponse response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    RealizationList realizationList = (RealizationList) response.getEntity();
    List<RealizationRestEntity> realizations = realizationList.getRealizations();
    assertEquals(limit, realizations.size());
    assertEquals(createdActionHistories.subList(0, limit)
                                       .stream()
                                       .map(RealizationEntity::getId)
                                       .toList(),
                 realizations.stream().map(RealizationRestEntity::getId).toList());

    restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&offset=0&limit=" + createdActionHistories.size() + "&sortBy=date&sortDescending=true" + "&returnType=" + JSON_TYPE;
    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    realizationList = (RealizationList) response.getEntity();
    realizations = realizationList.getRealizations();
    assertEquals(createdActionHistories.size(), realizations.size());
    assertEquals(createdActionHistories.stream().map(RealizationEntity::getId).toList(),
                 realizations.stream().map(RealizationRestEntity::getId).toList());
  }

  @Test
  public void testGetReport() throws Exception {
    startSessionAs("root1");
    ProgramEntity domainEntity = newDomain();
    RealizationEntity history1 = newRealizationEntity("rule", domainEntity.getId());
    RealizationEntity history2 = newRealizationEntity("rule", domainEntity.getId());
    String restPath = "realizations?fromDate=" + FROM_DATE + "&toDate=" + TO_DATE + "&earnerIds=1"
        + "&returnType=" + XLSX_TYPE;

    EnvironmentContext environmentContext = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    environmentContext.put(HttpServletRequest.class, httpRequest);
    environmentContext.put(SecurityContext.class, new MockSecurityContext("root"));
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ContainerResponse response = launcher.service("GET", getURLResource(restPath), "", h, null, environmentContext);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    InputStream inputStream = (InputStream) response.getEntity();
    assertNotNull(inputStream);
    Workbook workbook = WorkbookFactory.create(inputStream);
    assertNotNull(workbook);
    Sheet sheet = workbook.getSheetAt(0);
    assertNotNull(sheet);
    assertEquals(2, sheet.getLastRowNum());
    Row row1 = sheet.getRow(1);
    assertNotNull(row1);
    Row row2 = sheet.getRow(2);
    assertNotNull(row2);

    assertEquals(Utils.toRFC3339Date(history2.getCreatedDate()), row1.getCell(0).getStringCellValue());
    assertEquals(Utils.toRFC3339Date(history1.getCreatedDate()), row2.getCell(0).getStringCellValue());
  }

  @Test
  public void testUpdateRealization() throws Exception {
    RealizationDTO gHistory = newRealizationDTO();
    Long realizationId = gHistory.getId();
    RealizationDTO realization = realizationService.getRealizationById(realizationId);
    assertEquals(RealizationStatus.ACCEPTED.name(), realization.getStatus());

    startSessionAs("root1");
    ContainerResponse response = getPatchResponse(realizationId, RealizationStatus.REJECTED);
    assertNotNull(response);
    assertEquals(204, response.getStatus());
    realization = realizationService.getRealizationById(realizationId);
    assertEquals(RealizationStatus.REJECTED.name(), realization.getStatus());

    response = getPatchResponse(realizationId, RealizationStatus.CANCELED);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    realization = realizationService.getRealizationById(realizationId);
    assertEquals(RealizationStatus.REJECTED.name(), realization.getStatus());

    response = getPatchResponse(realizationId, RealizationStatus.DELETED);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    realization = realizationService.getRealizationById(realizationId);
    assertEquals(RealizationStatus.REJECTED.name(), realization.getStatus());

    response = getPatchResponse(realizationId, RealizationStatus.ACCEPTED);
    assertNotNull(response);
    assertEquals(204, response.getStatus());
    realization = realizationService.getRealizationById(realizationId);
    assertEquals(RealizationStatus.ACCEPTED.name(), realization.getStatus());

    response = getPatchResponse(5000l, RealizationStatus.REJECTED);
    assertNotNull(response);
    assertEquals(404, response.getStatus());
    realization = realizationService.getRealizationById(realizationId);
    assertEquals(RealizationStatus.ACCEPTED.name(), realization.getStatus());

    startSessionAs("root10");
    response = getPatchResponse(realizationId, RealizationStatus.REJECTED);
    assertNotNull(response);
    assertEquals(401, response.getStatus());
  }

  @Test
  public void testGetAllPointsByUserId() throws Exception {
    newRealizationDTO();

    String restPath = "/gamification/realizations/points?userId=root1&period=MONTH";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    ContainerResponse response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals(TEST__SCORE, response.getEntity().toString());

    restPath = "/gamification/realizations/points?userId=root1&period=WEEK";
    envctx = new EnvironmentContext();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals(TEST__SCORE, response.getEntity().toString());

    restPath = "/gamification/realizations/points?period=MONTH";
    envctx = new EnvironmentContext();
    httpRequest = new MockHttpServletRequest(restPath, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("root"));
    response = launcher.service("GET", restPath, "", null, null, envctx);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
  }

  private ContainerResponse getPatchResponse(long realizationId, RealizationStatus status) throws Exception {
    byte[] formData = ("id=" + realizationId + "&status=" + status).getBytes();
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    headers.putSingle("Content-Type", "application/x-www-form-urlencoded");
    return launcher.service("PATCH", getURLResource("realizations"), "", headers, formData, null);
  }

}

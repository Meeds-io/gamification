/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.rest;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.rest.model.LeaderboardInfo;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

@SuppressWarnings("unchecked")
public class TestLeaderboardRest extends AbstractServiceTest {// NOSONAR

  private static final String BASE_URL = "leaderboard";

  private long                programId1;

  private long                programId2;

  private long                ruleId1;

  private long                ruleId2;

  private long                ruleId3;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    registry(LeaderboardRest.class);
  }

  @Test
  public void testGetLeaderboardResultDetails() throws Exception {
    populateData();

    ContainerResponse response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=10"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    List<LeaderboardInfo> leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(2, leaderboardInfos.size());

    LeaderboardInfo leaderboardInfo = leaderboardInfos.get(0);
    assertNotNull(leaderboardInfo);
    assertNull(leaderboardInfo.getProfileUrl());
    assertNull(leaderboardInfo.getRemoteId());
    assertNull(leaderboardInfo.getTechnicalId());
    assertNotNull(leaderboardInfo.getFullname());
    assertEquals(1, leaderboardInfo.getRank());
    assertEquals(200, leaderboardInfo.getScore());
    assertEquals(1, leaderboardInfo.getIdentityId());

    leaderboardInfo = leaderboardInfos.get(1);
    assertNotNull(leaderboardInfo);
    assertNull(leaderboardInfo.getProfileUrl());
    assertNull(leaderboardInfo.getRemoteId());
    assertNull(leaderboardInfo.getTechnicalId());
    assertNotNull(leaderboardInfo.getFullname());
    assertEquals(2, leaderboardInfo.getRank());
    assertEquals(150, leaderboardInfo.getScore());
    assertEquals(2, leaderboardInfo.getIdentityId());

    startSessionAs("root1");
    response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=10"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(2, leaderboardInfos.size());

    leaderboardInfo = leaderboardInfos.get(0);
    assertNotNull(leaderboardInfo);
    assertNotNull(leaderboardInfo.getRemoteId());
    assertNotNull(leaderboardInfo.getFullname());
    assertEquals(1, leaderboardInfo.getRank());
    assertEquals(200, leaderboardInfo.getScore());
    assertEquals(1, leaderboardInfo.getIdentityId());

    leaderboardInfo = leaderboardInfos.get(1);
    assertNotNull(leaderboardInfo);
    assertNotNull(leaderboardInfo.getRemoteId());
    assertNotNull(leaderboardInfo.getFullname());
    assertEquals(2, leaderboardInfo.getRank());
    assertEquals(150, leaderboardInfo.getScore());
    assertEquals(2, leaderboardInfo.getIdentityId());
  }

  @Test
  public void testGetLeaderboard() throws Exception {
    populateData();

    ContainerResponse response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=10"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    List<LeaderboardInfo> leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(2, leaderboardInfos.size());

    response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=0"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    
    leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(0, leaderboardInfos.size());

    startSessionAs("root1");
    response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=0"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(1, leaderboardInfos.size());

    response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=10"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(2, leaderboardInfos.size());

    resetUserSession();
    response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=10&programId=" + programId2), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=10&programId=" + programId1), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(2, leaderboardInfos.size());
  }

  @Test
  public void testGetLeaderboardByAnonymous() throws Exception {
    newRealizationDTO();
    newRealizationDTO();
    newRealizationDTO();

    ContainerResponse response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    List<LeaderboardInfo> leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(0, leaderboardInfos.size());

    response = getResponse("GET", getURLResource(BASE_URL + "?period=ALL&limit=6"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    leaderboardInfos = (List<LeaderboardInfo>) response.getEntity();
    assertNotNull(leaderboardInfos);
    assertEquals(1, leaderboardInfos.size());

    LeaderboardInfo leaderboardInfo = leaderboardInfos.get(0);
    assertNotNull(leaderboardInfo);
    assertNull(leaderboardInfo.getProfileUrl());
    assertNull(leaderboardInfo.getRemoteId());
    assertNull(leaderboardInfo.getTechnicalId());
    assertNotNull(leaderboardInfo.getFullname());
    assertEquals(1, leaderboardInfo.getRank());
    assertEquals(150, leaderboardInfo.getScore());
    assertEquals(1, leaderboardInfo.getIdentityId());
  }

  @Test
  public void testGetLeaderboardByAnonymousWhenRestricted() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource(BASE_URL + "?period=MONTH"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource(BASE_URL + "?period=MONTH&identityType=SPACE"), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    setRestrictedHubAccess();
    response = getResponse("GET", getURLResource(BASE_URL + "?period=MONTH"), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());
  }

  @Test
  public void testGetStatsByAnonymousWhenRestricted() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource(BASE_URL + "/stats/1"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    setRestrictedHubAccess();
    response = getResponse("GET", getURLResource(BASE_URL + "/stats/1"), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());
  }

  private void populateData() {
    Date createDate = new Date(System.currentTimeMillis());
    Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
    ProgramDTO program1 = new ProgramDTO();
    String programTitle1 = "program1";
    String programTitle2 = "program2";

    program1.setTitle(programTitle1);
    program1.setDescription(DESCRIPTION);
    program1.setCreatedBy(TEST_USER_EARNER);
    program1.setCreatedDate(Utils.toRFC3339Date(createDate));
    program1.setLastModifiedBy(TEST_USER_EARNER);
    program1.setDeleted(false);
    program1.setEnabled(true);
    program1.setSpaceId(Long.parseLong(TEST_SPACE2_ID));
    program1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    program1 = programService.createProgram(program1);
    programId1 = program1.getId();

    ProgramDTO program2 = new ProgramDTO();
    program2.setTitle(programTitle2);
    program2.setDescription("Description");
    program2.setCreatedBy(TEST_USER_EARNER);
    program2.setCreatedDate(Utils.toRFC3339Date(createDate));
    program2.setLastModifiedBy(TEST_USER_EARNER);
    program2.setDeleted(false);
    program2.setEnabled(true);
    program1.setSpaceId(Long.parseLong(TEST_SPACE2_ID));
    program2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    program2 = programService.createProgram(program2);
    programId2 = program2.getId();

    RuleEntity rule1 = newRule("rule1", programId1);
    ruleId1 = rule1.getId();
    RuleEntity rule2 = newRule("rule2", programId1);
    ruleId2 = rule2.getId();
    RuleEntity rule3 = newRule("rule3", programId2);
    ruleId3 = rule3.getId();

    newRealizationByRuleByEarnerId(rule1, "1");
    newRealizationByRuleByEarnerId(rule1, "2");
    newRealizationByRuleByEarnerId(rule1, "1");
    newRealizationByRuleByEarnerId(rule2, "2");
    newRealizationByRuleByEarnerId(rule2, "1");
    newRealizationByRuleByEarnerId(rule2, "2");
    newRealizationByRuleByEarnerId(rule3, "1");
  }

}

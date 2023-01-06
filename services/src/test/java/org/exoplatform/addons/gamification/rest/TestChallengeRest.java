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

import org.exoplatform.addons.gamification.rest.model.ChallengeRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TestChallengeRest extends AbstractServiceTest {
  protected Class<?> getComponentClass() {
    return ChallengeRest.class;
  }

  private static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                                                            // NOSONAR

  private static final String startDate       = Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 3 * MILLIS_IN_A_DAY));

  private static final String endDate         = Utils.toRFC3339Date(new Date(System.currentTimeMillis() - 2 * MILLIS_IN_A_DAY));

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    Identity userAclIdentity = new Identity("user", Collections.singleton(new MembershipEntry("/platform/users")));
    Identity adminAclIdentity = new Identity("root1", Collections.singleton(new MembershipEntry(Utils.REWARDING_GROUP)));
    identityRegistry.register(userAclIdentity);
    identityRegistry.register(adminAclIdentity);
    registry(getComponentClass());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testCreateChallenge() throws Exception {
    DomainDTO domain = newDomainDTO();
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("0")
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1L))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();

    ContainerResponse response = getResponse("POST", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());
    startSessionAs("root10");
    response = getResponse("POST", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());
    startSessionAs("root1");
    response = getResponse("POST", getURLResource("challenges"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());
    response = getResponse("POST", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testUpdateChallenge() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("programId")
              .value(domain.getId())
              .key("audience")
              .value("1")
              .endObject();

    ContainerResponse response = getResponse("POST", getURLResource("challenges"), writer.getBuffer().toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");
    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("0")
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();

    // null data
    response = getResponse("PUT", getURLResource("challenges"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    // challenge id 0
    response = getResponse("PUT", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(challengeRestEntity.getId())
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();

    // update with root 2
    response = getResponse("PUT", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value(challengeRestEntity.getId())
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();

    // update with root 2
    response = getResponse("PUT", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");

    response = getResponse("PUT", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    writer = new StringWriter();
    jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("id")
              .value("10")
              .key("title")
              .value("challenge updated")
              .key("description")
              .value("challenge description updated")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("100")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();

    // challenge not exists
    response = getResponse("PUT", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void testGetChallengeById() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();

    ContainerResponse response = getResponse("POST", getURLResource("challenges"), writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");

    // challenge id 0
    response = getResponse("GET", getURLResource("challenges/0"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    // challenge id 5 not exist
    response = getResponse("GET", getURLResource("challenges/555"), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    startSessionAs("root1");

    response = getResponse("GET", getURLResource("challenges/" + challengeRestEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity savedChallenge = (ChallengeRestEntity) response.getEntity();
    assertNotNull(savedChallenge);
    assertEquals(challengeRestEntity.getId(), savedChallenge.getId());
  }

  @Test
  public void testGetChallengesByUser() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    String restPath = "/gamification/challenges/";
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("enabled")
              .value(true)
              .key("audience")
              .value("1")
              .endObject();

    ContainerResponse response = getResponse("POST", restPath, writer.getBuffer().toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    response = getResponse("POST", restPath, writer.getBuffer().toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");

    restPath = "/gamification/challenges?offset=0&limit=10";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    List<ChallengeRestEntity> savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(0, savedChallenges.size());

    startSessionAs("root1");

    restPath = "/gamification/challenges?offset=0&limit=10&domainId=" + domain.getId() + "&announcements=4";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(2, savedChallenges.size());

    restPath = "/gamification/challenges?offset=0&limit=10&domainId=0&announcements=4&groupByDomain=true";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(1, savedChallenges.size());

    restPath = "/gamification/challenges?offset=0&limit=10";
    response = getResponse("GET", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedChallenges = (List<ChallengeRestEntity>) response.getEntity();
    assertEquals(2, savedChallenges.size());
  }

  @Test
  public void testDeleteChallenge() throws Exception {
    // add challenge with root1
    startSessionAs("root1");
    DomainDTO domain = newDomainDTO();
    String restPath = "/gamification/challenges/";
    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object()
              .key("title")
              .value("challenge")
              .key("description")
              .value("challenge description")
              .key("startDate")
              .value(startDate)
              .key("endDate")
              .value(endDate)
              .key("managers")
              .value(Collections.singletonList(1l))
              .key("points")
              .value("10")
              .key("program")
              .value(domain.getTitle())
              .key("audience")
              .value("1")
              .endObject();

    ContainerResponse response = getResponse("POST", restPath, writer.getBuffer().toString());

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ChallengeRestEntity challengeRestEntity = (ChallengeRestEntity) response.getEntity();
    assertNotNull(challengeRestEntity);
    startSessionAs("root2");

    // challenge id 0
    restPath = "/gamification/challenges/0";
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    // challenge id not exist
    restPath = "/gamification/challenges/1100";
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    // unauthorized user
    restPath = "/gamification/challenges/" + challengeRestEntity.getId();
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = getResponse("DELETE", restPath, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testCanAddChallenge() throws Exception {
    startSessionAs("root1");
    String restPath = "/gamification/challenges/canAddChallenge";
    ContainerResponse response = getResponse("GET", getURLResource("challenges/canAddChallenge"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }
}

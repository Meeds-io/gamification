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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.rest.model.ProgramRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.ConversationState;

public class TestProgramRest extends AbstractServiceTest { // NOSONAR

  protected Class<?> getComponentClass() {
    return ProgramRest.class;
  }

  private ProgramDTO autoDomain;

  private ProgramDTO manualDomain;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    Identity userAclIdentity = new Identity("user", Collections.singleton(new MembershipEntry("/platform/users")));
    Identity adminAclIdentity = new Identity("root1", Collections.singleton(new MembershipEntry(Utils.REWARDING_GROUP)));
    identityRegistry.register(userAclIdentity);
    identityRegistry.register(adminAclIdentity);
    autoDomain = newProgram(EntityType.AUTOMATIC, "domain1", true, Collections.singleton(1L));
    manualDomain = newProgram(EntityType.MANUAL, "domain2", true, Collections.singleton(1L));
    ConversationState.setCurrent(null);
    registry(getComponentClass());
  }

  @Test
  public void testGetAllDomains() throws Exception {

    startSessionAs("root1");

    ContainerResponse response = getResponse("GET", getURLResource("programs"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource("programs?offset=0&limit=10"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource("programs?offset=0&limit=10&type=MANUAL&returnSize=true"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testCreateDomain() throws Exception {

    ProgramDTO domain = manualDomain.clone();
    domain.setId(0);
    domain.setTitle("foo");
    domain.setDescription("fooDescription");
    JSONObject domainData = new JSONObject();
    domainData.put("id", domain.getId());
    domainData.put("title", domain.getTitle());
    domainData.put("description", domain.getDescription());

    ContainerResponse response = getResponse("POST", getURLResource("programs"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    startSessionAs("user");
    response = getResponse("POST", getURLResource("programs"), domainData.toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = getResponse("POST", getURLResource("programs"), domainData.toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    ProgramRestEntity program = (ProgramRestEntity) response.getEntity();
    assertEquals("foo", program.getTitle());
    assertEquals("fooDescription", program.getDescription());
  }

  @Test
  public void testUpdateDomain() throws Exception {
    ProgramDTO domain = autoDomain.clone();
    domain.setId(0);

    JSONObject domainData = new JSONObject();
    domainData.put("id", domain.getId());

    ContainerResponse response = getResponse("PUT", getURLResource("programs/" + domain.getId()), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("PUT", getURLResource("programs/" + domain.getId()), domainData.toString());
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    domain = autoDomain.clone();
    startSessionAs("root10");
    response = getResponse("PUT", getURLResource("programs/" + domain.getId()), domainData.toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    domain.setTitle("TeamWork modified");
    domain.setDescription("description modified");
    domainData = new JSONObject();
    domainData.put("id", domain.getId());
    domainData.put("title", domain.getTitle());
    domainData.put("description", domain.getDescription());
    startSessionAs("root1");
    response = getResponse("PUT", getURLResource("programs/" + domain.getId()), domainData.toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    ProgramRestEntity program = (ProgramRestEntity) response.getEntity();
    assertEquals("TeamWork modified", program.getTitle());
    assertEquals("description modified", program.getDescription());
  }

  @Test
  public void testDeleteDomain() throws Exception {
    ContainerResponse response = getResponse("DELETE", getURLResource("programs/0"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    startSessionAs("root10");
    response = getResponse("DELETE", getURLResource("programs/" + manualDomain.getId()), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root1");
    response = getResponse("DELETE", getURLResource("programs/" + autoDomain.getId()), null);
    assertNotNull(response);
    assertEquals(204, response.getStatus());
  }

  @Test
  public void testCanAddProgram() throws Exception {
    startSessionAs("root1");
    ContainerResponse response = getResponse("GET", getURLResource("programs/canAddProgram"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("true", response.getEntity());

    startSessionAs("user");
    response = getResponse("GET", getURLResource("programs/canAddProgram"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("false", response.getEntity());
  }

  @Test
  public void testGetDomainAvatarById() throws Exception {
    long lastUpdateCoverTime = Utils.parseRFC3339Date(manualDomain.getLastModifiedDate()).getTime();
    String token = URLEncoder.encode(Utils.generateAttachmentToken(String.valueOf(manualDomain.getId()),
                                                                   Utils.TYPE,
                                                                   lastUpdateCoverTime),
                                     StandardCharsets.UTF_8);

    ContainerResponse response = getResponse("GET",
                                             getURLResource("programs/" + Utils.DEFAULT_IMAGE_REMOTE_ID + "/cover?lastModified="
                                                 + lastUpdateCoverTime + "&r=" + token),
                                             null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource("programs/155/cover?lastModified=" + lastUpdateCoverTime + "&r=" + token), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    response = getResponse("GET",
                           getURLResource("programs/" + manualDomain.getId() + "/cover?lastModified=" + lastUpdateCoverTime + "&r="
                               + token),
                           null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET",
                           getURLResource("programs/" + manualDomain.getId() + "/cover?lastModified=" + lastUpdateCoverTime + "&r="
                               + "wrongToken"),
                           null);
    assertNotNull(response);
    assertEquals(403, response.getStatus());
  }

  @Test
  public void testGetDomainById() throws Exception {
    startSessionAs("root1");
    ContainerResponse response = getResponse("GET", getURLResource("programs/0"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource("programs/7580"), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    response = getResponse("GET", getURLResource("programs/" + autoDomain.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ProgramRestEntity savedDomain = (ProgramRestEntity) response.getEntity();
    assertNotNull(savedDomain);
    assertEquals(autoDomain.getId(), (long) savedDomain.getId());
  }
}

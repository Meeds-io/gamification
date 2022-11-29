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
import java.util.Collections;

import org.exoplatform.addons.gamification.constant.EntityType;
import org.exoplatform.addons.gamification.model.DomainDTO;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.rest.model.DomainRestEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.ConversationState;

public class TestManageDomainsEndpoint extends AbstractServiceTest { // NOSONAR

  private static final String PATH_SEPARATOR = "/";       // NOSONAR

  private static final String REST_PATH      = "domains"; // NOSONAR

  protected Class<?> getComponentClass() {
    return ManageDomainsEndpoint.class;
  }

  private DomainDTO autoDomain;

  private DomainDTO manualDomain;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    Identity adminAclIdentity = new Identity("root", Collections.singleton(new MembershipEntry(Utils.REWARDING_GROUP)));
    Identity userAclIdentity = new Identity("user", Collections.singleton(new MembershipEntry("/platform/users")));
    identityRegistry.register(adminAclIdentity);
    identityRegistry.register(userAclIdentity);
    ConversationState.setCurrent(null);
    registry(getComponentClass());
    autoDomain = newDomainDTO(EntityType.AUTOMATIC, "domain1", true, Collections.singleton(1l));
    manualDomain = newDomainDTO(EntityType.MANUAL, "domain2", true, Collections.singleton(1l));
  }

  @Test
  public void testGetAllDomains() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource(REST_PATH + "?offset=-1&limit=10"), null);
    assertNotNull(response); // NOSONAR
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource(REST_PATH + "?offset=-1&limit=10"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource(REST_PATH), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource(REST_PATH + "?offset=0&limit=10"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource(REST_PATH + "?offset=0&limit=10&type=MANUAL&returnSize=true"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testCreateDomain() throws Exception {

    DomainDTO domain = manualDomain.clone();
    domain.setTitle("foo");
    domain.setDescription("fooDescription");

    JSONObject data = new JSONObject();
    data.put("title", domain.getTitle());
    data.put("description", domain.getDescription());

    startSessionAs("root");
    ContainerResponse response = getResponse("POST", getURLResource(REST_PATH), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    startSessionAs("user");
    response = getResponse("POST", getURLResource(REST_PATH), data.toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root");
    response = getResponse("POST", getURLResource(REST_PATH), data.toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    DomainRestEntity domainDTO = (DomainRestEntity) response.getEntity();
    assertEquals("foo", domainDTO.getTitle());
    assertEquals("fooDescription", domainDTO.getDescription());
  }

  @Test
  public void testUpdateDomain() throws Exception {

    DomainDTO domain = autoDomain.clone();

    JSONObject data = new JSONObject(domain);

    startSessionAs("root");
    ContainerResponse response = getResponse("PUT", getURLResource(REST_PATH + "/" + domain.getId()), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    startSessionAs("user");
    response = getResponse("PUT", getURLResource(REST_PATH + "/" + domain.getId()), data.toString());
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    domain.setTitle("TeamWork modified");
    domain.setDescription("description modified");
    data.put("title", domain.getTitle());
    data.put("description", domain.getDescription());
    startSessionAs("root");
    response = getResponse("PUT", getURLResource(REST_PATH + "/" + domain.getId()), data.toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    DomainRestEntity domainDTO = (DomainRestEntity) response.getEntity();
    assertEquals("TeamWork modified", domainDTO.getTitle());
    assertEquals("description modified", domainDTO.getDescription());
  }

  @Test
  public void testDeleteDomain() throws Exception {

    String restPath = REST_PATH + "/0";
    ContainerResponse response = getResponse("DELETE", getURLResource(restPath), null);

    assertNotNull(response);
    assertEquals(400, response.getStatus());

    startSessionAs("user");
    restPath = REST_PATH + PATH_SEPARATOR + manualDomain.getId(); // NOSONAR
    response = getResponse("DELETE", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    startSessionAs("root");
    restPath = REST_PATH + "/1555";
    response = getResponse("DELETE", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    restPath = REST_PATH + PATH_SEPARATOR + autoDomain.getId(); // NOSONAR
    response = getResponse("DELETE", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ConversationState.setCurrent(null);
  }

  @Test
  public void testGetDomainAvatarById() throws Exception {
    long lastUpdateCoverTime = Utils.parseRFC3339Date(manualDomain.getLastModifiedDate()).getTime();
    String token = URLEncoder.encode(Utils.generateAttachmentToken(String.valueOf(manualDomain.getId()),
                                                                   Utils.TYPE,
                                                                   lastUpdateCoverTime),
                                     "UTF-8");

    String extraPath = "/cover?lastModified="; // NOSONAR
    String restPath =
                    REST_PATH + PATH_SEPARATOR + Utils.DEFAULT_IMAGE_REMOTE_ID + extraPath + lastUpdateCoverTime + "&r=" + token;

    ContainerResponse response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    restPath = REST_PATH + PATH_SEPARATOR + 155 + extraPath + lastUpdateCoverTime + "&r=" + token;
    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    restPath = REST_PATH + PATH_SEPARATOR + manualDomain.getId() + extraPath + lastUpdateCoverTime + "&r=" + token;
    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    restPath = REST_PATH + PATH_SEPARATOR + manualDomain.getId() + extraPath + lastUpdateCoverTime + "&r=" + "wrongToken";
    response = getResponse("GET", getURLResource(restPath), null);
    assertNotNull(response);
    assertEquals(403, response.getStatus());
  }

  @Test
  public void testGetDomainById() throws Exception {

    ContainerResponse response = getResponse("GET", getURLResource(REST_PATH + "/0"), null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", getURLResource(REST_PATH + "/7580"), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    response = getResponse("GET", getURLResource(REST_PATH + "/" + autoDomain.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    DomainRestEntity savedDomain = (DomainRestEntity) response.getEntity();
    assertNotNull(savedDomain);
    assertEquals((long) autoDomain.getId(), (long) savedDomain.getId());
  }
}

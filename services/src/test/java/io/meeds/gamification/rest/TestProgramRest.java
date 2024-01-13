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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;

import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.mock.SpaceServiceMock;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.UserInfoContext;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.rest.model.ProgramList;
import io.meeds.gamification.rest.model.ProgramRestEntity;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

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
    autoDomain = newProgram(EntityType.AUTOMATIC, "domain1", true, Collections.singleton(1L));
    manualDomain = newProgram(EntityType.MANUAL, "domain2", true, Collections.singleton(1L));
    ConversationState.setCurrent(null);
    registry(getComponentClass());
  }

  @Test
  public void testGetPrograms() throws Exception {
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
  public void testGetProgramAdministrators() throws Exception {
    startSessionAs("root1");

    ContainerResponse response = getResponse("GET", getURLResource("programs"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    ProgramList programList = (ProgramList) response.getEntity();
    assertNotNull(programList);
    assertNull(programList.getAdministrators());

    response = getResponse("GET", getURLResource("programs?expand=administrators"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    programList = (ProgramList) response.getEntity();
    assertNotNull(programList);
    assertNotNull(programList.getAdministrators());
  }

  @Test
  public void testCreateProgram() throws Exception {

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
    assertEquals(401, response.getStatus());

    startSessionAsAdministrator("root1");
    response = getResponse("POST", getURLResource("programs"), domainData.toString());
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    ProgramRestEntity program = (ProgramRestEntity) response.getEntity();
    assertEquals("foo", program.getTitle());
    assertEquals("fooDescription", program.getDescription());
  }

  @Test
  public void testUpdateProgram() throws Exception {
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
    startSessionAsAdministrator("root1");
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
  public void testGetProgramCoverById() throws Exception {
    long lastUpdateCoverTime = Utils.parseRFC3339Date(manualDomain.getLastModifiedDate()).getTime();
    String token = URLEncoder.encode(Utils.generateAttachmentToken(String.valueOf(manualDomain.getId()),
                                                                   Utils.ATTACHMENT_COVER_TYPE,
                                                                   lastUpdateCoverTime),
                                     StandardCharsets.UTF_8);

    ContainerResponse response = getResponse("GET",
                                             getURLResource("programs/" + Utils.DEFAULT_COVER_REMOTE_ID + "/cover?lastModified=" +
                                                 lastUpdateCoverTime + "&r=" + token),
                                             null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET", getURLResource("programs/155/cover?lastModified=" + lastUpdateCoverTime + "&r=" + token), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    response = getResponse("GET",
                           getURLResource("programs/" + manualDomain.getId() + "/cover?lastModified=" + lastUpdateCoverTime +
                               "&r=" + token),
                           null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET",
                           getURLResource("programs/" + manualDomain.getId() + "/cover?lastModified=" + lastUpdateCoverTime +
                               "&r=" + "wrongToken"),
                           null);
    assertNotNull(response);
    assertEquals(403, response.getStatus());
  }

  @Test
  public void testGetProgramAvatarById() throws Exception {
    long lastUpdateAvatarTime = Utils.parseRFC3339Date(manualDomain.getLastModifiedDate()).getTime();
    String token = URLEncoder.encode(Utils.generateAttachmentToken(String.valueOf(manualDomain.getId()),
                                                                   Utils.ATTACHMENT_AVATAR_TYPE,
                                                                   lastUpdateAvatarTime),
                                     StandardCharsets.UTF_8);

    ContainerResponse response = getResponse("GET",
                                             getURLResource("programs/" + Utils.DEFAULT_AVATAR_REMOTE_ID +
                                                 "/avatar?lastModified=" + lastUpdateAvatarTime + "&r=" + token),
                                             null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response =
             getResponse("GET", getURLResource("programs/155/avatar?lastModified=" + lastUpdateAvatarTime + "&r=" + token), null);
    assertNotNull(response);
    assertEquals(404, response.getStatus());

    response = getResponse("GET",
                           getURLResource("programs/" + manualDomain.getId() + "/avatar?lastModified=" + lastUpdateAvatarTime +
                               "&r=" + token),
                           null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    response = getResponse("GET",
                           getURLResource("programs/" + manualDomain.getId() + "/avatar?lastModified=" + lastUpdateAvatarTime +
                               "&r=" + "wrongToken"),
                           null);
    assertNotNull(response);
    assertEquals(403, response.getStatus());
  }

  @Test
  public void testGetProgramById() throws Exception {
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
    assertEquals(autoDomain.getId(), savedDomain.getId());
  }

  @Test
  public void testGetDisabledProgramOwners() throws Exception {
    ProgramEntity programEntity = newOpenProgram("openProgram");
    Identity owner1 = identityManager.getOrCreateUserIdentity("root53");
    Identity owner2 = identityManager.getOrCreateUserIdentity("root54");
    HashSet<Long> ownerIds = new HashSet<>(Arrays.asList(Long.parseLong(owner1.getId()),
                                                         Long.parseLong(owner2.getId())));
    programEntity.setOwners(ownerIds);
    programDAO.update(programEntity);
    restartTransaction();

    startSessionAsAdministrator("root1");
    ContainerResponse response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ProgramRestEntity savedProgram = (ProgramRestEntity) response.getEntity();
    assertNotNull(savedProgram);
    assertEquals(programEntity.getId().longValue(), savedProgram.getId());
    assertNotNull(savedProgram.getOwners());
    assertEquals(2, savedProgram.getOwners().size());
    assertEquals(ownerIds, savedProgram.getOwnerIds());

    owner2.setEnable(false);
    identityManager.updateIdentity(owner2);
    owner2 = identityManager.getOrCreateUserIdentity("root54");
    assertFalse(owner2.isEnable());

    response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedProgram = (ProgramRestEntity) response.getEntity();
    assertNotNull(savedProgram);
    assertEquals(programEntity.getId().longValue(), savedProgram.getId());
    assertNotNull(savedProgram.getOwners());
    assertEquals(1, savedProgram.getOwners().size());
    assertEquals(Collections.singleton(Long.parseLong(owner1.getId())), savedProgram.getOwnerIds());
  }

  @Test
  public void testGetOpenProgramById() throws Exception {
    ProgramEntity programEntity = newOpenProgram("openProgram");

    startSessionAsAdministrator("root1");
    ContainerResponse response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ProgramRestEntity savedProgram = (ProgramRestEntity) response.getEntity();
    assertNotNull(savedProgram);
    assertEquals(programEntity.getId().longValue(), savedProgram.getId());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isCanEdit());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isManager());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isMember());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isMember());

    startSessionAs("root10");
    response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedProgram = (ProgramRestEntity) response.getEntity();
    assertNotNull(savedProgram);
    assertEquals(programEntity.getId().longValue(), savedProgram.getId());
    assertFalse(((UserInfoContext) savedProgram.getUserInfo()).isCanEdit());
    assertFalse(((UserInfoContext) savedProgram.getUserInfo()).isManager());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isMember());
    assertFalse(((UserInfoContext) savedProgram.getUserInfo()).isProgramOwner());

    startExternalSessionAs("root15");
    response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    ProgramDTO program = programService.getProgramById(programEntity.getId());
    program.setOwnerIds(Collections.singleton(Long.parseLong(identityManager.getOrCreateUserIdentity("root10").getId())));
    programService.updateProgram(program);

    startSessionAs("root10");
    response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    savedProgram = (ProgramRestEntity) response.getEntity();
    assertNotNull(savedProgram);
    assertEquals(programEntity.getId().longValue(), savedProgram.getId());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isCanEdit());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isManager());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isMember());
    assertTrue(((UserInfoContext) savedProgram.getUserInfo()).isProgramOwner());
  }

  @Test
  public void testGetAccessibleProgramByIdByAnonymous() throws Exception {
    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, null, OFFSET, 10).size());
    assertEquals(0, programService.countPrograms(filter, null));

    ProgramEntity programEntity = newDomain(EntityType.AUTOMATIC, "testGetAccessibleProgramById", true, Collections.emptySet());
    ContainerResponse response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());

    ProgramDTO program = programService.getProgramById(programEntity.getId());
    program.setSpaceId(Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    programService.updateProgram(program);

    response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    ProgramRestEntity programRestEntity = (ProgramRestEntity) response.getEntity();
    assertNotNull(programRestEntity);
    assertEquals(programEntity.getId().longValue(), programRestEntity.getId());
    assertTrue(((UserInfoContext) programRestEntity.getUserInfo()).isCanView());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isCanEdit());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isManager());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isMember());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isProgramOwner());
  }

  @Test
  public void testGetAccessibleProgramsByAnonymous() throws Exception {
    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, null, OFFSET, 10).size());
    assertEquals(0, programService.countPrograms(filter, null));

    ProgramEntity programEntity = newDomain(EntityType.AUTOMATIC, "testGetAccessiblePrograms", true, Collections.emptySet());
    ContainerResponse response = getResponse("GET", getURLResource("programs?offset=0&limit=10&returnSize=true&expand=administrators"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    ProgramList programList = (ProgramList) response.getEntity();
    assertNotNull(programList);
    assertNull(programList.getAdministrators());
    assertEquals(0, programList.getSize());
    assertEquals(0, programList.getPrograms().size());

    ProgramDTO program = programService.getProgramById(programEntity.getId());
    program.setSpaceId(Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    programService.updateProgram(program);

    response = getResponse("GET", getURLResource("programs?offset=0&limit=10&returnSize=true"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    programList = (ProgramList) response.getEntity();
    assertNotNull(programList);
    assertNotNull(programList.getPrograms());
    assertEquals(1, programList.getPrograms().size());
    assertEquals(1, programList.getSize());

    ProgramRestEntity programRestEntity = programList.getPrograms().get(0);
    assertNotNull(programRestEntity);
    assertEquals(programEntity.getId().longValue(), programRestEntity.getId());
    assertTrue(((UserInfoContext) programRestEntity.getUserInfo()).isCanView());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isCanEdit());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isManager());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isMember());
    assertFalse(((UserInfoContext) programRestEntity.getUserInfo()).isProgramOwner());
  }

  @Test
  public void testGetAccessibleProgramsByAnonymousWhenRestrictedHubAccess() throws Exception {
    ProgramFilter filter = new ProgramFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, null, OFFSET, 10).size());
    assertEquals(0, programService.countPrograms(filter, null));

    newDomain(EntityType.AUTOMATIC, "testGetAccessibleProgramsByAnonymousWhenNotOpen", true, Collections.emptySet());
    ContainerResponse response = getResponse("GET", getURLResource("programs?offset=0&limit=10&returnSize=true"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    setRestrictedHubAccess();
    response = getResponse("GET", getURLResource("programs?offset=0&limit=10&returnSize=true"), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());
  }

  @Test
  public void testGetAccessibleProgramByIdByAnonymousWhenRestrictedHubAccess() throws Exception {
    ProgramEntity programEntity = newDomain(EntityType.AUTOMATIC, "testGetAccessibleProgramByIdByAnonymousWhenRestrictedHubAccess", true, Collections.emptySet());
    programEntity.setVisibility(EntityVisibility.OPEN);
    programDAO.update(programEntity);

    ContainerResponse response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    setRestrictedHubAccess();
    response = getResponse("GET", getURLResource("programs/" + programEntity.getId()), null);
    assertNotNull(response);
    assertEquals(401, response.getStatus());
  }

}

/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
package io.meeds.gamification.service;

import static org.junit.Assert.assertThrows;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.mock.MockUploadService;
import org.exoplatform.upload.UploadService;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.mock.SpaceServiceMock;
import io.meeds.gamification.model.ProgramColorAlreadyExists;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.test.AbstractServiceTest;

@SuppressWarnings("deprecation")
public class ProgramServiceTest extends AbstractServiceTest {

  private static final String INTERNAL_USER     = "internalUser";

  private static final String SPACE_MEMBER_USER = "root10";

  private static final String ADMIN_USER        = "root1";

  private Identity            adminAclIdentity;

  private Identity            spaceMemberAclIdentity;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adminAclIdentity = registerAdministratorUser(ADMIN_USER);
    spaceMemberAclIdentity = registerInternalUser(SPACE_MEMBER_USER);
    registerInternalUser(INTERNAL_USER);
  }

  @Test
  public void testGetProgramsAsAdmin() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
  }

  @Test
  public void testGetProgramsAsInternalUser() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
  }

  @Test
  public void testGetOpenProgramsAsInternalUser() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, null, null);
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>(), null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>(), null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>(), null);
    assertEquals(4, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>(), null);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>(), null);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.getPrograms(filter, INTERNAL_USER, offset, 10).size());
  }

  @Test
  public void testGetProgramsAsSpaceMemberUser() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
  }

  @Test
  public void testGetProgramsByFilter() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", true, new HashSet<>());
    filter.setOwnerId(1);
    assertEquals(1, programService.getPrograms(filter, ADMIN_USER, offset, 10).size());
  }

  @Test
  public void testGetDomainsByOwner() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    assertEquals(0, programService.countPrograms(filter, SPACE_MEMBER_USER));

    ProgramEntity programEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    filter.setOwnerId(10);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    assertEquals(0, programService.countPrograms(filter, SPACE_MEMBER_USER));

    programEntity.setOwners(Collections.singleton(10l));
    programDAO.update(programEntity);

    assertEquals(1, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    assertEquals(1, programService.countPrograms(filter, SPACE_MEMBER_USER));
  }

  @Test
  public void testGetDomainsByAnonym() throws IllegalAccessException, ObjectNotFoundException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, null, offset, 10).size());
    assertEquals(0, programService.countPrograms(filter, null));

    ProgramEntity programEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    assertEquals(0, programService.getPrograms(filter, null, offset, 10).size());
    assertEquals(0, programService.countPrograms(filter, null));

    ProgramDTO program = programService.getProgramById(programEntity.getId());
    program.setSpaceId(Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    programService.updateProgram(program);

    assertEquals(1, programService.getPrograms(filter, null, offset, 10).size());
    assertEquals(1, programService.countPrograms(filter, null));
  }

  @Test
  public void testCountDomainsByOwner() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.getPrograms(filter, SPACE_MEMBER_USER, offset, 10).size());
    ProgramEntity domainEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    filter.setOwnerId(10);
    assertEquals(0, programService.countPrograms(filter, SPACE_MEMBER_USER));

    domainEntity.setOwners(Collections.singleton(10l));
    programDAO.update(domainEntity);

    assertEquals(1, programService.countPrograms(filter, SPACE_MEMBER_USER));
  }

  @Test
  public void testCountDomains() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, programService.countPrograms(filter, ADMIN_USER));
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.countPrograms(filter, ADMIN_USER));

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programService.countPrograms(filter, ADMIN_USER));
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programService.countPrograms(filter, ADMIN_USER));
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.countPrograms(filter, ADMIN_USER));
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programService.countPrograms(filter, ADMIN_USER));
  }

  @Test
  public void testCreateDomain() throws IllegalAccessException { // NOSONAR
    assertEquals(0, programDAO.count().longValue());
    ProgramDTO program = new ProgramDTO();
    program.setTitle(GAMIFICATION_DOMAIN);
    program.setDescription("Description");
    program.setDeleted(false);
    program.setEnabled(true);
    program.setBudget(20L);
    program.setColor("#FFEECCDD");
    assertThrows(IllegalArgumentException.class, () -> programService.createProgram(null, adminAclIdentity));
    ProgramDTO domainWithId = new ProgramDTO();
    domainWithId.setId(150L);
    assertThrows(IllegalArgumentException.class, () -> programService.createProgram(domainWithId, adminAclIdentity));
    program = programService.createProgram(program, adminAclIdentity);
    assertNotNull(program);
    assertEquals(EntityType.MANUAL.name(), program.getType());
    assertNotNull(programDAO.find(program.getId()));

    ProgramDTO programToSave = new ProgramDTO();
    programToSave.setTitle(GAMIFICATION_DOMAIN);
    programToSave.setDescription("Description");
    programToSave.setDeleted(false);
    programToSave.setEnabled(true);
    programToSave.setBudget(20L);
    programToSave.setColor("#FFEECCdd");
    programToSave.setOwnerIds(Collections.emptySet());
    programToSave.setCoverFileId(1L);
    programToSave.setAvatarFileId(2L);
    programToSave.setType(EntityType.MANUAL.name());
    assertThrows(IllegalAccessException.class, () -> programService.createProgram(programToSave, spaceMemberAclIdentity));
    assertThrows(ProgramColorAlreadyExists.class, () -> programService.createProgram(programToSave, adminAclIdentity));

    programToSave.setColor("#FFEECCEE");
    ProgramDTO savedDomain = programService.createProgram(programToSave, adminAclIdentity);
    assertNotNull(savedDomain);
    savedDomain = programService.getProgramById(savedDomain.getId());
    assertEquals(GAMIFICATION_DOMAIN, programToSave.getTitle());
    assertEquals("Description", programToSave.getDescription());
    assertEquals(adminAclIdentity.getUserId(), programToSave.getCreatedBy());
    assertEquals(adminAclIdentity.getUserId(), programToSave.getLastModifiedBy());
    assertFalse(programToSave.isDeleted());
    assertTrue(programToSave.isEnabled());
    assertEquals(20L, programToSave.getBudget());
    assertEquals(1L, programToSave.getCoverFileId());
    assertEquals(2L, programToSave.getAvatarFileId());
    assertEquals("#FFEECCEE", programToSave.getColor());
    assertEquals(EntityType.MANUAL.name(), savedDomain.getType());
    assertNotNull(programToSave.getCreatedDate());
    assertNotNull(programToSave.getLastModifiedDate());

    assertThrows(ProgramColorAlreadyExists.class, () -> programService.createProgram(programToSave, adminAclIdentity));
    programToSave.setColor("#FFEECCBB");

    program = programService.createProgram(programToSave);
    assertNotNull(program);
    assertEquals(EntityType.AUTOMATIC.name(), program.getType());
    assertEquals("#FFEECCBB", program.getColor());
  }

  @Test
  public void testUpdateDomain() throws Exception {
    ProgramDTO programWithColor = new ProgramDTO();
    programWithColor.setTitle(GAMIFICATION_DOMAIN);
    programWithColor.setDescription("Description");
    programWithColor.setDeleted(false);
    programWithColor.setEnabled(true);
    programWithColor.setBudget(20L);
    programWithColor.setColor("#FFEECCDD");
    programWithColor = programService.createProgram(programWithColor, adminAclIdentity);

    String newTitle = "title_2";
    String newDescription = "desc_2";
    long newBudget = 30;

    ProgramDTO notExistDomain = new ProgramDTO();
    notExistDomain.setId(150L);
    assertThrows(ObjectNotFoundException.class, () -> programService.updateProgram(notExistDomain, adminAclIdentity));
    ProgramDTO program = newProgram(EntityType.MANUAL, "domain1", true, Collections.singleton(1L));
    program.setDescription(newDescription);
    program.setTitle(newTitle);
    program.setBudget(newBudget);
    program.setEnabled(true);
    assertThrows(IllegalAccessException.class, () -> programService.updateProgram(program, spaceMemberAclIdentity));

    OrganizationService organizationService = ExoContainerContext.getService(OrganizationService.class);
    User user = organizationService.getUserHandler().createUserInstance(spaceMemberAclIdentity.getUserId());
    user.setFirstName("Regular");
    user.setLastName("User");
    user.setEmail("regularuser@localhost.com");
    organizationService.getUserHandler().createUser(user, true);

    String ownerId = identityManager.getOrCreateUserIdentity(user.getUserName()).getId();
    Set<Long> newOwners = Collections.singleton(Long.parseLong(ownerId));
    program.setOwnerIds(newOwners);
    program.setEnabled(false);
    ProgramDTO updatedDomain = programService.updateProgram(program, adminAclIdentity);
    assertFalse(updatedDomain.isEnabled());

    updatedDomain.setEnabled(true);
    updatedDomain.setColor("#FFEECCEE");
    programService.updateProgram(updatedDomain, adminAclIdentity);

    ProgramDTO storedDomain = programService.getProgramById(updatedDomain.getId());
    assertNotNull(storedDomain);
    assertEquals(newDescription, storedDomain.getDescription());
    assertEquals(newTitle, storedDomain.getTitle());
    assertEquals(newBudget, storedDomain.getBudget());
    assertEquals(newOwners, storedDomain.getOwnerIds());
    assertEquals("#FFEECCEE", storedDomain.getColor());
    assertTrue(storedDomain.isEnabled());

    updatedDomain = programService.updateProgram(updatedDomain, adminAclIdentity);
    assertEquals("#FFEECCEE", storedDomain.getColor());

    updatedDomain.setColor(programWithColor.getColor());
    ProgramDTO programToUpdate = updatedDomain.clone();
    assertThrows(ProgramColorAlreadyExists.class, () -> programService.updateProgram(programToUpdate, adminAclIdentity));

    programService.deleteProgramById(programWithColor.getId(), adminAclIdentity);
    updatedDomain = programService.updateProgram(programToUpdate, adminAclIdentity);
    assertEquals(programWithColor.getColor(), updatedDomain.getColor());

    programService.deleteProgramById(storedDomain.getId(), adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.updateProgram(programToUpdate, spaceMemberAclIdentity));
  }

  @Test
  public void testCanUseProgramColor() throws Exception {
    ProgramDTO programWithColor = new ProgramDTO();
    programWithColor.setTitle(GAMIFICATION_DOMAIN);
    programWithColor.setDescription("Description");
    programWithColor.setDeleted(false);
    programWithColor.setEnabled(true);
    programWithColor.setBudget(20L);
    programWithColor.setColor("#FFEECCDD");
    programWithColor = programService.createProgram(programWithColor, adminAclIdentity);

    assertTrue(programService.canUseProgramColor(programWithColor.getId(), programWithColor.getColor()));
    assertFalse(programService.canUseProgramColor(0, programWithColor.getColor()));
    assertTrue(programService.canUseProgramColor(0, "#FFEECCCC"));
    assertTrue(programService.canUseProgramColor(0, null));
  }

  @Test
  public void testDeleteDomain() throws Exception {
    ProgramDTO domain = newProgram(EntityType.MANUAL, "domain1", true, Collections.singleton(1L));
    assertFalse(domain.isDeleted());
    domain.setDeleted(true);

    assertThrows(ObjectNotFoundException.class, () -> programService.deleteProgramById(20000L, adminAclIdentity));
    assertThrows(IllegalAccessException.class, () -> programService.deleteProgramById(domain.getId(), spaceMemberAclIdentity));

    programService.deleteProgramById(domain.getId(), adminAclIdentity);
    ProgramEntity domainEntity = programDAO.find(domain.getId());
    assertTrue(domainEntity.isDeleted());
  }

  @Test
  public void testDisableProgramReturnsNoPoints() throws Exception {
    RuleDTO rule = newRuleDTO();
    ProgramDTO program = rule.getProgram();

    assertEquals(rule.getScore(), program.getRulesTotalScore());

    program.setEnabled(false);
    program = programService.updateProgram(program, adminAclIdentity);
    assertEquals(0, program.getRulesTotalScore());

    program.setEnabled(true);
    program = programService.updateProgram(program, adminAclIdentity);
    assertEquals(rule.getScore(), program.getRulesTotalScore());
    program = programService.getProgramById(program.getId(), adminAclIdentity.getUserId());
    assertEquals(rule.getScore(), program.getRulesTotalScore());
  }

  @Test
  public void testGetProgramById() {
    assertEquals(programDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> programService.getProgramById(-1L));
    ProgramDTO program = newProgram();
    assertNotNull(program);
    ProgramDTO domain = programService.getProgramById(program.getId());
    assertNotNull(domain);
    assertEquals(program.getId(), domain.getId());
  }

  @Test
  public void testGetProgramByIdAndUser() throws IllegalAccessException, ObjectNotFoundException {
    assertEquals(programDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> programService.getProgramById(-1L, ADMIN_USER));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(5000L, ADMIN_USER));

    ProgramDTO program = newProgram();
    assertNotNull(program);

    long programId = program.getId();

    ProgramDTO foundProgram = programService.getProgramById(programId, ADMIN_USER);
    assertNotNull(foundProgram);
    assertEquals(programId, foundProgram.getId());
    assertTrue(program.isEnabled());
    assertNotNull(programService.getProgramById(programId, SPACE_MEMBER_USER));
    assertThrows(IllegalAccessException.class, () -> programService.getProgramById(programId, "demo"));

    program.setEnabled(false);
    program = programService.updateProgram(program, adminAclIdentity);

    assertNotNull(programService.getProgramById(programId, ADMIN_USER));
    assertThrows(IllegalAccessException.class, () -> programService.getProgramById(programId, SPACE_MEMBER_USER));
    assertThrows(IllegalAccessException.class, () -> programService.getProgramById(programId, "demo"));

    programService.deleteProgramById(programId, adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(programId, ADMIN_USER));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(programId, SPACE_MEMBER_USER));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramById(programId, "demo"));
  }

  @Test
  public void testGetProgramCoverAsStream() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "cover.png", "image/png");
    domain.setCoverUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramCoverStream(domain.getId()));
  }

  @Test
  public void testGetProgramAvatarAsStream() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "avatar.png", "image/png");
    domain.setAvatarUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramAvatarStream(domain.getId()));
  }

  @Test
  public void testDeleteProgramCover() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "cover.png", "image/png");
    domain.setCoverUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramCoverStream(domain.getId()));

    programService.deleteProgramCoverById(domainId, adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramCoverStream(domainId));
  }

  @Test
  public void testDeleteProgramAvatar() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "avatar.png", "image/png");
    domain.setAvatarUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getProgramAvatarStream(domain.getId()));

    programService.deleteProgramAvatarById(domainId, adminAclIdentity);
    assertThrows(ObjectNotFoundException.class, () -> programService.getProgramAvatarStream(domainId));
  }

  @Test
  public void testCanAddDomain() {
    assertFalse(programService.canAddProgram(null));
    assertFalse(programService.canAddProgram(spaceMemberAclIdentity));
    assertTrue(programService.canAddProgram(adminAclIdentity));
  }

  @Test
  public void testCanUpdateDomain() throws IllegalAccessException, ObjectNotFoundException {
    ProgramDTO domain = newProgram();
    assertFalse(programService.isProgramOwner(domain.getId(), spaceMemberAclIdentity.getUserId()));
    assertTrue(programService.isProgramOwner(domain.getId(), adminAclIdentity.getUserId()));
    assertFalse(programService.isProgramOwner(0, spaceMemberAclIdentity.getUserId()));
    String identityId = identityManager.getOrCreateUserIdentity(spaceMemberAclIdentity.getUserId()).getId();
    domain.setOwnerIds(Collections.singleton(Long.parseLong(identityId)));
    programService.updateProgram(domain, adminAclIdentity);
    assertTrue(programService.isProgramOwner(domain.getId(), spaceMemberAclIdentity.getUserId()));
  }
}

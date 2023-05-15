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
package org.exoplatform.addons.gamification.service;

import static org.junit.Assert.assertThrows;

import java.io.File;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.ProgramEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.mock.MockUploadService;
import org.exoplatform.upload.UploadService;

public class ProgramServiceTest extends AbstractServiceTest {

  private final Identity adminAclIdentity   =
                                          new Identity("root1",
                                                       Collections.singleton(new MembershipEntry("/platform/administrators")));

  private final Identity regularAclIdentity =
                                            new Identity("root10", Collections.singleton(new MembershipEntry("/platform/users")));

  @Override
  public void setUp() throws Exception {
    super.setUp();
    identityRegistry.register(adminAclIdentity);
    identityRegistry.register(regularAclIdentity);
  }

  @Test
  public void testGetDomains() throws IllegalAccessException {

    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.getProgramsByFilter(filter, "root1", offset, 10).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.getProgramsByFilter(filter, "root1", offset, 10).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.getProgramsByFilter(filter, "root1", offset, 10).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
  }

  @Test
  public void testGetDomainsByFilter() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", true, new HashSet<>());
    filter.setOwnerId(1);
    assertEquals(1, programService.getProgramsByFilter(filter, "root1", offset, 10).size());
  }

  @Test
  public void testGetDomainsByOwner() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programService.getProgramsByFilter(filter, "root10", offset, 10).size());
    ProgramEntity domainEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    filter.setOwnerId(10);
    assertEquals(0, programService.getProgramsByFilter(filter, "root10", offset, 10).size());

    domainEntity.setOwners(Collections.singleton(10l));
    programDAO.update(domainEntity);

    assertEquals(1, programService.getProgramsByFilter(filter, "root10", offset, 10).size());
  }

  @Test
  public void testCountDomainsByOwner() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programService.getProgramsByFilter(filter, "root10", offset, 10).size());
    ProgramEntity domainEntity = newDomain(EntityType.AUTOMATIC, "domain10", true, Collections.emptySet());
    filter.setOwnerId(10);
    assertEquals(0, programService.countPrograms(filter, "root10"));

    domainEntity.setOwners(Collections.singleton(10l));
    programDAO.update(domainEntity);

    assertEquals(1, programService.countPrograms(filter, "root10"));
  }

  @Test
  public void testCountDomains() throws IllegalAccessException {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programService.countPrograms(filter, "root1"));
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programService.countPrograms(filter, "root1"));

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programService.countPrograms(filter, "root1"));
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programService.countPrograms(filter, "root1"));

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, programService.countPrograms(filter, "root1"));
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programService.countPrograms(filter, "root1"));

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, programService.countPrograms(filter, "root1"));
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, programService.countPrograms(filter, "root1"));
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programService.countPrograms(filter, "root1"));
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programService.countPrograms(filter, "root1"));
  }

  @Test
  public void testCreateDomain() throws IllegalAccessException {
    assertEquals(0, programDAO.count().longValue());
    ProgramDTO autoDomain = new ProgramDTO();
    autoDomain.setTitle(GAMIFICATION_DOMAIN);
    autoDomain.setDescription("Description");
    autoDomain.setDeleted(false);
    autoDomain.setEnabled(true);
    autoDomain.setBudget(20L);
    assertThrows(IllegalArgumentException.class, () -> programService.createProgram(null, adminAclIdentity));
    ProgramDTO domainWithId = new ProgramDTO();
    domainWithId.setId(150L);
    assertThrows(IllegalArgumentException.class, () -> programService.createProgram(domainWithId, adminAclIdentity));
    autoDomain = programService.createProgram(autoDomain, adminAclIdentity);
    assertNotNull(autoDomain);
    assertEquals(EntityType.AUTOMATIC.name(), autoDomain.getType());
    assertNotNull(programDAO.find(autoDomain.getId()));

    ProgramDTO manualDomain = new ProgramDTO();
    manualDomain.setTitle(GAMIFICATION_DOMAIN);
    manualDomain.setDescription("Description");
    manualDomain.setDeleted(false);
    manualDomain.setEnabled(true);
    manualDomain.setBudget(20L);
    manualDomain.setOwners(Collections.emptySet());
    manualDomain.setCoverFileId(1L);
    manualDomain.setType(EntityType.MANUAL.name());
    assertThrows(IllegalAccessException.class, () -> programService.createProgram(manualDomain, regularAclIdentity));

    ProgramDTO savedDomain = programService.createProgram(manualDomain, adminAclIdentity);
    assertNotNull(savedDomain);
    savedDomain = programService.getProgramById(savedDomain.getId());
    assertEquals(GAMIFICATION_DOMAIN, manualDomain.getTitle());
    assertEquals("Description", manualDomain.getDescription());
    assertEquals(adminAclIdentity.getUserId(), manualDomain.getCreatedBy());
    assertEquals(adminAclIdentity.getUserId(), manualDomain.getLastModifiedBy());
    assertFalse(manualDomain.isDeleted());
    assertTrue(manualDomain.isEnabled());
    assertEquals(20L, manualDomain.getBudget());
    assertEquals(1L, manualDomain.getCoverFileId());
    assertEquals(EntityType.MANUAL.name(), savedDomain.getType());
    assertNotNull(manualDomain.getCreatedDate());
    assertNotNull(manualDomain.getLastModifiedDate());

    ProgramDTO domain = programService.createProgram(manualDomain);
    assertNotNull(domain);
  }

  @Test
  public void testUpdateDomain() throws Exception {
    String newTitle = "title_2";
    String newDescription = "desc_2";
    long newBudget = 30;

    ProgramDTO notExistDomain = new ProgramDTO();
    notExistDomain.setId(150L);
    assertThrows(ObjectNotFoundException.class, () -> programService.updateProgram(notExistDomain, adminAclIdentity));
    ProgramDTO domain = newProgram(EntityType.MANUAL, "domain1", true, Collections.singleton(1L));
    domain.setDescription(newDescription);
    domain.setTitle(newTitle);
    domain.setBudget(newBudget);
    domain.setEnabled(true);
    assertThrows(IllegalAccessException.class, () -> programService.updateProgram(domain, regularAclIdentity));

    OrganizationService organizationService = ExoContainerContext.getService(OrganizationService.class);
    User user = organizationService.getUserHandler().createUserInstance(regularAclIdentity.getUserId());
    user.setFirstName("Regular");
    user.setLastName("User");
    user.setEmail("regularuser@localhost.com");
    organizationService.getUserHandler().createUser(user, true);

    String ownerId = identityManager.getOrCreateUserIdentity(user.getUserName()).getId();
    Set<Long> newOwners = Collections.singleton(Long.parseLong(ownerId));
    domain.setOwners(newOwners);
    domain.setEnabled(false);
    ProgramDTO updatedDomain = programService.updateProgram(domain, adminAclIdentity);
    assertFalse(updatedDomain.isEnabled());

    updatedDomain.setEnabled(true);
    programService.updateProgram(updatedDomain, adminAclIdentity);

    ProgramDTO storedDomain = programService.getProgramById(updatedDomain.getId());
    assertNotNull(storedDomain);
    assertEquals(newDescription, storedDomain.getDescription());
    assertEquals(newTitle, storedDomain.getTitle());
    assertEquals(newBudget, storedDomain.getBudget());
    assertEquals(newOwners, storedDomain.getOwners());
    assertTrue(storedDomain.isEnabled());

    programService.deleteProgramById(storedDomain.getId(), adminAclIdentity);
    assertThrows(IllegalAccessException.class, () -> programService.updateProgram(updatedDomain, regularAclIdentity));
  }

  @Test
  public void testDeleteDomain() throws Exception {
    ProgramDTO domain = newProgram(EntityType.MANUAL, "domain1", true, Collections.singleton(1L));
    assertFalse(domain.isDeleted());
    domain.setDeleted(true);

    assertThrows(ObjectNotFoundException.class, () -> programService.deleteProgramById(20000L, adminAclIdentity));
    assertThrows(IllegalAccessException.class, () -> programService.deleteProgramById(domain.getId(), regularAclIdentity));

    programService.deleteProgramById(domain.getId(), adminAclIdentity);
    ProgramEntity domainEntity = programDAO.find(domain.getId());
    assertTrue(domainEntity.isDeleted());
  }

  @Test
  public void testGetDomainById() {
    assertEquals(programDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> programService.getProgramById(-1L));
    ProgramDTO program = newProgram();
    assertNotNull(program);
    ProgramDTO domain = programService.getProgramById(program.getId());
    assertNotNull(domain);
    assertEquals(program.getId(), domain.getId());
  }

  @Test
  public void testGetFileDetailAsStream() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> programService.getFileDetailAsStream(0));
    assertThrows(ObjectNotFoundException.class, () -> programService.getFileDetailAsStream(150L));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    ProgramDTO domain = newProgram();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> programService.getFileDetailAsStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "cover.png", "image/png");
    domain.setCoverUploadId(uplaodId);
    domain = programService.updateProgram(domain, adminAclIdentity);
    assertNotNull(programService.getFileDetailAsStream(domain.getId()));
  }

  @Test
  public void testCanAddDomain() {
    assertFalse(programService.canAddProgram(null));
    assertFalse(programService.canAddProgram(regularAclIdentity));
    assertTrue(programService.canAddProgram(adminAclIdentity));
  }

  @Test
  public void testCanUpdateDomain() throws IllegalAccessException, ObjectNotFoundException {
    ProgramDTO domain = newProgram();
    assertFalse(programService.isProgramOwner(domain.getId(), regularAclIdentity));
    assertTrue(programService.isProgramOwner(domain.getId(), adminAclIdentity));
    assertFalse(programService.isProgramOwner(0, regularAclIdentity));
    String identityId = identityManager.getOrCreateUserIdentity(regularAclIdentity.getUserId()).getId();
    domain.setOwners(Collections.singleton(Long.parseLong(identityId)));
    programService.updateProgram(domain, adminAclIdentity);
    assertTrue(programService.isProgramOwner(domain.getId(), regularAclIdentity));
  }

  @Test
  public void testGetEnabledDomains() throws IllegalAccessException, ObjectNotFoundException {
    List<ProgramDTO> domains = programService.getEnabledPrograms();
    assertTrue(CollectionUtils.isEmpty(domains));
    ProgramDTO domain = newProgram();
    domains = programService.getEnabledPrograms();
    assertEquals(1, domains.size());
    domain.setEnabled(false);
    programService.updateProgram(domain, adminAclIdentity);

    domains = programService.getEnabledPrograms();
    assertEquals(0, domains.size());
  }
}

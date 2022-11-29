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
import org.exoplatform.addons.gamification.constant.EntityFilterType;
import org.exoplatform.addons.gamification.constant.EntityStatusType;
import org.exoplatform.addons.gamification.constant.EntityType;
import org.exoplatform.addons.gamification.model.DomainDTO;
import org.exoplatform.addons.gamification.model.DomainFilter;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.entity.DomainEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.mock.MockUploadService;
import org.exoplatform.upload.UploadService;

public class DomainServiceTest extends AbstractServiceTest {
  private final Identity adminAclIdentity   =
                                    new Identity("root1", Collections.singleton(new MembershipEntry("/platform/administrators")));

  private final Identity regularAclIdentity = new Identity("root10", Collections.singleton(new MembershipEntry("/platform/users")));

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    identityRegistry.register(adminAclIdentity);
    identityRegistry.register(regularAclIdentity);
  }

  @Test
  public void testGetAllDomains() throws Exception {

    DomainFilter filter = new DomainFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, domainService.getDomainsByFilter(filter, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, domainService.getDomainsByFilter(filter, offset, 10).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, domainService.getDomainsByFilter(filter, offset, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, domainService.getDomainsByFilter(filter, offset, 10).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, domainService.getDomainsByFilter(filter, offset, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, domainService.getDomainsByFilter(filter, offset, 10).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, domainService.getDomainsByFilter(filter, offset, 10).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, domainService.getDomainsByFilter(filter, offset, 10).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, domainService.getDomainsByFilter(filter, offset, 10).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, domainService.getDomainsByFilter(filter, offset, 10).size());
  }

  @Test
  public void testCountAllDomains() {
    DomainFilter filter = new DomainFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, domainService.countDomains(filter));
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, domainService.countDomains(filter));

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, domainService.countDomains(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, domainService.countDomains(filter));

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, domainService.countDomains(filter));
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, domainService.countDomains(filter));

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, domainService.countDomains(filter));
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, domainService.countDomains(filter));
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, domainService.countDomains(filter));
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, domainService.countDomains(filter));
  }

  @Test
  public void testCreateDomain() throws Exception {
    assertEquals(0, domainDAO.count().longValue());
    DomainDTO autoDomain = new DomainDTO();
    autoDomain.setTitle(GAMIFICATION_DOMAIN);
    autoDomain.setDescription("Description");
    autoDomain.setDeleted(false);
    autoDomain.setEnabled(true);
    autoDomain.setBudget(20l);
    assertThrows(IllegalArgumentException.class, () -> domainService.createDomain(null, adminAclIdentity));
    DomainDTO domainWithId = new DomainDTO();
    domainWithId.setId(150l);
    assertThrows(IllegalArgumentException.class, () -> domainService.createDomain(domainWithId, adminAclIdentity));
    autoDomain = domainService.createDomain(autoDomain, adminAclIdentity);
    assertNotNull(autoDomain);
    assertEquals(EntityType.AUTOMATIC.name(), autoDomain.getType());
    assertNotNull(domainDAO.find(autoDomain.getId()));

    DomainDTO manualDomain = new DomainDTO();
    manualDomain.setTitle(GAMIFICATION_DOMAIN);
    manualDomain.setDescription("Description");
    manualDomain.setDeleted(false);
    manualDomain.setEnabled(true);
    manualDomain.setBudget(20L);
    manualDomain.setOwners(Collections.emptyList());
    manualDomain.setCoverFileId(1L);
    manualDomain.setType(EntityType.MANUAL.name());
    assertThrows(IllegalAccessException.class, () -> domainService.createDomain(manualDomain, regularAclIdentity));

    DomainDTO savedDomain = domainService.createDomain(manualDomain, adminAclIdentity);
    assertNotNull(savedDomain);
    savedDomain = domainService.getDomainById(savedDomain.getId());
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

    DomainDTO domain = domainService.createDomain(manualDomain);
    assertNotNull(domain);
  }

  @Test
  public void testUpdateDomain() throws Exception {
    String newTitle = "title_2";
    String newDescription = "desc_2";
    long newBudget = 30;

    DomainDTO notExistDomain = new DomainDTO();
    notExistDomain.setId(150L);
    assertThrows(ObjectNotFoundException.class, () -> domainService.updateDomain(notExistDomain, adminAclIdentity));
    DomainDTO domain = newDomainDTO(EntityType.MANUAL, "domain1", true, Collections.singleton(1l));
    domain.setDescription(newDescription);
    domain.setTitle(newTitle);
    domain.setBudget(newBudget);
    domain.setEnabled(true);
    assertThrows(IllegalAccessException.class, () -> domainService.updateDomain(domain, regularAclIdentity));

    OrganizationService organizationService = ExoContainerContext.getService(OrganizationService.class);
    User user = organizationService.getUserHandler().createUserInstance(regularAclIdentity.getUserId());
    user.setFirstName("Regular");
    user.setLastName("User");
    user.setEmail("regularuser@localhost.com");
    organizationService.getUserHandler().createUser(user, true);

    String ownerId = identityManager.getOrCreateUserIdentity(user.getUserName()).getId();
    List<Long> newOwners = new ArrayList<>(Collections.singleton(Long.parseLong(ownerId)));
    domain.setOwners(newOwners);
    domain.setEnabled(false);
    DomainDTO updatedDomain = domainService.updateDomain(domain, adminAclIdentity);
    DomainDTO storedDomain = domainService.getDomainById(updatedDomain.getId());
    assertFalse(updatedDomain.isEnabled());

    updatedDomain.setEnabled(true);
    domainService.updateDomain(updatedDomain, regularAclIdentity);

    storedDomain = domainService.getDomainById(updatedDomain.getId());
    assertNotNull(storedDomain);
    assertEquals(newDescription, storedDomain.getDescription());
    assertEquals(newTitle, storedDomain.getTitle());
    assertEquals(newBudget, storedDomain.getBudget());
    assertEquals(newOwners, storedDomain.getOwners());
    assertTrue(storedDomain.isEnabled());

    domainService.deleteDomainById(storedDomain.getId(), adminAclIdentity);
    assertThrows(IllegalAccessException.class, () -> domainService.updateDomain(updatedDomain, regularAclIdentity));
  }

  @Test
  public void testDeleteDomain() throws Exception {
    DomainDTO domain = newDomainDTO(EntityType.MANUAL, "domain1", true, Collections.singleton(1l));
    assertFalse(domain.isDeleted());
    domain.setDeleted(true);

    assertThrows(ObjectNotFoundException.class, () -> domainService.deleteDomainById(20000l, adminAclIdentity));
    assertThrows(IllegalAccessException.class, () -> domainService.deleteDomainById(domain.getId(), regularAclIdentity));

    domainService.deleteDomainById(domain.getId(), adminAclIdentity);
    DomainEntity domainEntity = domainDAO.find(domain.getId());
    assertTrue(domainEntity.isDeleted());
  }

  @Test
  public void testGetDomainByTitle() {
    assertThrows(IllegalArgumentException.class, () -> domainService.getDomainByTitle(""));
    assertNull(domainService.getDomainByTitle(GAMIFICATION_DOMAIN));
    newDomain();
    DomainDTO domain = domainService.getDomainByTitle(GAMIFICATION_DOMAIN);
    assertNotNull(domain);
    assertEquals(domain.getTitle(), GAMIFICATION_DOMAIN);
  }

  @Test
  public void testGetDomainById() {
    assertEquals(domainDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> domainService.getDomainById(-1l));
    DomainDTO domainDTO = newDomainDTO();
    assertNotNull(domainDTO);
    DomainDTO domain = domainService.getDomainById(domainDTO.getId());
    assertNotNull(domain);
    assertEquals(domainDTO.getId(), domain.getId());
  }

  @Test
  public void testGetFileDetailAsStream() throws Exception {
    assertThrows(ObjectNotFoundException.class, () -> domainService.getFileDetailAsStream(0));
    assertThrows(ObjectNotFoundException.class, () -> domainService.getFileDetailAsStream(150l));

    String uplaodId = "uplaodId" + new Random().nextInt();
    File tempFile = File.createTempFile("image", "temp");
    DomainDTO domain = newDomainDTO();
    long domainId = domain.getId();

    assertThrows(ObjectNotFoundException.class, () -> domainService.getFileDetailAsStream(domainId));
    MockUploadService uploadService = (MockUploadService) ExoContainerContext.getService(UploadService.class);
    uploadService.createUploadResource(uplaodId, tempFile.getPath(), "cover.png", "image/png");
    domain.setCoverUploadId(uplaodId);
    domain = domainService.updateDomain(domain, adminAclIdentity);
    assertNotNull(domainService.getFileDetailAsStream(domain.getId()));
  }

  @Test
  public void testCanAddDomain() {
    assertFalse(domainService.canAddDomain(null));
    assertFalse(domainService.canAddDomain(regularAclIdentity));
    assertTrue(domainService.canAddDomain(adminAclIdentity));
  }

  @Test
  public void testCanUpdateDomain() throws IllegalAccessException, ObjectNotFoundException {
    DomainDTO domain = newDomainDTO();
    assertFalse(domainService.canUpdateDomain(domain.getId(), null));
    assertFalse(domainService.canUpdateDomain(domain.getId(), regularAclIdentity));
    assertTrue(domainService.canUpdateDomain(domain.getId(), adminAclIdentity));
    assertFalse(domainService.canUpdateDomain(0, regularAclIdentity));
    String identityId = identityManager.getOrCreateUserIdentity(regularAclIdentity.getUserId()).getId();
    domain.setOwners(new ArrayList<>(Collections.singleton(Long.parseLong(identityId))));
    domainService.updateDomain(domain, adminAclIdentity);
    assertTrue(domainService.canUpdateDomain(domain.getId(), regularAclIdentity));
  }

  @Test
  public void testGetEnabledDomains() throws IllegalAccessException, ObjectNotFoundException {
    List<DomainDTO> domains = domainService.getEnabledDomains();
    assertTrue(CollectionUtils.isEmpty(domains));
    DomainDTO domain = newDomainDTO();
    domains = domainService.getEnabledDomains();
    assertEquals(1, domains.size());
    domain.setEnabled(false);
    domainService.updateDomain(domain, adminAclIdentity);

    domains = domainService.getEnabledDomains();
    domains = domainService.getEnabledDomains();
    assertEquals(0, domains.size());
  }
}

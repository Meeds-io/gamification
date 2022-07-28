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
package org.exoplatform.addons.gamification.service.configuration;

import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;

public class DomainServiceTest extends AbstractServiceTest {

  @Test
  public void testGetAllDomains() throws Exception {

    DomainFilter filter = new DomainFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, domainService.getAllDomains(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, domainService.getAllDomains(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, domainService.getAllDomains(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, domainService.getAllDomains(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, domainService.getAllDomains(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, domainService.getAllDomains(offset, 10, filter).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, domainService.getAllDomains(offset, 10, filter).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, domainService.getAllDomains(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, domainService.getAllDomains(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, domainService.getAllDomains(offset, 10, filter).size());
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
  public void testFindEnabledDomainByTitle() {
    assertThrows(IllegalArgumentException.class, () -> domainService.findEnabledDomainByTitle(""));

    assertNull(domainService.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
    newDomainDTO(EntityType.MANUAL, GAMIFICATION_DOMAIN, true, Collections.singleton(1l));
    assertNotNull(domainService.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
    newDomainDTO(EntityType.MANUAL, "disabledDomain", false, Collections.singleton(1l));
    assertNull(domainService.findEnabledDomainByTitle("disabledDomain"));
  }

  @Test
  public void testAddDomain() throws Exception {
    assertEquals(domainDAO.findAll().size(), 0);
    DomainDTO autoDomain = new DomainDTO();
    autoDomain.setTitle(GAMIFICATION_DOMAIN);
    autoDomain.setDescription("Description");
    autoDomain.setCreatedBy(TEST_USER_SENDER);
    autoDomain.setCreatedDate(Utils.toRFC3339Date(new Date()));
    autoDomain.setLastModifiedBy(TEST_USER_SENDER);
    autoDomain.setDeleted(false);
    autoDomain.setEnabled(true);
    autoDomain.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    autoDomain.setCreatedDate(Utils.toRFC3339Date(new Date()));
    autoDomain.setBudget(20l);
    assertThrows(IllegalArgumentException.class, () -> domainService.addDomain(null, "root1", true));
    DomainDTO domainWithId = new DomainDTO();
    domainWithId.setId(150l);
    assertThrows(IllegalArgumentException.class, () -> domainService.addDomain(domainWithId, "root1", true));
    autoDomain = domainService.addDomain(autoDomain, "root1", true);
    assertNotNull(autoDomain);
    assertEquals(EntityType.AUTOMATIC.name(), autoDomain.getType());
    assertNotNull(domainDAO.find(autoDomain.getId()));

    DomainDTO manualDomain = new DomainDTO();
    manualDomain.setTitle(GAMIFICATION_DOMAIN);
    manualDomain.setDescription("Description");
    manualDomain.setCreatedBy(TEST_USER_SENDER);
    manualDomain.setCreatedDate(Utils.toRFC3339Date(new Date()));
    manualDomain.setLastModifiedBy(TEST_USER_SENDER);
    manualDomain.setDeleted(false);
    manualDomain.setEnabled(true);
    manualDomain.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    manualDomain.setCreatedDate(Utils.toRFC3339Date(new Date()));
    manualDomain.setBudget(20l);
    manualDomain.setOwners(new HashSet<>());
    manualDomain.setCoverFileId(1L);
    manualDomain.setType(EntityType.MANUAL.name());
    assertThrows(IllegalArgumentException.class, () -> domainService.addDomain(manualDomain, "root1", true));
    manualDomain.setOwners(Collections.singleton(1234l));
    assertThrows(IllegalAccessException.class, () -> domainService.addDomain(manualDomain, "root1", false));
    DomainDTO savedDomain = domainService.addDomain(manualDomain, "root1", true);
    assertNotNull(savedDomain);
    assertEquals(EntityType.MANUAL.name(), savedDomain.getType());
    assertNotNull(domainDAO.find(savedDomain.getId()));
  }

  @Test
  public void testUpdateDomain() throws Exception {
    DomainDTO notExistDomain = new DomainDTO();
    notExistDomain.setId(150l);
    assertThrows(ObjectNotFoundException.class, () -> domainService.updateDomain(notExistDomain, "root1", true));
    DomainDTO domain = newDomainDTO(EntityType.MANUAL, "domain1", true, Collections.singleton(1l));
    domain.setDescription("desc_2");
    assertThrows(IllegalAccessException.class, () -> domainService.updateDomain(domain, "root10", false));

    domainService.updateDomain(domain, "root1", true);
    DomainEntity domainEntity = domainDAO.find(domain.getId());
    assertEquals(domainEntity.getDescription(), "desc_2");
  }

  @Test
  public void testDeleteDomain() throws Exception {
    DomainDTO domain = newDomainDTO(EntityType.MANUAL, "domain1", true, Collections.singleton(1l));
    assertFalse(domain.isDeleted());
    domain.setDeleted(true);
    domainService.deleteDomain(domain.getId(), "root1", true);
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
  public void TestGetFileDetailAsStream() throws IOException {
    assertNull(domainService.getFileDetailAsStream(0));
    assertNull(domainService.getFileDetailAsStream(150l));
    assertNotNull(domainService.getFileDetailAsStream(1l));
  }
}

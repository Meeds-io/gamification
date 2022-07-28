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

package org.exoplatform.addons.gamification.storage;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.junit.Test;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;

public class DomainStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveDomain() throws Exception {
    assertEquals(0, domainDAO.findAll().size());
    DomainDTO domain = new DomainDTO();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_SENDER);
    domain.setLastModifiedBy(TEST_USER_SENDER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    domain.setType(EntityType.AUTOMATIC.name());
    domain.setCreatedDate(Utils.toRFC3339Date(new Date()));
    domain.setBudget(20l);
    domain.setOwners(Collections.singleton(1l));
    domain.setCoverFileId(1l);
    DomainDTO savedDomain = domainStorage.saveDomain(domain);
    assertNotNull(savedDomain);
    assertNotEquals(0, savedDomain.getId());
    assertNotNull(domainDAO.find(savedDomain.getId()));

    domain.setCoverUploadId("1");
    assertThrows(IllegalStateException.class, () ->  domainStorage.saveDomain(domain));

  }

  @Test
  public void testFindEnabledDomainByTitle() throws Exception {
    assertNull(domainStorage.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
    DomainDTO domain = newDomainDTO();
    assertNotNull(domainStorage.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
    domain.setEnabled(false);
    domainStorage.saveDomain(domain);
    assertNull(domainStorage.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
  }

  @Test
  public void testGetEnabledDomains() throws Exception {
    assertEquals(0, domainStorage.getEnabledDomains().size());
    newDomain("domain1");
    DomainDTO domain = newDomainDTO("domain2");
    assertEquals(2, domainStorage.getEnabledDomains().size());
    domain.setEnabled(false);
    domainStorage.saveDomain(domain);
    assertEquals(1, domainStorage.getEnabledDomains().size());
  }

  @Test
  public void testGetAllDomains() throws Exception {
    DomainFilter filter = new DomainFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, domainStorage.getAllDomains(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, domainStorage.getAllDomains(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, domainStorage.getAllDomains(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, domainStorage.getAllDomains(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, domainStorage.getAllDomains(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, domainStorage.getAllDomains(offset, 10, filter).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, domainStorage.getAllDomains(offset, 10, filter).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, domainStorage.getAllDomains(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, domainStorage.getAllDomains(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, domainStorage.getAllDomains(offset, 10, filter).size());

  }

  @Test
  public void testCountAllDomains() {
    DomainFilter filter = new DomainFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, domainStorage.countDomains(filter));
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, domainStorage.countDomains(filter));

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, domainStorage.countDomains(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, domainStorage.countDomains(filter));

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, domainStorage.countDomains(filter));
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, domainDAO.countAllDomains(filter));

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, domainStorage.countDomains(filter));
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, domainStorage.countDomains(filter));
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, domainStorage.countDomains(filter));
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, domainStorage.countDomains(filter));
  }
}

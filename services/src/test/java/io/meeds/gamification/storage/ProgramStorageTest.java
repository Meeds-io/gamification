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

package io.meeds.gamification.storage;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import org.junit.Test;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

@SuppressWarnings("deprecation")
public class ProgramStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveDomain() {
    assertEquals(0, programDAO.findAll().size());
    ProgramDTO domain = new ProgramDTO();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_EARNER);
    domain.setLastModifiedBy(TEST_USER_EARNER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    domain.setType(EntityType.AUTOMATIC.name());
    domain.setCreatedDate(Utils.toRFC3339Date(new Date()));
    domain.setBudget(20L);
    domain.setOwnerIds(Collections.singleton(1L));
    domain.setCoverFileId(1L);
    ProgramDTO savedDomain = domainStorage.saveProgram(domain);
    assertNotNull(savedDomain);
    assertNotEquals(0, savedDomain.getId());

    domain.setCoverUploadId("1");
    assertThrows(IllegalStateException.class, () -> domainStorage.saveProgram(domain));

  }

  @Test
  public void testGetAllDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, domainStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
  }

  @Test
  public void testCountDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, domainStorage.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, domainStorage.countPrograms(filter));

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, domainStorage.countPrograms(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, domainStorage.countPrograms(filter));

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, domainStorage.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programDAO.countPrograms(filter));

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, domainStorage.countPrograms(filter));
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, domainStorage.countPrograms(filter));
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, domainStorage.countPrograms(filter));
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, domainStorage.countPrograms(filter));
  }
}

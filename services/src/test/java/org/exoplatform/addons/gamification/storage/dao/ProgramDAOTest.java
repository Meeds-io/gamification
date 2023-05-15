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
package org.exoplatform.addons.gamification.storage.dao;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.ProgramEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class ProgramDAOTest extends AbstractServiceTest {

  @Test
  public void testGetEnabledDomains() {
    assertEquals(0, programDAO.getEnabledDomains().size());
    newDomain("domain1");
    ProgramEntity domainEntity = newDomain("domain2");
    assertEquals(2, programDAO.getEnabledDomains().size());
    domainEntity.setEnabled(false);
    programDAO.update(domainEntity);
    assertEquals(1, programDAO.getEnabledDomains().size());
  }

  @Test
  public void testGetAllDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programDAO.getDomainsByFilter(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    assertEquals(4, programDAO.getDomainsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.getDomainsByFilter(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(2, programDAO.getDomainsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.getDomainsByFilter(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain6", false, null);
    assertEquals(2, programDAO.getDomainsByFilter(offset, 10, filter).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, programDAO.getDomainsByFilter(offset, 10, filter).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.getDomainsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.getDomainsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getDomainsByFilter(offset, 10, filter).size());
  }
  
  @Test
  public void testGetDomainsByTextualFilter() {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programDAO.getDomainsByFilter(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    filter.setDomainTitle("domain1");
    assertEquals(1, programDAO.getDomainsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getDomainsByFilter(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(0, programDAO.getDomainsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getDomainsByFilter(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain1", true, null);
    assertEquals(1, programDAO.getDomainsByFilter(offset, 10, filter).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(2, programDAO.getDomainsByFilter(offset, 10, filter).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(0, programDAO.getDomainsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getDomainsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, programDAO.getDomainsByFilter(offset, 10, filter).size());
  }

  @Test
  public void testCountDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programDAO.countDomains(filter));
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    assertEquals(4, programDAO.countDomains(filter));

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.countDomains(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(2, programDAO.countDomains(filter));

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.countDomains(filter));
    newDomain(EntityType.MANUAL, "domain6", false, null);
    assertEquals(2, programDAO.countDomains(filter));

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, programDAO.countDomains(filter));
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.countDomains(filter));
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.countDomains(filter));
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.countDomains(filter));

  }

}

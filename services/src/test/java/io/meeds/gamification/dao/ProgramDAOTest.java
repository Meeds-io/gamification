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
package io.meeds.gamification.dao;

import org.junit.Test;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.test.AbstractServiceTest;

public class ProgramDAOTest extends AbstractServiceTest {

  @Test
  public void testGetAllDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    assertEquals(4, programDAO.getProgramIdsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(2, programDAO.getProgramIdsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain6", false, null);
    assertEquals(2, programDAO.getProgramIdsByFilter(offset, 10, filter).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
  }
  
  @Test
  public void testGetDomainsByTextualFilter() {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    filter.setDomainTitle("domain1");
    assertEquals(1, programDAO.getProgramIdsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(0, programDAO.getProgramIdsByFilter(offset, 10, filter).size());

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain1", true, null);
    assertEquals(1, programDAO.getProgramIdsByFilter(offset, 10, filter).size());

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(2, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(0, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, programDAO.getProgramIdsByFilter(offset, 10, filter).size());
  }

  @Test
  public void testCountDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setEntityFilterType(EntityFilterType.ALL);
    filter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, programDAO.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    assertEquals(4, programDAO.countPrograms(filter));

    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.countPrograms(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(2, programDAO.countPrograms(filter));

    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain6", false, null);
    assertEquals(2, programDAO.countPrograms(filter));

    filter.setEntityStatusType(EntityStatusType.ALL);
    filter.setEntityFilterType(EntityFilterType.ALL);
    assertEquals(6, programDAO.countPrograms(filter));
    filter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.countPrograms(filter));
    filter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.countPrograms(filter));
    filter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.countPrograms(filter));

  }

}

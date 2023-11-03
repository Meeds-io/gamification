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

@SuppressWarnings("deprecation")
public class ProgramDAOTest extends AbstractServiceTest {

  @Test
  public void testGetProgramsByFilter() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    assertEquals(4, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain6", false, null);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
  }

  @Test
  public void testGetOpenProgramsByFilter() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(false);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null, null);
    newDomain(EntityType.MANUAL, "domain2", true, null, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null, null);
    assertEquals(4, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null, null);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain6", false, null, null);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
  }

  @Test
  public void testGetProgramsByTextualFilter() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null, null);
    filter.setProgramTitle("domain1");
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain1", true, null);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
  }

  @Test
  public void testGetOpenProgramsByTextualFilter() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(false);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.MANUAL, "domain1", true, null, null);
    newDomain(EntityType.MANUAL, "domain2", true, null, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null, null);
    newDomain(EntityType.AUTOMATIC, "adminDomain", true, null, 1l);
    filter.setProgramTitle("domain1");
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, null, null);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    newDomain(EntityType.AUTOMATIC, "domain1", true, null, null);
    assertEquals(1, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(2, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, programDAO.getProgramIdsByFilter(OFFSET, 10, filter).size());
  }

  @Test
  public void testCountPrograms() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, programDAO.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain1", true, null);
    newDomain(EntityType.MANUAL, "domain2", true, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null);
    assertEquals(4, programDAO.countPrograms(filter));

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.countPrograms(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, null);
    assertEquals(2, programDAO.countPrograms(filter));

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain6", false, null);
    assertEquals(2, programDAO.countPrograms(filter));

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programDAO.countPrograms(filter));
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.countPrograms(filter));
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.countPrograms(filter));
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.countPrograms(filter));
  }

  @Test
  public void testCountOpenPrograms() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(false);
    assertEquals(0, programDAO.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain1", true, null, null);
    newDomain(EntityType.MANUAL, "domain2", true, null, null);
    newDomain(EntityType.AUTOMATIC, "domain3", true, null, null);
    newDomain(EntityType.AUTOMATIC, "domain4", true, null, null);
    assertEquals(4, programDAO.countPrograms(filter));

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programDAO.countPrograms(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, null, null);
    assertEquals(2, programDAO.countPrograms(filter));

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programDAO.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain6", false, null, null);
    assertEquals(2, programDAO.countPrograms(filter));

     filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programDAO.countPrograms(filter));
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programDAO.countPrograms(filter));
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programDAO.countPrograms(filter));
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programDAO.countPrograms(filter));
  }

}

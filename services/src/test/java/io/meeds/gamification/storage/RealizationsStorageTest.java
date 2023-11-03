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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package io.meeds.gamification.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.test.AbstractServiceTest;

public class RealizationsStorageTest extends AbstractServiceTest {

  @Test
  public void testFindAllRealizationsByFilter() {
    RealizationFilter filter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerIds(new ArrayList<>());
    filter.setEarnerType(IdentityType.getType(""));
    filter.setProgramIds(domainIds);
    ProgramEntity domainEntity = newDomain();
    assertEquals(0, realizationsStorage.getRealizationsByFilter(filter, OFFSET, LIMIT).size());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    assertEquals(3, realizationsStorage.getRealizationsByFilter(filter, OFFSET, LIMIT).size());
  }
  
  @Test
  public void testFindUsersRealizationsByFilter() {
    RealizationFilter filter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerIds(new ArrayList<>(Collections.singleton("1")));
    filter.setEarnerType(IdentityType.getType(""));
    filter.setProgramIds(domainIds);
    assertEquals(0, realizationsStorage.getRealizationsByFilter(filter, OFFSET, LIMIT).size());
    ProgramEntity domainEntity = newDomain();
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    newRealizationEntity("rule", domainEntity.getId());
    assertEquals(3, realizationsStorage.getRealizationsByFilter(filter, OFFSET, LIMIT).size());
  }

  @Test
  public void testGetRealizationById() {
    RealizationFilter filter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerIds(new ArrayList<>());
    filter.setEarnerType(IdentityType.getType(""));
    filter.setProgramIds(domainIds);
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, OFFSET, LIMIT).size(), 0);
    RealizationDTO gHistory = newRealizationDTO();
    RealizationDTO newGHistory = realizationsStorage.getRealizationById(gHistory.getId());
    assertNotNull(newGHistory);
    assertEquals(gHistory.getActionTitle(), newGHistory.getActionTitle());
  }

  @Test
  public void testUpdateRealizationStatus() {
    RealizationFilter filter = new RealizationFilter();
    List<Long> domainIds = Collections.emptyList();
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    filter.setEarnerIds(new ArrayList<>());
    filter.setEarnerType(IdentityType.getType(""));
    filter.setProgramIds(domainIds);
    assertEquals(realizationsStorage.getRealizationsByFilter(filter, OFFSET, LIMIT).size(), 0);
    RealizationDTO gHistory = newRealizationDTO();
    assertEquals(gHistory.getStatus(), RealizationStatus.ACCEPTED.name());
    gHistory.setStatus(RealizationStatus.REJECTED.name());
    RealizationDTO rejectedGHistory = realizationsStorage.updateRealization(gHistory);
    assertEquals(rejectedGHistory.getStatus(), RealizationStatus.REJECTED.name());

  }
}

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
package io.meeds.gamification.dao;

import org.junit.Test;

import io.meeds.gamification.entity.BadgeEntity;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.test.AbstractServiceTest;

public class BadgeDAOTest extends AbstractServiceTest {

  @Test
  public void testFindBadgeByTitle() {
    assertNull(badgeStorage.findBadgeByTitle(BADGE_NAME));
    newBadge(1L);
    assertNotNull(badgeStorage.findBadgeByTitle(BADGE_NAME));
  }

  @Test
  public void testFindBadgesByDomain() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(badgeStorage.findBadgesByProgramId(domainEntity.getId()).size(), 0);
    newBadge(domainEntity.getId());
    assertEquals(badgeStorage.findBadgesByProgramId(domainEntity.getId()).size(), 1);
  }

  @Test
  public void testFindEnabledBadgesByDomain() {
    ProgramEntity domainEntity = newDomain();
    assertEquals(badgeStorage.findEnabledBadgesByProgramId(domainEntity.getId()).size(), 0);
    BadgeEntity badgeEntity = newBadge(domainEntity.getId());
    assertEquals(badgeStorage.findEnabledBadgesByProgramId(domainEntity.getId()).size(), 1);
    badgeEntity.setEnabled(false);
    badgeStorage.update(badgeEntity);
    assertEquals(badgeStorage.findEnabledBadgesByProgramId(domainEntity.getId()).size(), 0);
  }

  @Test
  public void testGetAllBadges() {
    ProgramEntity domainEntity1 = newDomain();
    ProgramEntity domainEntity2 = newDomain();
    assertEquals(badgeStorage.getAllBadges().size(), 0);
    newBadge("badge1", domainEntity1.getId());
    newBadge("badge2", domainEntity1.getId());
    newBadge("badge3", domainEntity2.getId());
    newBadge("badge4", domainEntity2.getId());
    newBadge("badge5", domainEntity2.getId());
    assertEquals(badgeStorage.getAllBadges().size(), 5);
  }

}

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
package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class BadgeDAOTest extends AbstractServiceTest {

  @Test
  public void testFindBadgeByTitle() {
    assertNull(badgeStorage.findBadgeByTitle(BADGE_NAME));
    newBadge(1L);
    assertNotNull(badgeStorage.findBadgeByTitle(BADGE_NAME));
  }

  @Test
  public void testFindBadgesByDomain() {
    DomainEntity domainEntity = newDomain();
    assertEquals(badgeStorage.findBadgesByDomain(domainEntity.getId()).size(), 0);
    newBadge(domainEntity.getId());
    assertEquals(badgeStorage.findBadgesByDomain(domainEntity.getId()).size(), 1);
  }

  @Test
  public void testFindEnabledBadgesByDomain() {
    DomainEntity domainEntity = newDomain();
    assertEquals(badgeStorage.findEnabledBadgesByDomain(domainEntity.getId()).size(), 0);
    BadgeEntity badgeEntity = newBadge(domainEntity.getId());
    assertEquals(badgeStorage.findEnabledBadgesByDomain(domainEntity.getId()).size(), 1);
    badgeEntity.setEnabled(false);
    badgeStorage.update(badgeEntity);
    assertEquals(badgeStorage.findEnabledBadgesByDomain(domainEntity.getId()).size(), 0);
  }

  @Test
  public void testGetAllBadges() {
    DomainEntity domainEntity1 = newDomain();
    DomainEntity domainEntity2 = newDomain();
    assertEquals(badgeStorage.getAllBadges().size(), 0);
    newBadge("badge1", domainEntity1.getId());
    newBadge("badge2", domainEntity1.getId());
    newBadge("badge3", domainEntity2.getId());
    newBadge("badge4", domainEntity2.getId());
    newBadge("badge5", domainEntity2.getId());
    assertEquals(badgeStorage.getAllBadges().size(), 5);
  }

  @Test
  public void testGetAllBadgesWithNullDomain() {
    DomainEntity domainEntity1 = newDomain();
    DomainEntity domainEntity2 = newDomain();
    DomainEntity domainEntity3 = newDomain();
    DomainEntity domainEntity4 = newDomain();
    DomainEntity domainEntity5 = newDomain();
    assertEquals(badgeStorage.findAll().size(), 0);
    newBadge("badge1", domainEntity1.getId());
    newBadge("badge2", domainEntity2.getId());
    newBadge("badge3", domainEntity3.getId());
    newBadge("badge4", domainEntity4.getId());
    newBadge("badge5", domainEntity5.getId());
    assertEquals(badgeStorage.findAll().size(), 5);
    assertEquals(badgeStorage.getAllBadgesWithNullDomain().size(), 0);
    BadgeEntity badge_ = badgeStorage.findBadgeByTitle("badge1");
    badge_.setDomainEntity(null);
    badgeStorage.update(badge_);
    assertEquals(badgeStorage.getAllBadgesWithNullDomain().size(), 1);
  }
}

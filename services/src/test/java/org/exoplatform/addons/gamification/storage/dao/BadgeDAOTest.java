/*
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class BadgeDAOTest extends AbstractServiceTest {

  @Before
  public void setUp() throws Exception { // NOSONAR
    super.setUp();
  }

  @After
  public void tearDown() {
    super.tearDown();
  }

  @Test
  public void testFindBadgeByTitle() {
    assertNull(badgeStorage.findBadgeByTitle(BADGE_NAME));
    newBadge();
    assertNotNull(badgeStorage.findBadgeByTitle(BADGE_NAME));
  }

  @Test
  public void testFindBadgesByDomain() {
    assertEquals(badgeStorage.findBadgesByDomain(GAMIFICATION_DOMAIN).size(), 0);
    newBadge();
    assertEquals(badgeStorage.findBadgesByDomain(GAMIFICATION_DOMAIN).size(), 1);
  }

  @Test
  public void testFindEnabledBadgesByDomain() {
    assertEquals(badgeStorage.findEnabledBadgesByDomain(GAMIFICATION_DOMAIN).size(), 0);
    BadgeEntity badgeEntity = newBadge();
    assertEquals(badgeStorage.findEnabledBadgesByDomain(GAMIFICATION_DOMAIN).size(), 1);
    badgeEntity.setEnabled(false);
    badgeStorage.update(badgeEntity);
    assertEquals(badgeStorage.findEnabledBadgesByDomain(GAMIFICATION_DOMAIN).size(), 0);
  }

  @Test
  public void testGetAllBadges() {
    assertEquals(badgeStorage.getAllBadges().size(), 0);
    newBadge("badge1", "domain1");
    newBadge("badge2", "domain1");
    newBadge("badge3", "domain2");
    newBadge("badge4", "domain2");
    newBadge("badge5", "domain2");
    assertEquals(badgeStorage.getAllBadges().size(), 5);
  }

  @Test
  public void testGetAllBadgesWithNullDomain() {
    assertEquals(badgeStorage.findAll().size(), 0);
    newBadge("badge1", "domain1");
    newBadge("badge2", "domain2");
    newBadge("badge3", "domain3");
    newBadge("badge4", "domain4");
    newBadge("badge5", "domain5");
    assertEquals(badgeStorage.findAll().size(), 5);
    assertEquals(badgeStorage.getAllBadgesWithNullDomain().size(), 0);
    BadgeEntity badge_ = badgeStorage.findBadgeByTitle("badge1");
    badge_.setDomainEntity(null);
    badgeStorage.update(badge_);
    assertEquals(badgeStorage.getAllBadgesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetDomainList() {
    assertEquals(badgeStorage.findAll().size(), 0);
    newBadge("badge1", "domain1");
    newBadge("badge2", "domain1");
    newBadge("badge3", "domain2");
    newBadge("badge4", "domain2");
    newBadge("badge5", "domain2");
    assertEquals(badgeStorage.findAll().size(), 5);
    assertEquals(badgeStorage.getDomainList().size(), 2);
  }
}

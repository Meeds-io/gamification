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
package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.mapper.BadgeMapper;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import static org.junit.Assert.assertThrows;

public class BadgeServiceTest extends AbstractServiceTest {

  @Test
  public void testFindBadgeByTitle() {
    DomainEntity domainEntity = newDomain();
    assertNull(badgeService.findBadgeByTitle(BADGE_NAME));
    BadgeEntity badge = newBadge(domainEntity.getId());
    BadgeDTO badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertNotNull(badge_);
    assertEquals(badge.getTitle(), badge_.getTitle());
  }

  @Test
  public void testGetAllBadges() {
    DomainEntity domainEntity1 = newDomain();
    DomainEntity domainEntity2 = newDomain();
    DomainEntity domainEntity3 = newDomain();
    DomainEntity domainEntity4 = newDomain();
    DomainEntity domainEntity5 = newDomain();
    assertEquals(badgeService.getAllBadges().size(), 0);
    newBadge("badge1", domainEntity1.getId());
    newBadge("badge2", domainEntity2.getId());
    newBadge("badge3", domainEntity3.getId());
    newBadge("badge4", domainEntity4.getId());
    newBadge("badge5", domainEntity5.getId());
    assertEquals(badgeService.getAllBadges().size(), 5);
  }

  @Test
  public void testFindBadge() {
    DomainEntity domainEntity = newDomain();
    BadgeEntity badgeEntity = newBadge("badge1", domainEntity.getId());
    assertNotNull(badgeService.findBadgeById(badgeEntity.getId()));
    assertNotNull(badgeService.findBadgeByTitleAndDomain("badge1", domainEntity.getId()));
  }

  @Test
  public void testAddBadge() throws ObjectAlreadyExistsException {
    assertNull(badgeService.findBadgeByTitle(BADGE_NAME));
    BadgeDTO badge = new BadgeDTO();
    badge.setTitle(BADGE_NAME);
    badge.setDescription("Description");
    badge.setNeededScore(Integer.parseInt(TEST_GLOBAL_SCORE));
    badge.setDomainDTO(newDomainDTO(GAMIFICATION_DOMAIN));
    badge.setIconFileId(10245);
    badge.setEnabled(true);
    badge.setDeleted(false);
    badge.setCreatedBy(TEST_USER_SENDER);
    badge.setLastModifiedBy(TEST_USER_SENDER);
    badge = badgeService.addBadge(badge);
    assertNotNull(badge);
    assertNotNull(badgeService.findBadgeByTitle(BADGE_NAME));

    //
    BadgeDTO finalBadge = badge;
    assertThrows(ObjectAlreadyExistsException.class, () -> badgeService.addBadge(finalBadge));

    //
    badge.setDeleted(true);
    badgeService.updateBadge(badge);
    badge = badgeService.addBadge(badge);
    assertNotNull(badge);
    assertNotNull(badgeService.findBadgeByTitle(BADGE_NAME));
  }

  @Test
  public void testUpdateBadge() throws ObjectAlreadyExistsException {
    DomainEntity domainEntity = newDomain();
    BadgeEntity badge = newBadge(domainEntity.getId());
    badge.setDescription("Desc_2");
    badgeService.updateBadge(BadgeMapper.badgeToBadgeDTO(badge));
    BadgeDTO badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertNotNull(badge_);
    assertEquals(badge_.getDescription(), "Desc_2");
  }

  @Test
  public void testDeleteBadge() throws ObjectNotFoundException {
    assertNull(badgeService.findBadgeByTitle(BADGE_NAME));
    BadgeEntity badge = newBadge(1L);
    BadgeDTO badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertNotNull(badge_);
    badgeService.deleteBadge(badge.getId());
    badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertEquals(badge_.isDeleted(), true);
  }

  @Test
  public void testFindBadgesByDomain() {
    DomainEntity domainEntity1 = newDomain();
    DomainEntity domainEntity2 = newDomain();
    assertEquals(badgeStorage.findAll().size(), 0);
    assertEquals(badgeService.findBadgesByDomain(1L).size(), 0);
    assertEquals(badgeService.findBadgesByDomain(2L).size(), 0);
    newBadge("badge1", domainEntity1.getId());
    newBadge("badge2", domainEntity1.getId());
    newBadge("badge3", domainEntity1.getId());
    newBadge("badge4", domainEntity2.getId());
    newBadge("badge5", domainEntity2.getId());
    assertEquals(badgeService.findBadgesByDomain(domainEntity1.getId()).size(), 3);
    assertEquals(badgeService.findBadgesByDomain(domainEntity2.getId()).size(), 2);
  }

  @Test
  public void testFindEnabledBadgesByDomain() {
    DomainEntity domainEntity1 = newDomain();
    DomainEntity domainEntity2 = newDomain();
    assertEquals(badgeStorage.findAll().size(), 0);
    assertEquals(badgeService.findEnabledBadgesByDomain(domainEntity1.getId()).size(), 0);
    assertEquals(badgeService.findEnabledBadgesByDomain(domainEntity2.getId()).size(), 0);
    newBadge("badge1", domainEntity1.getId());
    newBadge("badge2", domainEntity1.getId());
    newBadge("badge3", domainEntity1.getId());
    newBadge("badge4", domainEntity2.getId());
    newBadge("badge5", domainEntity2.getId());
    assertEquals(badgeService.findEnabledBadgesByDomain(domainEntity1.getId()).size(), 3);
    assertEquals(badgeService.findEnabledBadgesByDomain(domainEntity2.getId()).size(), 2);
    BadgeEntity badge_ = badgeStorage.findBadgeByTitle("badge1");
    badge_.setEnabled(false);
    badgeStorage.update(badge_);
    assertEquals(badgeService.findEnabledBadgesByDomain(domainEntity1.getId()).size(), 2);
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
    try {
      assertEquals(badgeService.getAllBadgesWithNullDomain().size(), 0);
      BadgeEntity badge_ = badgeStorage.findBadgeByTitle("badge1");
      badge_.setDomainEntity(null);
      badgeStorage.update(badge_);
      assertEquals(badgeService.getAllBadgesWithNullDomain().size(), 1);
    } catch (Exception e) {
      fail("Error when getting the list of badges with null domain", e);
    }
  }
}

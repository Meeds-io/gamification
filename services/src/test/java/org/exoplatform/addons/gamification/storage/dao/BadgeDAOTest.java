package org.exoplatform.addons.gamification.storage.dao;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class BadgeDAOTest extends AbstractServiceTest {

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

package org.exoplatform.addons.gamification.service.configuration;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class BadgeServiceTest extends AbstractServiceTest {

  @Test
  public void testFindBadgeByTitle() {
    assertNull(badgeService.findBadgeByTitle(BADGE_NAME));
    BadgeEntity badge = newBadge();
    BadgeDTO badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertNotNull(badge_);
    assertEquals(badge.getTitle(), badge_.getTitle());
  }

  @Test
  public void testGetAllBadges() {
    assertEquals(badgeService.getAllBadges().size(), 0);
    newBadge("badge1", "domain1");
    newBadge("badge2", "domain2");
    newBadge("badge3", "domain3");
    newBadge("badge4", "domain4");
    newBadge("badge5", "domain5");
    assertEquals(badgeService.getAllBadges().size(), 5);
  }

  @Test
  public void testAddBadge() {
    assertNull(badgeService.findBadgeByTitle(BADGE_NAME));
    BadgeDTO badge = new BadgeDTO();
    badge.setTitle(BADGE_NAME);
    badge.setDescription("Description");
    badge.setNeededScore(Integer.parseInt(TEST_GLOBAL_SCORE));
    badge.setDomain(GAMIFICATION_DOMAIN);
    badge.setIconFileId(10245);
    badge.setEnabled(true);
    badge.setDeleted(false);
    badge.setCreatedBy(TEST_USER_SENDER);
    badge.setLastModifiedBy(TEST_USER_SENDER);
    badge = badgeService.addBadge(badge);
    assertNotNull(badge);
    assertNotNull(badgeService.findBadgeByTitle(BADGE_NAME));
  }

  @Test
  public void testUpdateBadge() {
    BadgeEntity badge = newBadge();
    badge.setDescription("Desc_2");
    badgeService.updateBadge(badgeMapper.badgeToBadgeDTO(badge));
    BadgeDTO badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertNotNull(badge_);
    assertEquals(badge_.getDescription(), "Desc_2");
  }

  @Test
  public void testDeleteBadge() {
    assertNull(badgeService.findBadgeByTitle(BADGE_NAME));
    BadgeEntity badge = newBadge();
    BadgeDTO badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertNotNull(badge_);
    badgeService.deleteBadge(badge.getId());
    badge_ = badgeService.findBadgeByTitle(BADGE_NAME);
    assertEquals(badge_.isDeleted(), true);
  }

  @Test
  public void testFindBadgesByDomain() {
    assertEquals(badgeStorage.findAll().size(), 0);
    assertEquals(badgeService.findBadgesByDomain("domain1").size(), 0);
    assertEquals(badgeService.findBadgesByDomain("domain2").size(), 0);
    newBadge("badge1", "domain1");
    newBadge("badge2", "domain1");
    newBadge("badge3", "domain1");
    newBadge("badge4", "domain2");
    newBadge("badge5", "domain2");
    assertEquals(badgeService.findBadgesByDomain("domain1").size(), 3);
    assertEquals(badgeService.findBadgesByDomain("domain2").size(), 2);
  }

  @Test
  public void testFindEnabledBadgesByDomain() {
    assertEquals(badgeStorage.findAll().size(), 0);
    assertEquals(badgeService.findEnabledBadgesByDomain("domain1").size(), 0);
    assertEquals(badgeService.findEnabledBadgesByDomain("domain2").size(), 0);
    newBadge("badge1", "domain1");
    newBadge("badge2", "domain1");
    newBadge("badge3", "domain1");
    newBadge("badge4", "domain2");
    newBadge("badge5", "domain2");
    assertEquals(badgeService.findEnabledBadgesByDomain("domain1").size(), 3);
    assertEquals(badgeService.findEnabledBadgesByDomain("domain2").size(), 2);
    BadgeEntity badge_ = badgeStorage.findBadgeByTitle("badge1");
    badge_.setEnabled(false);
    badgeStorage.update(badge_);
    assertEquals(badgeService.findEnabledBadgesByDomain("domain1").size(), 2);
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

  @Test
  public void testGetDomainListFromBadges() {
    assertEquals(badgeStorage.findAll().size(), 0);
    newBadge("badge1", "domain1");
    newBadge("badge2", "domain1");
    newBadge("badge3", "domain2");
    newBadge("badge4", "domain2");
    newBadge("badge5", "domain2");
    assertEquals(badgeStorage.findAll().size(), 5);
    assertEquals(badgeService.getDomainListFromBadges().size(), 2);
  }
}

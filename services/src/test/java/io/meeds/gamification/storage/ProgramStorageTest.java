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

package io.meeds.gamification.storage;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.UserStatus;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.storage.cached.ProgramCachedStorage;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

@SuppressWarnings("deprecation")
public class ProgramStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveDomain() {
    assertEquals(0, programDAO.findAll().size());
    ProgramDTO domain = new ProgramDTO();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_EARNER);
    domain.setLastModifiedBy(TEST_USER_EARNER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    domain.setType(EntityType.AUTOMATIC.name());
    domain.setCreatedDate(Utils.toRFC3339Date(new Date()));
    domain.setBudget(20L);
    domain.setOwnerIds(Collections.singleton(1L));
    domain.setCoverFileId(1L);
    ProgramDTO savedDomain = programStorage.saveProgram(domain);
    assertNotNull(savedDomain);
    assertNotEquals(0, savedDomain.getId());

    domain.setCoverUploadId("1");
    assertThrows(IllegalStateException.class, () -> programStorage.saveProgram(domain));

  }

  @Test
  public void testGetAllDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programStorage.getProgramIdsByFilter(filter, OFFSET, 10).size());
  }

  @Test
  public void testCountDomains() {
    ProgramFilter filter = new ProgramFilter();
    filter.setType(EntityFilterType.ALL);
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setAllSpaces(true);
    assertEquals(0, programStorage.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain1", true, new HashSet<>());
    newDomain(EntityType.MANUAL, "domain2", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain3", true, new HashSet<>());
    newDomain(EntityType.AUTOMATIC, "domain4", true, new HashSet<>());
    assertEquals(4, programStorage.countPrograms(filter));

    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(2, programStorage.countPrograms(filter));
    newDomain(EntityType.AUTOMATIC, "domain5", false, new HashSet<>());
    assertEquals(2, programStorage.countPrograms(filter));

    filter.setType(EntityFilterType.MANUAL);
    assertEquals(2, programStorage.countPrograms(filter));
    newDomain(EntityType.MANUAL, "domain6", false, new HashSet<>());
    assertEquals(2, programDAO.countPrograms(filter));

    filter.setStatus(EntityStatusType.ALL);
    filter.setType(EntityFilterType.ALL);
    assertEquals(6, programStorage.countPrograms(filter));
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, programStorage.countPrograms(filter));
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(1, programStorage.countPrograms(filter));
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(1, programStorage.countPrograms(filter));
  }

  @Test
  public void testTheAdministratorsAreReadThroughTheCacheAClusterInvalidates() throws Exception {
    ExoCache<String, List<String>> cache = ExoContainerContext.getService(CacheService.class)
                                                              .getCacheInstance(ProgramCachedStorage.ADMINISTRATORS_CACHE_NAME);
    cache.clearCache();

    List<String> administrators = programStorage.getAdministrators();

    // Held in the declared cache, whose entry a change on another node removes
    assertEquals(List.of(administrators), cache.getCachedObjects());
    assertEquals(administrators, programStorage.getAdministrators());

    // A user enabled again may be a rewarding administrator the list lacks
    UserHandler userHandler = ExoContainerContext.getService(OrganizationService.class).getUserHandler();
    User user = userHandler.createUserInstance("administratorsCacheUser");
    user.setFirstName("Administrators");
    user.setLastName("Cache");
    user.setEmail("administratorsCacheUser@example.com");
    userHandler.createUser(user, true);
    try {
      programStorage.getAdministrators();
      userHandler.setEnabled(user.getUserName(), false, true);
      assertEquals(0, cache.getCacheSize());

      programStorage.getAdministrators();
      userHandler.removeUser(user.getUserName(), true);
      assertEquals(0, cache.getCacheSize());
    } finally {
      if (userHandler.findUserByName(user.getUserName(), UserStatus.ANY) != null) {
        userHandler.removeUser(user.getUserName(), true);
      }
    }
  }

  @Test
  public void testAChangeOfTheRewardingGroupMembersRemovesTheCachedAdministrators() throws Exception {
    ExoCache<String, List<String>> cache = ExoContainerContext.getService(CacheService.class)
                                                              .getCacheInstance(ProgramCachedStorage.ADMINISTRATORS_CACHE_NAME);
    OrganizationService organizationService = ExoContainerContext.getService(OrganizationService.class);
    GroupHandler groupHandler = organizationService.getGroupHandler();
    Group rewardingGroup = groupHandler.findGroupById(Utils.REWARDING_GROUP);
    if (rewardingGroup == null) {
      Group parent = groupHandler.findGroupById("/platform");
      rewardingGroup = groupHandler.createGroupInstance();
      rewardingGroup.setGroupName("rewarding");
      rewardingGroup.setLabel("Rewarding");
      groupHandler.addChild(parent, rewardingGroup, true);
    }
    UserHandler userHandler = organizationService.getUserHandler();
    User user = userHandler.createUserInstance("rewardingMembershipUser");
    user.setFirstName("Rewarding");
    user.setLastName("Membership");
    user.setEmail("rewardingMembershipUser@example.com");
    userHandler.createUser(user, true);
    MembershipType member = organizationService.getMembershipTypeHandler().findMembershipType("member");
    try {
      programStorage.getAdministrators();
      assertEquals(1, cache.getCacheSize());

      organizationService.getMembershipHandler().linkMembership(user, rewardingGroup, member, true);
      waitUntilEmpty(cache);
      assertTrue(programStorage.getAdministrators().contains(user.getUserName()));

      organizationService.getMembershipHandler()
                         .removeMembership(organizationService.getMembershipHandler()
                                                              .findMembershipByUserGroupAndType(user.getUserName(),
                                                                                                Utils.REWARDING_GROUP,
                                                                                                "member")
                                                              .getId(),
                                           true);
      waitUntilEmpty(cache);
      assertFalse(programStorage.getAdministrators().contains(user.getUserName()));
    } finally {
      userHandler.removeUser(user.getUserName(), true);
    }
  }

  private static void waitUntilEmpty(ExoCache<String, List<String>> cache) throws InterruptedException {
    // The membership listener removes the entry asynchronously
    long end = System.currentTimeMillis() + 10_000;
    while (cache.getCacheSize() > 0 && System.currentTimeMillis() < end) {
      Thread.sleep(50);
    }
    assertEquals(0, cache.getCacheSize());
  }
}

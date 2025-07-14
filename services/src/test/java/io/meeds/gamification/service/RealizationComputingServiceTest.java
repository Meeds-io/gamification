/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.service;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.test.AbstractServiceTest;

import lombok.SneakyThrows;

public class RealizationComputingServiceTest extends AbstractServiceTest { // NOSONAR

  private static final String ADMIN_USER = "root1";

  private String              adminIdentityId;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adminIdentityId = identityManager.getOrCreateUserIdentity(ADMIN_USER).getId();
  }

  @SneakyThrows
  public void testGetLockingRules() {
    RuleDTO disabledRule = newRuleDTO();
    disabledRule.setEnabled(false);
    RuleDTO lockingRule01 = newRuleDTO("lockingRule01");
    RuleDTO lockingRule02 = newRuleDTO("lockingRule02");
    disabledRule.setPrerequisiteRuleIds(Stream.of(lockingRule01.getId(), lockingRule02.getId())
                                              .collect(Collectors.toSet()));
    ruleService.updateRule(disabledRule, ADMIN_USER);

    RuleDTO lockedRule1 = newRuleDTO();
    RuleDTO lockingRule1 = newRuleDTO("lockingRule1");
    RuleDTO lockingRule2 = newRuleDTO("lockingRule2");
    lockedRule1.setPrerequisiteRuleIds(Stream.of(lockingRule1.getId(), lockingRule2.getId())
                                             .collect(Collectors.toSet()));
    ruleService.updateRule(lockedRule1, ADMIN_USER);
    for (int i = 0; i < 10; i++) {
      newRuleDTO();
    }
    RuleDTO lockedRule2 = newRuleDTO();
    RuleDTO lockingRule21 = newRuleDTO("lockingRule21");
    RuleDTO lockingRule22 = newRuleDTO("lockingRule22");
    lockedRule2.setPrerequisiteRuleIds(Stream.of(lockingRule21.getId(), lockingRule22.getId())
                                             .collect(Collectors.toSet()));
    ruleService.updateRule(lockedRule2, ADMIN_USER);

    RuleFilter filter = new RuleFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    filter.setType(EntityFilterType.ALL);
    filter.setSortBy("createdDate");
    filter.setSortDescending(true);

    List<RuleDTO> lockingRules = realizationComputingService.getLockingRules(filter, ADMIN_USER, 0, 10);
    assertNotNull(lockingRules);
    assertEquals(4, lockingRules.size());
    assertEquals(lockingRule22.getId(), lockingRules.get(0).getId());
    assertEquals(lockingRule21.getId(), lockingRules.get(1).getId());
    assertEquals(lockingRule2.getId(), lockingRules.get(2).getId());
    assertEquals(lockingRule1.getId(), lockingRules.get(3).getId());

    achieveRule(lockingRule22);

    lockingRules = realizationComputingService.getLockingRules(filter, ADMIN_USER, 0, 10);
    assertNotNull(lockingRules);
    assertEquals(3, lockingRules.size());
    assertEquals(lockingRule21.getId(), lockingRules.get(0).getId());
    assertEquals(lockingRule2.getId(), lockingRules.get(1).getId());
    assertEquals(lockingRule1.getId(), lockingRules.get(2).getId());

    achieveRule(lockingRule1);

    lockingRules = realizationComputingService.getLockingRules(filter, ADMIN_USER, 0, 10);
    assertNotNull(lockingRules);
    assertEquals(2, lockingRules.size());
    assertEquals(lockingRule21.getId(), lockingRules.get(0).getId());
    assertEquals(lockingRule2.getId(), lockingRules.get(1).getId());

    lockingRules = realizationComputingService.getLockingRules(filter, ADMIN_USER, 1, 10);
    assertNotNull(lockingRules);
    assertEquals(1, lockingRules.size());
    assertEquals(lockingRule2.getId(), lockingRules.get(0).getId());

    lockingRules = realizationComputingService.getLockingRules(filter, ADMIN_USER, 0, 1);
    assertNotNull(lockingRules);
    assertEquals(1, lockingRules.size());
    assertEquals(lockingRule21.getId(), lockingRules.get(0).getId());

    achieveRule(lockingRule2);
    achieveRule(lockingRule21);

    lockingRules = realizationComputingService.getLockingRules(filter, ADMIN_USER, 0, 10);
    assertNotNull(lockingRules);
    assertEquals(0, lockingRules.size());
  }

  @SneakyThrows
  private RuleDTO newRuleDTO(String eventName) {
    RuleDTO rule = newRuleDTO();
    long eventId = rule.getEvent().getId();
    EventDTO event = eventService.getEvent(eventId);
    event.setTitle(eventName);
    eventService.updateEvent(event);
    rule.setEvent(event);
    rule.setTitle(eventName);
    ruleService.updateRule(rule);
    return rule;
  }

  private void achieveRule(RuleDTO rule) {
    RealizationDTO realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                       null,
                                                                       adminIdentityId,
                                                                       adminIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .getFirst();
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
  }

}

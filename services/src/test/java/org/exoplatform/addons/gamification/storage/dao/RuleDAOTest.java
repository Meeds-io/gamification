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

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import java.util.Collections;
import java.util.Date;

public class RuleDAOTest extends AbstractServiceTest {

  @Test
  public void testFindEnableRuleByTitle() {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertNull(ruleDAO.findEnableRuleByTitle(RULE_NAME));
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleDAO.findEnableRuleByTitle(RULE_NAME));
    ruleEntity.setEnabled(false);
    ruleDAO.update(ruleEntity);
    assertNull(ruleDAO.findEnableRuleByTitle(RULE_NAME));
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertEquals(ruleDAO.findEnabledRulesByEvent("rule1").size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleDAO.findEnabledRulesByEvent("rule1").size(), 3);
    r1.setEnabled(false);
    ruleDAO.update(r1);
    assertEquals(ruleDAO.findEnabledRulesByEvent("rule1").size(), 2);
  }

  @Test
  public void testFindRuleByTitle() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule();
    assertNotNull(ruleDAO.findRuleByTitle(RULE_NAME));
  }

  @Test
  public void getAllAutomaticRules() {
    assertEquals(ruleDAO.getAllAutomaticRules().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule1", "domain3");
    assertEquals(ruleDAO.getAllAutomaticRules().size(), 3);
  }

  @Test
  public void testGetActiveRules() {
    assertEquals(ruleDAO.getActiveRules().size(), 0);
    newRule("rule1", "domain1", false);
    newRule("rule2", "domain2", true);
    newRule("rule3", "domain3", true);
    assertEquals(ruleDAO.getActiveRules().size(), 2);
  }

  @Test
  public void testGetAllRulesByDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain1");
    newRule("rule3", "domain2");
    assertEquals(ruleDAO.getAllRulesByDomain("domain1").size(), 2);
    assertEquals(ruleDAO.getAllRulesByDomain("domain2").size(), 1);
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleDAO.getAllRulesWithNullDomain().size(), 0);
    r1.setDomainEntity(null);
    ruleDAO.update(r1);
    assertEquals(ruleDAO.getAllRulesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetDomainList() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain2");
    newRule("rule3", "domain2");
    assertEquals(ruleDAO.getDomainList().size(), 2);
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule2", "domain3");
    assertEquals(ruleDAO.getAllEvents().size(), 2);
  }
  
  @Test
  public void testFindAllChallengesByUserByDomain() {
    assertEquals(0, ruleDAO.findAllChallengesByUserByDomain(0, 0, 0, Collections.singletonList(0l)).size());
    DomainEntity domain1 = newDomain("domain1");
    DomainEntity domain2 = newDomain("domain2");
    newChallenge("rule1", domain1.getTitle());
    newChallenge("rule2", domain1.getTitle());
    newChallenge("rule3", domain2.getTitle());
    assertEquals(2, ruleDAO.findAllChallengesByUserByDomain(domain1.getId(), 0, 3,Collections.singletonList(1l)).size());
  }

   @Test
  public void testCountAllChallengesByUserByDomain() {
    assertEquals(0, ruleDAO.countAllChallengesByUserByDomain(0, Collections.singletonList(0l)));
    DomainEntity domain1 = newDomain("domain1");
    DomainEntity domain2 = newDomain("domain2");
    newChallenge("rule1", domain1.getTitle());
    newChallenge("rule2", domain1.getTitle());
    newChallenge("rule3", domain2.getTitle());
    assertEquals(2, ruleDAO.countAllChallengesByUserByDomain(domain1.getId(),Collections.singletonList(1l)));
  }

}

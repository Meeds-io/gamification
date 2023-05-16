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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package io.meeds.gamification.service;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;
import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.Date;

import org.junit.Test;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.RuleBuilder;
import io.meeds.gamification.utils.Utils;

public class RuleServiceTest extends AbstractServiceTest {
  private final Identity adminAclIdentity =
                                          new Identity("root1",
                                                       Collections.singleton(new MembershipEntry("/platform/administrators")));

  @Override
  public void setUp() throws Exception {
    super.setUp();
    Identity adminAclIdentity = new Identity("root", Collections.singleton(new MembershipEntry(Utils.REWARDING_GROUP)));
    identityRegistry.register(adminAclIdentity);
  }

  @Test
  public void testFindRuleById() {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleService.findRuleById(ruleEntity.getId()));
    assertThrows(IllegalArgumentException.class, () -> ruleService.findRuleById(0));
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> ruleService.findActiveRulesByEvent(null));
    assertEquals(ruleService.findActiveRulesByEvent("rule1").size(), 0);
    RuleEntity r1 = newRule("rule1", 1L);
    newRule("rule1", 2L);
    newRule("rule1", 3L);
    assertEquals(ruleService.findActiveRulesByEvent("rule1").size(), 3);
    r1.setEnabled(false);
    ruleDAO.update(r1);
    assertEquals(ruleService.findActiveRulesByEvent("rule1").size(), 2);
  }

  @Test
  public void testFindRuleByTitle() {
    assertEquals(ruleDAO.findAll().size(), 0);
    newRule();
    assertNotNull(ruleService.findRuleByTitle(RULE_NAME));
    assertThrows(IllegalArgumentException.class, () -> ruleService.findRuleByTitle(null));
  }

  @Test
  public void testFindRuleByIdAndUser() throws IllegalAccessException, ObjectNotFoundException {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertNotNull(rule);

    Long ruleId = rule.getId();
    RuleDTO foundRule = ruleService.findRuleById(ruleId, "root1");
    assertNotNull(foundRule);

    ruleService.deleteRuleById(ruleId, "root1");
    assertThrows(ObjectNotFoundException.class, () -> ruleService.findRuleById(ruleId, "root1"));
  }

  @Test
  public void testFindRuleAfterDomainAudienceChange() throws IllegalAccessException, ObjectNotFoundException {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertNotNull(rule);
    ProgramDTO program = rule.getProgram();
    assertNotNull(program);

    assertNotNull(ruleService.findRuleById(rule.getId()));
    program.setAudienceId(program.getAudienceId() + 1);
    ProgramDTO updatedProgram = programService.updateProgram(program, adminAclIdentity);

    RuleDTO updatedRuleAudience = ruleService.findRuleById(rule.getId());
    assertNotNull(updatedRuleAudience);
    assertNotNull(updatedRuleAudience.getProgram());
    assertEquals(updatedRuleAudience.getProgram().getId(), program.getId());
    assertEquals(updatedRuleAudience.getProgram().getAudienceId(), program.getAudienceId());
    assertEquals(updatedRuleAudience.getProgram().getAudienceId(), updatedProgram.getAudienceId());
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleDAO.findAll().size(), 0);
    ProgramEntity firstDomain = newDomain("firstDomain");
    ProgramEntity secondDomain = newDomain("secondDomain");
    ProgramEntity thirdDomain = newDomain("thirdDomain");
    newRule("rule1", firstDomain.getId());
    newRule("rule1", secondDomain.getId());
    newRule("rule2", thirdDomain.getId());
    assertEquals(ruleService.getAllEvents().size(), 2);
  }

  @Test
  public void testDeleteRule() throws Exception {
    RuleEntity ruleEntity = newRule();
    assertFalse(ruleEntity.isDeleted());
    ruleService.deleteRuleById(ruleEntity.getId(), "root1");
    assertTrue(ruleEntity.isDeleted());
    assertThrows(IllegalArgumentException.class, () -> ruleService.deleteRuleById(null, "root"));
  }

  @Test
  public void testDeleteNotEndedRule() throws Exception {
    RuleEntity ruleEntity = newRule();
    assertFalse(ruleEntity.isDeleted());

    RealizationDTO realization = realizationService.createRealizations(ruleEntity.getEvent(),
                                                                       TEST_USER_EARNER,
                                                                       TEST_USER_RECEIVER,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    RealizationDTO latestRealization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertNotNull(latestRealization);
    assertEquals(realization.getId(), latestRealization.getId());

    RuleDTO rule = ruleService.deleteRuleById(ruleEntity.getId(), "root1");
    assertNotNull(rule);
    assertTrue(rule.isDeleted());

    restartTransaction();
    rule = ruleService.findRuleById(rule.getId());
    assertNotNull(rule);

    latestRealization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertNotNull(latestRealization);
    assertEquals(realization.getId(), latestRealization.getId());
  }

  @Test
  public void testAddRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> ruleService.createRule(null, "root10"));
    RuleEntity rule = new RuleEntity();
    rule.setScore(Integer.parseInt(TEST__SCORE));
    rule.setTitle(RULE_NAME);
    rule.setDescription("Description");
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setEvent(RULE_NAME);
    rule.setCreatedBy(TEST_USER_EARNER);
    rule.setCreatedDate(new Date());
    rule.setLastModifiedBy(TEST_USER_EARNER);
    rule.setLastModifiedDate(new Date());
    rule.setDomainEntity(newDomain(GAMIFICATION_DOMAIN));
    rule.setType(EntityType.AUTOMATIC);
    rule.setRecurrence(RecurrenceType.NONE);
    ruleService.createRule(RuleBuilder.fromEntity(domainStorage, rule), "root1");
    assertEquals(ruleDAO.findAll().size(), 1);
  }

  @Test
  public void testUpdateRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(ObjectNotFoundException.class, () -> ruleService.updateRule(new RuleDTO(), "root"));
    RuleEntity ruleEntity = newRule();
    ruleEntity.setDescription("new_description");
    ruleService.updateRule(RuleBuilder.fromEntity(domainStorage, ruleEntity), "root1");
    RuleDTO rule = ruleService.findRuleById(ruleEntity.getId());
    assertEquals(ruleEntity.getDescription(), rule.getDescription());

    ruleService.deleteRuleById(rule.getId(), "root1");
    assertThrows(ObjectNotFoundException.class, () -> ruleService.updateRule(rule, "root"));
  }

  @Test
  public void getFindAllRules() {
    newRuleDTO();
    newRuleDTO();
    newRuleDTO();
    assertEquals(2, ruleService.findAllRules(0, 2).size());
    assertEquals(3, ruleService.findAllRules(0, 3).size());
  }

  @Test
  public void testGetRulesByFilter() throws Exception {
    newRuleDTO();
    ProgramEntity domainEntity1 = newDomain("domain1");
    ProgramEntity domainEntity2 = newDomain("domain2");
    RuleDTO ruleDTO1 = newRuleDTO("rule1", domainEntity1.getId());
    ruleDTO1.setEnabled(false);
    ruleService.updateRule(ruleDTO1, "root1");
    RuleDTO ruleDTO2 = newRuleDTO("rule2", domainEntity2.getId());
    ruleDTO2.setEnabled(false);
    ruleService.updateRule(ruleDTO2, "root1");
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(1, ruleService.getRules(ruleFilter,0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, ruleService.getRules(ruleFilter,0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    ruleFilter.setDomainId(domainId);
    assertEquals(1, ruleService.getRules(ruleFilter,0, 10).size());
    ruleFilter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(ruleFilter,0, 10).size());
    ruleFilter = new RuleFilter();
    ruleFilter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(3, ruleService.getRules(ruleFilter,0, 10).size());
    assertEquals(3, ruleService.findAllRules(0, 3).size());
  }

}

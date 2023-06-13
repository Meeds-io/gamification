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

import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.security.Identity;

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
import io.meeds.gamification.plugin.RuleConfigPlugin;
import io.meeds.gamification.storage.mapper.RuleMapper;
import io.meeds.gamification.test.AbstractServiceTest;

public class RuleServiceTest extends AbstractServiceTest {

  private static final String INTERNAL_USER     = "root50";

  private static final String SPACE_MEMBER_USER = "root10";

  private static final String ADMIN_USER        = "root1";

  private Identity            adminAclIdentity;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adminAclIdentity = registerAdministratorUser(ADMIN_USER);
    registerInternalUser(SPACE_MEMBER_USER);
    registerInternalUser(INTERNAL_USER);
  }

  @Test
  public void testFindRuleById() {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleService.findRuleById(ruleEntity.getId()));
    assertThrows(IllegalArgumentException.class, () -> ruleService.findRuleById(0));
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
    RuleDTO foundRule = ruleService.findRuleById(ruleId, ADMIN_USER);
    assertNotNull(foundRule);

    ruleService.deleteRuleById(ruleId, ADMIN_USER);
    assertThrows(ObjectNotFoundException.class, () -> ruleService.findRuleById(ruleId, ADMIN_USER));
  }

  @Test
  public void testFindRuleAfterDomainAudienceChange() throws IllegalAccessException, ObjectNotFoundException {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertNotNull(rule);
    ProgramDTO program = rule.getProgram();
    assertNotNull(program);

    assertNotNull(ruleService.findRuleById(rule.getId()));
    program.setSpaceId(program.getSpaceId() + 1);
    ProgramDTO updatedProgram = programService.updateProgram(program, adminAclIdentity);

    RuleDTO updatedRuleAudience = ruleService.findRuleById(rule.getId());
    assertNotNull(updatedRuleAudience);
    assertNotNull(updatedRuleAudience.getProgram());
    assertEquals(program.getId(), updatedRuleAudience.getProgram().getId());
    assertEquals(program.getSpaceId(), updatedRuleAudience.getProgram().getSpaceId());
    assertEquals(updatedProgram.getSpaceId(), updatedRuleAudience.getProgram().getSpaceId());
  }

  @Test
  public void testGetAllEvents() throws Exception {
    List<String> allEvents = ruleService.getAllEvents();
    int initialSize = allEvents.size();
    String eventName1 = "test-event1";
    String eventName2 = "test-event2";

    InitParams params = new InitParams();
    ValueParam valueParam = new ValueParam();
    valueParam.setName("rule-event");
    valueParam.setValue(eventName1);
    params.addParameter(valueParam);
    params.addParameter(valueParam);
    ruleRegistry.addPlugin(new RuleConfigPlugin(params));

    params = new InitParams();
    valueParam = new ValueParam();
    valueParam.setName("rule-title");
    valueParam.setValue(eventName2);
    params.addParameter(valueParam);
    params.addParameter(valueParam);
    ruleRegistry.addPlugin(new RuleConfigPlugin(params));

    allEvents = ruleService.getAllEvents();
    assertEquals(initialSize + 2, allEvents.size());
    assertTrue(allEvents.contains(eventName1));
    assertTrue(allEvents.contains(eventName2));
  }

  @Test
  public void testDeleteRule() throws Exception {
    RuleEntity ruleEntity = newRule();
    assertFalse(ruleEntity.isDeleted());
    ruleService.deleteRuleById(ruleEntity.getId(), ADMIN_USER);
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

    List<RealizationDTO> realizations = realizationService.findRealizationsByIdentityId(TEST_USER_EARNER, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    RealizationDTO latestRealization = realizations.get(0);
    assertNotNull(latestRealization);
    assertEquals(realization.getId(), latestRealization.getId());

    RuleDTO rule = ruleService.deleteRuleById(ruleEntity.getId(), ADMIN_USER);
    assertNotNull(rule);
    assertTrue(rule.isDeleted());

    restartTransaction();
    rule = ruleService.findRuleById(rule.getId());
    assertNotNull(rule);

    realizations = realizationService.findRealizationsByIdentityId(TEST_USER_EARNER, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    latestRealization = realizations.get(0);
    assertNotNull(latestRealization);
    assertEquals(realization.getId(), latestRealization.getId());
  }

  @Test
  public void testAddRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> ruleService.createRule(null, SPACE_MEMBER_USER));
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
    ruleService.createRule(RuleMapper.fromEntity(domainStorage, rule), ADMIN_USER);
    assertEquals(ruleDAO.findAll().size(), 1);
  }

  @Test
  public void testUpdateRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(ObjectNotFoundException.class, () -> ruleService.updateRule(new RuleDTO(), "root"));
    RuleEntity ruleEntity = newRule();
    ruleEntity.setDescription("new_description");
    ruleService.updateRule(RuleMapper.fromEntity(domainStorage, ruleEntity), ADMIN_USER);
    RuleDTO rule = ruleService.findRuleById(ruleEntity.getId());
    assertEquals(ruleEntity.getDescription(), rule.getDescription());
    assertThrows(IllegalAccessException.class, () -> ruleService.updateRule(rule, SPACE_MEMBER_USER));

    ruleService.deleteRuleById(rule.getId(), ADMIN_USER);
    assertThrows(ObjectNotFoundException.class, () -> ruleService.updateRule(rule, "root"));
  }

  @Test
  public void testGetRulesByAdmin() throws Exception {
    newRuleDTO();
    ProgramEntity domainEntity1 = newDomain("domain1");
    ProgramEntity domainEntity2 = newDomain("domain2");
    RuleDTO ruleDTO1 = newRuleDTO("rule1", domainEntity1.getId());
    ruleDTO1.setEnabled(false);
    ruleService.updateRule(ruleDTO1, ADMIN_USER);
    RuleDTO ruleDTO2 = newRuleDTO("rule2", domainEntity2.getId());
    ruleDTO2.setEnabled(false);
    ruleService.updateRule(ruleDTO2, ADMIN_USER);
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(1, ruleService.getRules(ruleFilter, ADMIN_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, ruleService.getRules(ruleFilter, ADMIN_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    ruleFilter.setProgramId(domainId);
    assertEquals(1, ruleService.getRules(ruleFilter, ADMIN_USER, 0, 10).size());
    ruleFilter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(ruleFilter, ADMIN_USER, 0, 10).size());
    ruleFilter = new RuleFilter();
    ruleFilter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(3, ruleService.getRules(ruleFilter, ADMIN_USER, 0, 10).size());
    assertEquals(3, ruleDAO.count().intValue());
  }

  @Test
  public void testGetRulesBySpaceMember() throws Exception {
    newRuleDTO();
    ProgramEntity domainEntity1 = newDomain("domain1");
    ProgramEntity domainEntity2 = newDomain("domain2");
    RuleDTO ruleDTO1 = newRuleDTO("rule1", domainEntity1.getId());
    ruleDTO1.setEnabled(false);
    ruleService.updateRule(ruleDTO1, ADMIN_USER);
    RuleDTO ruleDTO2 = newRuleDTO("rule2", domainEntity2.getId());
    ruleDTO2.setEnabled(false);
    ruleService.updateRule(ruleDTO2, ADMIN_USER);
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(1, ruleService.getRules(ruleFilter, SPACE_MEMBER_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, ruleService.getRules(ruleFilter, SPACE_MEMBER_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    ruleFilter.setProgramId(domainId);
    assertEquals(1, ruleService.getRules(ruleFilter, SPACE_MEMBER_USER, 0, 10).size());
    ruleFilter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(ruleFilter, SPACE_MEMBER_USER, 0, 10).size());
    ruleFilter = new RuleFilter();
    ruleFilter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(3, ruleService.getRules(ruleFilter, SPACE_MEMBER_USER, 0, 10).size());
    assertEquals(3, ruleDAO.count().intValue());
  }

  @Test
  public void testGetRulesByNonMemberUser() throws Exception {
    newRuleDTO();
    ProgramEntity domainEntity1 = newDomain("domain1");
    ProgramEntity domainEntity2 = newDomain("domain2");
    RuleDTO ruleDTO1 = newRuleDTO("rule1", domainEntity1.getId());
    ruleDTO1.setEnabled(false);
    ruleService.updateRule(ruleDTO1, ADMIN_USER);
    RuleDTO ruleDTO2 = newRuleDTO("rule2", domainEntity2.getId());
    ruleDTO2.setEnabled(false);
    ruleService.updateRule(ruleDTO2, ADMIN_USER);
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(0, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(0, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    ruleFilter.setProgramId(domainId);
    assertEquals(0, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter = new RuleFilter();
    ruleFilter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(0, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    assertEquals(3, ruleDAO.count().intValue());
  }

  @Test
  public void testGetOpenRulesByInternalUser() throws Exception {
    RuleDTO ruleDTO = newRuleDTO();
    ProgramDTO program = ruleDTO.getProgram();
    program.setOpen(true);
    programService.updateProgram(program, adminAclIdentity);

    ProgramEntity domainEntity1 = newOpenProgram("domain1");
    ProgramEntity domainEntity2 = newOpenProgram("domain2");
    RuleDTO ruleDTO1 = newRuleDTO("rule1", domainEntity1.getId());
    ruleDTO1.setEnabled(false);
    ruleService.updateRule(ruleDTO1, ADMIN_USER);
    RuleDTO ruleDTO2 = newRuleDTO("rule2", domainEntity2.getId());
    ruleDTO2.setEnabled(false);
    ruleService.updateRule(ruleDTO2, ADMIN_USER);
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setEntityStatusType(EntityStatusType.ENABLED);
    assertEquals(1, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.DISABLED);
    assertEquals(2, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter.setEntityStatusType(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    ruleFilter.setProgramId(domainId);
    assertEquals(1, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter.setEntityFilterType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    ruleFilter = new RuleFilter();
    ruleFilter.setEntityFilterType(EntityFilterType.AUTOMATIC);
    assertEquals(3, ruleService.getRules(ruleFilter, INTERNAL_USER, 0, 10).size());
    assertEquals(3, ruleDAO.count().intValue());
  }

}

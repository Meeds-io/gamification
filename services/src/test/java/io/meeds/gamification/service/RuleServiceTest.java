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
import static io.meeds.gamification.utils.Utils.POST_PUBLISH_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_OBJECT_TYPE;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.space.model.Space;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.RulePublication;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.model.filter.RuleFilter;
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
    long activityId = foundRule.getActivityId();
    assertTrue(activityId > 0);
    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(activityId));
    assertNotNull(activity);
    assertTrue(activity.isHidden());
    activity.isHidden(false);
    activityManager.updateActivity(activity);
    activity = activityManager.getActivity(String.valueOf(activityId));
    assertNotNull(activity);
    assertFalse(activity.isHidden());

    ruleService.deleteRuleById(ruleId, ADMIN_USER);
    activity = activityManager.getActivity(String.valueOf(activityId));
    assertNull(activity);
  }

  @Test
  public void testSwitchRuleAudience() throws IllegalAccessException, ObjectNotFoundException {
    assertEquals(ruleDAO.findAll().size(), 0);
    RuleDTO rule = newRuleDTO();
    assertNotNull(rule);

    Long ruleId = rule.getId();
    RuleDTO foundRule = ruleService.findRuleById(ruleId, ADMIN_USER);
    assertNotNull(foundRule);
    long activityId = foundRule.getActivityId();
    assertTrue(activityId > 0);
    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(activityId));
    assertNotNull(activity);
    assertTrue(activity.isHidden());
    activity.isHidden(false);
    activityManager.updateActivity(activity);
    activity = activityManager.getActivity(String.valueOf(activityId));
    assertNotNull(activity);
    assertFalse(activity.isHidden());

    long programId = rule.getProgram().getId();
    ProgramDTO program = programService.getProgramById(programId);
    program.setSpaceId(Long.parseLong(TEST_SPACE2_ID));
    programService.updateProgram(program);

    foundRule = ruleService.findRuleById(ruleId, ADMIN_USER);
    assertNotNull(foundRule);
    assertTrue(foundRule.isEnabled());
    assertFalse(foundRule.isDeleted());
    assertNotEquals(0, foundRule.getActivityId());
    assertNotEquals(activityId, foundRule.getActivityId());

    activity = activityManager.getActivity(String.valueOf(activityId));
    assertNotNull(activity);
    assertTrue(activity.isHidden());

    activity = activityManager.getActivity(String.valueOf(foundRule.getActivityId()));
    assertNotNull(activity);
    assertTrue(activity.isHidden());
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
  public void testDeleteRule() throws Exception {
    RuleEntity ruleEntity = newRule();
    assertFalse(ruleEntity.isDeleted());
    ruleService.deleteRuleById(ruleEntity.getId(), ADMIN_USER);
    assertTrue(ruleEntity.isDeleted());
    assertThrows(IllegalArgumentException.class, () -> ruleService.deleteRuleById(0, "root"));
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

    RealizationFilter identityFilter = new RealizationFilter();
    identityFilter.setEarnerIds(Collections.singletonList(TEST_USER_EARNER));
    List<RealizationDTO> realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
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

    realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    latestRealization = realizations.get(0);
    assertNotNull(latestRealization);
    assertEquals(realization.getId(), latestRealization.getId());
  }

  @Test
  public void testCreateRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> ruleService.createRule(null, SPACE_MEMBER_USER));
    RuleEntity rule = new RuleEntity();
    rule.setScore(Integer.parseInt(TEST_SCORE));
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
    assertThrows(ObjectAlreadyExistsException.class, () -> ruleService.createRule(RuleMapper.fromEntity(domainStorage, rule), ADMIN_USER));
    assertEquals(ruleDAO.findAll().size(), 1);
  }

  @Test
  public void testCreateRuleWithNoEvent() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(IllegalArgumentException.class, () -> ruleService.createRule(null, SPACE_MEMBER_USER));
    RuleEntity rule = new RuleEntity();
    rule.setScore(Integer.parseInt(TEST_SCORE));
    rule.setTitle(RULE_NAME);
    rule.setDescription("Description");
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setCreatedBy(TEST_USER_EARNER);
    rule.setCreatedDate(new Date());
    rule.setLastModifiedBy(TEST_USER_EARNER);
    rule.setLastModifiedDate(new Date());
    rule.setDomainEntity(newDomain(GAMIFICATION_DOMAIN));
    rule.setType(EntityType.AUTOMATIC);
    rule.setRecurrence(RecurrenceType.NONE);
    ruleService.createRule(RuleMapper.fromEntity(domainStorage, rule), ADMIN_USER);
    assertEquals(ruleDAO.findAll().size(), 1);
    ruleService.createRule(RuleMapper.fromEntity(domainStorage, rule), ADMIN_USER);
    assertEquals(ruleDAO.findAll().size(), 2);
  }

  @Test
  public void testUpdateRule() throws Exception {
    assertEquals(ruleDAO.findAll().size(), 0);
    assertThrows(ObjectNotFoundException.class, () -> ruleService.updateRule(new RuleDTO(), "root"));
    RuleDTO createdRule = ruleService.findRuleById(newRuleDTO().getId(), ADMIN_USER);
    long activityId = createdRule.getActivityId();
    assertTrue(activityId > 0);

    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(activityId));
    assertNotNull(activity);
    assertTrue(activity.isHidden());
    assertTrue(StringUtils.isBlank(activity.getTitle()));
    assertNotNull(activity.getActivityStream());
    assertTrue(activity.getActivityStream().isSpace());
    Space space = spaceService.getSpaceByPrettyName(activity.getActivityStream().getPrettyId());
    assertNotNull(space);
    assertEquals(createdRule.getSpaceId(), Long.parseLong(space.getId()));

    String message = "Text Message";
    String description = "new_description";

    String paramName = "testParam";
    String paramValue = "testValue";
    RuleDTO rule = new RulePublication(createdRule,
                                       0,
                                       message,
                                       Collections.singletonMap(paramName, paramValue),
                                       true);
    rule.setDescription(description);
    ruleService.updateRule(rule, ADMIN_USER);
    RuleDTO updatedRule = ruleService.findRuleById(createdRule.getId());
    assertEquals(description, updatedRule.getDescription());
    assertThrows(IllegalAccessException.class, () -> ruleService.updateRule(updatedRule, SPACE_MEMBER_USER));

    activity = activityManager.getActivity(String.valueOf(activityId));
    assertNotNull(activity);
    assertFalse(activity.isHidden());
    assertEquals(String.valueOf(activityId), activity.getId());
    assertEquals(message, activity.getTitle());
    assertNotNull(activity.getTemplateParams());
    assertEquals(paramValue, activity.getTemplateParams().get(paramName));
    assertNotEquals(createdRule.getProgram().getLastModifiedDate(), updatedRule.getProgram().getLastModifiedDate());

    ruleService.deleteRuleById(rule.getId(), ADMIN_USER);
    assertThrows(ObjectNotFoundException.class, () -> ruleService.updateRule(updatedRule, "root"));
  }

  @Test
  public void testCreateRuleWithPublication() throws Exception {
    AtomicInteger triggerCount = new AtomicInteger(0);
    listenerService.addListener(POST_PUBLISH_RULE_EVENT, new Listener<RuleDTO, String>() {
      @Override
      public void onEvent(Event<RuleDTO, String> event) throws Exception {
        triggerCount.incrementAndGet();
      }
    });
    ProgramDTO program = newProgram(GAMIFICATION_DOMAIN);

    RulePublication rule = new RulePublication();
    rule.setScore(Integer.parseInt(TEST_SCORE));
    rule.setTitle(RULE_NAME);
    rule.setDescription("Description");
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setEvent(RULE_NAME);
    rule.setProgram(program);
    rule.setType(EntityType.AUTOMATIC);
    rule.setRecurrence(RecurrenceType.NONE);
    rule.setPublish(true);
    String message = "Test publication Message";
    rule.setMessage(message);
    RuleDTO createdRule = ruleService.createRule(rule, ADMIN_USER);
    assertEquals(ruleDAO.findAll().size(), 1);
    assertTrue(createdRule.getActivityId() > 0);

    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(createdRule.getActivityId()));
    assertNotNull(activity);
    assertFalse(activity.isHidden());
    assertNotNull(activity.getActivityStream());
    assertTrue(activity.getActivityStream().isSpace());
    Space space = spaceService.getSpaceByPrettyName(activity.getActivityStream().getPrettyId());
    assertNotNull(space);
    assertEquals(createdRule.getSpaceId(), Long.parseLong(space.getId()));
    assertEquals(message, activity.getTitle());
    assertEquals(RULE_ACTIVITY_OBJECT_TYPE, activity.getMetadataObjectType());
    assertEquals(String.valueOf(createdRule.getId()), activity.getMetadataObjectId());
    assertEquals(1, triggerCount.get());
  }

  @Test
  public void testCreateRuleWithoutPublication() throws Exception {
    AtomicInteger triggerCount = new AtomicInteger(0);
    listenerService.addListener(POST_PUBLISH_RULE_EVENT, new Listener<RuleDTO, String>() {
      @Override
      public void onEvent(Event<RuleDTO, String> event) throws Exception {
        triggerCount.incrementAndGet();
      }
    });

    ProgramDTO program = newProgram(GAMIFICATION_DOMAIN);

    RulePublication rule = new RulePublication();
    rule.setScore(Integer.parseInt(TEST_SCORE));
    rule.setTitle(RULE_NAME);
    rule.setDescription("Description");
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setEvent(RULE_NAME);
    rule.setProgram(program);
    rule.setType(EntityType.AUTOMATIC);
    rule.setRecurrence(RecurrenceType.NONE);
    rule.setPublish(false);
    String message = "Test publication Message";
    rule.setMessage(message);
    rule.setSpaceId(program.getSpaceId());
    RuleDTO createdRule = ruleService.createRule(rule, ADMIN_USER);
    assertEquals(ruleDAO.findAll().size(), 1);
    assertTrue(createdRule.getActivityId() > 0);

    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(createdRule.getActivityId()));
    assertNotNull(activity);
    assertTrue(activity.isHidden());
    assertTrue(StringUtils.isBlank(activity.getTitle()));
    assertTrue(activity.getActivityStream().isSpace());
    Space space = spaceService.getSpaceByPrettyName(activity.getActivityStream().getPrettyId());
    assertNotNull(space);
    assertEquals(createdRule.getSpaceId(), Long.parseLong(space.getId()));
    assertEquals(0, triggerCount.get());
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
    RuleFilter filter = new RuleFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    List<RuleDTO> rules = ruleService.getRules(filter, ADMIN_USER, 0, 10);
    assertEquals(1, rules.size());
    filter.setStatus(EntityStatusType.DISABLED);
    rules = ruleService.getRules(filter, ADMIN_USER, 0, 10);
    assertEquals(2, rules.size());
    filter.setStatus(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    filter.setProgramId(domainId);
    rules = ruleService.getRules(filter, ADMIN_USER, 0, 10);
    assertEquals(1, rules.size());
    filter.setType(EntityFilterType.MANUAL);
    rules = ruleService.getRules(filter, ADMIN_USER, 0, 10);
    assertEquals(0, rules.size());
    filter = new RuleFilter();
    filter.setType(EntityFilterType.AUTOMATIC);
    rules = ruleService.getRules(filter, ADMIN_USER, 0, 10);
    assertEquals(3, rules.size());
    assertEquals(3, ruleDAO.count().intValue());
    assertTrue("Enabled rule should have an automatic generated activity", rules.get(0).getActivityId() > 0);
    assertEquals("Disabled rule shouldn't have an automatic generated activity", 0, rules.get(1).getActivityId());
    assertEquals("Disabled rule shouldn't have an automatic generated activity", 0, rules.get(2).getActivityId());
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
    RuleFilter filter = new RuleFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(1, ruleService.getRules(filter, SPACE_MEMBER_USER, 0, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, ruleService.getRules(filter, SPACE_MEMBER_USER, 0, 10).size());
    filter.setStatus(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    filter.setProgramId(domainId);
    assertEquals(1, ruleService.getRules(filter, SPACE_MEMBER_USER, 0, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(filter, SPACE_MEMBER_USER, 0, 10).size());
    filter = new RuleFilter();
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(3, ruleService.getRules(filter, SPACE_MEMBER_USER, 0, 10).size());
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
    RuleFilter filter = new RuleFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(0, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(0, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter.setStatus(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    filter.setProgramId(domainId);
    assertEquals(0, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter = new RuleFilter();
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(0, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
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
    RuleDTO rule1 = newRuleDTO("rule1", domainEntity1.getId());
    rule1 = ruleService.findRuleById(rule1.getId(), "root1");
    assertTrue(rule1.getActivityId() > 0);
    ExoSocialActivity activity = activityManager.getActivity(String.valueOf(rule1.getActivityId()));
    assertNotNull(activity);
    assertTrue(activity.isHidden());
    assertTrue(StringUtils.isBlank(activity.getTitle()));
    assertTrue(activity.getActivityStream().isUser());
    assertEquals("root1", activity.getActivityStream().getPrettyId());

    rule1.setEnabled(false);
    ruleService.updateRule(rule1, ADMIN_USER);
    RuleDTO ruleDTO2 = newRuleDTO("rule2", domainEntity2.getId());
    ruleDTO2.setEnabled(false);
    ruleService.updateRule(ruleDTO2, ADMIN_USER);
    RuleFilter filter = new RuleFilter();
    filter.setStatus(EntityStatusType.ENABLED);
    assertEquals(1, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter.setStatus(EntityStatusType.DISABLED);
    assertEquals(2, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter.setStatus(EntityStatusType.ALL);
    ProgramDTO domain = programService.getProgramByTitle(GAMIFICATION_DOMAIN);
    long domainId = domain.getId();
    filter.setProgramId(domainId);
    assertEquals(1, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter.setType(EntityFilterType.MANUAL);
    assertEquals(0, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    filter = new RuleFilter();
    filter.setType(EntityFilterType.AUTOMATIC);
    assertEquals(3, ruleService.getRules(filter, INTERNAL_USER, 0, 10).size());
    assertEquals(3, ruleDAO.count().intValue());
  }

}

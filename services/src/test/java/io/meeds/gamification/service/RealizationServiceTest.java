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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.service;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;
import static org.junit.Assert.assertThrows;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.Period;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class RealizationServiceTest extends AbstractServiceTest { // NOSONAR

  private static final String INTERNAL_USER     = "root50";

  private static final String SPACE_HOST_USER   = "root5";

  private static final String SPACE_MEMBER_USER = "root10";

  private static final String ADMIN_USER        = "root1";

  private Identity            adminAclIdentity;

  private String              adminIdentityId;

  private Identity            spaceMemberAclIdentity;

  private String              spaceMemberIdentityId;

  private Identity            spaceHostAclIdentity;

  private String              spaceHostIdentityId;

  private Identity            internalUserAclIdentity;

  private String              internalUserIdentityId;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adminAclIdentity = registerAdministratorUser(ADMIN_USER);
    adminIdentityId = identityManager.getOrCreateUserIdentity(ADMIN_USER).getId();
    spaceHostAclIdentity = registerInternalUser(SPACE_HOST_USER);
    spaceHostIdentityId = identityManager.getOrCreateUserIdentity(SPACE_HOST_USER).getId();
    spaceMemberAclIdentity = registerInternalUser(SPACE_MEMBER_USER);
    spaceMemberIdentityId = identityManager.getOrCreateUserIdentity(SPACE_MEMBER_USER).getId();
    internalUserAclIdentity = registerInternalUser(INTERNAL_USER);
    internalUserIdentityId = identityManager.getOrCreateUserIdentity(INTERNAL_USER).getId();
  }

  public void testCreateRealizationsByAdmin() {
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO rule = newRuleDTO();

    RealizationDTO realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                       adminIdentityId,
                                                                       adminIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(adminIdentityId, realizationEntity.getEarnerId());
    assertEquals(adminIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                        TEST_SPACE_ID,
                                                        adminIdentityId,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    realizationEntity = realizations.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(adminIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());
  }

  public void testCreateRealizationsBySpaceHost() {
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO ruleDTO = newRuleDTO();

    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                       spaceHostIdentityId,
                                                                       spaceMemberIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(spaceHostIdentityId, realizationEntity.getEarnerId());
    assertEquals(spaceMemberIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                        TEST_SPACE_ID,
                                                        spaceHostIdentityId,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    realizationEntity = realizations.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(spaceHostIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());
  }

  public void testCreateRealizationsBySpaceMember() {
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO ruleDTO = newRuleDTO();

    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                       spaceMemberIdentityId,
                                                                       spaceHostIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(spaceMemberIdentityId, realizationEntity.getEarnerId());
    assertEquals(spaceHostIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                        TEST_SPACE_ID,
                                                        spaceMemberIdentityId,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    realizationEntity = realizations.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(spaceMemberIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());
  }

  public void testGetRealizationBySpaceMember() throws IllegalAccessException, ObjectNotFoundException {
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO ruleDTO = newRuleDTO();

    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                       spaceHostIdentityId,
                                                                       spaceMemberIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(spaceHostIdentityId, realizationEntity.getEarnerId());
    assertEquals(spaceMemberIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                        TEST_SPACE_ID,
                                                        spaceHostIdentityId,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    RealizationDTO spaceMemberAccessedRealization = realizationService.getRealizationById(realization.getId(), spaceMemberAclIdentity);
    assertNotNull(spaceMemberAccessedRealization);
    assertEquals(realization.getId(), spaceMemberAccessedRealization.getId());
  }

  public void testCreateRealizationsByInternalUserInSpaceRule() {
    RuleDTO ruleDTO = newRuleDTO();

    List<RealizationDTO> realizations = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                              internalUserIdentityId,
                                                                              spaceHostIdentityId,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertTrue(realizations.isEmpty());
    assertEquals(realizations.size(), realizationDAO.count().intValue());

    realizations = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                         TEST_SPACE_ID,
                                                         internalUserIdentityId,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    assertEquals(realizations.size(), realizationDAO.count().intValue());

    RealizationDTO realization = realizations.get(0);
    assertEquals(TEST_SPACE_ID, realization.getEarnerId());
    assertEquals(internalUserIdentityId, realization.getReceiver());
    assertEquals(ACTIVITY_ID, realization.getObjectId());
    assertEquals(IdentityType.SPACE.name(), realization.getEarnerType());
  }

  public void testCreateRealizationsByInternalUser() {
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    ProgramEntity openProgram = newOpenProgram("testProgram");
    RuleDTO ruleDTO = newRuleDTO("testRule", openProgram.getId());

    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                       internalUserIdentityId,
                                                                       spaceHostIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(internalUserIdentityId, realizationEntity.getEarnerId());
    assertEquals(spaceHostIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                        TEST_SPACE_ID,
                                                        internalUserIdentityId,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    realizationEntity = realizations.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(internalUserIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());
  }

  public void testCancelRealizations() {
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO ruleDTO = newRuleDTO();

    List<RealizationDTO> createdRealizations = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                                     adminIdentityId,
                                                                                     spaceMemberIdentityId,
                                                                                     ACTIVITY_ID,
                                                                                     ACTIVITY_OBJECT_TYPE);
    RealizationDTO realization = createdRealizations.get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    List<RealizationDTO> canceledRealizations = realizationService.cancelRealizations(ruleDTO.getEvent().getTitle(),
                                                                                      adminIdentityId,
                                                                                      spaceMemberIdentityId,
                                                                                      ACTIVITY_ID,
                                                                                      ACTIVITY_OBJECT_TYPE);
    assertNotNull(canceledRealizations);
    assertEquals(createdRealizations.size(), canceledRealizations.size());
    RealizationDTO canceledRealization = canceledRealizations.get(0);
    assertEquals(canceledRealization.getId(), realization.getId());
    assertEquals(canceledRealization.getStatus(), RealizationStatus.CANCELED.name());
  }

  @Test
  public void testHadOwnedProgram() throws IllegalAccessException, ObjectNotFoundException {
    assertFalse(realizationService.isRealizationManager(spaceMemberAclIdentity.getUserId()));
    assertTrue(realizationService.isRealizationManager(adminAclIdentity.getUserId()));
    assertFalse(realizationService.isRealizationManager(spaceHostAclIdentity.getUserId()));

    ProgramDTO program = newProgram();

    assertTrue(realizationService.isRealizationManager(spaceHostAclIdentity.getUserId()));

    org.exoplatform.social.core.identity.model.Identity regularUserIdentity =
                                                                            identityManager.getOrCreateUserIdentity(spaceMemberAclIdentity.getUserId());

    program.setOwnerIds(Collections.singleton(Long.parseLong(regularUserIdentity.getId())));
    programService.updateProgram(program, adminAclIdentity);
    assertTrue(realizationService.isRealizationManager(spaceMemberAclIdentity.getUserId()));

    programService.deleteProgramById(program.getId(), adminAclIdentity);
    assertTrue(realizationService.isRealizationManager(spaceMemberAclIdentity.getUserId()));
  }

  public void testCreateRealizationsOnOutdatedRules() throws ObjectNotFoundException {
    assertEquals(0l, realizationDAO.count().longValue());

    RuleDTO rule = newRuleDTO();
    rule.setEndDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() - MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);

    List<RealizationDTO> realizations = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                              adminIdentityId,
                                                                              spaceMemberIdentityId,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());
    assertEquals(0l, realizationDAO.count().longValue());

    rule.setEndDate(null);
    rule.setStartDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);
    realizations = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                         adminIdentityId,
                                                         spaceMemberIdentityId,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());
    assertEquals(0l, realizationDAO.count().longValue());

    rule.setStartDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() - 2 * MILLIS_IN_A_DAY)));
    rule.setEndDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() - MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);
    realizations = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                         adminIdentityId,
                                                         spaceMemberIdentityId,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());
    assertEquals(0l, realizationDAO.count().longValue());

    rule.setEndDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + MILLIS_IN_A_DAY)));
    ruleService.updateRule(rule);

    ProgramDTO program = programService.getProgramById(rule.getProgramId());
    program.setEnabled(true); // Program was disabled automatically
    programService.updateProgram(program);

    realizations = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                         adminIdentityId,
                                                         spaceMemberIdentityId,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    assertEquals(1l, realizationDAO.count().longValue());

    RealizationDTO realization = realizations.get(0);

    assertEquals(adminIdentityId, realization.getEarnerId());
    assertEquals(spaceMemberIdentityId, realization.getReceiver());
    assertEquals(ACTIVITY_ID, realization.getObjectId());
    assertEquals(ACTIVITY_OBJECT_TYPE, realization.getObjectType());
    assertEquals(rule.getScore(), realization.getActionScore());
  }

  public void testCreateRealizationOnOnceRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.ONCE);
    assertEquals(2, realizationDAO.count().intValue());
  }

  public void testCreateRealizationOnDailyRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.DAILY);
    assertEquals(2, realizationDAO.count().intValue());
  }

  public void testCreateRealizationOnWeeklyRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.WEEKLY);
    assertEquals(2, realizationDAO.count().intValue());
  }

  public void testCreateRealizationOnMonthlyRule() throws ObjectNotFoundException {
    makeRecurrenceTypeChecks(RecurrenceType.MONTHLY);
    assertEquals(2, realizationDAO.count().intValue());
  }

  @SuppressWarnings("deprecation")
  public void testCreateRealizationForBlacklistUser() {

    List<RealizationEntity> realizationEntities = realizationDAO.findAll();
    assertEquals(0, realizationEntities.size());
    RuleDTO ruleDTO = newRuleDTO();

    List<RealizationDTO> realizations = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                              spaceMemberIdentityId,
                                                                              adminIdentityId,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizations.size());

    spaceMemberAclIdentity.setMemberships(Arrays.asList(new MembershipEntry("/platform/externals"),
                                                        new MembershipEntry(Utils.BLACK_LIST_GROUP)));
    identityRegistry.register(spaceMemberAclIdentity);

    realizations = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                         spaceMemberIdentityId,
                                                         adminIdentityId,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertEquals(0, realizations.size());
  }

  public void testCreateRealizationWithPrerequisiteRule() throws ObjectNotFoundException, IllegalAccessException {
    List<RealizationEntity> realizationEntities = realizationDAO.findAll();
    assertEquals(0, realizationEntities.size());
    RuleDTO rule = newRuleDTO();

    RuleDTO prerequisiteRule1 = newRuleDTO();
    prerequisiteRule1.setEvent(newEventDTO("prerequisiteEvent1"));
    prerequisiteRule1 = ruleService.updateRule(prerequisiteRule1);

    RuleDTO prerequisiteRule2 = newRuleDTO();
    prerequisiteRule2.setEvent(newEventDTO("prerequisiteEvent2"));
    prerequisiteRule2 = ruleService.updateRule(prerequisiteRule2);

    rule.setPrerequisiteRuleIds(new HashSet<>(Arrays.asList(prerequisiteRule1.getId(), prerequisiteRule2.getId())));
    rule = ruleService.updateRule(rule);

    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(0, realizationDAO.count().intValue());

    realizationService.createRealizations(prerequisiteRule1.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationDAO.count().intValue());

    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationDAO.count().intValue());

    realizationService.createRealizations(prerequisiteRule2.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(2, realizationDAO.count().intValue());

    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(3, realizationDAO.count().intValue());

    ruleService.deleteRuleById(prerequisiteRule2.getId(), ADMIN_USER);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(4, realizationDAO.count().intValue());

    ruleService.deleteRuleById(prerequisiteRule1.getId(), ADMIN_USER);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(5, realizationDAO.count().intValue());
  }

  public void testGetRealizations() throws IllegalAccessException, ObjectNotFoundException { // NOSONAR
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO rule = newRuleDTO();

    RealizationDTO realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                       adminIdentityId,
                                                                       adminIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(adminIdentityId, realizationEntity.getEarnerId());
    assertEquals(adminIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                        TEST_SPACE_ID,
                                                        adminIdentityId,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    realizationEntity = realizations.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(adminIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());

    RealizationFilter filter = new RealizationFilter();
    filter.setOwned(true);
    assertEquals(2, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setOwned(false);
    assertEquals(2, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerIds(Collections.singletonList(adminIdentityId));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerType(IdentityType.USER);
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerIds(null);
    filter.setEarnerType(IdentityType.SPACE);
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerType(IdentityType.USER);
    filter.setProgramIds(Collections.singletonList(rule.getProgramId()));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setProgramIds(Collections.singletonList(5555l));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertThrows(IllegalAccessException.class, () -> realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setProgramIds(null);
    filter.setRuleIds(Collections.singletonList(rule.getId()));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerType(null);
    filter.setEarnerIds(null);
    filter.setRuleIds(null);
    filter.setStatus(RealizationStatus.ACCEPTED);
    assertEquals(2, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    List<RealizationDTO> allRealizations = realizationService.getRealizationsByFilter(filter, spaceMemberAclIdentity, 0, 0);
    assertEquals(2, allRealizations.size());

    allRealizations = realizationService.getRealizationsByFilter(filter, spaceMemberAclIdentity, 0, 1);
    assertEquals(1, allRealizations.size());
    RealizationDTO lastRealization = allRealizations.get(0);
    assertNotNull(lastRealization);
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(lastRealization.getId(),
                                                                  RealizationStatus.CANCELED,
                                                                  ADMIN_USER));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(lastRealization.getId(),
                                                                  RealizationStatus.DELETED,
                                                                  ADMIN_USER));
    realizationService.updateRealizationStatus(lastRealization.getId(), RealizationStatus.REJECTED, SPACE_HOST_USER);

    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setStatus(RealizationStatus.REJECTED);
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setStatus(RealizationStatus.CANCELED);
    assertEquals(0, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));
  }

  public void testExportRealizations() throws IllegalAccessException, Exception { // NOSONAR
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    RuleDTO rule = newRuleDTO();
    rule.setTitle("Rule Title, with comma");
    ruleService.updateRule(rule);

    RealizationDTO realization1 = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                        adminIdentityId,
                                                                        adminIdentityId,
                                                                        ACTIVITY_ID,
                                                                        ACTIVITY_OBJECT_TYPE)
                                                    .get(0);
    realization1 = realizationService.getRealizationById(realization1.getId(), adminAclIdentity);

    RealizationDTO realization2 = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                        TEST_SPACE_ID,
                                                                        adminIdentityId,
                                                                        ACTIVITY_ID,
                                                                        ACTIVITY_OBJECT_TYPE)
                                                    .get(0);
    realization2 = realizationService.getRealizationById(realization2.getId(), adminAclIdentity);

    RealizationDTO realization3 = realizationService.getRealizationById(newRealizationEntity("Test Manual",
                                                                                             rule.getProgramId(),
                                                                                             true).getId(),
                                                                        adminAclIdentity);
    assertNotNull(realization3);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 3);

    RealizationFilter filter = new RealizationFilter();
    filter.setOwned(true);
    InputStream exportInputStream = realizationService.exportXlsx(filter, adminAclIdentity, "fileName", Locale.ENGLISH);
    assertNotNull(exportInputStream);

    Workbook workbook = WorkbookFactory.create(exportInputStream);
    assertNotNull(workbook);
    Sheet sheet = workbook.getSheetAt(0);
    assertNotNull(sheet);
    assertEquals(3, sheet.getLastRowNum());
    Row header = sheet.getRow(0);
    assertNotNull(header);
    assertEquals(7, header.getLastCellNum());
    assertTrue(StringUtils.isNotBlank(header.getCell(header.getFirstCellNum()).getStringCellValue()));
    assertTrue(StringUtils.isNotBlank(header.getCell(header.getLastCellNum() - 1).getStringCellValue()));

    Row row1 = sheet.getRow(1);
    assertNotNull(row1);
    assertEquals(7, row1.getLastCellNum());
    int cellIndex = 0;
    assertEquals(realization1.getCreatedDate(), row1.getCell(cellIndex++).getStringCellValue());
    assertEquals(Utils.getUserFullName(realization1.getEarnerId()), row1.getCell(cellIndex++).getStringCellValue());
    assertEquals(rule.getType().name(), row1.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization1.getProgramLabel(), row1.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization1.getActionTitle(), row1.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization1.getActionScore(), row1.getCell(cellIndex++).getNumericCellValue(), 0d);
    assertEquals(realization1.getStatus(), row1.getCell(cellIndex).getStringCellValue());

    Row row2 = sheet.getRow(2);
    assertNotNull(row2);
    assertEquals(7, row2.getLastCellNum());
    cellIndex = 0;
    assertEquals(realization2.getCreatedDate(), row2.getCell(cellIndex++).getStringCellValue());
    assertEquals(Utils.getUserFullName(realization2.getEarnerId()), row2.getCell(cellIndex++).getStringCellValue());
    assertEquals(rule.getType().name(), row2.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization2.getProgramLabel(), row2.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization2.getActionTitle(), row2.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization2.getActionScore(), row2.getCell(cellIndex++).getNumericCellValue(), 0d);
    assertEquals(realization2.getStatus(), row2.getCell(cellIndex).getStringCellValue());

    Row row3 = sheet.getRow(3);
    assertNotNull(row3);
    assertEquals(7, row3.getLastCellNum());
    cellIndex = 0;
    assertEquals(realization3.getCreatedDate(), row3.getCell(cellIndex++).getStringCellValue());
    assertEquals(Utils.getUserFullName(realization3.getEarnerId()), row3.getCell(cellIndex++).getStringCellValue());
    assertEquals(EntityType.MANUAL.name(), row3.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization3.getProgramLabel(), row3.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization3.getActionTitle(), row3.getCell(cellIndex++).getStringCellValue());
    assertEquals(realization3.getActionScore(), row3.getCell(cellIndex++).getNumericCellValue(), 0d);
    assertEquals(realization3.getStatus(), row3.getCell(cellIndex).getStringCellValue());
  }

  public void testGetRealizationsOnOpenProgram() throws IllegalAccessException, ObjectNotFoundException { // NOSONAR
    List<RealizationEntity> realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 0);
    ProgramEntity openProgramEntity = newOpenProgram("testProgram");
    ProgramDTO openProgram = programService.getProgramById(openProgramEntity.getId());
    openProgram.setOwnerIds(Collections.singleton(Long.parseLong(spaceHostIdentityId)));
    assertThrows(IllegalAccessException.class, () -> programService.updateProgram(openProgram, spaceHostAclIdentity));
    programService.updateProgram(openProgram, adminAclIdentity);
    // Test has became Program Manager
    programService.updateProgram(openProgram, spaceHostAclIdentity); // 

    RuleDTO rule = newRuleDTO("testRule", openProgram.getId());

    RealizationDTO realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                       internalUserIdentityId,
                                                                       spaceMemberIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 1);

    RealizationEntity realizationEntity = realizations.get(0);
    assertEquals(internalUserIdentityId, realizationEntity.getEarnerId());
    assertEquals(spaceMemberIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                        TEST_SPACE_ID,
                                                        spaceMemberIdentityId,
                                                        ACTIVITY_ID,
                                                        ACTIVITY_OBJECT_TYPE)
                                    .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    realizations = realizationDAO.findAll();
    assertEquals(realizations.size(), 2);

    realizationEntity = realizations.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(spaceMemberIdentityId, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());

    RealizationFilter filter = new RealizationFilter();
    filter.setOwned(true);
    assertEquals(2, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setOwned(false);
    assertEquals(2, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerIds(Collections.singletonList(adminIdentityId));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerIds(Collections.singletonList(internalUserIdentityId));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerType(IdentityType.USER);
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerIds(null);
    filter.setEarnerType(IdentityType.SPACE);
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerType(IdentityType.USER);
    filter.setProgramIds(Collections.singletonList(rule.getProgramId()));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setProgramIds(Collections.singletonList(5555l));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertThrows(IllegalAccessException.class, () -> realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setProgramIds(null);
    filter.setRuleIds(Collections.singletonList(rule.getId()));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setEarnerType(null);
    filter.setEarnerIds(null);
    filter.setRuleIds(null);
    filter.setStatus(RealizationStatus.ACCEPTED);
    assertEquals(2, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(2, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    List<RealizationDTO> allRealizations = realizationService.getRealizationsByFilter(filter, spaceMemberAclIdentity, 0, 0);
    assertEquals(2, allRealizations.size());

    allRealizations = realizationService.getRealizationsByFilter(filter, spaceMemberAclIdentity, 0, 1);
    assertEquals(1, allRealizations.size());
    RealizationDTO lastRealization = allRealizations.get(0);
    assertNotNull(lastRealization);
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(lastRealization.getId(),
                                                                  RealizationStatus.CANCELED,
                                                                  ADMIN_USER));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(lastRealization.getId(),
                                                                  RealizationStatus.DELETED,
                                                                  ADMIN_USER));
    assertThrows(IllegalAccessException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(lastRealization.getId(),
                                                                  RealizationStatus.REJECTED,
                                                                  INTERNAL_USER));
    assertThrows(IllegalAccessException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(lastRealization.getId(),
                                                                  RealizationStatus.REJECTED,
                                                                  SPACE_MEMBER_USER));
    realizationService.updateRealizationStatus(lastRealization.getId(), RealizationStatus.REJECTED, SPACE_HOST_USER);

    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setStatus(RealizationStatus.REJECTED);
    assertEquals(1, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(1, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));

    filter.setStatus(RealizationStatus.CANCELED);
    assertEquals(0, realizationService.countRealizationsByFilter(filter, adminAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceHostAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, spaceMemberAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, internalUserAclIdentity));
  }

  public void testLeaderboardRank() throws IllegalAccessException {
    ProgramDTO program = newProgram();
    RuleDTO rule = newRuleDTO(RULE_NAME, program.getId());
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          adminIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          spaceMemberIdentityId,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          TEST_SPACE_ID,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          TEST_SPACE_ID,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          TEST_SPACE_ID,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          TEST_SPACE2_ID,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          TEST_SPACE2_ID,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          TEST_SPACE2_ID,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(rule.getEvent().getTitle(),
                                          TEST_SPACE2_ID,
                                          spaceMemberIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    Date date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());

    assertEquals(1, realizationService.getLeaderboardRank(adminIdentityId, date, program.getId()));
    LeaderboardFilter leaderboardFilter = new LeaderboardFilter();
    leaderboardFilter.setIdentityType(IdentityType.USER);
    leaderboardFilter.setProgramId(program.getId());
    leaderboardFilter.setPeriod("WEEK");
    assertEquals(2, realizationService.getLeaderboard(leaderboardFilter, null).size());

    assertEquals(1, realizationService.getLeaderboardRank(adminIdentityId, date, program.getId()));
    assertEquals(2, realizationService.getLeaderboardRank(spaceMemberIdentityId, date, program.getId()));

    leaderboardFilter = new LeaderboardFilter();
    leaderboardFilter.setIdentityType(IdentityType.SPACE);
    leaderboardFilter.setProgramId(program.getId());
    leaderboardFilter.setPeriod("WEEK");
    assertEquals(1, realizationService.getLeaderboard(leaderboardFilter, ADMIN_USER).size());

    assertEquals(0, realizationService.getLeaderboardRank(TEST_SPACE2_ID, date, program.getId()));
    assertEquals(1, realizationService.getLeaderboardRank(TEST_SPACE_ID, date, program.getId()));
  }

  public void testFindUserReputationBySocialId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(realizationService.getScoreByIdentityId(adminIdentityId), 0);
    realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                          adminIdentityId,
                                          adminIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                          TEST_SPACE_ID,
                                          adminIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertTrue(realizationService.getScoreByIdentityId(adminIdentityId) > 0);
    assertTrue(realizationService.getScoreByIdentityId(TEST_SPACE_ID) > 0);
  }

  public void testBuildDomainScoreByUserId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(0, realizationService.getScorePerProgramByIdentityId(adminIdentityId).size());
    realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                          adminIdentityId,
                                          adminIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationService.getScorePerProgramByIdentityId(adminIdentityId).size());
  }

  public void testFilterByDomainId() throws IllegalAccessException {
    RuleDTO ruleDTO = newRuleDTO();
    LeaderboardFilter filter = new LeaderboardFilter();
    filter.setPeriod(Period.ALL.name());
    filter.setIdentityType(IdentityType.USER);
    filter.setLimit(LIMIT);
    filter.setProgramId(ruleDTO.getProgram().getId());
    List<StandardLeaderboard> filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(0, filteredLeaderboard.size());

    realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                          adminIdentityId,
                                          adminIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(1, filteredLeaderboard.size());
    StandardLeaderboard userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(adminIdentityId, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.WEEK.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(adminIdentityId, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.MONTH.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(adminIdentityId, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setIdentityType(IdentityType.SPACE);
    assertThrows(IllegalAccessException.class, () -> realizationService.getLeaderboard(filter, null));
    filteredLeaderboard = realizationService.getLeaderboard(filter, TEST_USER_EARNER);
    assertEquals(0, filteredLeaderboard.size());
  }

  public void testDeleteHistory() {
    ProgramDTO program = newProgram();
    RuleDTO ruleDTO = newRuleDTO(RULE_NAME, program.getId());
    realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                          adminIdentityId,
                                          adminIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                          adminIdentityId,
                                          adminIdentityId,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    RealizationFilter identityFilter = new RealizationFilter();
    identityFilter.setEarnerIds(Collections.singletonList(adminIdentityId));
    List<RealizationDTO> realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    RealizationDTO lastRealization = realizations.get(0);
    assertEquals(RealizationStatus.ACCEPTED.name(), lastRealization.getStatus());

    realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    lastRealization = realizations.get(0);
    assertEquals(RealizationStatus.ACCEPTED.name(), lastRealization.getStatus());

    realizationService.deleteRealizations(ACTIVITY_ID, ACTIVITY_OBJECT_TYPE);

    realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    lastRealization = realizations.get(0);
    assertEquals(RealizationStatus.DELETED.name(), lastRealization.getStatus());

    identityFilter.setStatus(RealizationStatus.ACCEPTED);
    realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(0, realizations.size());
  }

  private void makeRecurrenceTypeChecks(RecurrenceType recurrenceType) throws ObjectNotFoundException {
    List<RealizationEntity> realizationEntities = realizationDAO.findAll();
    assertEquals(0, realizationEntities.size());
    RuleDTO ruleDTO = newRuleDTO();
    ruleDTO.setRecurrence(recurrenceType);
    ruleDTO = ruleService.updateRule(ruleDTO);

    RealizationDTO realization = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                       adminIdentityId,
                                                                       adminIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .get(0);
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);
    realizationEntities = realizationDAO.findAll();
    assertEquals(1, realizationEntities.size());

    List<RealizationDTO> realizations = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                                              adminIdentityId,
                                                                              adminIdentityId,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertTrue(realizations.isEmpty());

    realizationEntities = realizationDAO.findAll();
    assertEquals(1, realizationEntities.size());

    RealizationEntity realizationEntity = realizationEntities.get(0);
    realizationEntity.setCreatedDate(new Date(recurrenceType.getPeriodStartDate().getTime() - 1));

    realizationDAO.update(realizationEntity);

    realizations = realizationService.createRealizations(ruleDTO.getEvent().getTitle(),
                                                         TEST_SPACE_ID,
                                                         adminIdentityId,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());

    realizationEntities = realizationDAO.findAll();
    assertEquals(2, realizationEntities.size());
  }
}

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

package org.exoplatform.addons.gamification.service;

import static org.exoplatform.addons.gamification.GamificationConstant.ACTIVITY_OBJECT_TYPE;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.RealizationEntity;
import org.exoplatform.addons.gamification.service.configuration.ProgramService;
import org.exoplatform.addons.gamification.service.configuration.RealizationServiceImpl;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter.Period;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.storage.RealizationStorage;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

@RunWith(MockitoJUnitRunner.class)
public class RealizationServiceTest extends AbstractServiceTest {

  protected static final long                      MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                           // NOSONAR

  protected static final Date                      fromDate        = new Date();

  protected static final Date                      toDate          = new Date(fromDate.getTime() + MILLIS_IN_A_DAY);

  protected static final int                       offset          = 0;

  protected static final int                       limit           = 3;

  private static MockedStatic<ExoContainerContext> CONTAINER_CONTEXT;

  @Mock
  IdentityManager                                  identityManager;

  @Mock
  ProgramService                                   programService;

  @Mock
  RuleService                                      ruleService;

  @Mock
  SpaceService                                     spaceService;

  @Mock
  RealizationStorage                               realizationsStorage;

  @Mock
  IdentityRegistry                                 identityRegistry;

  RealizationService                               realizationService;

  @BeforeClass
  public static void initClassContext() {
    CONTAINER_CONTEXT = mockStatic(ExoContainerContext.class);
  }

  @AfterClass
  public static void endClassContext() {
    CONTAINER_CONTEXT.close();
  }

  @Before
  public void setUp() throws Exception { // NOSONAR
    realizationService = new RealizationServiceImpl(programService,
                                                    ruleService,
                                                    identityManager,
                                                    spaceService,
                                                    realizationsStorage);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testGetRealizationsByFilter() throws IllegalAccessException {
    // Given
    RealizationFilter filter = new RealizationFilter();
    Identity rootAclIdentity = new Identity("root1");
    org.exoplatform.social.core.identity.model.Identity rootIdentity =
                                                                     mock(org.exoplatform.social.core.identity.model.Identity.class);
    when(rootIdentity.getId()).thenReturn("2");
    when(rootIdentity.getRemoteId()).thenReturn(rootAclIdentity.getUserId());

    ConversationState.setCurrent(new ConversationState(rootAclIdentity));
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();
    rootAclIdentity.setMemberships(memberships);
    CONTAINER_CONTEXT.when(() -> ExoContainerContext.getService(IdentityRegistry.class)).thenReturn(identityRegistry);
    when(identityRegistry.getIdentity(rootIdentity.getRemoteId())).thenReturn(rootAclIdentity);

    RealizationDTO gHistory1 = newRealizationDTO();
    RealizationDTO gHistory2 = newRealizationDTO();
    RealizationDTO gHistory3 = newRealizationDTO();
    List<RealizationDTO> realizations = new ArrayList<>();
    realizations.add(gHistory1);
    realizations.add(gHistory2);
    realizations.add(gHistory3);
    when(realizationsStorage.getRealizationsByFilter(filter, offset, limit)).thenReturn(realizations);
    when(identityManager.getOrCreateUserIdentity(rootAclIdentity.getUserId())).thenReturn(rootIdentity);
    assertThrows(IllegalArgumentException.class,
                 () -> realizationService.getRealizationsByFilter(null, rootAclIdentity, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationService.getRealizationsByFilter(filter, null, offset, limit));
    assertThrows(IllegalArgumentException.class,
                 () -> realizationService.getRealizationsByFilter(filter, rootAclIdentity, offset, limit));

    assertThrows(IllegalArgumentException.class, () -> realizationService.countRealizationsByFilter(null, rootAclIdentity));
    assertThrows(IllegalArgumentException.class, () -> realizationService.countRealizationsByFilter(filter, null));
    assertThrows(IllegalArgumentException.class, () -> realizationService.countRealizationsByFilter(filter, rootAclIdentity));

    // When
    filter.setFromDate(toDate);
    filter.setToDate(fromDate);
    assertThrows(IllegalArgumentException.class,
                 () -> realizationService.getRealizationsByFilter(filter, rootAclIdentity, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationService.countRealizationsByFilter(filter, rootAclIdentity));

    // When
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.getRealizationsByFilter(filter, rootAclIdentity, offset, limit));
    assertThrows(IllegalAccessException.class, () -> realizationService.countRealizationsByFilter(filter, rootAclIdentity));

    filter.setEarnerIds(Collections.singletonList(rootIdentity.getId()));
    List<RealizationDTO> createdRealizations =
                                             realizationService.getRealizationsByFilter(filter,
                                                                                        rootAclIdentity,
                                                                                        offset,
                                                                                        limit);
    // Then
    assertNotNull(createdRealizations);
    assertEquals(3, createdRealizations.size());

    MembershipEntry membershipentry = new MembershipEntry("/platform/administrators", "*");
    memberships.add(membershipentry);
    rootAclIdentity.setMemberships(memberships);
    filter.setEarnerIds(null);

    createdRealizations =
                        realizationService.getRealizationsByFilter(filter,
                                                                   rootAclIdentity,
                                                                   offset,
                                                                   limit);
    // Then
    assertNotNull(createdRealizations);
    assertEquals(3, createdRealizations.size());
  }

  @Test
  public void updateRealizationStatus() {
    // Given
    RealizationDTO gHistory1 = newRealizationDTO();
    RealizationDTO gHistory2 = newRealizationDTO();
    gHistory2.setStatus(HistoryStatus.REJECTED.name());
    when(realizationsStorage.getRealizationById(1L)).thenReturn(gHistory1);
    when(realizationsStorage.getRealizationById(2L)).thenReturn(null);
    when(realizationsStorage.updateRealization(gHistory1)).thenReturn(gHistory2);
    RealizationDTO rejectedHistory = null;
    // When
    assertThrows(IllegalArgumentException.class,
                 () -> realizationService.updateRealization(null));
    RealizationDTO realization1 = new RealizationDTO();
    realization1.setId(2L);
    assertThrows(ObjectNotFoundException.class,
                 () -> realizationService.updateRealization(realization1));

    try {
      gHistory1.setStatus(HistoryStatus.REJECTED.name());
      gHistory1.setActionTitle("new label");
      gHistory1.setActionScore(10L);
      rejectedHistory = realizationService.updateRealization(gHistory1);

    } catch (ObjectNotFoundException e) {
      fail(e.getMessage());
    }
    // Then
    assertNotNull(rejectedHistory);
    assertEquals(rejectedHistory.getStatus(), HistoryStatus.REJECTED.name());
  }

  public void testBuildHistory() {
    // root11 is not a member of domain audience
    RuleDTO ruleDTO = newRuleDTO();
    List<RealizationDTO> realizations = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                              "11",
                                                                              TEST_USER_RECEIVER,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertTrue(CollectionUtils.isEmpty(realizations));

    // case of deleted domain
    ProgramDTO program = newProgram();
    program.setDeleted(true);
    ruleDTO.setProgram(program);
    realizations = realizationService.createRealizations(ruleDTO.getEvent(),
                                                         TEST_USER_EARNER,
                                                         TEST_USER_RECEIVER,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertTrue(CollectionUtils.isEmpty(realizations));

    ruleDTO = newRuleDTO();
    realizations = realizationService.createRealizations(ruleDTO.getEvent(),
                                                         TEST_USER_EARNER,
                                                         TEST_USER_RECEIVER,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertTrue(CollectionUtils.isNotEmpty(realizations));
  }

  public void testSaveActionHistory() {
    List<RealizationEntity> realizationEntities = realizationDAO.findAll();
    assertEquals(realizationEntities.size(), 0);
    RuleDTO ruleDTO = newRuleDTO();

    List<RealizationDTO> realizations = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                              TEST_USER_EARNER,
                                                                              TEST_USER_RECEIVER,
                                                                              ACTIVITY_ID,
                                                                              ACTIVITY_OBJECT_TYPE);
    assertTrue(CollectionUtils.isNotEmpty(realizations));

    realizationEntities = realizationDAO.findAll();
    assertEquals(realizationEntities.size(), 1);

    RealizationEntity realizationEntity = realizationEntities.get(0);
    assertEquals(TEST_USER_EARNER, realizationEntity.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.USER, realizationEntity.getEarnerType());

    realizations = realizationService.createRealizations(ruleDTO.getEvent(),
                                                         TEST_SPACE_ID,
                                                         TEST_USER_RECEIVER,
                                                         ACTIVITY_ID,
                                                         ACTIVITY_OBJECT_TYPE);
    assertTrue(CollectionUtils.isNotEmpty(realizations));

    realizationEntities = realizationDAO.findAll();
    assertEquals(realizationEntities.size(), 2);

    realizationEntity = realizationEntities.get(1);
    assertEquals(TEST_SPACE_ID, realizationEntity.getEarnerId());
    assertEquals(TEST_USER_RECEIVER, realizationEntity.getReceiver());
    assertEquals(ACTIVITY_ID, realizationEntity.getObjectId());
    assertEquals(IdentityType.SPACE, realizationEntity.getEarnerType());
  }

  public void testComputeTotalScore() {
    RuleDTO ruleDTO = newRuleDTO();
    RealizationDTO realization1 = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                        TEST_USER_EARNER,
                                                                        TEST_USER_RECEIVER,
                                                                        ACTIVITY_ID,
                                                                        ACTIVITY_OBJECT_TYPE)
                                                    .get(0);

    RealizationDTO realization2 = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                        TEST_USER_EARNER,
                                                                        TEST_USER_RECEIVER,
                                                                        ACTIVITY_ID,
                                                                        ACTIVITY_OBJECT_TYPE)
                                                    .get(0);

    assertEquals(realization1.getActionScore() + realization2.getActionScore(), realizationDAO.getTotalScore(TEST_USER_EARNER));
  }

  public void testLeaderboardRank() {
    ProgramDTO program = newProgram();
    RuleDTO ruleDTO = newRuleDTO(RULE_NAME, program.getId());
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_RECEIVER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE2_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    Date date = Date.from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());

    int rankUser1 = realizationService.getLeaderboardRank(TEST_USER_EARNER, date, program.getId());
    int rankUser2 = realizationService.getLeaderboardRank(TEST_USER_RECEIVER, date, program.getId());
    assertEquals(1, rankUser1);
    assertEquals(2, rankUser2);

    int rankSpace2 = realizationService.getLeaderboardRank(TEST_SPACE2_ID, date, program.getId());
    int rankSpace1 = realizationService.getLeaderboardRank(TEST_SPACE_ID, date, program.getId());
    assertEquals(1, rankSpace2);
    assertEquals(2, rankSpace1);
  }

  public void testFindLatestActionHistoryBySocialId() {
    RuleDTO ruleDTO = newRuleDTO();
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    RealizationDTO lastRealization = realizationService.createRealizations(ruleDTO.getEvent(),
                                                                           TEST_USER_EARNER,
                                                                           TEST_USER_RECEIVER,
                                                                           ACTIVITY_ID,
                                                                           ACTIVITY_OBJECT_TYPE)
                                                       .get(0);

    RealizationDTO realization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertEquals(lastRealization.getActionScore(), realization.getActionScore());
    assertEquals(lastRealization.getActionTitle(), realization.getActionTitle());
    assertEquals(lastRealization.getProgram(), realization.getProgram());
    assertEquals(lastRealization.getGlobalScore(), realization.getGlobalScore());
    assertEquals(lastRealization.getObjectId(), realization.getObjectId());
    assertEquals(lastRealization.getReceiver(), realization.getReceiver());
    assertEquals(lastRealization.getEarnerId(), realization.getEarnerId());
    assertEquals(lastRealization.getCreatedBy(), realization.getCreatedBy());
  }

  public void testFindUserReputationBySocialId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(realizationService.getScoreByIdentityId(TEST_USER_EARNER), 0);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_SPACE_ID,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    assertTrue(realizationService.getScoreByIdentityId(TEST_USER_EARNER) > 0);
    assertTrue(realizationService.getScoreByIdentityId(TEST_SPACE_ID) > 0);
  }

  public void testBuildDomainScoreByUserId() {
    RuleDTO ruleDTO = newRuleDTO();
    assertEquals(0, realizationService.getScorePerDomainByIdentityId(TEST_USER_EARNER).size());
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationService.getScorePerDomainByIdentityId(TEST_USER_EARNER).size());
  }

  public void testFilterByDomainId() {
    RuleDTO ruleDTO = newRuleDTO();
    LeaderboardFilter filter = new LeaderboardFilter();
    filter.setPeriod(Period.ALL.name());
    filter.setIdentityType(IdentityType.USER);
    filter.setLoadCapacity(limit);
    filter.setDomainId(ruleDTO.getProgram().getId());
    List<StandardLeaderboard> filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(0, filteredLeaderboard.size());

    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(1, filteredLeaderboard.size());
    StandardLeaderboard userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.WEEK.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.MONTH.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setIdentityType(IdentityType.SPACE);
    filteredLeaderboard = realizationService.getLeaderboard(filter);
    assertEquals(0, filteredLeaderboard.size());
  }

  public void testDeleteHistory() {
    ProgramDTO program = newProgram();
    RuleDTO ruleDTO = newRuleDTO(RULE_NAME, program.getId());
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_RECEIVER,
                                          TEST_USER_EARNER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    RealizationDTO lastRealization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertEquals(HistoryStatus.ACCEPTED, lastRealization.getStatus());

    lastRealization = realizationService.findLatestRealizationByIdentityId(TEST_USER_RECEIVER);
    assertEquals(HistoryStatus.ACCEPTED, lastRealization.getStatus());

    realizationService.deleteRealizations(ACTIVITY_ID, ACTIVITY_OBJECT_TYPE);

    lastRealization = realizationService.findLatestRealizationByIdentityId(TEST_USER_EARNER);
    assertEquals(HistoryStatus.DELETED, lastRealization.getStatus());

    lastRealization = realizationService.findLatestRealizationByIdentityId(TEST_USER_RECEIVER);
    assertEquals(HistoryStatus.DELETED, lastRealization.getStatus());
  }

  protected RealizationDTO newRealizationDTO() {
    RealizationDTO gHistory = new RealizationDTO();
    gHistory.setId(1L);
    gHistory.setStatus(HistoryStatus.ACCEPTED.name());
    gHistory.setProgram(new ProgramDTO());
    gHistory.setReceiver("1");
    gHistory.setEarnerId("1L");
    gHistory.setEarnerType(IdentityType.USER.name());
    gHistory.setActionTitle("gamification title");
    gHistory.setActionScore(10);
    gHistory.setGlobalScore(10);
    gHistory.setRuleId(1L);
    gHistory.setCreatedDate(Utils.toRFC3339Date(fromDate));
    return gHistory;
  }

}

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

package io.meeds.gamification.service;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;
import static io.meeds.gamification.utils.Utils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.Period;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.service.impl.RealizationServiceImpl;
import io.meeds.gamification.storage.RealizationStorage;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class RealizationServiceMockTest extends AbstractServiceTest {

  private static final Random                      RANDOM          = new Random();

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

  @Mock
  ListenerService                                  listenerService;

  @Mock
  ResourceBundleService                            resourceBundleService;

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
                                                    resourceBundleService,
                                                    identityManager,
                                                    spaceService,
                                                    realizationsStorage,
                                                    listenerService,
                                                    null);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testGetRealizationsByFilter() throws IllegalAccessException {
    // Given
    RealizationFilter filter = new RealizationFilter();
    Identity userAclIdentity = new Identity("root1");
    org.exoplatform.social.core.identity.model.Identity adminIdentity =
                                                                      mock(org.exoplatform.social.core.identity.model.Identity.class);
    when(adminIdentity.getId()).thenReturn("2");
    when(adminIdentity.getRemoteId()).thenReturn(userAclIdentity.getUserId());

    ConversationState.setCurrent(new ConversationState(userAclIdentity));
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();
    userAclIdentity.setMemberships(memberships);
    CONTAINER_CONTEXT.when(() -> ExoContainerContext.getService(IdentityRegistry.class)).thenReturn(identityRegistry);
    when(identityRegistry.getIdentity(adminIdentity.getRemoteId())).thenReturn(userAclIdentity);

    RealizationDTO gHistory1 = newRealizationDTO();
    RealizationDTO gHistory2 = newRealizationDTO();
    RealizationDTO gHistory3 = newRealizationDTO();
    List<RealizationDTO> realizations = new ArrayList<>();
    realizations.add(gHistory1);
    realizations.add(gHistory2);
    realizations.add(gHistory3);
    when(realizationsStorage.getRealizationsByFilter(filter, offset, limit)).thenReturn(realizations);
    when(identityManager.getOrCreateUserIdentity(userAclIdentity.getUserId())).thenReturn(adminIdentity);
    assertThrows(IllegalArgumentException.class,
                 () -> realizationService.getRealizationsByFilter(null, userAclIdentity, offset, limit));
    assertEquals(0, realizationService.getRealizationsByFilter(filter, null, offset, limit).size());
    assertEquals(0, realizationService.countRealizationsByFilter(filter, userAclIdentity));

    // When
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    assertEquals(0, realizationService.countRealizationsByFilter(filter, userAclIdentity));

    filter.setOwned(true);
    assertEquals(0, realizationService.countRealizationsByFilter(filter, userAclIdentity));

    filter.setProgramIds(Collections.singletonList(5555l));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.getRealizationsByFilter(filter, userAclIdentity, offset, limit));
    assertThrows(IllegalAccessException.class, () -> realizationService.countRealizationsByFilter(filter, userAclIdentity));

    filter.setOwned(false);
    filter.setProgramIds(Collections.singletonList(5555l));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.getRealizationsByFilter(filter, userAclIdentity, offset, limit));
    assertThrows(IllegalAccessException.class, () -> realizationService.countRealizationsByFilter(filter, userAclIdentity));

    filter.setOwned(false);
    filter.setProgramIds(null);
    filter.setEarnerIds(Collections.singletonList(adminIdentity.getId()));
    List<RealizationDTO> createdRealizations =
                                             realizationService.getRealizationsByFilter(filter,
                                                                                        userAclIdentity,
                                                                                        offset,
                                                                                        limit);
    // Then
    assertNotNull(createdRealizations);
    assertEquals(3, createdRealizations.size());

    userAclIdentity.setMemberships(Arrays.asList(new MembershipEntry(Utils.ADMINS_GROUP)));

    filter.setFromDate(toDate);
    filter.setToDate(fromDate);
    assertThrows(IllegalArgumentException.class,
                 () -> realizationService.getRealizationsByFilter(filter, userAclIdentity, offset, limit));
    assertThrows(IllegalArgumentException.class, () -> realizationService.countRealizationsByFilter(filter, userAclIdentity));

    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
    assertThrows(IllegalArgumentException.class, () -> realizationService.countRealizationsByFilter(null, userAclIdentity));
    assertEquals(0, realizationService.countRealizationsByFilter(filter, null));

    // When
    filter.setEarnerIds(null);

    createdRealizations =
                        realizationService.getRealizationsByFilter(filter,
                                                                   userAclIdentity,
                                                                   offset,
                                                                   limit);
    // Then
    assertNotNull(createdRealizations);
    assertEquals(3, createdRealizations.size());
  }

  @Test
  public void updateRealizationStatus() throws Exception {
    // Given
    RealizationDTO gHistory1 = newRealizationDTO();
    RealizationDTO gHistory2 = newRealizationDTO();
    gHistory2.setStatus(RealizationStatus.REJECTED.name());
    when(realizationsStorage.getRealizationById(gHistory1.getId())).thenReturn(gHistory1);
    when(realizationsStorage.getRealizationById(gHistory2.getId())).thenReturn(gHistory2);
    when(realizationsStorage.updateRealization(gHistory1)).thenReturn(gHistory1);
    when(realizationsStorage.updateRealization(gHistory2)).thenReturn(gHistory2);
    // When
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(gHistory1.getId(), null));
    assertThrows(IllegalAccessException.class,
                 () -> realizationService.updateRealizationStatus(gHistory1.getId(), RealizationStatus.ACCEPTED, null));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(gHistory1.getId(), null, "test"));
    assertThrows(ObjectNotFoundException.class,
                 () -> realizationService.updateRealizationStatus(5000l, RealizationStatus.ACCEPTED));
    assertThrows(ObjectNotFoundException.class,
                 () -> realizationService.updateRealizationStatus(5000l, RealizationStatus.ACCEPTED, "test"));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(gHistory1.getId(), RealizationStatus.CANCELED, "root1"));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> realizationService.updateRealizationStatus(gHistory1.getId(), RealizationStatus.DELETED, "root1"));

    realizationService.updateRealizationStatus(gHistory1.getId(), RealizationStatus.REJECTED);
    assertEquals(RealizationStatus.REJECTED.name(), realizationService.getRealizationById(gHistory1.getId()).getStatus());
    verify(listenerService, times(1)).broadcast(eq(POST_REALIZATION_CANCELED_EVENT), any(), any());;

    realizationService.updateRealizationStatus(gHistory2.getId(), RealizationStatus.ACCEPTED);
    assertEquals(RealizationStatus.ACCEPTED.name(), realizationService.getRealizationById(gHistory2.getId()).getStatus());
    verify(listenerService, times(1)).broadcast(eq(POST_REALIZATION_UPDATE_EVENT), any(), any());;

    realizationService.updateRealizationStatus(gHistory1.getId(), RealizationStatus.REJECTED, "root1");
    assertEquals(RealizationStatus.REJECTED.name(), realizationService.getRealizationById(gHistory1.getId()).getStatus());
    verify(listenerService, times(2)).broadcast(eq(POST_REALIZATION_CANCELED_EVENT), any(), any());;

    realizationService.updateRealizationStatus(gHistory2.getId(), RealizationStatus.ACCEPTED, "root1");
    assertEquals(RealizationStatus.ACCEPTED.name(), realizationService.getRealizationById(gHistory2.getId()).getStatus());
    verify(listenerService, times(2)).broadcast(eq(POST_REALIZATION_UPDATE_EVENT), any(), any());;
  }

  public void testBuildHistory() throws Exception {
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
    verify(listenerService, times(1)).broadcast(eq(POST_REALIZATION_CREATE_EVENT), any(), any());;
  }

  public void testCreateRealizations() {
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
    assertEquals(0, realizationService.getScorePerProgramByIdentityId(TEST_USER_EARNER).size());
    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);
    assertEquals(1, realizationService.getScorePerProgramByIdentityId(TEST_USER_EARNER).size());
  }

  public void testFilterByDomainId() throws IllegalAccessException {
    RuleDTO ruleDTO = newRuleDTO();
    LeaderboardFilter filter = new LeaderboardFilter();
    filter.setPeriod(Period.ALL.name());
    filter.setIdentityType(IdentityType.USER);
    filter.setLimit(limit);
    filter.setProgramId(ruleDTO.getProgram().getId());
    List<StandardLeaderboard> filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(0, filteredLeaderboard.size());

    realizationService.createRealizations(ruleDTO.getEvent(),
                                          TEST_USER_EARNER,
                                          TEST_USER_RECEIVER,
                                          ACTIVITY_ID,
                                          ACTIVITY_OBJECT_TYPE);

    filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(1, filteredLeaderboard.size());
    StandardLeaderboard userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.WEEK.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setPeriod(Period.MONTH.name());
    filteredLeaderboard = realizationService.getLeaderboard(filter, null);
    assertEquals(1, filteredLeaderboard.size());
    userLeaderboard = filteredLeaderboard.get(0);
    assertEquals(TEST_USER_EARNER, userLeaderboard.getEarnerId());
    assertEquals(ruleDTO.getScore(), userLeaderboard.getReputationScore());

    filter.setIdentityType(IdentityType.SPACE);
    filteredLeaderboard = realizationService.getLeaderboard(filter, null);
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

    RealizationFilter identityFilter = new RealizationFilter();
    identityFilter.setEarnerIds(Collections.singletonList(TEST_USER_EARNER));
    List<RealizationDTO> realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    RealizationDTO lastRealization = realizations.get(0);
    assertEquals(RealizationStatus.ACCEPTED, lastRealization.getStatus());

    identityFilter.setEarnerIds(Collections.singletonList(TEST_USER_RECEIVER));
    realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    lastRealization = realizations.get(0);
    assertEquals(RealizationStatus.ACCEPTED, lastRealization.getStatus());

    realizationService.deleteRealizations(ACTIVITY_ID, ACTIVITY_OBJECT_TYPE);

    realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    lastRealization = realizations.get(0);
    assertEquals(RealizationStatus.DELETED, lastRealization.getStatus());

    realizations = realizationService.getRealizationsByFilter(identityFilter, 0, 1);
    assertNotNull(realizations);
    assertEquals(1, realizations.size());
    lastRealization = realizations.get(0);
    assertEquals(RealizationStatus.DELETED, lastRealization.getStatus());
  }

  protected RealizationDTO newRealizationDTO() {
    RealizationDTO gHistory = new RealizationDTO();
    gHistory.setId(RANDOM.nextLong(1, 10000));
    gHistory.setStatus(RealizationStatus.ACCEPTED.name());
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

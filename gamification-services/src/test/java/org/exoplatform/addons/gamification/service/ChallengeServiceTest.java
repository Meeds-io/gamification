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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.exoplatform.addons.gamification.model.Challenge;
import org.exoplatform.addons.gamification.model.RuleFilter;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class ChallengeServiceTest {

  @Mock
  private ChallengeStorage challengeStorage;

  @Mock
  private SpaceService     spaceService;

  @Mock
  private ListenerService  listenerService;

  private ChallengeService challengeService;

  private InitParams       params;

  @Before
  public void setUp() throws Exception { // NOSONAR
    params = new InitParams();
    ValueParam p = new ValueParam();
    p.setName("challenge.creator.group");
    p.setValue("/platform/administrators");
    params.addParam(p);

    challengeService = new ChallengeServiceImpl(challengeStorage, spaceService, listenerService, params);
  }

  @PrepareForTest({ Utils.class })
  @Test
  public void testCreateChallenge() throws IllegalAccessException {
    // Given
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);
    Challenge challengeCreated = new Challenge(1l,
                                               "new challenge",
                                               "challenge description",
                                               1l,
                                               new Date(System.currentTimeMillis()).toString(),
                                               new Date(System.currentTimeMillis() + 1).toString(),
                                               Collections.emptyList(),
                                               10L,
                                               "gamification",
                                               true); // Given
    Challenge challengeSystem = new Challenge(0,
                                              "new system challenge",
                                              "system challenge description",
                                              1l,
                                              new Date(System.currentTimeMillis()).toString(),
                                              new Date(System.currentTimeMillis() + 1).toString(),
                                              Collections.emptyList(),
                                              10L,
                                              "gamification",
                                              true);
    Challenge challengeCreatedSystem = new Challenge(2l,
                                                     "new challenge",
                                                     "challenge description",
                                                     1l,
                                                     new Date(System.currentTimeMillis()).toString(),
                                                     new Date(System.currentTimeMillis() + 1).toString(),
                                                     Collections.emptyList(),
                                                     10L,
                                                     "gamification",
                                                     true);
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");
    PowerMockito.mockStatic(Utils.class);
    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challengeCreated);

    assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(null, "root"));
    assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(challengeCreated, "root"));

    when(Utils.isChallengeManager(anyList(), anyLong(), anyString())).thenReturn(false);
    assertThrows(IllegalAccessException.class, () -> challengeService.createChallenge(challenge, "root"));
    when(Utils.isChallengeManager(anyList(), anyLong(), anyString())).thenReturn(true);

    Challenge savedChallenge = challengeService.createChallenge(challenge, "root");
    assertNotNull(savedChallenge);
    assertEquals(1l, savedChallenge.getId());
    when(challengeStorage.saveChallenge(challengeSystem, "SYSTEM")).thenReturn(challengeCreatedSystem);
    savedChallenge = challengeService.createChallenge(challengeSystem);
    assertNotNull(savedChallenge);
    assertEquals(2l, savedChallenge.getId());
  }

  @PrepareForTest({ Utils.class })
  @Test
  public void testUpdateChallenge() throws ObjectNotFoundException, IllegalAccessException {
    PowerMockito.mockStatic(Utils.class);

    // Given
    Challenge challenge = new Challenge(1l,
                                        "update challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);
    Challenge challenge1 = new Challenge(1l,
                                         "new challenge",
                                         "challenge description",
                                         1l,
                                         new Date(System.currentTimeMillis()).toString(),
                                         new Date(System.currentTimeMillis() + 1).toString(),
                                         Collections.emptyList(),
                                         10L,
                                         "gamification",
                                         true);

    Challenge challenge2 = new Challenge(1l,
                                         "update challenge",
                                         "challenge description",
                                         1l,
                                         new Date(System.currentTimeMillis()).toString(),
                                         new Date(System.currentTimeMillis() + 1).toString(),
                                         Collections.emptyList(),
                                         10L,
                                         "gamification",
                                         true);
    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challenge2);

    when(Utils.isChallengeManager(anyList(), anyLong(), anyString())).thenReturn(false);
    assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallenge(null, "root"));
    assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallenge(new Challenge(), "root"));
    when(Utils.isChallengeManager(anyList(), anyLong(), anyString())).thenReturn(true);

    assertThrows(ObjectNotFoundException.class, () -> challengeService.updateChallenge(challenge, "root"));
    when(challengeStorage.getChallengeById(anyLong())).thenReturn(challenge1);

    Challenge challengeUpdated = challengeService.updateChallenge(challenge, "root");
    assertNotNull(challengeUpdated);
    assertEquals("update challenge", challengeUpdated.getTitle());
  }

  @PrepareForTest({ Utils.class })
  @Test
  public void testDeleteChallenge() throws ObjectNotFoundException, IllegalAccessException {
    // Given
    Challenge challenge = new Challenge(1l,
                                        "update challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);

    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isManager(space, "root")).thenReturn(true);
    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    Challenge storedChallenge = challengeService.getChallengeById(1L, "root");
    assertNotNull(storedChallenge);
    assertEquals(1l, storedChallenge.getId());

    PowerMockito.mockStatic(Utils.class);

    // When
    assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(-1l, "root"));

    // When
    assertThrows(ObjectNotFoundException.class, () -> challengeService.deleteChallenge(2l, "root"));

    // When
    assertThrows(IllegalAccessException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

    // When
    when(Utils.countAnnouncementsByChallenge(1l)).thenReturn(2l);
    when(Utils.isChallengeManager(anyList(), anyLong(), anyString())).thenReturn(true);
    assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

    // When
    when(Utils.parseSimpleDate(challenge.getEndDate())).thenReturn(Date.from(ZonedDateTime.now().plusDays(10).toInstant()));
    assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

    when(Utils.countAnnouncementsByChallenge(1l)).thenReturn(0l);
    when(Utils.parseSimpleDate(challenge.getEndDate())).thenReturn(Date.from(ZonedDateTime.now().plusDays(-10).toInstant()));
    challengeService.deleteChallenge(challenge.getId(), "root");
  }

  @Test
  public void testCanAddChallenge() {
    org.exoplatform.services.security.Identity currentIdentity = new org.exoplatform.services.security.Identity("root");
    ConversationState state = new ConversationState(currentIdentity);
    ConversationState.setCurrent(state);
    boolean canAddChallenge = challengeService.canAddChallenge(currentIdentity);
    assertFalse(canAddChallenge);
    MembershipEntry membershipentry = new MembershipEntry("/platform/administrators", "*");
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();
    memberships.add(membershipentry);
    currentIdentity.setMemberships(memberships);
    state = new ConversationState(currentIdentity);
    ConversationState.setCurrent(state);
    canAddChallenge = challengeService.canAddChallenge(currentIdentity);
    assertTrue(canAddChallenge);
  }

  @Test
  public void testGetChallengeById() throws IllegalAccessException {
    assertThrows(IllegalArgumentException.class, () -> challengeService.getChallengeById(0l, "root"));
    Challenge challenge = new Challenge(1l,
                                        "update challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);
    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isManager(space, "root")).thenReturn(false);
    when(spaceService.isMember(space, "root")).thenReturn(false);
    assertThrows(IllegalArgumentException.class, () -> challengeService.getChallengeById(0l, "root"));
    Challenge savedChallenge = challengeService.getChallengeById(challenge.getId(), "root");
    assertNull(savedChallenge);
    savedChallenge = challengeService.getChallengeById(challenge.getId());
    assertNull(savedChallenge);
    when(challengeStorage.getChallengeById(anyLong())).thenReturn(challenge);
    assertThrows(IllegalAccessException.class, () -> challengeService.getChallengeById(challenge.getId(), "root"));
    when(spaceService.isManager(space, "root")).thenReturn(true);
    when(spaceService.isMember(space, "root")).thenReturn(true);
    savedChallenge = challengeService.getChallengeById(challenge.getId(), "root");
    assertNotNull(savedChallenge);
    savedChallenge = challengeService.getChallengeById(challenge.getId());
    assertNotNull(savedChallenge);
    assertEquals(challenge.getId(), savedChallenge.getId());
  }

  @Test
  public void testGetChallengesByFilterAndUser() {
    Challenge challenge = new Challenge(1l,
                                        "Challenge",
                                        "description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);
    List<Long> challengesIds = new ArrayList<>();
    challengesIds.add(challenge.getId());
    RuleFilter filter = new RuleFilter();

    List<String> userSpaceIds = Collections.singletonList("1");

    when(spaceService.getMemberSpacesIds("root", 0, -1)).thenReturn(Collections.emptyList());
    List<Challenge> savedChallenges = challengeService.getChallengesByFilterAndUser(filter, 0, 10, "root");
    assertEquals(0, savedChallenges.size());
    when(spaceService.getMemberSpacesIds("root", 0, -1)).thenReturn(userSpaceIds);
    when(challengeStorage.findChallengesIdsByFilter(filter, 0, 10)).thenReturn(challengesIds);

    savedChallenges = challengeService.getChallengesByFilterAndUser(filter, 0, 10, "root");

    assertEquals(1, savedChallenges.size());

    //Test get most realized challenges
    filter.setOrderByRealizations(true);
    when(challengeStorage.findMostRealizedChallengesIds(0, 10)).thenReturn(challengesIds);
    when(challengeStorage.getChallengeById(challengesIds.get(0))).thenReturn(challenge);
    List<Challenge> popularChallenges = challengeService.getChallengesByFilterAndUser(filter, 0, 10, "root");
    assertEquals(popularChallenges.get(0).getId(), challenge.getId());
  }

  @Test
  public void testCountChallengesByFilterAndUser() {
    Challenge challenge = new Challenge(1l,
                                        "Challenge",
                                        "description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);
    List<Challenge> challenges = new ArrayList<>();
    challenges.add(challenge);
    RuleFilter filter = new RuleFilter();

    List<String> userSpaceIds = Collections.singletonList("1");

    when(spaceService.getMemberSpacesIds("root", 0, -1)).thenReturn(Collections.emptyList());
    int count = challengeService.countChallengesByFilterAndUser(filter, "root");
    assertEquals(0, count);
    when(spaceService.getMemberSpacesIds("root", 0, -1)).thenReturn(userSpaceIds);
    when(challengeStorage.countChallengesByFilter(filter)).thenReturn(3);

    count = challengeService.countChallengesByFilterAndUser(filter, "root");

    assertEquals(3, count);
  }

  @Test
  public void testClearUserChallengeCache() {
    doNothing().when(challengeStorage).clearCache();
    challengeService.clearUserChallengeCache();
    verify(challengeStorage, times(1)).clearCache();
  }

}

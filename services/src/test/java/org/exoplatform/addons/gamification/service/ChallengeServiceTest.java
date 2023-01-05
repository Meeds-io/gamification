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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.exoplatform.addons.gamification.service.configuration.ChallengeServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ChallengeServiceTest {

  private static MockedStatic<Utils> UTILS;

  @Mock
  private ChallengeStorage    challengeStorage;

  @Mock
  private SpaceService        spaceService;

  @Mock
  private ListenerService     listenerService;

  @Mock
  private ExoFeatureService   exoFeatureService;

  private ChallengeService    challengeService;

  @BeforeClass
  public static void initClassContext() {
    UTILS = mockStatic(Utils.class);
  }

  @AfterClass
  public static void cleanClassContext() {
    UTILS.close();
  }

  @Before
  public void setUp() throws Exception { // NOSONAR
    UTILS.reset();
    challengeService = new ChallengeServiceImpl(challengeStorage, spaceService, exoFeatureService, listenerService);
  }


  @Test
  public void testCreateChallenge() throws IllegalAccessException {
    // Given
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1L,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        1L,
                                        true);
    Challenge challengeCreated = new Challenge(1L,
                                               "new challenge",
                                               "challenge description",
                                               1L,
                                               new Date(System.currentTimeMillis()).toString(),
                                               new Date(System.currentTimeMillis() + 1).toString(),
                                               Collections.emptyList(),
                                               10L,
                                               1L,
                                               true); // Given
    Challenge challengeSystem = new Challenge(0,
                                              "new system challenge",
                                              "system challenge description",
                                              1L,
                                              new Date(System.currentTimeMillis()).toString(),
                                              new Date(System.currentTimeMillis() + 1).toString(),
                                              Collections.emptyList(),
                                              10L,
                                              1L,
                                              true);
    Challenge challengeCreatedSystem = new Challenge(2L,
                                                     "new challenge",
                                                     "challenge description",
                                                     1L,
                                                     new Date(System.currentTimeMillis()).toString(),
                                                     new Date(System.currentTimeMillis() + 1).toString(),
                                                     Collections.emptyList(),
                                                     10L,
                                                     1L,
                                                     true);
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");
    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challengeCreated);

    assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(null, "root"));
    assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(challengeCreated, "root"));

    UTILS.when(() -> Utils.isChallengeManager(any(Challenge.class), anyLong(), anyString())).thenReturn(false);
    assertThrows(IllegalAccessException.class, () -> challengeService.createChallenge(challenge, "root"));
    UTILS.when(() -> Utils.isSuperManager("root")).thenReturn(true);
    assertThrows(IllegalAccessException.class, () -> challengeService.createChallenge(challenge, "root"));
    UTILS.when(() -> Utils.isChallengeManager(any(Challenge.class), anyLong(), anyString())).thenReturn(true);
    challenge.setAudience(0);
    assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(challenge, "root"));
    challenge.setAudience(1L);
    UTILS.when(() -> Utils.isChallengeManager(any(Challenge.class), anyString())).thenReturn(true);

    Challenge savedChallenge = challengeService.createChallenge(challenge, "root");
    assertNotNull(savedChallenge);
    assertEquals(1L, savedChallenge.getId());
    when(challengeStorage.saveChallenge(challengeSystem, "SYSTEM")).thenReturn(challengeCreatedSystem);
    savedChallenge = challengeService.createChallenge(challengeSystem);
    assertNotNull(savedChallenge);
    assertEquals(2L, savedChallenge.getId());
  }

  @Test
  public void testUpdateChallenge() throws ObjectNotFoundException, IllegalAccessException {

    // Given
    Challenge challenge = new Challenge(1L,
                                        "update challenge",
                                        "challenge description",
                                        1L,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        1L,
                                        true);
    Challenge challenge1 = new Challenge(1L,
                                         "new challenge",
                                         "challenge description",
                                         1L,
                                         new Date(System.currentTimeMillis()).toString(),
                                         new Date(System.currentTimeMillis() + 1).toString(),
                                         Collections.emptyList(),
                                         10L,
                                         1L,
                                         true);

    Challenge challenge2 = new Challenge(1L,
                                         "update challenge",
                                         "challenge description",
                                         1L,
                                         new Date(System.currentTimeMillis()).toString(),
                                         new Date(System.currentTimeMillis() + 1).toString(),
                                         Collections.emptyList(),
                                         10L,
                                         1L,
                                         true);
    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challenge2);

    UTILS.when(() -> Utils.isChallengeManager(any(Challenge.class), anyLong(), anyString())).thenReturn(false);
    assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallenge(null, "root"));
    assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallenge(new Challenge(), "root"));
    UTILS.when(() -> Utils.isChallengeManager(any(Challenge.class), anyLong(), anyString())).thenReturn(true);
    UTILS.when(() -> Utils.getChallengeDomainDTO(any())).thenReturn(new DomainDTO());

    assertThrows(ObjectNotFoundException.class, () -> challengeService.updateChallenge(challenge, "root"));
    when(challengeStorage.getChallengeById(anyLong())).thenReturn(challenge1);
    UTILS.when(() -> Utils.isChallengeManager(any(Challenge.class), anyString())).thenReturn(true);

    Challenge challengeUpdated = challengeService.updateChallenge(challenge, "root");
    assertNotNull(challengeUpdated);
    assertEquals("update challenge", challengeUpdated.getTitle());
  }

  @Test
  public void testDeleteChallenge() throws ObjectNotFoundException, IllegalAccessException {
    // Given
    UTILS.when(() -> Utils.getChallengeDomainDTO(any())).thenReturn(new DomainDTO());
    Challenge challenge = new Challenge(1L,
                                        "update challenge",
                                        "challenge description",
                                        1L,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        1L,
                                        true);

    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isManager(space, "root")).thenReturn(true);
    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    UTILS.when(() -> Utils.isSuperManager("root")).thenReturn(true);
    Challenge storedChallenge = challengeService.getChallengeById(1L, "root");
    assertNotNull(storedChallenge);
    assertEquals(1L, storedChallenge.getId());

    // When
    assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(-1l, "root"));

    // When
    assertThrows(ObjectNotFoundException.class, () -> challengeService.deleteChallenge(2l, "root"));

    // When
    assertThrows(IllegalAccessException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

    // When
    UTILS.when(() -> Utils.countAnnouncementsByChallenge(1l)).thenReturn(2l);
    UTILS.when(() -> Utils.isChallengeManager(any(Challenge.class), anyLong(), anyString())).thenReturn(true);
    assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

    // When
    UTILS.when(() -> Utils.parseSimpleDate(challenge.getEndDate()))
         .thenReturn(Date.from(ZonedDateTime.now().plusDays(10).toInstant()));
    assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

    UTILS.when(() -> Utils.countAnnouncementsByChallenge(1l)).thenReturn(0l);
    UTILS.when(() -> Utils.parseSimpleDate(challenge.getEndDate()))
         .thenReturn(Date.from(ZonedDateTime.now().plusDays(-10).toInstant()));
    challengeService.deleteChallenge(challenge.getId(), "root");
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
                                        1L,
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
    UTILS.when(() -> Utils.getChallengeDomainDTO(any())).thenReturn(new DomainDTO());
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
                                        1L,
                                        true);
    List<Long> challengesIds = new ArrayList<>();
    challengesIds.add(challenge.getId());
    RuleFilter filter = new RuleFilter();

    List<String> userSpaceIds = Collections.singletonList("1");

    when(spaceService.getMemberSpacesIds("root", 0, -1)).thenReturn(Collections.emptyList());
    List<Challenge> savedChallenges = challengeService.getChallengesByFilterAndUser(filter, 0, 10, "root");
    assertEquals(0, savedChallenges.size());
    when(spaceService.getMemberSpacesIds("root", 0, -1)).thenReturn(userSpaceIds);
    UTILS.when(() -> Utils.getChallengeDomainDTO(any())).thenReturn(new DomainDTO());
    when(challengeStorage.findChallengesIdsByFilter(filter, 0, 10)).thenReturn(challengesIds);

    savedChallenges = challengeService.getChallengesByFilterAndUser(filter, 0, 10, "root");

    assertEquals(1, savedChallenges.size());

    // Test get most realized challenges
    filter.setOrderByRealizations(true);
    List<Long> spacesIds = new ArrayList<>();
    spacesIds.add(1L);
    when(challengeStorage.findMostRealizedChallengesIds(spacesIds, 0, 10)).thenReturn(challengesIds);
    when(challengeStorage.getChallengeById(challengesIds.get(0))).thenReturn(challenge);
    List<Challenge> popularChallenges = challengeService.getChallengesByFilterAndUser(filter, 0, 10, "root");
    assertEquals(popularChallenges.get(0).getId(), challenge.getId());
  }

  @Test
  public void testCountChallengesByFilterAndUser() {
    Challenge challenge = new Challenge(1L,
                                        "Challenge",
                                        "description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        1L,
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

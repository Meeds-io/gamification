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

import org.exoplatform.addons.gamification.service.configuration.AnnouncementServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AnnouncementServiceTest extends BaseExoTestCase {

  private static MockedStatic<Utils> UTILS;

  private AnnouncementStorage announcementStorage;

  private ChallengeService    challengeService;

  private AnnouncementService announcementService;

  private ListenerService     listenerService;

  private SpaceService        spaceService;

  private IdentityManager     identityManager;

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
    super.setUp();
    UTILS.reset();
    announcementStorage = mock(AnnouncementStorage.class);
    challengeService = mock(ChallengeService.class);
    spaceService = mock(SpaceService.class);
    identityManager = mock(IdentityManager.class);
    listenerService = mock(ListenerService.class);
    announcementService = new AnnouncementServiceImpl(announcementStorage,
                                                      challengeService,
                                                      identityManager,
                                                      listenerService);
  }

  @Test
  public void testCreateAnnouncement() throws ObjectNotFoundException, IllegalAccessException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);

    Announcement announcement = new Announcement(0,
                                                 challenge.getId(),
                                                 challenge.getTitle(),
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null);

    Announcement announcementWithoutAssignee = new Announcement(0,
                                                                challenge.getId(),
                                                                challenge.getTitle(),

                                                                null,
                                                                "announcement comment",
                                                                1L,
                                                                new Date(System.currentTimeMillis()).toString(),
                                                                null);

    Announcement createdAnnouncement = new Announcement(1,
                                                        challenge.getId(),
                                                        challenge.getTitle(),

                                                        1L,
                                                        "announcement comment",
                                                        1L,
                                                        new Date(System.currentTimeMillis()).toString(),
                                                        null);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    String[] spaceMembers = { "root" };
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    space.setManagers(spaceMembers);
    space.setMembers(spaceMembers);
    space.setRedactors(new String[0]);
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isRedactor(space, "root")).thenReturn(true);
    when(identityManager.getOrCreateIdentity("space", "root")).thenReturn(spaceIdentity);
    when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);
    when(identityManager.getOrCreateUserIdentity("root")).thenReturn(rootIdentity);
    when(announcementStorage.saveAnnouncement(announcement)).thenReturn(createdAnnouncement);
    when(announcementStorage.getAnnouncementById(createdAnnouncement.getId())).thenReturn(createdAnnouncement);

    Identity identity = mock(Identity.class);
    when(identity.isEnable()).thenReturn(true);
    UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any()))
         .thenReturn(identity);
    when(identity.getId()).thenReturn("1");

    Map<String, String> templateParams = new HashMap<>();

    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.createAnnouncement(null, templateParams, "root", false));
    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.createAnnouncement(createdAnnouncement, templateParams, "root", false));

    assertThrows(ObjectNotFoundException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root", false));

    when(challengeService.getChallengeById(anyLong())).thenReturn(challenge);
    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.createAnnouncement(announcementWithoutAssignee, templateParams, "root", false));

    UTILS.when(() -> Utils.canAnnounce(any(), anyString()))
         .thenReturn(false);
    assertThrows(ObjectNotFoundException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root", false));
    when(identityManager.getIdentity("1")).thenReturn(identity);
    assertThrows(IllegalAccessException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root", false));
    UTILS.when(() -> Utils.canAnnounce(any(), anyString()))
         .thenReturn(true);

    Announcement newAnnouncement = null;
    newAnnouncement = announcementService.createAnnouncement(announcement, templateParams, "root", false);
    assertNotNull(newAnnouncement);
    assertEquals(1l, newAnnouncement.getId());
  }

  @Test
  public void testUpdateAnnouncement() throws ObjectNotFoundException, IllegalAccessException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);
    Announcement announcement = new Announcement(0,
                                                 challenge.getId(),
                                                 challenge.getTitle(),

                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null);
    Announcement createdAnnouncement = new Announcement(1,
                                                        challenge.getId(),
                                                        challenge.getTitle(),
                                                        1L,
                                                        "announcement comment",
                                                        1L,
                                                        new Date(System.currentTimeMillis()).toString(),
                                                        null);
    Announcement editedAnnouncement = new Announcement(1,
                                                       challenge.getId(),
                                                       challenge.getTitle(),
                                                       1L,
                                                       "announcement comment",
                                                       1L,
                                                       new Date(System.currentTimeMillis()).toString(),
                                                       1L);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    String[] spaceMembers = { "root" };
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    space.setManagers(spaceMembers);
    space.setMembers(spaceMembers);
    space.setRedactors(new String[0]);
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isRedactor(space, "root")).thenReturn(true);
    when(identityManager.getOrCreateIdentity("space", "root")).thenReturn(spaceIdentity);
    when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);
    when(identityManager.getOrCreateUserIdentity("root")).thenReturn(rootIdentity);
    when(announcementStorage.saveAnnouncement(announcement)).thenReturn(createdAnnouncement);
    when(announcementStorage.saveAnnouncement(createdAnnouncement)).thenReturn(editedAnnouncement);
    UTILS.when(() -> Utils.canAnnounce(any(), anyString()))
         .thenReturn(true);
    Identity identity = mock(Identity.class);
    when(identity.isEnable()).thenReturn(true);
    when(identity.getId()).thenReturn("1");
    UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any()))
         .thenReturn(identity);
    when(announcementStorage.getAnnouncementById(1L)).thenReturn(createdAnnouncement);

    Announcement newAnnouncement = null;
    when(challengeService.getChallengeById(anyLong())).thenReturn(challenge);
    when(identityManager.getIdentity("1")).thenReturn(identity);
    newAnnouncement = announcementService.createAnnouncement(announcement, new HashMap<>(), "root", false);
    assertNotNull(newAnnouncement);
    newAnnouncement.setActivityId(1L);

    assertThrows(IllegalArgumentException.class, () -> announcementService.updateAnnouncement(null));

    assertThrows(IllegalArgumentException.class, () -> announcementService.updateAnnouncement(announcement));

    when(announcementStorage.getAnnouncementById(1L)).thenReturn(null);
    Announcement finalNewAnnouncement = newAnnouncement;
    assertThrows(ObjectNotFoundException.class, () -> announcementService.updateAnnouncement(finalNewAnnouncement));
    when(announcementStorage.getAnnouncementById(1L)).thenReturn(createdAnnouncement);

    Announcement updatedAnnouncement = null;
    updatedAnnouncement = announcementService.updateAnnouncement(newAnnouncement);
    assertEquals(updatedAnnouncement.getId(), newAnnouncement.getId());
    assertEquals(updatedAnnouncement.getActivityId(), newAnnouncement.getActivityId());

  }

  @Test
  public void testGetAnnouncementByChallenge() throws ObjectNotFoundException, IllegalAccessException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);
    Announcement announcement1 = new Announcement(0,
                                                  challenge.getId(),
                                                  challenge.getTitle(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  null);
    Announcement announcement2 = new Announcement(1,
                                                  challenge.getId(),
                                                  challenge.getTitle(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  null);
    Announcement announcement3 = new Announcement(1,
                                                  challenge.getId(),
                                                  challenge.getTitle(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  1L);
    List<Announcement> announcementList = new ArrayList<>();
    announcementList.add(announcement1);
    announcementList.add(announcement2);
    announcementList.add(announcement3);
    when(announcementStorage.findAllAnnouncementByChallenge(challenge.getId(), 0, 10, PeriodType.ALL, "ALL")).thenReturn(announcementList);

    assertThrows(IllegalArgumentException.class, () -> announcementService.findAllAnnouncementByChallenge(0, 0, 10, PeriodType.ALL, "ALL"));

    List<Announcement> newAnnouncementList = null;
    newAnnouncementList = announcementService.findAllAnnouncementByChallenge(challenge.getId(), 0, 10, PeriodType.ALL, "ALL");
    assertNotNull(newAnnouncementList);
    assertEquals(announcementList, newAnnouncementList);
  }

  @Test
  public void testCountAllAnnouncementsByChallenge() throws ObjectNotFoundException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification",
                                        true);

    when(announcementStorage.countAnnouncementsByChallenge(challenge.getId())).thenReturn(10l);

    assertThrows(IllegalArgumentException.class, () -> announcementService.countAllAnnouncementsByChallenge(0l));
    assertThrows(ObjectNotFoundException.class, () -> announcementService.countAllAnnouncementsByChallenge(challenge.getId()));
    when(challengeService.getChallengeById(anyLong())).thenReturn(challenge);

    Long count = announcementService.countAllAnnouncementsByChallenge(challenge.getId());
    assertEquals(10l, (long) count);
  }

  @Test
  public void testGetAnnouncementById() {
    Announcement announcement = new Announcement(1,
                                                 1l,
                                                 "challenge title",
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null);
    when(announcementStorage.getAnnouncementById(announcement.getId())).thenReturn(announcement);

    assertThrows(IllegalArgumentException.class, () -> announcementService.getAnnouncementById(0l));
    assertThrows(IllegalArgumentException.class, () -> announcementService.getAnnouncementById(-1l));
    Announcement savedAnnouncement = announcementService.getAnnouncementById(1l);
    assertNotNull(savedAnnouncement);
    assertEquals(announcement.getId(), savedAnnouncement.getId());
  }
}

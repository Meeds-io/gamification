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

import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;
import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM;
import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ID_TEMPLATE_PARAM;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.configuration.AnnouncementServiceImpl;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AnnouncementServiceTest extends BaseExoTestCase {

  private static MockedStatic<Utils> UTILS;

  @Mock
  private AnnouncementStorage        announcementStorage;

  @Mock
  private RuleService                ruleService;

  @Mock
  private ListenerService            listenerService;

  @Mock
  private SpaceService               spaceService;

  @Mock
  private IdentityManager            identityManager;

  @Mock
  private ActivityManager            activityManager;

  private AnnouncementServiceImpl    announcementService;

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
    announcementService = new AnnouncementServiceImpl(announcementStorage,
                                                      ruleService,
                                                      spaceService,
                                                      identityManager,
                                                      activityManager,
                                                      listenerService);
  }

  @Test
  public void testCreateAnnouncement() throws ObjectNotFoundException, IllegalAccessException {
    RuleDTO rule = newRule();

    Announcement announcement = new Announcement(0,
                                                 rule.getId(),
                                                 rule.getTitle(),
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 Utils.toSimpleDateFormat(new Date()),
                                                 null);

    Announcement announcementWithoutAssignee = new Announcement(0,
                                                                rule.getId(),
                                                                rule.getTitle(),

                                                                null,
                                                                "announcement comment",
                                                                1L,
                                                                Utils.toSimpleDateFormat(new Date()),
                                                                null);

    Announcement createdAnnouncement = new Announcement(1,
                                                        rule.getId(),
                                                        rule.getTitle(),

                                                        1L,
                                                        "announcement comment",
                                                        1L,
                                                        Utils.toSimpleDateFormat(new Date()),
                                                        null);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    String[] spaceMembers = {
        "root"
    };
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
    when(announcementStorage.createAnnouncement(announcement)).thenReturn(createdAnnouncement);
    when(announcementStorage.getAnnouncementById(createdAnnouncement.getId())).thenReturn(createdAnnouncement);

    Identity identity = mock(Identity.class);
    when(identity.isEnable()).thenReturn(true);
    UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any()))
         .thenReturn(identity);
    when(identity.getId()).thenReturn("1");

    Map<String, String> templateParams = new HashMap<>();

    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.createAnnouncement(null, templateParams, "root"));
    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.createAnnouncement(createdAnnouncement, templateParams, "root"));

    assertThrows(ObjectNotFoundException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root"));

    when(ruleService.findRuleById(rule.getId(), "root")).thenReturn(rule);
    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.createAnnouncement(announcementWithoutAssignee, templateParams, "root"));

    UTILS.when(() -> Utils.canAnnounce(any(), anyString()))
         .thenReturn(false);
    assertThrows(ObjectNotFoundException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root"));
    when(identityManager.getIdentity("1")).thenReturn(identity);
    assertThrows(IllegalAccessException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root"));
    UTILS.when(() -> Utils.canAnnounce(any(), anyString()))
         .thenReturn(true);

    Announcement newAnnouncement = announcementService.createAnnouncement(announcement, templateParams, "root");
    assertNotNull(newAnnouncement);
    assertEquals(1l, newAnnouncement.getId());
  }

  @Test
  public void testCreateAnnouncementActivity() throws ObjectNotFoundException, IllegalAccessException {
    String spaceId = "1";
    String userIdentityId = "3";
    String activityId = "9";
    int announcementId = 56;

    RuleDTO rule = newRule();

    Identity userIdentity = new Identity();
    userIdentity.setId(userIdentityId);
    userIdentity.setProviderId(OrganizationIdentityProvider.NAME);
    userIdentity.setRemoteId("root");
    userIdentity.setEnable(true);

    Announcement announcement = new Announcement(0,
                                                 rule.getId(),
                                                 rule.getTitle(),
                                                 Long.parseLong(userIdentityId),
                                                 "announcement comment",
                                                 1L,
                                                 Utils.toSimpleDateFormat(new Date()),
                                                 null);

    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");

    String[] spaceMembers = {
        "root"
    };
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
    when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(userIdentity);
    when(identityManager.getOrCreateUserIdentity("root")).thenReturn(userIdentity);
    when(announcementStorage.createAnnouncement(announcement)).thenAnswer(invocation -> {
      announcement.setId(announcementId);
      return announcement;
    });

    Identity identity = mock(Identity.class);
    when(identity.isEnable()).thenReturn(true);
    UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any()))
         .thenReturn(identity);
    when(identity.getId()).thenReturn("1");

    Map<String, String> templateParams = new HashMap<>();

    when(ruleService.findRuleById(anyLong())).thenReturn(rule);
    UTILS.when(() -> Utils.canAnnounce(any(), anyString())).thenReturn(false);
    when(identityManager.getIdentity("1")).thenReturn(identity);
    UTILS.when(() -> Utils.canAnnounce(any(), anyString())).thenReturn(true);
    UTILS.when(() -> Utils.buildActivityParams(any(), any())).thenCallRealMethod();
    when(announcementStorage.getAnnouncementById(announcementId)).thenReturn(announcement);
    when(spaceService.getSpaceById(spaceId)).thenReturn(space);
    when(identityManager.getOrCreateSpaceIdentity(space.getPrettyName())).thenReturn(spaceIdentity);
    when(identityManager.getIdentity(userIdentity.getId())).thenReturn(userIdentity);
    when(ruleService.findRuleById(rule.getId(), userIdentity.getRemoteId())).thenReturn(rule);
    doAnswer(invocation -> {
      ExoSocialActivityImpl activity = invocation.getArgument(1);
      activity.setId(activityId);
      when(activityManager.getActivity(activityId)).thenReturn(activity);
      return null;
    }).when(activityManager).saveActivityNoReturn(any(), any());
    AnnouncementActivity announcementActivity = new AnnouncementActivity(6,
                                                                         rule.getId(),
                                                                         rule.getTitle(),
                                                                         Long.parseLong(userIdentityId),
                                                                         "announcement comment",
                                                                         Long.parseLong(userIdentityId),
                                                                         Utils.toSimpleDateFormat(new Date()),
                                                                         null,
                                                                         null);

    when(spaceService.getSpaceById(spaceId)).thenReturn(space);
    when(identityManager.getOrCreateSpaceIdentity(space.getPrettyName())).thenReturn(spaceIdentity);
    when(identityManager.getIdentity(userIdentity.getId())).thenReturn(userIdentity);
    when(ruleService.findRuleById(rule.getId(), userIdentity.getRemoteId())).thenReturn(rule);
    doAnswer(invocation -> {
      ExoSocialActivityImpl activity = invocation.getArgument(1);
      activity.setId(activityId);
      when(activityManager.getActivity(activityId)).thenReturn(activity);
      return null;
    }).when(activityManager).saveActivityNoReturn(any(), any());

    Announcement newAnnouncement = null;
    newAnnouncement = announcementService.createAnnouncement(announcement, templateParams, "root");
    assertNotNull(newAnnouncement);
    assertEquals(announcementId, newAnnouncement.getId());

    verify(activityManager, times(1)).saveActivityNoReturn(
                                                           argThat(spaceIdentityParam -> {
                                                             return StringUtils.equals(space.getPrettyName(),
                                                                                       String.valueOf(spaceIdentityParam.getRemoteId()));
                                                           }),
                                                           argThat(activity -> {
                                                             assertEquals(ANNOUNCEMENT_ACTIVITY_TYPE, activity.getType());
                                                             assertEquals(announcementActivity.getComment(), activity.getTitle());
                                                             assertEquals(userIdentityId, activity.getUserId());
                                                             assertEquals(String.valueOf(announcementId),
                                                                          activity.getTemplateParams()
                                                                                  .get(ANNOUNCEMENT_ID_TEMPLATE_PARAM));
                                                             assertEquals(rule.getTitle(),
                                                                          activity.getTemplateParams()
                                                                                  .get(ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM));
                                                             return true;
                                                           }));
  }

  @Test
  public void testUpdateAnnouncement() throws ObjectNotFoundException, IllegalAccessException {
    RuleDTO rule = newRule();
    Announcement createdAnnouncement = new Announcement(1,
                                                        rule.getId(),
                                                        rule.getTitle(),
                                                        1L,
                                                        "announcement comment",
                                                        1L,
                                                        Utils.toSimpleDateFormat(new Date()),
                                                        null);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    String[] spaceMembers = {
        "root"
    };
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
    UTILS.when(() -> Utils.canAnnounce(any(), anyString()))
         .thenReturn(true);
    Identity identity = mock(Identity.class);
    when(identity.isEnable()).thenReturn(true);
    when(identity.getId()).thenReturn("1");
    UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any()))
         .thenReturn(identity);
    when(announcementStorage.getAnnouncementById(1L)).thenReturn(createdAnnouncement);

    when(ruleService.findRuleById(rule.getId(), "root")).thenReturn(rule);
    when(identityManager.getIdentity("1")).thenReturn(identity);

    assertThrows(IllegalArgumentException.class, () -> announcementService.updateAnnouncementComment(0, "comment"));
    assertThrows(IllegalArgumentException.class, () -> announcementService.updateAnnouncementComment(2l, null));

    when(announcementStorage.getAnnouncementById(1L)).thenReturn(null);
    when(announcementStorage.updateAnnouncementComment(2l, "comment")).thenThrow(new ObjectNotFoundException("Fake exception"));
    assertThrows(ObjectNotFoundException.class, () -> announcementService.updateAnnouncementComment(2l, "comment"));
    when(announcementStorage.updateAnnouncementComment(1l, "comment")).thenReturn(createdAnnouncement);

    Announcement updatedAnnouncement = announcementService.updateAnnouncementComment(1l, "comment");
    assertNotNull(updatedAnnouncement);
  }

  @Test
  public void testCancelAnnouncement() throws ObjectNotFoundException, IllegalAccessException {
    Identity identity = mock(Identity.class);
    when(identity.isEnable()).thenReturn(true);
    UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    when(identity.getId()).thenReturn("1");
    UTILS.when(() -> Utils.canAnnounce(any(), anyString())).thenReturn(true);
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    String username = "root";
    rootIdentity.setRemoteId(username);
    Identity johnIdentity = new Identity();
    johnIdentity.setId("2");
    johnIdentity.setProviderId("organization");
    johnIdentity.setRemoteId("john");
    when(identityManager.getOrCreateIdentity("organization", username)).thenReturn(rootIdentity);
    when(identityManager.getOrCreateUserIdentity(username)).thenReturn(rootIdentity);
    when(identityManager.getOrCreateUserIdentity("john")).thenReturn(johnIdentity);
    RuleDTO rule = newRule();
    Announcement createdAnnouncement = new Announcement(1,
                                                        rule.getId(),
                                                        rule.getTitle(),

                                                        1L,
                                                        "announcement comment",
                                                        1L,
                                                        Utils.toSimpleDateFormat(new Date()),
                                                        1L);
    Announcement canceledAnnouncement = new Announcement(1,
                                                         rule.getId(),
                                                         rule.getTitle(),
                                                         1L,
                                                         "announcement comment",
                                                         1L,
                                                         Utils.toSimpleDateFormat(new Date()),
                                                         null);

    when(ruleService.findRuleById(rule.getId(), username)).thenReturn(rule);
    when(identityManager.getIdentity("1")).thenReturn(identity);
    when(announcementStorage.getAnnouncementById(createdAnnouncement.getId())).thenReturn(createdAnnouncement);
    when(announcementStorage.deleteAnnouncement(createdAnnouncement.getId())).thenReturn(canceledAnnouncement);

    assertThrows(IllegalArgumentException.class, () -> announcementService.deleteAnnouncement(-1L, username));

    assertThrows(ObjectNotFoundException.class, () -> announcementService.deleteAnnouncement(500L, username));

    assertThrows(IllegalAccessException.class, () -> announcementService.deleteAnnouncement(1L, "john"));

    assertNull(announcementService.deleteAnnouncement(createdAnnouncement.getId(), username).getActivityId());
  }

  @Test
  public void testGetAnnouncementByChallenge() throws ObjectNotFoundException, IllegalAccessException {
    RuleDTO rule = newRule();
    Announcement announcement1 = new Announcement(0,
                                                  rule.getId(),
                                                  rule.getTitle(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  Utils.toSimpleDateFormat(new Date()),
                                                  null);
    Announcement announcement2 = new Announcement(1,
                                                  rule.getId(),
                                                  rule.getTitle(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  Utils.toSimpleDateFormat(new Date()),
                                                  null);
    Announcement announcement3 = new Announcement(1,
                                                  rule.getId(),
                                                  rule.getTitle(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  Utils.toSimpleDateFormat(new Date()),
                                                  1L);
    List<Announcement> announcementList = new ArrayList<>();
    announcementList.add(announcement1);
    announcementList.add(announcement2);
    announcementList.add(announcement3);
    when(announcementStorage.findAnnouncements(rule.getId(), 0, 10, PeriodType.ALL, null)).thenReturn(announcementList);

    String username = "demo";
    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.findAnnouncements(0, 0, 10, PeriodType.ALL, null, null));
    assertThrows(ObjectNotFoundException.class,
                 () -> announcementService.findAnnouncements(rule.getId(), 0, 10, PeriodType.ALL, null, username));

    when(ruleService.findRuleById(rule.getId(), username)).thenThrow(new IllegalAccessException());
    assertThrows(IllegalAccessException.class,
                 () -> announcementService.findAnnouncements(rule.getId(), 0, 10, PeriodType.ALL, null, username));

    reset(ruleService);
    when(ruleService.findRuleById(rule.getId(), username)).thenReturn(rule);
    List<Announcement> newAnnouncementList = announcementService.findAnnouncements(rule.getId(),
                                                                                   0,
                                                                                   10,
                                                                                   PeriodType.ALL,
                                                                                   null,
                                                                                   username);
    assertNotNull(newAnnouncementList);
    assertEquals(announcementList, newAnnouncementList);
  }

  @Test
  public void testGetAnnouncementByChallengeByEarnerType() throws ObjectNotFoundException, IllegalAccessException {
    identityManager.getOrCreateIdentity("1L", "1L");
    Space space = new Space();
    space.setId("2L");
    spaceService.createSpace(space, "1L");

    RuleDTO rule = newRule();
    when(ruleService.findRuleById(rule.getId(), "root")).thenReturn(rule);

    Announcement announcement2 = new Announcement(1,
                                                  rule.getId(),
                                                  rule.getTitle(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  Utils.toSimpleDateFormat(new Date()),
                                                  null);
    Announcement announcement3 = new Announcement(1,
                                                  rule.getId(),
                                                  rule.getTitle(),
                                                  2L,
                                                  "announcement comment",
                                                  1L,
                                                  Utils.toSimpleDateFormat(new Date()),
                                                  1L);
    List<Announcement> announcementList = new ArrayList<>();
    announcementList.add(announcement2);
    announcementList.add(announcement3);
    when(announcementStorage.findAnnouncements(rule.getId(),
                                               0,
                                               10,
                                               PeriodType.ALL,
                                               IdentityType.USER)).thenReturn(announcementList);

    assertThrows(IllegalArgumentException.class,
                 () -> announcementService.findAnnouncements(0, 0, 10, PeriodType.ALL, IdentityType.USER, null));

    List<Announcement> announcements = announcementService.findAnnouncements(rule.getId(),
                                                                             0,
                                                                             10,
                                                                             PeriodType.ALL,
                                                                             IdentityType.USER,
                                                                             "root");
    assertNotNull(announcements);
    assertEquals(announcementList, announcements);
  }

  @Test
  public void testCountAllAnnouncementsByChallenge() throws ObjectNotFoundException {
    RuleDTO rule = newRule();

    when(announcementStorage.countAnnouncements(rule.getId())).thenReturn(10);

    assertThrows(IllegalArgumentException.class, () -> announcementService.countAnnouncements(0l));
    assertThrows(ObjectNotFoundException.class, () -> announcementService.countAnnouncements(rule.getId()));
    when(ruleService.findRuleById(anyLong())).thenReturn(rule);
    assertEquals(10, announcementService.countAnnouncements(rule.getId()));
  }

  @Test
  public void testGetAnnouncementById() {
    Announcement announcement = new Announcement(1,
                                                 1l,
                                                 "challenge title",
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 Utils.toSimpleDateFormat(new Date()),
                                                 null);
    when(announcementStorage.getAnnouncementById(announcement.getId())).thenReturn(announcement);

    assertThrows(IllegalArgumentException.class, () -> announcementService.getAnnouncementById(0l));
    assertThrows(IllegalArgumentException.class, () -> announcementService.getAnnouncementById(-1l));
    Announcement savedAnnouncement = announcementService.getAnnouncementById(1l);
    assertNotNull(savedAnnouncement);
    assertEquals(announcement.getId(), savedAnnouncement.getId());
  }

  private RuleDTO newRule() {
    RuleDTO rule = new RuleDTO();
    rule.setId(1l);
    rule.setTitle("new challenge");
    rule.setDescription("challenge description");
    rule.setStartDate(Utils.toSimpleDateFormat(new Date()));
    rule.setEndDate(Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 5000)));
    rule.setProgram(newProgram());
    rule.setEnabled(true);
    rule.setScore(10);
    return rule;
  }

  private ProgramDTO newProgram() {
    return new ProgramDTO(1, "gamification", null, 0, 1l, null, null, null, null, true, true, 0, null, 0, null, null, 0);
  }

}

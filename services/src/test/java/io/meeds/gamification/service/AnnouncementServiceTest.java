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

import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;
import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM;
import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_ID_TEMPLATE_PARAM;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
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

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.AnnouncementActivity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.rest.model.RealizationValidityContext;
import io.meeds.gamification.service.impl.AnnouncementServiceImpl;
import io.meeds.gamification.storage.AnnouncementStorage;
import io.meeds.gamification.utils.Utils;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AnnouncementServiceTest extends BaseExoTestCase {

  private static MockedStatic<Utils> UTILS;

  @Mock
  private AnnouncementStorage        announcementStorage;

  @Mock
  private RuleService                ruleService;

  @Mock
  private RealizationService         realizationService;

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
                                                      realizationService,
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

    assertThrows(IllegalStateException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root"));
    rule.setType(EntityType.MANUAL);
    RealizationValidityContext validityContext = new RealizationValidityContext();
    validityContext.setValidAudience(false);
    when(realizationService.getRealizationValidityContext(rule, identity.getId())).thenReturn(validityContext);
    assertThrows(IllegalAccessException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root"));
    when(identityManager.getIdentity("1")).thenReturn(identity);
    assertThrows(IllegalAccessException.class,
                 () -> announcementService.createAnnouncement(announcement, templateParams, "root"));

    validityContext.setValidAudience(true);
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
    rule.setType(EntityType.MANUAL);
    ruleService.updateRule(rule);

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
    when(identityManager.getIdentity("1")).thenReturn(identity);
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
    when(realizationService.getRealizationValidityContext(rule,
                                                          userIdentity.getId())).thenReturn(new RealizationValidityContext());

    Announcement newAnnouncement = announcementService.createAnnouncement(announcement, templateParams, "root");
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
    ProgramDTO program = new ProgramDTO();
    program.setId(1l);
    program.setTitle("gamification");
    program.setAudienceId(1l);
    program.setEnabled(true);
    return program;
  }

}

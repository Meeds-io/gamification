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

package io.meeds.gamification.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Date;

import io.meeds.gamification.model.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.social.core.identity.model.Identity;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.storage.mapper.ProgramMapper;
import io.meeds.gamification.utils.Utils;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AnnouncementStorageTest {

  private static MockedStatic<Utils>               UTILS;

  private static MockedStatic<ProgramMapper>       DOMAIN_MAPPER;

  @Mock
  private RealizationStorage                       realizationStorage;

  @Mock
  private RuleStorage                              ruleStorage;

  private AnnouncementStorage                      announcementStorage;

  @BeforeClass
  public static void initClassContext() {
    UTILS = mockStatic(Utils.class);
    DOMAIN_MAPPER = mockStatic(ProgramMapper.class);
  }

  @AfterClass
  public static void cleanClassContext() {
    UTILS.close();
    DOMAIN_MAPPER.close();
  }

  @Before
  public void setUp() throws Exception { // NOSONAR
    UTILS.reset();
    DOMAIN_MAPPER.reset();

    UTILS.when(() -> Utils.parseSimpleDate(any())).thenCallRealMethod();
    UTILS.when(() -> Utils.toSimpleDateFormat(any())).thenCallRealMethod();
    announcementStorage = new AnnouncementStorage(realizationStorage, ruleStorage);
  }

  @Test
  public void testSaveAnnouncement() {
    Date startDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(System.currentTimeMillis() + 2);
    RuleDTO rule = new RuleDTO();
    rule.setId(1l);
    rule.setTitle("new challenge");
    rule.setDescription("challenge description");
    rule.setStartDate(Utils.toSimpleDateFormat(startDate));
    rule.setEndDate(Utils.toSimpleDateFormat(endDate));
    rule.setScore(10);
    EventDTO event = new EventDTO();
    event.setTitle("gamification");
    event.setTrigger("gamification");
    event.setType("gamification");
    rule.setEvent(event);
    rule.setEnabled(true);

    Date createDate = new Date(System.currentTimeMillis() + 1);
    Announcement announcement = new Announcement(0,
                                                 rule.getId(),
                                                 rule.getTitle(),
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 Utils.toSimpleDateFormat(createDate),
                                                 null);

    RealizationDTO announcementRealization = new RealizationDTO();
    announcementRealization.setEarnerId(announcement.getAssignee().toString());
    announcementRealization.setEarnerType(IdentityType.USER.name());
    announcementRealization.setCreator(announcement.getCreator());
    announcementRealization.setComment(announcement.getComment());
    announcementRealization.setRuleId(rule.getId());
    announcementRealization.setCreatedDate(Utils.toSimpleDateFormat(createDate));

    RealizationDTO newAnnouncementRealization = new RealizationDTO();
    newAnnouncementRealization.setEarnerId(announcement.getAssignee().toString());
    newAnnouncementRealization.setCreator(announcementRealization.getCreator());
    newAnnouncementRealization.setComment(announcementRealization.getComment());
    newAnnouncementRealization.setRuleId(rule.getId());
    newAnnouncementRealization.setCreatedDate(Utils.toSimpleDateFormat(createDate));
    newAnnouncementRealization.setId(1l);

    Announcement announcementRealizationFromEntity = new Announcement();
    announcementRealizationFromEntity.setAssignee(Long.valueOf(newAnnouncementRealization.getEarnerId()));
    announcementRealizationFromEntity.setCreator(newAnnouncementRealization.getCreator());
    announcementRealizationFromEntity.setComment(newAnnouncementRealization.getComment());
    announcementRealizationFromEntity.setCreatedDate(Utils.toSimpleDateFormat(createDate));
    announcementRealizationFromEntity.setChallengeId(newAnnouncementRealization.getRuleId());
    announcementRealizationFromEntity.setId(newAnnouncementRealization.getId());

    Identity identity = mock(Identity.class);
    when(ruleStorage.findRuleById(anyLong())).thenReturn(rule);
    when(realizationStorage.createRealization(any())).thenReturn(newAnnouncementRealization);
    UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    ProgramDTO program = new ProgramDTO();
    program.setTitle("gamification");
    ProgramEntity domainEntity = new ProgramEntity();
    domainEntity.setTitle("gamification");
    DOMAIN_MAPPER.when(() -> ProgramMapper.toEntity(program)).thenReturn(domainEntity);

    Announcement createdAnnouncement = announcementStorage.createAnnouncement(announcement);

    // Then
    assertNotNull(createdAnnouncement);
    assertEquals(1l, createdAnnouncement.getId());

    // Cancel announcement
    // When
    RealizationDTO realization = new RealizationDTO();
    realization.setEarnerId(announcement.getAssignee().toString());
    realization.setCreator(announcementRealization.getCreator());
    realization.setRuleId(rule.getId());
    realization.setComment(announcementRealization.getComment());
    realization.setCreatedDate(Utils.toSimpleDateFormat(createDate));
    realization.setId(1L);
    realization.setActivityId(null);
    realization.setObjectId(null);

    announcementRealizationFromEntity = new Announcement();
    announcementRealizationFromEntity.setAssignee(Long.valueOf(newAnnouncementRealization.getEarnerId()));
    announcementRealizationFromEntity.setCreator(newAnnouncementRealization.getCreator());
    announcementRealizationFromEntity.setComment(newAnnouncementRealization.getComment());
    announcementRealizationFromEntity.setCreatedDate(Utils.toSimpleDateFormat(createDate));
    announcementRealizationFromEntity.setChallengeId(newAnnouncementRealization.getRuleId());
    announcementRealizationFromEntity.setId(newAnnouncementRealization.getId());
    announcementRealizationFromEntity.setActivityId(null);

    when(realizationStorage.getRealizationById(newAnnouncementRealization.getId())).thenReturn(realization);
    when(realizationStorage.updateRealization(any())).thenReturn(realization);
    Announcement deletedAnnouncement = announcementStorage.deleteAnnouncement(announcement.getId());

    // Then
    assertNotNull(deletedAnnouncement);
    assertEquals(1L, deletedAnnouncement.getId());
    assertNull(deletedAnnouncement.getActivityId());
  }

  @Test
  public void testGetAnnouncementById() {
    Date startDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(System.currentTimeMillis() + 1);
    RuleEntity ruleEntity = new RuleEntity();
    ruleEntity.setDescription("challenge description");
    ruleEntity.setTitle("new challenge");
    ruleEntity.setStartDate(startDate);
    ruleEntity.setEndDate(endDate);
    ruleEntity.setId(1l);

    Date createDate = new Date(System.currentTimeMillis());
    RealizationDTO realization = new RealizationDTO();
    realization.setId(1l);
    realization.setEarnerId("1");
    realization.setEarnerId("1");
    realization.setCreator(1L);
    realization.setRuleId(ruleEntity.getId());
    realization.setComment("announcement comment");
    realization.setCreatedDate(Utils.toSimpleDateFormat(createDate));

    Announcement announcementFromEntity = new Announcement();
    announcementFromEntity.setId(realization.getId());
    announcementFromEntity.setAssignee(Long.valueOf(realization.getEarnerId()));
    announcementFromEntity.setCreator(realization.getCreator());
    announcementFromEntity.setComment(realization.getComment());
    announcementFromEntity.setCreatedDate(Utils.toSimpleDateFormat(createDate));
    announcementFromEntity.setChallengeId(realization.getRuleId());
    announcementFromEntity.setId(realization.getId());

    when(realizationStorage.getRealizationById(anyLong())).thenReturn(realization);
    Announcement createdAnnouncement = announcementStorage.getAnnouncementById(1l);

    // Then
    assertNotNull(createdAnnouncement);
    assertEquals(1l, createdAnnouncement.getId());
    assertEquals(announcementFromEntity, createdAnnouncement);
  }
}

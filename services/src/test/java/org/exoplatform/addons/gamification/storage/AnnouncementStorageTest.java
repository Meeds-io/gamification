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

package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.social.core.identity.model.Identity;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AnnouncementStorageTest {

  private static MockedStatic<Utils>        UTILS;

  private static MockedStatic<EntityMapper> ENTITY_MAPPER;

  private static MockedStatic<DomainMapper> DOMAIN_MAPPER;

  private GamificationHistoryDAO announcementDAO;

  private RuleDAO                ruleDAO;

  private AnnouncementStorage    announcementStorage;

  @BeforeClass
  public static void initClassContext() {
    UTILS = mockStatic(Utils.class);
    ENTITY_MAPPER = mockStatic(EntityMapper.class);
    DOMAIN_MAPPER = mockStatic(DomainMapper.class);
  }

  @AfterClass
  public static void cleanClassContext() {
    UTILS.close();
    ENTITY_MAPPER.close();
    DOMAIN_MAPPER.close();
  }

  @Before
  public void setUp() throws Exception { // NOSONAR
    UTILS.reset();
    ENTITY_MAPPER.reset();
    DOMAIN_MAPPER.reset();

    announcementDAO = mock(GamificationHistoryDAO.class);
    ruleDAO = mock(RuleDAO.class);
    announcementStorage = new AnnouncementStorage(announcementDAO, ruleDAO);
  }

    @Test
    public void testSaveAnnouncement() {
        Date startDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis() + 2);
        Challenge challenge = new Challenge(1l,
                "new challenge",
                "challenge description",
                1l,
                startDate.toString(),
                endDate.toString(),
                Collections.emptyList(),
                10L,
                "gamification",
                true);

        RuleEntity challengeEntity = new RuleEntity();
        challengeEntity.setDescription(challenge.getDescription());
        challengeEntity.setTitle(challenge.getTitle());
        challengeEntity.setStartDate(startDate);
        challengeEntity.setEndDate(endDate);
        challengeEntity.setId(challenge.getId());

        Date createDate =  new Date(System.currentTimeMillis() + 1);
        Announcement announcement = new Announcement(0,
                challenge.getId(),
                challenge.getTitle(),
                1L,
                "announcement comment",
                1L,
                createDate.toString(),
                null);

        GamificationActionsHistory announcementEntity = new GamificationActionsHistory();
        announcementEntity.setEarnerId(announcement.getAssignee().toString());
        announcementEntity.setEarnerType(IdentityType.USER);
        announcementEntity.setCreator(announcement.getCreator());
        announcementEntity.setRuleId(challenge.getId());
        announcementEntity.setComment(announcement.getComment());
        announcementEntity.setCreatedDate(createDate);

        GamificationActionsHistory newAnnouncementEntity = new GamificationActionsHistory();
        newAnnouncementEntity.setEarnerId(announcement.getAssignee().toString());
        newAnnouncementEntity.setCreator(announcementEntity.getCreator());
        newAnnouncementEntity.setRuleId(challenge.getId());
        newAnnouncementEntity.setComment(announcementEntity.getComment());
        newAnnouncementEntity.setCreatedDate(createDate);
        newAnnouncementEntity.setId(1l);

        Announcement announcementFromEntity = new Announcement();
        announcementFromEntity.setAssignee(Long.valueOf(newAnnouncementEntity.getEarnerId()));
        announcementFromEntity.setCreator(newAnnouncementEntity.getCreator());
        announcementFromEntity.setComment(newAnnouncementEntity.getComment());
        announcementFromEntity.setCreatedDate(createDate.toString());
        announcementFromEntity.setChallengeId(newAnnouncementEntity.getRuleId());
        announcementFromEntity.setId(newAnnouncementEntity.getId());


        Identity identity = mock(Identity.class);
        when(ruleDAO.find(anyLong())).thenReturn(challengeEntity);
        when(announcementDAO.create(anyObject())).thenReturn(newAnnouncementEntity);
        UTILS.when(() -> Utils.getIdentityByTypeAndId(any(), any()))
             .thenReturn(identity);
        ENTITY_MAPPER.when(() -> EntityMapper.toEntity(challenge)).thenReturn(challengeEntity);
        ENTITY_MAPPER.when(() -> EntityMapper.fromEntity(newAnnouncementEntity)).thenReturn(announcementFromEntity);
        ENTITY_MAPPER.when(() -> EntityMapper.toEntity(any(Announcement.class), any(RuleEntity.class))).thenReturn(announcementEntity);
        DomainDTO domainDTO = new DomainDTO();
        domainDTO.setTitle("gamification");
        DomainEntity domainEntity = new DomainEntity();
        domainEntity.setTitle("gamification");
        UTILS.when(() -> Utils.getEnabledDomainByTitle(any())).thenReturn(domainDTO);
        DOMAIN_MAPPER.when(() -> DomainMapper.domainDTOToDomainEntity(domainDTO)).thenReturn(domainEntity);

        Announcement createdAnnouncement = announcementStorage.saveAnnouncement(announcement);

        // Then
        assertNotNull(createdAnnouncement);
        assertEquals(createdAnnouncement.getId(), 1l);
        announcementFromEntity.setActivityId(createdAnnouncement.getActivityId());
        assertEquals(announcementFromEntity, createdAnnouncement);
    }

    @Test
    public void testGetAnnouncementById(){
        Date startDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis() + 1);
        RuleEntity challengeEntity = new RuleEntity();
        challengeEntity.setDescription("challenge description");
        challengeEntity.setTitle("new challenge");
        challengeEntity.setStartDate(startDate);
        challengeEntity.setEndDate(endDate);
        challengeEntity.setId(1l);

        Date createDate = new Date(System.currentTimeMillis());
        GamificationActionsHistory announcementEntity = new GamificationActionsHistory();
        announcementEntity.setId(1l);
        announcementEntity.setEarnerId("1");
        announcementEntity.setEarnerId("1");
        announcementEntity.setCreator(1L);
        announcementEntity.setRuleId(challengeEntity.getId());
        announcementEntity.setComment("announcement comment");
        announcementEntity.setCreatedDate(createDate);

        Announcement announcementFromEntity = new Announcement();
        announcementFromEntity.setId(announcementEntity.getId());
        announcementFromEntity.setAssignee(Long.valueOf(announcementEntity.getEarnerId()));
        announcementFromEntity.setCreator(announcementEntity.getCreator());
        announcementFromEntity.setComment(announcementEntity.getComment());
        announcementFromEntity.setCreatedDate(createDate.toString());
        announcementFromEntity.setChallengeId(announcementEntity.getRuleId());
        announcementFromEntity.setId(announcementEntity.getId());


        when(announcementDAO.find(anyLong())).thenReturn(announcementEntity);
        ENTITY_MAPPER.when(() -> EntityMapper.fromEntity(announcementEntity)).thenReturn(announcementFromEntity);

        Announcement createdAnnouncement = announcementStorage.getAnnouncementById(1l);

        // Then
        assertNotNull(createdAnnouncement);
        assertEquals(createdAnnouncement.getId(), 1l);
        assertEquals(announcementFromEntity, createdAnnouncement);
    }
    @Test
    public void testGetAnnouncementByChallengeId(){
        Date startDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis() + 1);
        RuleEntity challengeEntity = new RuleEntity();
        challengeEntity.setDescription("challenge description");
        challengeEntity.setTitle("new challenge");
        challengeEntity.setStartDate(startDate);
        challengeEntity.setEndDate(endDate);
        challengeEntity.setId(1l);

        Date createDate = new Date(System.currentTimeMillis());

        GamificationActionsHistory announcementEntity1 = new GamificationActionsHistory();
        announcementEntity1.setId(1l);
        announcementEntity1.setEarnerId("1");
        announcementEntity1.setCreator(1L);
        announcementEntity1.setRuleId(challengeEntity.getId());
        announcementEntity1.setComment("announcement comment 1");
        announcementEntity1.setCreatedDate(createDate);

        Announcement announcementFromEntity1 = new Announcement();
        announcementFromEntity1.setId(announcementEntity1.getId());
        announcementFromEntity1.setAssignee(announcementEntity1.getId());
        announcementFromEntity1.setCreator(announcementEntity1.getCreator());
        announcementFromEntity1.setComment(announcementEntity1.getComment());
        announcementFromEntity1.setCreatedDate(createDate.toString());
        announcementFromEntity1.setChallengeId(announcementEntity1.getRuleId());
        announcementFromEntity1.setId(announcementEntity1.getId());

        GamificationActionsHistory announcementEntity2 = new GamificationActionsHistory();
        announcementEntity2.setId(1l);
        announcementEntity1.setId(1l);
        announcementEntity2.setCreator(1L);
        announcementEntity2.setEarnerId(String.valueOf(1L));
        announcementEntity1.setRuleId(challengeEntity.getId());
        announcementEntity2.setComment("announcement comment 2");
        announcementEntity2.setCreatedDate(createDate);

        Announcement announcementFromEntity2 = new Announcement();
        announcementFromEntity2.setId(announcementEntity2.getId());
        announcementFromEntity2.setAssignee(Long.valueOf(announcementEntity2.getEarnerId()));
        announcementFromEntity2.setCreator(announcementEntity2.getCreator());
        announcementFromEntity2.setComment(announcementEntity2.getComment());
        announcementFromEntity2.setCreatedDate(createDate.toString());
        announcementFromEntity2.setChallengeId(announcementEntity2.getRuleId());
        announcementFromEntity2.setId(announcementEntity2.getId());

        GamificationActionsHistory announcementEntity3 = new GamificationActionsHistory();
        announcementEntity3.setId(1l);
        announcementEntity3.setEarnerId("1");
        announcementEntity3.setCreator(1L);
        announcementEntity3.setRuleId(challengeEntity.getId());
        announcementEntity3.setComment("announcement comment 3");
        announcementEntity3.setCreatedDate(createDate);

        Announcement announcementFromEntity3 = new Announcement();
        announcementFromEntity3.setId(announcementEntity3.getId());
        announcementFromEntity3.setAssignee(Long.valueOf(announcementEntity3.getEarnerId()));
        announcementFromEntity3.setCreator(announcementEntity3.getCreator());
        announcementFromEntity3.setComment(announcementEntity3.getComment());
        announcementFromEntity3.setCreatedDate(createDate.toString());
        announcementFromEntity3.setChallengeId(announcementEntity3.getRuleId());
        announcementFromEntity3.setId(announcementEntity3.getId());

        List<Announcement> announcementList = new ArrayList<>();
        announcementList.add(announcementFromEntity1);
        announcementList.add(announcementFromEntity2);
        announcementList.add(announcementFromEntity3);

        List<GamificationActionsHistory> announcementEntities = new ArrayList<>();
        announcementEntities.add(announcementEntity1);
        announcementEntities.add(announcementEntity2);
        announcementEntities.add(announcementEntity3);

        when(announcementDAO.findAllAnnouncementByChallenge(anyLong(), anyInt(), anyInt(), any(), any())).thenReturn(announcementEntities);
        ENTITY_MAPPER.when(() -> EntityMapper.fromAnnouncementEntities(announcementEntities)).thenReturn(announcementList);

        List<Announcement> announcementListByChallenge = announcementStorage.findAllAnnouncementByChallenge(challengeEntity.getId(), 0, 10, PeriodType.ALL, "ALL");

        // Then
        assertNotNull(announcementListByChallenge);
        assertEquals(announcementListByChallenge.size(),3);
    }
}


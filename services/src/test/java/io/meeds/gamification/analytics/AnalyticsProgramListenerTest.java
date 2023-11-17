/*
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.analytics;

import static io.meeds.gamification.service.ProgramService.PROGRAM_CREATED_LISTENER;
import static io.meeds.gamification.service.ProgramService.PROGRAM_DELETED_LISTENER;
import static io.meeds.gamification.service.ProgramService.PROGRAM_DISABLED_LISTENER;
import static io.meeds.gamification.service.ProgramService.PROGRAM_ENABLED_LISTENER;
import static io.meeds.gamification.service.ProgramService.PROGRAM_UPDATED_LISTENER;
import static io.meeds.gamification.utils.Utils.STATISTICS_CREATE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_DELETE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_DISABLE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_ENABLE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_BUDGET_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_COVER_FILEID_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_ID_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_OWNERS_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_SUBMODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_TITLE_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_TYPE_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_UPDATE_PROGRAM_OPERATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.analytics.utils.AnalyticsUtils;
import org.exoplatform.services.listener.Event;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.ProgramDTO;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class AnalyticsProgramListenerTest {

  private static final long                   AUDIENCE_ID         = 1L;

  private static final String                 USER_NAME           = "userName";

  private static final long                   USER_IDENTITY_ID    = 2l;

  private static final int                    SCORE               = 1200;

  private static final String                 PROGRAM_DESCRIPTION = "programDescription";

  private static final String                 PROGRAM_TITLE       = "programTitle";

  private static MockedStatic<AnalyticsUtils> ANALYTICS_UTILS;

  @Mock
  private IdentityManager                     identityManager;

  @Mock
  private SpaceService                        spaceService;

  @Mock
  private Event<ProgramDTO, String>           event;

  @Mock
  private Identity                            userIdentity;

  @Mock
  private Space                               space;

  private ProgramDTO                          program;

  private AnalyticsProgramListener            programListener;

  @BeforeClass
  public static void initClassContext() {
    ANALYTICS_UTILS = mockStatic(AnalyticsUtils.class);
  }

  @AfterClass
  public static void endClassContext() {
    ANALYTICS_UTILS.close();
  }

  @Before
  public void setup() {
    program = newProgram();
    ANALYTICS_UTILS.reset();
    ANALYTICS_UTILS.when(() -> AnalyticsUtils.addSpaceStatistics(any(), any())).thenCallRealMethod();
    when(event.getSource()).thenReturn(program);
    when(event.getData()).thenReturn(USER_NAME);
    programListener = new AnalyticsProgramListener(identityManager, spaceService);
  }

  @Test
  public void testThrowExceptionWhenUnhandledEvent() {
    when(event.getEventName()).thenReturn("UNHANDLED");
    assertThrows(IllegalArgumentException.class, () -> programListener.onEvent(event));
  }

  @Test
  public void testHandleCreateRuleEvent() throws Exception {
    assertCollectedOperation(PROGRAM_CREATED_LISTENER, STATISTICS_CREATE_PROGRAM_OPERATION);
  }

  @Test
  public void testHandleUpdateRuleEvent() throws Exception {
    assertCollectedOperation(PROGRAM_UPDATED_LISTENER, STATISTICS_UPDATE_PROGRAM_OPERATION);
  }

  @Test
  public void testHandleDeleteRuleEvent() throws Exception {
    assertCollectedOperation(PROGRAM_DELETED_LISTENER, STATISTICS_DELETE_PROGRAM_OPERATION);
  }

  @Test
  public void testHandleEnableRuleEvent() throws Exception {
    assertCollectedOperation(PROGRAM_ENABLED_LISTENER, STATISTICS_ENABLE_PROGRAM_OPERATION);
  }

  @Test
  public void testHandleDisableRuleEvent() throws Exception {
    assertCollectedOperation(PROGRAM_DISABLED_LISTENER, STATISTICS_DISABLE_PROGRAM_OPERATION);
  }

  private void assertCollectedOperation(String eventName, String expectedOperation) throws Exception {
    when(event.getEventName()).thenReturn(eventName);
    String identityId = String.valueOf(USER_IDENTITY_ID);
    when(identityManager.getOrCreateUserIdentity(USER_NAME)).thenReturn(userIdentity);
    when(userIdentity.getId()).thenReturn(identityId);
    String spaceId = String.valueOf(AUDIENCE_ID);
    when(spaceService.getSpaceById(spaceId)).thenReturn(space);
    when(space.getId()).thenReturn(spaceId);

    programListener.onEvent(event);

    ANALYTICS_UTILS.verify(() -> AnalyticsUtils.addStatisticData(argThat(statisticData -> {
      assertEquals(STATISTICS_GAMIFICATION_MODULE, statisticData.getModule());
      assertEquals(STATISTICS_PROGRAM_SUBMODULE, statisticData.getSubModule());
      assertEquals(expectedOperation, statisticData.getOperation());
      assertEquals(USER_IDENTITY_ID, statisticData.getUserId());
      assertEquals(AUDIENCE_ID, statisticData.getSpaceId());

      assertEquals(String.valueOf(program.getId()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_ID_PARAM)));
      assertEquals(String.valueOf(program.getTitle()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_TITLE_PARAM)));
      assertEquals(String.valueOf(program.getBudget()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_BUDGET_PARAM)));
      assertEquals(String.valueOf(program.getType()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_TYPE_PARAM)));
      assertEquals(String.valueOf(program.getCoverFileId()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_COVER_FILEID_PARAM)));
      assertEquals(String.valueOf(program.getOwnerIds()),
                   String.valueOf(statisticData.getListParameters().get(STATISTICS_PROGRAM_OWNERS_PARAM)));
      return true;
    })), times(1));
  }

  private ProgramDTO newProgram() {
    ProgramDTO program = new ProgramDTO();
    program.setTitle(PROGRAM_TITLE);
    program.setDescription(PROGRAM_DESCRIPTION);
    program.setBudget(SCORE);
    program.setDeleted(false);
    program.setEnabled(true);
    program.setCoverFileId(2l);
    program.setType(EntityType.AUTOMATIC.name());
    program.setSpaceId(AUDIENCE_ID);
    HashSet<Long> owners = new HashSet<Long>();
    owners.add(1L);
    program.setOwnerIds(owners);
    return program;
  }

}

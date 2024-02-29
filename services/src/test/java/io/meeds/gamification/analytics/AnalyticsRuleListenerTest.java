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

import static io.meeds.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_DELETE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.STATISTICS_CREATE_RULE_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_DELETE_RULE_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_BUDGET_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_COVER_FILEID_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_ID_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_OWNERS_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_TITLE_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_TYPE_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_RULE_DESCRIPTION_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_RULE_ID_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_RULE_SCORE_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_RULE_SUBMODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_RULE_TITLE_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_RULE_TYPE_PARAM;
import static io.meeds.gamification.utils.Utils.STATISTICS_UPDATE_RULE_OPERATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import io.meeds.gamification.model.EventDTO;
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
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.EventService;
import io.meeds.gamification.service.RuleService;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class AnalyticsRuleListenerTest {

  private static final long                   AUDIENCE_ID           = 1L;

  private static final String                 USER_NAME             = "userName";

  private static final long                   USER_IDENTITY_ID      = 2l;

  private static final long                   CHALLENGE_ID          = 3l;

  private static final int                    SCORE                 = 1200;

  private static final String                 PROGRAM_DESCRIPTION   = "programDescription";

  private static final String                 PROGRAM_TITLE         = "programTitle";

  private static final String                 EVENT_NAME            = "eventName";

  private static final String                 CHALLENGE_DESCRIPTION = "ChallengeDescription";

  private static final String                 CHALLENGE_TITLE       = "challengeTitle";

  private static MockedStatic<AnalyticsUtils> ANALYTICS_UTILS;

  @Mock
  private IdentityManager                     identityManager;

  @Mock
  private SpaceService                        spaceService;

  @Mock
  private RuleService                         ruleService;

  @Mock
  private EventService                        eventService;

  @Mock
  private Event<Object, String>               event;

  @Mock
  private Identity                            userIdentity;

  @Mock
  private Space                               space;

  private RuleDTO                             ruleDTO;

  private AnalyticsRuleListener               ruleListener;

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
    ruleDTO = newRule();
    ANALYTICS_UTILS.reset();
    ANALYTICS_UTILS.when(() -> AnalyticsUtils.addSpaceStatistics(any(), any())).thenCallRealMethod();
    when(event.getSource()).thenReturn(ruleDTO);
    when(event.getData()).thenReturn(USER_NAME);
    ruleListener = new AnalyticsRuleListener(identityManager,
                                             spaceService,
                                             ruleService,
                                             eventService);
  }

  @Test
  public void testThrowExceptionWhenUnhandledEvent() {
    when(event.getEventName()).thenReturn("UNHANDLED");
    assertThrows(IllegalArgumentException.class, () -> ruleListener.onEvent(event));
  }

  @Test
  public void testHandleCreateRuleEvent() throws Exception {
    assertCollectedOperation(POST_CREATE_RULE_EVENT, STATISTICS_CREATE_RULE_OPERATION);
  }

  @Test
  public void testHandleUpdateRuleEvent() throws Exception {
    assertCollectedOperation(POST_UPDATE_RULE_EVENT, STATISTICS_UPDATE_RULE_OPERATION);
  }

  @Test
  public void testHandleDeleteRuleEvent() throws Exception {
    assertCollectedOperation(POST_DELETE_RULE_EVENT, STATISTICS_DELETE_RULE_OPERATION);
  }

  private void assertCollectedOperation(String eventName, String expectedOperation) throws Exception {
    when(event.getEventName()).thenReturn(eventName);
    String identityId = String.valueOf(USER_IDENTITY_ID);
    when(identityManager.getOrCreateUserIdentity(USER_NAME)).thenReturn(userIdentity);
    when(userIdentity.getId()).thenReturn(identityId);
    String spaceId = String.valueOf(AUDIENCE_ID);
    when(spaceService.getSpaceById(spaceId)).thenReturn(space);
    when(space.getId()).thenReturn(spaceId);

    ruleListener.onEvent(event);

    ANALYTICS_UTILS.verify(() -> AnalyticsUtils.addStatisticData(argThat(statisticData -> {
      assertEquals(STATISTICS_GAMIFICATION_MODULE, statisticData.getModule());
      assertEquals(STATISTICS_RULE_SUBMODULE, statisticData.getSubModule());
      assertEquals(expectedOperation, statisticData.getOperation());
      assertEquals(USER_IDENTITY_ID, statisticData.getUserId());
      assertEquals(AUDIENCE_ID, statisticData.getSpaceId());

      assertEquals(String.valueOf(ruleDTO.getId()), String.valueOf(statisticData.getParameters().get(STATISTICS_RULE_ID_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getTitle()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_RULE_TITLE_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getDescription()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_RULE_DESCRIPTION_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getScore()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_RULE_SCORE_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getType()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_RULE_TYPE_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getProgram().getId()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_ID_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getProgram().getTitle()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_TITLE_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getProgram().getBudget()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_BUDGET_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getProgram().getType()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_TYPE_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getProgram().getCoverFileId()),
                   String.valueOf(statisticData.getParameters().get(STATISTICS_PROGRAM_COVER_FILEID_PARAM)));
      assertEquals(String.valueOf(ruleDTO.getProgram().getOwnerIds()),
                   String.valueOf(statisticData.getListParameters().get(STATISTICS_PROGRAM_OWNERS_PARAM)));
      return true;
    })), times(1));
  }

  private RuleDTO newRule() {
    RuleDTO ruleDTO = new RuleDTO();
    ruleDTO.setId(CHALLENGE_ID);
    ruleDTO.setScore(SCORE);
    ruleDTO.setTitle(CHALLENGE_TITLE);
    ruleDTO.setDescription(CHALLENGE_DESCRIPTION);
    ruleDTO.setEnabled(true);
    ruleDTO.setDeleted(false);
    ruleDTO.setEvent(newEvent());
    ruleDTO.setProgram(newProgram());
    ruleDTO.setType(EntityType.MANUAL);
    return ruleDTO;
  }

  private ProgramDTO newProgram() {
    ProgramDTO program = new ProgramDTO();
    program.setTitle(PROGRAM_TITLE);
    program.setDescription(PROGRAM_DESCRIPTION);
    program.setDeleted(false);
    program.setEnabled(true);
    program.setType(EntityType.AUTOMATIC.name());
    program.setSpaceId(AUDIENCE_ID);
    HashSet<Long> owners = new HashSet<Long>();
    owners.add(1L);
    program.setOwnerIds(owners);
    return program;
  }

  private EventDTO newEvent() {
    EventDTO event = new EventDTO();
    event.setTitle(EVENT_NAME);
    event.setTrigger(EVENT_NAME);
    event.setType("eventType");
    return event;
  }

}

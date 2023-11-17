/**
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
package io.meeds.gamification.plugin;

import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_TYPE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.ActivityManagerImpl;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.storage.api.ActivityStorage;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;

@RunWith(MockitoJUnitRunner.class)
public class RuleActivityTypePluginTest {

  @Mock
  private ActivityStorage     storage;

  @Mock
  private IdentityManager     identityManager;

  @Mock
  private UserACL             userAclService;

  @Mock
  private ProgramService      programService;

  @Mock
  private RuleService         ruleService;

  @Mock
  private SpaceService        spaceService;

  @Mock
  private RelationshipManager relationshipManager;

  @Mock
  private RuleDTO             rule;

  private long                ruleId = 25l;

  private long                activityId = 35l;

  @Test
  public void testActivityTypePlugin() {
    // prepare activity
    ExoSocialActivity activity = mock(ExoSocialActivity.class);
    when(activity.isComment()).thenReturn(false);
    when(activity.getType()).thenReturn(RULE_ACTIVITY_TYPE);
    when(activity.getPosterId()).thenReturn("1");
    when(activity.getMetadataObjectId()).thenReturn(String.valueOf(ruleId));
    when(activity.getId()).thenReturn(String.valueOf(activityId));

    // prepare viewer
    org.exoplatform.services.security.Identity owner = mock(org.exoplatform.services.security.Identity.class);
    when(owner.getUserId()).thenReturn("demo");
    when(identityManager.getOrCreateUserIdentity("demo")).thenReturn(new Identity("1"));
    org.exoplatform.services.security.Identity viewer = mock(org.exoplatform.services.security.Identity.class);
    when(viewer.getUserId()).thenReturn("mary");
    when(identityManager.getOrCreateUserIdentity("mary")).thenReturn(new Identity("2"));
    when(ruleService.findRuleById(ruleId)).thenReturn(rule);
    when(rule.getProgramId()).thenReturn(2l);
    when(rule.getActivityId()).thenReturn(activityId);

    // no configuration
    // by default: edit activity/comment are all enabled
    ActivityManager activityManager = new ActivityManagerImpl(storage,
                                                              identityManager,
                                                              spaceService,
                                                              relationshipManager,
                                                              userAclService,
                                                              null);
    assertTrue(activityManager.isActivityViewable(activity, owner));
    assertFalse(activityManager.isActivityViewable(activity, viewer));

    InitParams initParams = new InitParams();
    ValueParam valueParam = new ValueParam();
    valueParam.setName("type");
    valueParam.setValue(RULE_ACTIVITY_TYPE);
    initParams.addParameter(valueParam);

    valueParam = new ValueParam();
    valueParam.setName("enableNotification");
    valueParam.setValue("false");
    initParams.addParameter(valueParam);

    activityManager.addActivityTypePlugin(new RuleActivityTypePlugin(programService, ruleService, initParams));

    when(programService.canViewProgram(rule.getProgramId(), owner.getUserId())).thenReturn(true);
    assertTrue(activityManager.isActivityViewable(activity, owner));
    assertFalse(activityManager.isActivityViewable(activity, viewer));

    when(programService.canViewProgram(rule.getProgramId(), viewer.getUserId())).thenReturn(true);
    assertTrue(activityManager.isActivityViewable(activity, viewer));
  }

}

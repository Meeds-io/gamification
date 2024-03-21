/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
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
 *
 */
package io.meeds.gamification.plugin;

import static io.meeds.gamification.utils.Utils.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.meeds.gamification.constant.RealizationStatus;
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

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AnnouncementActivityTypePluginTest {

  @Mock
  private ActivityStorage     storage;

  @Mock
  private IdentityManager     identityManager;

  @Mock
  private UserACL             userAclService;

  @Mock
  private SpaceService        spaceService;

  @Mock
  private RelationshipManager relationshipManager;

  @Test
  public void testAnnouncementActivityTypePlugin() {
    // prepare activity
    ExoSocialActivity activity = mock(ExoSocialActivity.class);
    when(activity.getType()).thenReturn(ANNOUNCEMENT_COMMENT_TYPE);
    when(activity.getPosterId()).thenReturn("1");
    Map<String, String> templateParams = new HashMap<>();
    templateParams.put(REALIZATION_STATUS_TEMPLATE_PARAM, String.valueOf(RealizationStatus.PENDING));
    when(activity.getTemplateParams()).thenReturn(templateParams);

    // prepare viewer
    org.exoplatform.services.security.Identity owner = mock(org.exoplatform.services.security.Identity.class);
    when(owner.getUserId()).thenReturn("demo");
    when(identityManager.getOrCreateUserIdentity("demo")).thenReturn(new Identity("1"));
    org.exoplatform.services.security.Identity viewer = mock(org.exoplatform.services.security.Identity.class);
    when(viewer.getUserId()).thenReturn("mary");
    when(identityManager.getOrCreateUserIdentity("mary")).thenReturn(new Identity("2"));

    // no configuration
    // by default: edit comment are all enabled
    ActivityManager activityManager = new ActivityManagerImpl(storage,
                                                              identityManager,
                                                              spaceService,
                                                              relationshipManager,
                                                              userAclService,
                                                              null);
    assertTrue(activityManager.isActivityEditable(activity, owner));
    assertFalse(activityManager.isActivityEditable(activity, viewer));

    InitParams initParams = new InitParams();
    ValueParam valueParam = new ValueParam();
    valueParam.setName("type");
    valueParam.setValue(ANNOUNCEMENT_COMMENT_TYPE);
    initParams.addParameter(valueParam);

    valueParam = new ValueParam();
    valueParam.setName("enableNotification");
    valueParam.setValue("false");
    initParams.addParameter(valueParam);

    activityManager.addActivityTypePlugin(new AnnouncementActivityTypePlugin(initParams, identityManager));

    assertTrue(activityManager.isActivityEditable(activity, owner));
    assertFalse(activityManager.isActivityEditable(activity, viewer));

    // When contribution accepted
    templateParams = new HashMap<>();
    templateParams.put(REALIZATION_STATUS_TEMPLATE_PARAM, String.valueOf(RealizationStatus.ACCEPTED));
    when(activity.getTemplateParams()).thenReturn(templateParams);

    assertFalse(activityManager.isActivityEditable(activity, owner));
    assertFalse(activityManager.isActivityEditable(activity, viewer));

    // When contribution rejected
    templateParams = new HashMap<>();
    templateParams.put(REALIZATION_STATUS_TEMPLATE_PARAM, String.valueOf(RealizationStatus.REJECTED));
    when(activity.getTemplateParams()).thenReturn(templateParams);

    assertFalse(activityManager.isActivityEditable(activity, owner));
    assertFalse(activityManager.isActivityEditable(activity, viewer));
  }

}

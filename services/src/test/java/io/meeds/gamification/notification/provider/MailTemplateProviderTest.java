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
package io.meeds.gamification.notification.provider;

import org.exoplatform.commons.api.notification.channel.AbstractChannel;
import org.exoplatform.commons.api.notification.channel.ChannelManager;
import org.exoplatform.commons.api.notification.model.ChannelKey;
import org.exoplatform.commons.api.notification.model.PluginKey;
import org.exoplatform.commons.notification.channel.MailChannel;
import org.exoplatform.social.notification.AbstractCoreTest;

import static io.meeds.gamification.utils.Utils.*;

public class MailTemplateProviderTest extends AbstractCoreTest {

  private ChannelManager manager;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    manager = getService(ChannelManager.class);
  }

  public void testMailTemplateProvider() {
    AbstractChannel channel = manager.getChannel(ChannelKey.key(MailChannel.ID));
    assertNotNull(channel);

    String actual = channel.getTemplateFilePath(PluginKey.key(RULE_PUBLISHED_NOTIFICATION_ID));
    String expected = "classpath:/notification/gamification/RulePublishedMailTemplate.gtmpl";
    assertEquals(expected, actual);

    actual = channel.getTemplateFilePath(PluginKey.key(RULE_ANNOUNCED_NOTIFICATION_ID));
    expected = "classpath:/notification/gamification/RuleAnnouncedMailTemplate.gtmpl";
    assertEquals(expected, actual);

    actual = channel.getTemplateFilePath(PluginKey.key(CONTRIBUTION_ACCEPTED_NOTIFICATION_ID));
    expected = "classpath:/notification/gamification/ContributionAcceptedMailTemplate.gtmpl";
    assertEquals(expected, actual);

    actual = channel.getTemplateFilePath(PluginKey.key(CONTRIBUTION_REJECTED_NOTIFICATION_ID));
    expected = "classpath:/notification/gamification/ContributionRejectedMailTemplate.gtmpl";
    assertEquals(expected, actual);
  }

  public void testMailTemplateBuilder() {
    AbstractChannel channel = manager.getChannel(ChannelKey.key(MailChannel.ID));
    assertNotNull(channel);
    assertTrue(channel.hasTemplateBuilder(PluginKey.key(RULE_PUBLISHED_NOTIFICATION_ID)));
    assertTrue(channel.hasTemplateBuilder(PluginKey.key(RULE_ANNOUNCED_NOTIFICATION_ID)));
    assertTrue(channel.hasTemplateBuilder(PluginKey.key(CONTRIBUTION_ACCEPTED_NOTIFICATION_ID)));
    assertTrue(channel.hasTemplateBuilder(PluginKey.key(CONTRIBUTION_REJECTED_NOTIFICATION_ID)));
  }

}

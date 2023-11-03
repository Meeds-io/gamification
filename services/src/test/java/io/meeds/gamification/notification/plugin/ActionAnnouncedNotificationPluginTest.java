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
package io.meeds.gamification.notification.plugin;

import static io.meeds.gamification.utils.Utils.RULE_ANNOUNCED_NOTIFICATION_ID;

import java.util.List;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.channel.template.AbstractTemplateBuilder;
import org.exoplatform.commons.api.notification.model.MessageInfo;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.model.PluginKey;
import org.exoplatform.commons.api.notification.plugin.BaseNotificationPlugin;
import org.exoplatform.commons.notification.impl.NotificationContextImpl;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.RulePublication;
import io.meeds.gamification.test.AbstractPluginTest;

public class ActionAnnouncedNotificationPluginTest extends AbstractPluginTest { // NOSONAR

  @Override
  public AbstractTemplateBuilder getTemplateBuilder() {
    return null;
  }

  @Override
  public BaseNotificationPlugin getPlugin() {
    return pluginService.getPlugin(PluginKey.key(RULE_ANNOUNCED_NOTIFICATION_ID));
  }

  public void testSimpleCase() throws Exception { // NOSONAR
    notificationService.clearAll();

    // STEP 1 post activity
    ProgramDTO program = new ProgramDTO();
    program.setTitle(GAMIFICATION_DOMAIN);
    program.setDescription(GAMIFICATION_DOMAIN);
    program.setCreatedBy(TEST_USER_EARNER);
    program.setLastModifiedBy(TEST_USER_EARNER);
    program.setDeleted(false);
    program.setEnabled(true);
    program.setSpaceId(1L);
    program = programService.createProgram(program);

    RulePublication rule = new RulePublication();
    rule.setScore(Integer.parseInt(TEST_SCORE));
    rule.setTitle(RULE_NAME);
    rule.setDescription("Description");
    rule.setEnabled(true);
    rule.setDeleted(false);
    rule.setProgram(program);
    rule.setType(EntityType.MANUAL);
    rule.setRecurrence(RecurrenceType.NONE);
    rule.setPublish(true);
    String message = "Test publication Message";
    rule.setMessage(message);
    RuleDTO createdRule = ruleService.createRule(rule, ADMIN_USER);
    assertTrue(createdRule.getActivityId() > 0); // NOSONAR

    Announcement announcement = new Announcement(0,
                                                 createdRule.getId(),
                                                 createdRule.getTitle(),
                                                 5L,
                                                 "announcement comment",
                                                 5L,
                                                 null,
                                                 null);
    announcementService.createAnnouncement(announcement, null, SIMPLE_USER);

    List<NotificationInfo> list = assertMadeWebNotifications(ADMIN_USER, 1);
    NotificationInfo ruleAnnouncementNotification = list.get(0);

    assertEquals(RULE_ANNOUNCED_NOTIFICATION_ID, ruleAnnouncementNotification.getKey().getId());
    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    ctx.setNotificationInfo(ruleAnnouncementNotification.setTo(SIMPLE_USER));
    MessageInfo info = buildMessageInfo(ctx);

    assertBody(info, rule.getTitle());
    assertEquals(1L, ruleAnnouncementNotification.getSpaceId());
  }

}

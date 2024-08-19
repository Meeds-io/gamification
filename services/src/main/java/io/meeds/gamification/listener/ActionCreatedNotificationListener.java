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
package io.meeds.gamification.listener;

import static io.meeds.gamification.utils.Utils.*;

import io.meeds.gamification.service.RuleService;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.PluginKey;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.notification.impl.NotificationContextImpl;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;

import io.meeds.gamification.model.RuleDTO;

@Asynchronous
public class ActionCreatedNotificationListener extends Listener<Long, String> {

  private final RuleService ruleService;

  public ActionCreatedNotificationListener(RuleService ruleService) {
    this.ruleService = ruleService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<Long, String> event) throws Exception {
    long ruleId = event.getSource();
    RuleDTO rule = ruleService.findRuleById(ruleId);
    if (rule == null) {
      return;
    }

    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    ctx.append(RULE_NOTIFICATION_PARAMETER, rule)
       .append(RULE_CREATOR_NOTIFICATION_PARAMETER, rule.getCreatedBy())
       .getNotificationExecutor()
       .with(ctx.makeCommand(PluginKey.key(NEW_ACTION_AVAILABLE_NOTIFICATION_ID)))
       .execute(ctx);
  }

}

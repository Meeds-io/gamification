/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.listener;

import static io.meeds.gamification.utils.Utils.RULE_ANNOUNCED_NOTIFICATION_ID;
import static io.meeds.gamification.utils.Utils.RULE_ID_NOTIFICATION_PARAM;
import static io.meeds.gamification.utils.Utils.RULE_PUBLISHED_NOTIFICATION_ID;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.model.PluginKey;
import org.exoplatform.commons.api.notification.model.WebNotificationFilter;
import org.exoplatform.commons.api.notification.service.WebNotificationService;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;

import io.meeds.gamification.model.RuleDTO;

@Asynchronous
public class RuleDeletedWebNotificationListener extends Listener<RuleDTO, String> {

  private WebNotificationService webNotificationService;

  public RuleDeletedWebNotificationListener(WebNotificationService webNotificationService) {
    this.webNotificationService = webNotificationService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<RuleDTO, String> event) throws Exception {
    RuleDTO rule = event.getSource();
    if (rule != null && rule.isDeleted()) {
      WebNotificationFilter filter = new WebNotificationFilter();
      filter.setPluginKeys(Arrays.asList(PluginKey.key(RULE_ANNOUNCED_NOTIFICATION_ID),
                                         PluginKey.key(RULE_PUBLISHED_NOTIFICATION_ID)));
      filter.setParameter(RULE_ID_NOTIFICATION_PARAM, String.valueOf(rule.getId()));
      List<NotificationInfo> webNotifications = webNotificationService.getNotificationInfos(filter, 0, 0);
      webNotifications.forEach(n -> webNotificationService.remove(n.getId()));
    }
  }

}

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

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.gamification.model.RuleDTO;

public class RuleDeletedActivityListener extends Listener<RuleDTO, String> {

  private static final Log LOG = ExoLogger.getLogger(RuleDeletedActivityListener.class);

  private ActivityManager  activityManager;

  public RuleDeletedActivityListener(ActivityManager activityManager) {
    this.activityManager = activityManager;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<RuleDTO, String> event) throws Exception {
    RuleDTO rule = event.getSource();
    try {
      if (rule != null && rule.getActivityId() > 0) {
        activityManager.deleteActivity(String.valueOf(rule.getActivityId()));
      }
    } catch (Exception e) {
      LOG.warn("Error hiding Rule activity: {}", rule, e);
    }
  }

}

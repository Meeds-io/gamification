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
package io.meeds.gamification.rest.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.meeds.gamification.model.Trigger;
import io.meeds.gamification.model.TriggerProperties;
import io.meeds.gamification.rest.model.TriggerRestEntity;
import io.meeds.gamification.service.TriggerService;
import org.apache.commons.collections.CollectionUtils;

public class TriggerBuilder {

  private TriggerBuilder() {
    // Class with static methods
  }

  public static TriggerRestEntity toRestEntity(TriggerService triggerService, Trigger trigger, List<String> expandFields) {
    if (trigger == null) {
      return null;
    }
    List<Long> disabledAccounts = null;
    List<String> permissions = null;
    if (CollectionUtils.isNotEmpty(expandFields)) {
      TriggerProperties triggerProperties = triggerService.getTriggerProperties(trigger.getTitle());
      if (expandFields.contains("disabledAccounts")) {
        disabledAccounts = triggerProperties.getDisabledAccounts();
      }
      if (expandFields.contains("permissions")) {
        permissions = triggerProperties.getPermissions();
      }
    }
    return new TriggerRestEntity(trigger.getTitle(),
                                 trigger.getType(),
                                 trigger.getCanceller(),
                                 trigger.isVerificationRequired(),
                                 CollectionUtils.isNotEmpty(disabledAccounts) ? disabledAccounts : Collections.emptyList(),
                                 CollectionUtils.isNotEmpty(permissions) ? permissions : Collections.emptyList());
  }

  public static List<TriggerRestEntity> toRestEntities(TriggerService triggerService,
                                                       Collection<Trigger> triggers,
                                                       List<String> expandFields) {
    return triggers.stream().map(trigger -> toRestEntity(triggerService, trigger, expandFields)).toList();
  }
}

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
package io.meeds.gamification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.meeds.gamification.model.TriggerProperties;
import io.meeds.gamification.service.TriggerService;
import io.meeds.gamification.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;

import java.io.IOException;
import java.util.ArrayList;

public class TriggerServiceImpl implements TriggerService {

  private static final Context CONNECTOR_TRIGGER_CONTEXT = Context.GLOBAL.id("ConnectorTrigger");

  private static final Scope   TRIGGER_SETTING_SCOPE     = Scope.APPLICATION.id("TriggerSettings");

  private final SettingService settingService;

  public TriggerServiceImpl(SettingService settingService) {
    this.settingService = settingService;
  }

  @Override
  public TriggerProperties getTriggerProperties(String triggerName) {
    SettingValue<?> settingValue = this.settingService.get(CONNECTOR_TRIGGER_CONTEXT, TRIGGER_SETTING_SCOPE, triggerName);
    if (settingValue == null || settingValue.getValue() == null || StringUtils.isBlank(settingValue.getValue().toString())) {
      return new TriggerProperties(new ArrayList<>(), new ArrayList<>());
    }
    return fromString(settingValue.getValue().toString());
  }

  @Override
  public void setTriggerEnabledForAccount(String triggerName,
                                          long accountId,
                                          boolean enabled,
                                          String currentUser) throws IllegalAccessException {
    if (!Utils.isRewardingManager(currentUser)) {
      throw new IllegalAccessException("The user is not authorized to enable/disable trigger");
    }
    TriggerProperties triggerProperties = getTriggerProperties(triggerName);
    if (enabled) {
      triggerProperties.getDisabledAccounts().remove(accountId);
    } else {
      triggerProperties.getDisabledAccounts().add(accountId);
    }
    this.settingService.set(CONNECTOR_TRIGGER_CONTEXT,
                            TRIGGER_SETTING_SCOPE,
                            triggerName,
                            SettingValue.create(toString(triggerProperties)));
  }

  @Override
  public boolean isTriggerEnabledForAccount(String triggerName, long accountId) {
    TriggerProperties triggerProperties = getTriggerProperties(triggerName);
    return !triggerProperties.getDisabledAccounts().contains(accountId);
  }

  private static TriggerProperties fromString(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(value, TriggerProperties.class);
    } catch (IOException e) {
      throw new IllegalStateException("Error creating object from string : " + value, e);
    }
  }

  private static String toString(TriggerProperties triggerProperties) {
    if (triggerProperties == null) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(triggerProperties);
    } catch (IOException e) {
      throw new IllegalStateException("Error creating string from object : " + triggerProperties, e);
    }
  }
}

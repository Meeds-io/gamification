/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
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
 */
package org.exoplatform.addons.gamification.model;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

public class RuleConfig extends BaseComponentPlugin {

  private String  title;

  private String  description;

  private String  event;

  private int     score;

  private String  zone;

  private boolean enable;

  public RuleConfig(InitParams params) throws Exception {

    ValueParam titleParam = params.getValueParam("rule-title");

    if (titleParam == null) {
      throw new ConfigurationException("No 'rule-title' parameter found");
    } else {
      title = titleParam.getValue();
    }

    ValueParam descriptionParam = params.getValueParam("rule-description");

    if (descriptionParam != null) {
      description = descriptionParam.getValue();
    }

    ValueParam eventParam = params.getValueParam("rule-event");

    if (eventParam != null) {
      event = eventParam.getValue();
    } else {
      event = title;
    }

    ValueParam scoreParam = params.getValueParam("rule-score");

    if (scoreParam != null) {
      score = Integer.parseInt(scoreParam.getValue());
    }

    ValueParam zoneParam = params.getValueParam("rule-zone");

    if (zoneParam != null) {
      zone = zoneParam.getValue();
    }

    ValueParam enableParam = params.getValueParam("rule-enable");

    if (enableParam != null) {
      enable = Boolean.parseBoolean(enableParam.getValue());
    }

  }

  public String getTitle() {
    return title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public int getScore() {
    return score;
  }

  public String getZone() {
    return zone;
  }

  public boolean isEnable() {
    return enable;
  }

  public String getEvent() {
    return event;
  }

}

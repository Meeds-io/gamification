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

public class BadgeConfig extends BaseComponentPlugin {

  private String  title;

  private String  description;

  private String  domain;

  private String  icon;

  private int     neededScore;

  private boolean enable;

  public BadgeConfig(InitParams params) throws Exception {
    ValueParam titleParam = params.getValueParam("badge-title");

    if (titleParam == null) {
      throw new ConfigurationException("No 'badge-title' parameter found");
    } else {
      title = titleParam.getValue();
    }

    ValueParam descriptionParam = params.getValueParam("badge-description");

    if (descriptionParam != null) {
      description = descriptionParam.getValue();
    }

    ValueParam domainParam = params.getValueParam("badge-domain");

    if (domainParam != null) {
      domain = domainParam.getValue();
    }

    ValueParam iconParam = params.getValueParam("badge-icon");

    if (iconParam != null) {
      icon = iconParam.getValue();
    }

    ValueParam neededScoreParam = params.getValueParam("badge-neededScore");

    if (neededScoreParam != null) {
      neededScore = Integer.parseInt(neededScoreParam.getValue());
    }

    ValueParam enableParam = params.getValueParam("badge-enable");

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

  public String getDomain() {
    return domain;
  }

  public String getIcon() {
    return icon;
  }

  public int getNeededScore() {
    return neededScore;
  }

  public boolean isEnable() {
    return enable;
  }
}

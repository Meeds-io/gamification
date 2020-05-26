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
package org.exoplatform.addons.gamification.service.setting.zone.model;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

public class ZoneConfig extends BaseComponentPlugin {

    private String zoneName;

    private String zoneDescription;

    private String zonePriority;

    public ZoneConfig(InitParams params) throws Exception {

        ValueParam nameParam = params.getValueParam("zone-name");

        if (nameParam == null) {
            throw new ConfigurationException("No 'zone-name' parameter found");
        } else {
            zoneName = nameParam.getValue();
        }


        ValueParam descriptionParam = params.getValueParam("zone-description");

        if (descriptionParam != null) {
            zoneDescription = descriptionParam.getValue();
        }

        ValueParam priorityParam = params.getValueParam("zone-priority");

        if (priorityParam != null) {
            zonePriority = priorityParam.getValue();
        }

    }

    public String getZoneName() {
        return zoneName;
    }

    public String getZoneDescription() {
        return zoneDescription;
    }

    public String getZonePriority() {
        return zonePriority;
    }
}

package org.exoplatform.gamification.service.setting.zone.model;

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

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
package org.exoplatform.addons.gamification.service.setting.zone.impl;

import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.setting.zone.ZoneRegistry;
import org.exoplatform.addons.gamification.service.setting.zone.model.ZoneConfig;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ZoneRegistryImpl implements Startable, ZoneRegistry {

    private static final Log LOG = ExoLogger.getLogger(ZoneRegistryImpl.class);

    private static final String GAMIFICATION_PREFERENCES = "gamification:";

    private final Map<String, ZoneConfig> zoneMap;

    private DomainService domainService;

    public ZoneRegistryImpl(DomainService domainService) {
        this.zoneMap = new HashMap<String, ZoneConfig>();
        this.domainService= domainService;
    }

    @Override
    public void addPlugin(ZoneConfig zone) {
        zoneMap.put(zone.getZoneName(), zone);
    }

    @Override
    public boolean remove(ZoneConfig zone) {
        return false;
    }

    @Override
    public void start() {

        try {
            // Processing registered domains

            if(domainService.getAllDomains().size() == 0){
                for (ZoneConfig domain : zoneMap.values()) {
                    DomainDTO domainDTO = domainService.findDomainByTitle(domain.getZoneName());
                    if (domainDTO == null) {
                        store(domain);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error when processing Domains ", e);
        }


    }

    @Override
    public void stop() {

    }

    private void store(ZoneConfig zoneConfig) {

        DomainDTO domainDTO = new DomainDTO();
        domainDTO.setTitle(zoneConfig.getZoneName());
        domainDTO.setDescription(zoneConfig.getZoneDescription());
        domainDTO.setLastModifiedDate(new Date());
        domainDTO.setLastModifiedBy("Gamification");
        domainDTO.setEnabled(true);
        domainDTO.setDeleted(false);
        domainDTO.setCreatedBy("Gamification");
        domainDTO.setCreatedDate(new Date());
        try {
            domainDTO.setPriority(Integer.parseInt(zoneConfig.getZonePriority()));
        } catch (Exception e) {
            domainDTO.setPriority(10);
        }
        domainService.addDomain(domainDTO);
    }
}

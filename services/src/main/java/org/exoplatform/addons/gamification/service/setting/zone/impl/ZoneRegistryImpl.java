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

            for (ZoneConfig domain : zoneMap.values()) {
                DomainDTO domainDTO = domainService.findDomainByTitle(domain.getZoneName());
                if (domainDTO == null) {
                    store(domain);
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
        domainDTO.setLastModifiedDate(LocalDate.now().toString());
        domainDTO.setLastModifiedBy("Gamification");
        domainDTO.setCreatedBy("Gamification");
        domainDTO.setCreatedDate(LocalDate.now().toString());
        try {
            domainDTO.setPriority(Integer.parseInt(zoneConfig.getZonePriority()));
        } catch (Exception e) {
            domainDTO.setPriority(10);
        }
        domainService.addDomain(domainDTO);
    }
}

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
package org.exoplatform.addons.gamification.service;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.addons.gamification.model.DomainDTO;
import org.exoplatform.addons.gamification.model.ZoneConfig;
import org.picocontainer.Startable;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ZoneRegistryImpl implements Startable, ZoneRegistry {

  private static final Log              LOG     = ExoLogger.getLogger(ZoneRegistryImpl.class);

  private final Map<String, ZoneConfig> zoneMap = new HashMap<>();

  private DomainService                 domainService;

  public ZoneRegistryImpl(DomainService domainService) {
    this.domainService = domainService;
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
        DomainDTO domainDTO = domainService.getDomainByTitle(domain.getZoneName());
        if (domainDTO == null) {
          LOG.info("Saving new Gamification Domain '{}'", domain.getZoneName());
          store(domain);
        }
      }
    } catch (Exception e) {
      LOG.error("Error when saving Domains ", e);
    }
  }

  @Override
  public void stop() {
    // Nothing to stop
  }

  private void store(ZoneConfig zoneConfig) {
    DomainDTO domainDTO = new DomainDTO();
    domainDTO.setTitle(zoneConfig.getZoneName());
    domainDTO.setDescription(zoneConfig.getZoneDescription());
    domainDTO.setLastModifiedBy("Gamification");
    domainDTO.setEnabled(true);
    domainDTO.setDeleted(false);
    domainDTO.setCreatedBy("Gamification");
    try {
      domainDTO.setPriority(Integer.parseInt(zoneConfig.getZonePriority()));
    } catch (Exception e) {
      domainDTO.setPriority(10);
    }
    domainService.createDomain(domainDTO);
  }
}

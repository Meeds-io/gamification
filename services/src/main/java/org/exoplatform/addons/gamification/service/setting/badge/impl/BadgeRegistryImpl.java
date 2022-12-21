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
package org.exoplatform.addons.gamification.service.setting.badge.impl;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.picocontainer.Startable;

import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.setting.badge.BadgeRegistry;
import org.exoplatform.addons.gamification.service.setting.badge.model.BadgeConfig;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class BadgeRegistryImpl implements Startable, BadgeRegistry {

  private static final Log               LOG = ExoLogger.getLogger(BadgeRegistryImpl.class);

  private final Map<String, BadgeConfig> badgesMap;

  private BadgeService                   badgeService;

  private FileService                    fileService;

  private DomainService                  domainService;

  public BadgeRegistryImpl(FileService fileService,
                           DomainService domainService,
                           BadgeService badgeService) {
    this.badgesMap = new HashMap<>();
    this.domainService = domainService;
    this.fileService = fileService;
    this.badgeService = badgeService;
  }

  @Override
  public void addPlugin(BadgeConfig badge) {
    badgesMap.put(badge.getTitle(), badge);

  }

  @Override
  public boolean remove(BadgeConfig badge) {
    badgesMap.remove(badge.getTitle());
    return true;
  }

  @Override
  public void start() {
    try {
      // Processing registered rules
      List<BadgeDTO> badges = badgeService.getAllBadges();
      if (badges.isEmpty()) {
        for (BadgeConfig badge : badgesMap.values()) {
          createBadge(badge);
        }
      }
    } catch (Exception e) {
      LOG.error("Error when processing Rules ", e);
    }
  }

  @Override
  public void stop() {
    // Nothing to stop
  }

  private void createBadge(BadgeConfig badge) {
    try {
      BadgeDTO badgeDTO = badgeService.findBadgeByTitleAndDomain(badge.getTitle(), badge.getDomain());
      if (badgeDTO == null) {
        store(badge);
      }
    } catch (Exception e) {
      LOG.error("Error when processing Rules ", e);
    }
  }

  private void store(BadgeConfig badgeConfig) throws ObjectAlreadyExistsException {
    BadgeDTO badgeDTO = new BadgeDTO();
    badgeDTO.setTitle(badgeConfig.getTitle());
    badgeDTO.setDescription(badgeConfig.getDescription());
    badgeDTO.setDomain(badgeConfig.getDomain());
    badgeDTO.setDomainDTO(domainService.getDomainByTitle(badgeConfig.getDomain()));
    badgeDTO.setIconFileId(storeIcon(badgeConfig.getIcon()));
    badgeDTO.setNeededScore(badgeConfig.getNeededScore());
    badgeDTO.setEnabled(badgeConfig.isEnable());
    badgeDTO.setDeleted(false);
    badgeDTO.setLastModifiedDate(LocalDate.now().toString());
    badgeDTO.setLastModifiedBy("Gamification");
    badgeDTO.setCreatedBy("Gamification");
    badgeDTO.setCreatedDate(LocalDate.now().toString());
    badgeService.addBadge(badgeDTO);
  }

  private long storeIcon(String iconTitle) {
    /** Upload badge's icon into DB */
    // Load icone's binary
    try (InputStream inputStream = BadgeRegistryImpl.class.getClassLoader().getResourceAsStream("medias/images/" + iconTitle)) {
      if (inputStream == null) {
        return 0;
      }
      FileItem fileItem = new FileItem(null,
                              iconTitle,
                              "image/png",
                              "gamification",
                              inputStream.available(),
                              new Date(),
                              "gamification",
                              false,
                              inputStream);

      fileItem = fileService.writeFile(fileItem);
      return fileItem.getFileInfo().getId();
    } catch (Exception e) {
      LOG.error("Enable to inject icon for badge {} ", iconTitle, e);
      return 0;
    }
  }

}

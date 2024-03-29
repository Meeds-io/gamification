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
package io.meeds.gamification.service.impl;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.picocontainer.Startable;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.plugin.BadgeConfigPlugin;
import io.meeds.gamification.service.BadgeRegistry;
import io.meeds.gamification.service.BadgeService;

public class BadgeRegistryImpl implements Startable, BadgeRegistry {

  private static final Log               LOG = ExoLogger.getLogger(BadgeRegistryImpl.class);

  private final Map<String, BadgeConfigPlugin> badgesMap;

  private BadgeService                   badgeService;

  private FileService                    fileService;

  public BadgeRegistryImpl(FileService fileService,
                           BadgeService badgeService) {
    this.badgesMap = new HashMap<>();
    this.fileService = fileService;
    this.badgeService = badgeService;
  }

  @Override
  public void addPlugin(BadgeConfigPlugin badge) {
    badgesMap.put(badge.getTitle(), badge);

  }

  @Override
  public boolean remove(BadgeConfigPlugin badge) {
    badgesMap.remove(badge.getTitle());
    return true;
  }

  @Override
  public void start() {
    try {
      // Processing registered rules
      List<BadgeDTO> badges = badgeService.getAllBadges();
      if (badges.isEmpty()) {
        for (BadgeConfigPlugin badge : badgesMap.values()) {
          store(badge);
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

  private void store(BadgeConfigPlugin badgeConfig) throws ObjectAlreadyExistsException {
    BadgeDTO badgeDTO = new BadgeDTO();
    badgeDTO.setTitle(badgeConfig.getTitle());
    badgeDTO.setDescription(badgeConfig.getDescription());
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

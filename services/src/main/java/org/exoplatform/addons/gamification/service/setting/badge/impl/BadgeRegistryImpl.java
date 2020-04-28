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

import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.setting.badge.BadgeRegistry;
import org.exoplatform.addons.gamification.service.setting.badge.model.BadgeConfig;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.file.services.NameSpaceService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BadgeRegistryImpl implements Startable, BadgeRegistry {

    private static final Log LOG = ExoLogger.getLogger(BadgeRegistryImpl.class);

    private final Map<String, BadgeConfig> badgesMap;

    private BadgeService badgeService;
    private FileService fileService;
    private DomainService domainService;

    public BadgeRegistryImpl(FileService fileService, NameSpaceService nameSpaceService) {
        this.badgesMap = new HashMap<String, BadgeConfig>();
        this.fileService = fileService;
    }

    @Override
    public void addPlugin(BadgeConfig badge) {
        badgesMap.put(badge.getTitle(),badge);

    }

    @Override
    public boolean remove(BadgeConfig badge) {
        badgesMap.remove(badge.getTitle());
        return true;
    }

    @Override
    public void start() {

        badgeService = CommonsUtils.getService(BadgeService.class);

        try {
            // Processing registered rules

            for (BadgeConfig badge : badgesMap.values()) {
                BadgeDTO badgeDTO = badgeService.findBadgeByTitleAndDomain(badge.getTitle(),badge.getDomain());

                if (badgeDTO == null) {
                    store(badge);
                }

            }


        } catch (Exception e) {
            LOG.error("Error when processing Rules ", e);
        }
    }

    private void store(BadgeConfig badgeConfig) {

        domainService = CommonsUtils.getService(DomainService.class);
        BadgeDTO badgeDTO = new BadgeDTO();
        badgeDTO.setTitle(badgeConfig.getTitle());
        badgeDTO.setDescription(badgeConfig.getDescription());
        badgeDTO.setDomain(badgeConfig.getDomain());
        badgeDTO.setDomainDTO(domainService.findDomainByTitle(badgeConfig.getDomain()));
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

    private long storeIcon (String iconTitle) {
        InputStream inputStream;

        /** Upload badge's icon into DB */
        FileItem fileItem = null;

        long iconFiledId = 0;
        try {

            // Load icone's binary
            inputStream = BadgeRegistryImpl.class.getClassLoader().getResourceAsStream("medias/images/"+iconTitle);

            fileItem = new FileItem(null,
                    iconTitle,
                    "image/png",
                    "gamification",
                    inputStream.available(),
                    new Date(),
                    "gamification",
                    false,
                    inputStream);

            fileItem = fileService.writeFile(fileItem);

            iconFiledId = fileItem.getFileInfo().getId();

        } catch (Exception e) {

            LOG.error("Enable to inject icon for badge {} ",iconTitle, e);

        }

        return iconFiledId;



    }

    @Override
    public void stop() {

    }
}
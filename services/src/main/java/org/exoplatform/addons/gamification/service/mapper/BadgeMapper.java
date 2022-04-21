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
package org.exoplatform.addons.gamification.service.mapper;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BadgeMapper {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private BadgeMapper() {
    }

    public static BadgeDTO badgeToBadgeDTO(BadgeEntity badge) {
        return new BadgeDTO(badge);
    }

    public static List<BadgeDTO> badgesToBadgeDTOs(List<BadgeEntity> badges) {
        return badges.stream()
                .filter(Objects::nonNull)
                .map(BadgeMapper::badgeToBadgeDTO)
                .collect(Collectors.toList());
    }
    public static BadgeEntity badgeDTOToBadge(BadgeDTO badgeDTO)  {
        try {
            if (badgeDTO == null) {
                return null;
            } else {
                BadgeEntity badge = new BadgeEntity();
                badge.setId(badgeDTO.getId());
                badge.setTitle(badgeDTO.getTitle());
                badge.setDescription(badgeDTO.getDescription());
                badge.setNeededScore(badgeDTO.getNeededScore());
                badge.setDomain(badgeDTO.getDomain());
                //TODO save an inputStream
                //badge.setIcon(badgeDTO.getIcon());
                badge.setIconFileId(badgeDTO.getIconFileId());
                if (badgeDTO.getStartValidityDate() != null) {
                    badge.setStartValidityDate(formatter.parse(badgeDTO.getStartValidityDate()));
                }
                if (badgeDTO.getEndValidityDate() != null) {
                    badge.setEndValidityDate(formatter.parse(badgeDTO.getEndValidityDate()));
                }
                badge.setEnabled(badgeDTO.isEnabled());
                badge.setDeleted(badgeDTO.isDeleted());
                badge.setCreatedBy(badgeDTO.getCreatedBy());
                if (badgeDTO.getCreatedDate() != null) {
                    badge.setCreatedDate(formatter.parse(badgeDTO.getCreatedDate()));
                }
                badge.setLastModifiedBy(badgeDTO.getLastModifiedBy());

                if (badgeDTO.getLastModifiedDate() != null) {
                    badge.setLastModifiedDate(formatter.parse(badgeDTO.getLastModifiedDate()));
                }
                badge.setDomainEntity(DomainMapper.domainDTOToDomain(badgeDTO.getDomainDTO()));
                return badge;
            }

        } catch (ParseException pe) {
        }
        return null;
    }

    public List<BadgeEntity> badgeDTOsToBadges(List<BadgeDTO> BadgeDTOs) {
        return BadgeDTOs.stream()
                .filter(Objects::nonNull)
                .map(BadgeMapper::badgeDTOToBadge)
                .collect(Collectors.toList());
    }

    public BadgeEntity badgeFromId(Long id) {
        if (id == null) {
            return null;
        }
        BadgeEntity badge = new BadgeEntity();
        badge.setId(id);
        return badge;
    }
}

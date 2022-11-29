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
package org.exoplatform.addons.gamification.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.exoplatform.addons.gamification.entity.BadgeEntity;
import org.exoplatform.addons.gamification.model.BadgeDTO;

public class BadgeMapper {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private BadgeMapper() {
    // Utils class, thus private constructor
  }

  public static BadgeDTO badgeToBadgeDTO(BadgeEntity badgeEntity) {
    BadgeDTO badgeDTO = new BadgeDTO();
    badgeDTO.setId(badgeEntity.getId());
    badgeDTO.setTitle(badgeEntity.getTitle());
    badgeDTO.setDescription(badgeEntity.getDescription());
    badgeDTO.setNeededScore(badgeEntity.getNeededScore());
    badgeDTO.setStartValidityDate(formatDate(badgeEntity.getStartValidityDate()));
    badgeDTO.setEndValidityDate(formatDate(badgeEntity.getEndValidityDate()));
    badgeDTO.setEnabled(badgeEntity.isEnabled());
    badgeDTO.setDeleted(badgeEntity.isDeleted());
    badgeDTO.setCreatedBy(badgeEntity.getCreatedBy());
    badgeDTO.setCreatedDate(formatDate(badgeEntity.getCreatedDate()));
    badgeDTO.setLastModifiedBy(badgeEntity.getLastModifiedBy());
    badgeDTO.setLastModifiedDate(formatDate(badgeEntity.getLastModifiedDate()));
    badgeDTO.setDomain(badgeEntity.getDomain());
    badgeDTO.setDomainDTO((badgeEntity.getDomainEntity() == null) ? null
                                                                  : DomainMapper.domainEntityToDomainDTO(badgeEntity.getDomainEntity()));
    badgeDTO.setIconFileId(badgeEntity.getIconFileId());
    return badgeDTO;
  }

  public static List<BadgeDTO> badgesToBadgeDTOs(List<BadgeEntity> badges) {
    return badges.stream().filter(Objects::nonNull).map(BadgeMapper::badgeToBadgeDTO).collect(Collectors.toList());
  }

  public static BadgeEntity badgeDTOToBadge(BadgeDTO badgeDTO) {
    if (badgeDTO == null) {
      return null;
    } else {

      BadgeEntity badge = new BadgeEntity();
      badge.setId(badgeDTO.getId());
      badge.setTitle(badgeDTO.getTitle());
      badge.setDescription(badgeDTO.getDescription());
      badge.setNeededScore(badgeDTO.getNeededScore());
      badge.setDomain(badgeDTO.getDomain());
      badge.setIconFileId(badgeDTO.getIconFileId());
      if (badgeDTO.getStartValidityDate() != null) {
        badge.setStartValidityDate(parseDate(badgeDTO.getStartValidityDate()));
      }
      if (badgeDTO.getEndValidityDate() != null) {
        badge.setEndValidityDate(parseDate(badgeDTO.getEndValidityDate()));
      }
      badge.setEnabled(badgeDTO.isEnabled());
      badge.setDeleted(badgeDTO.isDeleted());
      badge.setCreatedBy(badgeDTO.getCreatedBy());
      if (badgeDTO.getCreatedDate() != null) {
        badge.setCreatedDate(parseDate(badgeDTO.getCreatedDate()));
      }
      badge.setLastModifiedBy(badgeDTO.getLastModifiedBy());

      if (badgeDTO.getLastModifiedDate() != null) {
        badge.setLastModifiedDate(parseDate(badgeDTO.getLastModifiedDate()));
      }
      badge.setDomainEntity(DomainMapper.domainDTOToDomainEntity(badgeDTO.getDomainDTO()));
      return badge;
    }
  }

  public List<BadgeEntity> badgeDTOsToBadges(List<BadgeDTO> badgeDTOs) {
    return badgeDTOs.stream().filter(Objects::nonNull).map(BadgeMapper::badgeDTOToBadge).collect(Collectors.toList());
  }

  public BadgeEntity badgeFromId(Long id) {
    if (id == null) {
      return null;
    }
    BadgeEntity badge = new BadgeEntity();
    badge.setId(id);
    return badge;
  }

  private static Date parseDate(String dateString) {
    return dateString == null ? null
                              : Date.from(LocalDate.parse(dateString, DATE_FORMATTER)
                                                   .atStartOfDay()
                                                   .atZone(ZoneId.systemDefault())
                                                   .toInstant());
  }

  private static String formatDate(Date date) {
    return date == null ? null : LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DATE_FORMATTER);
  }

}

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
package io.meeds.gamification.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.meeds.gamification.entity.BadgeEntity;
import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.storage.ProgramStorage;

public class BadgeBuilder {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private BadgeBuilder() {
    // Utils class, thus private constructor
  }

  public static BadgeDTO fromEntity(ProgramStorage programStorage, BadgeEntity badgeEntity) {
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
    badgeDTO.setProgram((badgeEntity.getDomainEntity() == null) ? null
                                                                  : programStorage.getDomainById(badgeEntity.getDomainEntity()
                                                                                                           .getId()));
    badgeDTO.setIconFileId(badgeEntity.getIconFileId());
    return badgeDTO;
  }

  public static List<BadgeDTO> fromEntities(ProgramStorage programStorage, List<BadgeEntity> badges) {
    return badges.stream()
                 .filter(Objects::nonNull)
                 .map(entity -> fromEntity(programStorage, entity))
                 .toList();
  }

  public static BadgeEntity toEntity(BadgeDTO badgeDTO) {
    if (badgeDTO == null) {
      return null;
    } else {

      BadgeEntity badge = new BadgeEntity();
      badge.setId(badgeDTO.getId());
      badge.setTitle(badgeDTO.getTitle());
      badge.setDescription(badgeDTO.getDescription());
      badge.setNeededScore(badgeDTO.getNeededScore());
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
      badge.setDomainEntity(ProgramBuilder.toEntity(badgeDTO.getProgram()));
      return badge;
    }
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

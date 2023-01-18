/**
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.utils.Utils;

import static org.exoplatform.addons.gamification.utils.Utils.getRulesTotalScoreByDomain;

public class DomainMapper {

  private DomainMapper() {
  }

  public static List<DomainDTO> domainsToDomainDTOs(List<DomainEntity> domains) {
    return domains.stream().filter(Objects::nonNull).map(DomainMapper::domainEntityToDomainDTO).collect(Collectors.toList());
  }

  public static DomainEntity domainDTOToDomainEntity(DomainDTO domainDTO) {
    if (domainDTO == null) {
      return null;
    } else {
      DomainEntity domain = new DomainEntity();
      domain.setId(domainDTO.getId());
      domain.setTitle(domainDTO.getTitle());
      domain.setDescription(domainDTO.getDescription());
      domain.setCreatedBy(domainDTO.getCreatedBy());
      domain.setLastModifiedBy(domainDTO.getLastModifiedBy());
      domain.setDeleted(domainDTO.isDeleted());
      domain.setEnabled(domainDTO.isEnabled());
      if (domainDTO.getAudienceId() > 0) {
        domain.setAudienceId(domainDTO.getAudienceId());
      }
      if (domainDTO.getCreatedDate() != null) {
        domain.setCreatedDate(Utils.parseRFC3339Date(domainDTO.getCreatedDate()));
      }
      domain.setLastModifiedDate(Utils.parseRFC3339Date(domainDTO.getLastModifiedDate()));
      domain.setPriority(domainDTO.getPriority());
      domain.setBudget(domainDTO.getBudget());
      domain.setCoverFileId(domainDTO.getCoverFileId());
      if (StringUtils.isBlank(domainDTO.getType())) {
        domain.setType(EntityType.AUTOMATIC);
      } else {
        domain.setType(EntityType.valueOf(domainDTO.getType()));
      }
      if (domainDTO.getOwners() != null) {
        domain.setOwners(domainDTO.getOwners());
      } else {
        domain.setOwners(Collections.emptySet());
      }
      return domain;
    }
  }

  public static DomainDTO domainEntityToDomainDTO(DomainEntity domainEntity) {
    if (domainEntity == null) {
      return null;
    } else {
      long lastUpdateTime = domainEntity.getLastModifiedDate() == null ? 0 : domainEntity.getLastModifiedDate().getTime();
      String coverUrl = Utils.buildAttachmentUrl(String.valueOf(domainEntity.getId()),
                                                 lastUpdateTime,
                                                 Utils.TYPE,
                                                 domainEntity.getCoverFileId() == 0);
      DomainDTO domainDTO = new DomainDTO();
      domainDTO.setId(domainEntity.getId());
      domainDTO.setTitle(domainEntity.getTitle());
      domainDTO.setDescription(domainEntity.getDescription());
      if (domainEntity.getAudienceId() != null) {
        domainDTO.setAudienceId(domainEntity.getAudienceId());
      }
      domainDTO.setCreatedBy(domainEntity.getCreatedBy());
      domainDTO.setCreatedDate(Utils.toRFC3339Date(domainEntity.getCreatedDate()));
      domainDTO.setLastModifiedBy(domainEntity.getLastModifiedBy());
      domainDTO.setLastModifiedDate(Utils.toRFC3339Date(domainEntity.getLastModifiedDate()));
      domainDTO.setDeleted(domainEntity.isDeleted());
      domainDTO.setEnabled(domainEntity.isEnabled());
      domainDTO.setBudget(domainEntity.getBudget());
      domainDTO.setType(domainEntity.getType().name());
      domainDTO.setCoverFileId(domainEntity.getCoverFileId());
      domainDTO.setCoverUrl(coverUrl);
      domainDTO.setOwners(domainEntity.getOwners());
      domainDTO.setRulesTotalScore(getRulesTotalScoreByDomain(domainEntity.getId()));

      return domainDTO;
    }
  }
}

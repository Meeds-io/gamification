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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class DomainMapper {

  private static final Log        LOG       = ExoLogger.getLogger(DomainMapper.class);

  public DomainMapper() {
  }

  public static DomainDTO domainToDomainDTO(DomainEntity domain) {
    DomainDTO domainDTO = new DomainDTO();
    domainDTO.setId(domain.getId());
    domainDTO.setTitle(domain.getTitle());
    domainDTO.setDescription(domain.getDescription());
    domainDTO.setPriority(domain.getPriority());
    domainDTO.setCreatedBy(domain.getCreatedBy());
    domainDTO.setCreatedDate(Utils.toRFC3339Date(domain.getCreatedDate()));
    domainDTO.setLastModifiedBy(domain.getLastModifiedBy());
    domainDTO.setLastModifiedDate(Utils.toRFC3339Date(domain.getLastModifiedDate()));
    domainDTO.setDeleted(domainDTO.isDeleted());
    domainDTO.setEnabled(domainDTO.isEnabled());
    return  domainDTO;
  }

  public static List<DomainDTO> domainssToDomainDTOs(List<DomainEntity> domains) {
    return domains.stream()
                  .filter(Objects::nonNull)
                  .map((DomainEntity domain) -> domainToDomainDTO(domain))
                  .collect(Collectors.toList());
  }

  public static DomainEntity domainDTOToDomain(DomainDTO domainDTO) {

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
      domain.setCreatedDate(Utils.parseRFC3339Date(domainDTO.getCreatedDate()));
      domain.setLastModifiedDate(Utils.parseRFC3339Date(domainDTO.getLastModifiedDate()));
      domain.setPriority(domainDTO.getPriority());
      return domain;
    }
  }

  public static List<DomainEntity> domainDTOsToDomains(List<DomainDTO> domainDTOs) {
    return domainDTOs.stream()
                     .filter(Objects::nonNull)
                     .map((DomainDTO domainDTO) -> domainDTOToDomain(domainDTO))
                     .collect(Collectors.toList());
  }

  public static DomainEntity domainFromId(Long id) {
    if (id == null) {
      return null;
    }
    DomainEntity domain = new DomainEntity();
    domain.setId(id);
    return domain;
  }

  public static DomainDTO domainEntityToDomainDTO(DomainEntity domainEntity) {
    if (domainEntity == null) {
      return null;
    } else {
      DomainDTO domain = new DomainDTO();
      domain.setId(domainEntity.getId());
      domain.setTitle(domainEntity.getTitle());
      domain.setDescription(domainEntity.getDescription());
      domain.setCreatedBy(domainEntity.getCreatedBy());
      domain.setLastModifiedBy(domainEntity.getLastModifiedBy());
      domain.setDeleted(domainEntity.isDeleted());
      domain.setEnabled(domainEntity.isEnabled());
      domain.setCreatedDate(Utils.toRFC3339Date(domainEntity.getCreatedDate()));
      domain.setLastModifiedDate(Utils.toRFC3339Date(domainEntity.getLastModifiedDate()));
      domain.setPriority(domainEntity.getPriority());
      return domain;
    }
  }
}

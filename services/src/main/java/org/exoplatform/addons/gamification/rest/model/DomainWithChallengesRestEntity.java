/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

package org.exoplatform.addons.gamification.rest.model;

import java.util.List;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DomainWithChallengesRestEntity extends DomainDTO {

  private static final long         serialVersionUID = 7958375518563362512L;

  private List<ChallengeRestEntity> challenges;                             // NOSONAR

  private int                       challengesOffset;

  private int                       challengesLimit;

  private int                       challengesSize;

  public DomainWithChallengesRestEntity(DomainDTO domain) {
    super(domain.getId(),
          domain.getTitle(),
          domain.getDescription(),
          domain.getPriority(),
          domain.getAudienceId(),
          domain.getCreatedBy(),
          domain.getCreatedDate(),
          domain.getLastModifiedBy(),
          domain.getLastModifiedDate(),
          domain.isDeleted(),
          domain.isEnabled(),
          domain.getBudget(),
          domain.getType(),
          domain.getCoverFileId(),
          domain.getCoverUrl(),
          domain.getOwners(),
          domain.getRulesTotalScore());
  }

}

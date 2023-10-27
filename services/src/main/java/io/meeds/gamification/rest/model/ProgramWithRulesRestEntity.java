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

package io.meeds.gamification.rest.model;

import java.util.List;

import io.meeds.gamification.model.ProgramDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("deprecation")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProgramWithRulesRestEntity extends ProgramDTO {

  private static final long    serialVersionUID = 7958375518563362512L;

  private List<RuleRestEntity> rules;                                  // NOSONAR

  private int                  offset;

  private int                  limit;

  private int                  size;

  public ProgramWithRulesRestEntity(ProgramDTO program) {
    super(program.getId(),
          program.getTitle(),
          program.getDescription(),
          program.getColor(),
          program.getSpaceId(),
          program.getPriority(),
          program.getCreatedBy(),
          program.getCreatedDate(),
          program.getLastModifiedBy(),
          program.getLastModifiedDate(),
          program.isDeleted(),
          program.isEnabled(),
          program.getBudget(),
          program.getType(),
          program.getCoverUploadId(),
          program.getAvatarUploadId(),
          program.getCoverFileId(),
          program.getAvatarFileId(),
          program.getCoverUrl(),
          program.getAvatarUrl(),
          program.getOwnerIds(),
          program.getRulesTotalScore(),
          program.isOpen(),
          program.getVisibility());
  }

}

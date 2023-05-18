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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package io.meeds.gamification.rest.model;

import java.util.List;
import java.util.Set;

import org.exoplatform.social.core.space.model.Space;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProgramRestEntity extends ProgramDTO {

  private static final long serialVersionUID = -5995118028928360591L;

  private Space             space;                                   // NOSONAR

  private UserInfo          userInfo;                                // NOSONAR

  private List<UserInfo>    owners;                                  // NOSONAR

  public ProgramRestEntity(long id, // NOSONAR
                           String title,
                           String description,
                           long audienceId,
                           int priority,
                           String createdBy,
                           String createdDate,
                           String lastModifiedBy,
                           String lastModifiedDate,
                           boolean deleted,
                           boolean enabled,
                           long budget,
                           String type,
                           String coverUploadId,
                           long coverFileId,
                           String coverUrl,
                           Set<Long> ownerIds,
                           long rulesTotalScore,
                           Space space,
                           UserInfo userInfo,
                           List<UserInfo> owners) {
    super(id,
          title,
          description,
          audienceId,
          priority,
          createdBy,
          createdDate,
          lastModifiedBy,
          lastModifiedDate,
          deleted,
          enabled,
          budget,
          type,
          coverUploadId,
          coverFileId,
          coverUrl,
          ownerIds,
          rulesTotalScore);
    this.space = space;
    this.userInfo = userInfo;
    this.owners = owners;
  }

}

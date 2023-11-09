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

import io.meeds.gamification.constant.EntityVisibility;
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

  private List<UserInfo>    administrators;                          // NOSONAR

  private int               activeRulesCount;                        // NOSONAR

  public ProgramRestEntity(long id, // NOSONAR
                           String title,
                           String description,
                           String color,
                           long spaceId,
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
                           String avatarUploadId,
                           long coverFileId,
                           long avatarFileId,
                           String coverUrl,
                           String avatarUrl,
                           Set<Long> ownerIds,
                           long rulesTotalScore,
                           boolean open,
                           Space space,
                           UserInfo userInfo,
                           List<UserInfo> owners,
                           List<UserInfo> administrators,
                           int activeRulesCount,
                           EntityVisibility visibility) {
    super(id,
          title,
          description,
          color,
          spaceId,
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
          avatarUploadId,
          coverFileId,
          avatarFileId,
          coverUrl,
          avatarUrl,
          ownerIds,
          rulesTotalScore,
          open,
          visibility);
    this.space = space;
    this.userInfo = userInfo;
    this.owners = owners;
    this.administrators = administrators;
    this.activeRulesCount = activeRulesCount;
  }

}

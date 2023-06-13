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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.exoplatform.social.core.space.model.Space;

import io.meeds.gamification.model.UserInfo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgramRestEntity {

  private Long           id;

  private String         title;

  private String         description;

  private Space          space;

  private int            priority;

  private String         createdBy;

  private String         createdDate;

  private String         lastModifiedBy;

  private String         lastModifiedDate;

  private boolean        enabled;

  private boolean        deleted;

  private Long           budget;

  private String         type;

  private String         coverUrl;

  private String         coverUploadId;

  private Long           rulesTotalScore;

  private List<UserInfo> owners;

  private UserInfo       userInfo;

  public ProgramRestEntity(Long id, // NOSONAR
                          String title,
                          String description,
                          Space space,
                          int priority,
                          String createdBy,
                          String createdDate,
                          String lastModifiedBy,
                          String lastModifiedDate,
                          boolean enabled,
                          boolean deleted,
                          Long budget,
                          String type,
                          String coverUrl,
                          Long rulesTotalScore,
                          List<UserInfo> owners,
                          UserInfo userInfo) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.space = space;
    this.priority = priority;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.enabled = enabled;
    this.deleted = deleted;
    this.budget = budget;
    this.type = type;
    this.coverUrl = coverUrl;
    this.rulesTotalScore = rulesTotalScore;
    this.owners = owners;
    this.userInfo = userInfo;
  }

}

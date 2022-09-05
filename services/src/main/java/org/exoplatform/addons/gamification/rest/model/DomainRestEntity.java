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

package org.exoplatform.addons.gamification.rest.model;

import java.util.List;

import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DomainRestEntity implements Cloneable {

  private Long           id;

  private String         title;

  private String         description;

  private int            priority;

  private String         createdBy;

  private String         createdDate;

  private String         lastModifiedBy;

  private String         lastModifiedDate;

  private boolean        enabled;

  private Long           budget;

  private String         type;

  private String         coverUrl;

  private String         coverUploadId;

  private List<UserInfo> owners;

  private UserInfo       userInfo;

  public DomainRestEntity(Long id, // NOSONAR
                          String title,
                          String description,
                          int priority,
                          String createdBy,
                          String createdDate,
                          String lastModifiedBy,
                          String lastModifiedDate,
                          boolean enabled,
                          Long budget,
                          String type,
                          String coverUrl,
                          List<UserInfo> owners,
                          UserInfo userInfo) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.priority = priority;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.enabled = enabled;
    this.budget = budget;
    this.type = type;
    this.coverUrl = coverUrl;
    this.owners = owners;
    this.userInfo = userInfo;
  }

  @Override
  protected DomainRestEntity clone() { // NOSONAR
    return new DomainRestEntity(id,
                                title,
                                description,
                                priority,
                                createdBy,
                                createdDate,
                                lastModifiedBy,
                                lastModifiedDate,
                                enabled,
                                budget,
                                type,
                                coverUrl,
                                coverUploadId,
                                owners,
                                userInfo);
  }

}

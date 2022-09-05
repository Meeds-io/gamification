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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.addons.gamification.service.dto.configuration;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DomainDTO implements Serializable, Cloneable {

  private static final long serialVersionUID = -8857818632949907592L;

  private long              id;

  private String            title;

  private String            description;

  private int               priority;

  private String            createdBy;

  private String            createdDate;

  private String            lastModifiedBy;

  private String            lastModifiedDate;

  private boolean           deleted;

  private boolean           enabled;

  private long              budget;

  private String            type;

  private String            coverUploadId;

  private long              coverFileId;

  private String            coverUrl;

  private Set<Long>         owners;

  public DomainDTO(long id, // NOSONAR
                   String title,
                   String description,
                   int priority,
                   String createdBy,
                   String createdDate,
                   String lastModifiedBy,
                   String lastModifiedDate,
                   boolean deleted,
                   boolean enabled,
                   long budget,
                   String type,
                   long coverFileId,
                   String coverUrl,
                   Set<Long> owners) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.priority = priority;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.deleted = deleted;
    this.enabled = enabled;
    this.budget = budget;
    this.type = type;
    this.coverFileId = coverFileId;
    this.coverUrl = coverUrl;
    this.owners = owners;
  }

  @Override
  public DomainDTO clone() { // NOSONAR
    return new DomainDTO(id,
                         title,
                         description,
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
                         owners);
  }

}

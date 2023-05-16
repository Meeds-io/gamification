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
package io.meeds.gamification.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RecurrenceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RuleDTO implements Serializable {

  private static final long serialVersionUID = 7423093905330790451L;

  protected Long            id;

  protected String          title;

  protected String          description;

  protected int             score;

  private ProgramDTO        program;

  protected boolean         enabled;

  protected boolean         deleted;

  private String            createdBy;

  private String            createdDate;

  private String            lastModifiedBy;

  private String            event;

  private String            lastModifiedDate;

  private String            startDate;

  private String            endDate;

  private EntityType        type;

  private RecurrenceType    recurrence;

  public long getAudienceId() {
    return program == null ? 0 : program.getAudienceId();
  }

  public long getDomainId() {
    return program == null ? 0 : program.getId();
  }

  public Set<Long> getManagers() {
    return program == null ? Collections.emptySet() : program.getOwners();
  }

}

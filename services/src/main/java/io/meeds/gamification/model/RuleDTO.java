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
import java.util.HashSet;
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

  protected ProgramDTO      program;

  protected boolean         enabled;

  protected boolean         deleted;

  protected String          createdBy;

  protected String          createdDate;

  protected String          lastModifiedBy;

  protected String          event;

  protected String          lastModifiedDate;

  protected String          startDate;

  protected String          endDate;

  protected long            activityId;

  protected long            cacheTime;

  protected Set<Long>       prerequisiteRuleIds;                    // NOSONAR

  protected EntityType      type;

  protected RecurrenceType  recurrence;

  /**
   * Deprecated should be renamed to spaceId knowing that audienceId
   * should reference an identity id instead
   * @return Space Id attached to program
   * @deprecated user spaceId instead
   */
  @Deprecated(forRemoval = true, since = "1.5.0")
  public long getAudienceId() {
    return getSpaceId();
  }

  public long getProgramId() {
    return program == null ? 0 : program.getId();
  }

  public long getSpaceId() {
    return program == null ? 0 : program.getSpaceId();
  }

  public Set<Long> getManagers() {
    return program == null ? Collections.emptySet() : program.getOwnerIds();
  }

  public boolean isOpen() {
    return program != null && program.isOpen();
  }

  @Override
  public RuleDTO clone() { // NOSONAR
    return new RuleDTO(id,
                       title,
                       description,
                       score,
                       program,
                       enabled,
                       deleted,
                       createdBy,
                       createdDate,
                       lastModifiedBy,
                       event,
                       lastModifiedDate,
                       startDate,
                       endDate,
                       activityId,
                       cacheTime,
                       prerequisiteRuleIds == null ? null : new HashSet<>(prerequisiteRuleIds),
                       type,
                       recurrence);
  }

}

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

import java.util.Map;
import java.util.Set;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RecurrenceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class RulePublication extends RuleDTO {

  private static final long     serialVersionUID = -7606132880459885724L;

  protected long                spaceId;

  protected String              message;

  protected Map<String, String> templateParams;                          // NOSONAR

  protected boolean             publish;

  public RulePublication(RuleDTO rule,
                         long spaceId,
                         String message,
                         Map<String, String> templateParams,
                         boolean publish) {
    super(rule.getId(),
          rule.getTitle(),
          rule.getDescription(),
          rule.getScore(),
          rule.getProgram(),
          rule.isEnabled(),
          rule.isDeleted(),
          rule.getCreatedBy(),
          rule.getCreatedDate(),
          rule.getLastModifiedBy(),
          rule.getEvent(),
          rule.getLastModifiedDate(),
          rule.getStartDate(),
          rule.getEndDate(),
          rule.getActivityId(),
          rule.getCacheTime(),
          rule.getPrerequisiteRuleIds(),
          rule.getType(),
          rule.getRecurrence());
    this.spaceId = spaceId;
    this.message = message;
    this.templateParams = templateParams;
    this.publish = publish;
  }

  public RulePublication(Long id, // NOSONAR
                         String title,
                         String description,
                         int score,
                         ProgramDTO program,
                         boolean enabled,
                         boolean deleted,
                         String createdBy,
                         String createdDate,
                         String lastModifiedBy,
                         String event,
                         String lastModifiedDate,
                         String startDate,
                         String endDate,
                         long activityId,
                         long cacheTime,
                         Set<Long> prerequisiteRuleIds,
                         EntityType type,
                         RecurrenceType recurrence,
                         long spaceId,
                         String message,
                         Map<String, String> templateParams,
                         boolean publish) {
    super(id,
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
          prerequisiteRuleIds,
          type,
          recurrence);
    this.spaceId = spaceId;
    this.message = message;
    this.templateParams = templateParams;
    this.publish = publish;
  }

  @Override
  public RulePublication clone() { // NOSONAR
    return new RulePublication(id,
                               title,
                               description,
                               score,
                               program,
                               publish,
                               publish,
                               createdBy,
                               createdDate,
                               lastModifiedBy,
                               event,
                               lastModifiedDate,
                               startDate,
                               endDate,
                               activityId,
                               cacheTime,
                               prerequisiteRuleIds,
                               type,
                               recurrence,
                               spaceId,
                               message,
                               templateParams,
                               publish);
  }
}

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

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.UserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RuleRestEntity extends RuleDTO {

  private static final long           serialVersionUID = 5177522303947816474L;

  private long                        audience;

  private Set<Long>                   managers;

  private List<RealizationRestEntity> realizations;                           // NOSONAR

  private long                        realizationsCount;

  private boolean                     hasPendingRealization;

  private UserInfo                    userInfo;                               // NOSONAR

  private List<RuleDTO>               prerequisiteRules;

  private boolean                     published;

  private boolean                     favorite;

  public RuleRestEntity(Long id, // NOSONAR
                        String title,
                        String description,
                        int score,
                        ProgramDTO program,
                        boolean enabled,
                        boolean deleted,
                        String createdBy,
                        String createdDate,
                        String lastModifiedBy,
                        EventDTO event,
                        String lastModifiedDate,
                        String startDate,
                        String endDate,
                        long activityId,
                        long cacheTime,
                        boolean published,
                        boolean favorite,
                        Set<Long> prerequisiteRuleIds,
                        EntityType type,
                        RealizationStatus defaultRealizationStatus,
                        RecurrenceType recurrence,
                        long audience,
                        Set<Long> managers,
                        List<RealizationRestEntity> realizations,
                        long realizationsCount,
                        boolean hasPendingRealization,
                        UserInfo userInfo,
                        List<RuleDTO> prerequisiteRules) {
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
          defaultRealizationStatus,
          recurrence);
    this.published = published;
    this.favorite = favorite;
    this.audience = audience;
    this.managers = managers;
    this.realizations = realizations;
    this.realizationsCount = realizationsCount;
    this.hasPendingRealization = hasPendingRealization;
    this.userInfo = userInfo;
    this.prerequisiteRules = prerequisiteRules;
  }

  @Override
  public RuleRestEntity clone() { // NOSONAR
    return new RuleRestEntity(id,
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
                              published,
                              favorite,
                              prerequisiteRuleIds,
                              type,
                              defaultRealizationStatus,
                              recurrence,
                              audience,
                              managers,
                              realizations,
                              realizationsCount,
                              hasPendingRealization,
                              userInfo,
                              prerequisiteRules);
  }

}

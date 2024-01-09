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
package io.meeds.gamification.model.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleFilter implements Serializable {

  private static final long serialVersionUID = 7863115218512008695L;

  private String            term;

  private boolean           favorites;

  private Locale            locale;

  private String            eventName;

  private String            eventType;

  private long              programId;

  private List<Long>        spaceIds;

  private boolean           excludeNoSpace;

  private boolean           includeDeleted;

  private List<Long>        ruleIds;

  private DateFilterType    dateFilterType;

  private EntityFilterType  type             = EntityFilterType.ALL;

  private EntityStatusType  status           = EntityStatusType.ALL;

  private EntityStatusType  programStatus    = EntityStatusType.ALL;

  private boolean           orderByRealizations;

  private List<Long>        excludedRuleIds;

  private String            sortBy;

  private boolean           sortDescending   = true;

  private boolean           allSpaces;

  private List<String>      tagNames;

  private long              identityId;

  public RuleFilter(boolean allSpaces) {
    this.allSpaces = allSpaces;
  }

  public RuleFilter clone() { // NOSONAR
    return new RuleFilter(term,
                          favorites,
                          locale,
                          eventName,
                          eventType,
                          programId,
                          spaceIds == null ? null : new ArrayList<>(spaceIds),
                          excludeNoSpace,
                          includeDeleted,
                          ruleIds == null ? null : new ArrayList<>(ruleIds),
                          dateFilterType,
                          type,
                          status,
                          programStatus,
                          orderByRealizations,
                          excludedRuleIds == null ? null : new ArrayList<>(excludedRuleIds),
                          sortBy,
                          sortDescending,
                          allSpaces,
                          tagNames,
                          identityId);
  }

}

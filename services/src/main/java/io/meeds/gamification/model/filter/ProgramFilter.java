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

package io.meeds.gamification.model.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.meeds.gamification.constant.EntityFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramFilter implements Serializable {

  private static final long serialVersionUID = -610860313235738916L;

  private EntityStatusType  status           = EntityStatusType.ALL;

  /**
   * @deprecated There is no difference anymore between Automatic or Manual
   *             Domain/Program. This is preserved for future usages, but not
   *             used
   */
  @Deprecated(forRemoval = false, since = "1.5.0")
  private EntityFilterType  type             = EntityFilterType.ALL;

  private String            programTitle;

  private boolean           includeDeleted;

  private boolean           sortByBudget;

  private List<Long>        spacesIds;

  private long              ownerId;

  private boolean           allSpaces;

  private boolean           excludeOpen;

  private String            sortBy;

  private boolean           sortDescending   = true;

  public ProgramFilter(boolean allSpaces) {
    this.allSpaces = allSpaces;
  }

  public ProgramFilter clone() { // NOSONAR
    return new ProgramFilter(status,
                             type,
                             programTitle,
                             includeDeleted,
                             sortByBudget,
                             spacesIds == null ? null : new ArrayList<>(spacesIds),
                             ownerId,
                             allSpaces,
                             excludeOpen,
                             sortBy,
                             sortDescending);
  }

}

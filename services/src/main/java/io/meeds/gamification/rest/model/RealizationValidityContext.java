/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
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

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RealizationValidityContext implements Cloneable {

  private boolean              validIdentity       = true;

  private boolean              validProgram        = true;

  private boolean              validAudience       = true;

  private boolean              validRule           = true;

  private boolean              validDates          = true;

  private boolean              validRecurrence     = true;

  private boolean              validWhitelist      = true;

  private Map<String, Boolean> validPrerequisites  = null;

  private long                 nextOccurenceMillis = 0;

  public boolean isValidForIdentity() {
    return validIdentity && isValid();
  }

  public boolean isValid() {
    return validProgram
        && validAudience
        && validRule
        && validDates
        && validRecurrence
        && validWhitelist
        && (MapUtils.isEmpty(validPrerequisites) || validPrerequisites.values().stream().allMatch(Boolean::booleanValue));
  }

  @Override
  protected RealizationValidityContext clone() { // NOSONAR
    return new RealizationValidityContext(validIdentity,
                                          validProgram,
                                          validAudience,
                                          validRule,
                                          validDates,
                                          validRecurrence,
                                          validWhitelist,
                                          validPrerequisites,
                                          nextOccurenceMillis);
  }

}

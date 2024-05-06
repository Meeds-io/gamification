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

package io.meeds.gamification.constant;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public enum PeriodType {
  WEEK, MONTH, YEAR, ALL;

  public Date getFromDate() {
    return switch (this) {
    case ALL: {
      yield null;
    }
    case WEEK: {
      yield Date.from(LocalDate.now()
                               .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                               .atStartOfDay(ZoneId.systemDefault())
                               .toInstant());
    }
    case MONTH: {
      yield Date.from(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    case YEAR: {
      yield Date.from(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    };
  }

  public Date getToDate() {
    return switch (this) {
    case ALL: {
      yield null;
    }
    case WEEK: {
      yield Date.from(LocalDate.now()
                               .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                               .atStartOfDay(ZoneId.systemDefault())
                               .plusWeeks(1)
                               .toInstant());
    }
    case MONTH: {
      yield Date.from(LocalDate.now()
                               .with(TemporalAdjusters.firstDayOfNextMonth())
                               .atStartOfDay(ZoneId.systemDefault())
                               .toInstant());
    }
    case YEAR: {
      yield Date.from(LocalDate.now()
                               .with(TemporalAdjusters.firstDayOfNextYear())
                               .atStartOfDay(ZoneId.systemDefault())
                               .toInstant());
    }
    };
  }
}

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
package io.meeds.gamification.constant;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public enum RecurrenceType {

  NONE, ONCE, DAILY, WEEKLY, MONTHLY;

  private static final Date ONCE_RECURRENCE_START_DATE = new Date(1200000000000l);

  public Date getPeriodStartDate() {
    return switch (this) {
    case NONE: {
      yield null;
    }
    case ONCE: {
      yield ONCE_RECURRENCE_START_DATE;
    }
    case DAILY: {
      yield Date.from(LocalDate.now()
                               .atStartOfDay(ZoneId.systemDefault())
                               .toInstant());
    }
    case WEEKLY: {
      yield Date.from(LocalDate.now()
                               .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                               .atStartOfDay(ZoneId.systemDefault())
                               .toInstant());
    }
    case MONTHLY: {
      yield Date.from(LocalDate.now()
                               .with(TemporalAdjusters.firstDayOfMonth())
                               .atStartOfDay(ZoneId.systemDefault())
                               .toInstant());
    }
    default:
      throw new IllegalArgumentException("Unexpected value: " + this);
    };
  }
}

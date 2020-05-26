/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
package org.exoplatform.addons.gamification.service.effective;

import org.exoplatform.addons.gamification.IdentityType;

public class LeaderboardFilter {

  private int          loadCapacity = 10;

  private String       currentUser  = null;

  private String       domain       = "all";

  private IdentityType identityType = IdentityType.USER;

  private Period       period       = Period.WEEK;

  public enum Period {
    ALL,
    MONTH,
    WEEK
  }

  public int getLoadCapacity() {
    return loadCapacity;
  }

  public void setLoadCapacity(String loadCapacity) {
    this.loadCapacity = Integer.parseInt(loadCapacity);
  }

  public void setLoadCapacity(int limit) {
    this.loadCapacity = limit;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getPeriod() {
    return period.name();
  }

  public void setPeriod(String period) {
    this.period = Period.valueOf(period.toUpperCase());
  }

  public IdentityType getIdentityType() {
    return identityType;
  }

  public void setIdentityType(IdentityType identityType) {
    this.identityType = identityType;
  }

  public void setCurrentUser(String currentUser) {
    this.currentUser = currentUser;
  }

  public String getCurrentUser() {
    return currentUser;
  }
}

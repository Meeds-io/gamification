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

import java.io.Serializable;

public abstract class AbstractLeaderboard implements Serializable {

  private static final long serialVersionUID = 6094595264371166779L;

  protected String          earnerId;

  protected long            reputationScore;

  public String getEarnerId() {
    return earnerId;
  }

  public void setEarnerId(String earnerId) {
    this.earnerId = earnerId;
  }

  public long getReputationScore() {
    return reputationScore;
  }

  public void setReputationScore(long reputationScore) {
    this.reputationScore = reputationScore;
  }
}

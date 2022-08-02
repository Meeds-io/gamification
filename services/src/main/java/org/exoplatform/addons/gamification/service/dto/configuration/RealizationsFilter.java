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
package org.exoplatform.addons.gamification.service.dto.configuration;

import java.io.Serializable;

import javax.ws.rs.DefaultValue;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealizationsFilter implements Serializable {

  private static final long serialVersionUID = 7863115218512008696L;

  private Boolean           isSortedByActionTitle;

  private Boolean           isSortedByRuleId;

  private Boolean           sortDescending;

  private String            fromDate;

  private String            toDate;
  

  public Boolean getIsSortedByActionTitle() {
    return isSortedByActionTitle;
  }

  public void setIsSortedByActionTitle(Boolean isSortedByActionTitle) {
    this.isSortedByActionTitle = isSortedByActionTitle;
  }

  public Boolean getIsSortedByRuleId() {
    return isSortedByRuleId;
  }

  public void setIsSortedByRuleId(Boolean isSortedByRuleId) {
    this.isSortedByRuleId = isSortedByRuleId;
  }

  public Boolean getSortDescending() {
    return sortDescending;
  }

  public void setSortDescending(Boolean sortDescending) {
    this.sortDescending = sortDescending;
  }

  public String getFromDate() {
    return fromDate;
  }

  public void setFromDate(String fromDate) {
    this.fromDate = fromDate;
  }

  public String getToDate() {
    return toDate;
  }

  public void setToDate(String toDate) {
    this.toDate = toDate;
  }
  
  

}

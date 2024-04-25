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
import java.util.Date;
import java.util.List;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealizationFilter implements Serializable {

  private static final long serialVersionUID = 7863115218512008696L;

  private List<String>      earnerIds;

  private String            sortField;

  private boolean           sortDescending;

  private boolean           owned;

  private Date              fromDate;

  private Date              toDate;

  private RealizationStatus status;

  private IdentityType      earnerType;

  private List<Long>        programIds;

  private List<Long>        ruleIds;

  private List<Long>      reviewerIds;

  private boolean           allPrograms;

  public RealizationFilter(List<String> earnerIds, // NOSONAR
                           String sortField,
                           boolean sortDescending,
                           Date fromDate,
                           Date toDate,
                           IdentityType identityType,
                           RealizationStatus status,
                           List<Long> ruleIds,
                           List<Long> reviewerIds) {
    this.earnerIds = earnerIds;
    this.sortField = sortField;
    this.sortDescending = sortDescending;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.earnerType = identityType;
    this.status = status;
    this.ruleIds = ruleIds;
    this.reviewerIds = reviewerIds;
  }

  @Override
  public RealizationFilter clone() { // NOSONAR
    return new RealizationFilter(earnerIds == null ? null : new ArrayList<>(earnerIds),
                                 sortField,
                                 sortDescending,
                                 owned,
                                 fromDate,
                                 toDate,
                                 status,
                                 earnerType,
                                 programIds == null ? null : new ArrayList<>(programIds),
                                 ruleIds == null ? null : new ArrayList<>(ruleIds),
                                 reviewerIds == null ? null : new ArrayList<>(reviewerIds),
                                 allPrograms);
  }

}

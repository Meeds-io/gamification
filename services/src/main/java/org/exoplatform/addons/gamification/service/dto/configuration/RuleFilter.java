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
import java.util.List;

import org.exoplatform.addons.gamification.service.dto.configuration.constant.DateFilterType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleFilter implements Serializable {

  private static final long serialVersionUID = 7863115218512008695L;

  private String            username;

  private String            term;

  private long              domainId;

  private List<Long>        spaceIds;

  private DateFilterType    dateFilterType;

  private EntityFilterType  entityFilterType;

  private EntityStatusType  entityStatusType;

  private boolean           includeDeleted;

  private boolean           orderByRealizations;

  private List<Long>        excludedChallengesIds;
}

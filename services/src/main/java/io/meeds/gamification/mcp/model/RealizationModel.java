/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A thin view of a realization (a gamification achievement: points earned by
 * completing a quest).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealizationModel {

  @JsonProperty("realization_id")
  private long   id;

  @JsonProperty("quest_id")
  private Long   questId;

  @JsonProperty("quest_title")
  private String questTitle;

  @JsonProperty("campaign_id")
  private long   campaignId;

  private long   score;

  private String status;

  @JsonProperty("created_date")
  private String createdDate;

}

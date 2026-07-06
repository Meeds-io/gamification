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
 * A thin view of a campaign badge (a reachable level) exposed to the AI agent.
 * The badge title names the level; {@code neededScore} is the total number of
 * campaign points a user must accumulate to unlock it. The {@code level} is a
 * 1-based rank of the badge within its campaign, ordered by the score needed.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BadgeModel {

  @JsonProperty("badge_id")
  private long   id;

  private String title;

  private String description;

  private int    level;

  @JsonProperty("needed_score")
  private int    neededScore;

  @JsonProperty("icon_file_id")
  private long   iconFileId;

}

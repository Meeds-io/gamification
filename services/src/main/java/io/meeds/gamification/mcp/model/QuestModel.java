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

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A thin view of a quest (a gamification action/rule) exposed to the AI agent.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestModel {

  @JsonProperty("quest_id")
  private long      id;

  private String    title;

  private String    description;

  private int       score;

  /**
   * MANUAL (a challenge the user announces) or AUTOMATIC (event-triggered).
   */
  private String    type;

  @JsonProperty("start_date")
  private String    startDate;

  @JsonProperty("end_date")
  private String    endDate;

  private boolean   enabled;

  private String    recurrence;

  @JsonProperty("campaign_id")
  private long      campaignId;

  @JsonProperty("campaign_title")
  private String    campaignTitle;

  @JsonProperty("prerequisite_quest_ids")
  private Set<Long> prerequisiteQuestIds;

}

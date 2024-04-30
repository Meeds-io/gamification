/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
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
 *
 */
package io.meeds.gamification.plugin;

import static io.meeds.gamification.constant.GamificationConstant.*;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;

public class ContributionEventPlugin extends EventPlugin {

  public static final String EVENT_TYPE = "contribution";

  @Override
  public String getEventType() {
    return EVENT_TYPE;
  }

  public List<String> getTriggers() {
    return List.of(GAMIFICATION_CONTRIBUTIONS_REVIEW_CONTRIBUTIONS);
  }

  @Override
  public boolean isValidEvent(Map<String, String> eventProperties, String triggerDetails) {
    List<String> desiredRuleIds = getListFromCsv(eventProperties.get("ruleIds"));
    List<String> desiredProgramIds = getListFromCsv(eventProperties.get("programIds"));

    Map<String, String> triggerDetailsMap = stringToMap(triggerDetails);
    if (CollectionUtils.isNotEmpty(desiredProgramIds)) {
      String programId = triggerDetailsMap.get("programId");
      return programId != null && desiredProgramIds.contains(programId);
    }
    if (CollectionUtils.isNotEmpty(desiredRuleIds)) {
      String ruleId = triggerDetailsMap.get("ruleId");
      return ruleId != null && desiredRuleIds.contains(ruleId);
    }
    return triggerDetailsMap.get("eventReviewed") == null || !triggerDetailsMap.get("eventReviewed").equals("reviewContribution");
  }

  private List<String> getListFromCsv(String csvString) {
    if (csvString == null || csvString.isEmpty()) {
      return Collections.emptyList();
    }
    return Arrays.asList(csvString.split("\\s*,\\s*"));
  }

  private Map<String, String> stringToMap(String mapAsString) {
    Map<String, String> map = new HashMap<>();
    if (mapAsString != null && mapAsString.startsWith("{") && mapAsString.endsWith("}")) {
      mapAsString = mapAsString.substring(1, mapAsString.length() - 1);
      String[] pairs = mapAsString.split(",\\s*");
      for (String pair : pairs) {
        String[] keyValue = pair.split("\\s*:\\s*", 2);
        if (keyValue.length == 2) {
          String key = keyValue[0].trim();
          String value = keyValue[1].trim();
          map.put(key, value);
        }
      }
    }
    return map;
  }
}

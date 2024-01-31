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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StreamEventPlugin extends EventPlugin {

  public static final String EVENT_TYPE = "stream";

  @Override
  public String getEventType() {
    return EVENT_TYPE;
  }

  public List<String> getTriggers() {
    return List.of(GAMIFICATION_SOCIAL_POST_ACTIVITY,
                   GAMIFICATION_SOCIAL_POST_ACTIVITY_COMMENT,
                   GAMIFICATION_SOCIAL_LIKE_ACTIVITY,
                   GAMIFICATION_SOCIAL_LIKE_ACTIVITY_COMMENT,
                   GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT,
                   GAMIFICATION_SOCIAL_RECEIVE_LIKE_ACTIVITY,
                   GAMIFICATION_SOCIAL_RECEIVE_ACTIVITY_COMMENT);
  }

  @Override
  public boolean isValidEvent(Map<String, String> eventProperties, String triggerDetails) {
    String desiredActivityId = eventProperties.get("activityId");
    String desiredSpaceId = eventProperties.get("spaceId");
    Map<String, String> triggerDetailsMop = stringToMap(triggerDetails);
    return (desiredActivityId != null && desiredActivityId.equals(triggerDetailsMop.get("activityId")))
        || (desiredSpaceId != null && desiredSpaceId.equals(triggerDetailsMop.get("spaceId")));
  }

  private static Map<String, String> stringToMap(String mapAsString) {
    Map<String, String> map = new HashMap<>();
    mapAsString = mapAsString.substring(1, mapAsString.length() - 1);
    String[] pairs = mapAsString.split(", ");
    for (String pair : pairs) {
      String[] keyValue = pair.split(": ");
      String key = keyValue[0].trim();
      String value = keyValue[1].trim();
      map.put(key, value);
    }
    return map;
  }
}

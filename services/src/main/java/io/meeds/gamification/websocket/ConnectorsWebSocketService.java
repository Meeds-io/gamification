/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
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
package io.meeds.gamification.websocket;

import org.exoplatform.commons.utils.CommonsUtils;

import org.exoplatform.social.websocket.entity.WebSocketMessage;
import org.exoplatform.ws.frameworks.cometd.ContinuationService;

import java.util.HashMap;
import java.util.Map;

public class ConnectorsWebSocketService {

  public static final String  COMETD_CHANNEL = "/GamificationConnectors";

  private ContinuationService continuationService;

  /**
   * Propagate an event from Backend to frontend through WebSocket Message when
   * connector identifier changed
   *
   * @param eventName event name that will allow Browser to distinguish which
   *          behavior to adopt in order to update UI
   */
  public void sendMessage(String eventName, String connectorName, String username, String connectorIdentifier) {
    getContinuationService();
    Map<String, String> stringMap = new HashMap<>();
    stringMap.put("connectorName", connectorName);
    stringMap.put("connectorIdentifier", connectorIdentifier);
    String wsMessage = new WebSocketMessage(eventName, stringMap).toJsonString();
    continuationService.sendMessage(username, COMETD_CHANNEL, wsMessage);
  }

  private void getContinuationService() {
    if (continuationService == null) {
      continuationService = CommonsUtils.getService(ContinuationService.class);
    }
  }
}

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
package io.meeds.gamification.websocket.listener;

import io.meeds.gamification.websocket.ConnectorsWebSocketService;
import io.meeds.gamification.websocket.entity.ConnectorIdentifierModification;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;

/**
 * An gamification connectors listener that will broadcast changes to users.
 * This will allow to have dynamic UI and fresh updates without refreshing page.
 */
public class ConnectorIdentifierUpdateBroadcast extends Listener<ConnectorIdentifierModification, Long> {

  private final ConnectorsWebSocketService connectorsWebSocketService;

  public ConnectorIdentifierUpdateBroadcast(ConnectorsWebSocketService connectorsWebSocketService) {
    this.connectorsWebSocketService = connectorsWebSocketService;
  }

  @Override
  public void onEvent(Event<ConnectorIdentifierModification, Long> event) throws Exception {
    ConnectorIdentifierModification connectorIdentifierModification = event.getSource();
    connectorsWebSocketService.sendMessage(connectorIdentifierModification.getEventName(),
                                           connectorIdentifierModification.getConnectorName(),
                                           connectorIdentifierModification.getUsername(),
                                           connectorIdentifierModification.getConnectorIdentifier());
  }

}

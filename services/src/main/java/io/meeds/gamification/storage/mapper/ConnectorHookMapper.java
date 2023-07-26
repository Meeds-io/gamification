/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
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
package io.meeds.gamification.storage.mapper;

import io.meeds.gamification.entity.ConnectorHookEntity;
import io.meeds.gamification.model.ConnectorHook;
import io.meeds.gamification.utils.Utils;

public class ConnectorHookMapper {

  private ConnectorHookMapper() {
    // Class with static methods
  }

  public static ConnectorHookEntity toEntity(ConnectorHook connectorHook) {
    if (connectorHook == null) {
      return null;
    }
    ConnectorHookEntity connectorHookEntity = new ConnectorHookEntity();
    if (connectorHook.getHookRemoteId() > 0) {
      connectorHookEntity.setHookRemoteId(connectorHook.getHookRemoteId());
    }
    if (connectorHook.getOrganizationRemoteId() > 0) {
      connectorHookEntity.setOrganizationRemoteId(connectorHook.getOrganizationRemoteId());
    }
    if (connectorHook.getConnectorName() != null) {
      connectorHookEntity.setConnectorName(connectorHook.getConnectorName());
    }
    if (connectorHook.getName() != null) {
      connectorHookEntity.setName(connectorHook.getName());
    }
    if (connectorHook.getTitle() != null) {
      connectorHookEntity.setTitle(connectorHook.getTitle());
    }
    if (connectorHook.getDescription() != null) {
      connectorHookEntity.setDescription(connectorHook.getDescription());
    }
    if (connectorHook.getImageFileId() != 0) {
      connectorHookEntity.setImageFileId(connectorHook.getImageFileId());
    }
    if (connectorHook.getWatchedBy() != 0) {
      connectorHookEntity.setWatchedBy(connectorHook.getWatchedBy());
    }
    if (connectorHook.getSecret() != null) {
      connectorHookEntity.setSecret(connectorHook.getSecret());
    }
    return connectorHookEntity;
  }

  public static ConnectorHook fromEntity(ConnectorHookEntity connectorHookEntity) {
    if (connectorHookEntity == null) {
      return null;
    }
    return new ConnectorHook(connectorHookEntity.getHookRemoteId(),
                             connectorHookEntity.getOrganizationRemoteId(),
                             connectorHookEntity.getConnectorName(),
                             connectorHookEntity.getName(),
                             connectorHookEntity.getTitle(),
                             connectorHookEntity.getDescription(),
                             connectorHookEntity.getImageFileId(),
                             connectorHookEntity.getWatchDate() != null ? Utils.toSimpleDateFormat(connectorHookEntity.getWatchDate())
                                                                        : null,
                             connectorHookEntity.getWatchedBy(),
                             connectorHookEntity.getUpdatedDate() != null ? Utils.toSimpleDateFormat(connectorHookEntity.getUpdatedDate())
                                                                          : null,
                             null);
  }

}

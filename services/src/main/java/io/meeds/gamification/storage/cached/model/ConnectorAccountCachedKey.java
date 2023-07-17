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

package io.meeds.gamification.storage.cached.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConnectorAccountCachedKey implements Serializable {

  private static final long serialVersionUID = 2428564740732785395L;

  private String            connectorName;

  private String            remoteId;

  private long              userId;

  private boolean           object;

  public ConnectorAccountCachedKey(String connectorName, String remoteId) {
    this.connectorName = connectorName;
    this.remoteId = remoteId;
  }

  public ConnectorAccountCachedKey(String connectorName, long userId, boolean object) {
    this.connectorName = connectorName;
    this.userId = userId;
    this.object = object;
  }

}

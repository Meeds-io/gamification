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
package io.meeds.gamification.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.Data;
import org.exoplatform.commons.api.persistence.ExoEntity;

@Entity(name = "GamificationConnectorAccount")
@ExoEntity
@Table(name = "GAMIFICATION_CONNECTOR_ACCOUNTS")
@NamedQuery(
  name = "GamificationConnectorAccount.getConnectorAccountByNameAndUserId",
  query = "SELECT account FROM GamificationConnectorAccount account"
      + " WHERE account.connectorName = :connectorName"
      + " AND account.userId = :userId"
)
@NamedQuery(
  name = "GamificationConnectorAccount.getConnectorAccountByNameAndRemoteId",
  query = "SELECT account FROM GamificationConnectorAccount account"
      + " WHERE account.connectorName = :connectorName"
      + " AND account.remoteId = :remoteId"
)
@NamedQuery(
  name = "GamificationConnectorAccount.getConnectorRemoteId",
  query = "SELECT account.remoteId FROM GamificationConnectorAccount account"
      + " WHERE account.connectorName = :connectorName"
      + " AND account.userId = :userId"
)
@NamedQuery(
  name = "GamificationConnectorAccount.getAssociatedUserIdentityId",
  query = "SELECT account.userId FROM GamificationConnectorAccount account"
      + " WHERE account.connectorName = :connectorName"
      + " AND account.remoteId = :remoteId"
)
@Data
public class ConnectorAccountEntity implements Serializable {

  private static final long serialVersionUID = -8256376309023098995L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_CONNECTOR_ACCOUNTS_ID", sequenceName = "SEQ_GAMIFICATION_CONNECTOR_ACCOUNTS_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_CONNECTOR_ACCOUNTS_ID")
  @Column(name = "ID")
  private Long   id;

  @Column(name = "CONNECTOR_NAME")
  private String connectorName;

  @Column(name = "REMOTE_ID")
  private String remoteId;

  @Column(name = "USER_ID")
  private long   userId;

}

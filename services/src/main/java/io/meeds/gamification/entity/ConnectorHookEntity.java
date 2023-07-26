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
import java.util.Date;
import javax.persistence.*;
import org.exoplatform.commons.api.persistence.ExoEntity;
import lombok.Data;

@ExoEntity
@Entity(name = "ConnectorHook")
@Table(name = "GAMIFICATION_CONNECTOR_HOOKS")
@NamedQuery(
  name  = "GamificationConnectorHook.getConnectorHook", 
  query = "SELECT connectorHook FROM ConnectorHook connectorHook"
      + " WHERE connectorHook.connectorName = :connectorName" 
      + " AND connectorHook.name = :name")
@NamedQuery(
  name  = "GamificationConnectorHook.getConnectorHookIds", 
  query = "SELECT connectorHook.id FROM ConnectorHook connectorHook"
      + " WHERE connectorHook.connectorName = :connectorName")
@NamedQuery(
  name  = "GamificationConnectorHook.getConnectorHookSecret", 
  query = "SELECT connectorHook.secret FROM ConnectorHook connectorHook"
      + " WHERE connectorHook.connectorName = :connectorName" 
      + " AND connectorHook.organizationRemoteId = :organizationRemoteId")
@Data
public class ConnectorHookEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_CONNECTOR_HOOKS_ID", sequenceName = "SEQ_GAMIFICATION_CONNECTOR_HOOKS_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_CONNECTOR_HOOKS_ID")
  @Column(name = "ID")
  protected Long id;

  @Column(name = "HOOK_REMOTE_ID")
  protected Long hookRemoteId;

  @Column(name = "ORG_REMOTE_ID")
  protected Long organizationRemoteId;

  @Column(name = "CONNECTOR_NAME", nullable = false)
  private String connectorName;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "TITLE", nullable = false)
  private String title;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "IMAGE_FILE_ID")
  private Long   imageFileId;

  @Column(name = "WATCHED_DATE")
  private Date   watchDate = new Date();

  @Column(name = "UPDATED_DATE")
  private Date   updatedDate;

  @Column(name = "WATCHED_BY", nullable = false)
  private Long   watchedBy;

  @Column(name = "SECRET")
  private String secret;
}

/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * Base abstract class for entities which will hold definitions for created,
 * last modified by and created, last modified by date.
 */
@MappedSuperclass
@Data
public abstract class AbstractAuditingEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "CREATED_BY", nullable = false, length = 50, updatable = false)
  protected String          createdBy;

  @Column(name = "CREATED_DATE", nullable = false)
  protected Date            createdDate      = new Date();

  @Column(name = "LAST_MODIFIED_BY", length = 50)
  protected String          lastModifiedBy;

  @Column(name = "LAST_MODIFIED_DATE")
  protected Date            lastModifiedDate = new Date();

}

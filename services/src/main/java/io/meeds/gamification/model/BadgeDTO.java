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
package io.meeds.gamification.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class BadgeDTO implements Serializable {

  private static final long serialVersionUID = 5344793846683951097L;

  protected Long            id;

  protected String          title;

  protected String          description;

  protected int             neededScore;

  private ProgramDTO        program;

  private byte[]            icon;

  protected String          startValidityDate;

  protected String          endValidityDate;

  protected boolean         enabled;

  private String            createdBy;

  private String            createdDate;

  private String            lastModifiedBy;

  private String            lastModifiedDate;

  private long              iconFileId;

  private String            uploadId;

  protected boolean         deleted;

}

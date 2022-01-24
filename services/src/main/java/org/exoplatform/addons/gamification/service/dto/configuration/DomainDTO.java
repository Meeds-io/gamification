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
package org.exoplatform.addons.gamification.service.dto.configuration;

public class DomainDTO implements Cloneable {

  protected Long                  id;

  protected String                title;

  protected String                description;

  protected int                   priority;

  private String                  createdBy;

  private String                  createdDate;

  private String                  lastModifiedBy;

  private String                  lastModifiedDate;

  protected boolean               deleted;

  protected boolean               enabled;

  public DomainDTO() {  }

  public DomainDTO(Long id,
                   String title,
                   String description,
                   int priority,
                   String createdBy,
                   String createdDate,
                   String lastModifiedBy,
                   String lastModifiedDate,
                   boolean deleted,
                   boolean enabled) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.priority = priority;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.deleted = deleted;
    this.enabled = enabled;
  }

  @Override
  protected DomainDTO clone() { // NOSONAR
   return new DomainDTO(id,
           title,
           description,
           priority,
           createdBy,
           createdDate,
           lastModifiedBy,
           lastModifiedDate,
           deleted,
           enabled
           );
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public String getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(String lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}

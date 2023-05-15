/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.addons.gamification.service.dto.configuration;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO implements Serializable {

  private static final long serialVersionUID = 7423093905330790451L;

  protected Long            id;

  protected String          title;

  protected String          description;

  protected int             score;

  private ProgramDTO         program;

  protected boolean         enabled;

  protected boolean         deleted;

  private String            createdBy;

  private String            createdDate;

  private String            lastModifiedBy;

  private String            event;

  private String            lastModifiedDate;

  private String            startDate;

  private String            endDate;

  private EntityType        type;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public EntityType getType() {
    return type;
  }

  public void setType(EntityType type) {
    this.type = type;
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

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public ProgramDTO getProgram() {
    return program;
  }

  public void setProgram(ProgramDTO program) {
    this.program = program;
  }

  public long getAudienceId() {
    return program == null ? 0 : program.getAudienceId();
  }

  public long getDomainId() {
    return program == null ? 0 : program.getId();
  }

  public Set<Long> getManagers() {
    return program == null ? Collections.emptySet() : program.getOwners();
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

}

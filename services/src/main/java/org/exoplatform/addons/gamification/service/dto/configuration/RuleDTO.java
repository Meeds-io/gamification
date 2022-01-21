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

import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;

import java.io.Serializable;
import java.util.List;

public class RuleDTO implements Serializable {

  protected Long    id;

  protected String  title;

  protected String  description;

  protected int     score;

  protected String  area;

  private DomainDTO domainDTO;

  protected boolean enabled;

  protected boolean deleted;

  private String    createdBy;

  private String    createdDate;

  private String    lastModifiedBy;

  private String    event;

  private String    lastModifiedDate;

  private long      audience;

  private String    startDate;

  private String    endDate;

  private TypeRule   type;

  private List<Long> managers;

  public RuleDTO() {

  }

  public List<Long> getManagers() {
    return managers;
  }

  public void setManagers(List<Long> managers) {
    this.managers = managers;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TypeRule getType() {
    return type;
  }

  public void setType(TypeRule type) {
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

  public String getArea() {
    if (this.domainDTO != null)
      return this.domainDTO.getTitle();
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public DomainDTO getDomainDTO() {
    return domainDTO;
  }

  public void setDomainDTO(DomainDTO domainDTO) {
    this.domainDTO = domainDTO;
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

  public long getAudience() {
    return audience;
  }

  public void setAudience(long audience) {
    this.audience = audience;
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

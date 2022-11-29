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
package org.exoplatform.addons.gamification.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class BadgeDTO implements Serializable {

  private static final long       serialVersionUID = -4216991163673243263L;

  private static SimpleDateFormat formatter        = new SimpleDateFormat("yyyy-MM-dd");

  protected Long                  id;

  protected String                title;

  protected String                description;

  protected int                   neededScore;

  protected String                domain;

  private DomainDTO               domainDTO;

  // protected String icon;

  private byte[]                  icon;

  protected String                startValidityDate;

  protected String                endValidityDate;

  protected boolean               enabled;

  private String                  createdBy;

  private String                  createdDate;

  private String                  lastModifiedBy;

  private String                  lastModifiedDate;

  private long                    iconFileId;

  private String                  uploadId;

  protected boolean               deleted;

  public BadgeDTO() {
  }

  public String getUploadId() {
    return uploadId;
  }

  public void setUploadId(String uploadId) {
    this.uploadId = uploadId;
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

  public int getNeededScore() {
    return neededScore;
  }

  public void setNeededScore(int neededScore) {
    this.neededScore = neededScore;
  }

  public byte[] getIcon() {
    return icon;
  }

  public void setIcon(byte[] icon) {
    this.icon = icon;
  }

  public long getIconFileId() {
    return iconFileId;
  }

  public void setIconFileId(long iconFileId) {
    this.iconFileId = iconFileId;
  }

  // TODO : it seems that eXo ws module doesn't supoprt Date's serialization, thus
  // I have to use Tring instead of java.util.Date

  public String getStartValidityDate() {
    return startValidityDate;
  }

  public void setStartValidityDate(String startValidityDate) {
    this.startValidityDate = startValidityDate;
  }

  public String getEndValidityDate() {
    return endValidityDate;
  }

  public void setEndValidityDate(String endValidityDate) {
    this.endValidityDate = endValidityDate;
  }

  public boolean isEnabled() {
    return enabled;
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

  public String getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(String lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public String getDomain() {
    if (this.domainDTO != null)
      return this.domainDTO.getTitle();
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public DomainDTO getDomainDTO() {
    return domainDTO;
  }

  public void setDomainDTO(DomainDTO domainDTO) {
    this.domainDTO = domainDTO;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
    return "BadgeDTO{" + "title='" + title + '\'' + ", neededScore='" + neededScore + '\'' + ", startValidityDate='"
        + startValidityDate + '\'' + ", endValidityDate='" + endValidityDate + '\'' + ", description='" + description + '\''
        + ", icon='" + icon + '\'' + ", zone='" + domain + '\'' + ", createdBy=" + createdBy + ", createdDate=" + createdDate
        + ", lastModifiedBy='" + lastModifiedBy + '\'' + ", lastModifiedDate=" + lastModifiedDate + ", deleted=" + deleted
        + ", enabled=" + enabled + "}";
  }
}

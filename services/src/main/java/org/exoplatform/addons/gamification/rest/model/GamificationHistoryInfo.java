/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2023 Meeds Association
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

package org.exoplatform.addons.gamification.rest.model;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;

public class GamificationHistoryInfo {
  String         socialId;

  boolean        isSpace;

  String         avatarUrl;

  String         remoteId;

  String         fullname;

  String         profileUrl;

  String         createdDate;

  long           globalScore;

  String         actionTitle;

  DomainDTO      domainDTO;

  String         context;

  long           actionScore;

  private int    loadCapacity = 10;

  private String receiver;

  private String objectId;

  public int getLoadCapacity() {
    return loadCapacity;
  }

  public void setLoadCapacity(String loadCapacity) {
    this.loadCapacity = Integer.parseInt(loadCapacity);
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getRemoteId() {
    return remoteId;
  }

  public void setRemoteId(String remoteId) {
    this.remoteId = remoteId;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getProfileUrl() {
    return profileUrl;
  }

  public void setProfileUrl(String profileUrl) {
    this.profileUrl = profileUrl;
  }

  public String getSocialId() {
    return socialId;
  }

  public void setSocialId(String socialId) {
    this.socialId = socialId;
  }

  public boolean isSpace() {
    return isSpace;
  }

  public void setSpace(boolean isSpace) {
    this.isSpace = isSpace;
  }

  public long getGlobalScore() {
    return globalScore;
  }

  public void setGlobalScore(long globalScore) {
    this.globalScore = globalScore;
  }

  public String getActionTitle() {
    return actionTitle;
  }

  public void setActionTitle(String actionTitle) {
    this.actionTitle = actionTitle;
  }

  public DomainDTO getDomainDTO() {
    return domainDTO;
  }

  public void setDomain(DomainDTO domainDTO) {
    this.domainDTO = domainDTO;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public long getActionScore() {
    return actionScore;
  }

  public void setActionScore(long actionScore) {
    this.actionScore = actionScore;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }
}

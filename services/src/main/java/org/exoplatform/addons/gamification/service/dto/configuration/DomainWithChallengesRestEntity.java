/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

import java.util.List;

public class DomainWithChallengesRestEntity extends DomainDTO {

  private List<ChallengeRestEntity> challenges;

  private int                       challengesOffset;

  private int                       challengesLimit;

  private int                       challengesSize;

  public DomainWithChallengesRestEntity(DomainDTO domain) {
    super(domain.getId(),
          domain.getTitle(),
          domain.getDescription(),
          domain.getPriority(),
          domain.getCreatedBy(),
          domain.getCreatedDate(),
          domain.getLastModifiedBy(),
          domain.getLastModifiedDate(),
          domain.isDeleted(),
          domain.isEnabled());
  }

  public List<ChallengeRestEntity> getChallenges() {
    return challenges;
  }

  public void setChallenges(List<ChallengeRestEntity> challenges) {
    this.challenges = challenges;
  }

  public int getChallengesOffset() {
    return challengesOffset;
  }

  public void setChallengesOffset(int challengesOffset) {
    this.challengesOffset = challengesOffset;
  }

  public int getChallengesLimit() {
    return challengesLimit;
  }

  public void setChallengesLimit(int challengesLimit) {
    this.challengesLimit = challengesLimit;
  }

  public int getChallengesSize() {
    return challengesSize;
  }

  public void setChallengesSize(int challengesSize) {
    this.challengesSize = challengesSize;
  }

}

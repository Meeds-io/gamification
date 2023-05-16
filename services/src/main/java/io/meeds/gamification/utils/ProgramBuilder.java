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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.meeds.gamification.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.ProgramDTO;

public class ProgramBuilder {

  private ProgramBuilder() {
    // Class with static methods
  }

  public static List<ProgramDTO> fromEntities(RuleDAO ruleDAO,
                                             List<ProgramEntity> domains) {
    return domains.stream()
                  .filter(Objects::nonNull)
                  .map(entity -> fromEntity(ruleDAO, entity))
                  .toList();
  }

  public static ProgramEntity toEntity(ProgramDTO program) {
    if (program == null) {
      return null;
    }
    ProgramEntity domain = new ProgramEntity();
    domain.setId(program.getId());
    domain.setTitle(program.getTitle());
    domain.setDescription(program.getDescription());
    domain.setCreatedBy(program.getCreatedBy());
    domain.setLastModifiedBy(program.getLastModifiedBy());
    domain.setDeleted(program.isDeleted());
    domain.setEnabled(program.isEnabled());
    if (program.getAudienceId() > 0) {
      domain.setAudienceId(program.getAudienceId());
    }
    if (program.getCreatedDate() != null) {
      domain.setCreatedDate(Utils.parseRFC3339Date(program.getCreatedDate()));
    }
    domain.setLastModifiedDate(Utils.parseRFC3339Date(program.getLastModifiedDate()));
    domain.setPriority(program.getPriority());
    domain.setBudget(program.getBudget());
    domain.setCoverFileId(program.getCoverFileId());
    if (StringUtils.isBlank(program.getType())) {
      domain.setType(EntityType.AUTOMATIC);
    } else {
      domain.setType(EntityType.valueOf(program.getType()));
    }
    if (program.getOwners() != null) {
      domain.setOwners(program.getOwners());
    } else {
      domain.setOwners(Collections.emptySet());
    }
    return domain;
  }

  public static ProgramDTO fromEntity(RuleDAO ruleDAO, ProgramEntity domainEntity) {
    if (domainEntity == null) {
      return null;
    }
    long lastUpdateTime = domainEntity.getLastModifiedDate() == null ? 0 : domainEntity.getLastModifiedDate().getTime();
    String coverUrl = Utils.buildAttachmentUrl(String.valueOf(domainEntity.getId()),
                                               lastUpdateTime,
                                               Utils.TYPE,
                                               domainEntity.getCoverFileId() == 0);
    ProgramDTO program = new ProgramDTO();
    program.setId(domainEntity.getId());
    program.setTitle(domainEntity.getTitle());
    program.setDescription(domainEntity.getDescription());
    if (domainEntity.getAudienceId() != null) {
      program.setAudienceId(domainEntity.getAudienceId());
    }
    program.setCreatedBy(domainEntity.getCreatedBy());
    program.setCreatedDate(Utils.toRFC3339Date(domainEntity.getCreatedDate()));
    program.setLastModifiedBy(domainEntity.getLastModifiedBy());
    program.setLastModifiedDate(Utils.toRFC3339Date(domainEntity.getLastModifiedDate()));
    program.setDeleted(domainEntity.isDeleted());
    program.setEnabled(domainEntity.isEnabled());
    program.setBudget(domainEntity.getBudget());
    program.setType(domainEntity.getType().name());
    program.setCoverFileId(domainEntity.getCoverFileId());
    program.setCoverUrl(coverUrl);
    program.setOwners(domainEntity.getOwners());
    program.setRulesTotalScore(getRulesTotalScoreByDomain(ruleDAO, domainEntity.getId()));
    return program;
  }

  public static long getRulesTotalScoreByDomain(RuleDAO ruleDAO, long domainId) {
    if (domainId <= 0) {
      return 0;
    }
    return ruleDAO.getRulesTotalScoreByDomain(domainId);
  }

}

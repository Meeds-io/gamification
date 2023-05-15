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
package org.exoplatform.addons.gamification.service.mapper;

import java.util.List;
import java.util.Objects;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.storage.ProgramStorage;
import org.exoplatform.addons.gamification.utils.Utils;

public class RuleBuilder {

  private RuleBuilder() {
    // Class with static methods
  }

  public static List<RuleDTO> rulesToRuleDTOs(ProgramStorage domainStorage,
                                              List<RuleEntity> rules) {
    return rules.stream()
                .filter(Objects::nonNull)
                .map(entity -> ruleToRuleDTO(domainStorage, entity))
                .toList();
  }

  public static RuleEntity ruleDTOToRule(RuleDTO ruleDTO) {
    if (ruleDTO == null) {
      return null;
    }
    RuleEntity rule = new RuleEntity();
    rule.setId(ruleDTO.getId());
    rule.setScore(ruleDTO.getScore());
    rule.setTitle(ruleDTO.getTitle());
    rule.setDescription(ruleDTO.getDescription());
    rule.setEnabled(ruleDTO.isEnabled());
    rule.setDeleted(ruleDTO.isDeleted());
    rule.setEvent(ruleDTO.getEvent());
    rule.setCreatedBy(ruleDTO.getCreatedBy());
    if (ruleDTO.getStartDate() != null) {
      rule.setStartDate(Utils.parseSimpleDate(ruleDTO.getStartDate()));
    }
    if (ruleDTO.getEndDate() != null) {
      rule.setEndDate(Utils.parseSimpleDate(ruleDTO.getEndDate()));
    }
    if (ruleDTO.getType() != null) {
      rule.setType(ruleDTO.getType());
    } else {
      rule.setType(EntityType.AUTOMATIC);
    }
    if (ruleDTO.getCreatedDate() != null) {
      rule.setCreatedDate(Utils.parseRFC3339Date(ruleDTO.getCreatedDate()));
    }
    if (ruleDTO.getLastModifiedDate() != null) {
      rule.setCreatedDate(Utils.parseRFC3339Date(ruleDTO.getCreatedDate()));
    }
    rule.setLastModifiedBy(ruleDTO.getLastModifiedBy());
    rule.setDomainEntity(ProgramBuilder.toEntity(ruleDTO.getProgram()));
    return rule;
  }

  public static RuleDTO ruleToRuleDTO(ProgramStorage domainStorage, RuleEntity ruleEntity) {
    if (ruleEntity == null) {
      return null;
    } else {
      RuleDTO rule = new RuleDTO();
      rule.setId(ruleEntity.getId());
      rule.setScore(ruleEntity.getScore());
      rule.setTitle(ruleEntity.getTitle());
      rule.setDescription(ruleEntity.getDescription());
      rule.setEnabled(ruleEntity.isEnabled());
      rule.setDeleted(ruleEntity.isDeleted());
      rule.setEvent(ruleEntity.getEvent());
      rule.setCreatedBy(ruleEntity.getCreatedBy());
      if (ruleEntity.getStartDate() != null) {
        rule.setStartDate(Utils.toSimpleDateFormat(ruleEntity.getStartDate()));
      }
      if (ruleEntity.getEndDate() != null) {
        rule.setEndDate(Utils.toSimpleDateFormat(ruleEntity.getEndDate()));
      }
      if (ruleEntity.getType() != null) {
        rule.setType(ruleEntity.getType());
      } else {
        rule.setType(EntityType.AUTOMATIC);
      }
      rule.setCreatedDate(Utils.toRFC3339Date(ruleEntity.getCreatedDate()));
      rule.setLastModifiedDate(Utils.toRFC3339Date(ruleEntity.getLastModifiedDate()));
      rule.setLastModifiedBy(ruleEntity.getLastModifiedBy());
      rule.setProgram(ruleEntity.getDomainEntity() == null ? null
                                                             : domainStorage.getDomainById(ruleEntity.getDomainEntity().getId()));
      return rule;
    }
  }
}

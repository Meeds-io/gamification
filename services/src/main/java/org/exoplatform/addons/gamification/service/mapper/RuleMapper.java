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
package org.exoplatform.addons.gamification.service.mapper;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RuleMapper {

  private DomainMapper     domainMapper;

  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

  public RuleMapper(DomainMapper domainMapper) {
    this.domainMapper = domainMapper;
  }

  public List<RuleDTO> rulesToRuleDTOs(List<RuleEntity> rules) {
    return rules.stream().filter(Objects::nonNull).map(this::ruleToRuleDTO).collect(Collectors.toList());
  }

  public RuleEntity ruleDTOToRule(RuleDTO ruleDTO) {
    if (ruleDTO == null) {
      return null;
    } else {
      RuleEntity rule = new RuleEntity();
      rule.setId(ruleDTO.getId());
      rule.setScore(ruleDTO.getScore());
      rule.setTitle(ruleDTO.getTitle());
      rule.setDescription(ruleDTO.getDescription());
      rule.setArea(ruleDTO.getArea());
      rule.setEnabled(ruleDTO.isEnabled());
      rule.setDeleted(ruleDTO.isDeleted());
      rule.setEvent(ruleDTO.getEvent());
      rule.setCreatedBy(ruleDTO.getCreatedBy());
      if (ruleDTO.getAudience() > 0) {
        rule.setAudience(ruleDTO.getAudience());
      }
      if (ruleDTO.getEndDate() != null) {
        rule.setStartDate(Utils.parseRFC3339Date(ruleDTO.getStartDate()));
      }
      if (ruleDTO.getStartDate() != null) {
        rule.setEndDate(Utils.parseRFC3339Date(ruleDTO.getEndDate()));
      }
      if (ruleDTO.getType() != null) {
        rule.setType(ruleDTO.getType());
      } else {
        rule.setType(TypeRule.AUTOMATIC);
      }
      try {
        rule.setCreatedDate(formatter.parse(ruleDTO.getCreatedDate()));
        rule.setLastModifiedDate(formatter.parse(ruleDTO.getLastModifiedDate()));
      } catch (Exception e) {
        // ignore
      }
      rule.setLastModifiedBy(ruleDTO.getLastModifiedBy());
      rule.setDomainEntity(domainMapper.domainDTOToDomain(ruleDTO.getDomainDTO()));

      return rule;
    }
  }

  public RuleDTO ruleToRuleDTO(RuleEntity ruleEntity) {
    if (ruleEntity == null) {
      return null;
    } else {
      RuleDTO rule = new RuleDTO();
      rule.setId(ruleEntity.getId());
      rule.setScore(ruleEntity.getScore());
      rule.setTitle(ruleEntity.getTitle());
      rule.setDescription(ruleEntity.getDescription());
      rule.setArea(ruleEntity.getArea());
      rule.setEnabled(ruleEntity.isEnabled());
      rule.setDeleted(ruleEntity.isDeleted());
      rule.setEvent(ruleEntity.getEvent());
      rule.setCreatedBy(ruleEntity.getCreatedBy());
      if (ruleEntity.getAudience() != null) {
        rule.setAudience(ruleEntity.getAudience());
      }
      if (ruleEntity.getEndDate() != null) {
        rule.setStartDate(Utils.toRFC3339Date(ruleEntity.getStartDate()));
      }
      if (ruleEntity.getStartDate() != null) {
        rule.setEndDate(Utils.toRFC3339Date(ruleEntity.getEndDate()));
      }
      if (ruleEntity.getType() != null) {
        rule.setType(ruleEntity.getType());
      } else {
        rule.setType(TypeRule.AUTOMATIC);
      }
      try {
        rule.setCreatedDate(formatter.format(ruleEntity.getCreatedDate()));
        rule.setLastModifiedDate(formatter.format(ruleEntity.getLastModifiedDate()));
      } catch (Exception e) {
        // ignore
      }
      rule.setLastModifiedBy(ruleEntity.getLastModifiedBy());
      rule.setDomainDTO(domainMapper.domainEntityToDomainDTO(ruleEntity.getDomainEntity()));

      return rule;
    }
  }

  public List<RuleEntity> ruleDTOsToRules(List<RuleDTO> ruleDTOs) {
    return ruleDTOs.stream().filter(Objects::nonNull).map(this::ruleDTOToRule).collect(Collectors.toList());
  }

  public RuleEntity ruleFromId(Long id) {
    if (id == null) {
      return null;
    }
    RuleEntity rule = new RuleEntity();
    rule.setId(id);
    return rule;
  }

}

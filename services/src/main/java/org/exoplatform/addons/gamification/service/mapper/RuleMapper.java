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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class RuleMapper {

    private DomainMapper domainMapper;

    public RuleMapper(DomainMapper domainMapper) {
        this.domainMapper=domainMapper;
    }

    public RuleDTO ruleToRuleDTO(RuleEntity rule) {
        return new RuleDTO(rule);
    }

    public List<RuleDTO> rulesToRoleDTOs(List<RuleEntity> rules) {
        return rules.stream()
                .filter(Objects::nonNull)
                .map(this::ruleToRuleDTO)
                .collect(Collectors.toList());
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
            rule.setLastModifiedBy(ruleDTO.getLastModifiedBy());
            rule.setLastModifiedDate(ruleDTO.getLastModifiedDate());
            rule.setDomainEntity(domainMapper.domainDTOToDomain(ruleDTO.getDomainDTO()));

            return rule;
        }
    }

    public List<RuleEntity> ruleDTOsToRules(List<RuleDTO> ruleDTOs) {
        return ruleDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::ruleDTOToRule)
                .collect(Collectors.toList());
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

/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.rest.model.RealizationValidityContext;
import io.meeds.gamification.service.RealizationComputingService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

@Service
public class RealizationComputingServiceImpl implements RealizationComputingService {

  private RuleService        ruleService;

  private RealizationService realizationService;

  private IdentityManager    identityManager;

  public RealizationComputingServiceImpl(RuleService ruleService,
                                         RealizationService realizationService,
                                         IdentityManager identityManager) {
    this.ruleService = ruleService;
    this.realizationService = realizationService;
    this.identityManager = identityManager;
  }

  @Override
  public List<RuleDTO> getLockingRules(RuleFilter ruleFilter,
                                       String username,
                                       int offset,
                                       int limit) {
    Map<Long, RuleDTO> filteredRules = new LinkedHashMap<>();
    // Rules valid but have non-achieved prerequesites by current user
    List<RuleDTO> matchingParentRules = new ArrayList<>();
    Map<Long, RealizationValidityContext> realizationValidityContexts = new HashMap<>();
    int pageOffset = 0;
    int pageSize = 50;
    int rulesSize = ruleService.countRules(ruleFilter, username);
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    long identityId = identity.getIdentityId();
    while (pageOffset < rulesSize) {
      List<RuleDTO> rules = ruleService.getRules(ruleFilter, username, pageOffset, pageSize);
      rules.forEach(r -> {
        filteredRules.put(r.getId(), r);
        // Retrieve rules valid but having prerequisites not achieved yet
        RealizationValidityContext realizationValidityContext = getRealizationValidityContext(r,
                                                                                              identityId,
                                                                                              realizationValidityContexts);
        if (realizationValidityContext.isValidButLocked()
            && CollectionUtils.isNotEmpty(r.getPrerequisiteRuleIds())) {
          matchingParentRules.add(r);
        }
      });
      pageOffset += pageSize;
    }

    int maxItems = offset + limit;
    List<Long> lockingRuleIds = new ArrayList<>();
    matchingParentRules.forEach(r -> {
      Set<Long> prerequisiteRuleIds = r.getPrerequisiteRuleIds();
      int remainingLimit = maxItems - lockingRuleIds.size();
      if (remainingLimit > 0 && CollectionUtils.isNotEmpty(prerequisiteRuleIds)) {
        RealizationValidityContext validityContext = getRealizationValidityContext(r, identityId, realizationValidityContexts);
        validityContext.getValidPrerequisites()
                       .entrySet()
                       .stream()
                       // Not achieved prerequisite yet
                       .filter(e -> !e.getValue().booleanValue())
                       .map(Entry<String, Boolean>::getKey)
                       .map(Long::parseLong)
                       .filter(filteredRules::containsKey)
                       .map(filteredRules::get)
                       // Check if prerequisite can be done
                       .filter(prerequesiteRule -> getRealizationValidityContext(prerequesiteRule,
                                                                                 identityId,
                                                                                 realizationValidityContexts).isValid())
                       .limit(remainingLimit)
                       .map(RuleDTO::getId)
                       // Prerequisite to do first
                       .forEach(lockingRuleIds::add);
      }
    });
    // Use Same Sort as Matched rules
    return filteredRules.values()
                        .stream()
                        .filter(r -> lockingRuleIds.contains(r.getId()))
                        .skip(offset)
                        .limit(limit)
                        .toList();
  }

  private RealizationValidityContext getRealizationValidityContext(RuleDTO rule,
                                                                   long identityId,
                                                                   Map<Long, RealizationValidityContext> realizationValidityContexts) {
    return realizationValidityContexts.computeIfAbsent(rule.getId(),
                                                       id -> realizationService.getRealizationValidityContext(rule,
                                                                                                              identityId));
  }

}

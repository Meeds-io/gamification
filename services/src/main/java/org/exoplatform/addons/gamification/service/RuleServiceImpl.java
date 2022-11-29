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
package org.exoplatform.addons.gamification.service;

import static org.exoplatform.addons.gamification.utils.Utils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.model.DomainDTO;
import org.exoplatform.addons.gamification.model.RuleDTO;
import org.exoplatform.addons.gamification.model.RuleFilter;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;

public class RuleServiceImpl implements RuleService {

  private final RuleStorage      ruleStorage;

  private final ListenerService  listenerService;

  public RuleServiceImpl(RuleStorage ruleStorage,  ListenerService listenerService) {
    this.ruleStorage = ruleStorage;
    this.listenerService = listenerService;
  }

  public RuleDTO findEnableRuleByTitle(String ruleTitle) throws IllegalArgumentException {
    RuleDTO rule = findRuleByTitle(ruleTitle);
    return rule != null && rule.isEnabled() ? rule : null;
  }

  public RuleDTO findRuleById(long id) throws IllegalArgumentException {
    if (id == 0) {
      throw new IllegalArgumentException("Rule id is mandatory");
    }
    return ruleStorage.findRuleById(id);
  }

  public List<RuleDTO> findEnabledRulesByEvent(String event) throws IllegalArgumentException {
    if (StringUtils.isBlank(event)) {
      throw new IllegalArgumentException("Rule event is mandatory");
    }
    return ruleStorage.findEnabledRulesByEvent(event);
  }

  public RuleDTO findRuleByTitle(String ruleTitle) throws IllegalArgumentException {
    if (StringUtils.isBlank(ruleTitle)) {
      throw new IllegalArgumentException("rule title is mandatory");
    }
    return ruleStorage.findRuleByTitle(ruleTitle);
  }

  public RuleDTO findRuleByEventAndDomain(String ruleTitle, long domainId) throws IllegalArgumentException {
    if (StringUtils.isBlank(ruleTitle)) {
      throw new IllegalArgumentException("Rule title is mandatory");
    }
    if (domainId < 0) {
      throw new IllegalArgumentException("Domain id must be positive");
    }
    return ruleStorage.findRuleByEventAndDomain(ruleTitle, domainId);
  }

  public List<RuleDTO> findAllRules() {// NOSONAR
    return ruleStorage.findAllRules();
  }

  @Override
  public List<RuleDTO> findAllRules(int offset, int limit) {
    List<Long> rulesIds = ruleStorage.findAllRulesIds(offset, limit);
    List<RuleDTO> rules = new ArrayList<>();
    for (Long ruleId : rulesIds) {
      RuleDTO rule = findRuleById(ruleId);
      if (rule.isDeleted()) {
        continue;
      }
      rules.add(rule);
    }
    return rules;
  }

  @Override
  public List<RuleDTO> getRulesByFilter(RuleFilter ruleFilter, int offset, int limit) {
    List<Long> rulesIds = ruleStorage.findRulesIdsByFilter(ruleFilter, offset, limit);
    List<RuleDTO> rules = new ArrayList<>();
    ruleStorage.findRulesIdsByFilter(ruleFilter, offset, limit);

    for (Long ruleId : rulesIds) {
      RuleDTO rule = findRuleById(ruleId);
      if (rule.isDeleted()) {
        continue;
      }
      rules.add(rule);
    }
    return rules;
  }

  @Override
  public int countAllRules(RuleFilter ruleFilter) {
    return ruleStorage.countRulesByFilter(ruleFilter);
  }

  public List<RuleDTO> getActiveRules() {
    return ruleStorage.getActiveRules();
  }

  public List<RuleDTO> getAllRulesByDomain(String domain) throws IllegalArgumentException {
    if (StringUtils.isBlank(domain)) {
      throw new IllegalArgumentException("rule domain is mandatory");
    }
    return ruleStorage.getAllRulesByDomain(domain);
  }

  public List<RuleDTO> getAllRulesWithNullDomain() {
    return ruleStorage.getAllRulesWithNullDomain();
  }

  public List<String> getAllEvents() {
    return ruleStorage.getAllEvents();
  }

  public List<String> getDomainListFromRules() {
    return ruleStorage.getDomainListFromRules();
  }

  public long getRulesTotalScoreByDomain(long domainId) {
    return ruleStorage.getRulesTotalScoreByDomain(domainId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RuleDTO deleteRuleById(Long ruleId, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (username == null) {
      throw new IllegalArgumentException("Username is mandatory");
    }
    if (ruleId == null || ruleId <= 0) {
      throw new IllegalArgumentException("ruleId must be positive");
    }
    RuleDTO ruleDTO = ruleStorage.findRuleById(ruleId);
    if (ruleDTO == null) {
      throw new ObjectNotFoundException("Rule with id " + ruleId + " is not found");
    }
    if (!canManageRule(username)) {
      throw new IllegalAccessException("The user is not authorized to delete a rule");
    }
    ruleDTO = ruleStorage.deleteRuleById(ruleId, username);
    Utils.broadcastEvent(listenerService, POST_DELETE_RULE_EVENT, this, ruleDTO);
    return ruleDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RuleDTO createRule(RuleDTO ruleDTO, String username) throws IllegalAccessException {
    if (ruleDTO == null) {
      throw new IllegalArgumentException("rule object is mandatory");
    }
    if (username == null) {
      throw new IllegalArgumentException("Username is mandatory");
    }
    if (ruleDTO.getId() != null) {
      throw new IllegalArgumentException("domain id must be equal to 0");
    }
    if (!canManageRule(username)) {
      throw new IllegalAccessException("The user is not authorized to create a rule");
    }
    ruleDTO.setCreatedBy(username);
    ruleDTO.setLastModifiedBy(username);
    DomainDTO domainDTO = Utils.getDomainByTitle(ruleDTO.getArea());
    ruleDTO.setDomainDTO(domainDTO);

    return createRule(ruleDTO);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RuleDTO createRule(RuleDTO ruleDTO) {
    long domainId = ruleDTO.getDomainDTO().getId();
    RuleDTO oldRule = ruleStorage.findRuleByEventAndDomain(ruleDTO.getEvent(), domainId);
    if (oldRule != null) {
      throw new EntityExistsException("Rule with same event and domain already exist");
    }
    ruleDTO = ruleStorage.saveRule(ruleDTO);
    Utils.broadcastEvent(listenerService, POST_CREATE_RULE_EVENT, this, ruleDTO.getId());
    return ruleDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RuleDTO updateRule(RuleDTO ruleDTO, String username) throws ObjectNotFoundException, IllegalAccessException {
    if (username == null) {
      throw new IllegalArgumentException("Username is mandatory");
    }
    RuleDTO oldRule = ruleStorage.findRuleById(ruleDTO.getId());
    if (oldRule == null) {
      throw new ObjectNotFoundException("Rule does not exist");
    }
    DomainDTO domainDTO = ruleDTO.getDomainDTO();
    if (domainDTO != null) {
      long domainId = ruleDTO.getDomainDTO().getId();
      RuleDTO existRule = ruleStorage.findRuleByEventAndDomain(ruleDTO.getEvent(), domainId);
      if (existRule != null && !existRule.getId().equals(oldRule.getId())) {
        throw new EntityExistsException("Rule with same event and domain already exist");
      }
    }
    if (!canManageRule(username)) {
      throw new IllegalAccessException("The user is not authorized to update a rule");
    }
    ruleDTO.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    ruleDTO.setCreatedBy(username);
    ruleDTO.setLastModifiedBy(username);
    ruleDTO = ruleStorage.saveRule(ruleDTO);

    Utils.broadcastEvent(listenerService, POST_UPDATE_RULE_EVENT, this, ruleDTO.getId());

    return ruleDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canManageRule(String username) {
    return Utils.isAdministrator(username);
  }
}

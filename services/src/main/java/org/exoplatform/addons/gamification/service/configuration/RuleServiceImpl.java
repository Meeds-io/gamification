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
package org.exoplatform.addons.gamification.service.configuration;

import static org.exoplatform.addons.gamification.GamificationConstant.GAMIFICATION_DEFAULT_DATA_PREFIX;
import static org.exoplatform.addons.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class RuleServiceImpl implements RuleService {

  private static final Log LOG                = ExoLogger.getExoLogger(RuleServiceImpl.class);


  private RuleStorage ruleStorage;

  private ListenerService     listenerService;

  public RuleServiceImpl(RuleStorage ruleStorage,  ListenerService listenerService) {
    this.ruleStorage = ruleStorage;
    this.listenerService = listenerService;

  }

  public RuleDTO findEnableRuleByTitle(String ruleTitle) throws IllegalArgumentException {
    RuleDTO rule = findRuleByTitle(ruleTitle);
    return rule != null && rule.isEnabled() ? rule : null;
  }

  public RuleDTO findRuleById(Long id) throws IllegalArgumentException {
    if (id == null) {
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

  public RuleDTO findRuleByEventAndDomain(String ruleTitle, String domain) throws IllegalArgumentException {
    if (StringUtils.isBlank(ruleTitle)) {
      throw new IllegalArgumentException("Rule title is mandatory");
    }
    if (StringUtils.isBlank(domain)) {
      throw new IllegalArgumentException("Rule domain is mandatory");
    }
    return ruleStorage.findRuleByEventAndDomain(ruleTitle, domain);
  }

  public List<RuleDTO> findAllRules() {
    return ruleStorage.findAllRules();
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

  public void deleteRule(Long id) throws IllegalArgumentException, ObjectNotFoundException {
    if (id == null) {
      throw new IllegalArgumentException("rule id is mandatory");
    }
    ruleStorage.deleteRule(id);
    try {
      listenerService.broadcast(POST_UPDATE_RULE_EVENT, this,id);
    } catch (Exception e) {
      LOG.error("Error broadcasting rule with id {} update event", id, e);
    }
  }

  public RuleDTO addRule(RuleDTO ruleDTO) throws IllegalArgumentException, EntityExistsException {
    if (ruleDTO == null) {
      throw new IllegalArgumentException("rule is mandatory");
    }
    RuleDTO oldRule = ruleStorage.findRuleByEventAndDomain(ruleDTO.getEvent(), ruleDTO.getArea());
    if (oldRule != null) {
      throw new EntityExistsException("Rule with same event and domain already exist");
    }
    ruleDTO = ruleStorage.saveRule(ruleDTO);
    try {
      listenerService.broadcast(POST_CREATE_RULE_EVENT, this, ruleDTO.getId());
    } catch (Exception e) {
      LOG.error("Error broadcasting rule with id {} creation event", ruleDTO.getId(), e);
    }
    return ruleDTO;
  }

  public RuleDTO updateRule(RuleDTO ruleDTO) throws ObjectNotFoundException {
    RuleDTO oldRule = ruleStorage.findRuleByEventAndDomain(ruleDTO.getEvent(), ruleDTO.getArea());
    if (oldRule == null) {
      throw new ObjectNotFoundException("Rule does not exist");
    }
    if (!ruleDTO.getTitle().startsWith(GAMIFICATION_DEFAULT_DATA_PREFIX)) {
      ruleDTO.setTitle(ruleDTO.getEvent() + "_" + ruleDTO.getArea());
    }
    ruleDTO.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    ruleDTO = ruleStorage.saveRule(ruleDTO);
    try {
      listenerService.broadcast(POST_UPDATE_RULE_EVENT, this, ruleDTO.getId());
    } catch (Exception e) {
      LOG.error("Error broadcasting rule with id {} update event", ruleDTO.getId(), e);
    }
    return ruleDTO;
  }
}

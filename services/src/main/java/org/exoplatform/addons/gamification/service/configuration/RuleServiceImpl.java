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

import static org.exoplatform.addons.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_DELETE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;

public class RuleServiceImpl implements RuleService {

  private static final String   USERNAME_IS_MANDATORY_MESSAGE = "Username is mandatory";

  private final ProgramService   programService;

  private final RuleStorage     ruleStorage;

  private final ListenerService listenerService;

  public RuleServiceImpl(ProgramService programService,
                         RuleStorage ruleStorage,
                         ListenerService listenerService) {
    this.programService = programService;
    this.listenerService = listenerService;
    this.ruleStorage = ruleStorage;
  }

  @Override
  public RuleDTO findRuleById(long id) throws IllegalArgumentException {
    if (id == 0) {
      throw new IllegalArgumentException("Rule id is mandatory");
    }
    RuleDTO rule = ruleStorage.findRuleById(id);
    if (rule != null && rule.getProgram() != null) {
      rule.setProgram(programService.getProgramById(rule.getProgram().getId()));
    }
    return rule;
  }

  @Override
  public RuleDTO findRuleById(long ruleId, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (ruleId <= 0) {
      throw new IllegalArgumentException("ruleId is mandatory");
    }
    if (StringUtils.isBlank(username)) {
      throw new IllegalAccessException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    RuleDTO rule = findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    if (!Utils.isRuleManager(programService, rule, username)
        && (!rule.isEnabled()
            || rule.getProgram() == null
            || !Utils.isSpaceMember(rule.getProgram().getAudienceId(), username))) {
      throw new IllegalAccessException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    return rule;
  }

  @Override
  public List<RuleDTO> findActiveRulesByEvent(String event) throws IllegalArgumentException {
    if (StringUtils.isBlank(event)) {
      throw new IllegalArgumentException("Rule event is mandatory");
    }
    return ruleStorage.findActiveRulesByEvent(event);
  }

  @Override
  public RuleDTO findRuleByTitle(String ruleTitle) throws IllegalArgumentException {
    if (StringUtils.isBlank(ruleTitle)) {
      throw new IllegalArgumentException("rule title is mandatory");
    }
    RuleDTO rule = ruleStorage.findRuleByTitle(ruleTitle);
    if (rule != null && rule.getProgram() != null) {
      rule.setProgram(programService.getProgramById(rule.getProgram().getId()));
    }
    return rule;
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

  @Override
  public List<RuleDTO> findAllRules(int offset, int limit) {
    List<Long> rulesIds = ruleStorage.findAllRulesIds(offset, limit);
    return rulesIds.stream().map(this::findRuleById).toList();
  }

  @Override
  public List<RuleDTO> getRules(RuleFilter ruleFilter, int offset, int limit) {
    List<Long> rulesIds = ruleStorage.findRulesIdsByFilter(ruleFilter, offset, limit);
    return rulesIds.stream().map(this::findRuleById).toList();
  }

  @Override
  public int countRules(RuleFilter ruleFilter) {
    return ruleStorage.countRulesByFilter(ruleFilter);
  }

  public List<String> getAllEvents() {
    return ruleStorage.getAllEvents();
  }

  public long getRulesTotalScoreByDomain(long domainId) {
    return ruleStorage.getRulesTotalScoreByDomain(domainId);
  }

  @Override
  public RuleDTO deleteRuleById(Long ruleId, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (username == null) {
      throw new IllegalArgumentException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    if (ruleId == null || ruleId <= 0) {
      throw new IllegalArgumentException("ruleId must be positive");
    }
    RuleDTO rule = ruleStorage.findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule with id " + ruleId + " is not found");
    }
    if (!Utils.isRuleManager(programService, rule, username)) {
      throw new IllegalAccessException("The user is not authorized to delete a rule");
    }
    Date endDate = Utils.parseSimpleDate(rule.getEndDate());
    Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    if (endDate != null && (endDate.after(currentDate) || endDate.equals(currentDate))) {
      throw new IllegalArgumentException("Rule does not ended yet");
    }

    rule = ruleStorage.deleteRuleById(ruleId, username);
    Utils.broadcastEvent(listenerService, POST_DELETE_RULE_EVENT, rule, username);
    return rule;
  }

  @Override
  public RuleDTO createRule(RuleDTO rule, String username) throws IllegalAccessException, ObjectAlreadyExistsException {
    if (rule == null) {
      throw new IllegalArgumentException("rule object is mandatory");
    }
    if (username == null) {
      throw new IllegalArgumentException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    if (rule.getId() != null) {
      throw new IllegalArgumentException("domain id must be equal to 0");
    }
    if (!Utils.isRuleManager(programService, rule, username)) {
      throw new IllegalAccessException("The user is not authorized to create a rule");
    }
    checkPermissionAndDates(rule, username);
    rule.setCreatedBy(username);
    rule.setLastModifiedBy(username);
    long domainId = rule.getProgram().getId();
    RuleDTO similarRule = ruleStorage.findRuleByEventAndDomain(rule.getEvent(), domainId);
    if (similarRule != null && !similarRule.isDeleted()) {
      throw new ObjectAlreadyExistsException("Rule with same event and domain already exist");
    }
    return createRuleAndBroadcast(rule, username);
  }

  @Override
  public RuleDTO createRule(RuleDTO ruleDTO) {
    ruleDTO.setLastModifiedBy(Utils.SYSTEM_USERNAME);
    ruleDTO.setCreatedBy(Utils.SYSTEM_USERNAME);
    return createRuleAndBroadcast(ruleDTO, null);
  }

  @Override
  public RuleDTO updateRule(RuleDTO rule, String username) throws ObjectNotFoundException, IllegalAccessException {
    if (username == null) {
      throw new IllegalArgumentException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    if (rule.getId() == null || rule.getId() == 0) {
      throw new ObjectNotFoundException("Rule with id 0 doesn't exist");
    }
    RuleDTO oldRule = ruleStorage.findRuleById(rule.getId());
    if (oldRule == null) {
      throw new ObjectNotFoundException("Rule does not exist");
    }
    if (!Utils.isRuleManager(programService, oldRule, username)) {
      throw new IllegalAccessException("The user is not authorized to update a rule");
    }
    checkPermissionAndDates(oldRule, username); // Test if user
    // was manager
    checkPermissionAndDates(rule, username); // Test if user is
    // remaining manager
    rule.setCreatedDate(oldRule.getCreatedDate());
    rule.setCreatedBy(oldRule.getCreatedBy());
    return updateRuleAndBroadcast(rule, username);
  }

  @Override
  public RuleDTO updateRule(RuleDTO ruleDTO) throws ObjectNotFoundException {
    return updateRuleAndBroadcast(ruleDTO, null);
  }

  private void checkPermissionAndDates(RuleDTO rule, String username) throws IllegalAccessException {
    if (!Utils.isRuleManager(programService, rule, username)) {
      if (rule.getId() > 0) {
        throw new IllegalAccessException("User " + username + " is not allowed to update the rule with id " + rule.getId());
      } else {
        throw new IllegalAccessException("User " + username + " is not allowed to create a rule that it can't manage");
      }
    }
    Date startDate = Utils.parseSimpleDate(rule.getStartDate());
    Date endDate = Utils.parseSimpleDate(rule.getEndDate());
    if (startDate != null && endDate != null && endDate.compareTo(startDate) <= 0) {
      throw new IllegalStateException("endDate must be greater than startDate");
    }
  }

  private RuleDTO updateRuleAndBroadcast(RuleDTO rule, String username) throws ObjectNotFoundException {
    if (rule.getId() == null || rule.getId() == 0 || ruleStorage.findRuleById(rule.getId()) == null) {
      throw new ObjectNotFoundException("Rule id is mandatory");
    }
    ProgramDTO program = rule.getProgram();
    if (program != null) {
      long domainId = rule.getProgram().getId();
      RuleDTO similarRule = ruleStorage.findRuleByEventAndDomain(rule.getEvent(), domainId);
      if (similarRule != null && !similarRule.getId().equals(rule.getId()) && !similarRule.isDeleted()) {
        throw new IllegalStateException("Rule with same event and domain already exist");
      }
    }
    rule.setLastModifiedBy(username);
    rule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    rule = ruleStorage.saveRule(rule);
    Utils.broadcastEvent(listenerService, POST_UPDATE_RULE_EVENT, rule.getId(), username);
    return rule;
  }

  private RuleDTO createRuleAndBroadcast(RuleDTO ruleDTO, String username) {
    ruleDTO = ruleStorage.saveRule(ruleDTO);
    Utils.broadcastEvent(listenerService, POST_CREATE_RULE_EVENT, ruleDTO.getId(), username);
    return ruleDTO;
  }
}

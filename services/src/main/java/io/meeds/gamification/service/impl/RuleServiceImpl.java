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
package io.meeds.gamification.service.impl;

import static io.meeds.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_DELETE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.utils.Utils;

public class RuleServiceImpl implements RuleService {

  private static final String   USERNAME_IS_MANDATORY_MESSAGE = "Username is mandatory";

  private final ProgramService  programService;

  private final RuleStorage     ruleStorage;

  private final SpaceService    spaceService;

  private final ListenerService listenerService;

  public RuleServiceImpl(ProgramService programService,
                         RuleStorage ruleStorage,
                         SpaceService spaceService,
                         ListenerService listenerService) {
    this.programService = programService;
    this.listenerService = listenerService;
    this.spaceService = spaceService;
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
      throw new ObjectNotFoundException("Rule doesn't exist");
    }
    if (rule.isDeleted()) {
      throw new ObjectNotFoundException("Rule has been deleted");
    }
    if (!Utils.isRuleManager(rule, username)
        && (!rule.isEnabled()
            || rule.getProgram() == null
            || !Utils.isSpaceMember(rule.getProgram().getAudienceId(), username))) {
      throw new IllegalAccessException("Rule isn't accessible");
    }
    return rule;
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

  @Override
  public List<RuleDTO> getRules(RuleFilter ruleFilter,
                                String username,
                                int offset,
                                int limit) {
    if (computeUserSpaces(ruleFilter, username)) {
      return getRules(ruleFilter, offset, limit);
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public List<RuleDTO> getRules(RuleFilter ruleFilter,
                                int offset,
                                int limit) {
    List<Long> rulesIds = ruleStorage.findRulesIdsByFilter(ruleFilter, offset, limit);
    return rulesIds.stream().map(this::findRuleById).toList();
  }

  @Override
  public int countRules(RuleFilter ruleFilter, String username) {
    if (computeUserSpaces(ruleFilter, username)) {
      return countRules(ruleFilter);
    } else {
      return 0;
    }
  }

  @Override
  public int countRules(RuleFilter ruleFilter) {
    return ruleStorage.countRulesByFilter(ruleFilter);
  }

  @Override
  public List<String> getAllEvents() {
    return ruleStorage.getAllEvents();
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
    if (!Utils.isRuleManager(rule, username)) {
      throw new IllegalAccessException("The user is not authorized to delete a rule");
    }
    rule = ruleStorage.deleteRuleById(ruleId, username);
    Utils.broadcastEvent(listenerService, POST_DELETE_RULE_EVENT, rule, username);
    return rule;
  }

  @Override
  public RuleDTO createRule(RuleDTO rule, String username) throws IllegalAccessException,
                                                           ObjectAlreadyExistsException,
                                                           ObjectNotFoundException {
    if (rule == null) {
      throw new IllegalArgumentException("rule object is mandatory");
    }
    if (username == null) {
      throw new IllegalArgumentException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    if (rule.getId() != null) {
      throw new IllegalArgumentException("domain id must be equal to 0");
    }
    long programId = rule.getProgram().getId();
    ProgramDTO program = programService.getProgramById(programId, username);
    rule.setProgram(program);

    checkPermissionAndDates(rule, username);
    rule.setCreatedBy(username);
    rule.setLastModifiedBy(username);
    rule.setDeleted(false);
    RuleDTO similarRule = ruleStorage.findActiveRuleByEventAndDomain(rule.getEvent(), programId);
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
      throw new ObjectNotFoundException("Rule doesn't exist");
    }
    if (rule.getProgram() == null) {
      throw new IllegalArgumentException("Program is mandatory");
    }
    ProgramDTO program = programService.getProgramById(rule.getProgram().getId());
    rule.setProgram(program);
    if (rule.getProgram() == null) {
      throw new IllegalArgumentException("Program with id " + rule.getProgram().getId() + " wasn't found");
    }
    if (oldRule.isDeleted()) {
      throw new ObjectNotFoundException("Rule with id '" + oldRule.getId() + "' was deleted");
    }
    if (!Utils.isRuleManager(oldRule, username)) {
      throw new IllegalAccessException("The user is not authorized to update a rule");
    }
    checkPermissionAndDates(oldRule, username); // Test if user was manager
    checkPermissionAndDates(rule, username); // Test if user is remaining
                                             // manager
    rule.setCreatedDate(oldRule.getCreatedDate());
    rule.setCreatedBy(oldRule.getCreatedBy());
    return updateRuleAndBroadcast(rule, username);
  }

  @Override
  public RuleDTO updateRule(RuleDTO ruleDTO) throws ObjectNotFoundException {
    return updateRuleAndBroadcast(ruleDTO, null);
  }

  @Override
  public List<RuleDTO> getPrerequisiteRules(long ruleId) {
    RuleDTO rule = findRuleById(ruleId);
    if (rule != null && CollectionUtils.isNotEmpty(rule.getPrerequisiteRuleIds())) {
      return rule.getPrerequisiteRuleIds().stream().map(id -> {
        RuleDTO r = findRuleById(id);
        if (r == null
            || r.getProgram() == null
            || !r.isEnabled()
            || r.isDeleted()) {
          return null;
        } else {
          return r;
        }
      }).filter(Objects::nonNull).toList();
    } else {
      return Collections.emptyList();
    }
  }

  @SuppressWarnings("unchecked")
  private boolean computeUserSpaces(RuleFilter ruleFilter, String username) {
    if (StringUtils.isBlank(username)) {
      return false;
    }
    if (Utils.isRewardingManager(username)) {
      return true;
    }
    List<Long> memberSpacesIds = spaceService.getMemberSpacesIds(username, 0, -1)
                                             .stream()
                                             .map(Long::parseLong)
                                             .toList();
    if (CollectionUtils.isEmpty(memberSpacesIds)) {
      return false;
    }
    if (CollectionUtils.isNotEmpty(ruleFilter.getSpaceIds())) {
      memberSpacesIds = (List<Long>) CollectionUtils.intersection(memberSpacesIds, ruleFilter.getSpaceIds());
    }
    ruleFilter.setSpaceIds(memberSpacesIds);
    return true;
  }

  private void checkPermissionAndDates(RuleDTO rule, String username) throws IllegalAccessException {
    if (!Utils.isRuleManager(rule, username)) {
      if (rule.getId() != null && rule.getId() > 0) {
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
      RuleDTO similarRule = ruleStorage.findActiveRuleByEventAndDomain(rule.getEvent(), domainId);
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

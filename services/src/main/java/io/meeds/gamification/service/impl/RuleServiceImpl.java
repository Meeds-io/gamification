/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 * 
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
import static io.meeds.gamification.utils.Utils.POST_PUBLISH_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_OBJECT_TYPE;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_PARAM_RULE_DESCRIPTION;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_PARAM_RULE_ID;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_PARAM_RULE_SCORE;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_PARAM_RULE_TITLE;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_TYPE;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.RulePublication;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.search.RuleSearchConnector;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.utils.Utils;

public class RuleServiceImpl implements RuleService {

  private static final Log          LOG                           = ExoLogger.getLogger(RuleServiceImpl.class);

  private static final int          MAX_RULES_TO_SEARCH           = 100;

  private static final String       USERNAME_IS_MANDATORY_MESSAGE = "Username is mandatory";

  private final ProgramService      programService;

  private final RuleStorage         ruleStorage;

  private final RuleSearchConnector ruleSearchConnector;

  private final SpaceService        spaceService;

  private final ActivityManager     activityManager;

  private final IdentityManager     identityManager;

  private final ListenerService     listenerService;

  public RuleServiceImpl(ProgramService programService,
                         RuleStorage ruleStorage,
                         RuleSearchConnector ruleSearchConnector,
                         SpaceService spaceService,
                         ActivityManager activityManager,
                         IdentityManager identityManager,
                         ListenerService listenerService) {
    this.programService = programService;
    this.listenerService = listenerService;
    this.spaceService = spaceService;
    this.activityManager = activityManager;
    this.identityManager = identityManager;
    this.ruleStorage = ruleStorage;
    this.ruleSearchConnector = ruleSearchConnector;
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
    RuleDTO rule = findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule doesn't exist");
    }
    if (rule.isDeleted()) {
      throw new ObjectNotFoundException("Rule has been deleted");
    }
    if (!isRuleManager(rule, username)
        && (rule.getProgram() == null
            || !programService.canViewProgram(rule.getProgram().getId(), username))) {
      throw new IllegalAccessException("Rule isn't accessible");
    }
    if (rule.getProgram() != null) {
      computeActivity(rule, 0, null, false, null, null, true);
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
    ruleFilter = computeUserSpaces(ruleFilter, username);
    return getRules(ruleFilter, offset, limit);
  }

  @Override
  public List<RuleDTO> getRules(RuleFilter ruleFilter,
                                int offset,
                                int limit) {
    List<Long> ruleIds;
    if (isSearchFilter(ruleFilter)) {
      int searchLimit = offset + limit * 2;
      ruleIds = ruleSearchConnector.search(ruleFilter, 0, searchLimit);
      if (CollectionUtils.isEmpty(ruleIds)) {
        return Collections.emptyList();
      } else {
        ruleFilter.setTerm(null);
        ruleFilter.setRuleIds(ruleIds);
        ruleIds = ruleStorage.findRuleIdsByFilter(ruleFilter, offset, limit);
      }
    } else {
      ruleIds = ruleStorage.findRuleIdsByFilter(ruleFilter, offset, limit);
    }
    return ruleIds.stream().map(id -> {
      RuleDTO rule = findRuleById(id);
      if (rule != null && rule.getProgram() != null) {
        computeActivity(rule, 0, null, false, null, null, true);
      }
      return rule;
    }).toList();
  }

  @Override
  public int countRules(RuleFilter ruleFilter, String username) {
    ruleFilter = computeUserSpaces(ruleFilter, username);
    return countRules(ruleFilter);
  }

  @Override
  public int countRules(RuleFilter ruleFilter) {
    if (isSearchFilter(ruleFilter)) {
      List<Long> ruleIds = ruleSearchConnector.search(ruleFilter, 0, MAX_RULES_TO_SEARCH);
      if (CollectionUtils.isEmpty(ruleIds)) {
        return 0;
      } else {
        ruleFilter.setTerm(null);
        ruleFilter.setRuleIds(ruleIds);
        return ruleStorage.countRulesByFilter(ruleFilter);
      }
    } else {
      return ruleStorage.countRulesByFilter(ruleFilter);
    }
  }

  @Override
  public int countActiveRules(long programId) {
    RuleFilter ruleFilter = new RuleFilter(true);
    ruleFilter.setStatus(EntityStatusType.ENABLED);
    ruleFilter.setProgramStatus(EntityStatusType.ALL);
    ruleFilter.setProgramId(programId);
    ruleFilter.setDateFilterType(DateFilterType.ACTIVE);
    return countRules(ruleFilter);
  }

  @Override
  public RuleDTO deleteRuleById(long ruleId, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (username == null) {
      throw new IllegalArgumentException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    if (ruleId <= 0) {
      throw new IllegalArgumentException("ruleId must be positive");
    }
    RuleDTO rule = ruleStorage.findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule with id " + ruleId + " is not found");
    }
    if (!isRuleManager(rule, username)) {
      throw new IllegalAccessException("The user is not authorized to delete a rule");
    }
    return deleteRule(ruleId, username);
  }

  @Override
  public RuleDTO deleteRuleById(long ruleId) {
    try {
      return deleteRule(ruleId, null);
    } catch (ObjectNotFoundException e) {
      LOG.debug("Rule with id {} not found. Continue processing without interrupting current operation.",
                ruleId,
                e);
      return null;
    }
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
      throw new IllegalArgumentException("program id must be equal to 0");
    }
    long programId = rule.getProgram().getId();
    ProgramDTO program = programService.getProgramById(programId, username);
    rule.setProgram(program);

    checkPermissionAndDates(rule, username);
    rule.setCreatedBy(username);
    rule.setLastModifiedBy(username);
    rule.setDeleted(false);
    rule.setActivityId(0);
    if (rule.getEvent() != null) {
      RuleDTO similarRule = ruleStorage.findActiveRuleByEventAndProgramId(rule.getEvent().getTitle(), programId);
      if (similarRule != null && !similarRule.isDeleted() && rule.getEvent().getProperties() == similarRule.getEvent().getProperties()) {
        throw new ObjectAlreadyExistsException("Rule with same event and program already exist");
      }
    }
    return createRuleAndBroadcast(rule, username);
  }

  @Override
  public RuleDTO createRule(RuleDTO rule) {
    rule.setLastModifiedBy(Utils.SYSTEM_USERNAME);
    rule.setCreatedBy(Utils.SYSTEM_USERNAME);
    rule.setActivityId(0);
    return createRuleAndBroadcast(rule, null);
  }

  @Override
  public RuleDTO updateRule(RuleDTO rule, String username) throws ObjectNotFoundException, IllegalAccessException {
    if (username == null) {
      throw new IllegalArgumentException(USERNAME_IS_MANDATORY_MESSAGE);
    }
    if (rule.getId() == null || rule.getId() == 0) {
      throw new ObjectNotFoundException("Rule with id 0 doesn't exist");
    }
    RuleDTO storedRule = ruleStorage.findRuleById(rule.getId());
    if (storedRule == null) {
      throw new ObjectNotFoundException("Rule doesn't exist");
    }
    if (storedRule.isDeleted()) {
      throw new ObjectNotFoundException("Rule with id '" + storedRule.getId() + "' was deleted");
    }
    if (rule.getProgram() == null) {
      throw new IllegalArgumentException("Program is mandatory");
    }
    ProgramDTO program = programService.getProgramById(rule.getProgram().getId());
    rule.setProgram(program);
    if (rule.getProgram() == null) {
      throw new IllegalArgumentException("Program with id " + rule.getProgram().getId() + " wasn't found");
    }
    if (!isRuleManager(storedRule, username)) {
      throw new IllegalAccessException("The user is not authorized to update a rule");
    }
    checkPermissionAndDates(storedRule, username); // Test if user was manager
    checkPermissionAndDates(rule, username); // Test if user is remaining
                                             // manager
    rule.setCreatedDate(storedRule.getCreatedDate());
    rule.setCreatedBy(storedRule.getCreatedBy());
    rule.setActivityId(storedRule.getActivityId());
    return updateRuleAndBroadcast(rule, username);
  }

  @Override
  public RuleDTO updateRule(RuleDTO ruleDTO) throws ObjectNotFoundException {
    return updateRuleAndBroadcast(ruleDTO, null);
  }

  @Override
  public List<RuleDTO> getPrerequisiteRules(long ruleId) {
    RuleDTO rule = ruleStorage.findRuleById(ruleId);
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

  private boolean isSearchFilter(RuleFilter ruleFilter) {
    return ruleFilter.isFavorites()
        || CollectionUtils.isNotEmpty(ruleFilter.getTagNames())
        || (StringUtils.isNotBlank(ruleFilter.getTerm())
            && ruleFilter.getLocale() != null);
  }

  @SuppressWarnings("unchecked")
  private RuleFilter computeUserSpaces(RuleFilter ruleFilter, String username) {
    ruleFilter = ruleFilter.clone();
    if (Utils.isRewardingManager(username)) {
      ruleFilter.setAllSpaces(CollectionUtils.isEmpty(ruleFilter.getSpaceIds()));
      return ruleFilter;
    }
    List<Long> memberSpacesIds = spaceService.getMemberSpacesIds(username, 0, -1)
                                             .stream()
                                             .map(Long::parseLong)
                                             .toList();
    if (CollectionUtils.isNotEmpty(ruleFilter.getSpaceIds())) {
      memberSpacesIds = (List<Long>) CollectionUtils.intersection(memberSpacesIds, ruleFilter.getSpaceIds());
    }
    ruleFilter.setSpaceIds(memberSpacesIds);
    return ruleFilter;
  }

  private void checkPermissionAndDates(RuleDTO rule, String username) throws IllegalAccessException {
    if (!isRuleManager(rule, username)) {
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
    rule.setLastModifiedBy(username);
    rule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    if (rule instanceof RulePublication rulePublication) {
      computeActivity(rule,
                      rulePublication.getSpaceId(),
                      username,
                      rulePublication.isPublish(),
                      rulePublication.getMessage(),
                      rulePublication.getTemplateParams(),
                      false);
    }
    rule = ruleStorage.saveRule(rule);
    Utils.broadcastEvent(listenerService, POST_UPDATE_RULE_EVENT, rule.getId(), username);
    return rule;
  }

  private RuleDTO createRuleAndBroadcast(RuleDTO rule, String username) {
    RuleDTO savedRule = ruleStorage.saveRule(rule);
    if (rule instanceof RulePublication rulePublication) {
      savedRule = computeActivity(savedRule,
                                  rulePublication.getSpaceId(),
                                  username,
                                  rulePublication.isPublish(),
                                  rulePublication.getMessage(),
                                  rulePublication.getTemplateParams(),
                                  true);
    }
    if (savedRule != null) {
      Utils.broadcastEvent(listenerService, POST_CREATE_RULE_EVENT, savedRule.getId(), username);
    }
    return savedRule;
  }

  private boolean isRuleManager(RuleDTO rule, String username) {
    ProgramDTO program = rule.getProgram();
    if (program == null || StringUtils.isBlank(username)) {
      return false;
    } else {
      return programService.isProgramOwner(program.getId(), username);
    }
  }

  private RuleDTO deleteRule(Long ruleId, String username) throws ObjectNotFoundException {
    RuleDTO rule = ruleStorage.deleteRuleById(ruleId, username);
    Utils.broadcastEvent(listenerService, POST_DELETE_RULE_EVENT, rule, username);
    return rule;
  }

  private RuleDTO computeActivity(RuleDTO rule, // NOSONAR
                                  long spaceId,
                                  String username,
                                  boolean publish,
                                  String message,
                                  Map<String, String> templateParams,
                                  boolean saveRule) {
    if (rule == null
        || !rule.isEnabled()
        || rule.isDeleted()
        || rule.getProgram() == null
        || !rule.getProgram().isEnabled()
        || rule.getProgram().isDeleted()) {
      return rule;
    }
    ExoSocialActivity activity = getExistingActivity(rule);
    if (activity == null || (spaceId > 0 && getSpaceId(activity) != spaceId)) {
      if (spaceId == 0) {
        spaceId = rule.getSpaceId();
      }
      Space space = spaceId > 0 ? spaceService.getSpaceById(String.valueOf(spaceId)) : null;
      Identity publisherIdentity = getActivityPublisherIdentity(rule, space, username);
      if (publisherIdentity == null) {
        LOG.warn("Unable to generate and activity with a null publisher for a rule and/or program creator and/or modifier. Rule id : {}",
                 rule.getId());
        return rule;
      }
      activity = new ExoSocialActivityImpl();
      setActivityParams(activity, rule, publisherIdentity, message, templateParams, publish);
      createActivity(activity, space, publisherIdentity);
      rule.setActivityId(Long.parseLong(activity.getId()));
      if (saveRule) {
        rule = ruleStorage.saveRule(rule);
      }
      if (!activity.isHidden()) {
        Utils.broadcastEvent(listenerService, POST_PUBLISH_RULE_EVENT, rule, username);
      }
    } else if (publish) {
      Identity publisherIdentity = getActivityPublisherIdentity(rule, null, username);
      if (publisherIdentity == null) {
        LOG.warn("Unable to generate and activity with a null publisher for a rule and/or program creator and/or modifier. Rule id : {}",
                 rule.getId());
        return rule;
      }
      boolean newlyPublished = activity.isHidden();
      setActivityParams(activity, rule, publisherIdentity, message, templateParams, publish);
      activityManager.updateActivity(activity);
      if (newlyPublished) {
        Utils.broadcastEvent(listenerService, POST_PUBLISH_RULE_EVENT, rule, username);
      }
    }
    return rule;
  }

  private void createActivity(ExoSocialActivity activity,
                              Space space,
                              Identity publisherIdentity) {
    Identity streamOwner = space == null ? publisherIdentity : identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
    activityManager.saveActivityNoReturn(streamOwner, activity);
  }

  private void setActivityParams(ExoSocialActivity activity,
                                 RuleDTO rule,
                                 Identity publisherIdentity,
                                 String message,
                                 Map<String, String> templateParams,
                                 boolean publish) {
    activity.setUserId(publisherIdentity.getId());
    activity.setPosterId(publisherIdentity.getId());
    activity.setTitle(!publish || StringUtils.isBlank(message) ? "" : message);
    activity.setBody(!publish || StringUtils.isBlank(message) ? "" : message);
    activity.setType(RULE_ACTIVITY_TYPE);
    buildActivityParams(activity, templateParams);
    activity.setMetadataObjectType(RULE_ACTIVITY_OBJECT_TYPE);
    activity.setMetadataObjectId(String.valueOf(rule.getId()));
    activity.getTemplateParams().put(RULE_ACTIVITY_PARAM_RULE_ID, String.valueOf(rule.getId()));
    activity.getTemplateParams().put(RULE_ACTIVITY_PARAM_RULE_TITLE, rule.getTitle());
    activity.getTemplateParams().put(RULE_ACTIVITY_PARAM_RULE_DESCRIPTION, rule.getDescription());
    activity.getTemplateParams().put(RULE_ACTIVITY_PARAM_RULE_SCORE, String.valueOf(rule.getScore()));
    activity.isHidden(!publish);
  }

  private Identity getActivityPublisherIdentity(RuleDTO rule, Space space, String username) {
    Iterator<String> potentialPublishers = Arrays.asList(username,
                                                         rule.getCreatedBy(),
                                                         rule.getLastModifiedBy(),
                                                         rule.getProgram().getCreatedBy(),
                                                         rule.getProgram().getLastModifiedBy())
                                                 .iterator();
    Identity publisherIdentity = null;
    while (potentialPublishers.hasNext() && publisherIdentity == null) {
      String publisher = potentialPublishers.next();
      if (isValidUsername(publisher)) {
        Identity identity = getUserIdentity(publisher);
        if (identity != null
            && identity.isEnable()
            && !identity.isDeleted()
            && (space == null
                || spaceService.isMember(space, identity.getRemoteId())
                || Utils.isRewardingManager(identity.getRemoteId()))) {
          publisherIdentity = identity;
        }
      }
    }
    return publisherIdentity;
  }

  private ExoSocialActivity getExistingActivity(RuleDTO rule) {
    long activityId = rule.getActivityId();
    if (activityId != 0) {
      return activityManager.getActivity(String.valueOf(activityId));
    } else {
      return null;
    }
  }

  private long getSpaceId(ExoSocialActivity activity) {
    if (activity.getActivityStream().isSpace()) {
      Space space = spaceService.getSpaceByPrettyName(activity.getActivityStream().getPrettyId());
      if (space != null) {
        return Long.parseLong(space.getId());
      }
    }
    return 0;
  }

  private boolean isValidUsername(String username) {
    return StringUtils.isNotBlank(username) && !StringUtils.equals(Utils.SYSTEM_USERNAME, username);
  }

  private Identity getUserIdentity(String identityId) {
    Identity identity = identityManager.getOrCreateUserIdentity(identityId);
    if (identity == null && NumberUtils.isDigits(identityId)) {
      identity = identityManager.getIdentity(identityId); // NOSONAR
    }
    return identity;
  }

  private void buildActivityParams(ExoSocialActivity activity, Map<String, String> templateParams) {
    Map<String, String> currentTemplateParams = activity.getTemplateParams() == null ? new HashMap<>()
            : new HashMap<>(activity.getTemplateParams());
    if (templateParams != null) {
      currentTemplateParams.putAll(templateParams);
    }
    Iterator<Entry<String, String>> entries = currentTemplateParams.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry<String, String> entry = entries.next();
      if (entry != null && (StringUtils.isBlank(entry.getValue()) || StringUtils.equals(entry.getValue(), "-"))) {
        entry.setValue("");
      }
    }
    activity.setTemplateParams(currentTemplateParams);
  }

}

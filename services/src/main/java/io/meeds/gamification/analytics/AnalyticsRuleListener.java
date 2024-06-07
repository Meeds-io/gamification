/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
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
package io.meeds.gamification.analytics;

import static io.meeds.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_DELETE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.STATISTICS_CREATE_RULE_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_DELETE_RULE_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_RULE_SUBMODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_UPDATE_RULE_OPERATION;
import static io.meeds.gamification.utils.Utils.addRuleStatisticParameters;
import static io.meeds.analytics.utils.AnalyticsUtils.addStatisticData;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import io.meeds.analytics.model.StatisticData;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.EventService;
import io.meeds.gamification.service.RuleService;

@Asynchronous
public class AnalyticsRuleListener extends Listener<Object, String> {

  private IdentityManager identityManager;

  private SpaceService    spaceService;

  private RuleService     ruleService;

  private EventService    eventService;

  public AnalyticsRuleListener(IdentityManager identityManager,
                               SpaceService spaceService,
                               RuleService ruleService,
                               EventService eventService) {
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.ruleService = ruleService;
    this.eventService = eventService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<Object, String> event) throws Exception {
    Object object = event.getSource();
    String username = event.getData();

    RuleDTO rule = getRule(object);
    if (rule == null) {
      return;
    }

    StatisticData statisticData = new StatisticData();
    statisticData.setModule(STATISTICS_GAMIFICATION_MODULE);
    statisticData.setSubModule(STATISTICS_RULE_SUBMODULE);
    switch (event.getEventName()) {
      case POST_CREATE_RULE_EVENT ->
        statisticData.setOperation(STATISTICS_CREATE_RULE_OPERATION);
      case POST_UPDATE_RULE_EVENT ->
        statisticData.setOperation(STATISTICS_UPDATE_RULE_OPERATION);
      case POST_DELETE_RULE_EVENT ->
        statisticData.setOperation(STATISTICS_DELETE_RULE_OPERATION);
      default -> throw new IllegalArgumentException("Unexpected listener event name: " + event.getEventName());
    }
    EventDTO ruleEvent = null;
    if (rule.getEvent() != null) {
      List<EventDTO> events = eventService.getEventsByTitle(rule.getEvent().getTitle(), 0, 1);
      if (CollectionUtils.isNotEmpty(events)) {
        ruleEvent = events.get(0);
      }
    }
    addRuleStatisticParameters(identityManager,
                               spaceService,
                               rule,
                               ruleEvent,
                               statisticData,
                               username);
    addStatisticData(statisticData);
  }

  private RuleDTO getRule(Object object) {
    if (object instanceof Long id) {
      return ruleService.findRuleById(id);
    } else if (object instanceof RuleDTO rule) {
      return rule;
    }
    return null;
  }

}

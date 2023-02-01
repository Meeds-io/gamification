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

import static org.exoplatform.addons.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_DELETE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_CREATE_RULE_OPERATION;
import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_DELETE_RULE_OPERATION;
import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_RULE_SUBMODULE;
import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_UPDATE_RULE_OPERATION;
import static org.exoplatform.addons.gamification.utils.Utils.addRuleStatisticParameters;
import static org.exoplatform.analytics.utils.AnalyticsUtils.addStatisticData;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

@Asynchronous
public class AnalyticsRuleListener extends Listener<Object, String> {

  private IdentityManager identityManager;

  private SpaceService    spaceService;

  private RuleService     ruleService;

  public AnalyticsRuleListener(IdentityManager identityManager,
                               SpaceService spaceService,
                               RuleService ruleService) {
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.ruleService = ruleService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<Object, String> event) throws Exception {
    Object object = event.getSource();
    String username = event.getData();

    RuleDTO rule = getRule(object);

    StatisticData statisticData = new StatisticData();
    statisticData.setModule(STATISTICS_GAMIFICATION_MODULE);
    statisticData.setSubModule(STATISTICS_RULE_SUBMODULE);
    switch (event.getEventName()) {
    case POST_DELETE_RULE_EVENT: {
      statisticData.setOperation(STATISTICS_CREATE_RULE_OPERATION);
      break;
    }
    case POST_UPDATE_RULE_EVENT: {
      statisticData.setOperation(STATISTICS_UPDATE_RULE_OPERATION);
      break;
    }
    case POST_CREATE_RULE_EVENT: {
      statisticData.setOperation(STATISTICS_DELETE_RULE_OPERATION);
      break;
    }
    default:
      throw new IllegalArgumentException("Unexpected listener event name: " + event.getEventName());
    }
    addRuleStatisticParameters(identityManager,
                               spaceService,
                               rule,
                               statisticData,
                               username);
    addStatisticData(statisticData);
  }

  private RuleDTO getRule(Object object) {
    if (object instanceof Long id) {
      return ruleService.findRuleById(id);
    } else if (object instanceof RuleDTO rule) {
      return rule;
    } else if (object instanceof Challenge challenge) {
      long id = challenge.getId();
      return ruleService.findRuleById(id);
    }
    return null;
  }

}

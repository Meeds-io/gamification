/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
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
package io.meeds.gamification.analytics;

import static io.meeds.gamification.utils.Utils.POST_CANCEL_ANNOUNCEMENT_EVENT;
import static io.meeds.gamification.utils.Utils.POST_CREATE_ANNOUNCEMENT_EVENT;
import static io.meeds.gamification.utils.Utils.POST_REALIZATION_CANCEL_EVENT;
import static io.meeds.gamification.utils.Utils.POST_REALIZATION_CREATE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_REALIZATION_UPDATE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_ANNOUNCEMENT_EVENT;
import static io.meeds.gamification.utils.Utils.STATISTICS_CANCEL_REALIZATION_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_CREATE_REALIZATION_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_REALIZATION_SUBMODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_UPDATE_REALIZATION_OPERATION;
import static io.meeds.gamification.utils.Utils.addRealizationStatisticParameters;
import static org.exoplatform.analytics.utils.AnalyticsUtils.addStatisticData;


import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.EventService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;

@Asynchronous
public class AnalyticsRealizationListener extends Listener<Object, Object> {

  private RuleService        ruleService;

  private RealizationService realizationService;

  private IdentityManager    identityManager;

  private EventService       eventService;

  private SpaceService       spaceService;

  public AnalyticsRealizationListener(RuleService ruleService,
                                      EventService eventService,
                                      RealizationService realizationService,
                                      IdentityManager identityManager,
                                      SpaceService spaceService) {
    this.ruleService = ruleService;
    this.eventService = eventService;
    this.realizationService = realizationService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<Object, Object> event) throws Exception {
    RealizationDTO realization = getRealization(event.getSource());
    if (realization == null) {
      return;
    }
    RuleDTO rule = ruleService.findRuleById(realization.getRuleId());
    StatisticData statisticData = new StatisticData();
    statisticData.setModule(STATISTICS_GAMIFICATION_MODULE);
    statisticData.setSubModule(STATISTICS_REALIZATION_SUBMODULE);
    switch (event.getEventName()) {
    case POST_REALIZATION_CREATE_EVENT, POST_CREATE_ANNOUNCEMENT_EVENT: {
      statisticData.setOperation(STATISTICS_CREATE_REALIZATION_OPERATION);
      break;
    }
    case POST_REALIZATION_CANCEL_EVENT, POST_CANCEL_ANNOUNCEMENT_EVENT: {
      statisticData.setOperation(STATISTICS_CANCEL_REALIZATION_OPERATION);
      break;
    }
    case POST_REALIZATION_UPDATE_EVENT, POST_UPDATE_ANNOUNCEMENT_EVENT: {
      statisticData.setOperation(STATISTICS_UPDATE_REALIZATION_OPERATION);
      break;
    }
    default:
      throw new IllegalArgumentException("Unexpected listener event name: " + event.getEventName());
    }
    addRealizationStatisticParameters(identityManager, spaceService, rule, rule.getEvent(), realization, statisticData);
    addStatisticData(statisticData);
  }

  private RealizationDTO getRealization(Object object) {
    if (object instanceof RealizationDTO realization) {
      return realization;
    } else if (object instanceof Announcement announcement) {
      return realizationService.getRealizationById(announcement.getId());
    }
    return null;
  }

}

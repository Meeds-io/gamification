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

import static io.meeds.gamification.utils.Utils.POST_CREATE_ANNOUNCEMENT_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_ANNOUNCEMENT_EVENT;
import static io.meeds.gamification.utils.Utils.STATISTICS_ANNOUNCEMENT_SUBMODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_CREATE_ANNOUNCE_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_UPDATE_ANNOUNCE_OPERATION;
import static io.meeds.gamification.utils.Utils.addAnnouncementStatisticParameters;
import static io.meeds.analytics.utils.AnalyticsUtils.addStatisticData;

import io.meeds.analytics.model.StatisticData;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;

@Asynchronous
public class AnalyticsAnnouncementListener extends Listener<Announcement, Long> {

  private IdentityManager identityManager;

  private SpaceService    spaceService;

  private RuleService     ruleService;

  public AnalyticsAnnouncementListener(IdentityManager identityManager,
                                       SpaceService spaceService,
                                       RuleService ruleService) {
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.ruleService = ruleService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<Announcement, Long> event) throws Exception {
    Announcement announcement = event.getSource();
    Long userIdentityId = event.getData();

    RuleDTO rule = ruleService.findRuleById(announcement.getChallengeId());

    StatisticData statisticData = new StatisticData();
    statisticData.setModule(STATISTICS_GAMIFICATION_MODULE);
    statisticData.setSubModule(STATISTICS_ANNOUNCEMENT_SUBMODULE);
    switch (event.getEventName()) {
    case POST_CREATE_ANNOUNCEMENT_EVENT: {
      statisticData.setOperation(STATISTICS_CREATE_ANNOUNCE_OPERATION);
      break;
    }
    case POST_UPDATE_ANNOUNCEMENT_EVENT: {
      statisticData.setOperation(STATISTICS_UPDATE_ANNOUNCE_OPERATION);
      break;
    }
    default:
      throw new IllegalArgumentException("Unexpected listener event name: " + event.getEventName());
    }
    String username = userIdentityId == null ? null
                                             : identityManager.getIdentity(String.valueOf(userIdentityId))
                                                              .getRemoteId();
    addAnnouncementStatisticParameters(identityManager,
                                       spaceService,
                                       rule,
                                       announcement,
                                       statisticData,
                                       username);
    addStatisticData(statisticData);
  }

}

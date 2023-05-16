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

import static io.meeds.gamification.service.ProgramService.GAMIFICATION_DOMAIN_CREATE_LISTENER;
import static io.meeds.gamification.service.ProgramService.GAMIFICATION_DOMAIN_DELETE_LISTENER;
import static io.meeds.gamification.service.ProgramService.GAMIFICATION_DOMAIN_DISABLE_LISTENER;
import static io.meeds.gamification.service.ProgramService.GAMIFICATION_DOMAIN_ENABLE_LISTENER;
import static io.meeds.gamification.service.ProgramService.GAMIFICATION_DOMAIN_UPDATE_LISTENER;
import static io.meeds.gamification.utils.Utils.STATISTICS_CREATE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_DELETE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_DISABLE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_ENABLE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_PROGRAM_SUBMODULE;
import static io.meeds.gamification.utils.Utils.STATISTICS_UPDATE_PROGRAM_OPERATION;
import static io.meeds.gamification.utils.Utils.addDomainStatisticParameters;
import static org.exoplatform.analytics.utils.AnalyticsUtils.addStatisticData;

import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.ProgramDTO;

@Asynchronous
public class AnalyticsProgramListener extends Listener<ProgramDTO, String> {

  private IdentityManager identityManager;

  private SpaceService    spaceService;

  public AnalyticsProgramListener(IdentityManager identityManager, SpaceService spaceService) {
    this.identityManager = identityManager;
    this.spaceService = spaceService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<ProgramDTO, String> event) throws Exception {
    String userId = event.getData();
    ProgramDTO program = event.getSource();
    if (program == null) {
      return;
    }

    StatisticData statisticData = new StatisticData();
    statisticData.setModule(STATISTICS_GAMIFICATION_MODULE);
    statisticData.setSubModule(STATISTICS_PROGRAM_SUBMODULE);
    switch (event.getEventName()) {
    case GAMIFICATION_DOMAIN_CREATE_LISTENER: {
      statisticData.setOperation(STATISTICS_CREATE_PROGRAM_OPERATION);
      break;
    }
    case GAMIFICATION_DOMAIN_UPDATE_LISTENER: {
      statisticData.setOperation(STATISTICS_UPDATE_PROGRAM_OPERATION);
      break;
    }
    case GAMIFICATION_DOMAIN_DELETE_LISTENER: {
      statisticData.setOperation(STATISTICS_DELETE_PROGRAM_OPERATION);
      break;
    }
    case GAMIFICATION_DOMAIN_ENABLE_LISTENER: {
      statisticData.setOperation(STATISTICS_ENABLE_PROGRAM_OPERATION);
      break;
    }
    case GAMIFICATION_DOMAIN_DISABLE_LISTENER: {
      statisticData.setOperation(STATISTICS_DISABLE_PROGRAM_OPERATION);
      break;
    }
    default:
      throw new IllegalArgumentException("Unexpected listener event name: " + event.getEventName());
    }
    addDomainStatisticParameters(identityManager, spaceService, program, statisticData, userId);
    addStatisticData(statisticData);
  }

}

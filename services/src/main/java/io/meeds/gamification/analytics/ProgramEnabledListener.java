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

import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_GAMIFICATION_MODULE;
import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_PROGRAM_SUBMODULE;
import static org.exoplatform.addons.gamification.utils.Utils.STATISTICS_UPDATE_PROGRAM_OPERATION;
import static org.exoplatform.addons.gamification.utils.Utils.addDomainStatisticParameters;
import static org.exoplatform.analytics.utils.AnalyticsUtils.addStatisticData;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

public class ProgramEnabledListener extends Listener<DomainDTO, String> {

  private IdentityManager identityManager;

  private SpaceService    spaceService;

  public ProgramEnabledListener(IdentityManager identityManager, SpaceService spaceService) {
    this.identityManager = identityManager;
    this.spaceService = spaceService;
  }

  @Override
  public void onEvent(Event<DomainDTO, String> event) throws Exception {
    String userId = event.getData();
    DomainDTO domain = event.getSource();
    if (domain == null) {
      return;
    }

    StatisticData statisticData = new StatisticData();
    statisticData.setModule(STATISTICS_GAMIFICATION_MODULE);
    statisticData.setSubModule(STATISTICS_PROGRAM_SUBMODULE);
    statisticData.setOperation(STATISTICS_UPDATE_PROGRAM_OPERATION);
    addDomainStatisticParameters(identityManager, spaceService, domain, statisticData, userId);
    addStatisticData(statisticData);
  }

}

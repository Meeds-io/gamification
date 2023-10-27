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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.upgrade;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityVisibility;

public class ProgramVisibilityUpgradePlugin extends UpgradeProductPlugin {

  private static final Log     LOG                                  = ExoLogger.getLogger(ProgramVisibilityUpgradePlugin.class);

  private static final String  VISIBILITY_PARAM                     = "visibility";

  private static final String  AUDIENCE_IDS_PARAM                   = "audienceIds";

  private static final String  GET_PROGRAM_SPACE_IDS_QUERY          =
                                                           "SELECT DISTINCT p.audienceId FROM GamificationDomain p WHERE p.audienceId > 0";

  private static final String  UPGRADE_PROGRAMS_QUERY               =
                                                      "UPDATE GamificationDomain p SET p.visibility = :" + VISIBILITY_PARAM;

  private static final String  UPGRADE_PROGRAMS_WITH_AUDIENCE_QUERY =
                                                                    UPGRADE_PROGRAMS_QUERY +
                                                                        " WHERE p.audienceId IS NULL OR p.audienceId = 0 OR p.audienceId IN (:" +
                                                                        AUDIENCE_IDS_PARAM + ")";

  private static final String  UPGRADE_PROGRAMS_NO_AUDIENCE_QUERY   =
                                                                  "UPDATE GamificationDomain p SET p.visibility = :" +
                                                                      VISIBILITY_PARAM +
                                                                      " WHERE p.audienceId IS NULL OR p.audienceId = 0";

  private EntityManagerService entityManagerService;

  private SpaceService         spaceService;

  public ProgramVisibilityUpgradePlugin(EntityManagerService entityManagerService,
                                        SpaceService spaceService,
                                        SettingService settingService,
                                        InitParams initParams) {
    super(settingService, initParams);
    this.entityManagerService = entityManagerService;
    this.spaceService = spaceService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    LOG.info("Start:: Upgrade Programs Visibility");
    List<Long> spaceIds = getProgramSpaceIds();
    spaceIds = spaceIds.stream()
                       .map(spaceId -> spaceService.getSpaceById(String.valueOf(spaceId)))
                       .filter(Objects::nonNull)
                       .filter(s -> Space.OPEN.equals(s.getRegistration()))
                       .map(Space::getId)
                       .map(Long::parseLong)
                       .toList();
    upgradeAllProgramsAsRestricted();
    int openProgramsCount = upgradeProgramsAsOpen(spaceIds);
    LOG.info("End:: Upgrade Programs Visibility: {} upgraded as Open.",
             openProgramsCount);
  }

  @ExoTransactional
  public int upgradeAllProgramsAsRestricted() {
    EntityManager entityManager = entityManagerService.getEntityManager();
    Query query = entityManager.createQuery(UPGRADE_PROGRAMS_QUERY);
    query.setParameter(VISIBILITY_PARAM, EntityVisibility.RESTRICTED);
    return query.executeUpdate();
  }

  @ExoTransactional
  public int upgradeProgramsAsOpen(List<Long> programSpaceIds) {
    EntityManager entityManager = entityManagerService.getEntityManager();
    Query query = entityManager.createQuery(programSpaceIds.isEmpty() ? UPGRADE_PROGRAMS_NO_AUDIENCE_QUERY :
                                                                      UPGRADE_PROGRAMS_WITH_AUDIENCE_QUERY);
    query.setParameter(VISIBILITY_PARAM, EntityVisibility.OPEN);
    if (!programSpaceIds.isEmpty()) {
      query.setParameter(AUDIENCE_IDS_PARAM, programSpaceIds);
    }
    return query.executeUpdate();
  }

  @ExoTransactional
  public List<Long> getProgramSpaceIds() {
    EntityManager entityManager = entityManagerService.getEntityManager();
    TypedQuery<Long> query = entityManager.createQuery(GET_PROGRAM_SPACE_IDS_QUERY, Long.class);
    List<Long> result = query.getResultList();
    return result == null ? Collections.emptyList() : result;
  }
}

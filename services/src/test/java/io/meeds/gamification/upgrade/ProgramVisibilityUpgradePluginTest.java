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

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.mock.SpaceServiceMock;
import io.meeds.gamification.test.AbstractServiceTest;

public class ProgramVisibilityUpgradePluginTest extends AbstractServiceTest {// NOSONAR

  public void testProgramUpgrade() {

    InitParams initParams = new InitParams();

    ValueParam valueParam = new ValueParam();
    valueParam.setName("product.group.id");
    valueParam.setValue("org.exoplatform.social");
    initParams.addParam(valueParam);

    valueParam = new ValueParam();
    valueParam.setName("plugin.execution.order");
    valueParam.setValue("5");
    initParams.addParam(valueParam);

    ProgramEntity program1 = newDomain("testProgramUpgrade1");
    ProgramEntity program2 = newDomain("testProgramUpgrade2");
    ProgramEntity program3 = newDomain("testProgramUpgrade3");
    program3.setAudienceId(Long.parseLong(SpaceServiceMock.SPACE_ID_2));
    program3 = programDAO.update(program3);

    EntityManagerService entityManagerService = getContainer().getComponentInstanceOfType(EntityManagerService.class);
    SettingService settingService = getContainer().getComponentInstanceOfType(SettingService.class);
    SpaceService spaceService = getContainer().getComponentInstanceOfType(SpaceService.class);
    ProgramVisibilityUpgradePlugin upgradePlugin = new ProgramVisibilityUpgradePlugin(entityManagerService,
                                                                                      spaceService,
                                                                                      settingService,
                                                                                      initParams);
    upgradePlugin.setName("ProgramVisibilityUpgradePlugin");
    assertTrue(upgradePlugin.isEnabled());

    upgradePlugin.processUpgrade(null, null);
    restartTransaction();

    assertEquals(EntityVisibility.RESTRICTED, programDAO.find(program1.getId()).getVisibility());
    assertEquals(EntityVisibility.RESTRICTED, programDAO.find(program2.getId()).getVisibility());
    assertEquals(EntityVisibility.OPEN, programDAO.find(program3.getId()).getVisibility());
  }

}

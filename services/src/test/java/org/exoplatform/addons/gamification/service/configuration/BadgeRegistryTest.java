/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
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
package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.setting.badge.impl.BadgeRegistryImpl;
import org.exoplatform.addons.gamification.service.setting.badge.model.BadgeConfig;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.file.services.NameSpaceService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.junit.Before;
import org.junit.Test;

public class BadgeRegistryTest extends AbstractServiceTest {

  private BadgeRegistryImpl badgeRegistry;

  @Before
  public void init() throws Exception {
    if (badgeRegistry == null) {
      badgeRegistry = new BadgeRegistryImpl(CommonsUtils.getService(FileService.class),
                                            badgeService,
                                            CommonsUtils.getService(NameSpaceService.class));
    }
  }

  @Test
  public void testAddPlugin() throws Exception {
    InitParams initParams = new InitParams();
    String title = "Test Bagde1";
    addValueParam(initParams, "badge-title", title);
    addValueParam(initParams, "badge-description", "Badge description");
    addValueParam(initParams, "badge-domain", "Development");
    addValueParam(initParams, "badge-icon", "notExisting");
    addValueParam(initParams, "badge-neededScore", "20");
    addValueParam(initParams, "badge-enable", "true");
    badgeRegistry.addPlugin(new BadgeConfig(initParams));
    badgeRegistry.start();

    restartTransaction();

    BadgeDTO badge = badgeService.findBadgeByTitle(title);
    assertNotNull(badge);
    assertEquals(0, badge.getIconFileId());
  }

  @Test
  public void testRemovePlugin() throws Exception {
    InitParams initParams = new InitParams();
    String title = "Test Bagde1";
    addValueParam(initParams, "badge-title", title);
    addValueParam(initParams, "badge-description", "Badge description");
    addValueParam(initParams, "badge-domain", "Development");
    addValueParam(initParams, "badge-icon", "notExisting");
    addValueParam(initParams, "badge-neededScore", "20");
    addValueParam(initParams, "badge-enable", "true");
    BadgeConfig badgeConfig = new BadgeConfig(initParams);
    badgeRegistry.addPlugin(badgeConfig);
    badgeRegistry.remove(badgeConfig);
    badgeRegistry.start();

    restartTransaction();

    BadgeDTO badge = badgeService.findBadgeByTitle(title);
    assertNull(badge);
  }

  private void addValueParam(InitParams initParams, String name, String value) {
    ValueParam valueParam = new ValueParam();
    valueParam.setName(name);
    valueParam.setValue(value);
    initParams.addParameter(valueParam);
  }

}

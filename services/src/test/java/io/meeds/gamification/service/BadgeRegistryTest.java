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
package io.meeds.gamification.service;

import org.junit.Before;
import org.junit.Test;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.plugin.BadgeConfigPlugin;
import io.meeds.gamification.service.impl.BadgeRegistryImpl;
import io.meeds.gamification.test.AbstractServiceTest;

public class BadgeRegistryTest extends AbstractServiceTest {

  private BadgeRegistryImpl badgeRegistry;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    if (badgeRegistry == null) {
      badgeRegistry = new BadgeRegistryImpl(fileService,
                                            badgeService);
    }
  }

  @Test
  public void testAddPlugin() throws Exception {
    InitParams initParams = new InitParams();
    String title = "Test Bagde1";
    addValueParam(initParams, "badge-title", title);
    addValueParam(initParams, "badge-description", "Badge description");
    addValueParam(initParams, "badge-icon", "notExisting");
    addValueParam(initParams, "badge-neededScore", "20");
    addValueParam(initParams, "badge-enable", "true");
    badgeRegistry.addPlugin(new BadgeConfigPlugin(initParams));
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
    addValueParam(initParams, "badge-icon", "notExisting");
    addValueParam(initParams, "badge-neededScore", "20");
    addValueParam(initParams, "badge-enable", "true");
    BadgeConfigPlugin badgeConfig = new BadgeConfigPlugin(initParams);
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

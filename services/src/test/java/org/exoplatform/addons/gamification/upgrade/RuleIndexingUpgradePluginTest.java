/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.exoplatform.addons.gamification.upgrade;

import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.junit.Test;
import org.mockito.Mock;

public class RuleIndexingUpgradePluginTest extends AbstractServiceTest {

  @Mock
  private IndexingService indexingService;

  @Mock
  private SettingService  settingService;

  @Test
  public void testOldRulesIndexing() {
    InitParams initParams = new InitParams();

    ValueParam valueParam = new ValueParam();
    valueParam.setName("product.group.id");
    valueParam.setValue("org.exoplatform.addons.gamification");
    initParams.addParameter(valueParam);

    newRuleDTO();
    newRuleDTO();

    RuleIndexingUpgradePlugin ruleIndexingUpgradePlugin = new RuleIndexingUpgradePlugin(settingService,
                                                                                        indexingService,
                                                                                        ruleService,
                                                                                        initParams);
    ruleIndexingUpgradePlugin.processUpgrade("", "");
    assertEquals(2, ruleIndexingUpgradePlugin.getRulesCount());
    assertEquals(2, ruleIndexingUpgradePlugin.getIndexedRulesCount());
    assertEquals(0, ruleIndexingUpgradePlugin.getErrorIndexingCount());
  }

}

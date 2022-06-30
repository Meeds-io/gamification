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

import org.apache.commons.codec.language.bm.RuleType;
import org.exoplatform.addons.gamification.connector.RuleIndexingServiceConnector;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

public class RuleIndexingUpgradePlugin extends UpgradeProductPlugin {

  private static final Log      log = ExoLogger.getLogger(RuleIndexingUpgradePlugin.class.getName());

  private final IndexingService indexingService;

  private RuleService           ruleService;

  private int                   ruleIndexingCount;

  public RuleIndexingUpgradePlugin(IndexingService indexingService, RuleService ruleService, InitParams initParams) {
    super(initParams);
    this.indexingService = indexingService;
    this.ruleService = ruleService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    log.info("Start indexing old rules");
    long startupTime = System.currentTimeMillis();
    try {
      ruleIndexingCount = 0;
      int limit = 20;
      int offset = 0;
      boolean hasNext = true;
      while (hasNext) {
        List<RuleDTO> rules = ruleService.findAllRules(offset, limit);
        hasNext = rules.size() == limit ? true : false;
        for (RuleDTO ruleDTO : rules) {
          if (TypeRule.MANUAL.equals(ruleDTO.getType()) || TypeRule.AUTOMATIC.equals(ruleDTO.getType()) && ruleDTO.isEnabled() && !ruleDTO.isDeleted()) {
            indexingService.reindex(RuleIndexingServiceConnector.INDEX, String.valueOf(ruleDTO.getId()));
            ruleIndexingCount++;
          }
        }
        offset += limit;
      }
      log.info("End indexing of '{}' old rules. It took {} ms", ruleIndexingCount, (System.currentTimeMillis() - startupTime));
    } catch (Exception e) {
      log.error("An unexpected error occurs when indexing old rules", e);
    }

  }
  /**
   * @return the ruleIndexingCount
   */
  public int getIndexingCount() {
    return ruleIndexingCount;
  }
}

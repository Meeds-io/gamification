package org.exoplatform.addons.gamification.upgrade;

import java.util.List;

import org.exoplatform.addons.gamification.connector.RuleIndexingServiceConnector;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import lombok.Getter;

public class RuleIndexingUpgradePlugin extends UpgradeProductPlugin {

  private static final Log LOG        = ExoLogger.getLogger(RuleIndexingUpgradePlugin.class);

  private static final int PAGE_COUNT = 20;

  private IndexingService  indexingService;

  private RuleService      ruleService;

  /**
   * Used For Test only
   */
  @Getter
  private int              indexedRulesCount;

  /**
   * Used For Test only
   */
  @Getter
  private int              errorIndexingCount;

  /**
   * Used For Test only
   */
  @Getter
  private int              rulesCount;

  public RuleIndexingUpgradePlugin(SettingService settingService,
                                   IndexingService indexingService,
                                   RuleService ruleService,
                                   InitParams params) {
    super(settingService, params);
    this.indexingService = indexingService;
    this.ruleService = ruleService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) { // NOSONAR
    rulesCount = ruleService.countAllRules();
    LOG.info("START::Index '{}' Gamification rules", rulesCount);

    long startTime = System.currentTimeMillis();
    int offset = 0;
    while (offset < rulesCount) {
      List<RuleDTO> rules = ruleService.findAllRules(offset, PAGE_COUNT);
      for (RuleDTO rule : rules) {
        if (!rule.isDeleted() && (rule.isEnabled() || TypeRule.MANUAL == rule.getType())) {
          long ruleId = rule.getId();
          try {
            indexingService.reindex(RuleIndexingServiceConnector.INDEX, String.valueOf(ruleId));
            indexedRulesCount++;
          } catch (Exception e) {
            LOG.warn("Error while reindexing rule with id {}", ruleId, e);
            errorIndexingCount++;
          }
        }
        if ((indexedRulesCount + errorIndexingCount) % 10 == 0) {
          LOG.info("PROGRESS::Index '{}/{}' gamification rules processed.",
                   indexedRulesCount,
                   rulesCount);
        }
      }
      offset += PAGE_COUNT;
    }

    if (errorIndexingCount > 0) {
      long diff = (System.currentTimeMillis() - startTime);
      throw new IllegalStateException("END::Index '" + indexedRulesCount + "/" + rulesCount
          + "' gamification rules indexed successfully with '" + errorIndexingCount + "/" + rulesCount
          + "' rules idnexing failed. The operation took " + diff + " milliseconds.It will be re-attempted next startup");
    } else {
      LOG.info("END::Index '{}/{}' gamification rules indexed successfully. The operation took {} milliseconds.",
               indexedRulesCount,
               rulesCount,
               (System.currentTimeMillis() - startTime));
    }
  }
}

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

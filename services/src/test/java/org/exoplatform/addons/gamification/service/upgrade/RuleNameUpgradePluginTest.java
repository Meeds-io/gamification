package org.exoplatform.addons.gamification.service.upgrade;

import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.upgrade.RuleNameUpgradePlugin;
import org.exoplatform.container.xml.InitParams;
import org.junit.Test;

public class RuleNameUpgradePluginTest extends AbstractServiceTest {
  @Test
  public void testUpgradeRuleName() {
    newRule("GAMIFICATION_DEFAULT_DATA_PREFIX","domain");
    
    RuleDTO rule = ruleService.findRuleByTitle("GAMIFICATION_DEFAULT_DATA_PREFIX_domain");
    assertTrue(rule.getTitle().startsWith("GAMIFICATION_DEFAULT_DATA_PREFIX"));
    
    Long id= rule.getId();
  
    RuleNameUpgradePlugin upgradePlugin = new RuleNameUpgradePlugin(null,new InitParams(),ruleService);
    upgradePlugin.processUpgrade("0","0");
    RuleDTO ruleAfter = ruleService.findRuleById(id);
    assertFalse(ruleAfter.getTitle().startsWith("GAMIFICATION_DEFAULT_DATA_PREFIX"));
  
  }
}

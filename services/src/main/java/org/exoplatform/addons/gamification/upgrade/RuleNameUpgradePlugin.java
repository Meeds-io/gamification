package org.exoplatform.addons.gamification.upgrade;


import org.exoplatform.addons.gamification.GamificationConstant;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.setting.rule.impl.RuleRegistryImpl;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

public class RuleNameUpgradePlugin extends UpgradeProductPlugin {
    private              RuleService ruleService;
    private static final Log         LOG = ExoLogger.getLogger(RuleNameUpgradePlugin.class);

    public RuleNameUpgradePlugin(SettingService settingService, InitParams initParams, RuleService ruleService) {
        
        super(settingService, initParams);
        this.ruleService=ruleService;
        
    }
    
    @Override
    public void processUpgrade(String oldVersion, String newVersion) {

        List<RuleDTO> rules = ruleService.getAllRules();
        
        rules.stream()
             .filter(ruleDTO -> ruleDTO.getTitle().startsWith("GAMIFICATION_DEFAULT_DATA_PREFIX"))
             .forEach(ruleDTO -> {
                 String ruleTitle= ruleDTO.getTitle();
                 ruleTitle=ruleTitle.replace("GAMIFICATION_DEFAULT_DATA_PREFIX",
                                             GamificationConstant.GAMIFICATION_DEFAULT_DATA_PREFIX);
                 ruleDTO.setTitle(ruleTitle);
                 try {
                     ruleService.updateRule(ruleDTO);
                 } catch (Exception e) {
                     LOG.error("Unable to update Rule "+ruleDTO.getTitle());
                 }
             });
        
        
    }
}

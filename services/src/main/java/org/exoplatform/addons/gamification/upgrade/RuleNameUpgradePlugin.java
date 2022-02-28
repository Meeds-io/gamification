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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.upgrade;


import org.exoplatform.addons.gamification.GamificationConstant;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
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

        List<RuleDTO> rules = ruleService.getAllAutomaticRules();
        RequestLifeCycle.begin(PortalContainer.getInstance());
        rules.stream()
             .filter(ruleDTO -> ruleDTO.getTitle().startsWith("GAMIFICATION_DEFAULT_DATA_PREFIX"))
             .forEach(ruleDTO -> {
                 try {
                 String ruleTitle= ruleDTO.getTitle();
                 ruleTitle=ruleTitle.replace("GAMIFICATION_DEFAULT_DATA_PREFIX",
                                             GamificationConstant.GAMIFICATION_DEFAULT_DATA_PREFIX);
                 ruleDTO.setTitle(ruleTitle);

                     ruleService.updateRule(ruleDTO);
                 } catch (Exception e) {
                     LOG.error("Unable to update Rule "+ruleDTO.getTitle());
                 } finally {
                     RequestLifeCycle.end();
                 }
             });
        
        
    }
}

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
package org.exoplatform.addons.gamification.service.setting.rule.impl;

import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.setting.rule.RuleRegistry;
import org.exoplatform.addons.gamification.service.setting.rule.model.RuleConfig;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import static org.exoplatform.addons.gamification.GamificationConstant.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RuleRegistryImpl implements Startable, RuleRegistry {

    private static final Log LOG = ExoLogger.getLogger(RuleRegistryImpl.class);

    // Gamification Settings
    public static final String GAMIFICATION_SETTINGS_RULE_KEY = "GAMIFICATION_SETTINGS_RULE_KEY";

    //Compute the status of gamification's rules processing (true if rules are processed successfully)
    public static final String GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE = "GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE";

    private final Map<String, RuleConfig> ruleMap;

    protected RuleService ruleService;

    private DomainService domainService;

    public RuleRegistryImpl() {
        this.ruleMap = new HashMap<String, RuleConfig>();
    }

    @Override
    public void addPlugin(RuleConfig rule) {
        ruleMap.put(rule.getTitle(), rule);

    }

    @Override
    public boolean remove(RuleConfig rule) {
        ruleMap.remove(rule.getTitle());
        return true;
    }

    @Override
    public void start() {

        ruleService = CommonsUtils.getService(RuleService.class);

        try {
            // Processing registered rules

            for (RuleConfig rule : ruleMap.values()) {
                RuleDTO ruleDTO = ruleService.findRuleByTitle(GAMIFICATION_DEFAULT_DATA_PREFIX+rule.getTitle());
                if (ruleDTO == null || !(ruleDTO.getEvent().equals(rule.getEvent()))) {
                    store(rule,ruleDTO);
                }
            }


        } catch (Exception e) {
            LOG.error("Error when processing Rules ", e);
        }
    }

    @Override
    public void stop() {

    }

    /**
     * Persist new rule within DB
     *
     * @param ruleConfig
     */
    private void store(RuleConfig ruleConfig,RuleDTO ruleDTO) {

        domainService = CommonsUtils.getService(DomainService.class);

        try {

            if (ruleDTO != null) {
                ruleDTO.setTitle(GAMIFICATION_DEFAULT_DATA_PREFIX+ruleConfig.getTitle());
                ruleDTO.setDescription(ruleConfig.getDescription());
                ruleDTO.setEvent(ruleConfig.getEvent());
                CommonsUtils.getService(RuleService.class).updateRule(ruleDTO);
            }else{
                RuleDTO ruleDto = new RuleDTO();
                ruleDto.setTitle(GAMIFICATION_DEFAULT_DATA_PREFIX+ruleConfig.getTitle());
                ruleDto.setScore(ruleConfig.getScore());
                ruleDto.setEnabled(ruleConfig.isEnable());
                ruleDto.setEvent(ruleConfig.getEvent());
                ruleDto.setLastModifiedDate(new Date());
                ruleDto.setLastModifiedBy("Gamification");
                ruleDto.setCreatedBy("Gamification");
                ruleDto.setArea(ruleConfig.getZone());
                ruleDto.setEnabled(true);
                ruleDto.setDeleted(false);
                ruleDto.setDomainDTO(domainService.findDomainByTitle(ruleConfig.getZone()));
                if(ruleDto.getDomainDTO().isEnabled()==false){
                    ruleDto.setEnabled(false);
                }
                ruleDto.setDescription(ruleConfig.getDescription());
                ruleDto.setCreatedDate(new Date());
                CommonsUtils.getService(RuleService.class).addRule(ruleDto);
            }

        } catch (Exception e) {
            LOG.error("Error when saving Rule ", e);
        }

    }

    private boolean isRulesProcessingDone() {
        SettingValue<?> setting = CommonsUtils.getService(SettingService.class).get(Context.GLOBAL, Scope.APPLICATION.id(GAMIFICATION_SETTINGS_RULE_KEY), GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE);
        return (setting != null && setting.getValue().equals("true"));
    }

    private void setRuleProcessingSettingsDone() {
        CommonsUtils.getService(SettingService.class).set(Context.GLOBAL, Scope.APPLICATION.id(GAMIFICATION_SETTINGS_RULE_KEY), GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE, SettingValue.create("true"));
    }
}

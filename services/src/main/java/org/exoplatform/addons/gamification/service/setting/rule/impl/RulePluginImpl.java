package org.exoplatform.addons.gamification.service.setting.rule.impl;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.setting.rule.model.RuleConfig;
import org.exoplatform.addons.gamification.service.setting.rule.RulePlugin;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RulePluginImpl implements Startable, RulePlugin {

    private static final Log LOG = ExoLogger.getLogger(RulePluginImpl.class);

    private static final String GAMIFICATION_PREFERENCES = "gamification:";

    // Gamification Settings
    public static final String GAMIFICATION_SETTINGS_RULE_KEY = "GAMIFICATION_SETTINGS_RULE_KEY";

    //Compute the status of gamification's rules processing (true if rules are processed successfully)
    public static final String GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE = "GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE";

    private final Map<String, RuleConfig> ruleMap;

    public RulePluginImpl() {
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

        try {
            // Processing registered rules
            if (!isRulesProcessingDone()) {
                for (RuleConfig rule : ruleMap.values()) {
                    store(rule);

                }

                //Update processessing status
                setRuleProcessingSettingsDone();
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
     * @param ruleConfig
     */
    private void store(RuleConfig ruleConfig) {


        RuleDTO ruleDto = new RuleDTO();

        ruleDto.setTitle(ruleConfig.getTitle());
        ruleDto.setScore(ruleConfig.getScore());
        ruleDto.setEnabled(ruleConfig.isEnable());
        ruleDto.setLastModifiedDate(new Date());
        ruleDto.setLastModifiedBy("Gamification");
        ruleDto.setCreatedBy("Gamification");
        ruleDto.setArea(ruleConfig.getZone());
        ruleDto.setDescription(ruleConfig.getDescription());
        ruleDto.setCreatedDate(new Date());

        CommonsUtils.getService(RuleService.class).addRule(ruleDto);

    }

    private boolean isRulesProcessingDone() {
        SettingValue<?> setting = CommonsUtils.getService(SettingService.class).get(Context.GLOBAL, Scope.APPLICATION.id(GAMIFICATION_SETTINGS_RULE_KEY), GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE);
        return (setting != null && setting.getValue().equals("true"));
    }

    private void setRuleProcessingSettingsDone() {
        CommonsUtils.getService(SettingService.class).set(Context.GLOBAL, Scope.APPLICATION.id(GAMIFICATION_SETTINGS_RULE_KEY), GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE, SettingValue.create("true"));
    }
}

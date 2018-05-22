package org.exoplatform.gamification.service.setting.rule;

import org.exoplatform.gamification.service.setting.rule.model.RuleConfig;

public interface RulePlugin {

    void addPlugin(RuleConfig rule);


    boolean remove(RuleConfig rule);
}

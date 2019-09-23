package org.exoplatform.addons.gamification.service.setting.rule.model;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

public class RuleConfig extends BaseComponentPlugin {

    private String title;

    private String description;

    private String event;

    private int score;

    private String zone;

    private boolean enable;


    public RuleConfig(InitParams params) throws Exception {

        ValueParam titleParam = params.getValueParam("rule-title");

        if (titleParam == null) {
            throw new ConfigurationException("No 'rule-title' parameter found");
        } else {
            title = titleParam.getValue();
        }

        ValueParam descriptionParam = params.getValueParam("rule-description");

        if (descriptionParam != null) {
            description = descriptionParam.getValue();
        }

        ValueParam eventParam = params.getValueParam("rule-event");

        if (eventParam != null) {
            event = eventParam.getValue();
        }else{
            event=title;
        }

        ValueParam scoreParam = params.getValueParam("rule-score");

        if (scoreParam != null) {
            score = Integer.parseInt(scoreParam.getValue());
        }

        ValueParam zoneParam = params.getValueParam("rule-zone");

        if (zoneParam != null) {
            zone = zoneParam.getValue();
        }

        ValueParam enableParam = params.getValueParam("rule-enable");

        if (enableParam != null) {
            enable = Boolean.parseBoolean(enableParam.getValue());
        }

    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getScore() {
        return score;
    }

    public String getZone() {
        return zone;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getEvent() {
        return event;
    }

}

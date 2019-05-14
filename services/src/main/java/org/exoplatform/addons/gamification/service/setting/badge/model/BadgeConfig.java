package org.exoplatform.addons.gamification.service.setting.badge.model;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

public class BadgeConfig extends BaseComponentPlugin {

    private String title;

    private String description;

    private String domain;

    private String icon;

    private int neededScore;

    private boolean enable;


    public BadgeConfig(InitParams params) throws Exception {
        ValueParam titleParam = params.getValueParam("badge-title");

        if (titleParam == null) {
            throw new ConfigurationException("No 'badge-title' parameter found");
        } else {
            title = titleParam.getValue();
        }

        ValueParam descriptionParam = params.getValueParam("badge-description");

        if (descriptionParam != null) {
            description = descriptionParam.getValue();
        }

        ValueParam domainParam = params.getValueParam("badge-domain");

        if (domainParam != null) {
            domain = domainParam.getValue();
        }

        ValueParam iconParam = params.getValueParam("badge-icon");

        if (iconParam != null) {
            icon = iconParam.getValue();
        }

        ValueParam neededScoreParam = params.getValueParam("badge-neededScore");

        if (neededScoreParam != null) {
            neededScore = Integer.parseInt(neededScoreParam.getValue());
        }

        ValueParam enableParam = params.getValueParam("badge-enable");

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

    public String getDomain() {
        return domain;
    }

    public String getIcon() {
        return icon;
    }

    public int getNeededScore() {
        return neededScore;
    }

    public boolean isEnable() {
        return enable;
    }
}
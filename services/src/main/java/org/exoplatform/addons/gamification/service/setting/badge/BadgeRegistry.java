package org.exoplatform.addons.gamification.service.setting.badge;

import org.exoplatform.addons.gamification.service.setting.badge.model.BadgeConfig;

public interface BadgeRegistry {

    void addPlugin(BadgeConfig badge);


    boolean remove(BadgeConfig badgeConfig);

}

package org.exoplatform.addons.gamification.service.setting.zone;


import org.exoplatform.addons.gamification.service.setting.zone.model.ZoneConfig;

public interface ZoneRegistry {

    void addPlugin(ZoneConfig zone);


    boolean remove(ZoneConfig zone);

}

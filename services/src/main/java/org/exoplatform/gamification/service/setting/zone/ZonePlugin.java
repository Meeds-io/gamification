package org.exoplatform.gamification.service.setting.zone;


import org.exoplatform.gamification.service.setting.zone.model.ZoneConfig;

public interface ZonePlugin {

    void addPlugin(ZoneConfig zone);


    boolean remove(ZoneConfig zone);

}

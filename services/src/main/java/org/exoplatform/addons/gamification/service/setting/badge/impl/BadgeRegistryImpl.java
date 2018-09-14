package org.exoplatform.addons.gamification.service.setting.badge.impl;

import org.exoplatform.addons.gamification.service.setting.badge.BadgeRegistry;
import org.exoplatform.commons.file.services.FileService;
import org.picocontainer.Startable;

public class BadgeRegistryImpl implements Startable, BadgeRegistry {

    protected FileService fileService = null;

    public BadgeRegistryImpl(FileService fileService) {

        this.fileService = fileService;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }
}

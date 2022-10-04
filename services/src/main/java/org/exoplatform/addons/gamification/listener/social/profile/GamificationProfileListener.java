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
package org.exoplatform.addons.gamification.listener.social.profile;

import static org.exoplatform.addons.gamification.GamificationConstant.*;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;

public class GamificationProfileListener extends ProfileListenerPlugin {

    protected RuleService ruleService;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationProfileListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void avatarUpdated(ProfileLifeCycleEvent event) {

        Long lastUpdate = event.getProfile().getAvatarLastUpdated();
        String receiver = event.getProfile().getIdentity().getId();
        String profilUrl = event.getProfile().getUrl();
        // Do not reward a user when he update his avatar, reward user only when he add an avatar for the first time
        if (lastUpdate != null) {
            return;
        }
        gamificationService.createHistory(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR,
                receiver,
                receiver,
                profilUrl);
    }

    @Override
    public void bannerUpdated(ProfileLifeCycleEvent event) {

        Long lastUpdate = event.getProfile().getBannerLastUpdated();

        // Do not reward a user when he update his banner, reward user only when he add a banner for the first time
        if (lastUpdate != null) {
            return;
        }
        String receiver = event.getProfile().getId();
        String profilUrl = event.getProfile().getUrl();
        gamificationService.createHistory(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER,
                receiver,
                receiver,
                profilUrl);
    }

    @Override
    public void basicInfoUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void contactSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void experienceSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void headerSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void createProfile(ProfileLifeCycleEvent event) {


    }

    @Override
    public void aboutMeUpdated(ProfileLifeCycleEvent event) {
        
        String sender = event.getProfile().getId();
        String receiver = event.getProfile().getIdentity().getId();
        String profilUrl = event.getProfile().getUrl();

        gamificationService.createHistory(GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME,
                sender,
                receiver,
                profilUrl);
    }


}

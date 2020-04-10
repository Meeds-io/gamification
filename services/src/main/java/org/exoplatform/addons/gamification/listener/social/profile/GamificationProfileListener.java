package org.exoplatform.addons.gamification.listener.social.profile;

import static org.exoplatform.addons.gamification.GamificationConstant.*;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;

public class GamificationProfileListener extends ProfileListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationProfileListener.class);

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
        // Do not reward a user when he update his avatar, reward user only when he add an avatar for the first time
        if (lastUpdate != null) return;
        gamificationService.createHistory(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR, event.getProfile().getId(),event.getProfile().getId(),"/portal/intranet/profile/");

    }

    @Override
    public void bannerUpdated(ProfileLifeCycleEvent event) {

        GamificationActionsHistory aHistory = null;

        Long lastUpdate = event.getProfile().getBannerLastUpdated();

        // Do not reward a user when he update his avatar, reward user only when he add an avatar for the first time
        if (lastUpdate != null) return;
        String receiver =event.getProfile().getId();
        gamificationService.createHistory(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER, receiver,receiver,"/portal/intranet/profile/");
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
        gamificationService.createHistory(GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME,event.getProfile().getId(),event.getProfile().getIdentity().getId(),"/portal/intranet/profile/"+event.getProfile().getIdentity().getId());

    }


}

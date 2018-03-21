package org.exoplatform.gamification.listener.social.profile;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;

public class GamificationProfileListener extends ProfileListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationProfileListener.class);

    @Override
    public void avatarUpdated(ProfileLifeCycleEvent event) {
    }

    @Override
    public void bannerUpdated(ProfileLifeCycleEvent event) {
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
        Profile profile = event.getProfile();

    }

    @Override
    public void aboutMeUpdated(ProfileLifeCycleEvent event) {

    }
}

package org.exoplatform.gamification.listener.social.activity;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

public class GamificationActivityListener extends ActivityListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationActivityListener.class);
    @Override
    public void saveActivity(ActivityLifeCycleEvent event) {
        ExoSocialActivity activity = event.getSource();

    }

    @Override
    public void updateActivity(ActivityLifeCycleEvent event) {
    }

    @Override
    public void saveComment(ActivityLifeCycleEvent event) {
        ExoSocialActivity activity = event.getSource();

    }

    @Override
    public void likeActivity(ActivityLifeCycleEvent event) {
        ExoSocialActivity activity = event.getSource();

    }

    @Override
    public void likeComment(ActivityLifeCycleEvent event) {
        ExoSocialActivity activity = event.getSource();

    }


}

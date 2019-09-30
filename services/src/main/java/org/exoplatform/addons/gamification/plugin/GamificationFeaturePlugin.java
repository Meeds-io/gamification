package org.exoplatform.addons.gamification.plugin;

import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.commons.api.settings.FeaturePlugin;

public class GamificationFeaturePlugin  extends FeaturePlugin {

    private static final String  GAMIFICATION_FEATURE_NAME = "gamification";

    @Override
    public String getName() {
        return GAMIFICATION_FEATURE_NAME;
    }

    @Override
    public boolean isFeatureActiveForUser(String featureName, String username) {
        return true;
    }


}

package org.exoplatform.addons.gamification.service.effective;

import java.io.Serializable;

public class StandardLeaderboard extends AbstractLeaderboard implements Serializable {

    public StandardLeaderboard(String userSocialId, long reputationScore) {
        this.userSocialId = userSocialId;
        this.reputationScore = reputationScore;
    }
}

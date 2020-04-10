package org.exoplatform.addons.gamification.service.effective;

import java.io.Serializable;

public class StandardLeaderboard extends AbstractLeaderboard implements Serializable {

    public StandardLeaderboard(String earnerId, long reputationScore) {
        this.earnerId = earnerId;
        this.reputationScore = reputationScore;
    }
}

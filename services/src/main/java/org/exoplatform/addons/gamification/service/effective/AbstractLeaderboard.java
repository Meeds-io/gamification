package org.exoplatform.addons.gamification.service.effective;

import java.io.Serializable;

abstract public class AbstractLeaderboard implements Serializable {

    protected String userSocialId;
    protected long reputationScore;

    public String getUserSocialId() {
        return userSocialId;
    }

    public void setUserSocialId(String userSocialId) {
        this.userSocialId = userSocialId;
    }

    public long getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(long reputationScore) {
        this.reputationScore = reputationScore;
    }
}

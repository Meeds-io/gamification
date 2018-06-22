package org.exoplatform.addons.gamification.service.dto.effective;

import java.io.Serializable;

public class UserBadge implements Serializable {

    private String zone;

    private long score;

    public UserBadge() {
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}

package org.exoplatform.addons.gamification.service.effective;

import java.io.Serializable;

public class ProfileReputation implements Serializable {

    private String domain;
    private long score;

    public ProfileReputation() {

    }

    public ProfileReputation(String domain, long score) {
        this.domain = domain;
        this.score = score;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}

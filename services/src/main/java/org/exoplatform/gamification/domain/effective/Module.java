package org.exoplatform.gamification.domain.effective;

import java.io.Serializable;
import java.util.Set;

public class Module implements Serializable {

    String title;

    int score;

    Set<Badge> badges;

    public Module() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Set<Badge> getBadges() {
        return badges;
    }

    public void setBadges(Set<Badge> badges) {
        this.badges = badges;
    }
}

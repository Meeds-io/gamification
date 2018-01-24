package org.exoplatform.gamification.domain.effective;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class GamificationEntry implements Serializable {

    String username;

    int score;

    Date lastUpdate;

    Set<Module> modules;

    public GamificationEntry() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }
}

package org.exoplatform.gamification.service.effective;

import java.io.Serializable;

public class Leaderboard implements Serializable {

    private String userId;
    private long Score;

    public Leaderboard() {
    }

    public Leaderboard(String userId, long score) {
        this.userId = userId;
        Score = score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getScore() {
        return Score;
    }

    public void setScore(long score) {
        Score = score;
    }
}

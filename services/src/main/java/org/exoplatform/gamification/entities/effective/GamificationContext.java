package org.exoplatform.gamification.entities.effective;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.Serializable;
import java.time.Instant;

public class GamificationContext implements Serializable {

    private static final Log LOG = ExoLogger.getLogger(GamificationContext.class);

    protected String userName;

    protected Long score;

    protected Instant lastModifiedDate;

    protected Instant createdDate;

    public GamificationContext() {
        this.setLastModifiedDate(Instant.now());
    }

    public static GamificationContext instance() {
        return new GamificationContext();
    }

    public String getUserName() {
        return userName;
    }

    public GamificationContext setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Long getScore() {
        return score;
    }

    public GamificationContext setScore(Long score) {
        this.score = score;
        return this;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public GamificationContext setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public GamificationContext setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    @Override
    public GamificationContext clone() {
        return clone(false);
    }

    public GamificationContext end() {
        return this;
    }

    public GamificationContext clone(boolean isNew) {
        GamificationContext game = instance();
        game.setUserName(userName)
                .setScore(score)
                .setLastModifiedDate(lastModifiedDate)
                .setCreatedDate(createdDate);
        if (!isNew) {
            LOG.info("Is a new Instance");

        }
        return game;
    }
}

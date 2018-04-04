package org.exoplatform.gamification.entities.effective;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class GamificationContext implements Serializable {

    private static final Log LOG = ExoLogger.getLogger(GamificationContext.class);

    protected String actorUserName;

    protected List<String> targetUserName;

    protected boolean isSpaceActivity;

    protected Long actorScore;

    protected Long targetScore;

    protected Instant lastModifiedDate;

    protected Instant createdDate;

    public GamificationContext() {
        this.setLastModifiedDate(Instant.now());
    }

    public static GamificationContext instance() {
        return new GamificationContext();
    }

    public Long getActorScore() {
        return actorScore;
    }

    public GamificationContext setActorScore(Long actorScore) {
        this.actorScore = actorScore;
        return this;
    }

    public Long getTargetScore() {
        return targetScore;
    }

    public GamificationContext setTargetScore(Long targetScore) {
        this.targetScore = targetScore;
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

    public String getActorUserName() {
        return actorUserName;
    }

    public GamificationContext setActorUserName(String actorUserName) {
        this.actorUserName = actorUserName;
        return this;
    }

    public List<String> getTargetUserName() {
        return targetUserName;
    }

    public GamificationContext setTargetUserName(List<String> targetUserName) {
        this.targetUserName = targetUserName;
        return this;
    }

    public boolean isSpaceActivity() {
        return isSpaceActivity;
    }

    public GamificationContext setSpaceActivity(boolean spaceActivity) {
        isSpaceActivity = spaceActivity;
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
        game.setActorScore(actorScore)
                .setTargetScore(targetScore)
                .setActorUserName(actorUserName)
                .setTargetUserName(targetUserName)
                .setSpaceActivity(isSpaceActivity)
                .setLastModifiedDate(lastModifiedDate)
                .setCreatedDate(createdDate);
        if (!isNew) {
            LOG.info("Is a new Instance");

        }
        return game;
    }
    @Override
    public String toString() {
        return "GamificationContext{" +
                "actorUserName='" + actorUserName + '\'' +
                ", targetUserName='" + targetUserName + '\'' +
                ", isSpaceActivity='" + isSpaceActivity + '\'' +
                ", actorScore='" + actorScore + '\'' +
                ", targetScore='" + targetScore + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", createdDate='" + createdDate + '\'' +
                "}";
    }
}

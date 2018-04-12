package org.exoplatform.gamification.entities.effective;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class GamificationContext implements Serializable {

    private static final Log LOG = ExoLogger.getLogger(GamificationContext.class);

    protected SourceContextHolder sourceContextHolder;

    protected TargetContextholder targetContextholder;

    private GamificationContext() {

    }
    public static GamificationContext instance() {
        return new GamificationContext();
    }

    @Override
    public GamificationContext clone() {
        return clone(false);
    }

    public GamificationContext end() {
        return this;
    }

    public SourceContextHolder getSourceContextHolder() {
        return sourceContextHolder;
    }

    public GamificationContext setSourceContextHolder(SourceContextHolder sourceContextHolder) {
        this.sourceContextHolder = sourceContextHolder;
        return this;
    }

    public TargetContextholder getTargetContextholder() {
        return targetContextholder;
    }

    public GamificationContext setTargetContextholder(TargetContextholder targetContextholder) {
        this.targetContextholder = targetContextholder;
        return this;
    }

    public GamificationContext clone(boolean isNew) {

        GamificationContext game = instance();

        game.setSourceContextHolder(sourceContextHolder);
        game.setTargetContextholder(targetContextholder);

        if (!isNew) {
            LOG.info("Is a new Instance");

        }
        return game;
    }
    @Override
    public String toString() {
        return "GamificationContext{" +
                "Source:username='" + sourceContextHolder.getUsername() + '\'' +
                ", Source:score='" + sourceContextHolder.getScore() + '\'' +
                ", Source:createdDate='" + sourceContextHolder.getCreatedDate() + '\'' +
                ", Source:lastModifiedDate='" + sourceContextHolder.getLastModifiedDate() + '\'' +
                ", Target:usernames='" + targetContextholder.getUsernames() + '\'' +
                ", Target:score ='" + targetContextholder.getScore() + '\'' +
                "}";
    }
}

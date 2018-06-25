package org.exoplatform.addons.gamification.entities.effective;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.Serializable;

public class GamificationContext implements Serializable {

    private static final Log LOG = ExoLogger.getLogger(GamificationContext.class);

    protected SourceContext sourceContext;

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

    public SourceContext getSourceContext() {
        return sourceContext;
    }

    public GamificationContext setSourceContext(SourceContext sourceContext) {
        this.sourceContext = sourceContext;
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

        game.setSourceContext(sourceContext);

        game.setTargetContextholder(targetContextholder);

        if (!isNew) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Is not a new Instance");
            }


        }
        return game;
    }
    @Override
    public String toString() {
        return "GamificationContext{" +
                "Source:username='" + sourceContext.getUsername() + '\'' +
                ", Source:score='" + sourceContext.getScore() + '\'' +
                ", Source:createdDate='" + sourceContext.getCreatedDate() + '\'' +
                ", Source:lastModifiedDate='" + sourceContext.getLastModifiedDate() + '\'' +
                ", Target:usernames='" + targetContextholder.getUsernames() + '\'' +
                ", Target:score ='" + targetContextholder.getScore() + '\'' +
                "}";
    }
}

package org.exoplatform.gamification.listener.social.relationship;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.RelationshipListenerPlugin;
import org.exoplatform.social.core.relationship.model.Relationship;

public class GamificationRelationshipListener extends RelationshipListenerPlugin {

    private static final Log LOG = ExoLogger.getLogger(GamificationRelationshipListener.class);

    @Override
    public void confirmed(RelationshipEvent event) {
        Relationship relationship = event.getPayload();
        try {

        } catch (Exception e) {

        }

    }

    @Override
    public void ignored(RelationshipEvent event) {

    }

    @Override
    public void removed(RelationshipEvent event) {

    }

    @Override
    public void requested(RelationshipEvent event) {
        Relationship relationship = event.getPayload();
        try {

        } catch (Exception e) {

        }
    }

    @Override
    public void denied(RelationshipEvent event) {

    }
}

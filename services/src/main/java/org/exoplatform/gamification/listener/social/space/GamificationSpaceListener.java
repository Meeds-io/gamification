package org.exoplatform.gamification.listener.social.space;

import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

public class GamificationSpaceListener extends SpaceListenerPlugin {
    @Override
    public void spaceCreated(SpaceLifeCycleEvent event) {
    }

    @Override
    public void spaceRemoved(SpaceLifeCycleEvent event) {
    }

    @Override
    public void applicationAdded(SpaceLifeCycleEvent event) {
    }

    @Override
    public void applicationRemoved(SpaceLifeCycleEvent event) {
    }

    @Override
    public void applicationActivated(SpaceLifeCycleEvent event) {
    }

    @Override
    public void applicationDeactivated(SpaceLifeCycleEvent event) {
    }

    @Override
    public void joined(SpaceLifeCycleEvent event) {
    }

    @Override
    public void left(SpaceLifeCycleEvent event) {
    }

    @Override
    public void grantedLead(SpaceLifeCycleEvent event) {
    }

    @Override
    public void revokedLead(SpaceLifeCycleEvent event) {
    }

    @Override
    public void spaceRenamed(SpaceLifeCycleEvent event) {
    }

    @Override
    public void spaceDescriptionEdited(SpaceLifeCycleEvent event) {
    }

    @Override
    public void spaceAvatarEdited(SpaceLifeCycleEvent event) {
    }

    @Override
    public void spaceAccessEdited(SpaceLifeCycleEvent event) {
    }

    @Override
    public void addInvitedUser(SpaceLifeCycleEvent event) {
        Space space = event.getSpace();
        String userId = event.getTarget();


    }

    @Override
    public void addPendingUser(SpaceLifeCycleEvent event) {
        Space space = event.getSpace();
        String userId = event.getTarget();


    }

    @Override
    public void spaceBannerEdited(SpaceLifeCycleEvent event) {
    }
}

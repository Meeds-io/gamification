package org.exoplatform.addons.gamification.mock;

import org.exoplatform.services.naming.InitialContextInitializer;
import org.picocontainer.Startable;

public class DummyDependantComponent implements Startable {

    private final InitialContextInitializer jndiInitializer;

    public DummyDependantComponent(InitialContextInitializer jndiInitializer) {
        this.jndiInitializer = jndiInitializer;
    }

    @Override
    public void start() {
        // nothing
    }

    @Override
    public void stop() {
        // nothing
    }
}

package org.exoplatform.addons.gamification.migration;

import org.exoplatform.addons.gamification.migration.datamodel.DataModelMigration;
import org.exoplatform.commons.cluster.StartableClusterAware;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;

import javax.servlet.ServletContext;

public class GamificationAsyncMigrationService implements StartableClusterAware {

    DataModelMigration dataModelMigration = null;

    public GamificationAsyncMigrationService(DataModelMigration dataModelMigration) {
        this.dataModelMigration = dataModelMigration;
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() {
        PortalContainer.addInitTask(PortalContainer.getInstance().getPortalContext(), new RootContainer.PortalContainerPostInitTask() {
            @Override
            public void execute(ServletContext context, PortalContainer portalContainer) {
                // Migrate & cleanup asynchronously
                dataModelMigration.migrate();
            }
        });

    }

    @Override
    public boolean isDone() {
        return false;
    }
}

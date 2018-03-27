package org.exoplatform.gamification.service.effective;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.gamification.entities.effective.GamificationContext;
import org.exoplatform.gamification.service.completation.GamificationCompletionService;
import org.exoplatform.gamification.service.configuration.BadgeService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.concurrent.Callable;

public class GamificationService {

    private static final Log LOG = ExoLogger.getLogger(GamificationService.class);

    protected final BadgeService badgeService;
    protected final GamificationCompletionService gamificationCompletionService;

    public GamificationService() {
        badgeService = CommonsUtils.getService(BadgeService.class);
        gamificationCompletionService = CommonsUtils.getService(GamificationCompletionService.class);
    }

    public boolean process(final GamificationContext ctx) {
        try {

            Callable<Boolean> task = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        ExoContainerContext.setCurrentContainer(PortalContainer.getInstance());
                        if (ctx != null) {
                            execute(ctx);
                        }
                    } catch (Exception e) {
                        LOG.warn("Process GamificationContext is failed: " + e.getMessage(), e);
                        LOG.debug(e.getMessage(), e);
                        return false;
                    }
                    //
                    return true;
                }
            };
            gamificationCompletionService.addTask(task);

            return true;
        } catch (Exception e) {

            return false;
        }

    }

    private void execute(GamificationContext game) throws Exception {
        LOG.info("Working for game {}", game);



    }
}

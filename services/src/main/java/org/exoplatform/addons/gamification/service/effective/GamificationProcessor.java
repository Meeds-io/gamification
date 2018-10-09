package org.exoplatform.addons.gamification.service.effective;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.completation.GamificationCompletionService;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationContextHolder;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;
import java.util.concurrent.Callable;

public class GamificationProcessor {

    private static final Log LOG = ExoLogger.getLogger(GamificationProcessor.class);

    protected final GamificationCompletionService gamificationCompletionService;
    protected final GamificationService gamificationService;

    public GamificationProcessor() {

        gamificationCompletionService = CommonsUtils.getService(GamificationCompletionService.class);

        gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    public boolean process(final List<GamificationContextHolder> gamificationContexts) {
        try {

            Callable<Boolean> task = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        ExoContainerContext.setCurrentContainer(PortalContainer.getInstance());
                        if (gamificationContexts != null && !gamificationContexts.isEmpty()) {
                            //execute(gamificationContexts);
                        }
                    } catch (Exception e) {
                        LOG.warn("Process GamificationUserReputation is failed: " + e.getMessage(), e);
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

            LOG.error("Error processing gamification action",e);

            return false;
        }

    }

    public void execute(GamificationActionsHistory actionsHistory) throws Exception {
        try {

            if (actionsHistory != null) {
                gamificationService.saveActionHistory(actionsHistory);
            }


        } catch (Exception e) {
            LOG.error("Error to save an actionHistory entry {}",actionsHistory,e);
        }

    }
}

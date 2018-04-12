package org.exoplatform.gamification.service.effective;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.gamification.entities.domain.effective.GamificationEntity;
import org.exoplatform.gamification.entities.effective.GamificationContext;
import org.exoplatform.gamification.service.completation.GamificationCompletionService;
import org.exoplatform.gamification.service.configuration.BadgeService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class GamificationProcessor {

    private static final Log LOG = ExoLogger.getLogger(GamificationProcessor.class);

    protected final BadgeService badgeService;
    protected final GamificationCompletionService gamificationCompletionService;
    protected final GamificationService gamificationService;

    public GamificationProcessor() {

        badgeService = CommonsUtils.getService(BadgeService.class);

        gamificationCompletionService = CommonsUtils.getService(GamificationCompletionService.class);

        gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    public boolean process(final GamificationContext ctx) {
        try {

            Callable<Boolean> task = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        ExoContainerContext.setCurrentContainer(PortalContainer.getInstance());
                        if (ctx != null) {
                            execute(build(ctx));
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

    private void execute(List<GamificationEntity> games) throws Exception {

        //--- Build new gamification entity

        try {

            for (GamificationEntity game : games) {

                GamificationEntity aGame = gamificationService.findGamificationByUsername(game.getUsername());

                if (aGame != null) { //--- Update the existing one

                    game.setScore(aGame.getScore()+game.getScore());

                }
                gamificationService.persist(game);

            }

        } catch (Exception e) {

            LOG.error("Exception",e);


        } finally {

        }

    }
    private List<GamificationEntity> build(GamificationContext gamification) throws Exception {

        if (gamification == null) {
            return null;
        }


        GamificationEntity gEntity = new GamificationEntity();

        //---Build sourceContext
        gEntity.setUsername(gamification.getSourceContextHolder().getUsername());
        gEntity.setScore(gamification.getSourceContextHolder().getScore());
        gEntity.setLastModifiedDate(gamification.getSourceContextHolder().getLastModifiedDate());
        gEntity.setCreatedDate(gamification.getSourceContextHolder().getCreatedDate());

        // Build target Context
        List<GamificationEntity> output = null;
        if (gamification.getTargetContextholder() != null) {
            output = gamification.getTargetContextholder().getUsernames().stream().map(temp -> {
                GamificationEntity obj = new GamificationEntity();
                obj.setUsername(temp);
                obj.setScore(gamification.getTargetContextholder().getScore());
                obj.setLastModifiedDate(gamification.getTargetContextholder().getLastModifiedDate());
                obj.setCreatedDate(gamification.getTargetContextholder().getCreatedDate());

                return obj;
            }).collect(Collectors.toList());

            output.add(gEntity);
        }


        return output;


    }
}

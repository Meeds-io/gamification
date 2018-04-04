package org.exoplatform.gamification.service.effective;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.gamification.entities.domain.effective.BadgeItemEntity;
import org.exoplatform.gamification.entities.domain.effective.ExoCoinEntity;
import org.exoplatform.gamification.entities.domain.effective.GamificationEntity;
import org.exoplatform.gamification.entities.domain.effective.MissionItemEntity;
import org.exoplatform.gamification.entities.effective.GamificationContext;
import org.exoplatform.gamification.service.completation.GamificationCompletionService;
import org.exoplatform.gamification.service.configuration.BadgeService;
import org.exoplatform.gamification.storage.ogm.HibernateOgmFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
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

    private void execute(GamificationContext gamification) throws Exception {

        LOG.info("GamificationContext {}", gamification);

        EntityManager em = HibernateOgmFactory.getInstance().getOGMEntityManager();

        EntityTransaction tx = null;

        try {

            tx = em.getTransaction();

            tx.begin();

            //TODO : to use GamificationDTO instead
            GamificationEntity gEntity = new GamificationEntity();

            BadgeItemEntity bEntity = new BadgeItemEntity();

            bEntity.setTitle("badgeA");
            bEntity.setAffectedDate(new Date());
            bEntity.setRequiredScore(50);
            bEntity.setCurrentScore(40);

            gEntity.addBadgeItem(bEntity);

            bEntity = new BadgeItemEntity();

            bEntity.setTitle("badgeB");
            bEntity.setAffectedDate(new Date());
            bEntity.setRequiredScore(500);
            bEntity.setCurrentScore(400);

            gEntity.addBadgeItem(bEntity);

            MissionItemEntity mEntity = new MissionItemEntity();

            mEntity.setTitle("Mission A");
            mEntity.setEndDate(new Date());
            mEntity.setStartDate(new Date());
            mEntity.setWonScore(1200);

            gEntity.addMissionItem(mEntity);

            mEntity = new MissionItemEntity();

            mEntity.setTitle("Mission B");
            mEntity.setEndDate(new Date());
            mEntity.setStartDate(new Date());
            mEntity.setWonScore(2200);

            gEntity.addMissionItem(mEntity);

            ExoCoinEntity exoCoinEntity = new ExoCoinEntity();

            exoCoinEntity.setType("eXo Coin Cash");
            exoCoinEntity.setCount(120);
            exoCoinEntity.setHash("qsdf54dsfqs54gqsf454fq45q45qsdf");

            gEntity.setExoCoinEntity(exoCoinEntity);

            gEntity.setUsername(gamification.getActorUserName());
            gEntity.setLastModifiedDate(new Date());

            em.persist(gEntity);
            em.flush();
            tx.commit();

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

        } finally {

            em.close();

        }

    }
}

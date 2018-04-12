package org.exoplatform.gamification.service.effective;

import org.exoplatform.gamification.entities.domain.effective.GamificationEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.*;

public class GamificationService {

    private static final Log LOG = ExoLogger.getLogger(GamificationService.class);

    @PersistenceContext(unitName = "gamification-ogm")
    private EntityManager em;

    public GamificationService() {

    }

    public GamificationEntity findGamificationByUsername (String username) {

        //EntityManager em = HibernateOgmFactory.getInstance().getEntityManagerFactory().createEntityManager();

        EntityTransaction tx = null;

        try {

            TypedQuery<GamificationEntity> query = em.createNamedQuery("Gamification.findGamificationByUsername", GamificationEntity.class)
                    .setParameter("username", username);

            return query.getSingleResult();


        } catch (NoResultException e) {
            LOG.error("Error to find Gamification entity with title : {} ", username, e.getMessage());
            if (tx != null ){
                tx.rollback();
            }

        } finally {

            if (em != null) {
                em.close();
            }
        }

        return null;

    }

    public GamificationEntity persist (GamificationEntity entity) {

        //EntityManager em = HibernateOgmFactory.getInstance().getEntityManagerFactory().createEntityManager();

        EntityTransaction tx = null;

        try {

            tx = em.getTransaction();

            tx.begin();

            em.persist(entity);

            tx.commit();

        } catch (Exception e) {

            LOG.error("Error to persiste gamification entity : {} ", entity.getUsername(), e);
            if (tx != null ){
                tx.rollback();
            }

        } finally {
            if (em != null) {
                em.close();
            }

        }

        return entity;
    }

}

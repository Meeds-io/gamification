package org.exoplatform.gamification.storage.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.gamification.service.effective.GamificationSearch;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;

public class GamificationDAO extends GenericDAOJPAImpl<GamificationContextEntity, Long> {

    public GamificationDAO() {
    }

    public GamificationContextEntity findGamificationContextByUsername(String username) throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.findGamificationContextByUsername", GamificationContextEntity.class)
                .setParameter("username", username);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     * Get the user global score based on userId
     * @param username
     * @return GamificationContextEntity which hold effective data
     * @throws PersistenceException
     */
    public GamificationContextEntity getUserGlobalScore(String username) throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.getUserGlobalScore", GamificationContextEntity.class)
                .setParameter("username", username);

        try {

            return query.getSingleResult();

        } catch (NoResultException e) {

            return null;

        }

    }

    /**
     *
     * @return
     * @throws PersistenceException
     */
    public List<GamificationContextEntity> search(GamificationSearch gamificationSearch) throws PersistenceException {

       return null;

    }

    public GamificationContextEntity getUserGamification(String username) throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.getUserGamification", GamificationContextEntity.class)
                .setParameter("username", username);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     *
     * @param username
     * @return
     * @throws PersistenceException
     */
    public List<GamificationContextEntity> findLeaderboardByZone(String username) throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.findLeaderboardByZone", GamificationContextEntity.class)
                .setParameter("username", username)
                .setMaxResults(10);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     *
     * @return
     * @throws PersistenceException
     */
    public List<GamificationContextEntity> findDefaultLeaderboard() throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.findDefaultLeaderboard", GamificationContextEntity.class)
                .setMaxResults(10);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }






}

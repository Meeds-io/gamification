package org.exoplatform.gamification.storage.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.gamification.service.effective.GamificationSearch;
import org.exoplatform.gamification.service.effective.Leaderboard;
import org.exoplatform.gamification.service.effective.Piechart;

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

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.findGamificationContextByUsername", GamificationContextEntity.class)
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

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.findGamificationContextByUsername", GamificationContextEntity.class)
                .setParameter("username", username);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     *
     * @param domain
     * @return
     * @throws PersistenceException
     */
    public List<Leaderboard> findLeaderboardByDomain(String domain) throws PersistenceException {

                // TODO : We should load only first 10 users
                List <Leaderboard> leaderBoards = getEntityManager().createNamedQuery("GamificationContext.findLeaderboardByDomain")
                                                             .setParameter("domain", domain)
                                                             .getResultList();

        try {
            return leaderBoards;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<GamificationContextEntity> findOverallLeaderboard() throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.findOverallLeaderboard", GamificationContextEntity.class);

        try {

            return query.getResultList();

        } catch (NoResultException e) {

            return null;

        }
    }

    public List<Piechart> findStatsByUserId(String userId) throws PersistenceException {

        // TODO : We should load only first 10 users
        List <Piechart> pieChart = getEntityManager().createNamedQuery("GamificationContext.findStatsByUserId")
                .setParameter("userId", userId)
                .getResultList();

        try {
            return pieChart;
        } catch (NoResultException e) {
            return null;
        }
    }

}

package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.service.effective.GamificationSearch;
import org.exoplatform.addons.gamification.service.effective.Leaderboard;
import org.exoplatform.addons.gamification.service.effective.Piechart;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;

public class GamificationDAO extends GenericDAOJPAImpl<GamificationContextEntity, Long> {

    private static final Log LOG = ExoLogger.getLogger(GamificationDAO.class);

    private int queryLimitOffset;

    public GamificationDAO(InitParams params) {

        ValueParam offsetLimit = params.getValueParam("query-limit-offset");

        try {
            if (offsetLimit != null) {
                queryLimitOffset = Integer.parseInt(offsetLimit.getValue());
            }

        } catch (NumberFormatException nfe) {

            // Set a default value any
            queryLimitOffset = 11;

            LOG.error("Error while parsing query-limit-offset, the default value will be used {}",queryLimitOffset, nfe);


        }


    }

    public GamificationContextEntity findGamificationContextByUsername(String username) throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationUserReputation.findGamificationContextByUsername", GamificationContextEntity.class)
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

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationUserReputation.findGamificationContextByUsername", GamificationContextEntity.class)
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

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationUserReputation.findGamificationContextByUsername", GamificationContextEntity.class)
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
    public List<GamificationContextEntity> findLeaderboardByDomain(String domain) throws PersistenceException {

                // TODO : We should load only first 10 users
                List <GamificationContextEntity> leaderBoards = getEntityManager().createNamedQuery("GamificationUserReputation.findLeaderboardByDomain")
                                                             .setParameter("domain", domain)
                                                             .getResultList();

        try {
            return leaderBoards;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<GamificationContextEntity> findOverallLeaderboard() throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationUserReputation.findOverallLeaderboard", GamificationContextEntity.class)
                                                                        .setMaxResults(queryLimitOffset);

        try {

            return query.getResultList();

        } catch (NoResultException e) {

            return null;

        }
    }

    public List<Piechart> findStatsByUserId(String userId) throws PersistenceException {

        // TODO : We should load only first 10 users
        List <Piechart> pieChart = getEntityManager().createNamedQuery("GamificationUserReputation.findStatsByUserId")
                .setParameter("userId", userId)
                .setMaxResults(queryLimitOffset)
                .getResultList();

        try {
            return pieChart;
        } catch (NoResultException e) {
            return null;
        }
    }

}

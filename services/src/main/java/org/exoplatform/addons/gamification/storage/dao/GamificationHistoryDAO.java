package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.ProfileReputation;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class GamificationHistoryDAO extends GenericDAOJPAImpl<GamificationActionsHistory, Long> {

    private static final Log LOG = ExoLogger.getLogger(GamificationHistoryDAO.class);

    private int queryLimitOffset;

    public GamificationHistoryDAO(InitParams params) {

        ValueParam offsetLimit = params.getValueParam("query-limit-offset");

        try {
            if (offsetLimit != null) {
                queryLimitOffset = Integer.parseInt(offsetLimit.getValue());
            }

        } catch (NumberFormatException nfe) {

            // Set a default value any
            queryLimitOffset = 20;

            LOG.error("Error while parsing query-limit-offset, the default value will be used {}",queryLimitOffset, nfe);

        }

    }

    /**
     * Get all ActionHistory records and convert them to list of type StandardLeaderboard
     * @return list of type StandardLeaderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findAllActionsHistoryAgnostic() throws PersistenceException {
        List<StandardLeaderboard> defaultLeaderboard = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistory")
                .getResultList();
        try {
            return defaultLeaderboard;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find all gamification entries by domain and by type
     * @param date : date from when we load gamification entries
     * @param domain : domain filter
     * @return list of object of type StandardLeaderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findAllActionsHistoryByDateByDomain(Date date, String domain) throws PersistenceException {

        try {

            List<StandardLeaderboard> defaultLeaderboard = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistoryByDateByDomain")
                    .setParameter("date", date)
                    .setParameter("domain", domain)
                    .getResultList();

            return defaultLeaderboard;

        } catch (NoResultException e) {

            return null;

        }
    }

    /**
     * Find all gamification entries by domain
     * @param domain : domain filter
     * @return list of object of type StandardLeaderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findAllActionsHistoryByDomain(String domain) throws PersistenceException {
        try {
            List<StandardLeaderboard> defaultLeaderboard = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistoryByDomain")
                                                                             .setParameter("domain", domain)
                                                                             .getResultList();
            return defaultLeaderboard;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find all gamification entries by date
     * @param date : date from when entries are loaded
     * @return list of object of type StandardLeaderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findAllActionsHistoryByDate(Date date) throws PersistenceException {
        try {
            List<StandardLeaderboard> defaultLeaderboard = getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByDate")
                    .setParameter("date", date)
                    .getResultList();
            return defaultLeaderboard;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Get an ActionHistory record based on userId
     * @param socialUserId : the userId used in projection
     * @return list of objects of type GamificationActionsHistory
     * @throws PersistenceException
     */
    public List<GamificationActionsHistory> findActionsHistoryByUserId(String socialUserId) throws PersistenceException {

        List<GamificationActionsHistory> defaultLeaderboard = getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByUserId")
                                                                                .setMaxResults(queryLimitOffset)
                                                                                .setParameter("socialUserId",socialUserId)
                                                                                .getResultList();
        try {

            return defaultLeaderboard;

        } catch (NoResultException e) {

            return null;

        }
    }

    /**
     * Get all ActionHistory records paginated
     * @param isGlobalContext : true if call is made in a global context false else
     * @param loadCapacity : how many records we load from DB
     * @return list of object of type StandardLeaderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findAllActionsHistory(boolean isGlobalContext, int loadCapacity) throws PersistenceException {

        // Build base query
        Query query = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistory");

        if (isGlobalContext) {
            query.setMaxResults(loadCapacity);
        }

        // Execute query
        List<StandardLeaderboard> defaultLeaderboard = query.getResultList();

        try {

            return defaultLeaderboard;

        } catch (NoResultException e) {

            return null;

        }
    }

    /**
     * Get all ActionHistory records by a given domain
     * @param domain : domain name used to filter returned records
     * @param isGlobalContext : false when records are filtered based on space true else
     * @param loadCapacity : how many records we load from DB
     * @return a list of objects of type StandardLeaderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findAllActionsHistoryByDomain(String domain, boolean isGlobalContext, int loadCapacity) throws PersistenceException {

        // Build base query
        Query query = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllActionsHistoryByDomain")
                                        .setParameter("domain",domain);

        // Compute
        if (isGlobalContext) {
            query.setMaxResults(loadCapacity);
        }

        List<StandardLeaderboard> defaultLeaderboard = query.getResultList();

        try {

            return defaultLeaderboard;

        } catch (NoResultException e) {

            return null;

        }
    }

    /**
     * Get The last ActionHistory record
     * @param date : date from when we aim to track the user
     * @param socialId
     * @return an instance of type GamificationActionsHistory
     * @throws PersistenceException
     */
    public List<GamificationActionsHistory> findActionHistoryByDateBySocialId(Date date, String socialId) throws PersistenceException {

        TypedQuery<GamificationActionsHistory> query = getEntityManager().createNamedQuery("GamificationActionsHistory.findActionHistoryByDateBySocialId", GamificationActionsHistory.class)
                .setParameter("date", date)
                .setParameter("socialId", socialId);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     *
     * @param date : date from when we aim to track leaders
     * @param isGlobalContext : true if call is made in a global context false else
     * @param loadCapacity : how many records we load from DB
     * @return a list of object of type StandardLeaderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findActionsHistoryByDate(Date date, boolean isGlobalContext, int loadCapacity) throws PersistenceException {

        // Base query
        Query query = getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByDate");

        // Set condition
        query.setParameter("date",date);

        // Compute query based on space context
        if (isGlobalContext) {
            query.setMaxResults(loadCapacity);
        }

        // Execute query
        List<StandardLeaderboard> defaultLeaderboard = query.getResultList();

        try {

            return defaultLeaderboard;

        } catch (NoResultException e) {

            return null;

        }
    }

    /**
     * Find actionsHistory by data and domain
     * @param date : date from when we aim to track user
     * @param domain : domain we aim to track
     * @param isGlobalContext : true if call is made in a global context false else
     * @param loadCapacity : how many records we should load from DB
     * @return a list of object of type StandardLraderboard
     * @throws PersistenceException
     */
    public List<StandardLeaderboard> findActionsHistoryByDateByDomain(Date date, String domain, boolean isGlobalContext, int loadCapacity) throws PersistenceException {

        // Build base query
        Query query = getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByDateByDomain")
                                        .setParameter("date",date)
                                        .setParameter("domain",domain);

        // Set query's condition
        if (isGlobalContext) {
            query.setMaxResults(loadCapacity);
        }

        // Execute query
        List<StandardLeaderboard> defaultLeaderboard = query.getResultList();

        try {

            return defaultLeaderboard;

        } catch (NoResultException e) {

            return null;

        }
    }

    // TODO : HQL used for this method is the same than :  «findDomainScoreByUserId». We should use only one pattern
    /**
     * Get user stats
     * @param userSocialId
     * @return a list of objects of type PiechartLeaderboard
     * @throws PersistenceException
     */
    public List<PiechartLeaderboard> findStatsByUserId(String userSocialId) throws PersistenceException {

        List <PiechartLeaderboard> userStats = getEntityManager().createNamedQuery("GamificationActionsHistory.findStatsByUser")
                .setParameter("userSocialId", userSocialId)
                .getResultList();

        try {
            return userStats;
        } catch (NoResultException e) {
            return null;
        }
    }

    // TODO : HQL used for this method is the same than :  «findStatsByUser». We should use only one pattern
    /**
     * Compute for a given user the score earned for each doman
     * @param userSocialId ProfileReputation
     * @return a list of objects of type
     * @throws PersistenceException
     */
    public List<ProfileReputation> findDomainScoreByUserId(String userSocialId) throws PersistenceException {

        List <ProfileReputation> profileReputation = getEntityManager().createNamedQuery("GamificationActionsHistory.findDomainScoreByUserId")
                .setParameter("userSocialId", userSocialId)
                .getResultList();

        try {
            return profileReputation;
        } catch (NoResultException e) {
            return null;
        }
    }

    public long findUserReputationScoreBetweenDate(String userSocialId, Date fromDate, Date toDate) throws PersistenceException {

        Long reupationScore = (Long) getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreBetweenDate")
                .setParameter("userSocialId", userSocialId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getSingleResult();

        try {
            return reupationScore == null ? 0 : reupationScore;
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            LOG.error(e);
            return 0;
        }
    }
    public long findUserReputationScoreByMonth(String userSocialId, Date currentMonth) throws PersistenceException {

        Long reupationScore = (Long) getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreByMonth")
                .setParameter("userSocialId", userSocialId)
                .setParameter("currentMonth", currentMonth)
                .getSingleResult();

        try {
            return reupationScore == null ? 0 : reupationScore;
        } catch (NoResultException e) {
            return 0;
        }
    }

    public long findUserReputationScoreByDomainBetweenDate(String userSocialId, String domain, Date fromDate, Date toDate) throws PersistenceException {

        Long reupationScore = (Long) getEntityManager().createNamedQuery("GamificationActionsHistory.findUserReputationScoreByDomainBetweenDate")
                .setParameter("userSocialId", userSocialId)
                .setParameter("domain", domain)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getSingleResult();

        try {
            return reupationScore == null ? 0 : reupationScore;
        } catch (NoResultException e) {
            return 0;
        }
    }

    /** Provided as an API for wallet Addon*/
    public List<StandardLeaderboard> findAllLeaderboardBetweenDate(Date fromDate, Date toDate) throws PersistenceException {

        Query query = getEntityManager().createNamedQuery("GamificationActionsHistory.findAllLeaderboardBetweenDate")
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate);

        try {
            // Execute query
            return (List<StandardLeaderboard>)query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }



    /**
     * Find actionsHistory by data and domain and date and points
     /* @param userSocialId : userSocialId
     * @param isGlobalContext : true if call is made in a global context false else
     * @param loadCapacity : how many records we should load from DB
     * @return a list of object of type GamificationActionsHistory
     * @throws PersistenceException
     */
   /* public List<GamificationActionsHistory> findActionsHistoryByUserIdSortedByDate(String userSocialId, boolean  isGlobalContext, int loadCapacity) throws PersistenceException {

        // Build base query
        Query query = getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByUserIdSortedByDate")
                .setParameter("userSocialId",userSocialId);


        // Set query's condition
        if (isGlobalContext) {
            query.setMaxResults(loadCapacity);
        }

        // Execute query
        List<GamificationActionsHistory> gamificationActionsHistoryList = query.getResultList();

        try {

            return gamificationActionsHistoryList;

        } catch (NoResultException e) {

            return null;

        }
    }
*/

    public List<GamificationActionsHistory> findActionsHistoryByReceiverIdSortedByDate(String Receiver, boolean  isGlobalContext, int loadCapacity) throws PersistenceException {

        // Build base query
        Query query = getEntityManager().createNamedQuery("GamificationActionsHistory.findActionsHistoryByReceiverIdSortedByDate")
                .setParameter("receiver",Receiver);


        // Set query's condition
        if (isGlobalContext) {
            query.setMaxResults(loadCapacity);
        }

        // Execute query
        List<GamificationActionsHistory> gamificationActionsHistoryList = query.getResultList();

        try {

            return gamificationActionsHistoryList;

        } catch (NoResultException e) {

            return null;

        }
    }


  public long computeTotalScore(String actorIdentityId) {
    TypedQuery<Long> query =
                           getEntityManager().createNamedQuery("GamificationActionsHistory.computeTotalScore",
                                                               Long.class);
    query.setParameter("socialUserId", actorIdentityId);
    try {
      Long result = query.getSingleResult();
      return result == null ? 0 : result;
    } catch (NoResultException e) {
      return 0;
    }
  }


    @ExoTransactional
    public List<GamificationActionsHistory> getAllPointsByDomain(String domain) throws PersistenceException {

        TypedQuery<GamificationActionsHistory> query = getEntityManager().createNamedQuery("GamificationActionsHistory.getAllPointsByDomain", GamificationActionsHistory.class)
                .setParameter("domain", domain);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }


    @ExoTransactional
    public List<GamificationActionsHistory> getAllPointsWithNullDomain() throws PersistenceException {

        TypedQuery<GamificationActionsHistory> query = getEntityManager().createNamedQuery("GamificationActionsHistory.getAllPointsWithNullDomain", GamificationActionsHistory.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    @ExoTransactional
    public List<String>  getDomainList() throws PersistenceException {
        TypedQuery<String> query = getEntityManager().createNamedQuery("GamificationActionsHistory.getDomainList", String.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

}

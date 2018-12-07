package org.exoplatform.addons.gamification.service.effective;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.storage.dao.GamificationDAO;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.storage.dao.GamificationItemDAO;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

public class GamificationService {

    private static final Log LOG = ExoLogger.getLogger(GamificationService.class);

    protected final GamificationDAO gamificationDAO;

    protected final GamificationItemDAO gamificationItemDAO;

    protected final GamificationHistoryDAO gamificationHistoryDAO;

    public GamificationService(GamificationDAO gamificationDAO,
                               GamificationItemDAO gamificationItemDAO,
                               GamificationHistoryDAO gamificationHistoryDAO) {

        this.gamificationDAO = gamificationDAO;

        this.gamificationItemDAO = gamificationItemDAO;

        this.gamificationHistoryDAO = gamificationHistoryDAO;
    }

    @ExoTransactional
    public GamificationActionsHistory findLatestActionHistoryBySocialId(String userSocialId) {

        List<GamificationActionsHistory> entities = null;

        try {
            //--- Get Entity from DB
            entities = gamificationHistoryDAO.findActionsHistoryByUserId(userSocialId);

            // Return the first element since the underluing API returns entities ordered by insertion date
            return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;

        } catch (Exception e) {
            LOG.error("Error to find ActionsHistory entity with the following cretiria [userSocialId:{}]", userSocialId, e);
        }
        return null;

    }


    /**
     * Get actionsHistory entities
     * @param date : filter by date
     * @param socialId : filter by socialId
     * @return List of object of type GamificationActionsHistory
     */
    @ExoTransactional
    public List<GamificationActionsHistory> findActionHistoryByDateBySocialId(Date date, String socialId) {

        List<GamificationActionsHistory> entities = null;

        try {
            //--- Get Entity from DB
            entities = gamificationHistoryDAO.findActionHistoryByDateBySocialId(date, socialId);

        } catch (Exception e) {
            LOG.error("Error to find ActionsHistory entities with the following cretiria [socialId:{} / date:{}", socialId, date, e);
        }
        return entities;

    }

    public int bluidCurrentUserRank (String socialId, Date date, String domain) {
        List<StandardLeaderboard> leaderboard = null;
        int rank = 0;
        try {
            if (date != null) {
                if (domain.equalsIgnoreCase("all")) {
                    leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDate(date);
                } else {
                    leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDateByDomain(date, domain);
                }

            } else {
                if (domain.equalsIgnoreCase("all")) {
                    leaderboard = gamificationHistoryDAO.findAllActionsHistoryAgnostic();
                } else {
                    leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDomain(domain);
                }
            }
            // Get username
            StandardLeaderboard item = leaderboard.stream()
                    .filter(g -> socialId.equals(g.getUserSocialId()))
                    .findAny()
                    .orElse(null);

            return (leaderboard.indexOf(item)+1);

        } catch (Exception e) {

        }
        return rank;
    }

    /**
     * Compute reputation's score
     * @param socialId : the current user socialId
     * @return long score of user
     */
    @ExoTransactional
    public long findUserReputationBySocialId(String socialId) {

        GamificationActionsHistory aHistory = null;

        try {

            aHistory = this.findLatestActionHistoryBySocialId(socialId);
            return (aHistory != null ? aHistory.getGlobalScore() : 0);

        } catch (Exception e) {

            LOG.error("Error to find ActionsHistory entity with the following cretiria [socialId:{} / date:{}", socialId, LocalDate.now(), e);

        }

        return 0;

    }

    /**
     * Compute User reputation score by Domain
     * @param socialId
     * @return list of objects of type ProfileReputation
     */
    @ExoTransactional
    public List<ProfileReputation> buildDomainScoreByUserId(String socialId) {

        List<ProfileReputation> domainsScore = null;

        try {

            domainsScore = gamificationHistoryDAO.findDomainScoreByUserId(socialId);

        } catch (Exception e) {

            LOG.error("Error to find ActionsHistory entity with the following cretiria [socialId:{} / date:{}", socialId, LocalDate.now(), e);

        }

        return domainsScore;

    }

    /**
     * Save a GamificationActionsHistory in DB
     * @param aHistory
     */
    @ExoTransactional
    public void saveActionHistory(GamificationActionsHistory aHistory) {

        try {
            gamificationHistoryDAO.create(aHistory);

        } catch (Exception e) {
            LOG.error("Error to save the following GamificationActionsHistory entry {}", aHistory, e);
        }
    }

    /**
     * Filter Leaderboard logic (filter by Domain or/and by period)
     * @param filter
     * @return list of objects of type StandardLeaderboard
     */
    @ExoTransactional
    public List<StandardLeaderboard> filter(LeaderboardFilter filter, boolean isGlobalContext) {
        List<StandardLeaderboard> leaderboard = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Filtering leaderboard based on Period/Somain name : [{}/{}]", filter.getPeriod(),filter.getDomain());
        }
        try {

            // Get overall leaderboard
            if (filter.getDomain().equalsIgnoreCase("all")) {

                // Compute date
                LocalDate now = LocalDate.now();

                // Check the period
                if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {

                    leaderboard = gamificationHistoryDAO.findActionsHistoryByDate(Date.from(now.with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant()), isGlobalContext, filter.getLoadCapacity());


                } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {

                    leaderboard = gamificationHistoryDAO.findActionsHistoryByDate(Date.from(now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()), isGlobalContext, filter.getLoadCapacity());

                } else {
                    leaderboard = gamificationHistoryDAO.findAllActionsHistory(isGlobalContext, filter.getLoadCapacity());
                }

            } else {

                // Compute date
                LocalDate now = LocalDate.now();

                // Check the period
                if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {

                    leaderboard = gamificationHistoryDAO.findActionsHistoryByDateByDomain(Date.from(now.with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant()), filter.getDomain(),isGlobalContext, filter.getLoadCapacity());


                } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {

                    leaderboard = gamificationHistoryDAO.findActionsHistoryByDateByDomain(Date.from(now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()),filter.getDomain(), isGlobalContext,filter.getLoadCapacity());

                } else {
                    leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDomain(filter.getDomain(), isGlobalContext,filter.getLoadCapacity());
                }


            }


        } catch (Exception e) {
            LOG.error("Error to filter leaderboard using the following filter [Period:{}-domain:{}-period:{}]", filter.getPeriod(), filter.getDomain(), filter.getPeriod(), e);
        }

        return leaderboard;
    }

    /**
     * Build stats dashboard of a given user (based on domain)
     * @param userSocialId
     * @return a list of object of type PiechartLeaderboard
     */
    @ExoTransactional
    public List<PiechartLeaderboard> buildStatsByUser(String userSocialId) {

        List<PiechartLeaderboard> userStats = null;

        try {
            //--- Get Stats
            userStats = gamificationHistoryDAO.findStatsByUserId(userSocialId);

        } catch (Exception e) {
            LOG.error("Error to load gamification stats for user {} ", userSocialId, e);
        }

        return userStats;
    }

    @ExoTransactional
    public long findUserReputationScoreBetweenDate(String userSocialId, Date fromDate, Date toDate) {

        long reputationScore = 0;

        try {
            //--- Get Stats
            reputationScore = gamificationHistoryDAO.findUserReputationScoreBetweenDate(userSocialId,fromDate, toDate);

        } catch (Exception e) {
            LOG.error("Error to find gamification history for user {} from date:{} to dat:{}", userSocialId, fromDate, toDate, e);
        }

        return reputationScore;
    }

    @ExoTransactional
    public long findUserReputationScoreByMonth(String userSocialId, Date currentMonth) {

        long reputationScore = 0;

        try {
            //--- Get Stats
            reputationScore = gamificationHistoryDAO.findUserReputationScoreByMonth(userSocialId, currentMonth);

        } catch (Exception e) {
            LOG.error("Error to find gamification history for user {} for current month {} ", userSocialId, currentMonth, e);
        }

        return reputationScore;
    }

    @ExoTransactional
    public long findUserReputationScoreByDomainBetweenDate(String userSocialId, String domain, Date fromDate, Date toDate) {

        long reputationScore = 0;

        try {
            //--- Get Stats
            reputationScore = gamificationHistoryDAO.findUserReputationScoreByDomainBetweenDate(userSocialId,domain, fromDate, toDate);

        } catch (Exception e) {
            LOG.error("Error to find gamification history from user {} from date:{} to dat:{}", userSocialId, fromDate, toDate, e);
        }

        return reputationScore;
    }

    /** Provided as an API from Wallet addon*/
    @ExoTransactional
    public List<StandardLeaderboard> findAllLeaderboardBetweenDate(Date fromDate, Date toDate) {

        List<StandardLeaderboard> list = null;

        try {
            //--- Get Stats
            list = gamificationHistoryDAO.findAllLeaderboardBetweenDate(fromDate, toDate);

        } catch (Exception e) {
            LOG.error("Error to find gamification history from user {} from date:{} to dat:{}",fromDate, toDate, e);
        }

        return list;
    }
}

package org.exoplatform.addons.gamification.service.effective;

import static java.util.Date.from;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.GamificationUtils;
import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

public class GamificationService {

  private static final Log               LOG = ExoLogger.getLogger(GamificationService.class);

  protected final GamificationHistoryDAO gamificationHistoryDAO;

  private IdentityManager                identityManager;

  private DomainMapper                   domainMapper;

  private RuleService                    ruleService;

  public GamificationService(IdentityManager identityManager,
                             GamificationHistoryDAO gamificationHistoryDAO,
                             DomainMapper domainMapper,
                             RuleService ruleService) {
    this.gamificationHistoryDAO = gamificationHistoryDAO;
    this.identityManager = identityManager;
    this.domainMapper = domainMapper;
    this.ruleService = ruleService;
  }

  /**
   * Get actionsHistory entities
   * 
   * @param date : filter by date
   * @param earnerId : filter by identity id
   * @return {@link List} of {@link GamificationActionsHistory}
   */
  public List<GamificationActionsHistory> findActionHistoryByDateByEarnerId(Date date, String earnerId) {
    return gamificationHistoryDAO.findActionHistoryByDateByEarnerId(date, earnerId);
  }

  public int getLeaderboardRank(String earnerId, Date date, String domain) {
    List<StandardLeaderboard> leaderboard = null;
    @SuppressWarnings("deprecation")
    Identity identity = identityManager.getIdentity(earnerId); // NOSONAR :
                                                               // profile load
                                                               // is always true
    IdentityType identityType = IdentityType.getType(identity.getProviderId());
    if (date != null) {
      if (domain.equalsIgnoreCase("all")) {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDate(identityType, date);
      } else {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDateByDomain(identityType, date, domain);
      }
    } else {
      if (domain.equalsIgnoreCase("all")) {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryAgnostic(identityType);
      } else {
        leaderboard = gamificationHistoryDAO.findAllActionsHistoryByDomain(identityType, domain);
      }
    }
    // Get username
    StandardLeaderboard item = leaderboard.stream()
                                          .filter(g -> earnerId.equals(g.getEarnerId()))
                                          .findAny()
                                          .orElse(null);
    return (leaderboard.indexOf(item) + 1);
  }

  /**
   * Compute reputation's score
   * 
   * @param earnerId : the current user earner id
   * @return long score of user
   */
  public long findReputationByEarnerId(String earnerId) {
    GamificationActionsHistory aHistory = this.findLatestActionHistoryByEarnerId(earnerId);
    return (aHistory != null ? aHistory.getGlobalScore() : 0);
  }

  /**
   * Compute User reputation score by Domain
   * 
   * @param earnerId earner identity id
   * @return list of objects of type ProfileReputation
   */
  public List<ProfileReputation> buildDomainScoreByIdentityId(String earnerId) {
    return gamificationHistoryDAO.findDomainScoreByIdentityId(earnerId);
  }

  /**
   * Save a GamificationActionsHistory in DB
   * 
   * @param history history entru to save
   */
  public void saveActionHistory(GamificationActionsHistory history) {
    gamificationHistoryDAO.create(history);
  }

  public void createHistory(String event, String sender, String receiver, String object) {
    GamificationActionsHistory aHistory = null;
    List<RuleDTO> ruleDtos = null;
    // Get associated rules
    ruleDtos = ruleService.findEnabledRulesByEvent(event);

    // Process only when an enable rules are found
    if (ruleDtos != null) {
      for (RuleDTO ruleDto : ruleDtos) {
        aHistory = build(ruleDto, sender, receiver, object);
        if (aHistory != null) {
          saveActionHistory(aHistory);
          // Gamification simple audit logger
          LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"",
                   LocalDate.now(),
                   aHistory.getEarnerId(),
                   aHistory.getGlobalScore(),
                   ruleDto.getArea(),
                   ruleDto.getEvent(),
                   ruleDto.getScore());
        }
      }
    }
  }

  public GamificationActionsHistory findLatestActionHistoryByEarnerId(String earnerId) {
    List<GamificationActionsHistory> entities = gamificationHistoryDAO.findActionsHistoryByEarnerId(earnerId, 1);
    // Return the first element since the underluing API returns entities
    // ordered by insertion date
    return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
  }

  /**
   * Filter Leaderboard logic (filter by Domain or/and by period)
   * 
   * @param filter of type {@link LeaderboardFilter}, used to filter query
   * @return list of objects of type StandardLeaderboard
   */
  public List<StandardLeaderboard> filter(LeaderboardFilter filter) {
    LOG.debug("Filtering leaderboard based on Period/Domain name : [{}/{}]", filter.getPeriod(), filter.getDomain());
    // Get overall leaderboard
    if (StringUtils.isBlank(filter.getDomain()) || filter.getDomain().equalsIgnoreCase("all")) {
      // Compute date
      LocalDate now = LocalDate.now();
      // Check the period
      if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {
        return gamificationHistoryDAO.findAllActionsHistoryByDate(from(now.with(DayOfWeek.MONDAY)
                                                                          .atStartOfDay(ZoneId.systemDefault())
                                                                          .toInstant()),
                                                                  filter.getIdentityType(),
                                                                  filter.getLoadCapacity());
      } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {
        return gamificationHistoryDAO.findAllActionsHistoryByDate(from(now.with(TemporalAdjusters.firstDayOfMonth())
                                                                          .atStartOfDay(ZoneId.systemDefault())
                                                                          .toInstant()),
                                                                  filter.getIdentityType(),
                                                                  filter.getLoadCapacity());
      } else {
        return gamificationHistoryDAO.findAllActionsHistory(filter.getIdentityType(), filter.getLoadCapacity());
      }
    } else {
      // Compute date
      LocalDate now = LocalDate.now();
      // Check the period
      if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {
        return gamificationHistoryDAO.findActionsHistoryByDateByDomain(from(now.with(DayOfWeek.MONDAY)
                                                                               .atStartOfDay(ZoneId.systemDefault())
                                                                               .toInstant()),
                                                                       filter.getIdentityType(),
                                                                       filter.getDomain(),
                                                                       filter.getLoadCapacity());
      } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {
        return gamificationHistoryDAO.findActionsHistoryByDateByDomain(from(now.with(TemporalAdjusters.firstDayOfMonth())
                                                                               .atStartOfDay(ZoneId.systemDefault())
                                                                               .toInstant()),
                                                                       filter.getIdentityType(),
                                                                       filter.getDomain(),
                                                                       filter.getLoadCapacity());
      } else {
        return gamificationHistoryDAO.findAllActionsHistoryByDomain(filter.getDomain(),
                                                                    filter.getIdentityType(),
                                                                    filter.getLoadCapacity());
      }
    }
  }

  /**
   * Build stats dashboard of a given user (based on domain)
   * 
   * @param earnerId earner identity id
   * @return a list of object of type PiechartLeaderboard
   */
  public List<PiechartLeaderboard> buildStatsByUser(String earnerId) {
    return gamificationHistoryDAO.findStatsByUserId(earnerId);
  }

  public long findUserReputationScoreBetweenDate(String earnerId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUserReputationScoreBetweenDate(earnerId, fromDate, toDate);
  }

  public long findUserReputationScoreByMonth(String earnerId, Date currentMonth) {
    return gamificationHistoryDAO.findUserReputationScoreByMonth(earnerId, currentMonth);
  }

  public long findUserReputationScoreByDomainBetweenDate(String earnerId, String domain, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUserReputationScoreByDomainBetweenDate(earnerId, domain, fromDate, toDate);
  }

  public List<StandardLeaderboard> findAllLeaderboardBetweenDate(IdentityType earnedType, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findAllLeaderboardBetweenDate(earnedType, fromDate, toDate);
  }

  /**
   * Provided as an API from points n list to find gamification history from the
   * GamificationInformationsPortlet's earner earned points by date
   * 
   * @param earnerId earner identity Id
   * @param limit limit entries to return
   * @return {@link List} of {@link GamificationActionsHistory}
   */
  public List<GamificationActionsHistory> findActionsHistoryByEarnerId(String earnerId, int limit) {
    return gamificationHistoryDAO.findActionsHistoryByEarnerIdSortedByDate(earnerId, limit);
  }

  public GamificationActionsHistory build(RuleDTO ruleDto, String actor, String receiver, String objectId) {
    GamificationActionsHistory aHistory = null;
    // check if the current user is not a bot
    Identity actorIdentity = identityManager.getIdentity(actor, false);
    if (actorIdentity == null || StringUtils.isBlank(actorIdentity.getRemoteId())) {
      LOG.warn("Actor {} has earned some points but doesn't have a social identity", actor);
      return null;
    }
    if (actorIdentity.isDeleted()) {
      LOG.warn("Actor {} has earned some points but is marked as deleted", actor);
      return null;
    }
    if (!actorIdentity.isEnable()) {
      LOG.warn("Actor {} has earned some points but is marked as disabled", actor);
      return null;
    }
    String actorRemoteId = actorIdentity.getRemoteId();
    if (GamificationUtils.isBlackListed(actorRemoteId)) {
      LOG.debug("Actor {} has earned some points but is marked as blacklisted", actor);
      return null;
    }

    // Build only an entry when a rule enable and exist
    if (ruleDto != null) {
      aHistory = new GamificationActionsHistory();
      aHistory.setActionScore(ruleDto.getScore());
      aHistory.setGlobalScore(computeTotalScore(actor) + ruleDto.getScore());
      aHistory.setDate(new Date());
      aHistory.setEarnerId(actor);
      aHistory.setEarnerType(IdentityType.getType(actorIdentity.getProviderId()));
      aHistory.setActionTitle(ruleDto.getEvent());
      aHistory.setDomain(ruleDto.getArea());
      if (ruleDto.getDomainDTO() != null) {
        aHistory.setDomainEntity(domainMapper.domainDTOToDomain(ruleDto.getDomainDTO()));
      }
      aHistory.setReceiver(receiver);
      aHistory.setObjectId(objectId);
      // Set update metadata
      aHistory.setLastModifiedDate(new Date());
      aHistory.setLastModifiedBy("Gamification Inner Process");
      // Set create metadata
      aHistory.setCreatedBy("Gamification Inner Process");

    }
    return aHistory;
  }

  public long computeTotalScore(String actorIdentityId) {
    return gamificationHistoryDAO.getTotalScore(actorIdentityId);
  }
}

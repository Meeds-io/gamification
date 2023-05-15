package org.exoplatform.addons.gamification.storage;

import static org.exoplatform.addons.gamification.service.mapper.RealizationMapper.fromEntities;
import static org.exoplatform.addons.gamification.service.mapper.RealizationMapper.fromEntity;
import static org.exoplatform.addons.gamification.service.mapper.RealizationMapper.toEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.RealizationEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.ProfileReputation;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.storage.dao.RealizationDAO;

public class RealizationStorage {

  private ProgramStorage programStorage;

  private RuleStorage    ruleStorage;

  private RealizationDAO gamificationHistoryDAO;

  public RealizationStorage(ProgramStorage programStorage,
                            RuleStorage ruleStorage,
                            RealizationDAO gamificationHistoryDAO) {
    this.programStorage = programStorage;
    this.ruleStorage = ruleStorage;
    this.gamificationHistoryDAO = gamificationHistoryDAO;
  }

  public List<RealizationDTO> getRealizationsByFilter(RealizationFilter realizationFilter,
                                                      int offset,
                                                      int limit) {
    List<RealizationEntity> realizationEntities = gamificationHistoryDAO.findRealizationsByFilter(realizationFilter,
                                                                                                  offset,
                                                                                                  limit);
    return fromEntities(programStorage, realizationEntities);
  }

  public int countRealizationsByFilter(RealizationFilter realizationFilter) {
    return gamificationHistoryDAO.countRealizationsByFilter(realizationFilter);
  }

  public RealizationDTO getRealizationById(long id) {
    return fromEntity(programStorage, gamificationHistoryDAO.find(id));
  }

  public RealizationDTO updateRealization(RealizationDTO realization) {
    RealizationEntity realizationEntity = toEntity(ruleStorage, realization);
    realizationEntity = gamificationHistoryDAO.update(realizationEntity);
    return fromEntity(programStorage, realizationEntity);
  }

  public RealizationDTO createRealization(RealizationDTO realization) {
    RealizationEntity realizationEntity = toEntity(ruleStorage, realization);
    realizationEntity.setId(null);
    realizationEntity.setCreatedDate(new Date());
    return fromEntity(programStorage, gamificationHistoryDAO.create(realizationEntity));
  }

  public List<RealizationDTO> findRealizationsByObjectIdAndObjectType(String objectId, String objectType) {
    List<RealizationEntity> realizationEntities = gamificationHistoryDAO.getRealizationsByObjectIdAndObjectType(objectId,
                                                                                                                objectType);
    return fromEntities(programStorage, realizationEntities);
  }

  public List<StandardLeaderboard> findRealizationsByDate(IdentityType identityType, Date date) {
    return gamificationHistoryDAO.findRealizationsByDate(identityType, date);
  }

  public List<StandardLeaderboard> findRealizationsByDateAndDomain(IdentityType identityType, Date date, long domainId) {
    return gamificationHistoryDAO.findRealizationsByDateAndDomain(identityType, date, domainId);
  }

  public List<StandardLeaderboard> findRealizationsAgnostic(IdentityType identityType) {
    return gamificationHistoryDAO.findRealizationsAgnostic(identityType);
  }

  public List<StandardLeaderboard> findRealizationsByDomain(IdentityType identityType, long domainId) {
    return gamificationHistoryDAO.findRealizationsByDomain(identityType, domainId);
  }

  public List<RealizationDTO> findRealizationsByIdentityIdAndByType(String earnerId, EntityType entityType) {
    return fromEntities(programStorage, gamificationHistoryDAO.findRealizationsByIdentityIdAndByType(earnerId, entityType));
  }

  public List<RealizationDTO> findRealizationsByIdentityIdSortedByDate(String earnerIdentityId, int limit) {
    return fromEntities(programStorage, gamificationHistoryDAO.findRealizationsByIdentityIdSortedByDate(earnerIdentityId, limit));
  }

  public List<RealizationDTO> findRealizationsByDateAndIdentityId(Date date, String earnerIdentityId) {
    return fromEntities(programStorage, gamificationHistoryDAO.findRealizationsByDateAndIdentityId(date, earnerIdentityId));
  }

  public List<StandardLeaderboard> findRealizationsByDate(Date fromDate, IdentityType identityType, int limit) {
    return gamificationHistoryDAO.findRealizationsByDate(fromDate, identityType, limit);
  }

  public List<StandardLeaderboard> findRealizations(IdentityType identityType, int limit) {
    return gamificationHistoryDAO.findRealizations(identityType, limit);
  }

  public List<StandardLeaderboard> findRealizationsByDateByDomain(Date fromDate,
                                                                  IdentityType identityType,
                                                                  long domainId,
                                                                  int limit) {
    return gamificationHistoryDAO.findRealizationsByDateByDomain(fromDate, identityType, domainId, limit);
  }

  public List<StandardLeaderboard> findRealizationsByDomain(long domainId, IdentityType identityType, int limit) {
    return gamificationHistoryDAO.findRealizationsByDomain(domainId, identityType, limit);
  }

  public List<Long> findMostRealizedRuleIds(List<Long> spacesIds, int offset, int limit, EntityType type) {
    return gamificationHistoryDAO.findMostRealizedRuleIds(spacesIds, offset, limit, type);
  }

  public List<ProfileReputation> getScorePerDomainByIdentityId(String earnerIdentityId) {
    return gamificationHistoryDAO.getScorePerDomainByIdentityId(earnerIdentityId);
  }

  public List<PiechartLeaderboard> getStatsByIdentityId(String earnerIdentityId, Date startDate, Date endDate) {
    return gamificationHistoryDAO.findStatsByUserId(earnerIdentityId, startDate, endDate);
  }

  public long getScoreByIdentityIdAndBetweenDates(String earnerIdentityId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUserReputationScoreBetweenDate(earnerIdentityId, fromDate, toDate);
  }

  public long getScoreByIdentityId(String earnerIdentityId) {
    RealizationDTO aHistory = findLatestRealizationByIdentityId(earnerIdentityId);
    return (aHistory != null ? aHistory.getGlobalScore() : 0);
  }

  public Map<Long, Long> findUsersReputationScoreBetweenDate(List<String> earnersId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
  }

  public List<StandardLeaderboard> findAllLeaderboardBetweenDate(IdentityType earnedType, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findAllLeaderboardBetweenDate(earnedType, fromDate, toDate);
  }

  public RealizationDTO findRealizationByActionTitleAndEarnerIdAndReceiverAndObjectId(String actionTitle,
                                                                                      long domainId,
                                                                                      String earnerId,
                                                                                      String receiverId,
                                                                                      String objectId,
                                                                                      String objectType) {
    return fromEntity(programStorage,
                      gamificationHistoryDAO.findActionHistoryByActionTitleAndEarnerIdAndReceiverAndObjectId(actionTitle,
                                                                                                             domainId,
                                                                                                             earnerId,
                                                                                                             receiverId,
                                                                                                             objectId,
                                                                                                             objectType));
  }

  public RealizationDTO findLatestRealizationByIdentityId(String earnerIdentityId) {
    List<RealizationEntity> entities = gamificationHistoryDAO.findRealizationsByEarnerId(earnerIdentityId, 1);
    // Return the first element since the underluing API returns entities
    // ordered by insertion date
    return (entities != null && !entities.isEmpty()) ? fromEntity(programStorage, entities.get(0)) : null;
  }

  public List<RealizationDTO> findRealizationsByRuleId(long ruleId,
                                                       int offset,
                                                       int limit,
                                                       PeriodType periodType,
                                                       IdentityType earnerType) {
    return fromEntities(programStorage,
                        gamificationHistoryDAO.findRealizationsByRuleId(ruleId,
                                                                        offset,
                                                                        limit,
                                                                        periodType,
                                                                        earnerType));
  }

  public int countRealizationsByRuleId(long ruleId) {
    return gamificationHistoryDAO.countRealizationsByRuleId(ruleId);
  }

  public int countRealizationsByRuleIdAndEarnerType(long ruleId, IdentityType earnerType) {
    return gamificationHistoryDAO.countRealizationsByRuleIdAndEarnerType(ruleId, earnerType);
  }

}

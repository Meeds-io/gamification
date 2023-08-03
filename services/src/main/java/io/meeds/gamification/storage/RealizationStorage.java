package io.meeds.gamification.storage;

import static io.meeds.gamification.storage.mapper.RealizationMapper.fromEntities;
import static io.meeds.gamification.storage.mapper.RealizationMapper.fromEntity;
import static io.meeds.gamification.storage.mapper.RealizationMapper.toEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.dao.RealizationDAO;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProfileReputation;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.RealizationFilter;

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

  public List<StandardLeaderboard> findRealizationsByDateAndProgramId(IdentityType identityType, Date date, long programId) {
    return gamificationHistoryDAO.findRealizationsByDateAndProgramId(identityType, date, programId);
  }

  public List<StandardLeaderboard> findRealizationsAgnostic(IdentityType identityType) {
    return gamificationHistoryDAO.findRealizationsAgnostic(identityType);
  }

  public List<StandardLeaderboard> findRealizationsByProgramId(IdentityType identityType, long programId) {
    return gamificationHistoryDAO.findRealizationsByProgramId(identityType, programId);
  }

  public List<RealizationDTO> findRealizationsByIdentityIdSortedByDate(String earnerIdentityId, int limit) {
    return fromEntities(programStorage, gamificationHistoryDAO.findRealizationsByIdentityIdSortedByDate(earnerIdentityId, limit));
  }

  public List<StandardLeaderboard> findRealizationsByDate(Date fromDate, IdentityType identityType, int limit) {
    return gamificationHistoryDAO.findRealizationsByDate(fromDate, identityType, limit);
  }

  public List<StandardLeaderboard> findRealizations(IdentityType identityType, int limit) {
    return gamificationHistoryDAO.findRealizations(identityType, limit);
  }

  public List<StandardLeaderboard> findRealizationsByDateByProgramId(Date fromDate,
                                                                     IdentityType identityType,
                                                                     long programId,
                                                                     int limit) {
    return gamificationHistoryDAO.findRealizationsByDateByProgramId(fromDate, identityType, programId, limit);
  }

  public List<StandardLeaderboard> findRealizationsByProgramId(long programId, IdentityType identityType, int limit) {
    return gamificationHistoryDAO.findRealizationsByProgramId(programId, identityType, limit);
  }

  public List<ProfileReputation> getScorePerProgramByIdentityId(String earnerIdentityId) {
    return gamificationHistoryDAO.getScorePerProgramByIdentityId(earnerIdentityId);
  }

  public List<PiechartLeaderboard> getStatsByIdentityId(String earnerIdentityId, Date startDate, Date endDate) {
    return gamificationHistoryDAO.findStatsByUserId(earnerIdentityId, startDate, endDate);
  }

  public long getScoreByIdentityIdAndBetweenDates(String earnerIdentityId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUserReputationScoreBetweenDate(earnerIdentityId, fromDate, toDate);
  }

  public long getScoreByIdentityId(String earnerIdentityId) {
    return gamificationHistoryDAO.getScoreByIdentityId(earnerIdentityId);
  }

  public Map<Long, Long> findUsersReputationScoreBetweenDate(List<String> earnersId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
  }

  public List<StandardLeaderboard> findAllLeaderboardBetweenDate(IdentityType earnedType, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.findAllLeaderboardBetweenDate(earnedType, fromDate, toDate);
  }

  public RealizationDTO findLastReadlizationByRuleIdAndEarnerIdAndReceiverAndObjectId(long ruleId,
                                                                                      String earnerId,
                                                                                      String receiverId,
                                                                                      String objectId,
                                                                                      String objectType) {
    return fromEntity(programStorage,
                      gamificationHistoryDAO.findLastReadlizationByRuleIdAndEarnerIdAndReceiverAndObjectId(ruleId,
                                                                                                           earnerId,
                                                                                                           receiverId,
                                                                                                           objectId,
                                                                                                           objectType));
  }

  public int countRealizationsByRuleIdAndEarnerId(String earnerIdentityId, long ruleId) {
    return gamificationHistoryDAO.countRealizationsByRuleIdAndEarnerId(earnerIdentityId, ruleId);
  }

  public int countRealizationsByRuleIdAndEarnerIdSinceDate(String earnerIdentityId, long ruleId, Date sinceDate) {
    return gamificationHistoryDAO.countRealizationsByRuleIdAndEarnerIdSinceDate(earnerIdentityId, ruleId, sinceDate);
  }

  public long countParticipantsBetweenDates(Date fromDate, Date toDate) {
    return gamificationHistoryDAO.countParticipantsBetweenDates(fromDate, toDate);
  }

}

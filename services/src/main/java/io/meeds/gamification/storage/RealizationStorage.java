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

  public int getLeaderboardRankByDate(IdentityType identityType, String earnerIdentityId, Date fromDate) {
    return gamificationHistoryDAO.getLeaderboardRankByDate(identityType, earnerIdentityId, fromDate);
  }

  public int getLeaderboardRankByDateAndProgramId(IdentityType identityType, String earnerIdentityId, Date fromDate, long programId) {
    return gamificationHistoryDAO.getLeaderboardRankByDateAndProgramId(identityType, earnerIdentityId, fromDate, programId);
  }

  public int getLeaderboardRank(IdentityType identityType, String earnerIdentityId) {
    return gamificationHistoryDAO.getLeaderboardRank(identityType, earnerIdentityId);
  }

  public int getLeaderboardRankByProgramId(IdentityType identityType, String earnerIdentityId, long programId) {
    return gamificationHistoryDAO.getLeaderboardRankByProgramId(identityType, earnerIdentityId, programId);
  }

  public List<StandardLeaderboard> getLeaderboardByDate(Date fromDate, IdentityType identityType, int offset, int limit) {
    return gamificationHistoryDAO.getLeaderboardByDate(fromDate, identityType, offset, limit);
  }

  public List<StandardLeaderboard> getLeaderboard(IdentityType identityType, int offset, int limit) {
    return gamificationHistoryDAO.getLeaderboard(identityType, offset, limit);
  }

  public List<StandardLeaderboard> getLeaderboardByDateByProgramId(Date fromDate,
                                                                   IdentityType identityType,
                                                                   long programId,
                                                                   int offset,
                                                                   int limit) {
    return gamificationHistoryDAO.getLeaderboardByDateAndProgramId(fromDate, identityType, programId, offset, limit);
  }

  public List<StandardLeaderboard> getLeaderboardByProgramId(long programId, IdentityType identityType, int offset, int limit) {
    return gamificationHistoryDAO.getLeaderboardByProgramId(programId, identityType, offset, limit);
  }

  public List<ProfileReputation> getScorePerProgramByIdentityId(String earnerIdentityId) {
    return gamificationHistoryDAO.getScorePerProgramByIdentityId(earnerIdentityId);
  }

  public List<PiechartLeaderboard> getLeaderboardStatsByIdentityId(String earnerIdentityId, Date startDate, Date endDate) {
    return gamificationHistoryDAO.getLeaderboardStatsByIdentityId(earnerIdentityId, startDate, endDate);
  }

  public long getScoreByIdentityIdAndBetweenDates(String earnerIdentityId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.getScoreByIdentityIdAndBetweenDates(earnerIdentityId, fromDate, toDate);
  }

  public long getScoreByIdentityId(String earnerIdentityId) {
    return gamificationHistoryDAO.getScoreByIdentityId(earnerIdentityId);
  }

  public Map<Long, Long> getScoresByIdentityIdsAndBetweenDates(List<String> earnersId, Date fromDate, Date toDate) {
    return gamificationHistoryDAO.getScoreByIdentityIdsAndBetweenDates(earnersId, fromDate, toDate);
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

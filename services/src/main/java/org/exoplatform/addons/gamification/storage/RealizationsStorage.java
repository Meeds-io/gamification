package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.mapper.GamificationActionsHistoryMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;

import java.util.List;

public class RealizationsStorage {

  private GamificationHistoryDAO gamificationHistoryDAO;

  public RealizationsStorage(GamificationHistoryDAO gamificationHistoryDAO) {
    this.gamificationHistoryDAO = gamificationHistoryDAO;
  }

  public List<GamificationActionsHistoryDTO> getRealizationsByFilter(RealizationsFilter realizationFilter,
                                                                     int offset,
                                                                     int limit) {
    List<GamificationActionsHistory> gamificationActionsHistoryList =
                                                                    gamificationHistoryDAO.findRealizationsByFilter(realizationFilter,
                                                                                                                    offset,
                                                                                                                    limit);
    return GamificationActionsHistoryMapper.fromEntities(gamificationActionsHistoryList);
  }

  public int countRealizationsByFilter(RealizationsFilter realizationFilter) {
    return gamificationHistoryDAO.countRealizationsByFilter(realizationFilter);
  }

  public GamificationActionsHistoryDTO getRealizationById(Long id) {
    GamificationActionsHistory gamificationActionsHistory = gamificationHistoryDAO.find(id);
    return GamificationActionsHistoryMapper.fromEntity(gamificationActionsHistory);
  }

  public GamificationActionsHistoryDTO updateRealization(GamificationActionsHistoryDTO gamificationActionsHistory) {
    GamificationActionsHistory gamificationActionsHistoryEntity =
                                                                GamificationActionsHistoryMapper.toEntity(gamificationActionsHistory);
    gamificationActionsHistoryEntity = gamificationHistoryDAO.update(gamificationActionsHistoryEntity);
    return GamificationActionsHistoryMapper.fromEntity(gamificationActionsHistoryEntity);
  }

  public GamificationActionsHistoryDTO findRealizationByActionTitleAndEarnerIdAndReceiverAndObjectId(String actionTitle,
                                                                                                     long domainId,
                                                                                                     String earnerId,
                                                                                                     String receiverId,
                                                                                                     String objectId,
                                                                                                     String objectType) {
    GamificationActionsHistory gamificationActionsHistory =
                                                          gamificationHistoryDAO.findActionHistoryByActionTitleAndEarnerIdAndReceiverAndObjectId(actionTitle,
                                                                                                                                                 domainId,
                                                                                                                                                 earnerId,
                                                                                                                                                 receiverId,
                                                                                                                                                 objectId,
                                                                                                                                                 objectType);
    return gamificationActionsHistory != null ? GamificationActionsHistoryMapper.fromEntity(gamificationActionsHistory) : null;
  }

  public List<GamificationActionsHistoryDTO> getRealizationsByObjectIdAndObjectType(String objectId, String objectType) {
    List<GamificationActionsHistory> gamificationActionsHistoryList = gamificationHistoryDAO.getRealizationsByObjectIdAndObjectType(objectId, objectType);
    return GamificationActionsHistoryMapper.fromEntities(gamificationActionsHistoryList);
  }

}

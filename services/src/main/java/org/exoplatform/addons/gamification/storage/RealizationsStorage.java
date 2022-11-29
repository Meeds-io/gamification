package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.entity.GamificationActionsHistoryEntity;
import org.exoplatform.addons.gamification.model.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.model.RealizationsFilter;
import org.exoplatform.addons.gamification.utils.GamificationActionsHistoryMapper;

import java.util.List;

public class RealizationsStorage {

  private final GamificationHistoryDAO gamificationHistoryDAO;

  public RealizationsStorage(GamificationHistoryDAO gamificationHistoryDAO) {
    this.gamificationHistoryDAO = gamificationHistoryDAO;
  }

  public List<GamificationActionsHistoryDTO> getRealizationsByFilter(RealizationsFilter realizationFilter,
                                                                     int offset,
                                                                     int limit) {
    List<GamificationActionsHistoryEntity> gamificationActionsHistoryList =
                                                                          gamificationHistoryDAO.findRealizationsByFilter(realizationFilter,
                                                                                                                          offset,
                                                                                                                          limit);
    return GamificationActionsHistoryMapper.fromEntities(gamificationActionsHistoryList);
  }

  public int countRealizationsByFilter(RealizationsFilter realizationFilter) {
    return gamificationHistoryDAO.countRealizationsByFilter(realizationFilter);
  }

  public GamificationActionsHistoryDTO getRealizationById(Long id) {
    GamificationActionsHistoryEntity gamificationActionsHistory = gamificationHistoryDAO.find(id);
    return GamificationActionsHistoryMapper.fromEntity(gamificationActionsHistory);
  }

  public GamificationActionsHistoryDTO updateRealizationStatus(GamificationActionsHistoryDTO gamificationActionsHistory) {
    GamificationActionsHistoryEntity gamificationActionsHistoryEntity =
                                                                      GamificationActionsHistoryMapper.toEntity(gamificationActionsHistory);
    gamificationActionsHistoryEntity = gamificationHistoryDAO.update(gamificationActionsHistoryEntity);
    return GamificationActionsHistoryMapper.fromEntity(gamificationActionsHistoryEntity);
  }

}

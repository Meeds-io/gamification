package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.mapper.GamificationActionsHistoryMapper;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;


import java.util.Date;
import java.util.List;

public class RealizationsStorage {

    private GamificationHistoryDAO gamificationHistoryDAO  ;

    public RealizationsStorage(GamificationHistoryDAO gamificationHistoryDAO) {
        this.gamificationHistoryDAO = gamificationHistoryDAO;
    }

    public List<GamificationActionsHistoryDTO> getAllRealizationsByDate (Date fromDate, Date toDate, int offset, int limit){
        List<GamificationActionsHistory> gamificationActionsHistoryList = gamificationHistoryDAO.getAllRealizationsByDate(fromDate, toDate, offset, limit);
        return GamificationActionsHistoryMapper.fromEntities(gamificationActionsHistoryList);
    }

    public GamificationActionsHistoryDTO getRealizationById(Long id){
        GamificationActionsHistory gamificationActionsHistory = gamificationHistoryDAO.find(id);
        return GamificationActionsHistoryMapper.fromEntity(gamificationActionsHistory);
    }
    public GamificationActionsHistoryDTO updateRealizationStatus (GamificationActionsHistory gamificationActionsHistory){
               gamificationActionsHistory =  gamificationHistoryDAO.update(gamificationActionsHistory);
        return GamificationActionsHistoryMapper.fromEntity(gamificationActionsHistory);
    }

}

package org.exoplatform.addons.gamification.service.configuration;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.mapper.GamificationActionsHistoryMapper;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.Date;
import java.util.List;

public class RealizationsServiceImpl implements RealizationsService {

  private RealizationsStorage realizationsStorage;

  public RealizationsServiceImpl(RealizationsStorage realizationsStorage) {
    this.realizationsStorage = realizationsStorage;
  }

  @Override
  public List<GamificationActionsHistoryDTO> getAllRealizationsByDate(String fromDate,
                                                                      String toDate,
                                                                      int offset,
                                                                      int limit) throws IllegalArgumentException {
    if (StringUtils.isBlank(fromDate)) {
      throw new IllegalArgumentException("fromDate is mandatory");
    }
    if (StringUtils.isBlank(toDate)) {
      throw new IllegalArgumentException("toDate is mandatory");
    }
    Date dateFrom = Utils.parseRFC3339Date(fromDate);
    Date dateTo = Utils.parseRFC3339Date(toDate);

    if (dateFrom.after(dateTo)) {
      throw new IllegalArgumentException("Dates parameters are not set correctly");
    }
    return realizationsStorage.getAllRealizationsByDate(dateFrom, dateTo, offset, limit);
  }

  @Override
  public GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId, HistoryStatus status) throws IllegalArgumentException,
                                                                             ObjectNotFoundException {

    if (gHistoryId == null) {
      throw new IllegalArgumentException("GamificationActionsHistory id is mandatory");
    }
    GamificationActionsHistoryDTO gHistory = realizationsStorage.getRealizationById(gHistoryId);

    if (gHistory == null) {
      throw new ObjectNotFoundException("GamificationActionsHistory does not exist");
    }
    gHistory.setStatus(status.name());
    return realizationsStorage.updateRealizationStatus(gHistory);
  }
}

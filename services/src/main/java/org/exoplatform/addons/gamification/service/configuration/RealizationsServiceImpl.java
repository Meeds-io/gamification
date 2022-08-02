package org.exoplatform.addons.gamification.service.configuration;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
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
  public List<GamificationActionsHistoryDTO> getAllRealizationsByDate( RealizationsFilter filter,
                                                                      int offset,
                                                                      int limit) throws IllegalArgumentException {
    String fromDate = filter.getFromDate();
    String toDate = filter.getToDate();
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
    return realizationsStorage.getAllRealizationsByFilter(filter, offset, limit);
  }

  @Override
  public GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId,
                                                               HistoryStatus status,
                                                               String actionLabel,
                                                               Long points,
                                                               String domain) throws IllegalArgumentException,
                                                                              ObjectNotFoundException {

    if (gHistoryId == null) {
      throw new IllegalArgumentException("GamificationActionsHistory id is mandatory");
    }
    GamificationActionsHistoryDTO gHistory = realizationsStorage.getRealizationById(gHistoryId);

    if (gHistory == null) {
      throw new ObjectNotFoundException("GamificationActionsHistory does not exist");
    }
    if (!actionLabel.isEmpty()) {
      gHistory.setActionTitle(actionLabel);
    }
    if (points != 0) {
      gHistory.setGlobalScore(gHistory.getGlobalScore() - gHistory.getActionScore() + points);
      gHistory.setActionScore(points);
    }
    if (!domain.isEmpty()) {
      gHistory.setDomain(domain);
    }
    gHistory.setStatus(status.name());
    return realizationsStorage.updateRealizationStatus(gHistory);
  }
}

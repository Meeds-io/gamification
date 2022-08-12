package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.Date;
import java.util.List;

public class RealizationsServiceImpl implements RealizationsService {

  private RealizationsStorage realizationsStorage;

  public RealizationsServiceImpl(RealizationsStorage realizationsStorage) {
    this.realizationsStorage = realizationsStorage;
  }

  @Override
  public List<GamificationActionsHistoryDTO> getAllRealizationsByFilter(RealizationsFilter filter,
                                                                        int offset,
                                                                        int limit) {
    if (filter == null) {
      throw new IllegalArgumentException("filter is mandatory");
    }
    Date fromDate = filter.getFromDate();
    Date toDate = filter.getToDate();
    String userId = filter.getUserId();
    if (fromDate == null) {
      throw new IllegalArgumentException("fromDate is mandatory");
    }
    if (toDate == null) {
      throw new IllegalArgumentException("toDate is mandatory");
    }
    if (userId == null) {
      throw new IllegalArgumentException("userId is mandatory");
    }
    if (fromDate.after(toDate)) {
      throw new IllegalArgumentException("Dates parameters are not set correctly");
    }
    Identity identity = new Identity(userId);
    boolean isAdministrator = identity != null ? identity.isMemberOf("/platform/administrators") : false;
    filter.setAdministrator(isAdministrator);
    return realizationsStorage.getAllRealizationsByFilter(filter, offset, limit);
  }

  @Override
  public GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId,
                                                               HistoryStatus status,
                                                               String actionLabel,
                                                               Long points,
                                                               String domain) throws ObjectNotFoundException {

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

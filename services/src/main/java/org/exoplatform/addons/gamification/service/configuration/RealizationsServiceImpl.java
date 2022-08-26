package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import java.util.Date;
import java.util.List;

public class RealizationsServiceImpl implements RealizationsService {

  private RealizationsStorage realizationsStorage;
  
  private IdentityManager     identityManager;

  public RealizationsServiceImpl(RealizationsStorage realizationsStorage, IdentityManager identityManager) {
    this.realizationsStorage = realizationsStorage;
    this.identityManager = identityManager;
  }

  @Override
  public List<GamificationActionsHistoryDTO> getRealizationsByFilter(RealizationsFilter filter,
                                                                     Identity identity,
                                                                     int offset,
                                                                     int limit) throws IllegalAccessException {
    if (filter == null) {
      throw new IllegalArgumentException("filter is mandatory");
    }
    if (identity == null) {
      throw new IllegalArgumentException("identity is mandatory");
    }
    Date fromDate = filter.getFromDate();
    Date toDate = filter.getToDate();
    if (fromDate == null) {
      throw new IllegalArgumentException("fromDate is mandatory");
    }
    if (toDate == null) {
      throw new IllegalArgumentException("toDate is mandatory");
    }
    if (fromDate.after(toDate)) {
      throw new IllegalArgumentException("Dates parameters are not set correctly");
    }
    String username = identity.getUserId();
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (isAdministrator(identity)) {
      return realizationsStorage.getAllRealizationsByFilter(filter, offset, limit);
    }
    if (filter.getEarnerId() == Long.parseLong(userIdentity.getId())) {
      return realizationsStorage.getUsersRealizationsByFilter(filter, offset, limit);

    } else {
      throw new IllegalAccessException("User doesn't have enough privileges to access achievements of user "
          + filter.getEarnerId());
    }
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
  
  private boolean isAdministrator(Identity identity) {
    return identity.isMemberOf("/platform/administrators");
  }
}

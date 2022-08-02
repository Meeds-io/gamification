package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;

public interface RealizationsService {
  /**
   * Retrieves all Realizations by Date.
   *
   * @param RealizationsFilter filter
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List <GamificationActionsHistoryDTO>} object
   * @throws IllegalAccessException when RealizationsFilter parameters are not set correctly
   */
  List<GamificationActionsHistoryDTO> getAllRealizationsByDate(RealizationsFilter filter,
                                                               int offset,
                                                               int limit) throws IllegalArgumentException;

  /**
   * Retrieves all Realizations by Date.
   *
   * @param gHistoryId gHistoryId
   * @param status status
   * @throws IllegalAccessException when GamificationActionsHistory id is not set correctly
   *           announcement
   * @throws ObjectNotFoundException GamificationActionsHistory identified by its
   *           technical identifier is not found
   */
  GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId, HistoryStatus status, String actionLabel, Long points, String domain) throws IllegalArgumentException, ObjectNotFoundException;
}

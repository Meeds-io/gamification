package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;

public interface RealizationsService {
  /**
   * Retrieves all Realizations by Date.
   *
   * @param fromDate fromDate
   * @param toDate toDate
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List <GamificationActionsHistoryDTO>} object
   * @throws IllegalAccessException when user is not authorized to access
   *           announcement
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  List<GamificationActionsHistoryDTO> getAllRealizationsByDate(String fromDate,
                                                               String toDate,
                                                               int offset,
                                                               int limit) throws IllegalArgumentException;;

  /**
   * Retrieves all Realizations by Date.
   *
   * @param gHistoryId gHistoryId
   * @param status status
   * @throws IllegalAccessException when user is not authorized to access
   *           announcement
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId, HistoryStatus status) throws IllegalArgumentException, ObjectNotFoundException;
}

package org.exoplatform.addons.gamification.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

public interface RealizationsService {

  /**
   * Retrieves all Realizations by Filter.
   *
   * @param filter used to filter realizations using {@link RealizationsFilter}
   * @param current identity {@link Identity}
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List <GamificationActionsHistoryDTO>} object
   * @throws IllegalAccessException when User doesn't have enough privileges to access achievements of user
   */
  List<GamificationActionsHistoryDTO> getRealizationsByFilter(RealizationsFilter filter,
                                                              Identity identity,
                                                              int offset,
                                                              int limit) throws IllegalAccessException;

  /**
   * Retrieves all Realizations by Date.
   *
   * @param gHistoryId gHistoryId
   * @param status status
   * @throws ObjectNotFoundException GamificationActionsHistory identified by its
   *           technical identifier is not found
   */
  GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId, HistoryStatus status, String actionLabel, Long points, String domain) throws ObjectNotFoundException;

  /**
   * compute xlsx from all Realizations .
   *
   * @param A {@link List <GamificationActionsHistoryDTO>} object
   * @param A filename String 
   * @return A xlsx InputStream {@link InputStream}
   * @throws IOException
   * @throws ObjectNotFoundException GamificationActionsHistory identified by
   *           its technical identifier is not found
   */
  InputStream exportXlsx(String fileName,
                   List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) throws IllegalAccessException,
                                                                                                      IOException;
  
}


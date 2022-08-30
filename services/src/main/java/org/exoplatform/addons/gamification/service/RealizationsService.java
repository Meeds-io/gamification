package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface RealizationsService {

  /**
   * Retrieves all Realizations by Filter.
   *
   * @param filter used to filter realizations using {@link RealizationsFilter}
   * @param current identity {@link Identity}
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List <GamificationActionsHistoryDTO>} object
   * @throws IllegalAccessException 
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
   * compute xls from all Realizations .
   *
   * @param A {@link List <GamificationActionsHistoryDTO>} object
   * @return A {xls string
   * @throws IOException 
   * @throws ObjectNotFoundException GamificationActionsHistory identified by its
   *           technical identifier is not found
   */
  File writeXlsx(String filePath, String fileName,List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) throws IllegalAccessException, IOException;
  
}


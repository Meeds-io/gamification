package org.exoplatform.addons.gamification.service;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

public interface RealizationsService {

  /**
   * Retrieves all Realizations by Filter.
   *
   * @param filter used to filter realizations using {@link RealizationsFilter}
   * @param identity current identity {@link Identity}
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List &lt;GamificationActionsHistoryDTO&gt;} object
   * @throws IllegalAccessException when User doesn't have enough privileges to access achievements of user
   */
  List<GamificationActionsHistoryDTO> getRealizationsByFilter(RealizationsFilter filter,
                                                              Identity identity,
                                                              int offset,
                                                              int limit) throws IllegalAccessException;

  /**
   * Count realizations by filter
   *
   * @param filter used to filter realizations using {@link RealizationsFilter}
   * @param identity current {@link Identity}
   * @return realizations count
   * @throws IllegalAccessException when User doesn't have enough privileges to
   *           access achievements of user
   */
  int countRealizationsByFilter(RealizationsFilter filter, Identity identity) throws IllegalAccessException;

  /**
   * Export realizations found switch filter into an {@link InputStream}
   * containing a file of format XLS
   * 
   * @param filter used to filter realizations using {@link RealizationsFilter}
   * @param identity current {@link Identity}
   * @param fileName fileName to export
   * @param locale used {@link Locale} for XLS header labels
   * @return {@link InputStream} of a file of format XLS
   * @throws IllegalAccessException when User doesn't have enough privileges to access achievements of user
   */
  InputStream exportXlsx(RealizationsFilter filter,
                         Identity identity,
                         String fileName,
                         Locale locale) throws IllegalAccessException;

  /**
   * Updates realization status.
   *
   * @param gHistoryId gHistoryId
   * @param status status
   * @return {@link GamificationActionsHistoryDTO} identified by its id when found
   * @throws ObjectNotFoundException GamificationActionsHistory identified by its
   *           technical identifier is not found
   */
  GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId, HistoryStatus status) throws ObjectNotFoundException;
  
  /**
   * Retrieves all Realizations by Date.
   *
   * @param gHistoryId gHistoryId
   * @param status status
   * @param actionLabel 
   * @param points 
   * @return {@link GamificationActionsHistoryDTO} identified by its id when found
   * @throws ObjectNotFoundException GamificationActionsHistory identified by its
   *           technical identifier is not found
   */
  GamificationActionsHistoryDTO updateRealizationStatus(Long gHistoryId, HistoryStatus status, String actionLabel, Long points) throws ObjectNotFoundException;

}


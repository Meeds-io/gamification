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
   * Retrieves a realization identified by its technical identifier.
   *
   * @param realizationId technical identifier of a realization
   * @param username User name accessing realization
   * @return A {@link GamificationActionsHistoryDTO} object
   * @throws IllegalAccessException when user is not authorized to access
   *           realization
   */
  GamificationActionsHistoryDTO getRealizationById(long realizationId, String username) throws IllegalAccessException;

  /**
   * Retrieves a realization identified by its technical identifier.
   *
   * @param realizationId technical identifier of a realization
   * @return A {@link GamificationActionsHistoryDTO} object
   */
  GamificationActionsHistoryDTO getRealizationById(long realizationId);

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
   * Updates an existing realization
   *
   * @param realization {@link GamificationActionsHistoryDTO} object to update
   * @param username User name updating realization
   * @throws IllegalAccessException when user is not authorized to update the
   *           realization
   * @throws ObjectNotFoundException when the realization identified by its
   *           technical identifier is not found
   */
  GamificationActionsHistoryDTO updateRealization(GamificationActionsHistoryDTO realization, String username) throws IllegalAccessException, ObjectNotFoundException;

  /**
   * Updates an existing realization
   *
   * @param realization {@link GamificationActionsHistoryDTO} object to update
   * @throws ObjectNotFoundException when the realization identified by its
   *           technical identifier is not found
   */
  GamificationActionsHistoryDTO updateRealization(GamificationActionsHistoryDTO realization) throws ObjectNotFoundException;

}


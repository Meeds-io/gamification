package org.exoplatform.addons.gamification.service.configuration;

import static org.exoplatform.addons.gamification.utils.Utils.escapeIllegalCharacterInMessage;
import static org.exoplatform.addons.gamification.utils.Utils.getCurrentUserLocale;
import static org.exoplatform.addons.gamification.utils.Utils.getI18NMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryRestEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

public class RealizationsServiceImpl implements RealizationsService {

  private static final Log    LOG       = ExoLogger.getLogger(RealizationsServiceImpl.class);

  private RealizationsStorage realizationsStorage;

  private IdentityManager     identityManager;

  // Delimiters that must be in the CSV file
  private static final String DELIMITER = ",";

  private static final String SEPARATOR = "\n";

  // File header
  private static final String HEADER    = "Date,Grantee,Action type,Program label,Action label,Points,Status,Spaces";

  private static final String SHEETNAME = "Achivements Report";

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
    if (isAdministrator(identity) || filter.getEarnerId() == Long.parseLong(userIdentity.getId())) {
      return filter.getEarnerId() > 0 ? realizationsStorage.getUsersRealizationsByFilter(filter, offset, limit)
                                      : realizationsStorage.getAllRealizationsByFilter(filter, offset, limit);
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

  @Override
  public InputStream exportXlsx(String fileName,
                               List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) throws IOException {
    String data = stringifyAchievements(gamificationActionsHistoryRestEntities);
    String[] dataToWrite = data.split("\\r?\\n");
    SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
    fileName += formatter.format(new Date());
    File temp = File.createTempFile(fileName, ".xlsx");
    temp.deleteOnExit();
    try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(temp)) {
      Sheet sheet = workbook.createSheet(SHEETNAME);
      CreationHelper helper = workbook.getCreationHelper();
      for (int i = 0; i < dataToWrite.length; i++) {
        Row row = sheet.createRow((short) i);
        String[] str = dataToWrite[i].split(",");
        for (int j = 0; j < str.length; j++) {
          row.createCell(j).setCellValue(helper.createRichTextString(str[j]));
        }
      }
      workbook.write(outputStream);
    }
    return new FileInputStream(temp);
  }

  private String stringifyAchievements(List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) {
    Locale locale = getCurrentUserLocale();
    StringBuilder sbResult = new StringBuilder();
    // Add header
    sbResult.append(HEADER);
    // Add a new line after the header
    sbResult.append(SEPARATOR);

    gamificationActionsHistoryRestEntities.forEach(ga -> {
      try {
        String actionLabelKey = "exoplatform.gamification.gamificationinformation.rule.description.";
        String domainTitleKey = "exoplatform.gamification.gamificationinformation.domain.";
        String actionLabel = "-";
        actionLabel = getI18NMessage(locale, actionLabelKey + ga.getActionLabel());
        if (actionLabel == null && ga.getAction() != null) {
          actionLabel = escapeIllegalCharacterInMessage(ga.getAction().getTitle());
        } else {
          actionLabel = escapeIllegalCharacterInMessage(actionLabel);
        }
        String domainDescription = "-";
        if (ga.getDomain() != null) {
          domainDescription = getI18NMessage(locale, domainTitleKey + ga.getDomain().getDescription().replace(" ", ""));
          if (domainDescription == null) {
            domainDescription = ga.getDomain().getDescription();
          }
        }
        domainDescription = escapeIllegalCharacterInMessage(domainDescription);
        sbResult.append(ga.getCreatedDate());
        sbResult.append(DELIMITER);
        sbResult.append(ga.getEarner());
        sbResult.append(DELIMITER);
        sbResult.append(ga.getAction() != null ? ga.getAction().getType().name() : "-");
        sbResult.append(DELIMITER);
        sbResult.append(domainDescription);
        sbResult.append(DELIMITER);
        sbResult.append(actionLabel);
        sbResult.append(DELIMITER);
        sbResult.append(ga.getScore());
        sbResult.append(DELIMITER);
        sbResult.append(ga.getStatus());
        sbResult.append(DELIMITER);
        sbResult.append(SEPARATOR);
      } catch (Exception e) {
        LOG.error("Error when computing to XLSX ", e);
      }
    });
    return sbResult.toString();
  }

  private boolean isAdministrator(Identity identity) {
    return identity.isMemberOf("/platform/administrators");
  }
}

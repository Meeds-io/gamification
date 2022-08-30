package org.exoplatform.addons.gamification.service.configuration;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.exoplatform.addons.gamification.rest.RealizationsRest;
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

import static org.exoplatform.addons.gamification.utils.Utils.escapeIllegalCharacterInMessage;
import static org.exoplatform.addons.gamification.utils.Utils.getCurrentUserLocale;
import static org.exoplatform.addons.gamification.utils.Utils.getI18NMessage;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RealizationsServiceImpl implements RealizationsService {

  private RealizationsStorage realizationsStorage;
  
  private IdentityManager     identityManager;
  
  // Delimiters that must be in the CSV file
  private static final String DELIMITER = ",";

  private static final String SEPARATOR = "\n";

  private static final Log    LOG       = ExoLogger.getLogger(RealizationsService.class);

  private SimpleDateFormat    formater  = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");

  // File header
  private static final String HEADER    = "Date,Grantee,Action label,Action type,Program label,Points,Status,Spaces";

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
      throw new IllegalAccessException("User doesn't have enough privileges to access achievements of user " + filter.getEarnerId());
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
  
  @SuppressWarnings({ "resource", "static-access" })
  public File writeXlsx(String filePath, String fileName,List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) throws IOException {
    String data = computeXls(gamificationActionsHistoryRestEntities);
    String[] dataToWrite = new String[]{data};
    fileName += formater.format(new Date());
    // Create an object of File class to open xlsx file
    File temp = new File(fileName, ".xlsx"); // NOSONAR
    temp.deleteOnExit();
    // Create an object of InputStream class to read excel file
    Workbook workbook = null;
    WorkbookFactory workBookFactory = null ;
    if (!temp.exists()) {
       if (temp.toString().endsWith(".xlsx")) {
          workbook = new XSSFWorkbook();
       } else {
          workbook = new HSSFWorkbook();
       }
    } else {
       workbook = workBookFactory.create(new FileInputStream(temp));
    }
    if(workbook.getSheet("Achivements Report") == null) {
      workbook.createSheet("Achivements Report");
    }
//Read excel sheet by sheet name    
    Sheet sheet = workbook.getSheetAt(0);
//Get the first row from the sheet
    sheet.createRow(0);
    Row row = sheet.getRow(0);
    Row newRow = sheet.createRow(1);
//Create a loop over the cell of newly created Row
    for (int j = 0; j < row.getLastCellNum(); j++) {
      // Fill data in row
      Cell cell = newRow.createCell(j);
      cell.setCellValue(dataToWrite[j]);
    }
//Create an object of FileOutputStream class to create write data in excel file
    FileOutputStream outputStream = new FileOutputStream(temp.getName() + ".xlsx");
//write data in the excel file
    workbook.write(outputStream);
//close output stream
    outputStream.close();
    return temp;
  }


  public String computeXls(List<GamificationActionsHistoryRestEntity> gamificationActionsHistoryRestEntities) {
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
        sbResult.append(actionLabel);
        sbResult.append(DELIMITER);
        sbResult.append(ga.getAction() != null ? ga.getAction().getType().name() : "-");
        sbResult.append(DELIMITER);
        sbResult.append(domainDescription);
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
  
}

package org.exoplatform.addons.gamification.service.configuration;

import static org.exoplatform.addons.gamification.utils.Utils.escapeIllegalCharacterInMessage;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.exoplatform.addons.gamification.service.RealizationsService;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationsFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.api.persistence.ExoTransactional;
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
  private static final String HEADER    = "Date,Grantee,Action type,Program label,Action label,Points,Status";

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
    if (Utils.isSuperManager(username) || (CollectionUtils.isNotEmpty(filter.getEarnerIds()) && filter.getEarnerIds().size() == 1
        && filter.getEarnerIds().contains(userIdentity.getId()))) {
      return realizationsStorage.getRealizationsByFilter(filter, offset, limit);
    } else {
      throw new IllegalAccessException("User doesn't have enough privileges to access achievements of user "
          + filter.getEarnerIds());
    }
  }

  @Override
  public int countRealizationsByFilter(RealizationsFilter filter, Identity identity) throws IllegalAccessException {
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
    if (Utils.isSuperManager(username) || (CollectionUtils.isNotEmpty(filter.getEarnerIds()) && filter.getEarnerIds().size() == 1
        && filter.getEarnerIds().get(0).equals(userIdentity.getId()))) {
      return realizationsStorage.countRealizationsByFilter(filter);
    } else {
      throw new IllegalAccessException("User doesn't have enough privileges to access achievements of user "
          + filter.getEarnerIds());
    }
  }

  @Override
  public GamificationActionsHistoryDTO getRealizationById(long realizationId) {
    if (realizationId <= 0) {
      throw new IllegalArgumentException("realization id is mandatory");
    }
    return realizationsStorage.getRealizationById(realizationId);
  }

  @Override
  public GamificationActionsHistoryDTO getRealizationById(long realizationId, String username) throws IllegalAccessException {
    if (realizationId <= 0) {
      throw new IllegalArgumentException("realization id is mandatory");
    }
    if (username == null) {
      throw new IllegalArgumentException("Username is mandatory");
    }
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    GamificationActionsHistoryDTO realization = realizationsStorage.getRealizationById(realizationId);
    if (Utils.isSuperManager(username) || realization.getEarnerId().equals(userIdentity.getId())) {
      return realization;
    } else {
      throw new IllegalAccessException("User doesn't have enough privileges to access achievement");
    }
  }

  @Override
  public GamificationActionsHistoryDTO updateRealization(GamificationActionsHistoryDTO realization,
                                                         String username) throws IllegalAccessException, ObjectNotFoundException {
    if (realization == null) {
      throw new IllegalArgumentException("Realization is mandatory");
    }
    if (username == null) {
      throw new IllegalArgumentException("Username is mandatory");
    }
    long realizationId = realization.getId();

    if (realizationId <= 0) {
      throw new IllegalArgumentException("Realization id has to be positive integer");
    }

    GamificationActionsHistoryDTO storedRealization = realizationsStorage.getRealizationById(realizationId);
    if (storedRealization == null) {
      throw new ObjectNotFoundException("Realization with id " + realizationId + " wasn't found");
    }
    if (Utils.isSuperManager(username)) {
      return realizationsStorage.updateRealization(realization);
    } else {
      throw new IllegalAccessException("User doesn't have enough privileges to update achievements of user"
          + realization.getEarnerId());
    }
  }

  @Override
  @ExoTransactional
  public GamificationActionsHistoryDTO updateRealization(GamificationActionsHistoryDTO realization) throws ObjectNotFoundException {
    if (realization == null) {
      throw new IllegalArgumentException("Realization is mandatory");
    }
    long realizationId = realization.getId();

    if (realizationId <= 0) {
      throw new IllegalArgumentException("Realization id has to be positive integer");
    }
    GamificationActionsHistoryDTO storedRealization = realizationsStorage.getRealizationById(realizationId);
    if (storedRealization == null) {
      throw new ObjectNotFoundException("Realization with id " + realizationId + " wasn't found");
    }
    return realizationsStorage.updateRealization(realization);
  }

  public GamificationActionsHistoryDTO findRealizationByActionTitleAndEarnerIdAndReceiverAndObjectId(String actionTitle,
                                                                                                     long domainId,
                                                                                                     String earnerId,
                                                                                                     String receiverId,
                                                                                                     String objectId,
                                                                                                     String objectType) {

    return realizationsStorage.findRealizationByActionTitleAndEarnerIdAndReceiverAndObjectId(actionTitle,
                                                                                             domainId,
                                                                                             earnerId,
                                                                                             receiverId,
                                                                                             objectId,
                                                                                             objectType);
  }

  public List<GamificationActionsHistoryDTO> getRealizationsByObjectIdAndObjectType(String objectId, String objectType) {
    return realizationsStorage.getRealizationsByObjectIdAndObjectType(objectId, objectType);
  }

  public InputStream exportXlsx(RealizationsFilter filter,
                                Identity identity,
                                String fileName,
                                Locale locale) throws IllegalAccessException {
    try {
      List<GamificationActionsHistoryDTO> realizations = getRealizationsByFilter(filter, identity, 0, -1);
      String data = stringifyAchievements(realizations, locale);
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
    } catch (IOException e) {
      throw new IllegalStateException("Error exporting XLSX file for achievements with filter " + filter, e);
    }
  }

  private String stringifyAchievements(List<GamificationActionsHistoryDTO> realizations, Locale locale) { // NOSONAR
    StringBuilder sbResult = new StringBuilder();
    // Add header
    sbResult.append(HEADER);
    // Add a new line after the header
    sbResult.append(SEPARATOR);

    realizations.forEach(ga -> {
      try {
        String actionLabelKey = "exoplatform.gamification.gamificationinformation.rule.title.";
        String domainTitleKey = "exoplatform.gamification.gamificationinformation.domain.";
        String actionLabel = "-";

        RuleDTO rule = ga.getRuleId() != null && ga.getRuleId() != 0 ? Utils.getRuleById(ga.getRuleId())
                                                                     : Utils.getRuleByTitle(ga.getActionTitle());

        String ruleTitle = rule == null ? null : rule.getEvent();
        String actionTitleKey = ga.getActionTitle() != null ? ga.getActionTitle() : ruleTitle;
        actionLabel = getI18NMessage(locale, actionLabelKey + actionTitleKey);
        if (actionLabel == null && rule != null && actionTitleKey != null) {
          actionLabel = escapeIllegalCharacterInMessage(rule.getEvent());
        } else {
          actionLabel = escapeIllegalCharacterInMessage(actionLabel);
        }
        String domainTitle = "-";
        if (ga.getDomainDTO() != null) {
          domainTitle = getI18NMessage(locale, domainTitleKey + ga.getDomainDTO().getTitle().replace(" ", ""));
          if (domainTitle == null) {
            domainTitle = ga.getDomainDTO().getTitle();
          }
        }
        domainTitle = escapeIllegalCharacterInMessage(domainTitle);
        sbResult.append(ga.getCreatedDate());
        sbResult.append(DELIMITER);
        sbResult.append(Utils.getUserFullName(ga.getEarnerId()));
        sbResult.append(DELIMITER);
        sbResult.append(rule != null ? rule.getType().name() : "-");
        sbResult.append(DELIMITER);
        sbResult.append(domainTitle);
        sbResult.append(DELIMITER);
        sbResult.append(actionLabel);
        sbResult.append(DELIMITER);
        sbResult.append(ga.getActionScore());
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

package org.exoplatform.addons.gamification.service.configuration;

import static java.util.Date.from;
import static org.exoplatform.addons.gamification.utils.Utils.escapeIllegalCharacterInMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.RealizationService;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.effective.LeaderboardFilter;
import org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard;
import org.exoplatform.addons.gamification.service.effective.ProfileReputation;
import org.exoplatform.addons.gamification.service.effective.StandardLeaderboard;
import org.exoplatform.addons.gamification.service.mapper.RealizationBuilder;
import org.exoplatform.addons.gamification.storage.RealizationStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class RealizationServiceImpl implements RealizationService {

  private static final Log    LOG       = ExoLogger.getLogger(RealizationServiceImpl.class);

  // Delimiters that must be in the CSV file
  private static final String DELIMITER = ",";

  private static final String SEPARATOR = "\n";

  // File header
  private static final String HEADER    = "Date,Grantee,Action type,Program label,Action label,Points,Status";

  private static final String SHEETNAME = "Achivements Report";

  private ProgramService      programService;

  private RuleService         ruleService;

  private IdentityManager     identityManager;

  private SpaceService        spaceService;

  private RealizationStorage  realizationStorage;

  public RealizationServiceImpl(ProgramService programService,
                                RuleService ruleService,
                                IdentityManager identityManager,
                                SpaceService spaceService,
                                RealizationStorage realizationStorage) {
    this.programService = programService;
    this.ruleService = ruleService;
    this.spaceService = spaceService;
    this.realizationStorage = realizationStorage;
    this.identityManager = identityManager;
  }

  @Override
  public List<RealizationDTO> getRealizationsByFilter(RealizationFilter realizationFilter,
                                                      Identity userAclIdentity,
                                                      int offset,
                                                      int limit) throws IllegalAccessException {
    if (realizationFilter == null) {
      throw new IllegalArgumentException("filter is mandatory");
    }
    if (userAclIdentity == null) {
      throw new IllegalArgumentException("identity is mandatory");
    }
    checkDates(realizationFilter.getFromDate(), realizationFilter.getToDate());
    computeProgramFilter(realizationFilter, userAclIdentity);
    return realizationStorage.getRealizationsByFilter(realizationFilter, offset, limit);
  }

  @Override
  public int countRealizationsByFilter(RealizationFilter realizationFilter,
                                       Identity userAclIdentity) throws IllegalAccessException {
    if (realizationFilter == null) {
      throw new IllegalArgumentException("filter is mandatory");
    }
    if (userAclIdentity == null) {
      throw new IllegalArgumentException("identity is mandatory");
    }
    checkDates(realizationFilter.getFromDate(), realizationFilter.getToDate());
    computeProgramFilter(realizationFilter, userAclIdentity);
    return realizationStorage.countRealizationsByFilter(realizationFilter);
  }

  @Override
  public List<RealizationDTO> findRealizationsByDateAndIdentityId(Date date, String earnerIdentityId) {
    return realizationStorage.findRealizationsByDateAndIdentityId(date, earnerIdentityId);
  }

  @Override
  public int getLeaderboardRank(String earnerIdentityId, Date date, Long domainId) {
    List<StandardLeaderboard> leaderboard = null;
    org.exoplatform.social.core.identity.model.Identity identity = identityManager.getIdentity(earnerIdentityId); // NOSONAR
    // :
    // profile load
    // is always true
    IdentityType identityType = IdentityType.getType(identity.getProviderId());
    if (date != null) {
      if (domainId == null || domainId <= 0) {
        leaderboard = realizationStorage.findRealizationsByDate(identityType, date);
      } else {
        leaderboard = realizationStorage.findRealizationsByDateAndDomain(identityType, date, domainId);
      }
    } else {
      if (domainId == null || domainId <= 0) {
        leaderboard = realizationStorage.findRealizationsAgnostic(identityType);
      } else {
        leaderboard = realizationStorage.findRealizationsByDomain(identityType, domainId);
      }
    }
    // Get username
    StandardLeaderboard item = leaderboard.stream()
                                          .filter(g -> earnerIdentityId.equals(g.getEarnerId()))
                                          .findAny()
                                          .orElse(null);
    return (leaderboard.indexOf(item) + 1);
  }

  @Override
  public long getScoreByIdentityId(String earnerIdentityId) {
    return realizationStorage.getScoreByIdentityId(earnerIdentityId);
  }

  @Override
  public List<ProfileReputation> getScorePerDomainByIdentityId(String earnerIdentityId) {
    return realizationStorage.getScorePerDomainByIdentityId(earnerIdentityId);
  }

  @Override
  public List<RealizationDTO> createRealizations(String event,
                                                 String earnerIdentityId,
                                                 String receiverIdentityId,
                                                 String objectId,
                                                 String objectType) {
    List<RuleDTO> ruleDtos = ruleService.findActiveRulesByEvent(event);
    if (CollectionUtils.isEmpty(ruleDtos)) {
      return Collections.emptyList();
    }
    return ruleDtos.stream()
                   .map(ruleDto -> RealizationBuilder.toRealization(this,
                                                                    identityManager,
                                                                    ruleDto,
                                                                    earnerIdentityId,
                                                                    receiverIdentityId,
                                                                    objectId,
                                                                    objectType))
                   .filter(Objects::nonNull)
                   .map(realizationStorage::createRealization)
                   .toList();
  }

  @Override
  public RealizationDTO updateRealization(RealizationDTO realization,
                                          Identity userAclIdentity) throws IllegalAccessException,
                                                                    ObjectNotFoundException {
    if (realization == null) {
      throw new IllegalArgumentException("Realization is mandatory");
    }
    if (userAclIdentity == null) {
      throw new IllegalArgumentException("identity is mandatory");
    }
    long realizationId = realization.getId();

    if (realizationId <= 0) {
      throw new IllegalArgumentException("Realization id has to be positive integer");
    }
    String username = userAclIdentity.getUserId();

    RealizationDTO storedRealization = realizationStorage.getRealizationById(realizationId);
    if (storedRealization == null) {
      throw new ObjectNotFoundException("Realization with id " + realizationId + " wasn't found");
    }
    if (Utils.isRewardingManager(username) || programService.isProgramOwner(realization.getProgram().getId(), userAclIdentity)) {
      return realizationStorage.updateRealization(realization);
    } else {
      throw new IllegalAccessException("User doesn't have enough privileges to update achievements of user"
          + realization.getEarnerId());
    }
  }

  @Override
  public RealizationDTO updateRealization(RealizationDTO realization) throws ObjectNotFoundException {
    if (realization == null) {
      throw new IllegalArgumentException("Realization is mandatory");
    }
    long realizationId = realization.getId();

    if (realizationId <= 0) {
      throw new IllegalArgumentException("Realization id has to be positive integer");
    }
    RealizationDTO storedRealization = realizationStorage.getRealizationById(realizationId);
    if (storedRealization == null) {
      throw new ObjectNotFoundException("Realization with id " + realizationId + " wasn't found");
    }
    return realizationStorage.updateRealization(realization);
  }

  @Override
  public List<RealizationDTO> cancelRealizations(String event,
                                                 String earnerIdentityId,
                                                 String receiverIdentityId,
                                                 String objectId,
                                                 String objectType) {
    List<RuleDTO> ruleDtos = null;
    // Get associated rules
    ruleDtos = ruleService.findActiveRulesByEvent(event);
    if (CollectionUtils.isEmpty(ruleDtos)) {
      return Collections.emptyList();
    }
    return ruleDtos.stream()
                   .map(rule -> realizationStorage.findRealizationByActionTitleAndEarnerIdAndReceiverAndObjectId(rule.getTitle(),
                                                                                                                 rule.getProgram()
                                                                                                                     .getId(),
                                                                                                                 earnerIdentityId,
                                                                                                                 receiverIdentityId,
                                                                                                                 objectId,
                                                                                                                 objectType))
                   .filter(Objects::nonNull)
                   .filter(realization -> !HistoryStatus.CANCELED.name().equals(realization.getStatus()))
                   .map(realization -> {
                     realization.setStatus(HistoryStatus.CANCELED.name());
                     realization.setActivityId(null);
                     realization.setObjectId(null);
                     return realizationStorage.updateRealization(realization);
                   })
                   .toList();
  }

  @Override
  public List<RealizationDTO> deleteRealizations(String objectId, String objectType) {
    List<RealizationDTO> realizations = findRealizationsByObjectIdAndObjectType(objectId,
                                                                                objectType);
    realizations.forEach(realization -> {
      if (!HistoryStatus.DELETED.name().equals(realization.getStatus())
          && !HistoryStatus.CANCELED.name().equals(realization.getStatus())) {
        realization.setStatus(HistoryStatus.DELETED.name());
        realization.setActivityId(null);
        realization.setObjectId(null);
        realizationStorage.updateRealization(realization);
      }
    });
    return realizations;
  }

  @Override
  public RealizationDTO findLatestRealizationByIdentityId(String earnerIdentityId) {
    return realizationStorage.findLatestRealizationByIdentityId(earnerIdentityId);
  }

  @Override
  public List<StandardLeaderboard> getLeaderboard(LeaderboardFilter filter) { // NOSONAR
    int limit = filter.getLoadCapacity();
    IdentityType identityType = filter.getIdentityType();
    if (identityType.isSpace()) {
      // Try to get more elements when searching, to be able to retrieve at
      // least 'limit' elements after filtering on authorized spaces
      limit = limit * 3;
    }

    List<StandardLeaderboard> result = null;
    if (filter.getDomainId() == null || filter.getDomainId() <= 0) {
      // Compute date
      LocalDate now = LocalDate.now();
      // Check the period
      if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {
        Date fromDate = from(now.with(DayOfWeek.MONDAY)
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant());
        result = realizationStorage.findRealizationsByDate(fromDate, identityType, limit);
      } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {
        Date fromDate = from(now.with(TemporalAdjusters.firstDayOfMonth())
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant());
        result = realizationStorage.findRealizationsByDate(fromDate, identityType, limit);
      } else {
        result = realizationStorage.findRealizations(identityType, limit);
      }
    } else {
      // Compute date
      LocalDate now = LocalDate.now();
      // Check the period
      if (filter.getPeriod().equals(LeaderboardFilter.Period.WEEK.name())) {
        Date fromDate = from(now.with(DayOfWeek.MONDAY)
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant());
        result = realizationStorage.findRealizationsByDateByDomain(fromDate, identityType, filter.getDomainId(), limit);
      } else if (filter.getPeriod().equals(LeaderboardFilter.Period.MONTH.name())) {
        Date fromDate = from(now.with(TemporalAdjusters.firstDayOfMonth())
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant());
        result = realizationStorage.findRealizationsByDateByDomain(fromDate, identityType, filter.getDomainId(), limit);
      } else {
        result = realizationStorage.findRealizationsByDomain(filter.getDomainId(), identityType, limit);
      }
    }

    // Filter on spaces switch user identity
    if (identityType.isSpace() && result != null && !result.isEmpty()) {
      final String currentUser = filter.getCurrentUser();

      if (StringUtils.isNotBlank(currentUser)) {
        result = filterAuthorizedSpaces(result, currentUser, filter.getLoadCapacity());
      }
    }

    return result;
  }

  @Override
  public List<PiechartLeaderboard> getStatsByIdentityId(String earnerIdentityId, Date startDate, Date endDate) {
    return realizationStorage.getStatsByIdentityId(earnerIdentityId, startDate, endDate);
  }

  @Override
  public long getScoreByIdentityIdAndBetweenDates(String earnerIdentityId, Date fromDate, Date toDate) {
    return realizationStorage.getScoreByIdentityIdAndBetweenDates(earnerIdentityId, fromDate, toDate);
  }

  @Override
  public Map<Long, Long> getScoresByIdentityIdsAndBetweenDates(List<String> earnersId, Date fromDate, Date toDate) {
    return realizationStorage.findUsersReputationScoreBetweenDate(earnersId, fromDate, toDate);
  }

  @Override
  public List<StandardLeaderboard> getLeaderboardBetweenDate(IdentityType earnedType, Date fromDate, Date toDate) {
    return realizationStorage.findAllLeaderboardBetweenDate(earnedType, fromDate, toDate);
  }

  @Override
  public List<RealizationDTO> findRealizationsByIdentityId(String earnerIdentityId, int limit) {
    return realizationStorage.findRealizationsByIdentityIdSortedByDate(earnerIdentityId, limit);
  }

  @Override
  public RealizationDTO getRealizationById(long realizationId) {
    if (realizationId <= 0) {
      throw new IllegalArgumentException("realization id is mandatory");
    }
    return realizationStorage.getRealizationById(realizationId);
  }

  @Override
  public RealizationDTO getRealizationById(long realizationId, Identity identity) throws IllegalAccessException,
                                                                                  ObjectNotFoundException {
    if (realizationId <= 0) {
      throw new IllegalArgumentException("realization id is mandatory");
    }
    if (identity == null) {
      throw new IllegalArgumentException("identity is mandatory");
    }
    String username = identity.getUserId();

    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    RealizationDTO realization = realizationStorage.getRealizationById(realizationId);
    if (realization == null) {
      throw new ObjectNotFoundException("Realization with id " + realizationId + " doesn't exist");
    } else if (Utils.isRewardingManager(username) || programService.isProgramOwner(realization.getProgram().getId(), identity)
        || realization.getEarnerId().equals(userIdentity.getId())) {
      return realization;
    } else {
      throw new IllegalAccessException("User doesn't have enough privileges to access achievement");
    }
  }

  @Override
  public List<RealizationDTO> findRealizationsByObjectIdAndObjectType(String objectId, String objectType) {
    return realizationStorage.findRealizationsByObjectIdAndObjectType(objectId, objectType);
  }

  @Override
  public InputStream exportXlsx(RealizationFilter filter,
                                Identity identity,
                                String fileName,
                                Locale locale) throws IllegalAccessException {
    try {
      List<RealizationDTO> realizations = getRealizationsByFilter(filter, identity, 0, -1);
      String data = stringifyAchievements(realizations, locale);
      String[] dataToWrite = data.split("\\r?\\n");
      SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
      fileName += formatter.format(new Date());
      File temp;
      if (SystemUtils.IS_OS_UNIX) {
        FileAttribute<Set<PosixFilePermission>> tempFileAttributes = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
        temp = Files.createTempFile(fileName, ".xlsx", tempFileAttributes).toFile();
      } else {
        temp = Files.createTempFile(fileName, ".xlsx").toFile();
        if (!temp.setReadable(true, true) || !temp.setWritable(true, true)) {
          throw new IllegalStateException("Can't write a temp file to export XLS achievements file");
        }
      }

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

  private String stringifyAchievements(List<RealizationDTO> realizations, Locale locale) { // NOSONAR
    StringBuilder sbResult = new StringBuilder();
    // Add header
    sbResult.append(HEADER);
    // Add a new line after the header
    sbResult.append(SEPARATOR);

    realizations.forEach(ga -> {
      try {

        RuleDTO rule = ga.getRuleId() != null && ga.getRuleId() != 0 ? Utils.getRuleById(ruleService, ga.getRuleId())
                                                                     : Utils.getRuleByTitle(ruleService, ga.getActionTitle());

        String ruleTitle = rule == null ? null : rule.getEvent();
        String actionLabel = ga.getActionTitle() != null ? ga.getActionTitle() : ruleTitle;
        String domainTitle = escapeIllegalCharacterInMessage(ga.getDomainLabel());
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

  private void computeProgramFilter(RealizationFilter realizationFilter,
                                    Identity userAclIdentity) throws IllegalAccessException {
    String username = userAclIdentity.getUserId();

    if (Utils.isRewardingManager(username)) {
      return;
    }

    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    List<Long> filterDomainIds = realizationFilter.getDomainIds();

    boolean isSelfFilter = CollectionUtils.isNotEmpty(realizationFilter.getEarnerIds())
        && realizationFilter.getEarnerIds().size() == 1
        && realizationFilter.getEarnerIds().get(0).equals(userIdentity.getId());
    boolean isFilterByDomains = CollectionUtils.isNotEmpty(filterDomainIds);

    if (isSelfFilter) {
      if (isFilterByDomains && !isDomainsMember(filterDomainIds, userAclIdentity)) {
        throw new IllegalAccessException("User is not member of one or several selected domains :"
            + filterDomainIds);
      }
    } else {
      if (isFilterByDomains && !isDomainsOwner(filterDomainIds, userAclIdentity)) {
        throw new IllegalAccessException("User is not owner of one or several selected domains :"
            + filterDomainIds);
      } else if (!isFilterByDomains) {
        List<Long> ownedDomainIds = getOwnedDomainIds(userIdentity);
        if (CollectionUtils.isEmpty(ownedDomainIds)) {
          throw new IllegalAccessException("User is not owner of any domain");
        }
        realizationFilter.setDomainIds(ownedDomainIds);
      }
    }
  }

  private List<Long> getOwnedDomainIds(org.exoplatform.social.core.identity.model.Identity userIdentity) throws IllegalAccessException {
    ProgramFilter domainFilter = new ProgramFilter();
    domainFilter.setOwnerId(Long.parseLong(userIdentity.getId()));
    List<String> managedSpaceIds = spaceService.getManagerSpacesIds(userIdentity.getRemoteId(), 0, -1);
    if (CollectionUtils.isEmpty(managedSpaceIds)) {
      domainFilter.setSpacesIds(Collections.emptyList());
    } else {
      domainFilter.setSpacesIds(managedSpaceIds.stream().map(Long::parseLong).toList());
    }
    return programService.getProgramIdsByFilter(domainFilter, userIdentity.getRemoteId(), 0, -1);
  }

  private boolean isDomainsOwner(List<Long> domainIds, Identity userAclIdentity) {
    return domainIds.stream()
                    .allMatch(domainId -> programService.isProgramOwner(domainId, userAclIdentity));
  }

  private boolean isDomainsMember(List<Long> domainIds, Identity userAclIdentity) {
    return domainIds.stream()
                    .allMatch(domainId -> programService.isProgramMember(domainId,
                                                                       userAclIdentity));
  }

  private List<StandardLeaderboard> filterAuthorizedSpaces(List<StandardLeaderboard> result,
                                                           final String currentUser,
                                                           int limit) {
    result = result.stream().filter(spacePoint -> {
      String spaceIdentityId = spacePoint.getEarnerId();
      org.exoplatform.social.core.identity.model.Identity identity = identityManager.getIdentity(spaceIdentityId);
      if (identity == null) {
        LOG.debug("Space Identity with id {} was deleted, ignore it", spaceIdentityId);
        return false;
      }
      String spacePrettyName = identity.getRemoteId();
      Space space = spaceService.getSpaceByPrettyName(spacePrettyName);
      return space != null && (!Space.HIDDEN.equals(space.getVisibility()) || spaceService.isMember(space, currentUser));
    }).limit(limit).toList();
    return result;
  }

  private void checkDates(Date fromDate, Date toDate) {
    if (fromDate == null) {
      throw new IllegalArgumentException("fromDate is mandatory");
    }
    if (toDate == null) {
      throw new IllegalArgumentException("toDate is mandatory");
    }
    if (fromDate.after(toDate)) {
      throw new IllegalArgumentException("Dates parameters are not set correctly");
    }
  }

}

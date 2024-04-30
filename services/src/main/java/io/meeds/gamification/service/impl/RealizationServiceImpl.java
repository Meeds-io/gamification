package io.meeds.gamification.service.impl;

import static io.meeds.gamification.constant.GamificationConstant.*;
import static io.meeds.gamification.listener.GamificationGenericListener.GENERIC_EVENT_NAME;
import static io.meeds.gamification.utils.Utils.*;
import static java.util.Date.from;

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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import io.meeds.gamification.constant.*;
import io.meeds.gamification.plugin.EventPlugin;
import io.meeds.gamification.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.PluginKey;
import org.exoplatform.commons.notification.impl.NotificationContextImpl;
import org.picocontainer.Startable;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.PiechartLeaderboard;
import io.meeds.gamification.model.ProfileReputation;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.StandardLeaderboard;
import io.meeds.gamification.model.filter.LeaderboardFilter;
import io.meeds.gamification.model.filter.RealizationFilter;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.rest.model.RealizationValidityContext;
import io.meeds.gamification.storage.RealizationStorage;
import io.meeds.gamification.utils.Utils;

public class RealizationServiceImpl implements RealizationService, Startable {

  private static final String   REALIZATION_NOT_EXIST_MESSAGE = "Realization with id %s doesn't exist";

  private static final Log      LOG                           = ExoLogger.getLogger(RealizationServiceImpl.class);

  // File header
  private static final String[] COLUMNS                       = new String[] { "date", "grantee", "actionType", "programLabel",
      "actionLabel", "points", "status" };

  private static final String   SHEETNAME                     = "Achivements Report";

  private ExecutorService       executorService;

  private ProgramService        programService;

  private RuleService           ruleService;

  private EventService          eventService;

  private IdentityManager       identityManager;

  private SpaceService          spaceService;

  private ResourceBundleService resourceBundleService;

  private ListenerService       listenerService;

  private RealizationStorage    realizationStorage;

  private String                blacklistMembershipExpression = Utils.BLACK_LIST_GROUP;

  private MembershipEntry       blacklistMembership;

  public RealizationServiceImpl(ProgramService programService, // NOSONAR
                                RuleService ruleService,
                                ResourceBundleService resourceBundleService,
                                EventService eventService,
                                IdentityManager identityManager,
                                SpaceService spaceService,
                                RealizationStorage realizationStorage,
                                ListenerService listenerService,
                                InitParams initParams) {
    this.programService = programService;
    this.ruleService = ruleService;
    this.resourceBundleService = resourceBundleService;
    this.eventService = eventService;
    this.spaceService = spaceService;
    this.realizationStorage = realizationStorage;
    this.identityManager = identityManager;
    this.listenerService = listenerService;

    if (initParams != null && initParams.containsKey("blacklist.group")) {
      this.blacklistMembershipExpression = initParams.getValueParam("blacklist.group").getValue();
    }
    this.blacklistMembership = MembershipEntry.parse(this.blacklistMembershipExpression);
    if (this.blacklistMembership == null) {
      this.blacklistMembership = new MembershipEntry(this.blacklistMembershipExpression);
    }
  }

  @Override
  public void start() {
    QueuedThreadPool threadFactory = new QueuedThreadPool(5, 1, 1);
    threadFactory.setName("Gamification - Realization");
    executorService = Executors.newCachedThreadPool(threadFactory);
  }

  @Override
  public void stop() {
    executorService.shutdown();
  }

  @Override
  public List<RealizationDTO> getRealizationsByFilter(RealizationFilter realizationFilter,
                                                      Identity userAclIdentity,
                                                      int offset,
                                                      int limit) throws IllegalAccessException {
    realizationFilter = computeProgramFilter(realizationFilter, userAclIdentity);
    if (realizationFilter == null) {
      return Collections.emptyList();
    } else {
      return getRealizationsByFilter(realizationFilter, offset, limit);
    }
  }

  @Override
  public List<RealizationDTO> getRealizationsByFilter(RealizationFilter realizationFilter, int offset, int limit) {
    return realizationStorage.getRealizationsByFilter(realizationFilter, offset, limit);
  }

  @Override
  public int countRealizationsByFilter(RealizationFilter realizationFilter,
                                       Identity userAclIdentity) throws IllegalAccessException {
    realizationFilter = computeProgramFilter(realizationFilter, userAclIdentity);
    if (realizationFilter == null) {
      return 0;
    } else {
      return countRealizationsByFilter(realizationFilter);
    }
  }

  @Override
  public int countRealizationsByFilter(RealizationFilter realizationFilter) {
    return realizationStorage.countRealizationsByFilter(realizationFilter);
  }

  @Override
  public int getLeaderboardRank(String earnerIdentityId, Date fromDate, Long programId) {
    org.exoplatform.social.core.identity.model.Identity identity = identityManager.getIdentity(earnerIdentityId); // NOSONAR
    IdentityType identityType = IdentityType.getType(identity.getProviderId());
    if (fromDate != null) {
      if (programId == null || programId <= 0) {
        return realizationStorage.getLeaderboardRankByDate(identityType, earnerIdentityId, fromDate);
      } else {
        return realizationStorage.getLeaderboardRankByDateAndProgramId(identityType, earnerIdentityId, fromDate, programId);
      }
    } else {
      if (programId == null || programId <= 0) {
        return realizationStorage.getLeaderboardRank(identityType, earnerIdentityId);
      } else {
        return realizationStorage.getLeaderboardRankByProgramId(identityType, earnerIdentityId, programId);
      }
    }
  }

  @Override
  public long getScoreByIdentityId(String earnerIdentityId) {
    return realizationStorage.getScoreByIdentityId(earnerIdentityId);
  }

  @Override
  public List<ProfileReputation> getScorePerProgramByIdentityId(String earnerIdentityId) {
    return realizationStorage.getScorePerProgramByIdentityId(earnerIdentityId);
  }

  @Override
  public void createRealizationsAsync(String event,
                                      String eventDetails,
                                      String earnerIdentityId,
                                      String receiverIdentityId,
                                      String objectId,
                                      String objectType) {
    executorService.execute(() -> createRealizationsAsyncInternal(event,
                                                                  eventDetails,
                                                                  earnerIdentityId,
                                                                  receiverIdentityId,
                                                                  objectId,
                                                                  objectType));
  }

  @Override
  public List<RealizationDTO> createRealizations(String event,
                                                 String eventDetails,
                                                 String earnerIdentityId,
                                                 String receiverIdentityId,
                                                 String objectId,
                                                 String objectType) {
    org.exoplatform.social.core.identity.model.Identity earnerIdentity = identityManager.getIdentity(earnerIdentityId);
    if (earnerIdentity == null || earnerIdentity.isDeleted() || !earnerIdentity.isEnable()) {
      return Collections.emptyList();
    }

    List<RuleDTO> rules = findActiveRulesByEventAndEarner(event, earnerIdentity);
    if (CollectionUtils.isEmpty(rules)) {
      return Collections.emptyList();
    }
    EventPlugin eventPlugin = eventService.getEventPlugin(event);
    if (eventPlugin != null) {
      rules = rules.stream()
                   .filter(ruleDTO -> MapUtils.isEmpty(ruleDTO.getEvent().getProperties())
                       || eventPlugin.isValidEvent(ruleDTO.getEvent().getProperties(), eventDetails))
                   .toList();
    }
    return rules.stream()
                .distinct()
                .filter(rule -> getRealizationValidityContext(rule, earnerIdentity.getId()).isValidForIdentity())
                .map(rule -> toRealization(rule, earnerIdentity, receiverIdentityId, objectId, objectType))
                .map(r -> {
                  r = realizationStorage.createRealization(r);
                  Utils.broadcastEvent(listenerService, POST_REALIZATION_CREATE_EVENT, r, null);
                  return r;
                })
                .toList();
  }

  @Override
  public void updateRealizationStatus(long realizationId, RealizationStatus status) throws ObjectNotFoundException {
    RealizationDTO realization = getRealizationById(realizationId);
    if (realization == null) {
      throw new ObjectNotFoundException(String.format(REALIZATION_NOT_EXIST_MESSAGE, realizationId));
    }
    if (status == null) {
      throw new IllegalArgumentException("status is mandatory");
    }
    realization.setStatus(status.name());
    updateRealizationStatus(realization, status);
  }

  @Override
  public void updateRealizationStatus(long realizationId,
                                      RealizationStatus status,
                                      String username) throws IllegalAccessException, ObjectNotFoundException {
    if (StringUtils.isBlank(username)) {
      throw new IllegalAccessException("username is mandatory");
    }
    if (status == null) {
      throw new IllegalArgumentException("status is mandatory");
    }
    Set<RealizationStatus> allowedStatus = Set.of(RealizationStatus.ACCEPTED,
                                                  RealizationStatus.REJECTED,
                                                  RealizationStatus.PENDING);
    if (!allowedStatus.contains(status)) {
      throw new IllegalArgumentException("Allowed manual status can be either ACCEPTED or REJECTED");
    }
    RealizationDTO realization = getRealizationById(realizationId);
    if (realization == null) {
      throw new ObjectNotFoundException(String.format(REALIZATION_NOT_EXIST_MESSAGE, realizationId));
    }

    if (!Utils.isRewardingManager(username) && !programService.isProgramOwner(realization.getProgram().getId(), username)) {
      throw new IllegalAccessException("User doesn't have enough privileges to update achievements of user"
          + realization.getEarnerId());
    }
    if (RealizationStatus.CANCELED.name().equals(realization.getStatus())
        || RealizationStatus.DELETED.name().equals(realization.getStatus())) {
      throw new IllegalArgumentException("Canceled achievement cannot be updated");
    }
    if (RealizationStatus.PENDING.name().equals(realization.getStatus()) && realization.getSendingDate() == null) {
      realization.setSendingDate(realization.getCreatedDate());
      realization.setCreatedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    }
    org.exoplatform.social.core.identity.model.Identity reviewerIdentity = identityManager.getOrCreateUserIdentity(username);
    if (reviewerIdentity != null) {
      realization.setReviewerId(Long.valueOf(reviewerIdentity.getId()));
    }
    realization.setStatus(status.name());
    updateRealizationStatus(realization, status);
    if (!RealizationStatus.PENDING.equals(status) && reviewerIdentity != null) {
      RuleDTO rule = ruleService.findRuleById(realization.getRuleId());
      String eventDetails = null;
      if (rule != null) {
        eventDetails = "{ruleId: " + rule.getId() + ", programId: " + rule.getProgram().getId() + "}";
      }
      createRealizations(GAMIFICATION_CONTRIBUTIONS_REVIEW_CONTRIBUTIONS,
                         eventDetails,
                         reviewerIdentity.getId(),
                         reviewerIdentity.getId(),
                         String.valueOf(realization.getActivityId()),
                         null);
    }
  }

  @Override
  public List<RealizationDTO> cancelRealizations(String event,
                                                 String earnerIdentityId,
                                                 String receiverIdentityId,
                                                 String objectId,
                                                 String objectType) {
    List<RuleDTO> rules = findActiveRulesByEvent(event);
    if (CollectionUtils.isEmpty(rules)) {
      return Collections.emptyList();
    }
    return rules.stream()
                .map(rule -> realizationStorage.findLastReadlizationByRuleIdAndEarnerIdAndReceiverAndObjectId(rule.getId(),
                                                                                                              earnerIdentityId,
                                                                                                              receiverIdentityId,
                                                                                                              objectId,
                                                                                                              objectType))
                .filter(Objects::nonNull)
                .filter(realization -> !RealizationStatus.CANCELED.name().equals(realization.getStatus()))
                .map(realization -> {
                  realization.setStatus(RealizationStatus.CANCELED.name());
                  realization.setActivityId(null);
                  realization.setObjectId(null);
                  try {
                    return realizationStorage.updateRealization(realization);
                  } catch (Exception e) {
                    LOG.warn("Error canceling realization with id {}", realization.getId(), e);
                    return null;
                  } finally {
                    Utils.broadcastEvent(listenerService, POST_REALIZATION_CANCEL_EVENT, realization, null);
                  }
                })
                .filter(Objects::nonNull)
                .toList();
  }

  @Override
  public List<RealizationDTO> deleteRealizations(String objectId, String objectType) {
    List<RealizationDTO> realizations = findRealizationsByObjectIdAndObjectType(objectId, objectType);
    realizations.forEach(realization -> {
      if (!RealizationStatus.DELETED.name().equals(realization.getStatus())
          && !RealizationStatus.CANCELED.name().equals(realization.getStatus())) {
        realization.setStatus(RealizationStatus.DELETED.name());
        realization.setActivityId(null);
        realization.setObjectId(null);
        try {
          realizationStorage.updateRealization(realization);
        } catch (Exception e) {
          LOG.warn("Error deleting realization with id {}", realization.getId(), e);
        } finally {
          Utils.broadcastEvent(listenerService, POST_REALIZATION_CANCEL_EVENT, realization, null);
        }
      }
    });
    return realizations;
  }

  @Override
  public RealizationValidityContext getRealizationValidityContext(RuleDTO rule, String earnerIdentityId) { // NOSONAR
    RealizationValidityContext realizationRestriction = new RealizationValidityContext();
    if (rule == null || rule.isDeleted() || !rule.isEnabled()) {
      realizationRestriction.setValidRule(false);
      return realizationRestriction;
    }

    org.exoplatform.social.core.identity.model.Identity identity = identityManager.getIdentity(earnerIdentityId);
    boolean anonymous = identity == null || identity.isDeleted() || !identity.isEnable();
    if (anonymous || (identity.isUser() && !programService.isProgramMember(rule.getProgram().getId(), identity.getRemoteId()))
        || (identity.isSpace() && !rule.isOpen() && rule.getSpaceId() != getSpaceId(identity.getRemoteId()))) {
      realizationRestriction.setValidIdentity(false);
    }
    if (!isValidProgram(rule.getProgram())) {
      realizationRestriction.setValidProgram(false);
      return realizationRestriction;
    } else {
      if (!isValidDates(rule)) {
        realizationRestriction.setValidDates(false);
      }
      if (!anonymous && !isRecurrenceValid(rule, earnerIdentityId)) {
        realizationRestriction.setValidRecurrence(false);
        RecurrenceType recurrence = rule.getRecurrence();
        if (recurrence == RecurrenceType.DAILY || recurrence == RecurrenceType.WEEKLY || recurrence == RecurrenceType.MONTHLY) {
          Date endDate = Utils.parseSimpleDate(rule.getEndDate());
          Instant nextDate = recurrence.getNextPeriodStartDate().toInstant();
          if (endDate == null || endDate.toInstant().isAfter(nextDate)) {
            Instant now = Instant.now();
            realizationRestriction.setNextOccurenceMillis(ChronoUnit.MILLIS.between(now, nextDate));
          }
        }
      }
      if (!anonymous && CollectionUtils.isNotEmpty(rule.getPrerequisiteRuleIds())) {
        realizationRestriction.setValidPrerequisites(new HashMap<>());
        rule.getPrerequisiteRuleIds().forEach(prerequisiteRuleId -> {
          boolean prerequisiteRealized = realizationStorage.countRealizationsByRuleIdAndEarnerId(earnerIdentityId,
                                                                                                 prerequisiteRuleId) > 0;
          // Rule Id made as string due to JsonGeneratorImpl which needs a
          // String as key
          realizationRestriction.getValidPrerequisites().put(String.valueOf(prerequisiteRuleId), prerequisiteRealized);
        });
      }
    }
    if (!anonymous && realizationRestriction.isValidForIdentity()) {
      if (!rule.isOpen()) {
        Space space = spaceService.getSpaceById(String.valueOf(rule.getSpaceId()));
        if (space == null) {
          realizationRestriction.setValidAudience(false);
        }
      }
      if (identity.isUser() && isUserBlacklisted(identity.getRemoteId())) {
        realizationRestriction.setValidWhitelist(false);
      }
    }
    return realizationRestriction;
  }

  @Override
  public List<StandardLeaderboard> getLeaderboard(LeaderboardFilter filter, String currentUser) throws IllegalAccessException { // NOSONAR
    int limit = filter.getLimit();
    IdentityType identityType = filter.getIdentityType();
    if (identityType.isSpace()) {
      if (StringUtils.isBlank(currentUser)) {
        throw new IllegalAccessException("Anonymous user can't access spaces board");
      } else {
        // Try to get more elements when searching, to be able to retrieve at
        // least 'limit' elements after filtering on authorized spaces
        limit = limit * 3;
      }
    }

    String period = filter.getPeriod();
    long programId = filter.getProgramId() == null ? 0 : filter.getProgramId();

    List<StandardLeaderboard> leaderboardItems = null;
    Date fromDate = getFromDate(period);
    if (programId <= 0) {
      // Compute date
      if (period.equals(Period.ALL.name())) {
        leaderboardItems = realizationStorage.getLeaderboard(identityType, filter.getOffset(), limit);
      } else {
        leaderboardItems = realizationStorage.getLeaderboardByDate(fromDate, identityType, filter.getOffset(), limit);
      }
    } else {
      // Check the period
      if (period.equals(Period.ALL.name())) {
        leaderboardItems = realizationStorage.getLeaderboardByProgramId(programId, identityType, filter.getOffset(), limit);
      } else {
        leaderboardItems = realizationStorage.getLeaderboardByDateByProgramId(fromDate,
                                                                              identityType,
                                                                              programId,
                                                                              filter.getOffset(),
                                                                              limit);
      }
    }

    // Filter on spaces switch user identity
    if (identityType.isSpace() && leaderboardItems != null && !leaderboardItems.isEmpty()) {
      leaderboardItems = filterAuthorizedSpaces(leaderboardItems, currentUser, filter.getLimit());
    }

    return leaderboardItems;
  }

  @Override
  public List<PiechartLeaderboard> getLeaderboardStatsByIdentityId(String earnerIdentityId, Date startDate, Date endDate) {
    return realizationStorage.getLeaderboardStatsByIdentityId(earnerIdentityId, startDate, endDate);
  }

  @Override
  public long getScoreByIdentityIdAndBetweenDates(String earnerIdentityId, Date fromDate, Date toDate) {
    return realizationStorage.getScoreByIdentityIdAndBetweenDates(earnerIdentityId, fromDate, toDate);
  }

  @Override
  public Map<Long, Long> getScoresByIdentityIdsAndBetweenDates(List<String> earnersId, Date fromDate, Date toDate) {
    return realizationStorage.getScoresByIdentityIdsAndBetweenDates(earnersId, fromDate, toDate);
  }

  @Override
  public long countParticipantsBetweenDates(Date fromDate, Date toDate) {
    return realizationStorage.countParticipantsBetweenDates(fromDate, toDate);
  }

  @Override
  public RealizationDTO getRealizationById(long realizationId) {
    if (realizationId <= 0) {
      throw new IllegalArgumentException("realization id is mandatory");
    }
    return realizationStorage.getRealizationById(realizationId);
  }

  @Override
  public boolean isRealizationManager(String username) {
    if (Utils.isRewardingManager(username)) {
      return true;
    }
    return programService.countOwnedPrograms(username) > 0;
  }

  @Override
  public RealizationDTO getRealizationById(long realizationId, Identity userAclIdentity) throws IllegalAccessException,
                                                                                         ObjectNotFoundException {
    if (realizationId <= 0) {
      throw new IllegalArgumentException("realization id is mandatory");
    }
    if (userAclIdentity == null) {
      throw new IllegalArgumentException("identity is mandatory");
    }
    String username = userAclIdentity.getUserId();

    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    RealizationDTO realization = realizationStorage.getRealizationById(realizationId);
    if (realization == null) {
      throw new ObjectNotFoundException(String.format(REALIZATION_NOT_EXIST_MESSAGE, realizationId));
    } else if (programService.canViewProgram(realization.getProgram().getId(), userAclIdentity.getUserId())
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

  public boolean hasPendingRealization(long ruleId, String earnerIdentityId) {
    return realizationStorage.hasPendingRealization(ruleId, earnerIdentityId);
  }

  @Override
  public InputStream exportXlsx(RealizationFilter filter,
                                Identity identity,
                                String fileName,
                                Locale locale) throws IllegalAccessException {
    File temp = null;
    try { // NOSONAR
      temp = createTempFile(fileName);

      List<RealizationDTO> realizations = getRealizationsByFilter(filter, identity, 0, -1);
      try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(temp)) {
        int rowIndex = 0;
        CreationHelper helper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet(SHEETNAME);
        appendRealizationHeaderRow(sheet, rowIndex++, helper, locale);
        for (RealizationDTO realization : realizations) {
          appendRealizationRow(sheet, rowIndex++, helper, realization);
        }
        workbook.write(outputStream);
      }
      return new FileInputStream(temp);
    } catch (IOException e) {
      throw new IllegalStateException("Error exporting XLSX file for achievements with filter " + filter, e);
    } finally {
      if (temp != null && temp.exists()) {
        temp.deleteOnExit();
      }
    }
  }

  @ExoTransactional
  public void createRealizationsAsyncInternal(String event,
                                              String eventDetails,
                                              String earnerIdentityId,
                                              String receiverIdentityId,
                                              String objectId,
                                              String objectType) {
    createRealizations(event, eventDetails, earnerIdentityId, receiverIdentityId, objectId, objectType);
  }

  private RealizationFilter computeProgramFilter(RealizationFilter realizationFilter, // NOSONAR
                                                 Identity userAclIdentity) throws IllegalAccessException {
    if (realizationFilter == null) {
      throw new IllegalArgumentException("filter is mandatory");
    }
    realizationFilter = realizationFilter.clone();
    checkDates(realizationFilter.getFromDate(), realizationFilter.getToDate());

    String username = userAclIdentity == null ? null : userAclIdentity.getUserId();
    if (Utils.isRewardingManager(username) || realizationFilter.isAllPrograms()) {
      return realizationFilter;
    }

    List<Long> filterProgramIds = computeFilteredPrograms(realizationFilter);
    boolean isFilterByPrograms = CollectionUtils.isNotEmpty(filterProgramIds);

    if (realizationFilter.isOwned()) {
      if (isFilterByPrograms && !isProgramsOwner(filterProgramIds, username)) {
        throw new IllegalAccessException("User is not owner of one or several selected programs :" + filterProgramIds);
      } else if (!isFilterByPrograms) {
        List<Long> ownedProgramIds = programService.getOwnedProgramIds(username, 0, -1);
        if (CollectionUtils.isEmpty(ownedProgramIds)) {
          return null;
        } else {
          realizationFilter.setProgramIds(ownedProgramIds);
        }
      }
    } else if (isFilterByPrograms && !canViewPrograms(filterProgramIds, username)) {
      throw new IllegalAccessException("User is not member of one or several selected programs :" + filterProgramIds);
    } else if (!isFilterByPrograms && !isSelfFilter(realizationFilter, username)) {
      List<Long> memberProgramIds = programService.getMemberProgramIds(username, 0, -1);
      if (CollectionUtils.isEmpty(memberProgramIds)) {
        return null;
      } else {
        realizationFilter.setProgramIds(memberProgramIds);
      }
    }
    return realizationFilter;
  }

  private boolean isSelfFilter(RealizationFilter realizationFilter, String username) {
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    boolean filterByEarner = CollectionUtils.isNotEmpty(realizationFilter.getEarnerIds());
    return filterByEarner && realizationFilter.getEarnerIds().size() == 1 && userIdentity != null
        && realizationFilter.getEarnerIds().get(0).equals(userIdentity.getId());
  }

  @SuppressWarnings("unchecked")
  private List<Long> computeFilteredPrograms(RealizationFilter realizationFilter) {
    List<Long> filterProgramIds = realizationFilter.getProgramIds();
    List<Long> ruleIds = realizationFilter.getRuleIds();
    if (CollectionUtils.isNotEmpty(ruleIds)) {
      Set<Long> programIds = ruleIds.stream().map(ruleId -> {
        RuleDTO rule = ruleService.findRuleById(ruleId);
        return rule == null || rule.getProgramId() == 0 ? null : rule.getProgramId();
      }).filter(Objects::nonNull).collect(Collectors.toSet());
      if (CollectionUtils.isEmpty(filterProgramIds)) {
        return programIds.stream().toList();
      } else {
        return CollectionUtils.intersection(filterProgramIds, programIds).stream().toList();
      }
    }
    return filterProgramIds;
  }

  private boolean isProgramsOwner(List<Long> programIds, String username) {
    return programIds.stream().allMatch(programId -> programService.isProgramOwner(programId, username));
  }

  private boolean canViewPrograms(List<Long> programIds, String username) {
    return programIds.stream().allMatch(programId -> programService.canViewProgram(programId, username));
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
      Space space = spaceService.getSpaceByPrettyName(identity.getRemoteId());
      return space != null && (!Space.HIDDEN.equals(space.getVisibility()) || spaceService.isMember(space, currentUser));
    }).limit(limit).toList();
    return result;
  }

  private void checkDates(Date fromDate, Date toDate) {
    if (fromDate != null && toDate != null && fromDate.after(toDate)) {
      throw new IllegalArgumentException("Dates parameters are not set correctly");
    }
  }

  private boolean isRecurrenceValid(RuleDTO rule, String earnerIdentityId) {
    return rule.getRecurrence() == null || rule.getRecurrence() == RecurrenceType.NONE
        || hasNoRealizationInPeriod(earnerIdentityId, rule.getId(), rule.getRecurrence().getPeriodStartDate());
  }

  private boolean hasNoRealizationInPeriod(String earnerIdentityId, Long ruleId, Date sinceDate) {
    return realizationStorage.countRealizationsInPeriod(earnerIdentityId, ruleId, sinceDate) == 0;
  }

  private RealizationDTO toRealization(RuleDTO ruleDto,
                                       org.exoplatform.social.core.identity.model.Identity earnerIdentity,
                                       String receiverIdentityId,
                                       String objectId,
                                       String objectType) {
    // Build only an entry when a rule enable and exist
    RealizationDTO realization = new RealizationDTO();
    realization.setActionScore(ruleDto.getScore());
    realization.setGlobalScore(getScoreByIdentityId(earnerIdentity.getId()) + ruleDto.getScore());
    realization.setEarnerId(earnerIdentity.getId());
    realization.setEarnerType(earnerIdentity.getProviderId());
    realization.setActionTitle(ruleDto.getTitle());
    realization.setRuleId(ruleDto.getId());
    realization.setProgram(ruleDto.getProgram());
    realization.setReceiver(receiverIdentityId);
    realization.setObjectId(objectId);
    realization.setObjectType(objectType);
    boolean isVerificationRequired = eventService.isVerificationRequiredForEvent(ruleDto.getEvent().getType(),
                                                                                 ruleDto.getEvent().getTrigger());
    realization.setStatus(isVerificationRequired ? RealizationStatus.PENDING.name() : RealizationStatus.ACCEPTED.name());
    realization.setType(ruleDto.getType());
    return realization;
  }

  private boolean isValidProgram(ProgramDTO program) {
    return program != null && program.isEnabled() && !program.isDeleted();
  }

  private boolean isValidDates(RuleDTO rule) {
    Date startDate = Utils.parseSimpleDate(rule.getStartDate());
    Date endDate = Utils.parseSimpleDate(rule.getEndDate());
    return (startDate == null || startDate.getTime() < System.currentTimeMillis())
        && (endDate == null || endDate.getTime() > System.currentTimeMillis());
  }

  private List<RuleDTO> findActiveRulesByEvent(String eventName) {
    return findActiveRulesByEventAndEarner(eventName, null);
  }

  private List<RuleDTO> findActiveRulesByEventAndEarner(String eventName,
                                                        org.exoplatform.social.core.identity.model.Identity earnerIdentity) {
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setDateFilterType(DateFilterType.STARTED);
    ruleFilter.setType(EntityFilterType.AUTOMATIC);
    ruleFilter.setStatus(EntityStatusType.ENABLED);
    ruleFilter.setProgramStatus(EntityStatusType.ENABLED);
    ruleFilter.setEventName(eventName);
    if (earnerIdentity != null && earnerIdentity.isUser()) {
      return ruleService.getRules(ruleFilter, earnerIdentity.getRemoteId(), 0, -1);
    } else {
      ruleFilter.setAllSpaces(true);
      return ruleService.getRules(ruleFilter, 0, -1);
    }
  }

  private boolean isUserBlacklisted(String username) {
    if (StringUtils.isBlank(username)) {
      return false;
    }
    org.exoplatform.services.security.Identity identity = Utils.getUserAclIdentity(username);
    if (identity == null) {
      return false;
    }
    return identity.isMemberOf(blacklistMembership);
  }

  private void updateRealizationStatus(RealizationDTO realization, RealizationStatus status) {
    try {
      realizationStorage.updateRealization(realization);
      if (RealizationStatus.ACCEPTED.name().equals(realization.getStatus())
          || RealizationStatus.REJECTED.name().equals(realization.getStatus())) {
        String notificationPluginKey =
                                     RealizationStatus.ACCEPTED.name()
                                                               .equals(realization.getStatus()) ? CONTRIBUTION_ACCEPTED_NOTIFICATION_ID
                                                                                                : CONTRIBUTION_REJECTED_NOTIFICATION_ID;
        NotificationContext ctx = NotificationContextImpl.cloneInstance();
        ctx.append(REALIZATION_NOTIFICATION_PARAMETER, realization)
           .getNotificationExecutor()
           .with(ctx.makeCommand(PluginKey.key(notificationPluginKey)))
           .execute(ctx);
      }
    } catch (Exception e) {
      LOG.warn("Error deleting realization with id {}", realization.getId(), e);
    } finally {
      String eventName = switch (status) {
      case CANCELED, DELETED, REJECTED: {
        yield POST_REALIZATION_CANCEL_EVENT;
      }
      default:
        yield POST_REALIZATION_UPDATE_EVENT;
      };
      Utils.broadcastEvent(listenerService, eventName, realization, null);
    }
  }

  private void appendRealizationHeaderRow(Sheet sheet, int rowIndex, CreationHelper helper, Locale locale) {
    Row row = sheet.createRow(rowIndex);
    ResourceBundle resourceBundle = resourceBundleService.getResourceBundle("locale.addon.Gamification", locale);
    for (int i = 0; i < COLUMNS.length; i++) {
      row.createCell(i).setCellValue(helper.createRichTextString(resourceBundle.getString("realization.label." + COLUMNS[i])));
    }
  }

  private void appendRealizationRow(Sheet sheet, int rowIndex, CreationHelper helper, RealizationDTO realization) {
    Row row = sheet.createRow(rowIndex);
    try {
      RuleDTO rule = realization.getRuleId() != null
          && realization.getRuleId() != 0 ? ruleService.findRuleById(realization.getRuleId())
                                          : ruleService.findRuleByTitle(realization.getActionTitle());

      String eventTitle = rule == null || rule.getEvent() == null ? null : rule.getEvent().getTitle();
      String actionLabel = realization.getActionTitle() != null ? realization.getActionTitle() : eventTitle;
      String programTitle = escapeIllegalCharacterInMessage(realization.getProgramLabel());
      int cellIndex = 0;
      row.createCell(cellIndex++).setCellValue(helper.createRichTextString(realization.getCreatedDate()));
      row.createCell(cellIndex++).setCellValue(Utils.getUserFullName(realization.getEarnerId()));
      row.createCell(cellIndex++).setCellValue(rule != null ? rule.getType().name() : "-");
      row.createCell(cellIndex++).setCellValue(programTitle);
      row.createCell(cellIndex++).setCellValue(actionLabel);
      row.createCell(cellIndex++).setCellValue(realization.getActionScore());
      row.createCell(cellIndex).setCellValue(realization.getStatus());
    } catch (Exception e) {
      LOG.error("Error when computing to XLSX ", e);
    }
  }

  private File createTempFile(String fileName) throws IOException {
    SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
    fileName += formatter.format(new Date());
    if (SystemUtils.IS_OS_UNIX) {
      FileAttribute<Set<PosixFilePermission>> tempFileAttributes =
                                                                 PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
      return Files.createTempFile(fileName, ".xlsx", tempFileAttributes).toFile();
    } else {
      File temp = Files.createTempFile(fileName, ".xlsx").toFile();
      if (!temp.setReadable(true, true) || !temp.setWritable(true, true)) {
        throw new IllegalStateException("Can't write a temp file to export XLS achievements file");
      }
      return temp;
    }
  }

  private Date getFromDate(String period) {
    Date fromDate = null;
    if (Period.WEEK.name().equals(period)) {
      fromDate = from(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
    } else if (Period.MONTH.name().equals(period)) {
      fromDate = from(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    return fromDate;
  }

  private long getSpaceId(String spacePrettyName) {
    Space space = spaceService.getSpaceByPrettyName(spacePrettyName);
    return space == null ? 0 : Long.parseLong(space.getId());
  }

}

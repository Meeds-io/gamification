package org.exoplatform.addons.gamification.utils;

import static org.exoplatform.analytics.utils.AnalyticsUtils.addSpaceStatistics;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.configuration.ProgramService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.entity.IdentityEntity;
import org.exoplatform.web.security.codec.CodecInitializer;
import org.exoplatform.web.security.security.TokenServiceInitializationException;

public class Utils {

  public static final String            ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM = "announcementDescription";

  public static final String            ANNOUNCEMENT_ID_TEMPLATE_PARAM          = "announcementId";

  public static final String            ANNOUNCEMENT_ACTIVITY_EVENT             = "challenge.announcement.activity";

  public static final long              DEFAULT_COVER_LAST_MODIFIED             = System.currentTimeMillis();

  public static final DateTimeFormatter RFC_3339_FORMATTER                      =
                                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                            .withResolverStyle(ResolverStyle.LENIENT);

  public static final DateTimeFormatter SIMPLE_DATE_FORMATTER                   =
                                                              DateTimeFormatter.ofPattern("yyyy-MM-dd['T00:00:00']")
                                                                               .withResolverStyle(ResolverStyle.LENIENT);

  private static final char[]           ILLEGAL_MESSAGE_CHARACTERS              = {
      ',', ';', '\n'
  };

  public static final String            STATISTICS_CREATE_PROGRAM_OPERATION     = "createProgram";

  public static final String            STATISTICS_UPDATE_PROGRAM_OPERATION     = "updateProgram";

  public static final String            STATISTICS_DELETE_PROGRAM_OPERATION     = "deleteProgram";

  public static final String            STATISTICS_CREATE_RULE_OPERATION        = "createRule";

  public static final String            STATISTICS_UPDATE_RULE_OPERATION        = "updateRule";

  public static final String            STATISTICS_DELETE_RULE_OPERATION        = "deleteRule";

  public static final String            STATISTICS_ENABLE_PROGRAM_OPERATION     = "enableProgram";

  public static final String            STATISTICS_DISABLE_PROGRAM_OPERATION    = "disableProgram";

  public static final String            STATISTICS_CREATE_ANNOUNCE_OPERATION    = "createAnnouncement";

  public static final String            STATISTICS_UPDATE_ANNOUNCE_OPERATION    = "updateAnnouncement";

  public static final String            STATISTICS_PROGRAM_ID_PARAM             = "programId";

  public static final String            STATISTICS_PROGRAM_TITLE_PARAM          = "programTitle";

  public static final String            STATISTICS_PROGRAM_BUDGET_PARAM         = "programBudget";

  public static final String            STATISTICS_PROGRAM_TYPE_PARAM           = "programType";

  public static final String            STATISTICS_PROGRAM_COVERFILEID_PARAM    = "programCoverFileId";

  public static final String            STATISTICS_PROGRAM_OWNERS_PARAM         = "programOwners";

  public static final String            STATISTICS_RULE_ID_PARAM                = "ruleId";

  public static final String            STATISTICS_RULE_TITLE_PARAM             = "ruleTitle";

  public static final String            STATISTICS_RULE_DESCRIPTION_PARAM       = "ruleDescription";

  public static final String            STATISTICS_RULE_SCORE_PARAM             = "ruleBudget";

  public static final String            STATISTICS_RULE_TYPE_PARAM              = "ruleType";

  public static final String            STATISTICS_RULE_COVERFILEID_PARAM       = "ruleCoverFileId";

  public static final String            STATISTICS_RULE_EVENT_PARAM             = "ruleEvent";

  public static final String            STATISTICS_RULE_SUBMODULE               = "rule";

  public static final String            STATISTICS_ANNOUNCEMENT_SUBMODULE       = "announcement";

  public static final String            STATISTICS_ANNOUNCE_ID_PARAM            = "announcementId";

  public static final String            STATISTICS_ANNOUNCE_ACTIVITY_PARAM      = "announcementActivityId";

  public static final String            STATISTICS_ANNOUNCE_ASSIGNEE_PARAM      = "announcementAssignee";

  public static final String            STATISTICS_ANNOUNCE_COMMENT_PARAM       = "announcementComment";

  public static final String            STATISTICS_PROGRAM_SUBMODULE            = "program";

  public static final String            STATISTICS_GAMIFICATION_MODULE          = "gamification";

  public static final String            POST_CREATE_RULE_EVENT                  = "rule.created";

  public static final String            POST_UPDATE_RULE_EVENT                  = "rule.updated";

  public static final String            POST_DELETE_RULE_EVENT                  = "rule.deleted";

  public static final String            POST_CREATE_ANNOUNCEMENT_EVENT          = "announcement.created";

  public static final String            POST_UPDATE_ANNOUNCEMENT_EVENT          = "announcement.updated";

  public static final String            ANNOUNCEMENT_ACTIVITY_TYPE              = "challenges-announcement";

  public static final String            SYSTEM_USERNAME                         = "SYSTEM";

  public static final String            BASE_URL_PROGRAMS_REST_API              = "/gamification/programs";

  public static final String            DEFAULT_IMAGE_REMOTE_ID                 = "default-cover";

  public static final String            TYPE                                    = "cover";

  public static final String            REWARDING_GROUP                         = "/platform/rewarding";

  public static final String            ADMINS_GROUP                            = "/platform/administrators";

  public static final String            BLACK_LIST_GROUP                        = "/leaderboard-blacklist-users";

  private static final String           IDENTITIES_REST_PATH                    = "/v1/social/identities";                // NOSONAR

  private static final String           IDENTITIES_EXPAND                       = "all";

  private static final Log              LOG                                     = ExoLogger.getLogger(Utils.class);

  private Utils() { // NOSONAR
  }

  public static Identity getIdentityByTypeAndId(String type, String name) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getOrCreateIdentity(type, name);
  }

  public static String getUserRemoteId(String identityId) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Identity identity = identityManager.getIdentity(identityId);
    return identity != null ? identity.getRemoteId() : null;
  }

  public static String getUserFullName(String id) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Identity identity = identityManager.getIdentity(id);
    return identity != null && identity.getProfile() != null ? identity.getProfile().getFullName() : null;
  }

  public static IdentityEntity getIdentityEntity(IdentityManager identityManager, long identityId) {
    Identity identity = identityManager.getIdentity(String.valueOf(identityId));
    if (identity == null) {
      return null;
    }
    return EntityBuilder.buildEntityIdentity(identity, IDENTITIES_REST_PATH, IDENTITIES_EXPAND);
  }

  public static final long getCurrentUserIdentityId() {
    String currentUser = getCurrentUser();
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Identity identity = identityManager.getOrCreateUserIdentity(currentUser);
    return identity == null ? 0l : Long.parseLong(identity.getId());
  }

  public static final String getCurrentUser() {
    if (ConversationState.getCurrent() != null && ConversationState.getCurrent().getIdentity() != null) {
      return ConversationState.getCurrent().getIdentity().getUserId();
    }
    return null;
  }

  public static final boolean canAnnounce(String spaceId, String username) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(spaceId);
    if (space == null || StringUtils.isBlank(username)) {
      return false;
    } else {
      org.exoplatform.services.security.Identity identity = getUserAclIdentity(username);
      return spaceService.canRedactOnSpace(space, identity) && !Utils.isUserMemberOfGroupOrUser(username, Utils.BLACK_LIST_GROUP);
    }
  }

  public static String toRFC3339Date(Date dateTime) {
    if (dateTime == null) {
      return null;
    }
    ZonedDateTime zonedDateTime = dateTime.toInstant().atZone(ZoneId.systemDefault());
    return zonedDateTime.format(RFC_3339_FORMATTER);
  }

  public static String toSimpleDateFormat(Date dateTime) {
    if (dateTime == null) {
      return null;
    }
    ZonedDateTime zonedDateTime = dateTime.toInstant().atZone(ZoneId.systemDefault());
    return zonedDateTime.format(SIMPLE_DATE_FORMATTER);
  }

  public static Date parseRFC3339Date(String dateString) {
    if (StringUtils.isBlank(dateString)) {
      return null;
    }
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, RFC_3339_FORMATTER).withZoneSameInstant(ZoneId.systemDefault());
    return Date.from(zonedDateTime.toInstant());
  }

  public static Date parseSimpleDate(String dateString) {
    if (StringUtils.isBlank(dateString)) {
      return null;
    }
    ZonedDateTime zonedDateTime = LocalDate.parse(dateString.substring(0, 10), SIMPLE_DATE_FORMATTER)
                                           .atStartOfDay(ZoneId.systemDefault());
    return Date.from(zonedDateTime.toInstant());
  }

  public static Space getSpaceById(String spaceId) {
    if (StringUtils.isBlank(spaceId)) {
      return null;
    }
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(spaceId);
    if (space == null) {
      LOG.warn("space with id {} do not exist", spaceId);
      return null;
    }
    return space;
  }

  public static ProgramDTO getProgramByTitle(ProgramService programService, String domainTitle) {
    if (domainTitle == null || domainTitle.isEmpty()) {
      return null;
    }
    return programService.getProgramByTitle(domainTitle);
  }

  public static RuleDTO getRuleById(RuleService ruleService, long ruleId) throws IllegalArgumentException {
    if (ruleId == 0) {
      return null;
    }
    return ruleService.findRuleById(ruleId);
  }

  public static RuleDTO getRuleByTitle(RuleService ruleService, String title) {
    return StringUtils.isBlank(title) ? null : ruleService.findRuleByTitle("def_" + title);
  }

  public static boolean isRuleManager(ProgramService programService, RuleDTO rule, String username) {
    ProgramDTO program = rule.getProgram();
    if (program == null) {
      return false;
    } else {
      return programService.isProgramOwner(program.getId(), getUserAclIdentity(username));
    }
  }

  public static List<UserInfo> toUserInfo(Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return ids.stream()
              .distinct()
              .map(id -> {
                try {
                  Identity identity = identityManager.getIdentity(String.valueOf(id));
                  if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
                    return toUserInfo(identity);
                  }
                } catch (Exception e) {
                  LOG.warn("Error when getting Identity with id {}. Ignore retrieving identity information", id, e);
                }
                return null;
              })
              .filter(Objects::nonNull)
              .toList();
  }

  public static List<UserInfo> getProgramOwnersByIds(Set<Long> ids, long spaceId) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return ids.stream()
              .distinct()
              .map(id -> {
                try {
                  Identity identity = identityManager.getIdentity(String.valueOf(id));
                  if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
                    UserInfo userInfo = toUserInfo(identity);
                    userInfo.setDomainOwner(isProgramOwner(spaceId, ids, identity));
                    return userInfo;
                  }
                } catch (Exception e) {
                  LOG.warn("Error when getting domain owner with id {}. Ignore retrieving identity information", id, e);
                }
                return null;
              })
              .filter(Objects::nonNull)
              .toList();
  }

  public static UserInfo toUserInfo(ProgramService programService, long domainId, String username) {
    ProgramDTO domain = null;
    if (domainId > 0) {
      domain = programService.getProgramById(domainId);
    }
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    UserInfo userInfo = new UserInfo();
    userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    userInfo.setFullName(identity.getProfile().getFullName());
    userInfo.setRemoteId(identity.getRemoteId());
    userInfo.setId(identity.getId());
    if (domain != null) {
      long spaceId = domain.getAudienceId();
      SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
      Space space = spaceService.getSpaceById(String.valueOf(spaceId));
      if (space != null) {
        boolean isSuperManager = spaceService.isSuperManager(username);
        boolean isManager = isSuperManager || spaceService.isManager(space, username);
        boolean isMember = isManager || spaceService.isMember(space, username);
        boolean isRedactor = isManager || spaceService.isRedactor(space, username);
        userInfo.setManager(isManager);
        userInfo.setMember(isMember);
        userInfo.setRedactor(isRedactor);
        userInfo.setCanAnnounce(canAnnounce(space.getId(), username));
        userInfo.setCanEdit(isProgramOwner(spaceId, domain.getOwners(), identity));
      }
    }
    return userInfo;
  }

  public static UserInfo toUserInfo(Identity identity) {
    UserInfo userInfo = new UserInfo();
    userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    userInfo.setFullName(identity.getProfile().getFullName());
    userInfo.setRemoteId(identity.getRemoteId());
    userInfo.setId(identity.getId());
    return userInfo;
  }

  public static int countAnnouncementsByRuleIdAndEarnerType(AnnouncementService announcementService,
                                                            long ruleId,
                                                            IdentityType earnerType) {
    try {
      return announcementService.countAnnouncements(ruleId, earnerType);
    } catch (ObjectNotFoundException e) {
      return 0;
    }
  }

  public static String getSpaceFromObjectID(String objectId) {
    if (StringUtils.isBlank(objectId) || !objectId.contains("/portal/g/:spaces:")) {
      return null;
    }
    String groupID = objectId.substring(objectId.indexOf(":")).replace(":", "/");
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceByGroupId(groupID);
    return space != null ? space.getDisplayName() : null;
  }

  public static String getI18NMessage(Locale userLocale, String messageKey) {
    if (userLocale == null) {
      userLocale = Locale.ENGLISH;
    }
    try {
      ResourceBundleService resourceBundleService = CommonsUtils.getService(ResourceBundleService.class);
      ResourceBundle resourceBundle =
                                    resourceBundleService.getResourceBundle(resourceBundleService.getSharedResourceBundleNames(),
                                                                            userLocale);
      if (resourceBundle != null && messageKey != null && resourceBundle.containsKey(messageKey)) {
        return resourceBundle.getString(messageKey);
      }
    } catch (Exception e) {
      LOG.debug("Error retrieving resource bundle key {}", messageKey, e);
    }
    return null;
  }

  public static String escapeIllegalCharacterInMessage(String message) {
    if (message == null) {
      return null;
    }
    message = message.replaceAll("<[^>]+>", "");
    message = StringEscapeUtils.unescapeHtml(message);
    for (char c : ILLEGAL_MESSAGE_CHARACTERS) {
      message = message.replace(c, ' ');
    }
    return message;
  }

  public static void buildActivityParams(ExoSocialActivity activity, Map<String, ?> templateParams) {
    Map<String, String> currentTemplateParams =
                                              activity.getTemplateParams() == null ? new HashMap<>()
                                                                                   : new HashMap<>(activity.getTemplateParams());
    if (templateParams != null) {
      templateParams.forEach((name, value) -> currentTemplateParams.put(name, (String) value));
    }
    currentTemplateParams.entrySet()
                         .removeIf(entry -> entry != null
                             && (StringUtils.isBlank(entry.getValue()) || StringUtils.equals(entry.getValue(), "-")));
    activity.setTemplateParams(currentTemplateParams);
  }

  public static boolean isProgramOwner(long spaceId, Set<Long> ownerIds, Identity userIdentity) {
    String username = userIdentity.getRemoteId();
    if (isSpaceManager(spaceId, username)) {
      return true;
    }
    if (isSpaceMember(spaceId, username)
        && ownerIds != null
        && ownerIds.contains(Long.parseLong(userIdentity.getId()))) {
      return true;
    }
    return isRewardingManager(username);
  }

  public static String buildAttachmentUrl(String domainId, Long lastModifiedDate, String type, boolean isDefault) {
    if (Long.valueOf(domainId) == 0) {
      return null;
    }

    if (isDefault) {
      domainId = DEFAULT_IMAGE_REMOTE_ID;
      lastModifiedDate = DEFAULT_COVER_LAST_MODIFIED;
    }

    String token = generateAttachmentToken(domainId, type, lastModifiedDate);
    if (org.apache.commons.lang.StringUtils.isNotBlank(token)) {
      try {
        token = URLEncoder.encode(token, "UTF8");
      } catch (UnsupportedEncodingException e) {
        LOG.warn("Error encoding token", e);
        token = org.apache.commons.lang.StringUtils.EMPTY;
      }
    }

    return new StringBuilder(getBaseURLProgramRest()).append("/")
                                                     .append(domainId)
                                                     .append("/")
                                                     .append(type)
                                                     .append("?lastModified=")
                                                     .append(lastModifiedDate)
                                                     .append("&r=")
                                                     .append(token)
                                                     .toString();

  }

  public static String generateAttachmentToken(String domainId, String attachmentType, Long lastModifiedDate) {
    String token = null;
    CodecInitializer codecInitializer = ExoContainerContext.getService(CodecInitializer.class);
    if (codecInitializer == null) {
      LOG.debug("Can't find an instance of CodecInitializer, an empty token will be generated");
      token = org.apache.commons.lang.StringUtils.EMPTY;
    } else {
      try {
        String tokenPlain = attachmentType + ":" + domainId + ":" + lastModifiedDate;
        token = codecInitializer.getCodec().encode(tokenPlain);
      } catch (TokenServiceInitializationException e) {
        LOG.warn("Error generating token of {} for domain {}. An empty token will be used", attachmentType, domainId, e);
        token = org.apache.commons.lang.StringUtils.EMPTY;
      }
    }
    return token;
  }

  public static boolean isAttachmentTokenValid(String token, String domainId, String attachmentType, Long lastModifiedDate) {
    if (StringUtils.isBlank(token)) {
      LOG.warn("An empty token is used for {} for domain {}", attachmentType, domainId);
      return false;
    }
    String validToken = generateAttachmentToken(domainId, attachmentType, lastModifiedDate);
    return StringUtils.equals(validToken, token);
  }

  public static String getBaseURLProgramRest() {
    return "/" + PortalContainer.getCurrentPortalContainerName() + "/" + PortalContainer.getCurrentRestContextName()
        + BASE_URL_PROGRAMS_REST_API;
  }

  public static void broadcastEvent(ListenerService listenerService, String eventName, Object source, Object data) {
    try {
      listenerService.broadcast(eventName, source, data);
    } catch (Exception e) {
      LOG.warn("Error broadcasting event '" + eventName + "' using source '" + source + "' and data " + data, e);
    }
  }

  public static boolean isRewardingManager(String username) {
    org.exoplatform.services.security.Identity aclIdentity = getUserAclIdentity(username);
    return aclIdentity != null && (aclIdentity.isMemberOf(REWARDING_GROUP) || aclIdentity.isMemberOf(ADMINS_GROUP));
  }

  public static org.exoplatform.services.security.Identity getUserAclIdentity(String username) {
    IdentityRegistry identityRegistry = ExoContainerContext.getService(IdentityRegistry.class);
    org.exoplatform.services.security.Identity aclIdentity = identityRegistry.getIdentity(username);
    if (aclIdentity == null) {
      Authenticator authenticator = CommonsUtils.getService(Authenticator.class);
      try {
        aclIdentity = authenticator.createIdentity(username);
      } catch (Exception e) {
        LOG.warn("Can't check user ACL admin {} roles to determine if it's program manager", username, e);
      }
    }
    return aclIdentity;
  }

  public static boolean isSpaceManager(long spaceId, String userId) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    if (spaceService.isSuperManager(userId)) {
      return true;
    }
    Space space = getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      return false;
    }
    return spaceService.isManager(space, userId);
  }

  public static boolean isSpaceMember(long spaceId, String userId) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      return false;
    }
    return spaceService.isMember(space, userId);
  }

  public static boolean isUserMemberOfGroupOrUser(String username, String permissionExpression) {
    if (StringUtils.isBlank(permissionExpression)) {
      throw new IllegalArgumentException("Permission expression is mandatory");
    }
    if (StringUtils.isBlank(username)) {
      return false;
    }
    org.exoplatform.services.security.Identity identity = getUserAclIdentity(username);
    if (identity == null) {
      return false;
    }
    MembershipEntry membership = null;
    if (permissionExpression.contains(":")) {
      String[] permissionExpressionParts = permissionExpression.split(":");
      membership = new MembershipEntry(permissionExpressionParts[1], permissionExpressionParts[0]);
    } else if (permissionExpression.contains("/")) {
      membership = new MembershipEntry(permissionExpression, MembershipEntry.ANY_TYPE);
    } else {
      return StringUtils.equals(username, permissionExpression);
    }
    return identity.isMemberOf(membership);
  }

  public static void addDomainStatisticParameters(IdentityManager identityManager,
                                                  SpaceService spaceService,
                                                  ProgramDTO domain,
                                                  StatisticData statisticData,
                                                  String username) {
    if (domain == null) {
      return;
    }
    statisticData.addParameter(STATISTICS_PROGRAM_ID_PARAM, domain.getId());
    statisticData.addParameter(STATISTICS_PROGRAM_TITLE_PARAM, domain.getTitle());
    statisticData.addParameter(STATISTICS_PROGRAM_BUDGET_PARAM, domain.getBudget());
    statisticData.addParameter(STATISTICS_PROGRAM_TYPE_PARAM, domain.getType());
    statisticData.addParameter(STATISTICS_PROGRAM_COVERFILEID_PARAM, domain.getCoverFileId());
    statisticData.addParameter(STATISTICS_PROGRAM_OWNERS_PARAM, domain.getOwners());
    if (domain.getAudienceId() > 0) {
      Space space = spaceService.getSpaceById(String.valueOf(domain.getAudienceId()));
      if (space != null) {
        addSpaceStatistics(statisticData, space);
      }
    }
    if (StringUtils.isNotBlank(username)) {
      Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
      if (userIdentity != null) {
        statisticData.setUserId(Long.parseLong(userIdentity.getId()));
      }
    }
  }

  public static void addRuleStatisticParameters(IdentityManager identityManager,
                                                SpaceService spaceService,
                                                RuleDTO rule,
                                                StatisticData statisticData,
                                                String username) {
    if (rule == null) {
      return;
    }
    statisticData.addParameter(STATISTICS_RULE_ID_PARAM, rule.getId());
    statisticData.addParameter(STATISTICS_RULE_TITLE_PARAM, rule.getTitle());
    statisticData.addParameter(STATISTICS_RULE_DESCRIPTION_PARAM, rule.getDescription());
    statisticData.addParameter(STATISTICS_RULE_EVENT_PARAM, rule.getEvent());
    statisticData.addParameter(STATISTICS_RULE_SCORE_PARAM, rule.getScore());
    statisticData.addParameter(STATISTICS_RULE_TYPE_PARAM, rule.getType());

    addDomainStatisticParameters(identityManager, spaceService, rule.getProgram(), statisticData, username);
  }

  public static void addAnnouncementStatisticParameters(IdentityManager identityManager,
                                                        SpaceService spaceService,
                                                        RuleDTO rule,
                                                        Announcement announcement,
                                                        StatisticData statisticData,
                                                        String username) {
    if (rule == null || announcement == null) {
      return;
    }
    statisticData.addParameter(STATISTICS_ANNOUNCE_ID_PARAM, announcement.getId());
    statisticData.addParameter(STATISTICS_ANNOUNCE_ACTIVITY_PARAM, announcement.getActivityId());
    statisticData.addParameter(STATISTICS_ANNOUNCE_ASSIGNEE_PARAM, announcement.getAssignee());
    statisticData.addParameter(STATISTICS_ANNOUNCE_COMMENT_PARAM, announcement.getComment());

    addRuleStatisticParameters(identityManager, spaceService, rule, statisticData, username);
  }

}

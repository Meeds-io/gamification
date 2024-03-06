package io.meeds.gamification.utils;

import static org.exoplatform.analytics.utils.AnalyticsUtils.addSpaceStatistics;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.commons.api.notification.model.ArgumentLiteral;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.entity.IdentityEntity;
import org.exoplatform.web.security.codec.CodecInitializer;
import org.exoplatform.web.security.security.TokenServiceInitializationException;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.EventDTO;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.portal.security.constant.UserRegistrationType;
import io.meeds.portal.security.service.SecuritySettingService;

import org.exoplatform.ws.frameworks.json.JsonGenerator;
import org.exoplatform.ws.frameworks.json.impl.*;

@SuppressWarnings("deprecation")
public class Utils {

  public static final String                        ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM = "announcementDescription";

  public static final String                        ANNOUNCEMENT_ID_TEMPLATE_PARAM          = "announcementId";

  public static final String                        ANNOUNCEMENT_ACTIVITY_EVENT             = "challenge.announcement.activity";

  public static final String                        REALIZATION_STATUS_TEMPLATE_PARAM       = "realizationStatus";

  public static final long                          DEFAULT_LAST_MODIFIED                   = System.currentTimeMillis();

  public static final DateTimeFormatter             RFC_3339_FORMATTER                      =
                                                                       DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                                        .withResolverStyle(ResolverStyle.LENIENT);

  public static final DateTimeFormatter             SIMPLE_DATE_FORMATTER                   =
                                                                          DateTimeFormatter.ofPattern("yyyy-MM-dd['T00:00:00']")
                                                                                           .withResolverStyle(ResolverStyle.LENIENT);

  private static final char[]                       ILLEGAL_MESSAGE_CHARACTERS              = { ',', ';', '\n' };

  public static final String                        STATISTICS_CREATE_PROGRAM_OPERATION     = "createProgram";

  public static final String                        STATISTICS_UPDATE_PROGRAM_OPERATION     = "updateProgram";

  public static final String                        STATISTICS_DELETE_PROGRAM_OPERATION     = "deleteProgram";

  public static final String                        STATISTICS_CREATE_RULE_OPERATION        = "createRule";

  public static final String                        STATISTICS_UPDATE_RULE_OPERATION        = "updateRule";

  public static final String                        STATISTICS_DELETE_RULE_OPERATION        = "deleteRule";

  public static final String                        STATISTICS_CREATE_REALIZATION_OPERATION = "createRealization";

  public static final String                        STATISTICS_UPDATE_REALIZATION_OPERATION = "updateRealization";

  public static final String                        STATISTICS_CANCEL_REALIZATION_OPERATION = "cancelRealization";

  public static final String                        STATISTICS_ENABLE_PROGRAM_OPERATION     = "enableProgram";

  public static final String                        STATISTICS_DISABLE_PROGRAM_OPERATION    = "disableProgram";

  public static final String                        STATISTICS_CREATE_ANNOUNCE_OPERATION    = "createAnnouncement";

  public static final String                        STATISTICS_UPDATE_ANNOUNCE_OPERATION    = "updateAnnouncement";

  public static final String                        STATISTICS_PROGRAM_ID_PARAM             = "programId";

  public static final String                        STATISTICS_PROGRAM_TITLE_PARAM          = "programTitle";

  public static final String                        STATISTICS_PROGRAM_BUDGET_PARAM         = "programBudget";

  public static final String                        STATISTICS_PROGRAM_TYPE_PARAM           = "programType";

  public static final String                        STATISTICS_PROGRAM_COVER_FILEID_PARAM   = "programCoverFileId";

  public static final String                        STATISTICS_PROGRAM_AVATAR_FILEID_PARAM  = "programAvatarFileId";

  public static final String                        STATISTICS_PROGRAM_OWNERS_PARAM         = "programOwners";

  public static final String                        STATISTICS_RULE_ID_PARAM                = "ruleId";

  public static final String                        STATISTICS_RULE_ECTIVITY_ID_PARAM       = "ruleActivityId";

  public static final String                        STATISTICS_RULE_TITLE_PARAM             = "ruleTitle";

  public static final String                        STATISTICS_RULE_DESCRIPTION_PARAM       = "ruleDescription";

  public static final String                        STATISTICS_RULE_SCORE_PARAM             = "ruleBudget";

  public static final String                        STATISTICS_RULE_TYPE_PARAM              = "ruleType";

  public static final String                        STATISTICS_RULE_COVERFILEID_PARAM       = "ruleCoverFileId";

  public static final String                        STATISTICS_RULE_SUBMODULE               = "rule";

  public static final String                        STATISTICS_REALIZATION_SUBMODULE        = "realization";

  public static final String                        STATISTICS_ANNOUNCEMENT_SUBMODULE       = "announcement";

  public static final String                        STATISTICS_ANNOUNCE_ID_PARAM            = "announcementId";

  public static final String                        STATISTICS_ANNOUNCE_ACTIVITY_PARAM      = "announcementActivityId";

  public static final String                        STATISTICS_ANNOUNCE_ASSIGNEE_PARAM      = "announcementAssignee";

  public static final String                        STATISTICS_ANNOUNCE_COMMENT_PARAM       = "announcementComment";

  public static final String                        STATISTICS_EARNER_TYPE                  = "realizationEarnerType";

  public static final String                        STATISTICS_EARNER_ID_PARAM              = "realizationEarnerId";

  public static final String                        STATISTICS_ACTIVITY_PARAM               = "realizationActivityId";

  public static final String                        STATISTICS_RECEIVED_ID                  = "realizationSenderId";

  public static final String                        STATISTICS_STATUS                       = "realizationStatus";

  public static final String                        STATISTICS_PROGRAM_SUBMODULE            = "program";

  public static final String                        STATISTICS_GAMIFICATION_MODULE          = "gamification";

  public static final String                        STATISTICS_EVENT_ID_PARAM               = "gamificationEventId";

  public static final String                        STATISTICS_EVENT_TYPE_PARAM             = "gamificationEventType";

  public static final String                        STATISTICS_EVENT_TRIGGER_PARAM          = "gamificationEventTrigger";

  public static final String                        STATISTICS_EVENT_TITLE_PARAM            = "gamificationEventTitle";

  public static final String                        POST_CREATE_RULE_EVENT                  = "rule.created";

  public static final String                        POST_UPDATE_RULE_EVENT                  = "rule.updated";

  public static final String                        POST_DELETE_RULE_EVENT                  = "rule.deleted";

  public static final String                        POST_PUBLISH_RULE_EVENT                 = "rule.published";

  public static final String                        POST_CREATE_ANNOUNCEMENT_EVENT          = "announcement.created";

  public static final String                        POST_UPDATE_ANNOUNCEMENT_EVENT          = "announcement.updated";

  public static final String                        POST_CANCEL_ANNOUNCEMENT_EVENT          = "announcement.canceled";

  public static final String                        POST_REALIZATION_CREATE_EVENT           = "realization.created";

  public static final String                        POST_REALIZATION_UPDATE_EVENT           = "realization.updated";

  public static final String                        POST_REALIZATION_CANCEL_EVENT           = "realization.canceled";

  public static final String                        RULE_ACTIVITY_PARAM_RULE_ID             = "ruleId";

  public static final String                        RULE_ACTIVITY_PARAM_RULE_TITLE          = "ruleTitle";

  public static final String                        RULE_ACTIVITY_PARAM_RULE_DESCRIPTION    = "ruleTitle";

  public static final String                        RULE_ACTIVITY_PARAM_RULE_SCORE          = "ruleScore";

  public static final String                        RULE_ACTIVITY_OBJECT_TYPE               = "rule";

  public static final String                        RULE_ACTIVITY_TYPE                      = "gamificationRuleActivity";

  /**
   * @deprecated this was used when the announcement was of type 'Activity'
   */
  @Deprecated(forRemoval = true, since = "1.5.0")
  public static final String                        ANNOUNCEMENT_ACTIVITY_TYPE              = "challenges-announcement";

  public static final String                        ANNOUNCEMENT_COMMENT_TYPE               = "gamificationActionAnnouncement";

  public static final String                        SYSTEM_USERNAME                         = "SYSTEM";

  public static final String                        BASE_URL_PROGRAMS_REST_API              = "/gamification/programs";

  public static final String                        DEFAULT_COVER_REMOTE_ID                 = "default-cover";

  public static final String                        DEFAULT_AVATAR_REMOTE_ID                = "default-avatar";

  public static final String                        ATTACHMENT_COVER_TYPE                   = "cover";

  public static final String                        ATTACHMENT_AVATAR_TYPE                  = "avatar";

  public static final String                        REWARDING_GROUP                         = "/platform/rewarding";

  public static final String                        ADMINS_GROUP                            = "/platform/administrators";

  public static final String                        BLACK_LIST_GROUP                        = "/leaderboard-blacklist-users";

  public static final String                        IDENTITIES_REST_PATH                    = "/v1/social/identities";                // NOSONAR

  public static final String                        IDENTITIES_EXPAND                       = "all";

  public static final String                        RULE_PUBLISHED_NOTIFICATION_ID          =
                                                                                   "GamificationActionPublishedNotification";

  public static final String                        RULE_ANNOUNCED_NOTIFICATION_ID          =
                                                                                   "GamificationActionAnnouncedNotification";

  public static final String                        RULE_ID_NOTIFICATION_PARAM              = "RULE_ID";

  public static final String                        RULE_PUBLISHER_NOTIFICATION_PARAM       = "PUBLISHER";

  public static final String                        ANNOUNCEMENT_ID_NOTIFICATION_PARAM      = "ANNOUNCEMENT_ID";

  public static final ArgumentLiteral<RuleDTO>      RULE_NOTIFICATION_PARAMETER             =
                                                                                new ArgumentLiteral<>(RuleDTO.class, "rule");

  public static final ArgumentLiteral<String>       RULE_PUBLISHER_NOTIFICATION_PARAMETER   = new ArgumentLiteral<>(String.class,
                                                                                                                    "publisher");

  public static final ArgumentLiteral<Announcement> ANNOUNCEMENT_NOTIFICATION_PARAMETER     =
                                                                                        new ArgumentLiteral<>(Announcement.class,
                                                                                                              "announcement");

  public static final JsonGenerator                 JSON_GENERATOR                          = new JsonGeneratorImpl();

  private static final Log                          LOG                                     = ExoLogger.getLogger(Utils.class);

  private Utils() { // NOSONAR
  }

  public static Identity getIdentityByTypeAndId(String type, String name) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getOrCreateIdentity(type, name);
  }

  public static Identity getUserIdentity(String username) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getOrCreateUserIdentity(username);
  }

  public static String getUserRemoteId(String identityId) {
    Identity identity = getIdentityById(identityId);
    return identity != null ? identity.getRemoteId() : null;
  }

  public static String getUserFullName(String identityId) {
    Identity identity = getIdentityById(identityId);
    return identity != null && identity.getProfile() != null ? identity.getProfile().getFullName() : null;
  }

  public static Identity getIdentityById(String identityId) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getIdentity(identityId);
  }

  public static IdentityEntity getIdentityEntity(IdentityManager identityManager, long identityId) {
    Identity identity = identityManager.getIdentity(String.valueOf(identityId));
    if (identity == null) {
      return null;
    }
    return EntityBuilder.buildEntityIdentity(identity, IDENTITIES_REST_PATH, IDENTITIES_EXPAND);
  }

  public static final long getCurrentUserIdentityId() {
    String username = getCurrentUser();
    if (StringUtils.isBlank(username)) {
      return 0;
    } else {
      return getUserIdentityId(username);
    }
  }

  public static long getUserIdentityId(String username) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    return identity == null ? 0l : Long.parseLong(identity.getId());
  }
  
  public static final String getCurrentUser() {
    if (ConversationState.getCurrent() != null && ConversationState.getCurrent().getIdentity() != null) {
      String userId = ConversationState.getCurrent().getIdentity().getUserId();
      return StringUtils.equals(userId, IdentityConstants.ANONIM) ? null : userId;
    }
    return null;
  }

  public static final boolean isAnonymous() {
    return StringUtils.isBlank(getCurrentUser());
  }

  public static final boolean canAccessAnonymousResources() {
    return canAccessAnonymousResources(ExoContainerContext.getService(SecuritySettingService.class));
  }

  public static final boolean canAccessAnonymousResources(SecuritySettingService securitySettingService) {
    return !isAnonymous() || securitySettingService.getRegistrationType() == UserRegistrationType.OPEN;
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
    return spaceService.getSpaceById(spaceId);
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

  public static String escapeIllegalCharacterInMessage(String message) {
    if (message == null) {
      return null;
    }
    message = message.replaceAll("<[^>]+>", "");
    message = StringEscapeUtils.unescapeHtml4(message);
    for (char c : ILLEGAL_MESSAGE_CHARACTERS) {
      message = message.replace(c, ' ');
    }
    return message;
  }

  public static String buildAttachmentUrl(String programId,
                                          Long lastModifiedDate,
                                          String type,
                                          String defaultId,
                                          boolean isDefault) {
    if (Long.valueOf(programId) == 0) {
      return null;
    }

    if (isDefault) {
      programId = defaultId;
      lastModifiedDate = DEFAULT_LAST_MODIFIED;
    }

    String token = generateAttachmentToken(programId, type, lastModifiedDate);
    if (org.apache.commons.lang3.StringUtils.isNotBlank(token)) {
      try {
        token = URLEncoder.encode(token, "UTF8");
      } catch (UnsupportedEncodingException e) {
        LOG.warn("Error encoding token", e);
        token = org.apache.commons.lang3.StringUtils.EMPTY;
      }
    }

    return new StringBuilder(getBaseURLProgramRest()).append("/")
                                                     .append(programId)
                                                     .append("/")
                                                     .append(type)
                                                     .append("?lastModified=")
                                                     .append(lastModifiedDate)
                                                     .append("&r=")
                                                     .append(token)
                                                     .toString();

  }

  public static String generateAttachmentToken(String programId, String attachmentType, Long lastModifiedDate) {
    String token = null;
    CodecInitializer codecInitializer = ExoContainerContext.getService(CodecInitializer.class);
    if (codecInitializer == null) {
      LOG.debug("Can't find an instance of CodecInitializer, an empty token will be generated");
      token = org.apache.commons.lang3.StringUtils.EMPTY;
    } else {
      try {
        String tokenPlain = attachmentType + ":" + programId + ":" + lastModifiedDate;
        token = codecInitializer.getCodec().encode(tokenPlain);
      } catch (TokenServiceInitializationException e) {
        LOG.warn("Error generating token of {} for program {}. An empty token will be used", attachmentType, programId, e);
        token = org.apache.commons.lang3.StringUtils.EMPTY;
      }
    }
    return token;
  }

  public static boolean isAttachmentTokenValid(String token, String programId, String attachmentType, Long lastModifiedDate) {
    if (StringUtils.isBlank(token)) {
      LOG.warn("An empty token is used for {} for program {}", attachmentType, programId);
      return false;
    }
    String validToken = generateAttachmentToken(programId, attachmentType, lastModifiedDate);
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
    if (StringUtils.isBlank(username)) {
      return null;
    }
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

  public static void addProgramStatisticParameters(IdentityManager identityManager,
                                                   SpaceService spaceService,
                                                   ProgramDTO program,
                                                   StatisticData statisticData,
                                                   String username) {
    if (program == null) {
      return;
    }
    statisticData.addParameter(STATISTICS_PROGRAM_ID_PARAM, program.getId());
    statisticData.addParameter(STATISTICS_PROGRAM_TITLE_PARAM, program.getTitle());
    statisticData.addParameter(STATISTICS_PROGRAM_BUDGET_PARAM, program.getBudget());
    statisticData.addParameter(STATISTICS_PROGRAM_TYPE_PARAM, program.getType());
    statisticData.addParameter(STATISTICS_PROGRAM_COVER_FILEID_PARAM, program.getCoverFileId());
    statisticData.addParameter(STATISTICS_PROGRAM_AVATAR_FILEID_PARAM, program.getAvatarFileId());
    statisticData.addParameter(STATISTICS_PROGRAM_OWNERS_PARAM, program.getOwnerIds());
    if (program.getSpaceId() > 0) {
      Space space = spaceService.getSpaceById(String.valueOf(program.getSpaceId()));
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
                                                EventDTO event,
                                                StatisticData statisticData,
                                                String username) {
    if (rule == null) {
      return;
    }
    statisticData.addParameter(STATISTICS_RULE_ID_PARAM, rule.getId());
    statisticData.addParameter(STATISTICS_RULE_ECTIVITY_ID_PARAM, rule.getActivityId());
    statisticData.addParameter(STATISTICS_RULE_TITLE_PARAM, rule.getTitle());
    statisticData.addParameter(STATISTICS_RULE_DESCRIPTION_PARAM, rule.getDescription());
    statisticData.addParameter(STATISTICS_RULE_SCORE_PARAM, rule.getScore());
    statisticData.addParameter(STATISTICS_RULE_TYPE_PARAM, rule.getType());
    if (event != null) {
      statisticData.addParameter(STATISTICS_EVENT_ID_PARAM, event.getId());
      statisticData.addParameter(STATISTICS_EVENT_TYPE_PARAM, event.getType());
      statisticData.addParameter(STATISTICS_EVENT_TRIGGER_PARAM, event.getTrigger());
      statisticData.addParameter(STATISTICS_EVENT_TITLE_PARAM, event.getTitle());
      Map<String, String> properties = event.getProperties();
      if (properties != null) {
        properties.forEach((k, v) -> {
          if (!statisticData.getParameters().containsKey(k)) {
            String[] values = StringUtils.split(v, ",");
            statisticData.addParameter(k, Arrays.asList(values));
          }
        });
      }
    }

    addProgramStatisticParameters(identityManager, spaceService, rule.getProgram(), statisticData, username);
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

    addRuleStatisticParameters(identityManager, spaceService, rule, null, statisticData, username);
  }

  public static void addRealizationStatisticParameters(IdentityManager identityManager,
                                                       SpaceService spaceService,
                                                       RuleDTO rule,
                                                       EventDTO ruleEvent,
                                                       RealizationDTO realization,
                                                       StatisticData statisticData) {
    addRuleStatisticParameters(identityManager,
                               spaceService,
                               rule,
                               ruleEvent,
                               statisticData,
                               null);

    if (StringUtils.isNotBlank(realization.getEarnerId())) {
      if ((realization.getEarnerType() == null || IdentityType.USER.name().equalsIgnoreCase(realization.getEarnerType()))
          && StringUtils.isNumeric(realization.getEarnerId())) {
        statisticData.setUserId(Long.parseLong(realization.getEarnerId()));
      }
      statisticData.addParameter(STATISTICS_EARNER_TYPE, realization.getEarnerType());
      statisticData.addParameter(STATISTICS_EARNER_ID_PARAM, realization.getEarnerId());
      statisticData.addParameter(STATISTICS_ACTIVITY_PARAM, realization.getActivityId());
      statisticData.addParameter(STATISTICS_RECEIVED_ID, realization.getReceiver());
      statisticData.addParameter(STATISTICS_STATUS, realization.getStatus());
    }
    if (StringUtils.isNotBlank(realization.getEarnerType())) {
      statisticData.setUserId(Long.parseLong(realization.getEarnerId()));
    }
  }

  public static String removeSpecialCharacters(String content) {
    return Normalizer.normalize(StringEscapeUtils.unescapeHtml4(content), Normalizer.Form.NFD)
                     .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                     .replace("'", "");
  }

  public static List<String> getExpandOptions(String expand) {
    String[] expandFieldsArray = StringUtils.split(expand, ",");
    return expandFieldsArray == null ? Collections.emptyList() : Arrays.asList(expandFieldsArray);
  }

  public static String toJsonString(Object object) {
    try {
      return JSON_GENERATOR.createJsonObject(object).toString();
    } catch (JsonException e) {
      throw new IllegalStateException("Error parsing object to string " + object, e);
    }
  }

  public static <T> T fromJsonString(String value, Class<T> resultClass) {
    try {
      if (StringUtils.isBlank(value)) {
        return null;
      }
      JsonDefaultHandler jsonDefaultHandler = new JsonDefaultHandler();
      new JsonParserImpl().parse(new ByteArrayInputStream(value.getBytes()), jsonDefaultHandler);
      return ObjectBuilder.createObject(resultClass, jsonDefaultHandler.getJsonObject());
    } catch (JsonException e) {
      throw new IllegalStateException("Error creating object from string : " + value, e);
    }
  }
}

package org.exoplatform.addons.gamification.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.*;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
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

  public static final String            ANNOUNCEMENT_ACTIVITY_EVENT = "challenge.announcement.activity";

  public static final long              DEFAULT_COVER_LAST_MODIFIED = System.currentTimeMillis();

  public static final DateTimeFormatter RFC_3339_FORMATTER          =
                                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                            .withResolverStyle(ResolverStyle.LENIENT);

  public static final DateTimeFormatter SIMPLE_DATE_FORMATTER       = DateTimeFormatter.ofPattern("yyyy-MM-dd['T00:00:00']")
                                                                                       .withResolverStyle(ResolverStyle.LENIENT);

  private static final char[]           ILLEGAL_MESSAGE_CHARACTERS  = { ',', ';', '\n' };

  public static final String            POST_CREATE_RULE_EVENT      = "rule.created";

  public static final String            POST_UPDATE_RULE_EVENT      = "rule.updated";

  public static final String            POST_DELETE_RULE_EVENT      = "rule.deleted";

  private static GamificationService    gamificationService;

  private static RuleService            ruleService;

  public static final String            ANNOUNCEMENT_ACTIVITY_TYPE  = "challenges-announcement";

  public static final String            SYSTEM_USERNAME             = "SYSTEM";

  public static final String            BASE_URL_DOMAINS_REST_API   = "/gamification/domains";

  public static final String            DEFAULT_IMAGE_REMOTE_ID     = "default-cover";

  public static final String            TYPE                        = "cover";

  public static final String            REWARDING_GROUP             = "/platform/rewarding";

  public static final String            ADMINS_GROUP                = "/platform/administrators";

  public static final String            BLACK_LIST_GROUP            = "/leaderboard-blacklist-users";

  private static final String           IDENTITIES_REST_PATH        = "/v1/social/identities";                                   // NOSONAR

  private static final String           IDENTITIES_EXPAND           = "all";

  private static final Log              LOG                         = ExoLogger.getLogger(Utils.class);

  private Utils() { // NOSONAR
  }

  public static Identity getIdentityByTypeAndId(String type, String name) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getOrCreateIdentity(type, name);
  }

  public static String getUserRemoteId(String id) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Identity identity = identityManager.getIdentity(id);
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

  public static final String getCurrentUser() {
    if (ConversationState.getCurrent() != null && ConversationState.getCurrent().getIdentity() != null) {
      return ConversationState.getCurrent().getIdentity().getUserId();
    }
    return null;
  }

  @SuppressWarnings("deprecation")
  public static boolean isChallengeManager(Challenge challenge, String username) {
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    long programId = challenge.getProgramId();
    if (programId == 0) {
      if (StringUtils.isBlank(challenge.getProgram())) {// NOSONAR
        return false;
      } else {
        DomainDTO domain = domainService.getDomainByTitle(challenge.getProgram());// NOSONAR
        if (domain == null) {
          return false;
        }
        programId = domain.getId();
      }
    }
    return domainService.isDomainOwner(programId, getUserAclIdentity(username));
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
    Space space = CommonsUtils.getService(SpaceService.class).getSpaceById(spaceId);
    if (space == null) {
      LOG.warn("space with id {} do not exist", spaceId);
      return null;
    }
    return space;
  }

  public static DomainDTO getEnabledDomainByTitle(String domainTitle) {
    if (domainTitle == null || domainTitle.isEmpty()) {
      return null;
    }
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    return domainService.findEnabledDomainByTitle(domainTitle);
  }

  public static DomainDTO getDomainByTitle(String domainTitle) {
    if (domainTitle == null || domainTitle.isEmpty()) {
      return null;
    }
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    return domainService.getDomainByTitle(domainTitle);
  }

  public static DomainDTO getDomainDTOById(long domainId) {
    if (domainId <= 0) {
      return null;
    }
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    return domainService.getDomainById(domainId);
  }

  @SuppressWarnings("deprecation")
  public static DomainDTO getChallengeDomainDTO(Challenge challenge) {
    DomainDTO domain;
    if (challenge.getProgramId() > 0) {
      domain = Utils.getDomainDTOById(challenge.getProgramId());
    } else {
      domain = Utils.getEnabledDomainByTitle(challenge.getProgram());// NOSONAR
                                                                     // kept for
                                                                     // backward
                                                                     // compatibility
    }
    return domain;
  }

  public static DomainEntity getDomainById(long domainId) {
    return DomainMapper.domainDTOToDomainEntity(getDomainDTOById(domainId));
  }

  public static long getRulesTotalScoreByDomain(long domainId) {
    if (domainId <= 0) {
      return 0;
    }
    RuleService ruleService = CommonsUtils.getService(RuleService.class);
    return ruleService.getRulesTotalScoreByDomain(domainId);
  }

  public static RuleDTO getRuleById(long ruleId) throws IllegalArgumentException {
    if (ruleId == 0) {
      return null;
    }
    RuleService ruleService = CommonsUtils.getService(RuleService.class);
    return ruleService.findRuleById(ruleId);
  }

  public static RuleDTO getRuleByTitle(String title) {
    return StringUtils.isBlank(title) ? null : getRuleService().findRuleByTitle("def_" + title);
  }

  public static boolean isRuleManager(RuleDTO rule, String username) {
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    long programId;
    if (StringUtils.isBlank(rule.getArea())) {
      return false;
    } else {
      DomainDTO domain = domainService.getDomainByTitle(rule.getArea());
      if (domain == null) {
        return false;
      }
      programId = domain.getId();
    }
    return domainService.isDomainOwner(programId, getUserAclIdentity(username));
  }

  public static List<UserInfo> getOwners(Challenge challenge) {// NOSONAR
    DomainDTO domain = getChallengeDomainDTO(challenge);
    if (domain == null) {
      return Collections.emptyList();
    } else {
      Set<Long> owners = domain.getOwners() == null ? new HashSet<>() : new HashSet<>(domain.getOwners());
      if (domain.getAudienceId() > 0) {
        SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
        Space space = spaceService.getSpaceById(String.valueOf(domain.getAudienceId()));
        if (space != null) {
          IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
          String[] managers = space.getManagers();
          Arrays.stream(managers).map(manager -> {
            Identity identity = identityManager.getOrCreateUserIdentity(manager);
            return identity == null || identity.isDeleted() || !identity.isEnable() ? null : Long.parseLong(identity.getId());
          }).filter(Objects::nonNull).forEach(owners::add);
        }
      }
      return toUserInfo(owners);
    }
  }

  public static List<UserInfo> toUserInfo(Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    try {
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      return ids.stream().distinct().map(id -> {
        Identity identity = identityManager.getIdentity(String.valueOf(id));
        if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
          return toUserInfo(identity);
        }
        return null;
      }).filter(Objects::nonNull).toList();
    } catch (Exception e) {
      LOG.error("Error when getting challenge managers {}", e);
      return Collections.emptyList();
    }
  }

  public static List<UserInfo> getDomainOwnersByIds(Set<Long> ids, long spaceId) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    try {
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      return ids.stream().distinct().map(id -> {
        Identity identity = identityManager.getIdentity(String.valueOf(id));
        if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
          UserInfo userInfo = toUserInfo(identity);
          userInfo.setDomainOwner(isProgramOwner(spaceId, ids, identity));
          return userInfo;
        }
        return null;
      }).filter(Objects::nonNull).toList();
    } catch (Exception e) {
      LOG.error("Error when getting challenge managers {}", e);
      return Collections.emptyList();
    }
  }

  public static UserInfo toUserInfo(Challenge challenge, Identity identity) {
    DomainDTO domain = getChallengeDomainDTO(challenge);
    return toUserInfo(domain == null ? 0 : domain.getId(), identity.getRemoteId());
  }

  public static UserInfo toUserInfo(long domainId, String username) {
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    DomainDTO domain = domainService.getDomainById(domainId);

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
        userInfo.setCanAnnounce(Utils.canAnnounce(space.getId(), username));
        userInfo.setCanEdit(Utils.isProgramOwner(spaceId, domain.getOwners(), identity));
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

  public static Long countAnnouncementsByChallenge(Long challengeId) {
    AnnouncementService announcementService = CommonsUtils.getService(AnnouncementService.class);
    try {
      return announcementService.countAllAnnouncementsByChallenge(challengeId);
    } catch (Exception e) {
      // NOSONAR
      return 0L;
    }
  }

  public static List<Announcement> findAllAnnouncementByChallenge(Long challengeId,
                                                                  int offset,
                                                                  int limit,
                                                                  IdentityType earnerType) throws IllegalAccessException {
    AnnouncementService announcementService = CommonsUtils.getService(AnnouncementService.class);
    return announcementService.findAllAnnouncementByChallenge(challengeId, offset, limit, PeriodType.ALL, earnerType);
  }


  public static Long countAnnouncementByChallengeAndEarnerType(long challengeId, IdentityType earnerType) {
    AnnouncementService announcementService = CommonsUtils.getService(AnnouncementService.class);
    try {
      return announcementService.countAnnouncementsByChallengeAndEarnerType(challengeId, earnerType);
    } catch (ObjectNotFoundException e) {
      return 0L;
    }
  }

  public static GamificationService getGamificationService() {
    if (gamificationService == null) {
      gamificationService = CommonsUtils.getService(GamificationService.class);
    }
    return gamificationService;
  }

  public static RuleService getRuleService() {
    if (ruleService == null) {
      ruleService = CommonsUtils.getService(RuleService.class);
    }
    return ruleService;
  }

  public static Long getUserGlobalScore(String earnerId) {
    return StringUtils.isBlank(earnerId) ? 0L : getGamificationService().computeTotalScore(earnerId);
  }

  public static String getSpaceFromObjectID(String objectID) {
    if (StringUtils.isBlank(objectID) || !objectID.contains("/portal/g/:spaces:")) {
      return null;
    }
    String groupID = objectID.substring(objectID.indexOf(":")).replace(":", "/");
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceByGroupId(groupID);
    return space != null ? space.getDisplayName() : null;
  }

  public static String getI18NMessage(Locale userLocale, String messageKey) {
    ResourceBundleService resourceBundleService = CommonsUtils.getService(ResourceBundleService.class);
    if (userLocale == null) {
      userLocale = Locale.ENGLISH;
    }
    try {
      ResourceBundle resourceBundle = resourceBundleService.getResourceBundle(resourceBundleService.getSharedResourceBundleNames(),
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
    return isSuperManager(username);
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

    return new StringBuilder(getBaseURLDomainRest()).append("/")
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

  public static String getBaseURLDomainRest() {
    return "/" + PortalContainer.getCurrentPortalContainerName() + "/" + PortalContainer.getCurrentRestContextName()
        + BASE_URL_DOMAINS_REST_API;
  }

  public static void broadcastEvent(ListenerService listenerService, String eventName, Object source, Object data) {
    try {
      listenerService.broadcast(eventName, source, data);
    } catch (Exception e) {
      LOG.warn("Error broadcasting event '" + eventName + "' using source '" + source + "' and data " + data, e);
    }
  }

  public static List<String> getPermissions(String permission) {
    List<String> result = new ArrayList<>();
    if (permission != null) {
      if (permission.contains(",")) {
        String[] groups = permission.split(",");
        for (String group : groups) {
          result.add(group.trim());
        }
      } else {
        result.add(permission);
      }
    }
    return result;
  }

  public static boolean isSuperManager(String username) {
    org.exoplatform.services.security.Identity aclIdentity = getUserAclIdentity(username);
    return aclIdentity != null && (aclIdentity.isMemberOf(REWARDING_GROUP) || aclIdentity.isMemberOf(ADMINS_GROUP));
  }

  public static org.exoplatform.services.security.Identity getUserAclIdentity(String username) {
    IdentityRegistry identityRegistry = CommonsUtils.getService(IdentityRegistry.class);
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

}

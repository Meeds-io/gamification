package org.exoplatform.addons.gamification.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.IdentityRegistry;
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

  public static boolean isChallengeManager(List<Long> managersId, long spaceId, String username) {
    org.exoplatform.services.security.Identity currentUser = ConversationState.getCurrent().getIdentity();
    ChallengeService challengeService = CommonsUtils.getService(ChallengeService.class);
    if (currentUser != null && challengeService.canAddChallenge(currentUser)) {
      return true;
    }
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);

    Identity identity = getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, username);
    if (identity != null) {
      return managersId.stream().anyMatch(id -> id == Long.parseLong(identity.getId())) || isSpaceManager(spaceId, username)
          || spaceService.isSuperManager(username);
    }
    return false;
  }

  public static final boolean canAnnounce(String spaceId, String username) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(spaceId);
    if (space == null || StringUtils.isBlank(username)) {
      return false;
    } else {
      IdentityRegistry identityRegistry = CommonsUtils.getService(IdentityRegistry.class);
      org.exoplatform.services.security.Identity identity = identityRegistry.getIdentity(username);
      return spaceService.canRedactOnSpace(space, identity);
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
      domain = Utils.getEnabledDomainByTitle(challenge.getProgram());// NOSONAR kept for backward compatibility
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

  public static List<UserInfo> getManagersByIds(List<Long> ids) {
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
      }).filter(Objects::nonNull).collect(Collectors.toList());
    } catch (Exception e) {
      LOG.error("Error when getting challenge managers {}", e);
      return Collections.emptyList();
    }
  }

  public static List<UserInfo> getDomainOwnersByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    try {
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      return ids.stream().distinct().map(id -> {
        Identity identity = identityManager.getIdentity(String.valueOf(id));
        if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
          UserInfo userInfo = toUserInfo(identity);
          userInfo.setDomainOwner(true);
          return userInfo;
        }
        return null;
      }).filter(Objects::nonNull).collect(Collectors.toList());
    } catch (Exception e) {
      LOG.error("Error when getting challenge managers {}", e);
      return Collections.emptyList();
    }
  }

  public static UserInfo toUserInfo(Identity identity, Space space, List<Long> managerIds) {
    UserInfo userInfo = new UserInfo();
    userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    userInfo.setFullName(identity.getProfile().getFullName());
    userInfo.setRemoteId(identity.getRemoteId());
    userInfo.setId(identity.getId());
    String username = identity.getRemoteId();
    if (space != null) {
      SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
      boolean isSuperManager = spaceService.isSuperManager(username);
      boolean isManager = isSuperManager || spaceService.isManager(space, username);
      boolean isMember = isManager || spaceService.isMember(space, username);
      boolean isRedactor = isManager || spaceService.isRedactor(space, username);
      userInfo.setManager(isManager);
      userInfo.setMember(isMember);
      userInfo.setRedactor(isRedactor);
      userInfo.setCanAnnounce(Utils.canAnnounce(space.getId(), username));
      userInfo.setCanEdit(Utils.isChallengeManager(managerIds, Long.parseLong(space.getId()), username));
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

  public static UserInfo toUserInfo(String username, Set<Long> domainsOwners, long spaceId) {
    if (StringUtils.isBlank(username)) {
      return null;
    }
    Identity identity = getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, username);
    if (identity == null) {
      return null;
    }
    UserInfo userInfo = new UserInfo();
    userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    userInfo.setFullName(identity.getProfile().getFullName());
    userInfo.setRemoteId(identity.getRemoteId());
    userInfo.setId(identity.getId());
    userInfo.setDomainOwner(isProgramOwner(domainsOwners, Long.parseLong(identity.getId())) || isSpaceManager(spaceId, username));
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
      return resourceBundleService.getResourceBundle(resourceBundleService.getSharedResourceBundleNames(), userLocale)
                                  .getString(messageKey);
    } catch (Exception e) {
      LOG.warn("Resource bundle key " + messageKey + " not found");
      return null;
    }
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
    currentTemplateParams.entrySet().removeIf(entry -> entry != null && (StringUtils.isBlank(entry.getValue()) || StringUtils.equals(entry.getValue(), "-")));
    activity.setTemplateParams(currentTemplateParams);
  }

  public static boolean isProgramOwner(Set<Long> ownerIds, long userId) {
    return ownerIds != null && !ownerIds.isEmpty() && ownerIds.stream().anyMatch(id -> id == userId);
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

  public static boolean isAdministrator(String username) {
    if (StringUtils.isBlank(username)) {
      throw new IllegalArgumentException("Username is mandatory");
    }
    org.exoplatform.services.security.Identity identity = CommonsUtils.getService(IdentityRegistry.class).getIdentity(username);
    if (identity != null) {
      return identity.isMemberOf(REWARDING_GROUP) || identity.isMemberOf(ADMINS_GROUP);
    }
    return false;
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

  public static boolean isSpaceManager(long spaceId,
                                       String userId) {
    Space space = getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      return false;
    }
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    return spaceService.isManager(space, userId) || spaceService.isSuperManager(userId);
  }
}

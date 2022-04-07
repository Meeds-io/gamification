package org.exoplatform.addons.gamification.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.portal.Constants;
import org.exoplatform.portal.localization.LocaleContextInfoUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.resources.LocaleContextInfo;
import org.exoplatform.services.resources.LocalePolicy;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import javax.servlet.http.HttpServletRequest;

public class Utils {

  public static final String            ANNOUNCEMENT_ACTIVITY_EVENT = "challenge.announcement.activity";

  private static final Log              LOG                         = ExoLogger.getLogger(Utils.class);

  public static final DateTimeFormatter RFC_3339_FORMATTER          =
                                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                            .withResolverStyle(ResolverStyle.LENIENT);

  public static final DateTimeFormatter SIMPLE_DATE_FORMATTER       = DateTimeFormatter.ofPattern("yyyy-MM-dd['T00:00:00']")
                                                                                       .withResolverStyle(ResolverStyle.LENIENT);

  private static final char[]           ILLEGAL_MESSAGE_CHARACTERS  = { ',', ';', '\n' };

  private static GamificationService    gamificationService;

  private static RuleService            ruleService;

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

  public static final String getCurrentUser() {
    if (ConversationState.getCurrent() != null && ConversationState.getCurrent().getIdentity() != null) {
      return ConversationState.getCurrent().getIdentity().getUserId();
    }
    return null;
  }

  public static final boolean canEditChallenge(List<Long> managersId, String spaceId) {
    String userId = getCurrentUser();
    Identity identity = getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, userId);
    Space space = getSpaceById(spaceId);
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Boolean isChallengeOwner = false;
    Boolean isSpaceManager = false;
    if (identity != null) {
      isChallengeOwner = managersId.stream().anyMatch(i -> i == Long.parseLong(identity.getId()));
    }
    if (space != null) {
      isSpaceManager = spaceService.isManager(space, userId) || spaceService.isSuperManager(userId);
    }
    return isChallengeOwner && isSpaceManager;
  }

  public static final boolean canAnnounce(String id) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(id);
    if (space == null) {
      throw new IllegalArgumentException("space is not exist");
    }
    String currentUser = getCurrentUser();
    if (StringUtils.isNotBlank(currentUser)) {
      return spaceService.hasRedactor(space) ? spaceService.isRedactor(space, currentUser)
          || spaceService.isManager(space, currentUser) || spaceService.isSuperManager(currentUser)
                                             : spaceService.isMember(space, currentUser);
    } else {
      return false;
    }
  }

  public static String toRFC3339Date(Date dateTime) {
    if (dateTime == null) {
      return null;
    }
    ZonedDateTime zonedDateTime = dateTime.toInstant().atZone(ZoneOffset.UTC);
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

  public static DomainDTO getDomainByTitle(String domainTitle) {
    if (domainTitle == null || domainTitle.isEmpty()) {
      return null;
    }
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    return domainService.findDomainByTitle(domainTitle);
  }

  public static RuleDTO getRuleById(long ruleId) throws IllegalArgumentException {
    if (ruleId == 0) {
      return null;
    }
    try {
      RuleService ruleService = CommonsUtils.getService(RuleService.class);
      return ruleService.findRuleById(ruleId);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  public static RuleDTO getRuleByTitle(String title) {
    try {
      return StringUtils.isBlank(title) ? null : getRuleService().findRuleByTitle("def_" + title);
    } catch (IllegalArgumentException e) {
      return null;
    }

  }

  public static List<UserInfo> getManagersByIds(List<Long> ids) {
    try {
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      if (ids.isEmpty()) {
        return Collections.emptyList();
      }
      List<UserInfo> users = new ArrayList<>();
      for (Long id : ids) {
        Identity identity = identityManager.getIdentity(String.valueOf(id));
        if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
          users.add(createUser(identity));
        }
      }
      return users;
    } catch (Exception e) {
      LOG.error("Error when getting challenge managers {}", e);
      return Collections.emptyList();
    }

  }

  public static UserInfo getUserById(Long id, Long challengeId) {
    try {
      if (id == null) {
        return null;
      }
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      Identity identity = identityManager.getIdentity(String.valueOf(id));
      if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
        if (challengeId != null) {
          Space space;
          ChallengeService challengeService = CommonsUtils.getService(ChallengeService.class);
          Challenge challenge = challengeService.getChallengeById(challengeId, getCurrentUser());
          space = getSpaceById(String.valueOf(challenge.getAudience()));
          return createUser(identity, space, challenge.getManagers());
        } else {
          return createUser(identity);
        }
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  public static UserInfo createUser(Identity identity, Space space, List<Long> managersId) {
    UserInfo userInfo = new UserInfo();
    userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    userInfo.setFullName(identity.getProfile().getFullName());
    userInfo.setRemoteId(identity.getRemoteId());
    userInfo.setId(identity.getId());
    String userId = identity.getRemoteId();
    if (space != null) {
      SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
      Boolean isSuperManager = spaceService.isSuperManager(userId);
      boolean isManager = isSuperManager || spaceService.isManager(space, userId);
      boolean isMember = isManager || spaceService.isMember(space, userId);
      boolean isRedactor = isManager || spaceService.isRedactor(space, userId);
      boolean hasRedactor = spaceService.hasRedactor(space);
      Boolean isChallengeOwner = managersId.stream().anyMatch(i -> i == Long.parseLong(identity.getId()));
      userInfo.setManager(isManager);
      userInfo.setMember(isMember);
      userInfo.setRedactor(isRedactor);
      userInfo.setCanAnnounce(hasRedactor ? isRedactor : isMember);
      userInfo.setCanEdit(isChallengeOwner && isManager);
    }
    return userInfo;
  }

  public static UserInfo createUser(Identity identity) {
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
      return 0l;
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

  public static final Locale getCurrentUserLocale() {
    String username = getCurrentUser();

    LocalePolicy localePolicy = CommonsUtils.getService(LocalePolicy.class);
    LocaleContextInfo localeCtx = LocaleContextInfoUtils.buildLocaleContextInfo((HttpServletRequest) null);
    localeCtx.setUserProfileLocale(getUserLocale(username));
    localeCtx.setRemoteUser(username);
    Set<Locale> supportedLocales = LocaleContextInfoUtils.getSupportedLocales();

    Locale locale = localePolicy.determineLocale(localeCtx);
    boolean supported = supportedLocales.contains(locale);

    if (!supported && !"".equals(locale.getCountry())) {
      locale = new Locale(locale.getLanguage());
      supported = supportedLocales.contains(locale);
    }
    if (!supported) {
      LOG.warn("Unsupported locale returned by LocalePolicy: " + localePolicy + ". Falling back to 'en'.");
      locale = Locale.ENGLISH;
    }
    return locale;
  }

  public static final String getI18NMessage(Locale userLocale, String messageKey) {
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

  public static final Locale getUserLocale(String username) {
    OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
    UserProfile profile = null;
    try {
      profile = organizationService.getUserProfileHandler().findUserProfileByName(username);
    } catch (Exception e) {
      LOG.error("Error when getting user locale ", e);
    }
    String lang = null;
    if (profile != null) {
      lang = profile.getAttribute(Constants.USER_LANGUAGE);
    }
    if (StringUtils.isNotBlank(lang)) {
      return LocaleUtils.toLocale(lang);
    }
    return null;
  }

  public static String escapeIllegalCharacterInMessage(String message) {
    if (message == null) {
      return null;
    }
    message = StringEscapeUtils.unescapeHtml(message);
    for (char c : ILLEGAL_MESSAGE_CHARACTERS) {
      message = message.replace(c, ' ');
    }
    return message;
  }

}

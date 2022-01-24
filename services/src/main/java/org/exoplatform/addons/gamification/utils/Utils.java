package org.exoplatform.addons.gamification.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class Utils {

  public static final String            ANNOUNCEMENT_ACTIVITY_EVENT = "announcement.activity";

  private static final Log              LOG                         = ExoLogger.getLogger(Utils.class);

  public static final DateTimeFormatter RFC_3339_FORMATTER          =
                                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                            .withResolverStyle(ResolverStyle.LENIENT);

  private Utils() { // NOSONAR
  }

  public static Identity getIdentityByTypeAndId(String type, String name) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getOrCreateIdentity(type, name);
  }

  public static final String getCurrentUser() {
    return ConversationState.getCurrent().getIdentity().getUserId();
  }

  public static final boolean canEditChallenge(List<Long> managersId) {
    Identity identity = getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, getCurrentUser());

    return managersId.stream().anyMatch(i -> i == Long.parseLong(identity.getId()));
  }

  public static final boolean canAnnounce(String id) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(id);
    if (space == null) {
      throw new IllegalArgumentException("space is not exist");
    }
    return spaceService.hasRedactor(space) ? spaceService.isRedactor(space, getCurrentUser())
        || spaceService.isManager(space, getCurrentUser()) : spaceService.isMember(space, getCurrentUser());
  }

  public static String toRFC3339Date(Date dateTime) {
    if (dateTime == null) {
      return null;
    }
    ZonedDateTime zonedDateTime = ZonedDateTime.from(dateTime.toInstant().atOffset(ZoneOffset.UTC));
    return zonedDateTime.format(RFC_3339_FORMATTER);
  }

  public static Date parseRFC3339Date(String dateString) {
    if (StringUtils.isBlank(dateString)) {
      return null;
    }
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, RFC_3339_FORMATTER);
    return Date.from(zonedDateTime.toInstant());
  }

  public static Space getSpaceById(String spaceId) {
    if (StringUtils.isBlank(spaceId)) {
      return null;
    }
    Space space = CommonsUtils.getService(SpaceService.class).getSpaceById(spaceId);
    if (space == null) {
      throw new IllegalArgumentException("space is not exist");
    }
    return space;
  }

  public static DomainDTO getDomainByTitle(String domainTitle) {
    DomainService domainService = CommonsUtils.getService(DomainService.class);
    return domainService.findDomainByTitle(domainTitle);
  }

  public static RuleDTO getRuleById(long ruleId) {
    RuleService ruleService = CommonsUtils.getService(RuleService.class);
    return ruleService.findRuleById(ruleId);
  }

  public static List<UserInfo> getManagersByIds(List<Long> ids, Long challengeId) {
    try {
      ChallengeService challengeService = CommonsUtils.getService(ChallengeService.class);
      Challenge challenge = challengeService.getChallengeById(challengeId, getCurrentUser());
      Space space = getSpaceById(String.valueOf(challenge.getAudience()));
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      if (ids.isEmpty()) {
        return Collections.emptyList();
      }
      List<UserInfo> users = new ArrayList<>();
      for (Long id : ids) {
        Identity identity = identityManager.getIdentity(String.valueOf(id));
        if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
          users.add(createUser(identity, space, challenge.getManagers()));
        }
      }
      return users;
    } catch (Exception e) {
      LOG.info("challenge not exist with this id {0}", challengeId);
      return Collections.emptyList();
    }

  }

  public static UserInfo getUserById(Long id, Long challengeId) {
    try {
      ChallengeService challengeService = CommonsUtils.getService(ChallengeService.class);
      Challenge challenge = challengeService.getChallengeById(challengeId, getCurrentUser());
      Space space = getSpaceById(String.valueOf(challenge.getAudience()));
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      if (id == null) {
        return null;
      }
      Identity identity = identityManager.getIdentity(String.valueOf(id));
      if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
        return createUser(identity, space, challenge.getManagers());
      } else {
        return null;
      }
    } catch (Exception e) {
      LOG.info("challenge not exist with this id {0}", challengeId);
      return null;
    }

  }

  public static UserInfo createUser(Identity identity, Space space, List<Long> managersId) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    UserInfo userInfo = new UserInfo();
    userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    userInfo.setFullName(identity.getProfile().getFullName());
    userInfo.setRemoteId(identity.getRemoteId());
    userInfo.setId(identity.getId());
    if (space != null) {
      userInfo.setManager(spaceService.isManager(space, getCurrentUser()));
      userInfo.setMember(spaceService.isMember(space, getCurrentUser()));
      userInfo.setRedactor(spaceService.isRedactor(space, getCurrentUser()));
    }
    userInfo.setCanAnnounce(canAnnounce(space.getId()));
    userInfo.setCanEdit(canEditChallenge(managersId));
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
}

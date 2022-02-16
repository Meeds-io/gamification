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
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
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

  public static final String            ANNOUNCEMENT_ACTIVITY_EVENT = "challenge.announcement.activity";

  private static final Log              LOG                         = ExoLogger.getLogger(Utils.class);

  public static final DateTimeFormatter RFC_3339_FORMATTER          =
                                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                            .withResolverStyle(ResolverStyle.LENIENT);

  private static GamificationService    gamificationService;

  private static RuleService    ruleService;

  private Utils() { // NOSONAR
  }

  public static Identity getIdentityByTypeAndId(String type, String name) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getOrCreateIdentity(type, name);
  }

  public static final String getCurrentUser() {
    if (ConversationState.getCurrent() != null && ConversationState.getCurrent().getIdentity() != null) {
      return ConversationState.getCurrent().getIdentity().getUserId();
    }
    return null;
  }

  public static final boolean canEditChallenge(List<Long> managersId) {
    Identity identity = getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, getCurrentUser());
    if (identity != null) {
      return managersId.stream().anyMatch(i -> i == Long.parseLong(identity.getId()));
    } else {
      return true;
    }
  }

  public static final boolean canAnnounce(String id) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(id);
    if (space == null) {
      throw new IllegalArgumentException("space is not exist");
    }
    if (StringUtils.isNotBlank(getCurrentUser())) {
      return spaceService.hasRedactor(space) ? spaceService.isRedactor(space, getCurrentUser())
          || spaceService.isManager(space, getCurrentUser()) : spaceService.isMember(space, getCurrentUser());
    } else {
      return true;
    }
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

  public static RuleDTO getRuleById(long ruleId) throws IllegalArgumentException {
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
      LOG.error("challenge not exist with this id {}", challengeId, e);
      return Collections.emptyList();
    }

  }

  public static UserInfo getUserById(Long id, Long challengeId) {
    try {
      if (id == null) {
        return null;
      }
      Space space;
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      Identity identity = identityManager.getIdentity(String.valueOf(id));
      if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
        if(challengeId != null){
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
      userInfo.setCanAnnounce(canAnnounce(space.getId()));
    }
    userInfo.setCanEdit(canEditChallenge(managersId));
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

  public static DomainEntity getDomain(String domain) {
    DomainDAO domainService = CommonsUtils.getService(DomainDAO.class);
    return domainService.findDomainByTitle(domain);
  }

  public static String getSpaceFromObjectID(String objectID) {
    if( StringUtils.isBlank(objectID) || !objectID.contains("/portal/g/:spaces:")){
      return null;
    }
    String groupID = objectID.substring(objectID.indexOf(":"),objectID.length()).replace(":","/");
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space =  spaceService.getSpaceByGroupId(groupID);
    return space != null ? space.getDisplayName() : null  ;
  }

}

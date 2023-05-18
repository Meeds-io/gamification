package io.meeds.gamification.service.impl;

import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;
import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM;
import static io.meeds.gamification.utils.Utils.ANNOUNCEMENT_ID_TEMPLATE_PARAM;
import static io.meeds.gamification.utils.Utils.POST_CREATE_ANNOUNCEMENT_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_ANNOUNCEMENT_EVENT;
import static io.meeds.gamification.utils.Utils.broadcastEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.PeriodType;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.RealizationService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.storage.AnnouncementStorage;
import io.meeds.gamification.utils.Utils;

public class AnnouncementServiceImpl implements AnnouncementService {

  private static final Log    LOG             = ExoLogger.getLogger(AnnouncementServiceImpl.class);

  public static final long    MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;                               // NOSONAR

  private ActivityManager     activityManager;

  private IdentityManager     identityManager;

  private SpaceService        spaceService;

  private RuleService         ruleService;

  private RealizationService  realizationService;

  private AnnouncementStorage announcementStorage;

  private ListenerService     listenerService;

  public AnnouncementServiceImpl(AnnouncementStorage announcementStorage,
                                 RuleService ruleService,
                                 RealizationService realizationService,
                                 SpaceService spaceService,
                                 IdentityManager identityManager,
                                 ActivityManager activityManager,
                                 ListenerService listenerService) {
    this.ruleService = ruleService;
    this.realizationService = realizationService;
    this.announcementStorage = announcementStorage;
    this.spaceService = spaceService;
    this.activityManager = activityManager;
    this.identityManager = identityManager;
    this.listenerService = listenerService;
  }

  @Override
  public Announcement createAnnouncement(Announcement announcement, // NOSONAR
                                         Map<String, String> templateParams,
                                         String username) throws ObjectNotFoundException, IllegalAccessException {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (username == null) {
      throw new IllegalAccessException("Username is mandatory");
    }
    if (announcement.getId() != 0) {
      throw new IllegalArgumentException("announcement id must be equal to 0");
    }
    RuleDTO rule = ruleService.findRuleById(announcement.getChallengeId(), username);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule with id '" + announcement.getChallengeId() + "' doesn't exist");
    }
    if (rule.isDeleted()) {
      throw new IllegalAccessException("Rule with id '" + announcement.getChallengeId() + "' is deleted");
    }
    if (!rule.isEnabled()) {
      throw new IllegalAccessException("Rule with id '" + announcement.getChallengeId() + "' isn't enabled");
    }
    if (rule.getType() != EntityType.MANUAL) {
      throw new IllegalStateException("Rule with id '" + announcement.getChallengeId() + "' isn't a challenge");
    }
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    if (identity == null || !canAnnounce(rule, identity.getId())) {
      throw new IllegalAccessException("user " + username + " is not allowed to announce a challenge on space with id "
          + rule.getAudienceId());
    }

    long creatorId = Long.parseLong(identity.getId());
    announcement.setCreator(creatorId);
    announcement.setAssignee(creatorId);
    announcement = announcementStorage.createAnnouncement(announcement);
    createActivity(rule, announcement, templateParams);
    broadcastEvent(listenerService, POST_CREATE_ANNOUNCEMENT_EVENT, announcement, creatorId);
    return announcement;
  }

  @Override
  public boolean canAnnounce(RuleDTO rule, String earnerIdentityId) {
    return realizationService.getRealizationValidityContext(rule, earnerIdentityId).isValid();
  }

  @Override
  public Announcement deleteAnnouncement(long announcementId, String username) throws ObjectNotFoundException,
                                                                               IllegalAccessException {
    if (announcementId <= 0) {
      throw new IllegalArgumentException("Announcement id has to be positive integer");
    }
    Announcement announcement = getAnnouncementById(announcementId);
    if (announcement == null) {
      throw new ObjectNotFoundException("Announcement does not exist");
    }
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    if (!announcement.getCreator().equals(Long.parseLong(identity.getId()))) {
      throw new IllegalAccessException("user " + username + " is not allowed to cancel announcement with id "
          + announcement.getId());
    }
    deleteActivity(announcement);
    return announcementStorage.deleteAnnouncement(announcementId);
  }

  @Override
  public Announcement getAnnouncementById(long announcementId) {
    if (announcementId <= 0) {
      throw new IllegalArgumentException("announcementId is mandatory");
    }
    return announcementStorage.getAnnouncementById(announcementId);
  }

  @Override
  public List<Announcement> findAnnouncements(long ruleId,
                                              int offset,
                                              int limit,
                                              PeriodType periodType,
                                              IdentityType earnerType,
                                              String username) throws IllegalAccessException, ObjectNotFoundException {
    if (ruleId <= 0) {
      throw new IllegalArgumentException("ruleId has to be positive integer");
    }
    RuleDTO rule = ruleService.findRuleById(ruleId, username);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule with id " + ruleId + " wasn't found");
    }
    return announcementStorage.findAnnouncements(ruleId, offset, limit, periodType, earnerType);
  }

  @Override
  public List<Announcement> findAnnouncements(String earnerIdentityId) {
    return announcementStorage.findAnnouncements(earnerIdentityId);
  }

  @Override
  public int countAnnouncements(long ruleId) throws ObjectNotFoundException {
    if (ruleId <= 0) {
      throw new IllegalArgumentException("ruleId has to be positive integer");
    }
    RuleDTO rule = ruleService.findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule doesn't exist");
    }
    return announcementStorage.countAnnouncements(ruleId);
  }

  @Override
  public int countAnnouncements(long ruleId, IdentityType earnerType) throws ObjectNotFoundException {
    if (ruleId <= 0) {
      throw new IllegalArgumentException("ruleId has to be positive integer");
    }
    RuleDTO rule = ruleService.findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule doesn't exist");
    }
    return announcementStorage.countAnnouncements(ruleId, earnerType);
  }

  @Override
  public Announcement updateAnnouncementComment(long announcementId, String comment) throws ObjectNotFoundException {
    if (announcementId == 0) {
      throw new IllegalArgumentException("announcement id is mandatory");
    }
    if (StringUtils.isBlank(comment)) {
      throw new IllegalArgumentException("announcement comment is mandatory");
    }

    Announcement announcement = announcementStorage.updateAnnouncementComment(announcementId, comment);
    broadcastEvent(listenerService, POST_UPDATE_ANNOUNCEMENT_EVENT, announcement, announcement.getCreator());
    return announcement;
  }

  private void deleteActivity(Announcement announcement) {
    try {
      String activityId = String.valueOf(announcement.getActivityId());
      activityManager.deleteActivity(activityId);
    } catch (Exception e) {
      LOG.warn("Error while deleting activity for announcement with challenge with id {} made by user {}",
               announcement.getChallengeId(),
               announcement.getCreator(),
               e);
    }
  }

  private void createActivity(RuleDTO rule,
                              Announcement announcement,
                              Map<String, String> templateParams) {
    try {
      if (templateParams == null) {
        templateParams = new HashMap<>();
      }
      Space space = spaceService.getSpaceById(String.valueOf(rule.getAudienceId()));
      if (space == null) {
        throw new ObjectNotFoundException("space doesn't exists");
      }
      Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
      if (spaceIdentity == null) {
        throw new ObjectNotFoundException("space doesn't exists");
      }
      ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
      activity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
      activity.setTitle(announcement.getComment());
      activity.setUserId(String.valueOf(announcement.getCreator()));
      templateParams.put(ANNOUNCEMENT_ID_TEMPLATE_PARAM, String.valueOf(announcement.getId()));
      templateParams.put(ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM, rule.getTitle());
      Utils.buildActivityParams(activity, templateParams);

      activityManager.saveActivityNoReturn(spaceIdentity, activity);
      long activityId = Long.parseLong(activity.getId());
      announcementStorage.updateAnnouncementActivityId(announcement.getId(), activityId);
      announcement.setActivityId(activityId);
    } catch (Exception e) {
      LOG.warn("Error while creating activity for announcement with challenge with id {} made by user {}",
               rule.getId(),
               announcement.getCreator(),
               e);
    }
  }

}

package org.exoplatform.addons.gamification.service.configuration;

import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ACTIVITY_TYPE;
import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_DESCRIPTION_TEMPLATE_PARAM;
import static org.exoplatform.addons.gamification.utils.Utils.ANNOUNCEMENT_ID_TEMPLATE_PARAM;
import static org.exoplatform.addons.gamification.utils.Utils.POST_CREATE_ANNOUNCEMENT_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.POST_UPDATE_ANNOUNCEMENT_EVENT;
import static org.exoplatform.addons.gamification.utils.Utils.broadcastEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.utils.Utils;
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

public class AnnouncementServiceImpl implements AnnouncementService {

  private static final Log    LOG = ExoLogger.getLogger(AnnouncementServiceImpl.class);

  private AnnouncementStorage announcementStorage;

  private ActivityManager     activityManager;

  private IdentityManager     identityManager;

  private SpaceService        spaceService;

  private RuleService         ruleService;

  private ListenerService     listenerService;

  public AnnouncementServiceImpl(AnnouncementStorage announcementStorage,
                                 RuleService ruleService,
                                 SpaceService spaceService,
                                 IdentityManager identityManager,
                                 ActivityManager activityManager,
                                 ListenerService listenerService) {
    this.announcementStorage = announcementStorage;
    this.spaceService = spaceService;
    this.activityManager = activityManager;
    this.identityManager = identityManager;
    this.ruleService = ruleService;
    this.listenerService = listenerService;
  }

  @Override
  public Announcement createAnnouncement(Announcement announcement,
                                         Map<String, String> templateParams,
                                         String username,
                                         boolean system) throws ObjectNotFoundException, IllegalAccessException {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (announcement.getId() != 0) {
      throw new IllegalArgumentException("announcement id must be equal to 0");
    }
    RuleDTO rule = ruleService.findRuleById(announcement.getChallengeId(), username);
    if (rule == null) {
      throw new ObjectNotFoundException("Rule with id '" + announcement.getChallengeId() + "' doesn't exist");
    }
    Long assignee = announcement.getAssignee();
    if (assignee == null) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    Identity assigneeIdentity = identityManager.getIdentity(assignee.toString());
    if (assigneeIdentity == null || assigneeIdentity.isDeleted() || !assigneeIdentity.isEnable()) {
      throw new ObjectNotFoundException("Assignee with id " + assignee + " does not exist");
    }

    if (!Utils.canAnnounce(String.valueOf(rule.getAudienceId()), username)) {
      throw new IllegalAccessException("user " + username + " is not allowed to announce a challenge on space with id "
          + rule.getAudienceId());
    }
    Identity creatorIdentity = identityManager.getOrCreateUserIdentity(username);
    long creatorId = Long.parseLong(creatorIdentity.getId());
    announcement.setCreator(creatorId);
    announcement.setAssignee(creatorId);
    announcement.setCreatedDate(Utils.toRFC3339Date(new Date()));
    announcement = announcementStorage.saveAnnouncement(announcement);
    if (!system) {
      try {
        announcement = createActivity(rule, announcement, templateParams);
      } catch (Exception e) {
        LOG.warn("Error while creating activity for announcement with challenge with id {} made by user {}",
                 rule.getId(),
                 creatorIdentity.getId(),
                 e);
      }
    }
    broadcastEvent(listenerService, POST_CREATE_ANNOUNCEMENT_EVENT, announcement, creatorId);
    return announcement;
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
  public Long countAnnouncements(long ruleId) throws ObjectNotFoundException {
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
  public Long countAnnouncements(long ruleId, IdentityType earnerType) throws ObjectNotFoundException {
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
  public Announcement updateAnnouncement(Announcement announcement) throws ObjectNotFoundException {
    return updateAnnouncement(announcement, true);
  }

  @Override
  public Announcement updateAnnouncement(Announcement announcement, boolean broadcast) throws ObjectNotFoundException {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (announcement.getId() == 0) {
      throw new IllegalArgumentException("announcement id must not be equal to 0");
    }

    Announcement oldAnnouncement = announcementStorage.getAnnouncementById(announcement.getId());
    if (oldAnnouncement == null) {
      throw new ObjectNotFoundException("Announcement does not exist");
    }
    Announcement savedAnnouncement = announcementStorage.saveAnnouncement(announcement);
    if (broadcast) {
      broadcastEvent(listenerService, POST_UPDATE_ANNOUNCEMENT_EVENT, announcement, announcement.getCreator());
    }
    return savedAnnouncement;
  }

  @Override
  public Announcement deleteAnnouncement(long announcementId, String username) throws ObjectNotFoundException,
                                                                               IllegalAccessException {
    if (announcementId <= 0) {
      throw new IllegalArgumentException("Announcement id has to be positive integer");
    }
    Announcement announcementToCancel = announcementStorage.getAnnouncementById(announcementId);
    if (announcementToCancel == null) {
      throw new ObjectNotFoundException("Announcement does not exist");
    }
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    if (!announcementToCancel.getCreator().equals(Long.parseLong(identity.getId()))) {
      throw new IllegalAccessException("user " + username + " is not allowed to cancel announcement with id "
          + announcementToCancel.getId());
    }
    String activityId = String.valueOf(announcementToCancel.getActivityId());
    activityManager.deleteActivity(activityId);
    return announcementStorage.deleteAnnouncement(announcementToCancel);
  }

  @Override
  public Announcement getAnnouncementById(Long announcementId) {
    if (announcementId == null || announcementId <= 0) {
      throw new IllegalArgumentException("announcement id is mandatory");
    }
    return announcementStorage.getAnnouncementById(announcementId);
  }

  public Announcement createActivity(RuleDTO rule,
                                     Announcement announcement,
                                     Map<String, String> templateParams) throws ObjectNotFoundException {
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
    announcement.setActivityId(Long.parseLong(activity.getId()));
    return updateAnnouncement(announcement, false);
  }
}

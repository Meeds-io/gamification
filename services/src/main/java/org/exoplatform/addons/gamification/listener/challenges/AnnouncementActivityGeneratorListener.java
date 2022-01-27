package org.exoplatform.addons.gamification.listener.challenges;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.social.websocket.ActivityStreamWebSocketService;
import org.exoplatform.social.websocket.entity.ActivityStreamModification;

import java.util.HashMap;
import java.util.Map;

public class AnnouncementActivityGeneratorListener extends Listener<AnnouncementService, Announcement> {
  private static final Log               LOG                        =
                                             ExoLogger.getLogger(AnnouncementActivityGeneratorListener.class);

  public static final String             ANNOUNCEMENT_ACTIVITY_TYPE = "challenges-announcement";

  private ExoContainer                   container;

  private ActivityStorage                activityStorage;

  private ChallengeService               challengeService;

  private ActivityStreamWebSocketService activityStreamWebSocketService;

  public AnnouncementActivityGeneratorListener(ExoContainer container,
                                               ActivityStorage activityStorage,
                                               ChallengeService challengeService,
                                               ActivityStreamWebSocketService activityStreamWebSocketService) {
    this.container = container;
    this.activityStorage = activityStorage;
    this.challengeService = challengeService;
    this.activityStreamWebSocketService = activityStreamWebSocketService;
  }

  @Override
  public void onEvent(Event<AnnouncementService, Announcement> event) throws ObjectNotFoundException, IllegalAccessException {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    try {
      Announcement announcement = event.getData();
      AnnouncementService announcementService = event.getSource();
      Challenge challenge = challengeService.getChallengeById(announcement.getChallengeId(), Utils.getCurrentUser());
      ExoSocialActivity activity = createActivity(announcement, challenge);
      announcement.setActivityId(Long.parseLong(activity.getId()));
      announcementService.updateAnnouncement(announcement);
      ActivityStreamModification activityStreamModification = new ActivityStreamModification(activity.getId(), "createActivity");
      activityStreamWebSocketService.sendMessage(activityStreamModification);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private ExoSocialActivity createActivity(Announcement announcement, Challenge challenge) throws ObjectNotFoundException {
    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    activity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
    activity.setTitle(challenge.getTitle());
    activity.setUserId(String.valueOf(announcement.getCreator()));
    Map<String, String> params = new HashMap<>();
    params.put("announcementId", String.valueOf(announcement.getId()));
    params.put("announcementComment", announcement.getComment());
    params.put("announcementDescription", challenge.getTitle());
    activity.setTemplateParams(params);
    Space space = Utils.getSpaceById(String.valueOf(challenge.getAudience()));
    if (space == null) {
      throw new ObjectNotFoundException("space does not exist");
    }
    Identity owner = Utils.getIdentityByTypeAndId("space", space.getPrettyName());
    return activityStorage.saveActivity(owner, activity);
  }

}

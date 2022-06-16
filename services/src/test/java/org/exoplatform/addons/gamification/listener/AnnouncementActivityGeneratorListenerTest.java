package org.exoplatform.addons.gamification.listener;

import org.exoplatform.addons.gamification.listener.challenges.AnnouncementActivityGeneratorListener;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.EntityBuilder;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.AnnouncementActivity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.listener.Event;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.social.websocket.ActivityStreamWebSocketService;
import org.exoplatform.social.websocket.entity.ActivityStreamModification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.Date;

import static org.exoplatform.addons.gamification.service.EntityBuilder.fromAnnouncementActivity;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
@PrepareForTest({ EntityBuilder.class, Utils.class })
public class AnnouncementActivityGeneratorListenerTest {

  @Mock
  private AnnouncementService            announcementService;

  @Mock
  private ExoContainer                   container;

  @Mock
  private ActivityStorage                activityStorage;

  @Mock
  private ChallengeService               challengeService;

  @Mock
  private ActivityStreamWebSocketService activityStreamWebSocketService;

  @Test
  public void testGenerateAnnouncementActivity() throws IllegalAccessException, ObjectNotFoundException {
    PowerMockito.mockStatic(EntityBuilder.class);
    PowerMockito.mockStatic(Utils.class);
    AnnouncementActivityGeneratorListener announcementActivityGeneratorListener =
                                                                                new AnnouncementActivityGeneratorListener(container,
                                                                                                                          activityStorage,
                                                                                                                          challengeService,
                                                                                                                          activityStreamWebSocketService);

    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification");

    Announcement announcement = new Announcement(1,
                                                 challenge.getId(),
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null);
    AnnouncementActivity announcementActivity = new AnnouncementActivity(1,
                                                                         challenge.getId(),
                                                                         1L,
                                                                         "announcement comment",
                                                                         1L,
                                                                         new Date(System.currentTimeMillis()).toString(),
                                                                         null,
                                                                         null);
    Space space = new Space();
    space.setId("1");
    space.setDisplayName("test space");
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");

    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    activity.setId("1");
    when(fromAnnouncementActivity(any(AnnouncementActivity.class))).thenReturn(announcement);
    when(Utils.getCurrentUser()).thenReturn("root");
    when(Utils.getSpaceById(String.valueOf(challenge.getAudience()))).thenReturn(space);
    when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(spaceIdentity);

    when(challengeService.getChallengeById(anyLong(), anyString())).thenReturn(challenge);
    when(announcementService.updateAnnouncement(any(Announcement.class))).thenReturn(announcement);
    when(activityStorage.saveActivity(any(), any())).thenReturn(activity);
    Event event = new Event<>(Utils.ANNOUNCEMENT_ACTIVITY_EVENT, announcementService, announcementActivity);
    announcementActivityGeneratorListener.onEvent(event);
    verify(announcementService, times(1)).updateAnnouncement(any(Announcement.class));
    verify(challengeService, times(1)).getChallengeById(anyLong(), anyString());
    verify(activityStreamWebSocketService, times(1)).sendMessage(any(ActivityStreamModification.class));
  }

}

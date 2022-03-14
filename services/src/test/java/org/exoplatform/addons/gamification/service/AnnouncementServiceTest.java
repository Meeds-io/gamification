package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.configuration.AnnouncementServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class AnnouncementServiceTest extends BaseExoTestCase {
  private AnnouncementStorage announcementStorage;

  private RuleStorage challengeStorage;

  private AnnouncementService announcementService;

  private ListenerService     listenerService;

  private SpaceService        spaceService;

  private IdentityManager     identityManager;

  @Before
  public void setUp() { // NOSONAR
    announcementStorage = mock(AnnouncementStorage.class);
    challengeStorage = mock(RuleStorage.class);
    spaceService = mock(SpaceService.class);
    identityManager = mock(IdentityManager.class);
    listenerService = mock(ListenerService.class);
    announcementService = new AnnouncementServiceImpl(announcementStorage, challengeStorage, listenerService);
  }

  @PrepareForTest({ Utils.class, EntityMapper.class })
  @Test
  public void testCreateAnnouncement() throws IllegalAccessException, ObjectNotFoundException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification");

    Announcement announcement = new Announcement(0,
                                                 challenge.getId(),
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null);

    Announcement createdAnnouncement = new Announcement(1,
                                                        challenge.getId(),
                                                        1L,
                                                        "announcement comment",
                                                        1L,
                                                        new Date(System.currentTimeMillis()).toString(),
                                                        null);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    String[] spaceMembers = { "root" };
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    space.setManagers(spaceMembers);
    space.setMembers(spaceMembers);
    space.setRedactors(new String[0]);
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isRedactor(space, "root")).thenReturn(true);
    when(identityManager.getOrCreateIdentity("space", "root")).thenReturn(spaceIdentity);
    when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);
    when(announcementStorage.saveAnnouncement(announcement)).thenReturn(createdAnnouncement);
    PowerMockito.mockStatic(Utils.class);
    when(Utils.canAnnounce(any())).thenReturn(true);
    when(Utils.canEditChallenge(any(),any())).thenReturn(true);
    Identity identity = mock(Identity.class);
    when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    when(identity.getId()).thenReturn("1");
    Announcement newAnnouncement = announcementService.createAnnouncement(announcement, "root", false);

    assertNotNull(newAnnouncement);
    assertEquals(1l, newAnnouncement.getId());
  }

  @PrepareForTest({ Utils.class, EntityMapper.class })
  @Test
  public void testUpdateAnnouncement() throws IllegalAccessException, ObjectNotFoundException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification");
    Announcement announcement = new Announcement(0,
                                                 challenge.getId(),
                                                 1L,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null);
    Announcement createdAnnouncement = new Announcement(1,
                                                        challenge.getId(),
                                                        1L,
                                                        "announcement comment",
                                                        1L,
                                                        new Date(System.currentTimeMillis()).toString(),
                                                        null);
    Announcement editedAnnouncement = new Announcement(1,
                                                       challenge.getId(),
                                                       1L,
                                                       "announcement comment",
                                                       1L,
                                                       new Date(System.currentTimeMillis()).toString(),
                                                       1L);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    String[] spaceMembers = { "root" };
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    space.setManagers(spaceMembers);
    space.setMembers(spaceMembers);
    space.setRedactors(new String[0]);
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isRedactor(space, "root")).thenReturn(true);
    when(identityManager.getOrCreateIdentity("space", "root")).thenReturn(spaceIdentity);
    when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);
    when(announcementStorage.saveAnnouncement(announcement)).thenReturn(createdAnnouncement);
    when(announcementStorage.saveAnnouncement(createdAnnouncement)).thenReturn(editedAnnouncement);
    when(announcementStorage.getAnnouncementById(1L)).thenReturn(createdAnnouncement);
    PowerMockito.mockStatic(Utils.class);
    when(Utils.canAnnounce(any())).thenReturn(true);
    when(Utils.canEditChallenge(any(),any())).thenReturn(true);
    Identity identity = mock(Identity.class);
    when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    when(identity.getId()).thenReturn("1");

    Announcement newAnnouncement = announcementService.createAnnouncement(announcement, "root", false);

    assertNotNull(newAnnouncement);

    newAnnouncement.setActivityId(1L);
    Announcement updatedAnnouncement = announcementService.updateAnnouncement(newAnnouncement);
    assertEquals(updatedAnnouncement.getId(), newAnnouncement.getId());
    assertEquals(updatedAnnouncement.getActivityId(), newAnnouncement.getActivityId());
  }

  @Test
  public void testGetAnnouncementByChallenge() throws IllegalAccessException, ObjectNotFoundException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification");
    Announcement announcement1 = new Announcement(0,
                                                  challenge.getId(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  null);
    Announcement announcement2 = new Announcement(1,
                                                  challenge.getId(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  null);
    Announcement announcement3 = new Announcement(1,
                                                  challenge.getId(),
                                                  1L,
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  1L);
    List<Announcement> announcementList = new ArrayList<>();
    announcementList.add(announcement1);
    announcementList.add(announcement2);
    announcementList.add(announcement3);

    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    when(announcementStorage.findAllAnnouncementByChallenge(challenge.getId(), 0, 10)).thenReturn(announcementList);

    List<Announcement> newAnnouncementList = announcementService.findAllAnnouncementByChallenge(challenge.getId(), 0, 10);

    assertNotNull(newAnnouncementList);
    assertEquals(announcementList, newAnnouncementList);
  }

}

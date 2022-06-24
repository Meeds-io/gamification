package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.service.configuration.AnnouncementServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.AnnouncementStorage;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
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

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.xml.*", "org.xml.*"})
public class AnnouncementServiceTest extends BaseExoTestCase {
    private AnnouncementStorage announcementStorage;

    private ChallengeStorage challengeStorage;

    private AnnouncementService announcementService;

    private ListenerService listenerService;

    private SpaceService spaceService;

    private IdentityManager identityManager;

    @Before
    public void setUp() { // NOSONAR
        announcementStorage = mock(AnnouncementStorage.class);
        challengeStorage = mock(ChallengeStorage.class);
        spaceService = mock(SpaceService.class);
        identityManager = mock(IdentityManager.class);
        listenerService = mock(ListenerService.class);
        announcementService = new AnnouncementServiceImpl(announcementStorage, challengeStorage, listenerService);
    }

    @PrepareForTest({Utils.class, EntityMapper.class})
    @Test
    public void testCreateAnnouncement() {
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

        Announcement announcementWithoutAssignee = new Announcement(0,
                challenge.getId(),
                null,
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


        String[] spaceMembers = {"root"};
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
        when(announcementStorage.getAnnouncementById(createdAnnouncement.getId())).thenReturn(createdAnnouncement);
        PowerMockito.mockStatic(Utils.class);

        Identity identity = mock(Identity.class);
        when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
        when(identity.getId()).thenReturn("1");

        Map<String, String > templateParams = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> announcementService.createAnnouncement(null,  templateParams, "root", false));
        assertThrows(IllegalArgumentException.class, () -> announcementService.createAnnouncement(createdAnnouncement,  templateParams, "root", false));


        assertThrows(ObjectNotFoundException.class, () -> announcementService.createAnnouncement(announcement, templateParams, "root", false));

        assertThrows(IllegalArgumentException.class, () -> announcementService.createAnnouncement(announcementWithoutAssignee, templateParams, "root", false));

        when(Utils.canAnnounce(any(), anyString())).thenReturn(false);
        assertThrows(IllegalAccessException.class, () -> announcementService.createAnnouncement(announcement, templateParams, "root", false));
        when(Utils.canAnnounce(any(), anyString())).thenReturn(true);

        Announcement newAnnouncement = null;
        try {
            newAnnouncement = announcementService.createAnnouncement(announcement, templateParams, "root", false);
        } catch (IllegalAccessException | ObjectNotFoundException e) {
        }
        assertNotNull(newAnnouncement);
        assertEquals(1l, newAnnouncement.getId());
    }

    @PrepareForTest({Utils.class, EntityMapper.class})
    @Test
    public void testUpdateAnnouncement() {
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

        String[] spaceMembers = {"root"};
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
        PowerMockito.mockStatic(Utils.class);
        when(Utils.canAnnounce(any(), anyString())).thenReturn(true);
        Identity identity = mock(Identity.class);
        when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
        when(identity.getId()).thenReturn("1");
        when(announcementStorage.getAnnouncementById(1L)).thenReturn(createdAnnouncement);

        Announcement newAnnouncement = null;
        try {
            newAnnouncement = announcementService.createAnnouncement(announcement, new HashMap<>(), "root", false);
        } catch (IllegalAccessException | ObjectNotFoundException e) {
        }
        assertNotNull(newAnnouncement);
        newAnnouncement.setActivityId(1L);

        assertThrows(IllegalArgumentException.class, () -> announcementService.updateAnnouncement(null));

        assertThrows(IllegalArgumentException.class, () -> announcementService.updateAnnouncement(announcement));

        when(announcementStorage.getAnnouncementById(1L)).thenReturn(null);
        Announcement finalNewAnnouncement = newAnnouncement;
        assertThrows(ObjectNotFoundException.class, () -> announcementService.updateAnnouncement(finalNewAnnouncement));
        when(announcementStorage.getAnnouncementById(1L)).thenReturn(createdAnnouncement);

        Announcement updatedAnnouncement = null;
        try {
            updatedAnnouncement = announcementService.updateAnnouncement(newAnnouncement);
            assertEquals(updatedAnnouncement.getId(), newAnnouncement.getId());
            assertEquals(updatedAnnouncement.getActivityId(), newAnnouncement.getActivityId());
        } catch (ObjectNotFoundException e) {
        }
    }

    @Test
    public void testGetAnnouncementByChallenge() {
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

        when(announcementStorage.findAllAnnouncementByChallenge(challenge.getId(), 0, 10)).thenReturn(announcementList);

        assertThrows(IllegalArgumentException.class, () -> announcementService.findAllAnnouncementByChallenge(0, 0, 10));

        List<Announcement> newAnnouncementList = null;
        try {
            newAnnouncementList = announcementService.findAllAnnouncementByChallenge(challenge.getId(), 0, 10);
            assertNotNull(newAnnouncementList);
            assertEquals(announcementList, newAnnouncementList);
        } catch (IllegalAccessException | ObjectNotFoundException e) {
        }
    }

    @Test
    public void testCountAllAnnouncementsByChallenge() {
        Challenge challenge = new Challenge(1,
                "new challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");

        when(announcementStorage.countAnnouncementsByChallenge(challenge.getId())).thenReturn(10l);

        assertThrows(IllegalArgumentException.class, () -> announcementService.countAllAnnouncementsByChallenge(0l));
        assertThrows(ObjectNotFoundException.class, () -> announcementService.countAllAnnouncementsByChallenge(challenge.getId()));

        try {
            Long count = announcementService.countAllAnnouncementsByChallenge(challenge.getId());
            assertEquals(10l, (long) count);
        } catch (IllegalArgumentException | ObjectNotFoundException e) {
        }
    }

    @Test
    public void testGetAnnouncementById() {
        Announcement announcement = new Announcement(1,
                1l,
                1L,
                "announcement comment",
                1L,
                new Date(System.currentTimeMillis()).toString(),
                null);
        when(announcementStorage.getAnnouncementById(announcement.getId())).thenReturn(announcement);

        assertThrows(IllegalArgumentException.class, () -> announcementService.getAnnouncementById(0l));
        assertThrows(IllegalArgumentException.class, () -> announcementService.getAnnouncementById(-1l));
        Announcement savedAnnouncement = announcementService.getAnnouncementById(1l);
        assertNotNull(savedAnnouncement);
        assertEquals(announcement.getId(), savedAnnouncement.getId());
    }
}


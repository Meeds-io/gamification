package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.service.configuration.AnnouncementServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.Date;


import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.xml.*", "org.xml.*"})
@PrepareForTest({Utils.class})
public class AnnouncementStorageTest extends AbstractServiceTest {

    @Before
    public void setUp() { // NOSONAR
        PowerMockito.mockStatic(Utils.class);
        when(Utils.getGamificationService()).thenReturn(gamificationService);
    }


    @Test
    public void testSaveAnnouncement() {
      Date startDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis() + 2);
        DomainDTO domainDTO = newDomainDTO();
        Challenge challenge = new Challenge(0l,
                "new challenge",
                "challenge description",
                1l,
                Utils.toSimpleDateFormat(startDate),
                Utils.toSimpleDateFormat(endDate),
                Collections.emptyList(),
                10L,
                domainDTO.getTitle());
       challenge = challengeStorage.saveChallenge(challenge, "root");

        Announcement announcement = new Announcement(0,
                challenge.getId(),
                challenge.getTitle(),
                1L,
                "announcement comment",
                1L,
                Utils.toRFC3339Date(new Date(System.currentTimeMillis() + 1)),
                null);
        
        Announcement createdAnnouncement = announcementStorage.saveAnnouncement(announcement);
        
        assertNotNull(createdAnnouncement);
        assertNotSame(0, announcement.getId());
        assertEquals( challenge.getId(),(long) announcement.getChallengeId());
        
    }

  @Test
  public void testGetAnnouncementById() {
      Date startDate = new Date(System.currentTimeMillis());
      Date endDate = new Date(System.currentTimeMillis() + 2);
      DomainDTO domainDTO = newDomainDTO();
      Challenge challenge = new Challenge(0l,
              "new challenge",
              "challenge description",
              1l,
              Utils.toSimpleDateFormat(startDate),
              Utils.toSimpleDateFormat(endDate),
              Collections.emptyList(),
              10L,
              domainDTO.getTitle());
      challenge = challengeStorage.saveChallenge(challenge, "root");

      Announcement announcement = new Announcement(0,
              challenge.getId(),
              challenge.getTitle(),
              1L,
              "announcement comment",
              1L,
              Utils.toRFC3339Date(new Date(System.currentTimeMillis() + 1)),
              null);

      Announcement createdAnnouncement = announcementStorage.saveAnnouncement(announcement);
      assertNotNull( createdAnnouncement);
      createdAnnouncement = announcementStorage.getAnnouncementById(announcement.getId());
      assertNotNull(createdAnnouncement);
  }

  @Test
  public void testGetAnnouncementByChallengeId() {

      Date startDate = new Date(System.currentTimeMillis());
      Date endDate = new Date(System.currentTimeMillis() + 2);
      DomainDTO domainDTO = newDomainDTO();
      Challenge challenge = new Challenge(0l,
              "new challenge",
              "challenge description",
              1l,
              Utils.toSimpleDateFormat(startDate),
              Utils.toSimpleDateFormat(endDate),
              Collections.emptyList(),
              10L,
              domainDTO.getTitle());
      challenge = challengeStorage.saveChallenge(challenge, "root");
      assertEquals(0, (long) announcementStorage.countAnnouncementsByChallenge(challenge.getId()));

      Announcement announcement = new Announcement(0,
              challenge.getId(),
              challenge.getTitle(),
              1L,
              "announcement comment",
              1L,
              Utils.toRFC3339Date(new Date(System.currentTimeMillis() + 1)),
              null);

      announcementStorage.saveAnnouncement(announcement);
      announcementStorage.saveAnnouncement(announcement);
      announcementStorage.saveAnnouncement(announcement);
      assertEquals(3l, (long) announcementStorage.countAnnouncementsByChallenge(challenge.getId()));
  }
}

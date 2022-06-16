package org.exoplatform.addons.gamification.utils;

import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.exoplatform.addons.gamification.utils.Utils.getGamificationService;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.xml.*", "org.xml.*"})
public class UtilsTest {

    @Test
    public void testToRFC3339Date() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date date = new Date(System.currentTimeMillis());
        date.setHours(15);
        assertNull(Utils.toRFC3339Date(null));
        int dateMonth = date.getMonth() + 1;
        int dateDay = date.getDate();
        int dateYear = date.getYear() + 1900;

        String rfc3339Date = Utils.toRFC3339Date(date);
        assertNotNull(rfc3339Date);
        String fullDate = rfc3339Date.split("T")[0];
        String year = fullDate.split("-")[0];
        String month = fullDate.split("-")[1];
        String day = fullDate.split("-")[2];

        assertEquals(Integer.parseInt(month), dateMonth);
        assertEquals(Integer.parseInt(day), dateDay);
        assertEquals(Integer.parseInt(year), dateYear);
    }

    @Test
    public void testParseRFC3339Date() {
        String rfc3339Date = "2022-04-05T13:45:51.701Z";
        String fullDate = rfc3339Date.split("T")[0];
        String year = fullDate.split("-")[0];
        String month = fullDate.split("-")[1];
        String day = fullDate.split("-")[2];

        assertNull(Utils.parseRFC3339Date(""));

        Date date = Utils.parseRFC3339Date(rfc3339Date);
        assertNotNull(date);
        int dateMonth = date.getMonth() + 1;
        int dateDay = date.getDate();
        int dateYear = date.getYear() + 1900;

        assertEquals(Integer.parseInt(month), dateMonth);
        assertEquals(Integer.parseInt(day), dateDay);
        assertEquals(Integer.parseInt(year), dateYear);
    }

    @Test
    public void testToSimpleDateFormat() {
        Date date = new Date(System.currentTimeMillis());
        assertNull(Utils.toRFC3339Date(null));
        int dateMonth = date.getMonth() + 1;
        int dateDay = date.getDate();
        int dateYear = date.getYear() + 1900;

        String rfc3339Date = Utils.toSimpleDateFormat(date);
        assertNotNull(rfc3339Date);
        String fullDate = rfc3339Date.split("T")[0];
        String year = fullDate.split("-")[0];
        String month = fullDate.split("-")[1];
        String day = fullDate.split("-")[2];

        assertEquals("00:00:00", rfc3339Date.split("T")[1]);
        assertEquals(Integer.parseInt(month), dateMonth);
        assertEquals(Integer.parseInt(day), dateDay);
        assertEquals(Integer.parseInt(year), dateYear);
    }

    @Test
    public void testParseSimpleDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("US/Hawaii"));

        String simpleDate = "2022-04-05T00:00:00";
        String fullDate = simpleDate.split("T")[0];
        String year = fullDate.split("-")[0];
        String month = fullDate.split("-")[1];
        String day = fullDate.split("-")[2];
        Date date = Utils.parseSimpleDate(simpleDate);
        assertNull(Utils.toRFC3339Date(null));
        int dateMonth = date.getMonth() + 1;
        int dateDay = date.getDate();
        int dateYear = date.getYear() + 1900;

        assertEquals("00:00:00", simpleDate.split("T")[1]);
        assertEquals(Integer.parseInt(month), dateMonth);
        assertEquals(Integer.parseInt(day), dateDay);
        assertEquals(Integer.parseInt(year), dateYear);
    }

    @Test
    public void testEscapeIllegalCharacterInMessage() {
        String message = "testing escaping illegal characters such as , and ; and \n";
        assertTrue(message.contains(","));
        assertTrue(message.contains(";"));
        assertTrue(message.contains("\n"));

        assertNull(Utils.escapeIllegalCharacterInMessage(null));

        message = Utils.escapeIllegalCharacterInMessage(message);
        assertNotNull(message);
        assertFalse(message.contains(","));
        assertFalse(message.contains(";"));
        assertFalse(message.contains("\n"));
    }

    @Test
    public void testGetCurrentUser() {
        String currentUser = Utils.getCurrentUser();
        assertNull(currentUser);
        ConversationState.setCurrent(new ConversationState(new org.exoplatform.services.security.Identity("root")));
        currentUser = Utils.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("root", currentUser);
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetRuleService() {
        RuleService ruleService = mock(RuleService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(RuleService.class)).thenReturn(ruleService);
        ruleService = Utils.getRuleService();
        assertNotNull(ruleService);
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetGamificationService() {
        GamificationService gamificationService = mock(GamificationService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(GamificationService.class)).thenReturn(gamificationService);
        gamificationService = getGamificationService();
        assertNotNull(gamificationService);
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetUserGlobalScore() {
        GamificationService gamificationService = mock(GamificationService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(GamificationService.class)).thenReturn(gamificationService);
        when(getGamificationService().computeTotalScore("1")).thenReturn(10l);
        Long userScore = Utils.getUserGlobalScore("");
        assertEquals(0, (long) userScore);
        userScore = Utils.getUserGlobalScore("1");
        assertEquals(10l, (long) userScore);
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetSpaceFromObjectID() {
        SpaceService spaceService = mock(SpaceService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(SpaceService.class)).thenReturn(spaceService);
        Space space = new Space();
        space.setId("1");
        space.setPrettyName("test_space");
        space.setDisplayName("test space");
        space.setGroupId("/spaces/test_space");
        String ObjectId = "/portal/g/:spaces:test_space";
        when(spaceService.getSpaceByGroupId("/spaces/test_space")).thenReturn(space);
        String spaceDisplayName = Utils.getSpaceFromObjectID("test");
        assertNull(spaceDisplayName);
        spaceDisplayName = Utils.getSpaceFromObjectID("");
        assertNull(spaceDisplayName);
        spaceDisplayName = Utils.getSpaceFromObjectID(ObjectId);
        assertNotNull(spaceDisplayName);
        assertEquals("test space", spaceDisplayName);
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testCountAnnouncementsByChallenge() {
        AnnouncementService announcementService = mock(AnnouncementService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(AnnouncementService.class)).thenReturn(announcementService);

        try {
            when(announcementService.countAllAnnouncementsByChallenge(1L)).thenReturn(2l);
            when(announcementService.countAllAnnouncementsByChallenge(2l)).thenReturn(3l);
            when(announcementService.countAllAnnouncementsByChallenge(3l)).thenThrow(ObjectNotFoundException.class);
            Long announcementsChallenge1 = Utils.countAnnouncementsByChallenge(1l);
            Long announcementsChallenge2 = Utils.countAnnouncementsByChallenge(2l);
            assertEquals((Long) 2l, announcementsChallenge1);
            assertEquals((Long) 3l, announcementsChallenge2);
            assertThrows(ObjectNotFoundException.class, () -> announcementService.countAllAnnouncementsByChallenge(3l));

        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testCreateUser() {
        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");
        Profile rootProfile = new Profile(rootIdentity);
        rootProfile.setAvatarUrl("/avatar");
        rootProfile.setProperty("firstName", "root");
        rootProfile.setProperty("lastName", "root");
        rootIdentity.setProfile(rootProfile);
        UserInfo simpleUserInfo = Utils.createUser(rootIdentity);
        assertNotNull(simpleUserInfo);
        assertEquals("root", simpleUserInfo.getRemoteId());
        String[] spaceMembers = {"root"};
        Space space = new Space();
        space.setId("1");
        space.setPrettyName("test_space");
        space.setDisplayName("test space");
        space.setGroupId("/spaces/test_space");
        space.setManagers(spaceMembers);
        space.setMembers(spaceMembers);
        space.setRedactors(new String[0]);

        SpaceService spaceService = mock(SpaceService.class);

        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(SpaceService.class)).thenReturn(spaceService);
        when(spaceService.isSuperManager("root")).thenReturn(true);
        when(spaceService.hasRedactor(space)).thenReturn(false);

        UserInfo userInfo = Utils.createUser(rootIdentity, space, Collections.singletonList(1l));
        assertNotNull(userInfo);
        assertEquals("root", userInfo.getRemoteId());
        assertTrue(userInfo.isCanAnnounce());
        assertTrue(userInfo.isCanEdit());

    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetDomainByTitle() {
        DomainService domainService = mock(DomainService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(DomainService.class)).thenReturn(domainService);
        DomainDTO domain = new DomainDTO();
        domain.setTitle("gamification");
        when(domainService.findEnabledDomainByTitle("gamification")).thenReturn(domain);
        DomainDTO savedDomain = Utils.getEnabledDomainByTitle("");
        assertNull(savedDomain);
        savedDomain = Utils.getEnabledDomainByTitle(null);
        assertNull(savedDomain);
        savedDomain = Utils.getEnabledDomainByTitle("gamification");
        assertNotNull(savedDomain);
        assertEquals("gamification", savedDomain.getTitle());
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetRuleById() {
        RuleService ruleService = mock(RuleService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(RuleService.class)).thenReturn(ruleService);
        RuleDTO rule = new RuleDTO();
        rule.setTitle("gamification");
        rule.setId(1l);
        when(ruleService.findRuleById(1l)).thenReturn(rule);
        RuleDTO savedRule = Utils.getRuleById(0);
        assertNull(savedRule);
        savedRule = Utils.getRuleById(1l);
        assertNotNull(savedRule);
        assertEquals("gamification", savedRule.getTitle());
        assertEquals(1l, (long) savedRule.getId());
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetRuleByTitle() {
        RuleService ruleService = mock(RuleService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(RuleService.class)).thenReturn(ruleService);
        RuleDTO rule = new RuleDTO();
        rule.setTitle("gamification");
        rule.setId(1l);
        when(ruleService.findRuleByTitle("def_gamification")).thenReturn(rule);
        RuleDTO savedRule = Utils.getRuleByTitle("");
        assertNull(savedRule);
        savedRule = Utils.getRuleByTitle("gamification");
        assertNotNull(savedRule);
        assertEquals("gamification", savedRule.getTitle());
        assertEquals(1l, (long) savedRule.getId());
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetIdentityByTypeAndId() {
        PowerMockito.mockStatic(CommonsUtils.class);
        IdentityManager identityManager = mock(IdentityManager.class);

        when(CommonsUtils.getService(IdentityManager.class)).thenReturn(identityManager);

        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");
        when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);
        Identity identity = Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, "root");
        assertNotNull(identity);
        assertEquals(rootIdentity.getRemoteId(), identity.getRemoteId());
        assertEquals(rootIdentity.getId(), identity.getId());
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetUserRemoteId() {
        PowerMockito.mockStatic(CommonsUtils.class);
        IdentityManager identityManager = mock(IdentityManager.class);
        when(CommonsUtils.getService(IdentityManager.class)).thenReturn(identityManager);
        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");
        String remoteId = Utils.getUserRemoteId("1");
        assertNull(remoteId);
        when(identityManager.getIdentity("1")).thenReturn(rootIdentity);
        remoteId = Utils.getUserRemoteId("1");
        assertNotNull(remoteId);
        assertEquals("root", remoteId);
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testCanAnnounce() {
        SpaceService spaceService = mock(SpaceService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(SpaceService.class)).thenReturn(spaceService);

        Space space = new Space();
        space.setId("1");
        space.setPrettyName("test_space");
        space.setDisplayName("test space");
        space.setGroupId("/spaces/test_space");
        when(spaceService.isSuperManager("root")).thenReturn(true);
        when(spaceService.hasRedactor(space)).thenReturn(false);
        when(spaceService.isMember(space, "root")).thenReturn(true);

        String username = "root";
        assertThrows(IllegalArgumentException.class, () -> Utils.canAnnounce("1", username));
        when(spaceService.getSpaceById("1")).thenReturn(space);
        boolean canAnnounce = Utils.canAnnounce("1", "");
        assertFalse(canAnnounce);
        canAnnounce = Utils.canAnnounce("1", username);
        assertTrue(canAnnounce);
        when(spaceService.hasRedactor(space)).thenReturn(false);
        when(spaceService.isManager(space, "root")).thenReturn(true);
        canAnnounce = Utils.canAnnounce("1", username);
        assertTrue(canAnnounce);
        canAnnounce = Utils.canAnnounce("1", username);
        assertTrue(canAnnounce);
        when(spaceService.isManager(space, "root")).thenReturn(false);
        when(spaceService.isSuperManager("root")).thenReturn(true);
        canAnnounce = Utils.canAnnounce("1", username);
        assertTrue(canAnnounce);
        when(spaceService.isSuperManager("root")).thenReturn(false);
        when(spaceService.isRedactor(space, "root")).thenReturn(false);
        canAnnounce = Utils.canAnnounce("1", username);
        assertTrue(canAnnounce);
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testCanEditChallenge() {
        ConversationState.setCurrent(new ConversationState(new org.exoplatform.services.security.Identity("root")));
        SpaceService spaceService = mock(SpaceService.class);
        IdentityManager identityManager = mock(IdentityManager.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(SpaceService.class)).thenReturn(spaceService);
        when(CommonsUtils.getService(IdentityManager.class)).thenReturn(identityManager);
        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");
        when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);

        Space space = new Space();
        space.setId("1");
        space.setPrettyName("test_space");
        space.setDisplayName("test space");
        space.setGroupId("/spaces/test_space");
        when(spaceService.getSpaceById("1")).thenReturn(space);
        when(spaceService.isSuperManager("root")).thenReturn(false);
        when(spaceService.isManager(space, "root")).thenReturn(false);
        List<Long> challengeManagers = Collections.singletonList(1l);

        boolean canEdit = Utils.canEditChallenge(challengeManagers, "1");
        assertFalse(canEdit);
        when(spaceService.isManager(space, "root")).thenReturn(true);
        canEdit = Utils.canEditChallenge(challengeManagers, "1");
        assertTrue(canEdit);
        when(spaceService.isSuperManager("root")).thenReturn(true);
        when(spaceService.isManager(space, "root")).thenReturn(false);
        canEdit = Utils.canEditChallenge(challengeManagers, "1");
        assertTrue(canEdit);

    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetSpaceById() {
        SpaceService spaceService = mock(SpaceService.class);
        PowerMockito.mockStatic(CommonsUtils.class);
        when(CommonsUtils.getService(SpaceService.class)).thenReturn(spaceService);

        Space space = new Space();
        space.setId("1");
        space.setPrettyName("test_space");
        space.setDisplayName("test space");
        space.setGroupId("/spaces/test_space");

        when(spaceService.getSpaceById("1")).thenReturn(space);
        when(spaceService.getSpaceById("2")).thenReturn(null);
        IdentityManager identityManager = mock(IdentityManager.class);

        when(CommonsUtils.getService(IdentityManager.class)).thenReturn(identityManager);

        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");
        when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);

        Space savedSpace = Utils.getSpaceById("");
        assertNull(savedSpace);
        savedSpace = Utils.getSpaceById("2");
        assertNull(savedSpace);
        savedSpace = Utils.getSpaceById("1");
        assertNotNull(savedSpace);
        assertEquals(space.getId(), savedSpace.getId());
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetUserById() {
        PowerMockito.mockStatic(CommonsUtils.class);
        IdentityManager identityManager = mock(IdentityManager.class);
        when(CommonsUtils.getService(IdentityManager.class)).thenReturn(identityManager);
        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");

        UserInfo userInfo = Utils.getUserById(null, null);
        assertNull(userInfo);

        when(identityManager.getIdentity("1")).thenReturn(null);
        userInfo = Utils.getUserById(1l, null);
        assertNull(userInfo);

        when(identityManager.getIdentity("1")).thenReturn(rootIdentity);
        userInfo = Utils.getUserById(1l, null);
        assertNotNull(userInfo);
        assertEquals(rootIdentity.getRemoteId(), userInfo.getRemoteId());
    }

    @PrepareForTest({CommonsUtils.class})
    @Test
    public void testGetManagersByIds() {
        PowerMockito.mockStatic(CommonsUtils.class);
        IdentityManager identityManager = mock(IdentityManager.class);
        when(CommonsUtils.getService(IdentityManager.class)).thenReturn(identityManager);
        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");
        when(identityManager.getIdentity("1")).thenReturn(rootIdentity);
        List<UserInfo> usersInfo = Utils.getManagersByIds(Collections.emptyList());
        assertEquals(0, usersInfo.size());
        usersInfo = Utils.getManagersByIds(Collections.singletonList(1l));
        assertEquals(1, usersInfo.size());
        assertEquals(rootIdentity.getRemoteId(), usersInfo.get(0).getRemoteId());
    }


    @Test
    public void testBuildActivityParams() {
      ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
      Map<String, String> activityParams = new HashMap<>();
      activityParams.put("id", "1");
      activityParams.put("titre", "titre");
      activityParams.put("description", null);
      activity.setTemplateParams(activityParams);
      Map<String, String> extraParams = new HashMap<>();
      extraParams.put("toAdd", "true");

      Utils.buildActivityParams(activity, extraParams);
      Map<String, String> buildedParams = activity.getTemplateParams();
      assertNotNull(buildedParams);
      assertEquals(3, buildedParams.size());
      assertFalse(buildedParams.containsKey("description"));
    }
  }

package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.search.ChallengeSearchConnector;
import org.exoplatform.addons.gamification.service.configuration.ChallengeServiceImpl;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.ListAccessImpl;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.xml.*", "org.xml.*"})
public class ChallengeServiceTest {

    private RuleStorage challengeStorage;

    private SpaceService spaceService;

    private ChallengeService challengeService;

    private InitParams params;

    private ListenerService listenerService;

    private ChallengeSearchConnector challengeSearchConnector ;

    @Before
    public void setUp() throws Exception { // NOSONAR
        challengeStorage = mock(RuleStorage.class);
        spaceService = mock(SpaceService.class);
        spaceService = mock(SpaceService.class);
        params = new InitParams();
        ValueParam p = new ValueParam();
        p.setName("challenge.creator.group");
        p.setValue("/platform/administrators");
        params.addParam(p);
        listenerService = mock(ListenerService.class);
        challengeSearchConnector = mock(ChallengeSearchConnector.class);

        challengeService = new ChallengeServiceImpl(challengeStorage, spaceService, params, listenerService, challengeSearchConnector);
    }

    @PrepareForTest({Utils.class})
    @Test
    public void testCreateChallenge() throws IllegalAccessException {
        // Given
        Challenge challenge = new Challenge(0,
                "new challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");
        Challenge challengeCreated = new Challenge(1l,
                "new challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");        // Given
        Challenge challengeSystem = new Challenge(0,
                "new system challenge",
                "system challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");
        Challenge challengeCreatedSystem = new Challenge(2l,
                "new challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");
        Identity rootIdentity = new Identity();
        rootIdentity.setId("1");
        rootIdentity.setProviderId("organization");
        rootIdentity.setRemoteId("root");
        PowerMockito.mockStatic(Utils.class);
        Space space = new Space();
        when(spaceService.getSpaceById("1")).thenReturn(space);
        when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challengeCreated);

        assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(null, "root"));
        assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(challengeCreated, "root"));

        when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(challenge, "root"));
        when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(rootIdentity);

        when(spaceService.isManager(space, "root")).thenReturn(false);
        assertThrows(IllegalAccessException.class, () -> challengeService.createChallenge(challenge, "root"));
        when(spaceService.isManager(space, "root")).thenReturn(true);

        Challenge savedChallenge = challengeService.createChallenge(challenge, "root");
        assertNotNull(savedChallenge);
        assertEquals(1l, savedChallenge.getId());
        when(challengeStorage.saveChallenge(challengeSystem, "SYSTEM")).thenReturn(challengeCreatedSystem);
        savedChallenge = challengeService.createChallenge(challengeSystem);
        assertNotNull(savedChallenge);
        assertEquals(2l, savedChallenge.getId());
    }

    @Test
    public void testUpdateChallenge() throws ObjectNotFoundException, IllegalAccessException {
        // Given
        Challenge challenge = new Challenge(1l,
                "update challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");
        Challenge challenge1 = new Challenge(1l,
                "new challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");

        Challenge challenge2 = new Challenge(1l,
                "update challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");
        Space space = new Space();
        when(spaceService.getSpaceById("1")).thenReturn(space);
        when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challenge2);

        assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallenge(null, "root"));
        assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallenge(new Challenge(), "root"));

        when(spaceService.isManager(space, "root")).thenReturn(false);
        assertThrows(IllegalAccessException.class, () -> challengeService.updateChallenge(challenge, "root"));
        when(spaceService.isManager(space, "root")).thenReturn(true);
        when(challengeStorage.getChallengeById(1l)).thenReturn(null);
        assertThrows(ObjectNotFoundException.class, () -> challengeService.updateChallenge(challenge, "root"));

        when(challengeStorage.getChallengeById(1l)).thenReturn(challenge);
        assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallenge(challenge, "root"));
        when(challengeStorage.getChallengeById(1l)).thenReturn(challenge1);

        Challenge challengeUpdated = challengeService.updateChallenge(challenge, "root");
        assertNotNull(challengeUpdated);
        assertEquals(1l, challenge1.getId());
        assertEquals("update challenge", challengeUpdated.getTitle());
    }

    @PrepareForTest({Utils.class})
    @Test
    public void testDeleteChallenge() throws ObjectNotFoundException, IllegalAccessException {
        // Given
        Challenge challenge = new Challenge(1l,
                "update challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");

        Space space = new Space();
        when(spaceService.getSpaceById("1")).thenReturn(space);
        when(spaceService.isManager(space, "root")).thenReturn(true);
        when(challengeStorage.getChallengeById(1l)).thenReturn(challenge);
        Challenge storedChallenge = challengeService.getChallengeById(1L, "root");
        assertNotNull(storedChallenge);
        assertEquals(1l, storedChallenge.getId());

        PowerMockito.mockStatic(Utils.class);

        // When
        assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(-1l, "root"));

        // When
        when(challengeStorage.getChallengeById(2l)).thenReturn(null);
        assertThrows(ObjectNotFoundException.class, () -> challengeService.deleteChallenge(2l, "root"));

        // When
        when(Utils.canEditChallenge(any(), any())).thenReturn(false);
        assertThrows(IllegalAccessException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));
        when(Utils.canEditChallenge(any(), any())).thenReturn(true);

        // When
        when(Utils.countAnnouncementsByChallenge(1l)).thenReturn(2l);
        assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

        // When
        when(Utils.parseSimpleDate(challenge.getEndDate())).thenReturn(Date.from(ZonedDateTime.now().plusDays(10).toInstant()));
        assertThrows(IllegalArgumentException.class, () -> challengeService.deleteChallenge(challenge.getId(), "root"));

        when(Utils.countAnnouncementsByChallenge(1l)).thenReturn(0l);
        when(Utils.parseSimpleDate(challenge.getEndDate())).thenReturn(Date.from(ZonedDateTime.now().plusDays(-10).toInstant()));
        challengeService.deleteChallenge(challenge.getId(), "root");
    }

    @Test
    public void testCanAddChallenge() {
        org.exoplatform.services.security.Identity currentIdentity = new org.exoplatform.services.security.Identity("root");
        ConversationState state = new ConversationState(currentIdentity);
        ConversationState.setCurrent(state);
        boolean canAddChallenge = challengeService.canAddChallenge();
        assertFalse(canAddChallenge);
        MembershipEntry membershipentry = new MembershipEntry("/platform/administrators", "*");
        List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();
        memberships.add(membershipentry);
        currentIdentity.setMemberships(memberships);
        state = new ConversationState(currentIdentity);
        ConversationState.setCurrent(state);
        canAddChallenge = challengeService.canAddChallenge();
        assertTrue(canAddChallenge);
    }

    @Test
    public void testGetAllChallengesByUser() {

        assertThrows(IllegalAccessException.class, () -> challengeService.getAllChallengesByUser(0, 10, ""));

        Space space = new Space();
        space.setId("1");
        space.setPrettyName("test_space");
        space.setDisplayName("test space");
        space.setGroupId("/spaces/test_space");

        RuleEntity challengeEntity = new RuleEntity();
        challengeEntity.setTitle("Challenge 1");
        challengeEntity.setDescription("description 1");
        challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
        challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
        challengeEntity.setId(1l);
        challengeEntity.setAudience(1l);
        challengeEntity.setManagers(Collections.emptyList());
        List<RuleEntity> challenges = new ArrayList<>();
        challenges.add(challengeEntity);

        try {
            when(spaceService.getMemberSpaces("root")).thenReturn(new ListAccessImpl(Space.class, Collections.emptyList()));

            List<Challenge> userChallenges = challengeService.getAllChallengesByUser(0, 10, "root");
            assertEquals(0, userChallenges.size());
            when(spaceService.getMemberSpaces("root")).thenReturn(new ListAccessImpl(Space.class, Collections.singletonList(space)));
            when(challengeStorage.findAllChallengesByUser(0, 10, Collections.singletonList(1l))).thenReturn(Collections.emptyList());
            userChallenges = challengeService.getAllChallengesByUser(0, 10, "root");
            assertEquals(0, userChallenges.size());
            when(challengeStorage.findAllChallengesByUser(0, 10, Collections.singletonList(1l))).thenReturn(challenges);
            userChallenges = challengeService.getAllChallengesByUser(0, 10, "root");
            assertEquals(1, userChallenges.size());

        } catch (Exception e) {
        }

    }

    @Test
    public void testGetChallengeById() {
        assertThrows(IllegalArgumentException.class, () -> challengeService.getChallengeById(0l, "root"));
        Challenge challenge = new Challenge(1l,
                "update challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");


        Space space = new Space();
        when(spaceService.getSpaceById("1")).thenReturn(space);
        when(spaceService.isManager(space, "root")).thenReturn(false);
        when(spaceService.isMember(space, "root")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> challengeService.getChallengeById(0l, "root"));

        when(spaceService.isManager(space, "root")).thenReturn(true);
        when(spaceService.isMember(space, "root")).thenReturn(true);
        when(challengeStorage.getChallengeById(1l)).thenReturn(null);
        try {
            Challenge savedChallenge = challengeService.getChallengeById(challenge.getId(), "root");
            assertNull(savedChallenge);
            when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
            savedChallenge = challengeService.getChallengeById(challenge.getId(), "root");
            assertNotNull(savedChallenge);
            assertEquals(challenge.getId(), savedChallenge.getId());

        } catch (IllegalAccessException e) {
        }
    }

    @Test
    public void testSearch() {

        Space space = new Space();
        space.setId("1");
        space.setPrettyName("test_space");
        space.setDisplayName("test space");
        space.setGroupId("/spaces/test_space");

        Challenge challenge = new Challenge(1l,
                "new challenge",
                "challenge description",
                1l,
                new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis() + 1).toString(),
                Collections.emptyList(),
                10L,
                "gamification");
        List<Challenge> challenges = Collections.singletonList(challenge);

        try {
            when(spaceService.getMemberSpaces("root")).thenReturn(new ListAccessImpl(Space.class, Collections.emptyList()));

            List<Challenge> userChallenges = challengeService.getAllChallengesByUser(0, 10, "root");
            assertEquals(0, userChallenges.size());
            when(spaceService.getMemberSpaces("root")).thenReturn(new ListAccessImpl(Space.class, Collections.singletonList(space)));
            List<Long> listIdSpace = Collections.singletonList(1l);
            when(challengeSearchConnector.search(listIdSpace.stream().map(String::valueOf).collect(Collectors.toSet()),"test", 0, 10)).thenReturn(Collections.emptyList());
            when(challengeSearchConnector.search(listIdSpace.stream().map(String::valueOf).collect(Collectors.toSet()),"challenge", 0, 10)).thenReturn(challenges);
            userChallenges = challengeService.search("test",0, 10, "root");
            assertEquals(0, userChallenges.size());
            userChallenges = challengeService.search("challenge",0, 10, "root");
            assertEquals(1, userChallenges.size());

        } catch (Exception e) {
        }

    }

    @Test
    public void testGetAllChallenges() {

      Challenge challenge = new Challenge(1l,
                                          "Challenge",
                                          "description",
                                          1l,
                                          new Date(System.currentTimeMillis()).toString(),
                                          new Date(System.currentTimeMillis() + 1).toString(),
                                          Collections.emptyList(),
                                          10L,
                                          "gamification");
      List<Challenge> challenges = new ArrayList<>();
      challenges.add(challenge);

      when(challengeStorage.getAllChallenges()).thenReturn(challenges);
      List<Challenge> userChallenges = challengeService.getAllChallenges();
      assertEquals(1, userChallenges.size());
    }

}

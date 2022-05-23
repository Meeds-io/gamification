package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.social.core.identity.model.Identity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class ChallengeStorageTest {
  private RuleDAO     challengeDAO;

  private RuleStorage challengeStorage;

  @Before
  public void setUp() throws Exception { // NOSONAR
    challengeDAO = mock(RuleDAO.class);
    challengeStorage = new RuleStorage(challengeDAO);
  }

  @PrepareForTest({ Utils.class, EntityMapper.class })
  @Test
  public void testSaveChallenge() {
    // Given
    String startDate = new Date(System.currentTimeMillis()).toString();
    String endDate = new Date(System.currentTimeMillis() + 1).toString();
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        startDate,
                                        endDate,
                                        Collections.emptyList(),
                                        10L,
                                        "gamification");
    long challengeId = 0;
    RuleEntity challengeEntity = new RuleEntity();
    challengeEntity.setDescription("challenge description");
    challengeEntity.setTitle("new challenge");
    challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity.setId(challengeId);

    RuleEntity newChallengeEntity = new RuleEntity();
    newChallengeEntity.setDescription("challenge description");
    newChallengeEntity.setTitle("new challenge");
    newChallengeEntity.setStartDate(challengeEntity.getStartDate());
    newChallengeEntity.setEndDate(challengeEntity.getEndDate());
    newChallengeEntity.setId(1l);

    when(challengeDAO.create(anyObject())).thenReturn(newChallengeEntity);
    PowerMockito.mockStatic(Utils.class);
    PowerMockito.mockStatic(EntityMapper.class);
    Identity identity = mock(Identity.class);
    when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    when(EntityMapper.toEntity(challenge)).thenReturn(challengeEntity);
    Challenge challengeFromEntity = new Challenge(1l,
                                                  "new challenge",
                                                  "challenge description",
                                                  1l,
                                                  startDate,
                                                  endDate,
                                                  Collections.emptyList(),
                                                  10L,
                                                  "gamification");
    when(EntityMapper.fromEntity(newChallengeEntity)).thenReturn(challengeFromEntity);
    // When
    Challenge createdChallenge = challengeStorage.saveChallenge(challenge, "root");

    // Then
    assertNotNull(createdChallenge);
    assertEquals(createdChallenge.getId(), 1l);
    challenge.setId(createdChallenge.getId());
    assertEquals(challenge.getId(), createdChallenge.getId());
    assertEquals(challenge.getTitle(), createdChallenge.getTitle());
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testGetChallengeById() {
    RuleEntity challengeEntity = new RuleEntity();
    challengeEntity.setTitle("Challenge");
    challengeEntity.setDescription("description");
    challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity.setId(1l);
    challengeEntity.setAudience(1l);
    challengeEntity.setManagers(Collections.emptyList());
    PowerMockito.mockStatic(EntityMapper.class);

    Challenge challenge = new Challenge(1l,
                                        "Challenge",
                                        "description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification");

    when(EntityMapper.fromEntity(challengeEntity)).thenReturn(challenge);
    when(challengeDAO.find(eq(1l))).thenReturn(challengeEntity);
    when(challengeDAO.find(eq(2l))).thenReturn(null);

    Challenge notExistingChallenge = challengeStorage.getChallengeById(2l);
    assertNull(notExistingChallenge);
    verify(challengeDAO, times(1)).find(anyLong());

    Challenge result = challengeStorage.getChallengeById(1l);
    assertNotNull(result);
    verify(challengeDAO, times(2)).find(anyLong());

  }

  @Test
  public void testFindAllChallengesByUser() {
    // Given
    List<Long> ids = new ArrayList<>(Arrays.asList(1l, 2l, 3l));
    List<RuleEntity> challengeEntities = new ArrayList<>();
    // challenge 1
    RuleEntity challengeEntity = new RuleEntity();
    challengeEntity.setTitle("Challenge");
    challengeEntity.setDescription("description");
    challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity.setId(1l);
    challengeEntity.setAudience(1l);
    challengeEntity.setManagers(Collections.emptyList());
    // challenge 2
    RuleEntity challengeEntity2 = new RuleEntity();
    challengeEntity2.setTitle("Challenge 2");
    challengeEntity2.setDescription("description 2");
    challengeEntity2.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity2.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity2.setId(2l);
    challengeEntity2.setAudience(2l);
    challengeEntity2.setManagers(Collections.emptyList());
    // challenge 3
    RuleEntity challengeEntity3 = new RuleEntity();
    challengeEntity3.setTitle("Challenge 3");
    challengeEntity3.setDescription("description 3");
    challengeEntity3.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity3.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity3.setId(3l);
    challengeEntity3.setAudience(3l);
    challengeEntity3.setManagers(Collections.emptyList());
    challengeEntities.add(challengeEntity);
    challengeEntities.add(challengeEntity2);
    challengeEntities.add(challengeEntity3);
    when(challengeDAO.findAllChallengesByUser(0, 3, ids)).thenReturn(challengeEntities);

    // When
    List<RuleEntity> challengeEntityList = challengeStorage.findAllChallengesByUser(0, 3, ids);

    assertEquals(3, challengeEntityList.size());
    assertNotNull(challengeEntities);

  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testDeleteChallenge() {
    RuleEntity challengeEntity = new RuleEntity();
    challengeEntity.setTitle("Challenge");
    challengeEntity.setDescription("description");
    challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity.setId(1l);
    challengeEntity.setAudience(1l);
    challengeEntity.setManagers(Collections.emptyList());
    Challenge challenge = new Challenge(1l,
                                        "Challenge",
                                        "description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        Collections.emptyList(),
                                        10L,
                                        "gamification");
    when(challengeDAO.find(1l)).thenReturn(challengeEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.fromEntity(challengeEntity)).thenReturn(challenge);
    when(EntityMapper.toEntity(challenge)).thenReturn(challengeEntity);

    Challenge result = challengeStorage.getChallengeById(1l);
    assertNotNull(result);
    challengeStorage.deleteChallenge(challenge);

    when(challengeDAO.find(1l)).thenReturn(null);
    // When
    result = challengeStorage.getChallengeById(1l);
    assertNull(result);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testFindAllChallenges() {
    // Given
    List<RuleEntity> challengeEntities = new ArrayList<>();
    // challenge 1
    RuleEntity challengeEntity = new RuleEntity();
    challengeEntity.setTitle("Challenge");
    challengeEntity.setDescription("description");
    challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity.setId(1l);
    challengeEntity.setAudience(1l);
    challengeEntity.setManagers(Collections.emptyList());
    // challenge 2
    RuleEntity challengeEntity2 = new RuleEntity();
    challengeEntity2.setTitle("Challenge 2");
    challengeEntity2.setDescription("description 2");
    challengeEntity2.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity2.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity2.setId(2l);
    challengeEntity2.setAudience(2l);
    challengeEntity2.setManagers(Collections.emptyList());

    Challenge challenge1 = new Challenge(1l,
                                         "Challenge 1",
                                         "description 1",
                                         1l,
                                         new Date(System.currentTimeMillis()).toString(),
                                         new Date(System.currentTimeMillis() + 1).toString(),
                                         Collections.emptyList(),
                                         10L,
                                         "gamification");
    Challenge challenge2 = new Challenge(1l,
                                         "Challenge 2",
                                         "description 2",
                                         1l,
                                         new Date(System.currentTimeMillis()).toString(),
                                         new Date(System.currentTimeMillis() + 1).toString(),
                                         Collections.emptyList(),
                                         10L,
                                         "gamification");

    List<Challenge> challenges = new ArrayList<>();
    challenges.add(challenge1);
    challenges.add(challenge2);
    when(challengeDAO.findAllChallenges(0, 10)).thenReturn(challengeEntities);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.fromChallengeEntities(challengeEntities)).thenReturn(challenges);

    // When
    List<Challenge> challengeList = challengeStorage.getAllChallenges(0, 10);
    assertEquals(2, challengeList.size());
    assertNotNull(challengeEntities);
  }
}

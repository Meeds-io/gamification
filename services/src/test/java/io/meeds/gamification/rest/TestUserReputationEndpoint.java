package io.meeds.gamification.rest;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;

import java.util.Map;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.security.Identity;

import io.meeds.gamification.entity.BadgeEntity;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.test.AbstractServiceTest;

public class TestUserReputationEndpoint extends AbstractServiceTest {

  private static final String ADMIN_USER = "root1";

  private Identity            adminAclIdentity;

  private String              adminIdentityId;

  protected Class<?> getComponentClass() {
    return UserReputationEndpoint.class;
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    startSessionAs("root1");
    registry(getComponentClass());
    ProgramEntity domainEntity = newDomain();
    newBadge(domainEntity.getId());
    newRealizationEntity("rule1", domainEntity.getId());
    newRealizationEntity("rule2", domainEntity.getId());
    newRealizationEntity("rule3", domainEntity.getId());

    adminAclIdentity = registerAdministratorUser(ADMIN_USER);
    adminIdentityId = identityManager.getOrCreateUserIdentity(ADMIN_USER).getId();
  }

  @Test
  public void testGetReputationStatus() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("reputation/status?username=root1"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testGetUserBadges() throws Exception {
    RuleDTO rule = newRuleDTO();

    RealizationDTO realization = realizationService.createRealizations(rule.getEvent().getTitle(),
                                                                       null,
                                                                       adminIdentityId,
                                                                       adminIdentityId,
                                                                       ACTIVITY_ID,
                                                                       ACTIVITY_OBJECT_TYPE)
                                                   .getFirst();
    assertNotNull(realization);
    assertTrue(realization.getId() > 0);

    BadgeEntity badge = newBadge("Badge1", realization.getProgram().getId());
    badge.setNeededScore((int) realization.getActionScore());

    ContainerResponse response = getResponse("GET", getURLResource("reputation/badges/" + adminIdentityId), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    String userBadgesString = (String) response.getEntity();
    JSONArray userBadges = new JSONArray(userBadgesString);
    assertNotNull(userBadges);
    assertTrue(userBadges.length() >= 1);
    assertTrue(userBadges.toList()
                         .stream()
                         .map(o -> (Map) o)
                         .map(m -> m.get("id"))
                         .anyMatch(id -> String.valueOf(id).equals(String.valueOf(badge.getId()))));

    response = getResponse("GET", getURLResource("reputation/badges"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetBadgeAvatarById() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("reputation/badge/5/avatar"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetAllBadges() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("reputation/won"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetOtherBadges() throws Exception {
    newBadgeWithScore();
    ContainerResponse response = getResponse("GET", getURLResource("reputation/otherBadges"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testStats() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("reputation/stats"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetAllOfBadges() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("reputation/AllofBadges"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }
}

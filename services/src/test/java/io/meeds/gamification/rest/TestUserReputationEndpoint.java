package io.meeds.gamification.rest;

import org.junit.Before;
import org.junit.Test;

import org.exoplatform.services.rest.impl.ContainerResponse;

import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.test.AbstractServiceTest;

public class TestUserReputationEndpoint extends AbstractServiceTest {

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
  }

  @Test
  public void testGetReputationStatus() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("reputation/status?username=root1"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testGetUserBadges() throws Exception {
    ContainerResponse response = getResponse("GET", getURLResource("reputation/badges/1"), null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

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

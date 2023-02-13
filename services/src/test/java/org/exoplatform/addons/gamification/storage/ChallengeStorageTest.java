/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

public class ChallengeStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveChallenge() {
    DomainEntity domain = newDomain(GAMIFICATION_DOMAIN);
    RuleEntity ruleEntity = newRule();
    Challenge challenge = new Challenge(ruleEntity.getId(),
                                        "new challenge",
                                        "challenge description",
                                        1L,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getId(),
                                        true);
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain.getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(0, challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size());
    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertEquals(1, challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size());
    assertEquals("new challenge", challenge.getTitle());
    challenge.setDescription("challenge description updated");
    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertEquals("challenge description updated", challenge.getDescription());
    assertEquals(1, challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size());
  }

  @Test
  public void testDeleteChallenge() throws ObjectNotFoundException {
    DomainEntity domain = newDomain(GAMIFICATION_DOMAIN);
    RuleEntity ruleEntity = newRule();
    Challenge challenge = new Challenge(ruleEntity.getId(),
                                        "new challenge",
                                        "challenge description",
                                        1L,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getId(),
                                        true);

    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertNotSame(0, challenge.getId());
    Challenge savedChallenge = challengeStorage.getChallengeById(challenge.getId());
    assertNotNull(savedChallenge);

    challengeStorage.deleteChallenge(challenge.getId(), "root");
    savedChallenge = challengeStorage.getChallengeById(challenge.getId());
    assertNull(savedChallenge);
  }

  @Test
  public void testGetChallengeById() {
    DomainEntity domain = newDomain(GAMIFICATION_DOMAIN);
    RuleEntity ruleEntity = newRule();
    Challenge challenge = new Challenge(ruleEntity.getId(),
                                        "new challenge",
                                        "challenge description",
                                        1L,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getId(),
                                        true);

    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertNotSame(0, challenge.getId());
    Challenge savedChallenge = challengeStorage.getChallengeById(challenge.getId());
    assertNotNull(savedChallenge);
  }

  @Test
  public void testFindChallengesIdsByFilter() {
    DomainEntity domain = newDomain(GAMIFICATION_DOMAIN);
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1L,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getId(),
                                        true);
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain.getId());
    filter.setSpaceIds(Collections.singletonList(1l));
    assertEquals(0, challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size());
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(1, challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size());
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(2, challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size());
    filter.setDomainId(100L);
    assertEquals(0, challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size());

  }

  @Test
  public void testCountChallengesByFilter() {
    DomainEntity domain = newDomain(GAMIFICATION_DOMAIN);
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1L,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getId(),
                                        true);
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain.getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(0, challengeStorage.countChallengesByFilter(filter));
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(1, challengeStorage.countChallengesByFilter(filter));
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(2, challengeStorage.countChallengesByFilter(filter));
    filter.setDomainId(100L);
    assertEquals(0, challengeStorage.countChallengesByFilter(filter));
  }

}

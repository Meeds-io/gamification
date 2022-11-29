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

import org.exoplatform.addons.gamification.entity.DomainEntity;
import org.exoplatform.addons.gamification.model.Challenge;
import org.exoplatform.addons.gamification.model.RuleFilter;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

public class ChallengeStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveChallenge() {
    DomainEntity domain = newDomain();
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getTitle(),
                                        true);
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain.getId());
    filter.setSpaceIds(Collections.singletonList(1l));
    assertEquals(challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size(), 0);
    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertEquals(challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size(), 1);
    assertEquals("new challenge", challenge.getTitle());
    challenge.setDescription("challenge description updated");
    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertEquals("challenge description updated", challenge.getDescription());
    assertEquals(challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size(), 1);
  }

  @Test
  public void testDeleteChallenge() throws ObjectNotFoundException {
    DomainEntity domain = newDomain();
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getTitle(),
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
    DomainEntity domain = newDomain();
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getTitle(),
                                        true);

    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertNotSame(0, challenge.getId());
    Challenge savedChallenge = challengeStorage.getChallengeById(challenge.getId());
    assertNotNull(savedChallenge);
  }

  @Test
  public void testFindChallengesIdsByFilter() {
    DomainEntity domain = newDomain();
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getTitle(),
                                        true);
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain.getId());
    filter.setSpaceIds(Collections.singletonList(1l));
    assertEquals(challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size(), 0);
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size(), 1);
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size(), 2);
    filter.setDomainId(100l);
    assertEquals(challengeStorage.findChallengesIdsByFilter(filter, 0, 10).size(), 0);

  }

  @Test
  public void testCountChallengesByFilter() {
    DomainEntity domain = newDomain();
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getTitle(),
                                        true);
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain.getId());
    filter.setSpaceIds(Collections.singletonList(1L));
    assertEquals(challengeStorage.countChallengesByFilter(filter), 0);
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(challengeStorage.countChallengesByFilter(filter), 1);
    challengeStorage.saveChallenge(challenge, "root");
    assertEquals(challengeStorage.countChallengesByFilter(filter), 2);
    filter.setDomainId(100L);
    assertEquals(challengeStorage.countChallengesByFilter(filter), 0);
  }

}

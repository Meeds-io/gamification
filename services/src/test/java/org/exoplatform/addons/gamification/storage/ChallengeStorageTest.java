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
    DomainEntity domain = newDomain();
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis())),
                                        Utils.toSimpleDateFormat(new Date(System.currentTimeMillis() + 1)),
                                        Collections.emptyList(),
                                        10L,
                                        domain.getTitle());
    RuleFilter filter = new RuleFilter();
    filter.setDomainId(domain.getId());
    filter.setSpaceIds(Collections.singletonList(1l));
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 0);
    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 1);
    assertEquals("new challenge", challenge.getTitle());
    challenge.setDescription("challenge description updated");
    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertEquals("challenge description updated", challenge.getDescription());
    assertEquals(ruleDAO.findRulesByFilter(filter, 0, 10).size(), 1);

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
                                        domain.getTitle());

    challenge = challengeStorage.saveChallenge(challenge, "root");
    assertNotNull(challenge);
    assertNotSame(0, challenge.getId());
    Challenge savedChallenge = challengeStorage.getChallengeById(challenge.getId());
    assertNotNull(savedChallenge);

    challengeStorage.deleteChallenge(challenge.getId());
    savedChallenge = challengeStorage.getChallengeById(challenge.getId());
    assertNull(savedChallenge);
  }

}

/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2022 Meeds Association contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.addons.gamification.storage;

import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.mapper.EntityMapper;
import org.exoplatform.commons.exception.ObjectNotFoundException;

public class ChallengeStorage {

  private RuleStorage ruleStorage;

  public ChallengeStorage(RuleStorage ruleStorage) {
    this.ruleStorage = ruleStorage;
  }

  public Challenge saveChallenge(Challenge challenge, String username) {
    RuleDTO ruleDTO = EntityMapper.fromChallengeToRule(challenge);
    if (ruleDTO.getId() == null) {
      ruleDTO.setCreatedBy(username);
      ruleDTO.setEvent(challenge.getTitle());
    } else {
      RuleDTO storedRuleDTO = ruleStorage.findRuleById(ruleDTO.getId());
      ruleDTO.setCreatedBy(storedRuleDTO.getCreatedBy());
      ruleDTO.setEvent(storedRuleDTO.getEvent());
      ruleDTO.setLastModifiedBy(username);
    }
    ruleDTO = ruleStorage.saveRule(ruleDTO);
    return EntityMapper.fromRuleToChallenge(ruleDTO);
  }

  public Challenge deleteChallenge(long challengeId) throws ObjectNotFoundException {
    RuleDTO ruleDTO = ruleStorage.deleteRule(challengeId, true);
    return EntityMapper.fromRuleToChallenge(ruleDTO);
  }

  public List<Challenge> findChallengesByFilter(RuleFilter ruleFilter, int offset, int limit) {
    List<RuleDTO> rules = ruleStorage.findRulesByFilter(ruleFilter, offset, limit);
    return rules.stream().map(EntityMapper::fromRuleToChallenge).collect(Collectors.toList());
  }

  public int countChallengesByFilter(RuleFilter challengeFilter) {
    return ruleStorage.countRulesByFilter(challengeFilter);
  }

  public Challenge getChallengeById(Long challengeId) {
    RuleDTO ruleDTO = ruleStorage.findRuleById(challengeId);
    return EntityMapper.fromRuleToChallenge(ruleDTO);
  }

  public void clearCache() {
    ruleStorage.clearCache();
  }

}

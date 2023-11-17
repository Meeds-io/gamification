/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.listener;

import java.util.List;

import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;

public class ProgramDeletedRuleListener extends Listener<ProgramDTO, String> {

  private static final Log LOG = ExoLogger.getLogger(ProgramDeletedRuleListener.class);

  protected ProgramService programService;

  protected RuleService    ruleService;

  public ProgramDeletedRuleListener(ProgramService programService,
                                    RuleService ruleService) {
    this.programService = programService;
    this.ruleService = ruleService;
  }

  @Override
  public void onEvent(Event<ProgramDTO, String> event) throws Exception { // NOSONAR
    ProgramDTO program = event.getSource();
    if (!program.isDeleted()) {
      LOG.warn("Program {} seems not deleted. Ignore marking Program rules as deleted as well.",
               program.getId());
      return;
    }
    RuleFilter ruleFilter = new RuleFilter(true);
    ruleFilter.setProgramId(program.getId());
    ruleFilter.setIncludeDeleted(true);
    List<RuleDTO> rules = ruleService.getRules(ruleFilter, 0, -1);
    rules.stream()
         .filter(r -> !r.isDeleted())
         .map(RuleDTO::getId)
         .forEach(ruleService::deleteRuleById);
  }
}

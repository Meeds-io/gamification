/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
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
package io.meeds.gamification.listener;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;

public class ProgramAutoDisableListener extends Listener<Object, String> {

  private static final Log LOG = ExoLogger.getLogger(ProgramAutoDisableListener.class);

  private ProgramService   programService;

  private RuleService      ruleService;

  public ProgramAutoDisableListener(ProgramService programService,
                                    RuleService ruleService) {
    this.programService = programService;
    this.ruleService = ruleService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<Object, String> event) throws Exception {
    Object object = event.getSource();
    try {
      boolean ruleDeleted = Utils.POST_DELETE_RULE_EVENT.equals(event.getEventName());
      RuleDTO rule = ruleDeleted ? (RuleDTO) object : ruleService.findRuleById((Long) object);
      if (rule != null) {
        ProgramDTO program = programService.getProgramById(rule.getProgramId());
        if (program != null
            && !program.isDeleted()
            && program.isEnabled()
            && ruleService.countActiveRules(program.getId()) == 0) {
          program.setEnabled(false);
          programService.updateProgram(program);
        }
      }
    } catch (Exception e) {
      LOG.warn("Error while automatically switching program status. Rule = {} ", object, e);
    }
  }

}

/*
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
package org.exoplatform.addons.gamification.listener.gamification.domain;

import static org.exoplatform.addons.gamification.service.configuration.ProgramService.GAMIFICATION_DOMAIN_DELETE_LISTENER;
import static org.exoplatform.addons.gamification.service.configuration.ProgramService.GAMIFICATION_DOMAIN_DISABLE_LISTENER;
import static org.exoplatform.addons.gamification.service.configuration.ProgramService.GAMIFICATION_DOMAIN_ENABLE_LISTENER;

import java.util.List;

import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class GamificationDomainListener extends Listener<ProgramDTO, String> {

  private static final Log LOG = ExoLogger.getLogger(GamificationDomainListener.class);

  protected RuleService    ruleService;

  protected BadgeService   badgeService;

  public GamificationDomainListener(RuleService ruleService, BadgeService badgeService) {
    this.ruleService = ruleService;
    this.badgeService = badgeService;
  }

  @Override
  public void onEvent(Event<ProgramDTO, String> event) throws Exception {
    ProgramDTO program = event.getSource();
    String action = event.getEventName();
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setDomainId(program.getId());
    List<RuleDTO> rules = ruleService.getRules(ruleFilter, 0, -1);
    List<BadgeDTO> badges = badgeService.findBadgesByDomain(program.getId());
    switch (action) {
    case GAMIFICATION_DOMAIN_DELETE_LISTENER:
      for (RuleDTO rule : rules) {
        rule.setProgram(null);
        rule.setEnabled(false);
        ruleService.updateRule(rule);
      }
      for (BadgeDTO badge : badges) {
        badge.setProgram(null);
        badge.setEnabled(false);
        badgeService.updateBadge(badge);
      }
      break;
    case GAMIFICATION_DOMAIN_DISABLE_LISTENER:
      for (RuleDTO rule : rules) {
        rule.setEnabled(false);
        ruleService.updateRule(rule);
      }
      for (BadgeDTO badge : badges) {
        badge.setEnabled(false);
        badgeService.updateBadge(badge);
      }
      break;
    case GAMIFICATION_DOMAIN_ENABLE_LISTENER:
      for (RuleDTO rule : rules) {
        rule.setEnabled(true);
        ruleService.updateRule(rule);
      }
      for (BadgeDTO badge : badges) {
        badge.setEnabled(true);
        badgeService.updateBadge(badge);
      }
      break;
    default:
      LOG.warn("Unknown  triggered action name {}", action);
      break;
    }
  }
}

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

import java.util.List;

import liquibase.pro.packaged.R;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class GamificationDomainListener extends Listener<DomainDTO, String> {

  private static final Log LOG = ExoLogger.getLogger(GamificationDomainListener.class);

  protected RuleService    ruleService;

  protected BadgeService   badgeService;

  public GamificationDomainListener(RuleService ruleService, BadgeService badgeService) {
    this.ruleService = ruleService;
    this.badgeService = badgeService;
  }

  @Override
  public void onEvent(Event<DomainDTO, String> event) throws Exception {
    LOG.info("Update Rules related to the edited domain");
    DomainDTO domain = event.getSource();
    String action = (String) event.getData();
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setDomainId(domain.getId());
    List<RuleDTO> rules = ruleService.getRulesByFilter(ruleFilter, 0, -1);
    List<BadgeDTO> badges = badgeService.findBadgesByDomain(domain.getTitle());
    if (action.equals("delete")) {
      for (RuleDTO rule : rules) {
        rule.setDomainDTO(null);
        rule.setArea("");
        rule.setEnabled(false);
        ruleService.updateRule(rule, Utils.getCurrentUser());
      }
      for (BadgeDTO badge : badges) {
        badge.setDomainDTO(null);
        badge.setDomain("");
        badge.setEnabled(false);
        badgeService.updateBadge(badge);
      }
    }
    if (action.equals("disable")) {
      for (RuleDTO rule : rules) {
        rule.setEnabled(false);
        ruleService.updateRule(rule, Utils.getCurrentUser());
      }
      for (BadgeDTO badge : badges) {
        badge.setEnabled(false);
        badgeService.updateBadge(badge);
      }
    }
    if (action.equals("enable")) {
      for (RuleDTO rule : rules) {
        rule.setEnabled(true);
        ruleService.updateRule(rule, Utils.getCurrentUser());
      }
      for (BadgeDTO badge : badges) {
        badge.setEnabled(true);
        badgeService.updateBadge(badge);
      }
    }

  }
}

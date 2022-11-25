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
package org.exoplatform.addons.gamification.listener.gamification.rule;

import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class GamificationRuleListener extends Listener<RuleDTO, String> {

  private static final Log LOG = ExoLogger.getLogger(GamificationRuleListener.class);

  protected RuleService    ruleService;

  protected DomainService domainService;

  public GamificationRuleListener(RuleService ruleService, DomainService domainService) {
    this.ruleService = ruleService;
    this.domainService = domainService;
  }

  @Override
  public void onEvent(Event<RuleDTO, String> event) throws Exception {
    LOG.info("Update domain related to the edited rule");
    RuleDTO rule = event.getSource();
    String action = event.getData();
  
    if (action.equals("delete") || action.equals("disable")) {
      DomainDTO domain = domainService.getDomainById(rule.getDomainDTO().getId());
      long newBudget = domain.getBudget() - rule.getScore();
      domain.setBudget(newBudget);
      domainService.updateDomain(domain);
    }

    if (action.equals("enable") || action.equals("create")) {
      DomainDTO domain = domainService.getDomainById(rule.getDomainDTO().getId());
      long newBudget = domain.getBudget() + rule.getScore();
      domain.setBudget(newBudget);
      domainService.updateDomain(domain);
    }

  }
}

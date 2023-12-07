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
package io.meeds.gamification.listener;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.search.RuleIndexingServiceConnector;
import io.meeds.gamification.service.RuleService;

@Asynchronous
public class RulePublisherListener extends Listener<Object, String> {

  private static final Log LOG = ExoLogger.getLogger(RulePublisherListener.class);

  private IndexingService  indexingService;

  private RuleService      ruleService;

  public RulePublisherListener(IndexingService indexingService,
                         RuleService ruleService) {
    this.indexingService = indexingService;
    this.ruleService = ruleService;
  }

  @Override
  @ExoTransactional
  public void onEvent(Event<Object, String> event) throws Exception {
    Object object = event.getSource();
    Long ruleId = getRuleId(object);
    RuleDTO rule = ruleService.findRuleById(ruleId);
    if (rule == null) {
      LOG.debug("Notifying remove index of rule with id={}", ruleId);
      indexingService.unindex(RuleIndexingServiceConnector.INDEX, String.valueOf(ruleId));
    } else {
      LOG.debug("Notifying reindex rule with id={}", ruleId);
      indexingService.reindex(RuleIndexingServiceConnector.INDEX, String.valueOf(ruleId));
    }
  }

  private Long getRuleId(Object object) {
    if (object instanceof Long id) {
      return id;
    } else if (object instanceof RuleDTO rule) {
      return rule.getId();
    }
    return null;
  }

}

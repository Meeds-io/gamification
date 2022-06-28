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
package org.exoplatform.addons.gamification.listener.es;

import org.exoplatform.addons.gamification.connector.RuleIndexingServiceConnector;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

@Asynchronous
public class RulesESListener extends Listener<Object, Long> {
  private static final Log      LOG = ExoLogger.getLogger(RulesESListener.class);

  private final PortalContainer container;

  private final IndexingService indexingService;

  public RulesESListener(PortalContainer container,
                         IndexingService indexingService) {
    this.container = container;
    this.indexingService = indexingService;
  }

  @Override
  public void onEvent(Event<Object, Long> event) throws Exception {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    Long ruleId = event.getData();
    try {
      if (Utils.POST_DELETE_RULE_EVENT.equals(event.getEventName())) {
        LOG.debug("Notifying unindexing service for rule with id={}", ruleId);
        indexingService.unindex(RuleIndexingServiceConnector.INDEX, String.valueOf(ruleId));
      } else {
        LOG.debug("Notifying indexing service for rule with id={}", ruleId);
        indexingService.reindex(RuleIndexingServiceConnector.INDEX, String.valueOf(ruleId));
      }
    } finally {
      RequestLifeCycle.end();
    }
  }

}
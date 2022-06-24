package org.exoplatform.addons.gamification.listener.es;

import org.exoplatform.addons.gamification.connector.RuleIndexingServiceConnector;
import org.exoplatform.addons.gamification.service.ChallengeService;
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
package org.exoplatform.addons.gamification.listener.es;

import org.exoplatform.addons.gamification.connector.ChallengesIndexingServiceConnector;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class RulesESListener extends Listener<ChallengeService, Long> {
  private static final Log      LOG = ExoLogger.getLogger(RulesESListener.class);

  private final PortalContainer container;

  private final IndexingService indexingService;


  public RulesESListener(PortalContainer container, IndexingService indexingService) {
    this.container = container;
    this.indexingService = indexingService;
  }

  @Override
  public void onEvent(Event<ChallengeService, Long> event) throws Exception {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);

    ChallengeService challengeService = event.getSource();
    Long challengeId = event.getData();
    try {
      if (indexingService != null) {
        if (!Utils.POST_DELETE_RULE_EVENT.equals(event.getEventName())) {
          Challenge challenge = challengeService.getChallengeById(challengeId, Utils.getCurrentUser());
          if (challenge == null) {
            return;
          }
        }
        if (Utils.POST_CREATE_RULE_EVENT.equals(event.getEventName())) {
          reindexChallenge(challengeId, "create rule");
        } else if (Utils.POST_UPDATE_RULE_EVENT.equals(event.getEventName())) {
          reindexChallenge(challengeId, "update rule");
        } else if (Utils.POST_DELETE_RULE_EVENT.equals(event.getEventName())) {
          unindexChallenge(challengeId, "delete rule");
        }
      }
    } finally {
      RequestLifeCycle.end();
    }

  }

  private void reindexChallenge(long challengeId, String cause) {
    LOG.debug("Notifying indexing service for challenge with id={}. Cause: {}", challengeId, cause);
    indexingService.reindex(ChallengesIndexingServiceConnector.INDEX, String.valueOf(challengeId));
  }

  private void unindexChallenge(long challengeId, String cause) {
    LOG.debug("Notifying unindexing service for challenge with id={}. Cause: {}", challengeId, cause);
    indexingService.unindex(ChallengesIndexingServiceConnector.INDEX, String.valueOf(challengeId));
  }

}

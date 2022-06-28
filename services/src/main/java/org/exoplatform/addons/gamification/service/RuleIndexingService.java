package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.connector.RuleIndexingServiceConnector;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.exoplatform.commons.search.index.IndexingService;

import java.util.List;

public class RuleIndexingService implements Startable {

  private static final Log      log = ExoLogger.getLogger(RuleIndexingService.class);

  private final IndexingService indexingService;

  private RuleService           ruleService;

  private static int            ruleIndexingCount;

  private final PortalContainer container;

  public RuleIndexingService(IndexingService indexingService, RuleService ruleService, PortalContainer container) {
    this.indexingService = indexingService;
    this.ruleService = ruleService;
    this.container = container;
  }

  @Override
  public void start() {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    log.info("Start indexing old rules");
    long startupTime = System.currentTimeMillis();
    try {
      List<RuleDTO> rules = ruleService.findAllRules();
      for (RuleDTO ruleDTO : rules) {
        indexingService.reindex(RuleIndexingServiceConnector.INDEX, String.valueOf(ruleDTO.getId()));
        ruleIndexingCount++;
        log.info("rule with id {} has been indexed", ruleDTO.getId());
      }
      log.info("End indexing of '{}' old rules. It took {} ms",
               ruleIndexingCount,
               (System.currentTimeMillis() - startupTime));
    } catch (Exception e) {
      log.error("An unexpected error occurs when indexing old rules", e);
    } finally {
      RequestLifeCycle.end();

    }
  }

  @Override
  public void stop() {

  }
}

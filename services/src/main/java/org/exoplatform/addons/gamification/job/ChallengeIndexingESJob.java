package org.exoplatform.addons.gamification.job;

import org.exoplatform.addons.gamification.connector.ChallengesIndexingServiceConnector;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class ChallengeIndexingESJob implements Job {
  private static final Log log = ExoLogger.getLogger(ChallengeIndexingESJob.class.getName());

  private IndexingService  indexingService;

  private ChallengeService challengeService;

  private ExoContainer     container;

  public ChallengeIndexingESJob() {
    this.container = PortalContainer.getInstance();
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    log.info("Start indexing old challenges");
    long startupTime = System.currentTimeMillis();
    RequestLifeCycle.begin(container);
    try {
      List<Challenge> challenges = getChallengeService().getAllChallenges();
      for (Challenge challenge : challenges) {
        getIndexingService().index(ChallengesIndexingServiceConnector.INDEX, String.valueOf(challenge.getId()));
      }
      log.info("End indexing of '{}' old challenges. It took {} ms",
               challenges.size(),
               (System.currentTimeMillis() - startupTime));
    } catch (Exception e) {
      if (log.isErrorEnabled()) {
        log.error("An unexpected error occurs when indexing old challenges", e);
      }
    } finally {
      RequestLifeCycle.end();
    }

  }

  private ChallengeService getChallengeService() {
    if (challengeService == null) {
      challengeService = CommonsUtils.getService(ChallengeService.class);
    }
    return challengeService;
  }

  private IndexingService getIndexingService() {
    if (indexingService == null) {
      indexingService = CommonsUtils.getService(IndexingService.class);
    }
    return indexingService;
  }
}

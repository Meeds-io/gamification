package org.exoplatform.addons.gamification.connector;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.commons.search.domain.Document;
import org.exoplatform.commons.search.index.impl.ElasticIndexingServiceConnector;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.*;
import java.util.stream.Collectors;

public class ChallengesIndexingServiceConnector extends ElasticIndexingServiceConnector {

  public static final String INDEX = "challenges";

  private static final Log   LOG   = ExoLogger.getLogger(ChallengesIndexingServiceConnector.class);

  private ChallengeService   challengeService;

  public ChallengesIndexingServiceConnector(InitParams initParams, ChallengeService challengeService) {
    super(initParams);
    this.challengeService = challengeService;
  }

  @Override
  public String getConnectorName() {
    return INDEX;
  }

  @Override
  public Document create(String id) {
    return getDocument(id);
  }

  @Override
  public Document update(String id) {
    return getDocument(id);
  }

  @Override
  public List<String> getAllIds(int offset, int limit) {
    throw new UnsupportedOperationException();
  }

  private Document getDocument(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("id is mandatory");
    }
    LOG.debug("Index document for challenge id={}", id);

    Challenge challenge = challengeService.getChallengeById(Long.valueOf(id));

    if (challenge == null) {
      throw new IllegalStateException("challenge with id '" + id + "' not found");
    }

    Map<String, String> fields = new HashMap<>();
    fields.put("id", Long.toString(challenge.getId()));
    fields.put("title", challenge.getTitle());
    fields.put("description", challenge.getDescription());
    fields.put("audience", String.valueOf(challenge.getAudience()));
    fields.put("program", challenge.getProgram());
    fields.put("points", String.valueOf(challenge.getPoints()));
    fields.put("startDate", challenge.getStartDate());
    fields.put("endDate", challenge.getEndDate());
    String managers = "";
    for (Long manager : challenge.getManagers()) {
      managers = managers + manager + "#";
    }
    fields.put("managers", managers);

    Set<String> challengeManagers = challenge.getManagers().stream().map(String::valueOf).collect(Collectors.toSet());

    return new Document(id, null, new Date(System.currentTimeMillis()), challengeManagers, fields);
  }
}

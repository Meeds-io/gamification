package org.exoplatform.addons.gamification.listener;

import org.exoplatform.addons.gamification.listener.es.RulesESListener;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.services.listener.Event;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RulesESListenerTest extends AbstractServiceTest {

  @Mock
  private IndexingService indexingService;

  @Test
  public void testIndexingRule() throws Exception {
    RuleDTO rule = newRuleDTO();
    RulesESListener rulesESListener = new RulesESListener(getContainer(), indexingService, ruleService);

    Event event = new Event<>(Utils.POST_CREATE_RULE_EVENT, null, rule.getId());
    rulesESListener.onEvent(event);
    verify(indexingService, times(1)).reindex(anyString(), anyString());
    verify(indexingService, times(0)).unindex(anyString(), anyString());

    event = new Event<>(Utils.POST_DELETE_RULE_EVENT, this, rule.getId());
    rulesESListener.onEvent(event);
    verify(indexingService, times(1)).reindex(anyString(), anyString());
    verify(indexingService, times(1)).unindex(anyString(), anyString());
  }
}

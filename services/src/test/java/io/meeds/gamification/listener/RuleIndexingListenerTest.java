/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

package io.meeds.gamification.listener;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.services.listener.Event;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class RuleIndexingListenerTest extends AbstractServiceTest { // NOSONAR

  @Mock
  private IndexingService indexingService;

  @Test
  public void testIndexingRule() throws Exception {
    RuleDTO rule = newRuleDTO();
    RuleIndexingListener ruleIndexingListener = new RuleIndexingListener(indexingService, ruleService);

    Event<Object, String> event = new Event<>(Utils.POST_CREATE_RULE_EVENT, rule.getId(), null);
    ruleIndexingListener.onEvent(event);
    verify(indexingService, times(1)).reindex(anyString(), anyString());
    verify(indexingService, times(0)).unindex(anyString(), anyString());

    event = new Event<>(Utils.POST_DELETE_RULE_EVENT, rule.getId(), null);
    ruleIndexingListener.onEvent(event);
    verify(indexingService, times(2)).reindex(anyString(), anyString());
    verify(indexingService, times(0)).unindex(anyString(), anyString());

    event = new Event<>(Utils.POST_DELETE_RULE_EVENT, 22554866l, null);
    ruleIndexingListener.onEvent(event);
    verify(indexingService, times(2)).reindex(anyString(), anyString());
    verify(indexingService, times(1)).unindex(anyString(), anyString());
  }

}

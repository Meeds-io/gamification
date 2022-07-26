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
package org.exoplatform.addons.gamification.search;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.DateFilterType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RuleSearchConnectorTest extends AbstractServiceTest {

  private static final String    ES_INDEX      = "rule_alias";

  public static final String     FAKE_ES_QUERY = "{offset: @offset@, limit: @limit@, term1: @term_query@, term2: @term_query@}";

  private ConfigurationManager   configurationManager;

  private ElasticSearchingClient client;

  private String                 searchResult  = null;

  private RuleSearchConnector    ruleSearchConnector;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    configurationManager = mock(ConfigurationManager.class);
    client = mock(ElasticSearchingClient.class);
    try {
      searchResult = IOUtil.getStreamContentAsString(getClass().getClassLoader()
                                                               .getResourceAsStream("rule-search-result.json"));
      Mockito.reset(configurationManager);
      when(configurationManager.getInputStream("FILE_PATH")).thenReturn(new ByteArrayInputStream(FAKE_ES_QUERY.getBytes()));
    } catch (Exception e) {
      throw new IllegalStateException("Error retrieving ES Query content", e);
    }
    ruleSearchConnector = new RuleSearchConnector(configurationManager, client,identityManager, getParams());
  }

  private InitParams getParams() {
    InitParams params = new InitParams();
    PropertiesParam propertiesParam = new PropertiesParam();
    propertiesParam.setName("constructor.params");
    propertiesParam.setProperty("index", ES_INDEX);

    ValueParam valueParam = new ValueParam();
    valueParam.setName("query.file.path");
    valueParam.setValue("FILE_PATH");

    params.addParameter(propertiesParam);
    params.addParameter(valueParam);
    return params;
  }

  @Test
  public void testSearch() {
    String term = "rule";
    newDomainDTO();
    RuleFilter filter = new RuleFilter();
    List<Long> listIdSpace = Collections.singletonList(1l);
    List<Long> listIdSpaceEmpty = Collections.emptyList();

    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter,-1, 10));
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter, 0, -10));
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter, 0, 10));
    filter.setTerm("test");
    filter.setSpaceIds(listIdSpaceEmpty);
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter, 0, 10));
    filter.setSpaceIds(listIdSpaceEmpty);
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter, 0, 10));
    filter.setSpaceIds(listIdSpace);
    String expectedESQuery = FAKE_ES_QUERY.replaceAll("@term_query@", term).replaceAll("@offset@", "0").replaceAll("@limit@", "10");
    String unexpectedESQuery = FAKE_ES_QUERY.replaceAll("@term_query@", "test").replaceAll("@offset@", "0").replaceAll("@limit@", "10");

    when(client.sendRequest(expectedESQuery, ES_INDEX)).thenReturn(searchResult);
    when(client.sendRequest(unexpectedESQuery, ES_INDEX)).thenReturn("{}");

    List<RuleEntity> rules = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(rules);
    assertEquals(0, rules.size());

    filter.setTerm(term);
    rules =  ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(rules);
    assertEquals(1, rules.size());
  }

  @Test
  public void testCount() {
    String term = "rule";
    newDomainDTO();
    RuleFilter filter = new RuleFilter();
    List<Long> listIdSpace = Collections.singletonList(1l);
    List<Long> listIdSpaceEmpty = Collections.emptyList();

    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.count(filter));
    filter.setTerm("test");
    filter.setSpaceIds(listIdSpaceEmpty);
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.count(filter));
    filter.setSpaceIds(listIdSpace);

    String expectedESQuery = FAKE_ES_QUERY.replaceAll("@term_query@", term).replaceAll("@offset@", "0").replaceAll("@limit@", "0");
    String unexpectedESQuery = FAKE_ES_QUERY.replaceAll("@term_query@", "test").replaceAll("@offset@", "0").replaceAll("@limit@", "0");

    when(client.sendRequest(expectedESQuery, ES_INDEX)).thenReturn(searchResult);
    when(client.sendRequest(unexpectedESQuery, ES_INDEX)).thenReturn("{}");

    int count = ruleSearchConnector.count(filter);
    assertEquals(0, count);

    filter.setTerm(term);
    count = ruleSearchConnector.count(filter);
    assertEquals(1, count);
  }

  @Test
  public void testSearchWithDateFilter() {
    String term = DateFilterType.ALL.name();
    newDomainDTO();
    RuleFilter filter = new RuleFilter();
    List<Long> listIdSpace = Collections.singletonList(1l);
    filter.setTerm("test");
    filter.setSpaceIds(listIdSpace);
    String expectedESQuery = FAKE_ES_QUERY.replaceAll("@term_query@", term)
                                          .replaceAll("@offset@", "0")
                                          .replaceAll("@limit@", "10");
    String unexpectedESQueryNotStarted = FAKE_ES_QUERY.replaceAll("@term_query@", DateFilterType.NOT_STARTED.name() + "~1")
                                             .replaceAll("@offset@", "0")
                                             .replaceAll("@limit@", "10");
    String unexpectedESQueryStarted = FAKE_ES_QUERY.replaceAll("@term_query@", DateFilterType.STARTED.name() + "~1")
                                             .replaceAll("@offset@", "0")
                                             .replaceAll("@limit@", "10");
    String unexpectedESQueryEnded = FAKE_ES_QUERY.replaceAll("@term_query@", DateFilterType.ENDED.name() + "~1")
                                             .replaceAll("@offset@", "0")
                                             .replaceAll("@limit@", "10");

    when(client.sendRequest(expectedESQuery, ES_INDEX)).thenReturn(searchResult);
    when(client.sendRequest(unexpectedESQueryNotStarted, ES_INDEX)).thenReturn("{}");
    when(client.sendRequest(unexpectedESQueryStarted, ES_INDEX)).thenReturn("{}");
    when(client.sendRequest(unexpectedESQueryEnded, ES_INDEX)).thenReturn("{}");

    filter.setDateFilterType(DateFilterType.STARTED);
    filter.setTerm(DateFilterType.STARTED.name());
    List<RuleEntity> rules = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(rules);
    assertEquals(0, rules.size());

    filter.setDateFilterType(DateFilterType.NOT_STARTED);
    filter.setTerm(DateFilterType.NOT_STARTED.name());
    rules = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(rules);
    assertEquals(0, rules.size());

    filter.setDateFilterType(DateFilterType.ENDED);
    filter.setTerm(DateFilterType.ENDED.name());
    rules = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(rules);
    assertEquals(0, rules.size());

    filter.setTerm(term);
    rules = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(rules);
    assertEquals(1, rules.size());
  }
}

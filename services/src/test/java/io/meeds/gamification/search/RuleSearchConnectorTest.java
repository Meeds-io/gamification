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
package io.meeds.gamification.search;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;

import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

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
    ruleSearchConnector = new RuleSearchConnector(configurationManager, client, getParams());
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
    newProgram();
    RuleFilter filter = new RuleFilter();
    List<Long> listIdSpace = Collections.singletonList(1l);
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter, -1, 10));
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter, 0, -10));
    assertThrows(IllegalArgumentException.class, () -> ruleSearchConnector.search(filter, 0, 10));
    filter.setTerm("test");
    filter.setLocale(Locale.ENGLISH);
    filter.setSpaceIds(listIdSpace);
    String expectedESQuery =
                           FAKE_ES_QUERY.replaceAll("@term_query@", term).replaceAll("@offset@", "0").replaceAll("@limit@", "10");
    String unexpectedESQuery = FAKE_ES_QUERY.replaceAll("@term_query@", "test")
                                            .replaceAll("@offset@", "0")
                                            .replaceAll("@limit@", "10");

    when(client.sendRequest(expectedESQuery, ES_INDEX)).thenReturn(searchResult);
    when(client.sendRequest(unexpectedESQuery, ES_INDEX)).thenReturn("{}");

    List<Long> ruleIds = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(ruleIds);
    assertEquals(0, ruleIds.size());

    filter.setTerm(term);
    ruleIds = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(ruleIds);
    assertEquals(1, ruleIds.size());
  }

  @Test
  public void testSearchWithDateFilter() {
    String term = "ALL";
    newProgram();
    RuleFilter filter = new RuleFilter();
    List<Long> listIdSpace = Collections.singletonList(1l);
    filter.setLocale(Locale.ENGLISH);
    filter.setSpaceIds(listIdSpace);
    String expectedESQuery = FAKE_ES_QUERY.replaceAll("@term_query@", StringUtils.lowerCase(Utils.removeSpecialCharacters(term)))
                                          .replaceAll("@offset@", "0")
                                          .replaceAll("@limit@", "10");

    when(client.sendRequest(expectedESQuery, ES_INDEX)).thenReturn(searchResult);
    when(client.sendRequest(argThat(query -> !StringUtils.equals(query, expectedESQuery)),
                            eq(ES_INDEX))).thenReturn("{}");

    filter.setTerm("NOT_STARTED");
    List<Long> ruleIds = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(ruleIds);
    assertEquals(0, ruleIds.size());

    filter.setTerm("ENDED");
    ruleIds = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(ruleIds);
    assertEquals(0, ruleIds.size());

    filter.setTerm(term);
    ruleIds = ruleSearchConnector.search(filter, 0, 10);
    assertNotNull(ruleIds);
    assertEquals(1, ruleIds.size());
  }
}

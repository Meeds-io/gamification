/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;
import org.junit.Before;
import org.mockito.Mockito;

public class RuleSearchConnectorTest extends AbstractServiceTest {

  private static final String    ES_INDEX      = "challenges_alias";

  public static final String     FAKE_ES_QUERY = "{offset: @offset@, limit: @limit@, term1: @term@, term2: @term@}";

  private ConfigurationManager   configurationManager;

  private ElasticSearchingClient client;

  private String                 searchResult  = null;

  private RuleSearchConnector    challengeSearchConnector;

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
    challengeSearchConnector = new RuleSearchConnector(configurationManager, client, getParams());
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
}

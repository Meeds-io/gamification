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

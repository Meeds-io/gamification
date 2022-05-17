package org.exoplatform.addons.gamification.search;

import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChallengeSearchConnectorTest extends AbstractServiceTest {

  private static final String      ES_INDEX      = "challenges_alias";

  public static final String       FAKE_ES_QUERY = "{offset: @offset@, limit: @limit@, term1: @term@, term2: @term@}";

  private ConfigurationManager     configurationManager;

  private ElasticSearchingClient   client;

  String                           searchResult  = null;

  private ChallengeSearchConnector challengeSearchConnector;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    configurationManager = mock(ConfigurationManager.class);
    client = mock(ElasticSearchingClient.class);
    try {
      searchResult = IOUtil.getStreamContentAsString(getClass().getClassLoader()
                                                               .getResourceAsStream("challenges-search-result.json"));
      Mockito.reset(configurationManager);
      when(configurationManager.getInputStream("FILE_PATH")).thenReturn(new ByteArrayInputStream(FAKE_ES_QUERY.getBytes()));
    } catch (Exception e) {
      throw new IllegalStateException("Error retrieving ES Query content", e);
    }
    challengeSearchConnector = new ChallengeSearchConnector(configurationManager, client, getParams());
  }

  @Test
  public void testSearch() {
    String term = "challenge";

    Set<String> listIdSpace = Collections.singletonList(1l).stream().map(String::valueOf).collect(Collectors.toSet());
    Set<String> listIdSpaceEmpty = Collections.emptyList().stream().map(String::valueOf).collect(Collectors.toSet());
    assertThrows(IllegalArgumentException.class, () -> challengeSearchConnector.search(listIdSpace, term, -1, 10));
    assertThrows(IllegalArgumentException.class, () -> challengeSearchConnector.search(listIdSpace, term, 1, -10));
    assertThrows(IllegalArgumentException.class, () -> challengeSearchConnector.search(listIdSpaceEmpty, term, 1, 10));
    assertThrows(IllegalArgumentException.class, () -> challengeSearchConnector.search(listIdSpace, "", -1, 10));

    String expectedESQuery = FAKE_ES_QUERY.replaceAll("@term@", term).replaceAll("@offset@", "0").replaceAll("@limit@", "10");
    String unexpectedESQuery = FAKE_ES_QUERY.replaceAll("@term@", "test").replaceAll("@offset@", "0").replaceAll("@limit@", "10");

    when(client.sendRequest(expectedESQuery, ES_INDEX)).thenReturn(searchResult);
    when(client.sendRequest(unexpectedESQuery, ES_INDEX)).thenReturn("{}");

    List<Challenge> challenges = challengeSearchConnector.search(listIdSpace, "test", 0, 10);
    assertNotNull(challenges);
    assertEquals(0, challenges.size());

    challenges = challengeSearchConnector.search(listIdSpace, term, 0, 10);
    assertNotNull(challenges);
    assertEquals(1, challenges.size());
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

package org.exoplatform.addons.gamification.search;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.ChallengeSearchEntity;
import org.exoplatform.commons.search.es.ElasticSearchException;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class ChallengeSearchConnector {

  private static final Log             LOG                          = ExoLogger.getLogger(ChallengeSearchConnector.class);

  private static final String          SEARCH_QUERY_FILE_PATH_PARAM = "query.file.path";

  private final ConfigurationManager   configurationManager;

  private final ElasticSearchingClient client;

  private String                       index;

  private String                       searchQueryFilePath;

  private String                       searchQuery;

  public ChallengeSearchConnector(ConfigurationManager configurationManager,
                                  ElasticSearchingClient client,
                                  InitParams initParams) {
    this.configurationManager = configurationManager;
    this.client = client;

    PropertiesParam param = initParams.getPropertiesParam("constructor.params");
    this.index = param.getProperty("index");
    if (initParams.containsKey(SEARCH_QUERY_FILE_PATH_PARAM)) {
      searchQueryFilePath = initParams.getValueParam(SEARCH_QUERY_FILE_PATH_PARAM).getValue();
      try {
        retrieveSearchQuery();
      } catch (Exception e) {
        LOG.error("Can't read elasticsearch search query from path {}", searchQueryFilePath, e);
      }
    }
  }

  public List<ChallengeSearchEntity> search(Set<String> spaceList, String term, long domainId, long offset, long limit) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset must be positive");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }
    if (StringUtils.isBlank(term)) {
      throw new IllegalArgumentException("Filter term is mandatory");
    }
    if (spaceList.isEmpty()) {
      throw new IllegalArgumentException("User identity id must be positive");
    }
    String esQuery = buildQueryStatement(spaceList, term, domainId, offset, limit);
    String jsonResponse = this.client.sendRequest(esQuery, this.index);
    return buildResult(jsonResponse);
  }

  private String buildQueryStatement(Set<String> spaceList, String term, long domainId, long offset, long limit) {
    term = removeSpecialCharacters(term);
    List<String> termsQuery = Arrays.stream(term.split(" ")).filter(StringUtils::isNotBlank).map(word -> {
      word = word.trim();
      if (word.length() > 5) {
        word = word + "~1";
      }
      return word;
    }).collect(Collectors.toList());
    String termQuery = StringUtils.join(termsQuery, " AND ");
    return retrieveSearchQuery().replace("@domainId@", String.valueOf(domainId))
                                .replace("@term_query@", termQuery)
                                .replace("@spaceList@", StringUtils.join(spaceList, ","))
                                .replace("@offset@", String.valueOf(offset))
                                .replace("@limit@", String.valueOf(limit));
  }

  @SuppressWarnings("rawtypes")
  private List<ChallengeSearchEntity> buildResult(String jsonResponse) {
    LOG.debug("Search Query response from ES : {} ", jsonResponse);

    List<ChallengeSearchEntity> results = new ArrayList<>();
    JSONParser parser = new JSONParser();

    Map json = null;
    try {
      json = (Map) parser.parse(jsonResponse);
    } catch (ParseException e) {
      throw new ElasticSearchException("Unable to parse JSON response", e);
    }

    JSONObject jsonResult = (JSONObject) json.get("hits");
    if (jsonResult == null) {
      return results;
    }

    //
    JSONArray jsonHits = (JSONArray) jsonResult.get("hits");
    for (Object jsonHit : jsonHits) {
      try {
        ChallengeSearchEntity challenge = new ChallengeSearchEntity();

        JSONObject jsonHitObject = (JSONObject) jsonHit;
        JSONObject hitSource = (JSONObject) jsonHitObject.get("_source");
        long id = parseLong(hitSource, "id");
        String title = (String) hitSource.get("title");
        String description = (String) hitSource.get("description");
        String managers = (String) hitSource.get("managers");
        long audience = parseLong(hitSource, "audience");
        String startDate = (String) hitSource.get("startDate");
        String endDate = (String) hitSource.get("endDate");
        Long program = parseLong(hitSource, "program");
        long points = parseLong(hitSource, "points");

        challenge.setId(id);
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setAudience(audience);
        challenge.setEndDate(endDate);
        challenge.setStartDate(startDate);
        challenge.setProgramId(program);
        challenge.setPoints(points);
        challenge.setManagers(getManagersList(managers));

        results.add(challenge);
      } catch (Exception e) {
        LOG.warn("Error processing challenge search result item, ignore it from results", e);
      }
    }
    return results;
  }

  private Long parseLong(JSONObject hitSource, String key) {
    String value = (String) hitSource.get(key);
    return StringUtils.isBlank(value) ? null : Long.parseLong(value);
  }

  private List<Long> getManagersList(String managers) {
    List<String> managersList = List.of(managers.split("#"));
    List<Long> listManagers = new ArrayList<>();
    for (String s : managersList) {
      listManagers.add(Long.valueOf(s));
    }
    return listManagers;
  }

  private String removeSpecialCharacters(String string) {
    string = Normalizer.normalize(string, Normalizer.Form.NFD);
    string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").replace("'", " ");
    return string;
  }

  private String retrieveSearchQuery() {
    if (StringUtils.isBlank(this.searchQuery) || PropertyManager.isDevelopping()) {
      try {
        InputStream queryFileIS = this.configurationManager.getInputStream(searchQueryFilePath);
        this.searchQuery = IOUtil.getStreamContentAsString(queryFileIS);
      } catch (Exception e) {
        throw new IllegalStateException("Error retrieving search query from file: " + searchQueryFilePath, e);
      }
    }
    return this.searchQuery;
  }

}

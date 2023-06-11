/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2022 Meeds Association contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.search;

import static io.meeds.gamification.utils.Utils.removeSpecialCharacters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.exoplatform.commons.search.es.ElasticSearchException;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.filter.RuleFilter;

public class RuleSearchConnector {

  private static final Log             LOG                          = ExoLogger.getLogger(RuleSearchConnector.class);

  private static final String          SEARCH_QUERY_FILE_PATH_PARAM = "query.file.path";

  private static final String          LANG                         = "@lang@";

  private static final String          DOMAIN_FILTERING_QUERY       = """
            ,{
              "term": {
                "domainId": {
                  "value": "@domainId@"
                }
              }
            }
      """;

  private static final String          AUDIENCE_FILTERING_QUERY     = """
            ,{
              "terms": {
                "audience": [
                  @spaceList@
                ]
              }
            }
      """;

  private static final String          ILLEGAL_SEARCH_CHARACTERS    = "\\!?^()+-=<>{}[]:\"*~&|#%@";

  private final ConfigurationManager   configurationManager;

  private final ElasticSearchingClient client;

  private String                       index;

  private String                       searchQueryFilePath;

  private String                       searchQuery;

  public RuleSearchConnector(ConfigurationManager configurationManager,
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

  public List<Long> search(RuleFilter filter, long offset, long limit) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset must be positive");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }
    if (StringUtils.isBlank(filter.getTerm())) {
      throw new IllegalArgumentException("Filter term is mandatory");
    }
    if (filter.getLocale() == null) {
      throw new IllegalArgumentException("Filter locale is mandatory");
    }
    String esQuery = buildQueryStatement(filter, offset, limit);
    if (StringUtils.isBlank(esQuery)) {
      return Collections.emptyList();
    }
    String jsonResponse = this.client.sendRequest(esQuery, this.index);
    return buildSearchResult(jsonResponse);
  }

  private String buildQueryStatement(RuleFilter filter, long offset, long limit) {
    String term = StringUtils.lowerCase(removeSpecialCharacters(filter.getTerm()));
    term = escapeIllegalCharacterInQuery(term);
    if (StringUtils.isBlank(term)) {
      return null;
    }
    term = term.trim();
    List<String> termsQuery = Arrays.stream(term.split(" ")).filter(StringUtils::isNotBlank).map(word -> {
      word = word.trim();
      if (word.length() > 4) {
        word = word + "~1";
      }
      return word;
    }).toList();
    String termQuery = StringUtils.join(termsQuery, " AND ");
    String query = retrieveSearchQuery();
    if (filter.getProgramId() > 0) {
      query = query.replace("@domain_filtering@", DOMAIN_FILTERING_QUERY);
    } else {
      query = query.replace("@domain_filtering@", "");
    }
    if (!CollectionUtils.isEmpty(filter.getSpaceIds())) {
      query = query.replace("@audience_filtering@", AUDIENCE_FILTERING_QUERY);
    } else {
      query = query.replace("@audience_filtering@", "");
    }

    Set<Long> spaceList = Optional.ofNullable(filter.getSpaceIds())
                                  .map(HashSet::new)
                                  .orElse(new HashSet<>());

    return query.replace("@domainId@", String.valueOf(filter.getProgramId()))
                .replace("@term@", term)
                .replace(LANG, filter.getLocale().toLanguageTag())
                .replace("@term_query@", termQuery)
                .replace("@spaceList@", StringUtils.join(spaceList, ","))
                .replace("@offset@", String.valueOf(offset))
                .replace("@limit@", String.valueOf(limit));
  }

  @SuppressWarnings("rawtypes")
  private List<Long> buildSearchResult(String jsonResponse) {
    List<Long> results = new ArrayList<>();
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

    JSONArray jsonHits = (JSONArray) jsonResult.get("hits");
    for (Object jsonHit : jsonHits) {
      try {
        JSONObject jsonHitObject = (JSONObject) jsonHit;
        JSONObject hitSource = (JSONObject) jsonHitObject.get("_source");
        long id = parseLong(hitSource, "id");
        results.add(id);
      } catch (Exception e) {
        LOG.warn("Error processing rules search result item, ignore it from results", e);
      }
    }
    return results;
  }

  private long parseLong(JSONObject hitSource, String key) {
    String value = (String) hitSource.get(key);
    return StringUtils.isBlank(value) ? 0 : Long.parseLong(value);
  }

  private static String escapeIllegalCharacterInQuery(String query) {
    if (StringUtils.isBlank(query)) {
      return null;
    }
    for (char c : ILLEGAL_SEARCH_CHARACTERS.toCharArray()) {
      query = query.replace(c + "", "");
    }
    return query;
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
